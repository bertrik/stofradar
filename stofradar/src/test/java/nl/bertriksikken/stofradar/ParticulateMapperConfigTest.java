package nl.bertriksikken.stofradar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.stofradar.config.ParticulateMapperConfig;

public final class ParticulateMapperConfigTest {

	@Test
	public void testBlacklist() {
		ParticulateMapperConfig config = new ParticulateMapperConfig();
		List<String> blackList = config.getSensComConfig().getBlacklist();
		Assert.assertTrue(blackList.contains("11697"));
	}
	
}
