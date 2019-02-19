package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PointGeometry {

    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private double[] coordinates;

    public String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates.clone();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{type=%s,coordinates=%s}", type, Arrays.toString(coordinates));
    }

}
