package nl.bertriksikken.stofradar.geolocation;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public final class GeoLocationResourceTest {

    @Test
    public void testRateLimit() throws IOException {
        GeoLocationRequest request = new GeoLocationRequest(false);
        GeoLocationResponse response = new GeoLocationResponse(0.0, 0.0, 0.0);
        IGeoLocator locator = Mockito.mock(IGeoLocator.class);
        Mockito.when(locator.geoLocate(Mockito.any())).thenReturn(response);
        GeoLocationResource resource = new GeoLocationResource(locator);

        // fire requests in quick succession and check that only 10 got through
        for (int i = 0; i < 20; i++) {
            try {
                resource.geoLocate(request);
            } catch (WebApplicationException e) {
                // ignore
            }
        }
        var unused = Mockito.verify(locator, Mockito.atMost(10));

        // verify exception
        ClientErrorException exception = Assert.assertThrows(ClientErrorException.class, () -> resource.geoLocate(request));
        Assert.assertEquals(Response.Status.TOO_MANY_REQUESTS.getStatusCode(), exception.getResponse().getStatus());
    }
}
