package net.gappc.flightdata;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aggregate {@link Flightdata} (SBS as JSON) into valid flightdata.
 *
 * Each incoming flightdata contains only part of the information that is necessary to display e.g. an aircraft
 * on a map. One instance may contain the altitude, another one the speed.
 *
 * Therefore, the data must be aggregated, which is done by this implementation.
 */
public class FlightdataAggregation implements AggregationStrategy {

    private final Map<Integer, Flightdata> flightdataIndex = new ConcurrentHashMap<>();

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // Read message body
        Flightdata nextFlightdata = newExchange.getMessage().getBody(Flightdata.class);

        // Use IcaoAddr as ID
        Integer id = nextFlightdata.getIcaoAddr();

        // Get latest entry for ID from map or, if no such entry exists, put a new empty Flightdata
        // object into the map and return its value (contract of computeIfAbsent)
        Flightdata fromIndex = flightdataIndex.computeIfAbsent(id, icaoAddr -> new Flightdata());

        // Apply incoming changes to the latest entry that was stored in the map
        boolean didUpdate = applyFlightdataUpdate(fromIndex, nextFlightdata);

        // If there were updates and the object is complete, emit a new value from the aggregator
        if (didUpdate && isComplete(fromIndex)) {
            // The element to emit is a deep copy of the latest element, because the latest element
            // values may change while the aggregator keeps going on
            Flightdata flightdataToEmit = fromIndex.deepCopy();
            flightdataToEmit.setTimestamp(ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT));

            newExchange.getMessage().setBody(flightdataToEmit);
            newExchange.setProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, true);
        } else {
            newExchange.setProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, false);
        }

        return newExchange;
    }

    private boolean applyFlightdataUpdate(Flightdata fd1, Flightdata fd2) {
        boolean didUpdate = false;

        if (fd2.getIcaoAddr() != null && !fd2.getIcaoAddr().equals(fd1.getIcaoAddr())) {
            fd1.setIcaoAddr(fd2.getIcaoAddr());
            didUpdate = true;
        }

        if (fd2.getTail() != null && !fd2.getTail().equals(fd1.getTail())) {
            fd1.setTail(fd2.getTail());
            didUpdate = true;
        }

        if (fd2.getEmitterCategory() != null && !fd2.getEmitterCategory().equals(fd1.getEmitterCategory())) {
            fd1.setEmitterCategory(fd2.getEmitterCategory());
            didUpdate = true;
        }

        if (fd2.getOnGround() != null && !fd2.getOnGround().equals(fd1.getOnGround())) {
            fd1.setOnGround(fd2.getOnGround());
            didUpdate = true;
        }

        if (fd2.getTrack() != null && !fd2.getTrack().equals(fd1.getTrack())) {
            fd1.setTrack(fd2.getTrack());
            didUpdate = true;
        }

        if (fd2.getVvel() != null && !fd2.getVvel().equals(fd1.getVvel())) {
            fd1.setVvel(fd2.getVvel());
            didUpdate = true;
        }

        if (fd2.getAlt() != null && !fd2.getAlt().equals(fd1.getAlt())) {
            fd1.setAlt(fd2.getAlt());
            didUpdate = true;
        }

        if (fd2.getSpeed() != null && !fd2.getSpeed().equals(fd1.getSpeed())) {
            fd1.setSpeed(fd2.getSpeed());
            didUpdate = true;
        }

        if (fd2.getLat() != null && !fd2.getLat().equals(fd1.getLat())) {
            fd1.setLat(fd2.getLat());
            didUpdate = true;
        }

        if (fd2.getLng() != null && !fd2.getLng().equals(fd1.getLng())) {
            fd1.setLng(fd2.getLng());
            didUpdate = true;
        }

        return didUpdate;
    }

    private boolean isComplete(Flightdata fd) {
        return fd.getIcaoAddr() != null
                && fd.getTail() != null
                && fd.getEmitterCategory() != null
                && fd.getOnGround() != null
                && fd.getTrack() != null
                && fd.getVvel() != null
                && fd.getAlt() != null
                && fd.getSpeed() != null
                && fd.getLat() != null
                && fd.getLng() != null;
    }

}
