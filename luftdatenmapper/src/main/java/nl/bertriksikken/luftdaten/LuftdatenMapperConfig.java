package nl.bertriksikken.luftdaten;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.luftdaten.config.LuftdatenConfig;

/**
 * Configuration class.
 */
public final class LuftdatenMapperConfig {

    @JsonProperty("luftdaten")
	private LuftdatenConfig luftdatenConfig = new LuftdatenConfig();

    @JsonProperty("compositeCmd")
    private String compositeCmd = "c:/cygwin64/bin/composite.exe";
    
    @JsonProperty("convertCmd")
    private String convertCmd = "c:/cygwin64/bin/convert.exe";
    
    @JsonProperty("intermediateDir")
    private String intermediateDir = "tmp";
    
    @JsonProperty("outputPath")
    private String outputPath = "/home/bertrik/stofradar.nl/www";

	/**
	 * @return the luftdaten configuration
	 */
	public LuftdatenConfig getLuftdatenConfig() {
		return luftdatenConfig;
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
