// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata.flightdata.db;

import it.bz.opendatahub.datacollector.flightdata.mqtt.MqttConfig;
import it.bz.opendatahub.datacollector.flightdata.utils.BatchStrategy;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static it.bz.opendatahub.datacollector.flightdata.flightdata.transformation.AggregateFlightdata.SEDA_AGGREGATED_FLIGHTDATA;

/**
 * Store aggregated filghtdata.
 */
@ApplicationScoped
public class StoreAggregatedFlightdata extends RouteBuilder {

    @Inject
    ReadAggregatedFlightdataFromStore readAggregatedFlightdataFromStore;

    private static final int DB_INSERT_MAX_BATCH_SIZE = 100;
    private static final int DB_INSERT_MAX_BATCH_INTERVALL = 1000;

    private final MqttConfig mqttConfig;

    public StoreAggregatedFlightdata(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void configure() {
        String mqttUsername = mqttConfig.user().orElse("UNKNOWN");

        // Use VALID_FLIGHTDATA_MULTIPLE_CONSUMERS stream (aggregated flightdata)
        // -> Write that data into table flightdata in batches
        from(SEDA_AGGREGATED_FLIGHTDATA)
                .routeId("[Route: to flightdata table]")
                // Aggregate DB batch writes
                .aggregate(constant(true), new BatchStrategy(exchange -> exchange.getMessage().getHeader("rawdata", String.class)))
                .completionSize(DB_INSERT_MAX_BATCH_SIZE)
                .completionTimeout(DB_INSERT_MAX_BATCH_INTERVALL)
                .to("sql:insert into flightdata(username, topic, rawdata) values ('" + mqttUsername + "', :#topic, :#message::jsonb)?batch=true");
    }

}
