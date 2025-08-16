package nl.bertriksikken.stofradar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ParticulateMapperConfigTest {

    private final ObjectMapper mapper = new YAMLMapper();

    @Test
    public void testDefault() throws IOException {
        ParticulateMapperConfig config = new ParticulateMapperConfig();
        File file = new File("default_config.yaml");
        mapper.writeValue(file, config);
    }

    @Test
    public void testReadConfig() throws IOException {
        URL url = getClass().getClassLoader().getResource("test_config.yaml");
        ParticulateMapperConfig config = mapper.readValue(url, ParticulateMapperConfig.class);
        assertEquals("/opt/stofradar.nl/www", config.outputPath);
    }

}
