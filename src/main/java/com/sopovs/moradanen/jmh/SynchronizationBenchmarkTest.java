package com.sopovs.moradanen.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SynchronizationBenchmarkTest {

    @State(Scope.Benchmark)
    public static class SharedSynchronized {
        public synchronized void testSynchronized() throws InterruptedException {
            Thread.sleep(50);
        }
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(2)
    public synchronized void testSyncrhonized() throws InterruptedException {
        Thread.sleep(50);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(2)
    public synchronized void testSharedSyncrhonized(SharedSynchronized shared) throws InterruptedException {
        shared.testSynchronized();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + SynchronizationBenchmarkTest.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }

}