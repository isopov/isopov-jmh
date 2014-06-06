package com.sopovs.moradanen.jmh;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class AtomicSynchronizationBenchmark {


	@Param({"1", "2", "4", "8", "16", "32", "64"})
	public int threads;

	private static final int ITERATIONS = 101;
	private static final long ITERATION_SUM = 10000L;


	@GenerateMicroBenchmark
	public long longAdder() throws Exception {
		final LongAdder longAdder = new LongAdder();
		Runnable counter = new Runnable() {
			@Override
			public void run() {
				for (long i = 0L; i <= ITERATION_SUM; i++) {
					longAdder.add(i);
				}
			}
		};
		execute(counter);
		return longAdder.sum();
	}


	@GenerateMicroBenchmark
	public long computeSynchronized() throws Exception {
		final long[] sum = {0};
		Runnable counter = new Runnable() {
			@Override
			public void run() {
				for (long i = 0L; i <= ITERATION_SUM; i++) {
					synchronized (sum) {
						sum[0] += i;
					}
				}
			}
		};
		execute(counter);
		return sum[0];
	}

	@GenerateMicroBenchmark
	public long atomic() throws Exception {
		final AtomicLong sum = new AtomicLong();
		Runnable counter = new Runnable() {
			@Override
			public void run() {
				for (long i = 0L; i <= ITERATION_SUM; i++) {
					sum.addAndGet(i);
				}
			}
		};
		execute(counter);
		return sum.get();
	}


	private void execute(Runnable counter) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < ITERATIONS; i++) {
			executorService.execute(counter);
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		if(executorService.isTerminated()){
			//We cannot measure next methods - because this one will continue execution and will interfere with them
			System.exit(1);
		}
	}


//	Benchmark                                                    (threads)   Mode   Samples        Score  Score error    Units
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                        1   avgt         5        5.331        0.183    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                        2   avgt         5       26.479        1.335    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                        4   avgt         5       27.399        1.777    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                        8   avgt         5       24.298        1.306    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                       16   avgt         5       24.361        1.930    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                       32   avgt         5       24.514        0.496    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.atomic                       64   avgt         5       24.964        2.052    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized           1   avgt         5       20.496        0.587    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized           2   avgt         5       47.982        5.642    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized           4   avgt         5       46.629        7.271    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized           8   avgt         5       48.691        8.895    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized          16   avgt         5       58.486       33.764    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized          32   avgt         5       60.109       26.486    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.computeSynchronized          64   avgt         5       52.833        6.765    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                     1   avgt         5        8.849        0.103    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                     2   avgt         5        4.591        0.081    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                     4   avgt         5        2.386        0.031    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                     8   avgt         5        2.079        0.098    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                    16   avgt         5        2.336        0.196    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                    32   avgt         5        2.828        0.163    ms/op
//	c.s.m.j.AtomicSynchronizationBenchmark.longAdder                    64   avgt         5        4.054        0.286    ms/op
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + AtomicSynchronizationBenchmark.class.getSimpleName() + ".*")
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
