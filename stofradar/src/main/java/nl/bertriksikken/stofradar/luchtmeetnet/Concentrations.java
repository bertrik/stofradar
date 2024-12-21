package nl.bertriksikken.stofradar.luchtmeetnet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Concentrations(@JsonProperty("data") List<Data> data) {

    public Concentrations {
        data = Objects.requireNonNullElse(List.copyOf(data), new ArrayList<>());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(@JsonProperty("station_number") String stationNumber,
                       @JsonProperty("formula") String formula,
                       @JsonProperty("value") Double value,
                       @JsonProperty("timestamp_measured") String timestamp) {
        public Data {
            stationNumber = Objects.requireNonNullElse(stationNumber, "");
            formula = Objects.requireNonNullElse(formula, "");
            value = Objects.requireNonNullElse(value, Double.NaN);
            timestamp = Objects.requireNonNullElse(timestamp, "");
        }

    }
}
