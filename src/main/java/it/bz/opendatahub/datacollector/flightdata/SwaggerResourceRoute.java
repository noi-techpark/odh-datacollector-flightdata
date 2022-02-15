package it.bz.opendatahub.datacollector.flightdata;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

/**
 * Define a Swagger route that creates definitions for REST endpoints.
 */
@ApplicationScoped
public class SwaggerResourceRoute extends RouteBuilder {

    @Override
    public void configure() {
        rest("/swagger")
                .description("OpenAPI")
                .enableCORS(true)
                .get()
                .description("Get OpenAPI")
                .route()
                .routeId("[Route: Swagger]")
                .to("rest-api:basePath");
    }

}
