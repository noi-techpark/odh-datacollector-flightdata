package it.bz.opendatahub.datacollector.flightdata.genericdata;

import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Produce;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Read flightdata from store.
 */
@ApplicationScoped
public class ReadFlightdataFromStore {

    @Produce("sql:select id, timestamp, rawdata::text as rawdata from genericdata where datasource='MQTT' AND timestamp >= :#ts order by timestamp desc limit :#limit")
    FluentProducerTemplate fetchFlightdataFromGenericdata;

    /**
     * Fetch flightdata from store.
     *
     * @param start Return entries starting from this moment.
     * @param limit Limit result size.
     * @return (Aggregated) flightdata.
     */
    public List<Object> fetchFlightdataFromGenericDataOrderByTimestamp(LocalDateTime start, long limit) {
        return fetchFlightdataFromGenericdata
                .withBody(Map.of("ts", start, "limit", limit))
                .request(List.class);
    }

}
