package it.bz.opendatahub.datacollector.flightdata;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

/**
 * This class sets configuration values for REST endpoints.
 */
@ApplicationScoped
public class RestConfig extends RouteBuilder {

    @Override
    public void configure() {
        restConfiguration()
                .apiProperty("api.title", "Flightdata API")
                .apiProperty("api.version", "0.0.1");
    }

}
