package com.sopovs.moradanen.jmh.ycsb;

import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.SIMPLE_ARRAY;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.SIMPLE_RANDOM;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.SIMPLE_STREAM;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.SIMPLE_STRING;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.YCSB_ARRAY;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.YCSB_RANDOM;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.YCSB_STREAM;
import static com.sopovs.moradanen.jmh.ycsb.ByteIteratorBenchmark.YCSB_STRING;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteIteratorBenchmarkTest {

	@Test
	public void testToArray() {
		for (String type : new String[] { SIMPLE_RANDOM, YCSB_RANDOM, SIMPLE_ARRAY, YCSB_ARRAY, YCSB_STREAM,
				SIMPLE_STREAM, SIMPLE_STRING, YCSB_STRING }) {
			ByteIteratorBenchmark.ToArray bench = new ByteIteratorBenchmark.ToArray();
			bench.size = 333;
			bench.type = type;
			bench.setup();
			assertEquals(type, 333, bench.get().length);
			assertEquals(type, 333, bench.get().length);
		}

	}

}
