// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata.flightdata.db;

import it.bz.opendatahub.datacollector.flightdata.genericdata.ReadFlightdataFromStore;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Produce;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Read (aggregated) flightdata from store.
 */
@ApplicationScoped
public class ReadAggregatedFlightdataFromStore {

    @Inject
    ReadFlightdataFromStore readFlightdataFromStore;

    @Produce("sql:select id, timestamp, rawdata::text as rawdata from flightdata where timestamp >= :#ts order by timestamp desc limit :#limit")
    FluentProducerTemplate fetchAggregatedFlightdata;

    /**
     * Fetch (aggregated) flightdata from store.
     *
     * @param aggregate If set to <code>true</code>, aggregated flightdata is returned. If set to <code>false</code>, raw flightdata is returned.
     * @param start     Return entries starting from this moment.
     * @param limit     Limit result size.
     * @return (Aggregated) flightdata.
     */
    public List<Object> fetchFlightdataOrderByTimestamp(boolean aggregate, LocalDateTime start, long limit) {
        return aggregate
                ? fetchAggregatedFlightdataOrderByTimestamp(start, limit)
                : fetchGenericFlightdataOrderByTimestamp(start, limit);
    }

    private List<Object> fetchAggregatedFlightdataOrderByTimestamp(LocalDateTime start, long limit) {
        return fetchAggregatedFlightdata
                .withBody(Map.of("ts", start, "limit", limit))
                .request(List.class);
    }

    private List<Object> fetchGenericFlightdataOrderByTimestamp(LocalDateTime start, long limit) {
        return readFlightdataFromStore.fetchFlightdataFromGenericDataOrderByTimestamp(start, limit);
    }
}
