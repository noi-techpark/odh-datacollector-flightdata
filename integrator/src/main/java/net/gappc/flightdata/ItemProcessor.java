package net.gappc.flightdata;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws2.ddb.Ddb2Constants;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ItemProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("topic", AttributeValue.builder().s("mqtt-test-camel").build());
        String timestamp = Long.toString(Instant.now().toEpochMilli());
        item.put("ts", AttributeValue.builder().n(timestamp).build());
        item.put("body", AttributeValue.builder().s(exchange.getIn().getBody(String.class)).build());

        exchange.getMessage().setHeader(Ddb2Constants.ITEM, item);
    }
}
