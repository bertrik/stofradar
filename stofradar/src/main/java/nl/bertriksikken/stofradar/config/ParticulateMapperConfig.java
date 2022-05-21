package nl.bertriksikken.stofradar.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.stofradar.meetjestad.MeetjestadConfig;
import nl.bertriksikken.stofradar.restapi.AirRestApiConfig;
import nl.bertriksikken.stofradar.samenmeten.csv.SamenmetenCsvConfig;
import nl.bertriksikken.stofradar.senscom.SensComConfig;

/**
 * Configuration class.
 */
public final class ParticulateMapperConfig {

    @JsonProperty("sensCom")
    private final SensComConfig sensComConfig = new SensComConfig();

    @JsonProperty("samenmeten")
    private final SamenmetenCsvConfig samenmetenConfig = new SamenmetenCsvConfig();

    @JsonProperty("meetjestad")
    private final MeetjestadConfig meetjestadConfig = new MeetjestadConfig();

    @JsonProperty("keepingDurationMinutes")
    private final int keepingDurationMinutes = 300;

    @JsonProperty("renderJobs")
    private final List<RenderJob> renderJobs = new ArrayList<>();

    @JsonProperty("compositeCmd")
    private final String compositeCmd = "/usr/bin/composite";

    @JsonProperty("convertCmd")
    private final String convertCmd = "/usr/bin/convert";

    @JsonProperty("intermediateDir")
    private final String intermediateDir = "tmp";

    @JsonProperty("outputPath")
    private final String outputPath = "/home/bertrik/stofradar.nl/www";

    @JsonProperty("airRestApi")
    private final AirRestApiConfig airRestApiConfig = new AirRestApiConfig();

    public ParticulateMapperConfig() {
        renderJobs.add(new RenderJob("netherlands", "netherlands.png", 53.560406, 3.359403, 50.750938, 7.227496, 1.0,
                10.0, 65));
    }

    /**
     * @return the sensor.community configuration
     */
    public SensComConfig getSensComConfig() {
        return sensComConfig;
    }

    public Duration getKeepingDuration() {
        return Duration.ofMinutes(keepingDurationMinutes);
    }

    public List<RenderJob> getRenderJobs() {
        return Collections.unmodifiableList(renderJobs);
    }

    /**
     * @return the path to the imagemagick 'composite' command
     */
    public String getCompositeCmd() {
        return compositeCmd;
    }

    /**
     * @return the path to the imagemagick 'convert' command
     */
    public String getConvertCmd() {
        return convertCmd;
    }

    /**
     * @return the path to the directory with intermediate files
     */
    public String getIntermediateDir() {
        return intermediateDir;
    }

    /**
     * @return the name of the final output file
     */
    public String getOutputPath() {
        return outputPath;
    }

    public SamenmetenCsvConfig getSamenmetenCsvConfig() {
        return samenmetenConfig;
    }

    public AirRestApiConfig getPmRestApiConfig() {
        return airRestApiConfig;
    }

    public MeetjestadConfig getMeetjestadConfig() {
        return meetjestadConfig;
    }

}
