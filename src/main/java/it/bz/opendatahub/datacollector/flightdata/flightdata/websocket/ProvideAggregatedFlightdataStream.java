package it.bz.opendatahub.datacollector.flightdata.flightdata.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.opendatahub.datacollector.flightdata.flightdata.Flightdata;
import it.bz.opendatahub.datacollector.flightdata.flightdata.transformation.AggregateFlightdata;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

/**
 * Provide aggregated flightdata as Websocket stream.
 */
@ApplicationScoped
public class ProvideAggregatedFlightdataStream extends RouteBuilder {

    @Override
    public void configure() {
        // Use SEDA_AGGREGATED_FLIGHTDATA
        // -> expose that data as Websocket on topic /flightdata/sbs-aggregated
        ObjectMapper objectMapper = new ObjectMapper();
        from(AggregateFlightdata.SEDA_AGGREGATED_FLIGHTDATA)
                .routeId("[Route: Websocket SBS aggregated]")
                .process(exchange -> {
                    Flightdata flightdata = exchange.getMessage().getBody(Flightdata.class);
                    exchange.getMessage().setBody(objectMapper.writeValueAsString(flightdata));
                })
                .to("websocket://0.0.0.0:8081/flightdata/sbs-aggregated?sendToAll=true");
    }

}
