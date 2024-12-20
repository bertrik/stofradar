package nl.bertriksikken.stofradar.config;

import nl.bertriksikken.stofradar.render.EDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public final class RenderJobTest {

    @Test
    public void testSpecificSources() {
        RenderJob job = new RenderJob("job", "map", 0, 0, 0, 0, 0, 0, 0, "sensor.community,samenmeten");
        Set<EDataSource> sources = job.getSources();
        Assertions.assertEquals(Set.of(EDataSource.SENSOR_COMMUNITY, EDataSource.SAMENMETEN), sources);
    }

    @Test
    public void testAllSources() {
        RenderJob job = new RenderJob("job", "map", 0, 0, 0, 0, 0, 0, 0, "*");
        Set<EDataSource> sources = job.getSources();
        Assertions.assertEquals(Set.of(EDataSource.values()), sources);
    }

}
