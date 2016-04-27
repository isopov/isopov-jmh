package com.sopovs.moradanen.jmh;

import static org.openjdk.jmh.annotations.Threads.MAX;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
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
public class FilterBenchmark {

	@Param({ "synchronized", "atomic" })
	private String filterType;
	private Filter filter;

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + FilterBenchmark.class.getSimpleName() + ".*").build();
		new Runner(opt).run();
	}

	@Setup
	public void setup() {
		switch (filterType) {
		case "synchronized":
			filter = new SyncrhonizedFilter(10, TimeUnit.SECONDS);
			break;
		case "atomic":
			filter = new AtomicFilter(10, TimeUnit.SECONDS);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	@Threads(MAX)
	@Benchmark
	public boolean benchmark() {
		return filter.isSignalAllowed();
	}

	interface Filter {
		boolean isSignalAllowed();
	}

	public static class AtomicFilter implements Filter {
		private final long nsForSignal;
		private final AtomicLong last;

		public AtomicFilter(int n, TimeUnit timeUnit) {
			nsForSignal = timeUnit.toNanos(1) / n;
			last = new AtomicLong(System.nanoTime() - nsForSignal);
		}

		@Override
		public boolean isSignalAllowed() {
			long lastTime = last.get();
			long currentTime = System.nanoTime();
			if (currentTime - lastTime >= nsForSignal) {
				return last.compareAndSet(lastTime, currentTime);
			}
			return false;
		}

	}

	public static class SyncrhonizedFilter implements Filter {
		private final long nsForSignal;
		private long last;

		public SyncrhonizedFilter(int n, TimeUnit timeUnit) {
			nsForSignal = timeUnit.toNanos(1) / n;
			last = System.nanoTime() - nsForSignal;
		}

		@Override
		public synchronized boolean isSignalAllowed() {
			long nanoTime = System.nanoTime();
			if (nanoTime - last >= nsForSignal) {
				last = nanoTime;
				return true;
			}
			return false;
		}
	}

}
