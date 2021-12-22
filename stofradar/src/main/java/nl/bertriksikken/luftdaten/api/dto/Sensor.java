package nl.bertriksikken.luftdaten.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Sensor {

    @JsonProperty("id")
    private int id;

    @JsonProperty("sensor_type")
    private SensorType sensorType;

    public int getId() {
        return id;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%d,%s}", id, sensorType);
    }

}
