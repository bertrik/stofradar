package nl.bertriksikken.stofradar.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class DataSourceTest {

    @Test
    public void testFromName() {
        assertEquals(EDataSource.SENSOR_COMMUNITY, EDataSource.fromName("sensor.community"));
        assertNull(EDataSource.fromName("unknown"));
    }

}
