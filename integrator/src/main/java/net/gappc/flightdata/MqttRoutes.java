package net.gappc.flightdata;

import org.apache.camel.builder.RouteBuilder;

public class MqttRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("paho-mqtt5:#")
                .to("stream:out");
    }
}
