package it.bz.opendatahub.datacollector.flightdata.flightdatascheduled;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Provide the data from flightdata_scheduled as REST resource.
 */
@ApplicationScoped
public class ProvideFlightdataScheduledResource extends RouteBuilder {

    @Inject
    ReadFlightdataScheduledFromStore readFlightdataScheduledFromStore;

    @Override
    public void configure() {
        rest("/flightdata-scheduled")
                .enableCORS(true)
                .get()
                .description("Get scheduled flightdata")
                .route()
                .routeId("[Route: REST FD scheduled]")
                .setBody(exchange -> readFlightdataScheduledFromStore.fetchFlightdataScheduledOrderByTimestamp())
                .marshal()
                .json();
    }

}
