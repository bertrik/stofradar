package nl.bertriksikken.stofradar.senscom.dto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object describing a particular sensor and its current measurement.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DataPoint {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("sensordatavalues")
    private SensorDataValues sensorDataValues;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("sensor")
    private Sensor sensor;
    
    public Instant getTimestamp() {
        return ZonedDateTime.parse(timestamp, DATE_FORMATTER).toInstant();
    }
    
    public SensorDataValues getSensorDataValues() {
        return new SensorDataValues(sensorDataValues);
    }

    public Location getLocation() {
        return location;
    }

    public Sensor getSensor() {
        return sensor;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,%s,%s}", location, sensor, sensorDataValues);
    }

}
