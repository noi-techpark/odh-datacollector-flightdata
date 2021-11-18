package net.gappc.flightdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

@ApplicationScoped
public class MqttRoutes extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(MqttRoutes.class);

    private static final int DB_INSERT_MAX_BATCH_SIZE = 100;
    private static final int DB_INSERT_MAX_BATCH_INTERVALL = 1000;
    private static final String FLIGHTDATA_SBS_TOPIC = "flightdata/sbs";

    private static final String MQTTSTREAM_MULTIPLE_CONSUMERS = "seda:mqttstream?multipleConsumers=true";
    private static final String VALID_FLIGHTDATA_MULTIPLE_CONSUMERS = "seda:flightdata?multipleConsumers=true";

    private final DataSourceProvider dataSourceProvider;
    private final MqttConfig mqttConfig;

    public MqttRoutes(
            DataSourceProvider dataSourceProvider,
            MqttConfig mqttConfig
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void configure() {
        DataSource dataSource = dataSourceProvider.setupDataSource();
        bindToRegistry("integratorStore", dataSource);

        String mqttConnectionString = getMqttConnectionString();
        LOG.info("-------MQTT------------");
        LOG.info("Connection string: {}", mqttConnectionString);
        LOG.info("-------MQTT-END--------");

        String mqttUsername = mqttConfig.user().orElse("UNKNOWN");

        // Use MQTT connection
        // -> expose the data as MQTTSTREAM_MULTIPLE_CONSUMERS stream
        from(mqttConnectionString)
                .routeId("[Route: MQTT subscription]")
                .setHeader("topic", header(PahoMqtt5Constants.MQTT_TOPIC))
                .multicast()
                .to(MQTTSTREAM_MULTIPLE_CONSUMERS);

        // Use MQTTSTREAM_MULTIPLE_CONSUMERS stream
        // -> Write that data to table genericdata in batches
        from(MQTTSTREAM_MULTIPLE_CONSUMERS)
                .routeId("[Route: to genericdata table]")
                .aggregate(constant(true), new BatchStrategy(exchange -> exchange.getMessage().getBody(String.class)))
                .completionSize(DB_INSERT_MAX_BATCH_SIZE)
                .completionTimeout(DB_INSERT_MAX_BATCH_INTERVALL)
                .to("sql:insert into genericdata(username, datasource, type, rawdata) values ('" + mqttUsername + "', 'MQTT', :#topic, :#message::jsonb)?dataSource=#integratorStore&batch=true");

        // Use MQTTSTREAM_MULTIPLE_CONSUMERS stream
        // -> filter for MQTT topic FLIGHTDATA_SBS_TOPIC
        // -> Use that stream to build aggregates of valid flightdata
        // -> Expose that data as FLIGHTDATA_MULTIPLE_CONSUMERS stream
        from(MQTTSTREAM_MULTIPLE_CONSUMERS)
                .routeId("[Route: to flightdata stream]")
                .filter(header(PahoMqtt5Constants.MQTT_TOPIC).isEqualTo(FLIGHTDATA_SBS_TOPIC))
                // Put body as string into header "rawdata" for later reuse
                .process(exchange -> {
                    String bodyAsString = exchange.getMessage().getBody(String.class);
                    exchange.getMessage().setHeader("rawdata", bodyAsString);
                })
                // Aggregate valid flightdata
                .process(new FlightdataConverter())
                .filter(simple("${body.icaoAddr} != null"))
                .aggregate(header("flightDataId"), new FlightdataAggregation())
                .completionPredicate(exchange -> exchange.getProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, false, Boolean.class))
                .multicast()
                .to(VALID_FLIGHTDATA_MULTIPLE_CONSUMERS);

        // Use VALID_FLIGHTDATA_MULTIPLE_CONSUMERS stream (aggregated flightdata)
        // -> Write that data into table flightdata in batches
        from(VALID_FLIGHTDATA_MULTIPLE_CONSUMERS)
                .routeId("[Route: to flightdata table]")
                // Aggregate DB batch writes
                .aggregate(constant(true), new BatchStrategy(exchange -> exchange.getMessage().getHeader("rawdata", String.class)))
                .completionSize(DB_INSERT_MAX_BATCH_SIZE)
                .completionTimeout(DB_INSERT_MAX_BATCH_INTERVALL)
                .log(">>> ${header.topic} ${header.rawdata}")
                .to("sql:insert into flightdata(username, topic, rawdata) values ('" + mqttUsername + "', :#topic, :#message::jsonb)?dataSource=#integratorStore&batch=true");

        // Expose REST API that returns last 100 elements from flightdata table
        rest("/api")
                .enableCORS(true)
                .get()
                .route()
                .routeId("[Route: REST test]")
                .setBody(simple("select id, topic, rawdata#>>'{}' as rawdata, timestamp from flightdata order by timestamp desc limit 100"))
                .to("jdbc:integratorStore")
                .marshal()
                .json()
                .log(">>> ${body}");

        rest("/flightdata/sbs?aggregate={aggregate}&ts={ts}&limit={limit}")
                .enableCORS(true)
                .get()
                .route()
                .routeId("[Route: REST SBS]")
                .process(new SbsRestParameterExtractor())
                .choice()
                    .when(header("aggregate").isEqualTo(Boolean.TRUE))
                    .to("sql:select id, timestamp, rawdata::text as rawdata from flightdata where timestamp >= :#ts order by timestamp desc limit :#limit")
                .otherwise()
                    .to("sql:select id, timestamp, rawdata::text as rawdata from genericdata where datasource='MQTT' AND timestamp >= :#ts order by timestamp desc limit :#limit")
                .end()
                .marshal()
                .json()
                .log(">>> ${body}");

        ((WebsocketComponent)getContext().getComponent("websocket")).setMaxThreads(5);

        // Use MQTTSTREAM_MULTIPLE_CONSUMERS stream
        // -> Filter for MQTT topic FLIGHTDATA_SBS_TOPIC
        // -> Expose that stream as WebSocket on topic /flightdata/sbs
        from(MQTTSTREAM_MULTIPLE_CONSUMERS)
                .routeId("[Route: WebSocket SBS]")
                .filter(header(PahoMqtt5Constants.MQTT_TOPIC).isEqualTo(FLIGHTDATA_SBS_TOPIC))
                .to("websocket://0.0.0.0:8081/flightdata/sbs?sendToAll=true");

        // Use FLIGHTDATA_MULTIPLE_CONSUMERS stream (aggregated flightdata)
        // -> Expose that data as WebSocket on topic /flightdata/sbs-aggregated
        ObjectMapper objectMapper = new ObjectMapper();
        from(VALID_FLIGHTDATA_MULTIPLE_CONSUMERS)
                .routeId("[Route: WebSocket SBS aggregated]")
                .process(exchange -> {
                    Flightdata flightdata = exchange.getMessage().getBody(Flightdata.class);
                    exchange.getMessage().setBody(objectMapper.writeValueAsString(flightdata));
                })
                .to("websocket://0.0.0.0:8081/flightdata/sbs-aggregated?sendToAll=true");
    }

    private String getMqttConnectionString() {
        final StringBuilder uri = new StringBuilder(String.format("paho-mqtt5:#?brokerUrl=%s", mqttConfig.url()));

        // Check if MQTT credentials are provided. If so, then add the credentials to the connection string
        mqttConfig.user().ifPresent(user -> uri.append(String.format("&userName=%s", user)));
        mqttConfig.pass().ifPresent(pass -> uri.append(String.format("&password=%s", pass)));

        return uri.toString();
    }

    private RouteDefinition fromGenericdataExportAsMqttWithFlightdataSbsTopic() {
        return from("stream:file?fileName=/home/chris/projects/noi/flightdata/data-export/genericdata.export")
                .setHeader(PahoMqtt5Constants.MQTT_TOPIC, simple(FLIGHTDATA_SBS_TOPIC))
                .setHeader("topic", header(PahoMqtt5Constants.MQTT_TOPIC));
    }

}
