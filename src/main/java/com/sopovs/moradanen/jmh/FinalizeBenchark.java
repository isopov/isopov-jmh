package com.sopovs.moradanen.jmh;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.base.FinalizablePhantomReference;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.collect.Sets;

//Benchmark                            Mode  Cnt     Score      Error  Units
//FinalizeBenchark.baseline            avgt   15   141.919 ±   12.554  ns/op
//FinalizeBenchark.finalizable         avgt   15   776.125 ±   75.940  ns/op
//FinalizeBenchark.guardedFinalizable  avgt   15   773.450 ±  109.128  ns/op
//FinalizeBenchark.guavaFinalizable    avgt   15  5754.685 ± 5708.960  ns/op
//FinalizeBenchark.phantomFinalizable  avgt   15   153.219 ±    7.176  ns/op

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class FinalizeBenchark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + FinalizeBenchark.class.getSimpleName() + ".*")
				.build();
		new Runner(opt).run();
	}

	@Benchmark
	public void baseline(Blackhole bh) {
		try (Baseline obj = new Baseline()) {
			bh.consume(obj);
		}
	}

	@Benchmark
	public void finalizable(Blackhole bh) {
		try (Finalizable obj = new Finalizable()) {
			bh.consume(obj);
		}
	}

	@Benchmark
	public void guardedFinalizable(Blackhole bh) {
		try (GuardedFinalizable obj = new GuardedFinalizable()) {
			bh.consume(obj);
		}
	}

	@Benchmark
	public void guavaFinalizable(Blackhole bh) {
		try (GuavaFinalizable obj = new GuavaFinalizable()) {
			bh.consume(obj);
		}
	}

	@Benchmark
	public void phantomFinalizable(Blackhole bh) {
		try (PhantomFinalizable obj = new PhantomFinalizable()) {
			bh.consume(obj);
		}
	}

	public static final class PhantomFinalizable implements AutoCloseable {
		private static final ReferenceQueue<PhantomFinalizable> RFQ = new ReferenceQueue<>();

		private static final Set<Object> SET = ConcurrentHashMap.newKeySet();
		private final Object resource = new Object();

		public PhantomFinalizable() {
			SET.add(resource);
			// TODO should I store this reference somewhere?
			new ResourcePhantomReference(this, RFQ, resource);
			ResourcePhantomReference dead = (ResourcePhantomReference) RFQ.poll();
			while (dead != null) {
				SET.remove(dead.resource);
				dead = (ResourcePhantomReference) RFQ.poll();
			}
		}

		@Override
		public void close() {
			SET.remove(resource);
		}

		private static final class ResourcePhantomReference extends PhantomReference<PhantomFinalizable> {
			public final Object resource;

			public ResourcePhantomReference(PhantomFinalizable referent, ReferenceQueue<? super PhantomFinalizable> q,
					Object resource) {
				super(referent, q);
				this.resource = resource;
			}

		}
	}

	public static final class GuavaFinalizable implements AutoCloseable {
		private static final FinalizableReferenceQueue FRQ = new FinalizableReferenceQueue();
		private static final Set<Reference<?>> REFERENCES = Sets.newConcurrentHashSet();

		private static final Set<Object> SET = ConcurrentHashMap.newKeySet();
		private final Object resource = new Object();

		public GuavaFinalizable() {
			SET.add(resource);
			Reference<?> reference = new FinalizablePhantomReference<GuavaFinalizable>(this, FRQ) {
				public void finalizeReferent() {
					REFERENCES.remove(this);
					SET.remove(resource);
				}
			};
			REFERENCES.add(reference);
		}

		@Override
		public void close() {
			SET.remove(resource);
		}

	}

	public static final class GuardedFinalizable implements AutoCloseable {
		private static final Set<Object> SET = ConcurrentHashMap.newKeySet();
		private final Object resource = new Object();
		private volatile boolean closed = false;

		public GuardedFinalizable() {
			SET.add(resource);
		}

		@Override
		public void close() {
			SET.remove(resource);
			closed = true;
		}

		@Override
		protected void finalize() {
			if (!closed) {
				SET.remove(resource);
			}
		}
	}

	public static final class Finalizable implements AutoCloseable {
		private static final Set<Object> SET = ConcurrentHashMap.newKeySet();
		private final Object resource = new Object();

		public Finalizable() {
			SET.add(resource);
		}

		@Override
		public void close() {
			SET.remove(resource);
		}

		@Override
		protected void finalize() {
			SET.remove(resource);
		}
	}

	public static final class Baseline implements AutoCloseable {
		private static final Set<Object> SET = ConcurrentHashMap.newKeySet();
		private final Object resource = new Object();

		public Baseline() {
			SET.add(resource);
		}

		@Override
		public void close() {
			SET.remove(resource);
		}
	}

}
