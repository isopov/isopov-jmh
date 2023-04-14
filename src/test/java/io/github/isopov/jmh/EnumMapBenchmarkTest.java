package io.github.isopov.jmh;


import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class EnumMapBenchmarkTest  {
	@Test
	public void testEnums(){
		assertEquals(80, EnumMapBenchmark.LargeEnum.values().length);
		assertEquals(25, EnumMapBenchmark.SmallEnum.values().length);
	}

	@Test
	public void testMaps(){
		EnumMapBenchmark bench = new EnumMapBenchmark();
		assertEquals(40, bench.largeEnumMap.size());
		assertEquals(40, bench.largeHashMap.size());
		assertEquals(40,bench.largeFastMap.size());

		assertEquals(13,bench.smallEnumMap.size());
		assertEquals(13,bench.smallHashMap.size());
		assertEquals(13,bench.smallFastMap.size());
	}
}
