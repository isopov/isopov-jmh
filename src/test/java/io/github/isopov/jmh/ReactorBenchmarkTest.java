package io.github.isopov.jmh;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ReactorBenchmarkTest {

	private ReactorBenchmark bench;

	@Before
	public void setup() {
		bench = new ReactorBenchmark();
		bench.books = 3;
		bench.bookSize = 5;
		bench.shelves = 7;
		bench.setup();
	}

	@Test
	public void testListVersion() {
		List<List<String>> result = new ArrayList<>();
		bench.listVersion(result::add);

		assertEquals(bench.shelves, result.size());
		assertEquals(bench.shelves, result.stream().distinct().count());
		for (List<String> books : result) {
			assertEquals(bench.books, books.size());
		}
	}

	@Test
	public void testFluxVersion() {
		List<List<String>> result = new ArrayList<>();
		bench.fluxVersion(result::add);

		assertEquals(bench.shelves, result.size());
		assertEquals(bench.shelves, result.stream().distinct().count());
		for (List<String> books : result) {
			assertEquals(bench.books, books.size());
		}
	}

}
