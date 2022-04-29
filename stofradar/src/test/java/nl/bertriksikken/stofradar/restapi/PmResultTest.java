package nl.bertriksikken.stofradar.restapi;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PmResultTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // ordinary number
        Assert.assertEquals("{\"pm2.5\":12.46}", mapper.writeValueAsString(new AirResult(12.45678)));
        // NaN
        Assert.assertEquals("{}", mapper.writeValueAsString(new AirResult(Double.NaN)));
    }

}
