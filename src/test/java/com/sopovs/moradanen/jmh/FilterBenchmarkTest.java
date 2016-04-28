package com.sopovs.moradanen.jmh;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.sopovs.moradanen.jmh.FilterBenchmark.Filter;

@RunWith(Parameterized.class)
public class FilterBenchmarkTest {

	@Parameters
	public static Filter[] filters() {
		return new Filter[] {
				new FilterBenchmark.SyncrhonizedFilter(10, SECONDS),
				new FilterBenchmark.GuavaFilter(10, TimeUnit.SECONDS),
				new FilterBenchmark.AtomicFilter(10, TimeUnit.SECONDS)
		};
	}

	@Parameter
	public Filter filter;

	@Test
	public void test() {

		long start = System.nanoTime();
		int counter = 0;
		while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(1)) {
			if (filter.isSignalAllowed()) {
				counter++;
			}
		}
		assertEquals(filter.getClass() + " failed", 10, counter);

	}

}
