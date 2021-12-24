package nl.bertriksikken.stofradar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.stofradar.config.LuftdatenMapperConfig;

public final class LuftdatenMapperConfigTest {

	@Test
	public void testBlacklist() {
		LuftdatenMapperConfig config = new LuftdatenMapperConfig();
		List<String> blackList = config.getSensComConfig().getBlacklist();
		Assert.assertTrue(blackList.contains("11697"));
	}
	
}
