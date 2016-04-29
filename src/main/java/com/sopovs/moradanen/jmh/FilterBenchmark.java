package com.sopovs.moradanen.jmh;

import static org.openjdk.jmh.annotations.Threads.MAX;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.openjdk.jmh.annotations.TearDown;
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

	@Param({
			"SynchronizedFilter",
			"GuavaFilter",
			"AtomicFilter",
			"SynchronizedDequeFilter",
			"SingleSchedulerFilter",
	})
	public String filterType;
	private Filter filter;

	static Filter createFilter(String filterType) {
		switch (filterType) {
		case "SynchronizedFilter":
			return new FilterBenchmark.SynchronizedFilter(10);
		case "GuavaFilter":
			return new FilterBenchmark.GuavaFilter(10);
		case "AtomicFilter":
			return new FilterBenchmark.AtomicFilter(10);
		case "SynchronizedDequeFilter":
			return new FilterBenchmark.SynchronizedDequeFilter(10);
		case "SingleSchedulerFilter":
			return new FilterBenchmark.SingleSchedulerFilter(10);
		default:
			throw new IllegalStateException();
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + FilterBenchmark.class.getSimpleName() + ".*").build();
		new Runner(opt).run();
	}

	@Setup
	public void setup() {
		filter = createFilter(filterType);
	}

	@TearDown
	public void tearDown() {
		filter.shutdown();
	}

	@Threads(MAX)
	@Benchmark
	public boolean benchmark() {
		return filter.isSignalAllowed();
	}

	interface Filter {
		boolean isSignalAllowed();

		default void shutdown() {
			// no code
		}
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

	public static class SynchronizedDequeFilter implements Filter {
		private final int n;
		private final long time;
		private final Deque<Long> acquisitions = new ArrayDeque<>();

		public SynchronizedDequeFilter(int n) {
			this(n, TimeUnit.SECONDS);
		}

		public SynchronizedDequeFilter(int n, TimeUnit timeUnit) {
			this.n = n;
			this.time = timeUnit.toNanos(1);
		}

		@Override
		public synchronized boolean isSignalAllowed() {
			long currentTime = System.nanoTime();
			if (acquisitions.size() < n) {
				acquisitions.addLast(currentTime);
				return true;
			}
			if (currentTime - acquisitions.getFirst() >= time) {
				acquisitions.removeFirst();
				acquisitions.addLast(currentTime);
				return true;
			}
			return false;
		}

		@Override
		public void shutdown() {
			acquisitions.clear();
		}
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

	public static class SingleSchedulerFilter implements Filter {
		private final int n;
		private final AtomicInteger count = new AtomicInteger(0);
		private final ScheduledExecutorService scheduler;

		public SingleSchedulerFilter(int n) {
			this(n, TimeUnit.SECONDS);
		}

		public SingleSchedulerFilter(int n, TimeUnit timeUnit) {
			this.n = n;
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(() -> count.set(0), 1, 1, timeUnit);

		}

		@Override
		public boolean isSignalAllowed() {
			return count.getAndUpdate(curr -> curr == n ? curr : curr + 1) < n;
		}

		@Override
		public void shutdown() {
			scheduler.shutdownNow();
		}

	}

}
