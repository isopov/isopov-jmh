package com.sopovs.moradanen.jmh;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author isopov
 * @since 19.03.2014
 */
public class DeepCopyBenchmarkTest {

	DeepCopyBenchmark bench = new DeepCopyBenchmark();

	@Test
	public void testLoopCopy() throws Exception {
		assertEquals(bench.state, bench.loopCopy());
	}
	@Test
	public void testDeepCopy() throws Exception {
		assertEquals(bench.state, bench.deepCopy());
	}
	@Test
	public void testCloneCopy() throws Exception {
		assertEquals(bench.state, bench.cloneCopy());
	}
}
