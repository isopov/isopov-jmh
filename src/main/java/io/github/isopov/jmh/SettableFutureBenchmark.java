package io.github.isopov.jmh;


import com.google.common.base.Throwables;
import com.google.common.util.concurrent.SettableFuture;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author isopov
 * @since 19.06.2014
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SettableFutureBenchmark {


	@Param({"1", "2", "4", "8", "16", "32", "64"})
	public int threads;

	private static final int ITERATIONS = 101;


	@Benchmark
	public void spring() throws InterruptedException, ExecutionException {
		SettableListenableFuture<Object> settableFuture = new SettableListenableFuture<>();
		ExecutorService executorService = execute(new FutureWaiter(settableFuture));
		settableFuture.set(new Object());
		await(executorService);
	}


	@Benchmark
	public void guava() throws InterruptedException, ExecutionException {
		SettableFuture<Object> settableFuture = SettableFuture.create();
		ExecutorService executorService = execute(new FutureWaiter(settableFuture));
		settableFuture.set(new Object());
		await(executorService);
	}

	private ExecutorService execute(Runnable waiter) {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < ITERATIONS; i++) {
			executorService.execute(waiter);
		}
		return executorService;
	}

	private void await(ExecutorService executorService) throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		if (!executorService.isTerminated()) {
			//We cannot measure next methods - because this one will continue execution and will interfere with them
			System.exit(1);
		}
	}

	private static class FutureWaiter implements Runnable {
		private final Future<Object> settableFuture;

		private FutureWaiter(Future<Object> settableFuture) {
			this.settableFuture = settableFuture;
		}

		@Override
		public void run() {
			try {
				settableFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				Throwables.propagate(e);
			}
		}

	}


	//	Benchmark                                (threads)   Mode   Samples        Score  Score error    Units
	// 	c.s.m.j.SettableFutureBenchmark.guava            1   avgt        15       56.893       10.191    us/op
	//	c.s.m.j.SettableFutureBenchmark.guava            2   avgt        15      103.997       14.948    us/op
	//	c.s.m.j.SettableFutureBenchmark.guava            4   avgt        15      201.399       80.005    us/op
	//	c.s.m.j.SettableFutureBenchmark.guava            8   avgt        15      351.546       50.324    us/op
	//	c.s.m.j.SettableFutureBenchmark.guava           16   avgt        15      899.167      408.489    us/op
	//	c.s.m.j.SettableFutureBenchmark.guava           32   avgt        15     1388.645      592.096    us/op
	//	c.s.m.j.SettableFutureBenchmark.guava           64   avgt        15     2408.513      583.869    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring           1   avgt        15       64.606       21.150    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring           2   avgt        15      116.302       21.473    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring           4   avgt        15      151.943       13.653    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring           8   avgt        15      371.170      139.444    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring          16   avgt        15      725.176      145.712    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring          32   avgt        15     1306.490      351.653    us/op
	//	c.s.m.j.SettableFutureBenchmark.spring          64   avgt        15     2421.996      215.935    us/op
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + SettableFutureBenchmark.class.getSimpleName() + ".*")
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(3)
				.build();

		new Runner(opt).run();
	}
}
