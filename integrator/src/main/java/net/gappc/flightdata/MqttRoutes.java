package net.gappc.flightdata;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

@ApplicationScoped
public class MqttRoutes extends RouteBuilder {

    private final DataSourceProvider dataSourceProvider;
    private final MosquittoConfig mosquittoConfig;

    public MqttRoutes(
            DataSourceProvider dataSourceProvider,
            MosquittoConfig mosquittoConfig
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.mosquittoConfig = mosquittoConfig;
    }

    @Override
    public void configure() throws Exception {
        DataSource dataSource = dataSourceProvider.setupDataSource();
        bindToRegistry("flightdata", dataSource);

        from("paho-mqtt5:#?brokerUrl=" + mosquittoConfig.url())
                .setHeader("topic", header(PahoMqtt5Constants.MQTT_TOPIC))
                .setBody(simple("insert into flightdata(topic, body) values (:?topic, '${body}'::jsonb)"))
                .log(">>> ${body}")
                .to("jdbc:flightdata?useHeadersAsParameters=true");
    }
}
