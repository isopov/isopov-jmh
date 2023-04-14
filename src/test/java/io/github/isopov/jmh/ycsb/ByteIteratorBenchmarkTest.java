package io.github.isopov.jmh.ycsb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteIteratorBenchmarkTest {

	@Test
	public void testToArray() {
		for (String type : new String[] { ByteIteratorBenchmark.SIMPLE_RANDOM, ByteIteratorBenchmark.YCSB_RANDOM, ByteIteratorBenchmark.SIMPLE_ARRAY, ByteIteratorBenchmark.YCSB_ARRAY, ByteIteratorBenchmark.YCSB_STREAM,
				ByteIteratorBenchmark.SIMPLE_STREAM, ByteIteratorBenchmark.SIMPLE_STRING, ByteIteratorBenchmark.YCSB_STRING }) {
			ByteIteratorBenchmark.ToArray bench = new ByteIteratorBenchmark.ToArray();
			bench.size = 333;
			bench.type = type;
			bench.setup();
			assertEquals(type, 333, bench.get().length);
			assertEquals(type, 333, bench.get().length);
		}

	}

}
