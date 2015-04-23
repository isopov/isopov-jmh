package com.sopovs.moradanen.jmh;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class ConcurrentMapBenchmark {

    private static int NUMBER_OF_THREADS = 100;
    private static int MAP_SIZE = 10000;
    private static int NUMBER_OF_TASKS = MAP_SIZE * 10;

    List<Integer> tasks = IntStream.range(0, NUMBER_OF_TASKS)
            .map(i -> i % MAP_SIZE)
            .sorted()
            .boxed()
            .collect(Collectors.toList());

    @Param({ "0", "256", "4096", "8192", "16384", "32768", "65536" })
    public long cpu;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(".*" + ConcurrentMapBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void regularImpl(Blackhole bh) throws Exception {
        ConcurrentMap<Integer, Integer> map = new ConcurrentHashMap<>();
        doWork(bh, map);
    }

    @Benchmark
    public void defaultImpl(Blackhole bh) throws Exception {
        ConcurrentMap<Integer, Integer> map = new MyConcurrentHashMap<>();
        doWork(bh, map);
    }

    private void doWork(Blackhole bh, ConcurrentMap<Integer, Integer> map) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 0; i < NUMBER_OF_TASKS; i++) {
            final int j = i;
            pool.execute(() -> {
                bh.consume(map.computeIfAbsent(tasks.get(j), val -> {
                    Blackhole.consumeCPU(cpu);
                    return val;
                }));
            });
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }

    public static class MyConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
        private static final long serialVersionUID = -496190000364949099L;

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            // Implementation taken from ConcurrentMap interface
            Objects.requireNonNull(mappingFunction);
            V v, newValue;
            return ((v = get(key)) == null &&
                    (newValue = mappingFunction.apply(key)) != null && (v = putIfAbsent(key, newValue)) == null) ? newValue
                    : v;
        }
    }

}
