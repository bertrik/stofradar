package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ComponentsData {

    @JsonProperty("formula")
    private String formula;

    @JsonProperty("name")
    private BilingualText name;

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{formula=%s, name=%s}", formula, name);
    }

}
