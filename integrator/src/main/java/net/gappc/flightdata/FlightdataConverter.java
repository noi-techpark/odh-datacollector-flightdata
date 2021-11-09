package net.gappc.flightdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Convert SBS as JSON into {@link Flightdata} objects that are easier to handle.
 *
 * Set the converted objects as the message body and put the IcaoAddr as "flightDataId"
 * into the header.
 */
public class FlightdataConverter implements Processor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String bodyAsString = exchange.getMessage().getBody(String.class);
        Flightdata flightdata = OBJECT_MAPPER.readValue(bodyAsString, Flightdata.class);

        exchange.getMessage().setBody(flightdata);
        exchange.getMessage().setHeader("flightDataId", flightdata.getIcaoAddr());
    }
}
