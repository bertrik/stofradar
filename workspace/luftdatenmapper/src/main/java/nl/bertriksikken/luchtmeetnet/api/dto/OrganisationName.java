package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrganisationName {

    @JsonProperty("NL")
    private String nameNL;

    @JsonProperty("EN")
    private String nameEN;

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{NL=%s, EN=%s}", nameNL, nameEN);
    }

}
