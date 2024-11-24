package nl.bertriksikken.stofradar.geolocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.gelocation.GeoLocationResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public final class GeoLocationResponseTest {

    @Test
    public void testDeserialize() throws IOException {
        URL url = getClass().getClassLoader().getResource("GeoLocationResponse.json");
        ObjectMapper mapper = new ObjectMapper();
        GeoLocationResponse response = mapper.readValue(url, GeoLocationResponse.class);

        Assert.assertEquals(-22.7539192, response.location().latitude(), 1E-6);
        Assert.assertEquals(-43.4371081, response.location().longitude(), 1E-6);
        Assert.assertEquals(100.0, response.accuracy(), 0.1);
    }
}
