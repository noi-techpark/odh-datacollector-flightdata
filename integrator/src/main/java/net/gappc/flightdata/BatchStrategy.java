package net.gappc.flightdata;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;

import java.util.Map;
import java.util.function.Function;

/**
 * Prepare data for DB batch inserts.
 */
public class BatchStrategy extends AbstractListAggregationStrategy<Map<String, String>> {

    private final Function<Exchange, String> messageExtractFun;

    public BatchStrategy(Function<Exchange, String> messageExtractFun) {
        this.messageExtractFun = messageExtractFun;
    }

    @Override
    public Map<String, String> getValue(Exchange exchange) {
        return Map.of(
                "topic", exchange.getMessage().getHeader("topic").toString(),
                "message", messageExtractFun.apply(exchange));
    }
}
