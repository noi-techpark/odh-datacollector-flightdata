// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata.flightdata.rest;

import it.bz.opendatahub.datacollector.flightdata.flightdata.db.ReadAggregatedFlightdataFromStore;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Provide REST resource for SBS flightdata
 */
@ApplicationScoped
public class ProvideFlightdataResource extends RouteBuilder {

    @Inject
    ReadAggregatedFlightdataFromStore readAggregatedFlightdataFromStore;

    private static final String HEADER_AGGREGATE = "aggregate";
    private static final String HEADER_TS = "ts";
    private static final String HEADER_LIMIT = "limit";

    private static final boolean DEFAULT_AGGREGATED = false;
    private static final long DEFAULT_TS = 1635724800000L;
    private static final int DEFAULT_LIMIT = 1000;

    @Override
    public void configure() {
        rest("/flightdata/sbs")
                .enableCORS(true)
                .get()
                .description("Get flightdata")
                .param().name(HEADER_AGGREGATE)
                .description("Return aggregated flightdata if set to **true**, return all flightdata otherwise")
                .type(RestParamType.query)
                .dataType("boolean").required(false).defaultValue(Boolean.toString(DEFAULT_AGGREGATED)).endParam()
                .param().name(HEADER_TS)
                .description("Load flightdata beginning from this timestamp (milliseconds since 1970-01-01)")
                .type(RestParamType.query)
                .dataType("number").required(false).defaultValue(Long.toString(DEFAULT_TS)).endParam()
                .param().name(HEADER_LIMIT)
                .description("Limit results")
                .type(RestParamType.query)
                .dataType("number").required(false).defaultValue(Integer.toString(DEFAULT_LIMIT)).endParam()
                .route()
                .routeId("[Route: REST SBS]")
                .process(fetchFlightdata)
                .end()
                .marshal()
                .json();
    }

    private final Processor fetchFlightdata = exchange -> {
        // Extract header parameters
        Message message = exchange.getMessage();
        Boolean aggregate = message.getHeader(HEADER_AGGREGATE, Boolean.class);
        Long ts = message.getHeader(HEADER_TS, Long.class);
        LocalDateTime timestamp = Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault().normalized()).toLocalDateTime();
        Integer limit = message.getHeader(HEADER_LIMIT, Integer.class);

        // Fetch data from store and set message body
        List<Object> flightdataEntries = readAggregatedFlightdataFromStore.fetchFlightdataOrderByTimestamp(aggregate, timestamp, limit);
        message.setBody(flightdataEntries);
    };


}
