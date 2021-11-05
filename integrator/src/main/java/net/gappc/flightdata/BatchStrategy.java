package net.gappc.flightdata;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;

import java.util.Map;

public class BatchStrategy extends AbstractListAggregationStrategy<Map<String, String>> {

    @Override
    public Map<String, String> getValue(Exchange exchange) {
        return Map.of(
                "topic", exchange.getMessage().getHeader("topic").toString(),
                "message", exchange.getMessage().getBody(String.class));
    }
}
