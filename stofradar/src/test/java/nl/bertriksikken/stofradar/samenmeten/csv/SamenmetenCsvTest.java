package nl.bertriksikken.stofradar.samenmeten.csv;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public final class SamenmetenCsvTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvTest.class);

    @Test
    public void testDeserialize() throws IOException {
        URL url = Resources.getResource("lucht.txt");
        String lucht = Resources.toString(url, StandardCharsets.US_ASCII);
        SamenmetenCsv csv = SamenmetenCsv.parse(lucht);
        LOG.info("csv = {}", csv);
    }
    
}
