package it.bz.opendatahub.datacollector.flightdata.genericdata;

import it.bz.opendatahub.datacollector.flightdata.mqtt.MqttConfig;
import it.bz.opendatahub.datacollector.flightdata.utils.BatchStrategy;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

import static it.bz.opendatahub.datacollector.flightdata.mqtt.MqttRoute.SEDA_MQTT_ALL_STREAM;

/**
 * Store all MQTT messages.
 */
@ApplicationScoped
public class StoreAllMqttMessages extends RouteBuilder {

    private static final int DB_INSERT_MAX_BATCH_SIZE = 100;
    private static final int DB_INSERT_MAX_BATCH_INTERVALL = 1000;

    private final MqttConfig mqttConfig;

    public StoreAllMqttMessages(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void configure() {
        String mqttUsername = mqttConfig.user().orElse("UNKNOWN");

        // Use SEDA_MQTT_ALL_STREAM stream (all MQTT messages)
        // -> Write that data to table genericdata in batches
        from(SEDA_MQTT_ALL_STREAM)
                .end()
                .routeId("[Route: to genericdata table]")
                .aggregate(constant(true), new BatchStrategy(exchange -> exchange.getMessage().getBody(String.class)))
                .completionSize(DB_INSERT_MAX_BATCH_SIZE)
                .completionTimeout(DB_INSERT_MAX_BATCH_INTERVALL)
                .to("sql:insert into genericdata(username, datasource, type, rawdata) values ('" + mqttUsername + "', 'MQTT', :#topic, :#message::jsonb)?batch=true");


    }

}
