// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata.flightdata.websocket;

import it.bz.opendatahub.datacollector.flightdata.mqtt.MqttRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

/**
 * Provide flightdata as Websocket stream.
 */
@ApplicationScoped
public class ProvideFlightdataStream extends RouteBuilder {

    @Override
    public void configure() {
        // Use SEDA_MQTT_FLIGHTDATA_STREAM stream
        // -> expose that stream as Websocket on topic /flightdata/sbs
        from(MqttRoute.SEDA_MQTT_FLIGHTDATA_STREAM)
                .routeId("[Route: Websocket SBS]")
                .to("websocket://0.0.0.0:8081/flightdata/sbs?sendToAll=true");
    }

}
