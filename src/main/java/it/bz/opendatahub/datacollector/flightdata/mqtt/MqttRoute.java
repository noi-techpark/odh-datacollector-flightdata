// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata.mqtt;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;

import javax.enterprise.context.ApplicationScoped;

/**
 * Route to read from MQTT.
 */
@ApplicationScoped
public class MqttRoute extends RouteBuilder {

    public static final String SEDA_MQTT_ALL_STREAM = "seda:mqttAllStream?multipleConsumers=true";
    public static final String SEDA_MQTT_FLIGHTDATA_STREAM = "seda:mqttFlightdataStream?multipleConsumers=true";

    private final MqttConfig mqttConfig;

    public MqttRoute(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void configure() {
        MqttConfigLogger.log(mqttConfig);

        String mqttConnectionString = getMqttConnectionString();

        // Use MQTT connection
        // -> expose all MQTT data as SEDA_MQTT_STREAM stream
        from(mqttConnectionString)
                .routeId("[Route: MQTT subscription]")
                .setHeader("topic", header(PahoMqtt5Constants.MQTT_TOPIC))
                .multicast()
                .to(SEDA_MQTT_ALL_STREAM);

        String flightdataTopic = mqttConfig.flightdataTopic();

        // Use the SEDA_MQTT_ALL_STREAM
        // -> write only data from the flightdata topic to the SEDA_MQTT_FLIGHTDATA_STREAM
        from(SEDA_MQTT_ALL_STREAM)
                .filter(header(PahoMqtt5Constants.MQTT_TOPIC).isEqualTo(flightdataTopic))
                .multicast()
                .to(SEDA_MQTT_FLIGHTDATA_STREAM);
    }

    private String getMqttConnectionString() {
        final StringBuilder uri = new StringBuilder(String.format("paho-mqtt5:#?brokerUrl=%s", mqttConfig.url()));

        // Check if MQTT credentials are provided. If so, then add the credentials to the connection string
        mqttConfig.user().ifPresent(user -> uri.append(String.format("&userName=%s", user)));
        mqttConfig.pass().ifPresent(pass -> uri.append(String.format("&password=%s", pass)));

        return uri.toString();
    }

}
