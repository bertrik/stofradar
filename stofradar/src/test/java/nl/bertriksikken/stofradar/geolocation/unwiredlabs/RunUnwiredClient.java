package nl.bertriksikken.stofradar.geolocation.unwiredlabs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.geolocation.GeoLocationRequest;
import nl.bertriksikken.stofradar.geolocation.GeoLocationResponse;
import nl.bertriksikken.stofradar.geolocation.beacondb.RunBeacondbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class RunUnwiredClient {

    private static final File CONFIG_FILE = new File(".unwired.yaml");
    private static final Logger LOG = LoggerFactory.getLogger(RunBeacondbClient.class);

    public static void main(String[] args) throws IOException {
        RunUnwiredClient runner = new RunUnwiredClient();
        runner.run();
    }

    private void run() throws IOException {
        YAMLMapper yamlMapper = new YAMLMapper();
        UnwiredConfig config = new UnwiredConfig();
        if (CONFIG_FILE.exists()) {
            config = yamlMapper.readValue(CONFIG_FILE, UnwiredConfig.class);
        } else {
            yamlMapper.writeValue(CONFIG_FILE, config);
            return;
        }

        HostConnectionConfig hostConnectionConfig = new HostConnectionConfig(config.url, 10);

        GeoLocationRequest request = new GeoLocationRequest(false);
        request.addAccessPoint("8C:19:B5:58:A6:A1", -69);
        request.addAccessPoint("A2:21:B7:9F:5A:BC", -54);
        request.addAccessPoint("A6:21:B7:9F:5A:BC", -50);
        String userAgent = "github.com/bertrik/stofradar";
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        try (UnwiredClient client = UnwiredClient.create(hostConnectionConfig, config.token(), mapper, userAgent)) {
            GeoLocationResponse response = client.geoLocate(request);
            LOG.info("Response: {}", response);
        }
    }

    private record UnwiredConfig(@JsonProperty("url") String url, @JsonProperty("token") String token) {
        UnwiredConfig() {
            this("https://eu1.unwiredlabs.com", "token");
        }
    }

}
