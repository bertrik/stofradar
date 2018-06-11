package nl.bertriksikken.luftdaten.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bertrik
 *
 */
public final class SensorType {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("manufacturer")
    private String manufacturer;

    private SensorType() {
        // jackson constructor
    }

    public SensorType(int id, String name, String manufacturer) {
        this();
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{%d,%s,%s}", id, name, manufacturer);
    }

}
