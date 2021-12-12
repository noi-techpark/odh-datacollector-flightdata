package it.bz.opendatahub.datacollector.flightdata;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Extract parameters from for SBS REST endpoint invocation.
 */
public class SbsRestParameterExtractor implements Processor {

    @Override
    public void process(Exchange exchange) {
        Message message = exchange.getMessage();

        Long tsHeader = message.getHeader("ts", Long.class);
        // Default value is 2021-11-01
        LocalDateTime tsValue = tsHeader == null
                ? LocalDateTime.of(2021, 11, 1, 0, 0)
                : Instant.ofEpochMilli(tsHeader).atZone(ZoneId.systemDefault().normalized()).toLocalDateTime();

        Integer limitHeader = message.getHeader("limit", Integer.class);
        // Allow max. 1000 results
        int limitValue = limitHeader == null ? 1000 : Math.min(limitHeader, 1000);

        Map<String, Object> parameterMap = Map.of(
                "ts", tsValue,
                "limit", limitValue
        );
        message.setBody(parameterMap);
    }
}
