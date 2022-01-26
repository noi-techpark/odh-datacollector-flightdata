package it.bz.opendatahub.datacollector.flightdata.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to log {@link MqttConfig}.
 */
public final class MqttConfigLogger {

    private static Logger LOG = LoggerFactory.getLogger(MqttConfigLogger.class);

    private MqttConfigLogger() {
        // Private constructor, don't allow new instances
    }

    /**
     * Log {@link MqttConfig}.
     *
     * @param config The {@link MqttConfig} to log.
     */
    public static void log(MqttConfig config) {
        String url = config.url();
        String user = config.user().orElseGet(() -> "*** no user ***");
        String pass = config.pass().map(p -> "*****").orElseGet(() -> "*** no password ***");

        LOG.info("MQTT URL: {}", url);
        LOG.info("MQTT user: {}", user);
        LOG.info("MQTT password: {}", pass);
    }
}
