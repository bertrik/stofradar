package nl.bertriksikken.stofradar.config;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public final class ParticulateMapperConfigTest {

    @Test
    public void testDefault() throws IOException {
        ParticulateMapperConfig config = new ParticulateMapperConfig();
        ObjectMapper mapper = new YAMLMapper();
        File file = new File("default_config.yaml");
        mapper.writeValue(file, config);
    }

}
