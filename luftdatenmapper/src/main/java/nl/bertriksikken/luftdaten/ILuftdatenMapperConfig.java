package nl.bertriksikken.luftdaten;

import java.util.List;

/**
 * Configuration interface for the application.
 */
public interface ILuftdatenMapperConfig {

    /**
     * @return the base luftdaten.info URL
     */
    String getLuftdatenUrl();
    
    /**
     * @return the luftdaten.info timeout
     */
    int getLuftdatenTimeoutMs();
    
    /**
     * @return the list of blacklisted nodes by number
     */
    List<Integer> getLuftdatenBlacklist();
    
    /**
     * @return the path to the imagemagick 'composite' command
     */
    String getCompositeCmd();

    /**
     * @return the path to the imagemagick 'convert' command
     */
    String getConvertCmd();

    /**
     * @return the path to the directory with intermediate files
     */
    String getIntermediateDir();

    /**
     * @return the path to the base map
     */
    String getBaseMapPath();

    /**
     * @return the name of the final output file
     */
    String getOutputPath();

}