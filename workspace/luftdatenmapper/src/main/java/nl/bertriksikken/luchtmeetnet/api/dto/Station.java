package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Station {

    @JsonProperty("data")
    private StationData data;

    public StationData getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{data=%s}", data);
    }

}
