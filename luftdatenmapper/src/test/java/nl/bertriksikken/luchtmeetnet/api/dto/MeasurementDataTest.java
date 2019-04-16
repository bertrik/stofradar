package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MeasurementDataTest {

    private final Logger LOG = LoggerFactory.getLogger(MeasurementDataTest.class);

    @Test
    public void testSerialize() throws JsonProcessingException {
        MeasurementData data = new MeasurementData(1.23, "NO", new Date());
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(data);
        LOG.info(s);
    }

}
