package io.github.isopov.jmh;

import com.google.common.collect.ImmutableMap;
import javolution.util.FastMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by isopov on 2/27/14.
 */
@State(Scope.Benchmark)
public class FastMapBenchmark {

	static int KEYS_NUMBER = 100000;
	static int KEYS_TO_TEST_NUMBER = KEYS_NUMBER / 10;


	final Map<String, String> fastMap = new FastMap<>();
	final Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
	final Map<String, String> hashMap = new HashMap<>();
	final Map<String, String> immutableMap;
	final List<String> keysToTest = new ArrayList<>(KEYS_TO_TEST_NUMBER);
	final List<String> keys = new ArrayList<>(KEYS_NUMBER);
	final List<String> vals = new ArrayList<>(KEYS_NUMBER);

	public FastMapBenchmark() {
		Random r = new Random();
		for (int i = 0; i < KEYS_NUMBER; i++) {
			String key = String.valueOf(r.nextLong());
			keys.add(key);
			String val = String.valueOf(r.nextLong());
			vals.add(val);
			fastMap.put(key, val);
			concurrentHashMap.put(key, val);
			hashMap.put(key, val);
			if (i < KEYS_TO_TEST_NUMBER) {
				keysToTest.add(key);
			}
		}
		immutableMap = ImmutableMap.copyOf(hashMap);
	}


	@Benchmark
	public Map<String, String> createFastMap() {
		Map<String, String> fastMap = new FastMap<>();
		for (Iterator<String> keyIterator = keys.iterator(), valIterator = vals.iterator(); keyIterator.hasNext(); ) {
			String key = keyIterator.next();
			String val = valIterator.next();
			fastMap.put(key, val);
		}
		return fastMap;
	}

	@Benchmark
	public Map<String, String> createHashMap() {
		Map<String, String> hashMap = new HashMap<>();
		for (Iterator<String> keyIterator = keys.iterator(), valIterator = vals.iterator(); keyIterator.hasNext(); ) {
			String key = keyIterator.next();
			String val = valIterator.next();
			hashMap.put(key, val);
		}
		return hashMap;
	}

	@Benchmark
	public Map<String, String> createEmptyHashMap() {
		return new HashMap<>();
	}

	@Benchmark
	public Map<String, String> createEmptyFastMap() {
		return new FastMap<>();
	}

	@Benchmark
	public void getFromFastMap(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(fastMap.get(key));
		}
	}

	@Benchmark
	public void getFromConcurrentHashMap(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(concurrentHashMap.get(key));
		}
	}

	@Benchmark
	public void getFromImmutableMap(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(immutableMap.get(key));
		}
	}

	@Benchmark
	public void getFromHashMap(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(hashMap.get(key));
		}
	}

	@Benchmark
	@Threads(8)
	public void getFromConcurrentHashMap8Threads(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(concurrentHashMap.get(key));
		}
	}

	@Benchmark
	@Threads(8)
	public void getFromImmutableMap8Threads(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(immutableMap.get(key));
		}
	}

	@Benchmark
	@Threads(8)
	public void getFromFastMap8Threads(Blackhole bh) {
		for (String key : keysToTest) {
			bh.consume(fastMap.get(key));
		}
	}

//    Benchmark                                                     Mode   Samples         Mean   Mean error    Units
//    c.s.m.j.FastMapBenchmark.createFastMap                       thrpt        15        0.097        0.003   ops/ms
//    c.s.m.j.FastMapBenchmark.createHashMap                       thrpt        15        0.093        0.004   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromConcurrentHashMap            thrpt        15        6.340        0.756   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromConcurrentHashMap8Threads    thrpt        15       34.010        0.615   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromFastMap                      thrpt        15        8.280        0.225   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromFastMap8Threads              thrpt        15       32.464        0.436   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromHashMap                      thrpt        15        7.272        0.497   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromImmutableMap                 thrpt        15        3.962        0.070   ops/ms
//    c.s.m.j.FastMapBenchmark.getFromImmutableMap8Threads         thrpt        15       20.528        0.768   ops/ms
//    c.s.m.j.FastMapBenchmark.createEmptyFastMap                  thrpt        15    45653.391     1456.907   ops/ms
//    c.s.m.j.FastMapBenchmark.createEmptyHashMap                  thrpt        15    95875.401      725.543   ops/ms

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
//                .jvm("/usr/lib/jvm/java-8-oracle/bin/java")
				.include(".*" + FastMapBenchmark.class.getSimpleName() + ".*")
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(3)
				.build();

		new Runner(opt).run();
	}
}
