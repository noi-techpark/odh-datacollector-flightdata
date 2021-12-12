package it.bz.opendatahub.datacollector.flightdata;

import io.smallrye.config.ConfigMapping;

/**
 * Database configuration as defined by Quarkus.
 *
 * The data in this interface is taken from System properties, ENV variables,
 * .env file and more. Take a look at https://quarkus.io/guides/config-reference
 * to see how it works.
 */
@ConfigMapping(prefix = "database")
public interface DatabaseConfig {
    String url();

    String name();

    String user();

    String pass();
}
