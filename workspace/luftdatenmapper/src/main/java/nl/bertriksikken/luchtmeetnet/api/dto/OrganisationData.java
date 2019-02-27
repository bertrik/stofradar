package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrganisationData {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private OrganisationName name;

    public int getId() {
        return id;
    }

    public OrganisationName getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{id=%d, name=%s}", id, name);
    }

}
