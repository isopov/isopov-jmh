package com.sopovs.moradanen.jmh;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
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

	@GenerateMicroBenchmark()
	public long testNanoTime() throws InterruptedException {
		return System.nanoTime();
	}

	@GenerateMicroBenchmark()
	public long testMillisFromNanoTime() throws InterruptedException {
		return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@GenerateMicroBenchmark()
	public long testMiliTime() {
		return System.currentTimeMillis();
	}


	//  On linux, Corei7 4770
	//	Benchmark                                              Mode   Samples         Mean   Mean error    Units
	//	c.s.m.j.SystemTimeBenchmark.testMiliTime               avgt        15       18.331        0.066    ns/op
	//	c.s.m.j.SystemTimeBenchmark.testMillisFromNanoTime     avgt        15       16.837        0.134    ns/op
	//	c.s.m.j.SystemTimeBenchmark.testNanoTime               avgt        15       16.167        0.228    ns/op
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
