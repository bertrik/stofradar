package nl.bertriksikken.stofradar.senscom.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Location {

    @JsonProperty("id")
    private int id;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("altitude")
    private double altitude;

    @JsonProperty("country")
    private String country;

    @JsonProperty("indoor")
    private int indoor;

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getCountry() {
        return country;
    }

    public int getIndoor() {
        return indoor;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%f,%f,%f,%s}", latitude, longitude, altitude, country);
    }

}
