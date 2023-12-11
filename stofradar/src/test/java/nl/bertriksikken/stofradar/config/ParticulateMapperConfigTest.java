package nl.bertriksikken.stofradar.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

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
        Assert.assertEquals("/opt/stofradar.nl/www", config.outputPath);
    }

}
