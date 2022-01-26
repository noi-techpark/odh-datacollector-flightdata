package it.bz.opendatahub.datacollector.flightdata.flightdatascheduled;

import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Produce;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Read scheduled flightdata from store.
 */
@ApplicationScoped
public class ReadFlightdataScheduledFromStore {

    @Produce("sql:select id, company, timestamp, rawdata::text as rawdata from flightdata_scheduled order by timestamp desc")
    FluentProducerTemplate fetchFlightdataScheduled;

    /**
     * Fetch scheduled flightdata from store.
     *
     * @return A list of scheduled flightdata entries.
     */
    public List<Object> fetchFlightdataScheduledOrderByTimestamp() {
        return fetchFlightdataScheduled.request(List.class);
    }

}
