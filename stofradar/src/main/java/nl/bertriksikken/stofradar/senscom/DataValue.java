package nl.bertriksikken.stofradar.senscom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataValue(@JsonProperty("value") double value, @JsonProperty("value_type") String valueType) {

}
