package nl.bertriksikken.luftdaten;

import nl.bertriksikken.luftdaten.config.BaseConfig;

/**
 * Configuration class.
 */
public final class LuftdatenMapperConfig extends BaseConfig implements ILuftdatenMapperConfig {
    
	private enum EConfigItem {
        COMPOSITE_CMD("cmd.composite", "c:/cygwin64/bin/composite.exe", "Path to the imagemagick composite command"),
        CONVERT_CMD("cmd.convert", "c:/cygwin64/bin/convert.exe", "Path to the imagemagick convert command"),
        LUFTDATEN_URL("luftdaten.url", "https://api.luftdaten.info", "luftdaten server URL (empty to disable)"),
        LUFTDATEN_TIMEOUT("luftdaten.timeout", "5000", "luftdaten server timeout (milliseconds)"),
        INTERMEDIATE_DIR("intermediate.dir", "tmp", "Path to intermediate file storage"), 
        BASEMAP_PATH("basemap.path", "netherlands.png", "Path to base map"),
        OUTPUT_PATH("output.path", "/home/bertrik/stofradar.nl/fijnstof.png", "Path to output file");
		
		private final String key, value, comment;
		
		private EConfigItem(String key, String defValue, String comment) {
			this.key = key;
			this.value = defValue;
			this.comment = comment;
		}
	}
    
    /**
     * Constructor.
     */
    public LuftdatenMapperConfig() {
        for (EConfigItem e : EConfigItem.values()) {
            add(e.key, e.value, e.comment);
        }
    }

    @Override
    public String getLuftdatenUrl() {
        return get(EConfigItem.LUFTDATEN_URL.key);
    }

    @Override
    public int getLuftdatenTimeout() {
        return Integer.parseInt(get(EConfigItem.LUFTDATEN_TIMEOUT.key));
    }

    @Override
    public String getCompositeCmd() {
        return get(EConfigItem.COMPOSITE_CMD.key);
    }

    @Override
    public String getConvertCmd() {
        return get(EConfigItem.CONVERT_CMD.key);
    }

    @Override
    public String getIntermediateDir() {
        return get(EConfigItem.INTERMEDIATE_DIR.key);
    }

    @Override
    public String getBaseMapPath() {
        return get(EConfigItem.BASEMAP_PATH.key);
    }

    @Override
    public String getOutputPath() {
        return get(EConfigItem.OUTPUT_PATH.key);
    }
    

}