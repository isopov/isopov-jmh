package io.github.isopov.jmh;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author isopov
 * @since 02.04.2014
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class SystemTimeBenchmark {

	@Benchmark()
	public long testNanoTime() throws InterruptedException {
		return System.nanoTime();
	}

	@Benchmark()
	public long testMillisFromNanoTime() throws InterruptedException {
		return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@Benchmark()
	public long testMiliTime() {
		return System.currentTimeMillis();
	}


	//  On linux, Corei7 4770
	//	Benchmark                                              Mode   Samples         Mean   Mean error    Units
	//	c.s.m.j.SystemTimeBenchmark.testMiliTime               avgt        15       18.331        0.066    ns/op
	//	c.s.m.j.SystemTimeBenchmark.testMillisFromNanoTime     avgt        15       16.837        0.134    ns/op
	//	c.s.m.j.SystemTimeBenchmark.testNanoTime               avgt        15       16.167        0.228    ns/op

    //  On Windows, Corei3 3120-M
    //  Benchmark                                              Mode   Samples         Mean   Mean error    Units
    //  c.s.m.j.SystemTimeBenchmark.testMiliTime               avgt        15       16.499        0.058    ns/op
    //  c.s.m.j.SystemTimeBenchmark.testMillisFromNanoTime     avgt        15       19.472        0.311    ns/op
    //  c.s.m.j.SystemTimeBenchmark.testNanoTime               avgt        15       18.108        0.269    ns/op
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + SystemTimeBenchmark.class.getSimpleName() + ".*")
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(3)
				.build();

		new Runner(opt).run();
	}
}
