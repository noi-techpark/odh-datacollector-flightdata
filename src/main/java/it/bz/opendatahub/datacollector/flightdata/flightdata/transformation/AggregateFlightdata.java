// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata.flightdata.transformation;

import it.bz.opendatahub.datacollector.flightdata.mqtt.MqttRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

/**
 * Route to aggregate flightdata.
 */
@ApplicationScoped
public class AggregateFlightdata extends RouteBuilder {

    public static final String SEDA_AGGREGATED_FLIGHTDATA = "seda:flightdata?multipleConsumers=true";

    @Override
    public void configure() {
        // Use SEDA_MQTT_FLIGHTDATA_STREAM stream
        // -> build aggregates of valid flightdata
        // -> expose that data as SEDA_AGGREGATED_FLIGHTDATA stream
        from(MqttRoute.SEDA_MQTT_FLIGHTDATA_STREAM)
                .routeId("[Route: to aggregated flightdata stream]")
                // Put body as string into header "rawdata" for later reuse
                .process(exchange -> {
                    String bodyAsString = exchange.getMessage().getBody(String.class);
                    exchange.getMessage().setHeader("rawdata", bodyAsString);
                })
                .process(new FlightdataConverter())
                .filter(simple("${body.icaoAddr} != null"))
                .aggregate(header("flightDataId"), new FlightdataAggregator())
                .completionPredicate(exchange -> exchange.getProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, false, Boolean.class))
                .multicast()
                .to(SEDA_AGGREGATED_FLIGHTDATA);
    }

}
