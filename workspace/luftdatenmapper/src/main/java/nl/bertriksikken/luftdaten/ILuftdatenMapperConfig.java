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
     * @return comma-separated list of blacklisted stations
     */
    List<Integer> getLuftdatenBlacklist();

    /**
     * @return the path to the imagemagick 'composite' command
     */
    String getCompositeCmd();

    /**
     * @return the geometry (dimensions width x height) of the dust overlay
     */
    String getOverlayGeometry();

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
