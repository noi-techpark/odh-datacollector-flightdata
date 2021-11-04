package net.gappc.flightdata;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

@ApplicationScoped
public class MqttRoutes extends RouteBuilder {

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
    public void configure() throws Exception {
        DataSource dataSource = dataSourceProvider.setupDataSource();
        bindToRegistry("flightdata", dataSource);

        String mqttConnectionString = getMqttConnectionString();
        System.out.println("-------MQTT------------");
        System.out.println("Connection string: " + mqttConnectionString);
        System.out.println("-------MQTT-END--------");

        from(mqttConnectionString)
                .setHeader("topic", header(PahoMqtt5Constants.MQTT_TOPIC))
                .setBody(simple("insert into flightdata(topic, body) values (:?topic, '${body}'::jsonb)"))
                .log(">>> ${body}")
                .to("jdbc:flightdata?useHeadersAsParameters=true");

        rest("/api")
                .enableCORS(true)
                .get()
                .route()
                .setBody(simple("select * from flightdata order by created_at desc limit 100"))
                .to("jdbc:flightdata")
                .marshal()
                .json()
                .log(">>> ${body}");
                
    }

    private String getMqttConnectionString() {
        final StringBuilder uri = new StringBuilder(String.format("paho-mqtt5:%s?brokerUrl=%s", mqttConfig.topic(), mqttConfig.url()));

        // Check if MQTT credentials are provided. If so, then add the credentials to the connection string
        mqttConfig.user().ifPresent(user -> uri.append(String.format("&userName=%s", user)));
        mqttConfig.pass().ifPresent(pass -> uri.append(String.format("&password=%s", pass)));

        return uri.toString();
    }
}
