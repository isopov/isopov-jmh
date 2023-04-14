package io.github.isopov.jmh;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.util.concurrent.Striped;

public class StrippedBenchmark {

    private static final int NUMBER_OF_LOCKS = 10;
    private static final int NUMBER_OF_THREADS = 100;
    private static final int NUMBER_OF_TASKS = NUMBER_OF_THREADS * 1000;
    private static final long CPU_CONSUMPTION = 1000L;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + StrippedBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void striped(Blackhole bh) throws Exception {
        Striped<Lock> striped = Striped.lock(NUMBER_OF_LOCKS);
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 0; i < NUMBER_OF_TASKS; i++) {
            pool.execute(new StripedRunnable(bh, i % NUMBER_OF_THREADS, striped));
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Benchmark
    public void map(Blackhole bh) throws Exception {
        ConcurrentMap<Integer, Lock> locks = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 0; i < NUMBER_OF_TASKS; i++) {
            pool.execute(new MapRunnable(bh, i % NUMBER_OF_THREADS, locks));
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Benchmark
    public void mapWithDefaultComputeIfAbsent(Blackhole bh) throws Exception {
        ConcurrentMap<Integer, Lock> locks = new MyConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 0; i < NUMBER_OF_TASKS; i++) {
            pool.execute(new MapRunnable(bh, i % NUMBER_OF_THREADS, locks));
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }

    private static void doWork(Lock lock, Blackhole bh, Integer resourceId) {
        lock.lock();
        try {
            bh.consume(resourceId);
            Blackhole.consumeCPU(CPU_CONSUMPTION);
        } finally {
            lock.unlock();
        }
    }

    private static class MapRunnable implements Runnable {

        private final Blackhole bh;
        private final Integer resourceId;
        private final ConcurrentMap<Integer, Lock> locks;

        public MapRunnable(Blackhole bh, Integer resourceId, ConcurrentMap<Integer, Lock> locks) {
            this.bh = bh;
            this.resourceId = resourceId;
            this.locks = locks;
        }

        @Override
        public void run() {
            doWork(locks.computeIfAbsent(resourceId, i -> new ReentrantLock()), bh, resourceId);
        }
    }

    private static class StripedRunnable implements Runnable {
        private final Blackhole bh;
        private final Integer resourceId;
        private final Striped<Lock> striped;

        public StripedRunnable(Blackhole bh, Integer resourceId, Striped<Lock> striped) {
            this.bh = bh;
            this.resourceId = resourceId;
            this.striped = striped;
        }

        @Override
        public void run() {
            doWork(striped.get(resourceId), bh, resourceId);
        }
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
