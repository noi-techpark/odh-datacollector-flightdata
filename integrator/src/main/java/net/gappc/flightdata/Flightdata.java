package net.gappc.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Flightdata POJO.
 * <p>
 * The property definition can be found here: https://github.com/cyoung/stratux/blob/master/main/traffic.go, dump1090Data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flightdata {

    //    - Icao_addr           uint32      # Aircraft address
    //    - DF                  int         # Mode S downlink format.
    //    - CA                  int         # Lowest 3 bits of first byte of Mode S message (DF11 and DF17 capability; DF18 control field, zero for all other DF types)
    //    - TypeCode            int         # Mode S type code
    //    - SubtypeCode         int         # Mode S subtype code
    //    - SBS_MsgType         int         # type of SBS message (used in "old" 1090 parsing)
    //    - SignalLevel         float64     # Decimal RSSI (0-1 nominal) as reported by dump1090-mutability. Convert to dB RSSI before setting in TrafficInfo.
    //    - Tail                *string     # Callsign. Transmitted by aircraft.
    //    - Squawk              *int        # 12-bit squawk code in octal format
    //    - Emitter_category    *int        # Formatted using GDL90 standard, e.g. in a Mode ES report, A7 becomes 0x07, B0 becomes 0x08, etc.
    //    - OnGround            *bool       # Air-ground status. On-ground is "true".
    //    - Lat                 *float32    # decimal degrees, north positive
    //    - Lng                 *float32    # decimal degrees, east positive
    //    - Position_valid      bool        # TODO: set when position report received. Unset after n seconds?
    //    - NACp                *int        # Navigation Accuracy Category for Position
    //    - Alt                 *int        # Pressure altitude, feet
    //    - AltIsGNSS           bool        # Pressure alt = 0; GNSS alt = 1
    //    - GnssDiffFromBaroAlt *int16      # GNSS height above baro altitude in feet; valid range is -3125 to 3125. +/- 3138 indicates larger difference.
    //    - Vvel                *int16      # Feet per minute
    //    - Speed_valid         bool        # Set when speed report received
    //    - Speed               *uint16     # Aircraft speed
    //    - Track               *uint16     # degrees true
    //    - Timestamp           time.Time   # time traffic last seen, UTC

    @JsonProperty("Icao_addr")
    private Integer icaoAddr;

    @JsonProperty("DF")
    private Integer df;

    @JsonProperty("CA")
    private Integer ca;

    @JsonProperty("TypeCode")
    private Integer typeCode;

    @JsonProperty("SubtypeCode")
    private Integer subtypeCode;

    @JsonProperty("SignalLevel")
    private Double signalLevel;

    @JsonProperty("IsMlat")
    private Boolean isMlat;

    @JsonProperty("Tail")
    private String tail;

    @JsonProperty("Alt")
    private Integer alt;

    @JsonProperty("AltIsGNSS")
    private Boolean altIsGNSS;

    @JsonProperty("GnssDiffFromBaroAlt")
    private Integer gnssDiffFromBaroAlt;

    @JsonProperty("Speed_valid")
    private Boolean speedValid;

    @JsonProperty("Speed")
    private Integer speed;

    @JsonProperty("Track")
    private Integer track;

    @JsonProperty("Lat")
    private Double lat;

    @JsonProperty("Lng")
    private Double lng;

    @JsonProperty("Position_valid")
    private Boolean positionValid;

    @JsonProperty("Vvel")
    private Integer vvel;

    @JsonProperty("Squawk")
    private Integer squawk;

    @JsonProperty("OnGround")
    private Boolean onGround;

    @JsonProperty("NACp")
    private Integer nacp;

    @JsonProperty("Emitter_category")
    private Integer emitterCategory;

    @JsonProperty("Timestamp")
    private String timestamp;

    public Flightdata deepCopy() {
        Flightdata flightdata = new Flightdata();

        flightdata.icaoAddr = this.icaoAddr;
        flightdata.df = this.df;
        flightdata.ca = this.ca;
        flightdata.typeCode = this.typeCode;
        flightdata.subtypeCode = this.subtypeCode;
        flightdata.signalLevel = this.signalLevel;
        flightdata.isMlat = this.isMlat;
        flightdata.tail = this.tail;
        flightdata.alt = this.alt;
        flightdata.altIsGNSS = this.altIsGNSS;
        flightdata.gnssDiffFromBaroAlt = this.gnssDiffFromBaroAlt;
        flightdata.speedValid = this.speedValid;
        flightdata.speed = this.speed;
        flightdata.track = this.track;
        flightdata.lat = this.lat;
        flightdata.lng = this.lng;
        flightdata.positionValid = this.positionValid;
        flightdata.vvel = this.vvel;
        flightdata.squawk = this.squawk;
        flightdata.onGround = this.onGround;
        flightdata.nacp = this.nacp;
        flightdata.emitterCategory = this.emitterCategory;
        flightdata.timestamp = this.timestamp;

        return flightdata;
    }

    public Integer getIcaoAddr() {
        return icaoAddr;
    }

    public void setIcaoAddr(Integer icaoAddr) {
        this.icaoAddr = icaoAddr;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(Integer df) {
        this.df = df;
    }

    public Integer getCa() {
        return ca;
    }

    public void setCa(Integer ca) {
        this.ca = ca;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getSubtypeCode() {
        return subtypeCode;
    }

    public void setSubtypeCode(Integer subtypeCode) {
        this.subtypeCode = subtypeCode;
    }

    public Double getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(Double signalLevel) {
        this.signalLevel = signalLevel;
    }

    public Boolean getMlat() {
        return isMlat;
    }

    public void setMlat(Boolean mlat) {
        isMlat = mlat;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public Integer getAlt() {
        return alt;
    }

    public void setAlt(Integer alt) {
        this.alt = alt;
    }

    public Boolean getAltIsGNSS() {
        return altIsGNSS;
    }

    public void setAltIsGNSS(Boolean altIsGNSS) {
        this.altIsGNSS = altIsGNSS;
    }

    public Integer getGnssDiffFromBaroAlt() {
        return gnssDiffFromBaroAlt;
    }

    public void setGnssDiffFromBaroAlt(Integer gnssDiffFromBaroAlt) {
        this.gnssDiffFromBaroAlt = gnssDiffFromBaroAlt;
    }

    public Boolean getSpeedValid() {
        return speedValid;
    }

    public void setSpeedValid(Boolean speedValid) {
        this.speedValid = speedValid;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getTrack() {
        return track;
    }

    public void setTrack(Integer track) {
        this.track = track;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Boolean getPositionValid() {
        return positionValid;
    }

    public void setPositionValid(Boolean positionValid) {
        this.positionValid = positionValid;
    }

    public Integer getVvel() {
        return vvel;
    }

    public void setVvel(Integer vvel) {
        this.vvel = vvel;
    }

    public Integer getSquawk() {
        return squawk;
    }

    public void setSquawk(Integer squawk) {
        this.squawk = squawk;
    }

    public Boolean getOnGround() {
        return onGround;
    }

    public void setOnGround(Boolean onGround) {
        this.onGround = onGround;
    }

    public Integer getNacp() {
        return nacp;
    }

    public void setNacp(Integer nacp) {
        this.nacp = nacp;
    }

    public Integer getEmitterCategory() {
        return emitterCategory;
    }

    public void setEmitterCategory(Integer emitterCategory) {
        this.emitterCategory = emitterCategory;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Flightdata{" +
                "icaoAddr='" + icaoAddr + '\'' +
                ", df=" + df +
                ", ca=" + ca +
                ", typeCode=" + typeCode +
                ", subtypeCode=" + subtypeCode +
                ", signalLevel=" + signalLevel +
                ", isMlat=" + isMlat +
                ", tail='" + tail + '\'' +
                ", alt=" + alt +
                ", altIsGNSS=" + altIsGNSS +
                ", gnssDiffFromBaroAlt=" + gnssDiffFromBaroAlt +
                ", speedValid=" + speedValid +
                ", speed=" + speed +
                ", track='" + track + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", positionValid=" + positionValid +
                ", vvel='" + vvel + '\'' +
                ", squawk='" + squawk + '\'' +
                ", onGround=" + onGround +
                ", nacp='" + nacp + '\'' +
                ", emitterCategory='" + emitterCategory + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
