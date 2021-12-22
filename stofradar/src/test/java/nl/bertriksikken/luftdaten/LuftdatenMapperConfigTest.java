package nl.bertriksikken.luftdaten;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.luftdaten.config.LuftdatenMapperConfig;

public final class LuftdatenMapperConfigTest {

	@Test
	public void testBlacklist() {
		LuftdatenMapperConfig config = new LuftdatenMapperConfig();
		List<Integer> blackList = config.getLuftdatenConfig().getBlacklist();
		Assert.assertTrue(blackList.contains(11697));
	}
	
}
