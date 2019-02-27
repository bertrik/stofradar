package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class BilingualText {

    @JsonProperty("NL")
    private String textNL;

    @JsonProperty("EN")
    private String textEN;

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{NL=%s, EN=%s}", textNL, textEN);
    }

}
