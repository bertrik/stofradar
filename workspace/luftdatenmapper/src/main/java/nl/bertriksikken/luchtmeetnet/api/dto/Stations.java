package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Stations {

    @JsonProperty("pagination")
    private Pagination pagination;

    @JsonProperty("data")
    private List<StationsData> data;

    public Pagination getPagination() {
        return pagination;
    }

    public List<StationsData> getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{data=%s}", data);
    }

}
