package io.github.isopov.jmh;

import io.undertow.server.handlers.cache.LRUCache;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
public class UndertowLRUCacheBenchmark {

    @Benchmark
    public void undertow(Blackhole bh) {
        LRUCache<Integer, String> cache = new LRUCache<Integer, String>(100, 100000);

        for (int i = 0; i < 100; i++) {
            int start = (i % 7) * 10;
            for (int j = start; j < start + 100; j++) {
                String val = cache.get(Integer.valueOf(j));
                if (val == null) {
                    val = String.valueOf(j);
                    cache.add(Integer.valueOf(j), val);
                }
                bh.consume(val);
            }
        }
    }

    @Benchmark
    public void guava(Blackhole bh) {
        Cache<Integer, String> cache = CacheBuilder.newBuilder().maximumSize(100L).build();

        for (int i = 0; i < 100; i++) {
            int start = (i % 7) * 10;
            for (int j = start; j < start + 100; j++) {
                String val = cache.getIfPresent(Integer.valueOf(j));
                if (val == null) {
                    val = String.valueOf(j);
                    cache.put(Integer.valueOf(j), val);
                }
                bh.consume(val);
            }
        }

    }

    // UndertowLRUCacheBenchmark.guava avgt 5 0.911 ± 0.023 ms/op
    // UndertowLRUCacheBenchmark.undertow avgt 5 0.726 ± 0.016 ms/op
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UndertowLRUCacheBenchmark.class.getSimpleName())
                .addProfiler(LinuxPerfNormProfiler.class)
                .build();
        new Runner(opt).run();
    }

}
