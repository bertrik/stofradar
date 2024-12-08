package nl.bertriksikken.stofradar.render;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DataSourceTest {

    @Test
    public void testFromName() {
        Assertions.assertEquals(EDataSource.SENSOR_COMMUNITY, EDataSource.fromName("sensor.community"));
        Assertions.assertNull(EDataSource.fromName("unknown"));
    }

}
