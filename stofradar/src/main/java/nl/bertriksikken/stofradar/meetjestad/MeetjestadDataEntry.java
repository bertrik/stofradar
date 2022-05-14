package nl.bertriksikken.stofradar.meetjestad;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class MeetjestadDataEntry {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("UTC"));

    @JsonProperty("id")
    int id = 0;

    @JsonProperty("timestamp")
    String timestamp = "";

    @JsonProperty("latitude")
    double latitude = Double.NaN;

    @JsonProperty("longitude")
    double longitude = Double.NaN;

    @JsonProperty("pm2.5")
    double pm2_5 = Double.NaN;

    @JsonProperty("pm10")
    double pm10 = Double.NaN;

    public int getId() {
        return id;
    }

    public Instant getTimestamp() {
        return ZonedDateTime.parse(timestamp, DATE_FORMATTER).toInstant();
    }

    public boolean hasLocation() {
        return Double.isFinite(latitude) && Double.isFinite(latitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean hasPm() {
        return Double.isFinite(pm2_5) && Double.isFinite(pm10);
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public double getPm10() {
        return pm10;
    }

}
