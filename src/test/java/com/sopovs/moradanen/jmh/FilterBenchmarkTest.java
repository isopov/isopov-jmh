package com.sopovs.moradanen.jmh;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openjdk.jmh.annotations.Param;

import com.sopovs.moradanen.jmh.FilterBenchmark.Filter;

@RunWith(Parameterized.class)
public class FilterBenchmarkTest {

	@Parameters(name = "{0}")
	public static String[] filterTypes() throws Exception {
		return FilterBenchmark.class.getField("filterType").getAnnotation(Param.class).value();
	}

	@Parameter
	public String filterType;

	private Filter filter;

	@Before
	public void setup() {
		filter = FilterBenchmark.createFilter(filterType);
	}

	@After
	public void tearDown() {
		filter.shutdown();
	}

	@Test
	public void testMultipleTthreads() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		List<Future<Boolean>> acquisitions = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			acquisitions.add(pool.submit(filter::isSignalAllowed));
		}
		pool.shutdown();
		pool.awaitTermination(500, TimeUnit.MILLISECONDS);

		assertEquals(1000, acquisitions.size());
		assertEquals(1000, acquisitions.stream().filter(Future::isDone).count());

		long realAcuisitions = acquisitions.stream().map(this::get).filter(Boolean.TRUE::equals).count();
		assertEquals(10, realAcuisitions);

	}

	private <T> T get(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException();
		}
	}

	@Test
	public void testSecondAfterSleep() throws InterruptedException {
		Thread.sleep(500);
		long start = System.nanoTime();
		int counter = 0;
		while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(1)) {
			if (filter.isSignalAllowed()) {
				counter++;
			}
		}
		assertEquals(filter.getClass() + " failed", 10, counter);
	}

	@Test
	public void testSecond() {

		long start = System.nanoTime();
		int counter = 0;
		while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(1)) {
			if (filter.isSignalAllowed()) {
				counter++;
			}
		}
		assertEquals(filter.getClass() + " failed", 10, counter);
	}

	@Test
	public void testTwoEvents() {
		int counter = 0;
		for (int i = 0; i < 5; i++) {
			if (filter.isSignalAllowed()) {
				counter++;
			}
		}

		assertEquals(filter.getClass() + " failed", 5, counter);
	}

}
