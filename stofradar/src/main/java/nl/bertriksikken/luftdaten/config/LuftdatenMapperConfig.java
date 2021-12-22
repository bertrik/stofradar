package nl.bertriksikken.luftdaten.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class.
 */
public final class LuftdatenMapperConfig {

    @JsonProperty("luftdaten")
    private final LuftdatenConfig luftdatenConfig = new LuftdatenConfig();

    @JsonProperty("keepingDurationMinutes")
    private final int keepingDurationMinutes = 60;

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

    public LuftdatenMapperConfig() {
        renderJobs.add(
                new RenderJob("netherlands", "netherlands.png", 53.560406, 3.359403, 50.750938, 7.227496, 1.0, 10.0));
    }

    /**
     * @return the luftdaten configuration
     */
    public LuftdatenConfig getLuftdatenConfig() {
        return luftdatenConfig;
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

}
