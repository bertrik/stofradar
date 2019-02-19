package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StationsData {

    @JsonProperty("number")
    private String number;

    @JsonProperty("location")
    private String location;

    public String getNumber() {
        return number;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s - %s}", number, location);
    }

}
