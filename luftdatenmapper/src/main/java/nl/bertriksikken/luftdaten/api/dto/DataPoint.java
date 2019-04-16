package nl.bertriksikken.luftdaten.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object describing a particular sensor and its current measurement.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DataPoint {

    @JsonProperty("sensordatavalues")
    private SensorDataValues sensorDataValues;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("sensor")
    private Sensor sensor;

    public SensorDataValues getSensorDataValues() {
        return sensorDataValues;
    }

    public Location getLocation() {
        return location;
    }

    public Sensor getSensor() {
        return sensor;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{%s,%s,%s}", location, sensor, sensorDataValues);
    }
}
