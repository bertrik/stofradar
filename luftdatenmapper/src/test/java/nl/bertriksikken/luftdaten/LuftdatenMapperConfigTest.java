package nl.bertriksikken.luftdaten;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public final class LuftdatenMapperConfigTest {

	@Test
	public void testBlacklist() {
		ILuftdatenMapperConfig config = new LuftdatenMapperConfig();
		List<Integer> blackList = config.getLuftdatenBlacklist();
		Assert.assertTrue(blackList.contains(11697));
	}
	
}
