package nl.bertriksikken.stofradar.render;

import org.junit.Assert;
import org.junit.Test;

public final class DataSourceTest {

    @Test
    public void testFromName() {
        Assert.assertEquals(EDataSource.SENSOR_COMMUNITY, EDataSource.fromName("sensor.community"));
        Assert.assertEquals(null, EDataSource.fromName("unknown"));
    }

}
