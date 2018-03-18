package com.sopovs.moradanen.jmh.ycsb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RandomByteIteratorBenchmarkTest {

	@Test
	public void testToArray() {
		RandomByteIteratorBenchmark.ToArray bench = new RandomByteIteratorBenchmark.ToArray();
		bench.size = 333;
		bench.type = "ycsb";
		bench.setup();
		assertEquals(333, bench.get().length);
		assertEquals(333, bench.get().length);
	}
	
}
