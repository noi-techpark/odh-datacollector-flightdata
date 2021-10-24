package net.gappc.flightdata;

import io.smallrye.config.ConfigMapping;

/**
 * Mosquitto configuration as defined by Quarkus.
 *
 * The data in this interface is taken from System properties, ENV variables,
 * .env file and more. Take a look at https://quarkus.io/guides/config-reference
 * to see how it works.
 */
@ConfigMapping(prefix = "mosquitto")
public interface MosquittoConfig {
    String url();
}
