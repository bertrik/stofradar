package nl.bertriksikken.stofradar.geolocation.beacondb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.geolocation.GeoLocationRequest;
import nl.bertriksikken.stofradar.geolocation.GeoLocationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class RunBeacondbClient {

    private static final Logger LOG = LoggerFactory.getLogger(RunBeacondbClient.class);
    private static final ObjectWriter WRITER = new ObjectMapper().findAndRegisterModules().writerWithDefaultPrettyPrinter();

    public static void main(String[] args) throws IOException {
        RunBeacondbClient runner = new RunBeacondbClient();
        runner.run();
    }

    private void run() throws IOException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        HostConnectionConfig config = new HostConnectionConfig("https://api.beacondb.net", 10);
        GeoLocationRequest request = new GeoLocationRequest(true);
        request.addAccessPoint("82:B6:87:4E:D8:13", -73);
        request.addAccessPoint("54:83:3A:3B:15:B1", -76);
        request.addAccessPoint("A0:21:B7:9F:5A:BC", -50);
        request.addAccessPoint("88:AC:C0:B5:E5:71", -84);
        LOG.info("Request: {}", WRITER.writeValueAsString(request));
        try (BeacondbClient client = BeacondbClient.create(config, mapper, "github.com/bertrik/stofradar")) {
            GeoLocationResponse response = client.geolocate(request);
            LOG.info("Response: {}", response);
        }
    }

}
