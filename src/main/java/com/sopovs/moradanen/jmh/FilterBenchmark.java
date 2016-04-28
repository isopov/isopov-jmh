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

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class FilterBenchmark {

	@Param({ "synchronized", "atomic", "guava" })
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
			filter = new SynchronizedFilter(10, TimeUnit.SECONDS);
			break;
		case "atomic":
			filter = new AtomicFilter(10, TimeUnit.SECONDS);
			break;
		case "guava":
			filter = new GuavaFilter(10, TimeUnit.SECONDS);
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

	public static class GuavaFilter implements Filter {
		private final RateLimiter limiter;

		public GuavaFilter(int n) {
			this(n, TimeUnit.SECONDS);
		}

		public GuavaFilter(int n, TimeUnit timeUnit) {
			limiter = RateLimiter.create(timeUnit.toSeconds(1) * n);
		}

		@Override
		public boolean isSignalAllowed() {
			return limiter.tryAcquire();
		}
	}

	public static class AtomicFilter implements Filter {
		private final long nanosForSignal;
		private final AtomicLong last;

		public AtomicFilter(int n) {
			this(n, TimeUnit.SECONDS);
		}

		public AtomicFilter(int n, TimeUnit timeUnit) {
			nanosForSignal = nanosForSignal(n, timeUnit);
			last = new AtomicLong(System.nanoTime() - nanosForSignal);
		}

		@Override
		public boolean isSignalAllowed() {
			long lastTime = last.get();
			long currentTime = System.nanoTime();
			if (currentTime - lastTime >= nanosForSignal) {
				return last.compareAndSet(lastTime, currentTime);
			}
			return false;
		}

	}

	private static long nanosForSignal(int n, TimeUnit timeUnit) {
		long result = timeUnit.toNanos(1) / n;
		Preconditions.checkState(result * n == timeUnit.toNanos(1));
		return result;
	}

	public static class SynchronizedFilter implements Filter {
		private final long nsForSignal;
		private long last;

		public SynchronizedFilter(int n) {
			this(n, TimeUnit.SECONDS);
		}

		public SynchronizedFilter(int n, TimeUnit timeUnit) {
			nsForSignal = nanosForSignal(n, timeUnit);
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
