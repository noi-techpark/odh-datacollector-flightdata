package it.bz.opendatahub.datacollector.flightdata.mqtt;

import io.smallrye.config.ConfigMapping;

import java.util.Optional;

/**
 * MQTT configuration as defined by Quarkus.
 * <p>
 * The data in this interface is taken from System properties, ENV variables,
 * .env file and more. Take a look at https://quarkus.io/guides/config-reference
 * to see how it works.
 */
@ConfigMapping(prefix = "mqtt")
public interface MqttConfig {
    String url();

    // Topic to collect SBS flightdata from
    String flightdataTopic();

    // Username is optional and may not be set
    Optional<String> user();

    // Password is optional and may not be set
    Optional<String> pass();

}
