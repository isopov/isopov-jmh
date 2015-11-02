package com.sopovs.moradanen.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

@SuppressWarnings("restriction")
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class StringUnsafeBenchmark {

	private static final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();

	private char[] input = "Hello World!".toCharArray();

	@Benchmark
	public String constructor() {
		return new String(input);
	}

	@Benchmark
	public void newStringUnsafe() {
		javaLangAccess.newStringUnsafe(input);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + StringUnsafeBenchmark.class.getSimpleName() + ".*")
				// .addProfiler(LinuxPerfNormProfiler.class)
				.build();

		new Runner(opt).run();
	}

}
