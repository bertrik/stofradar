package nl.bertriksikken.stofradar.geolocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class GeoLocationResponseTest {

    @Test
    public void testDeserialize() throws IOException {
        URL url = getClass().getClassLoader().getResource("GeoLocationResponse.json");
        ObjectMapper mapper = new ObjectMapper();
        GeoLocationResponse response = mapper.readValue(url, GeoLocationResponse.class);

        assertEquals(-22.7539192, response.location().latitude(), 1E-6);
        assertEquals(-43.4371081, response.location().longitude(), 1E-6);
        assertEquals(100.0, response.accuracy(), 0.1);
    }
}
