package io.github.isopov.jmh;

import com.google.common.collect.ImmutableMap;
import javolution.util.FastMap;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author isopov
 * @since 03.04.2014
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Threads(32)
public class IsEmptyBenchmark {

	static int KEYS_NUMBER = 1000;


	final Map<String, String> fastMap = new FastMap<>();
	final Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
	final Map<String, String> immutableMap;

	final Map<String, String> emptyFastMap = new FastMap<>();
	final Map<String, String> emptyConcurrentHashMap = new ConcurrentHashMap<>();
	final Map<String, String> emptyImmutableMap = ImmutableMap.of();

	public IsEmptyBenchmark() {
		Random r = new Random();
		for (int i = 0; i < KEYS_NUMBER; i++) {
			String key = String.valueOf(r.nextLong());
			String val = String.valueOf(r.nextLong());
			fastMap.put(key, val);
			concurrentHashMap.put(key, val);
		}
		immutableMap = ImmutableMap.copyOf(fastMap);
	}

	@Benchmark
	public boolean isEmptyFastMapFalse() {
		return fastMap.isEmpty();
	}

	@Benchmark
	public boolean isEmptyFastMapTrue() {
		return emptyFastMap.isEmpty();
	}

	@Benchmark
	public boolean isEmptyConcurrentHashMapFalse() {
		return concurrentHashMap.isEmpty();
	}

	@Benchmark
	public boolean isEmptyConcurrentHashMapTrue() {
		return emptyConcurrentHashMap.isEmpty();
	}

	@Benchmark
	public boolean isEmptyImmutableMaFalse() {
		return immutableMap.isEmpty();
	}

	@Benchmark
	public boolean isEmptyImmutableMapTrue() {
		return emptyImmutableMap.isEmpty();
	}


	@Benchmark
	public boolean isZeroSizeFastMapFalse() {
		return fastMap.size() == 0;
	}

	@Benchmark
	public boolean isZeroSizeFastMapTrue() {
		return emptyFastMap.size() == 0;
	}

	@Benchmark
	public boolean isZeroSizeConcurrentHashMapFalse() {
		return concurrentHashMap.size() == 0;
	}

	@Benchmark
	public boolean isZeroSizeConcurrentHashMapTrue() {
		return emptyConcurrentHashMap.size() == 0;
	}

	@Benchmark
	public boolean isZeroSizeImmutableMaFalse() {
		return immutableMap.size() == 0;
	}

	@Benchmark
	public boolean isZeroSizeImmutableMapTrue() {
		return emptyImmutableMap.size() == 0;
	}

	//  Corei7 47770 Linux
	//	Benchmark                                                     Mode   Samples         Mean   Mean error    Units
	//	c.s.m.j.IsEmptyBenchmark.isEmptyConcurrentHashMapFalse        avgt        15       14.509        0.861    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isEmptyConcurrentHashMapTrue         avgt        15       79.762        2.926    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isEmptyFastMapFalse                  avgt        15       14.661        0.190    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isEmptyFastMapTrue                   avgt        15       13.901        0.898    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isEmptyImmutableMaFalse              avgt        15       11.665        0.393    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isEmptyImmutableMapTrue              avgt        15        6.891        0.174    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isZeroSizeConcurrentHashMapFalse     avgt        15      286.287       10.399    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isZeroSizeConcurrentHashMapTrue      avgt        15       91.699        0.823    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isZeroSizeFastMapFalse               avgt        15       13.749        0.168    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isZeroSizeFastMapTrue                avgt        15       14.888        0.190    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isZeroSizeImmutableMaFalse           avgt        15       11.766        0.692    ns/op
	//	c.s.m.j.IsEmptyBenchmark.isZeroSizeImmutableMapTrue           avgt        15        6.789        0.129    ns/op
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + IsEmptyBenchmark.class.getSimpleName() + ".*")
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(3)
				.build();

		new Runner(opt).run();
	}

}
