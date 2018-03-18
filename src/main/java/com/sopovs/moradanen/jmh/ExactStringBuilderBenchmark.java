package com.sopovs.moradanen.jmh;

import java.util.Arrays;
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

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ExactStringBuilderBenchmark {

	private String[] inputs = new String[] { "Hello", ", ", "stranger", " ", "welcome to the land of mistery" };
	private int totalSize = Arrays.stream(inputs).mapToInt(String::length).sum();

	@Benchmark
	public String stringBuilder() {
		StringBuilder builder = new StringBuilder();
		for (String input : inputs) {
			builder.append(input);
		}
		return builder.toString();
	}

	@Benchmark
	public String stringBuilderSize() {
		StringBuilder builder = new StringBuilder(totalSize);
		for (String input : inputs) {
			builder.append(input);
		}
		return builder.toString();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + ExactStringBuilderBenchmark.class.getSimpleName() + ".*")
				// .addProfiler(LinuxPerfNormProfiler.class)
				.build();

		new Runner(opt).run();
	}
}
