package nl.bertriksikken.stofradar.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.stofradar.restapi.AirRestApiConfig;

/**
 * Configuration class.
 */
public final class ParticulateMapperConfig {

    @JsonProperty("sensCom")
    public final HostConnectionConfig sensComConfig = new HostConnectionConfig("https://api.sensor.community", 30);

    @JsonProperty("samenmeten")
    public final HostConnectionConfig samenmetenConfig = new HostConnectionConfig("https://samenmeten.rivm.nl", 30);

    @JsonProperty("meetjestad")
    public final HostConnectionConfig meetjestadConfig = new HostConnectionConfig("https://meetjestad.net", 30);

    @JsonProperty("keepingDurationMinutes")
    public final int keepingDurationMinutes = 300;

    @JsonProperty("compositeCmd")
    public final String compositeCmd = "/usr/bin/composite";

    @JsonProperty("convertCmd")
    public final String convertCmd = "/usr/bin/convert";

    @JsonProperty("intermediateDir")
    public final String intermediateDir = "tmp";

    @JsonProperty("outputPath")
    public final String outputPath = "/home/bertrik/stofradar.nl/www";

    @JsonProperty("airRestApi")
    public final AirRestApiConfig airRestApiConfig = new AirRestApiConfig();

    @JsonProperty("renderJobs")
    public final List<RenderJob> renderJobs = new ArrayList<>();

    public ParticulateMapperConfig() {
        renderJobs.add(new RenderJob("netherlands", "netherlands.png", 53.560406, 3.359403, 50.750938, 7.227496, 1.0,
                10.0, 65, 5));
    }

}
