package nl.bertriksikken.stofradar.samenmeten.csv;

import java.io.IOException;
import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

public final class SamenmetenCsvLuchtEntryTest {
    
    @Test
    public void testParseLine() throws IOException {
        String line = "2021-12-23 19:00:00, AMF_pm001, BA001, Amersfoort, "
                + "{\"type\":\"Point\",\"coordinates\":[5.37537,52.20406]}, 8.636, 8.614, , , -99, -99, -99, , 9, 9, ";
        SamenmetenCsvLuchtEntry csvLine = SamenmetenCsvLuchtEntry.parse(line);
        Assert.assertNotNull(csvLine);
        Instant timestamp = csvLine.getTimestamp();
        Assert.assertNotNull(timestamp);
    }

}
