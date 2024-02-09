package nl.bertriksikken.stofradar.senscom.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class DataValue {

    @JsonProperty("value")
    private double value;

    @JsonProperty("value_type")
    private String valueType;

    public double getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s=%f}", valueType, value);
    }

}
