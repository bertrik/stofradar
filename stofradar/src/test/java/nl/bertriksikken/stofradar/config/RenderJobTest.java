package nl.bertriksikken.stofradar.config;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.stofradar.render.EDataSource;

public final class RenderJobTest {

    @Test
    public void testSpecificSources() {
        RenderJob job = new RenderJob("job", "map", 0, 0, 0, 0, 0, 0, 0, "sensor.community,samenmeten");
        Set<EDataSource> sources = job.getSources();
        Assert.assertEquals(Set.of(EDataSource.SENSOR_COMMUNITY, EDataSource.SAMENMETEN), sources);
    }

    @Test
    public void testAllSources() {
        RenderJob job = new RenderJob("job", "map", 0, 0, 0, 0, 0, 0, 0, "*");
        Set<EDataSource> sources = job.getSources();
        Assert.assertEquals(Set.of(EDataSource.values()), sources);
    }

}
