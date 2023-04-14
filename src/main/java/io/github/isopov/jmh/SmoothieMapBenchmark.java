package io.github.isopov.jmh;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import net.openhft.smoothie.SmoothieMap;

//Benchmark                                        Mode  Cnt  Score   Error  Units
//SmoothieMapBenchmark.entrySetForeachHashMap      avgt   15  3.456 ± 0.106  ms/op
//SmoothieMapBenchmark.entrySetForeachSmoothieMap  avgt   15  1.470 ± 0.094  ms/op
//SmoothieMapBenchmark.foreachHashMap              avgt   15  3.391 ± 0.075  ms/op
//SmoothieMapBenchmark.foreachSmoothieMap          avgt   15  2.416 ± 0.066  ms/op
//SmoothieMapBenchmark.iteratorHashMap             avgt   15  3.760 ± 0.126  ms/op
//SmoothieMapBenchmark.iteratorSmoothieMap         avgt   15  1.349 ± 0.068  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class SmoothieMapBenchmark {

    private Map<Integer, String> smoothieMap = new SmoothieMap<>();
    private Map<Integer, String> hashMap = new HashMap<>();

    @Setup
    public void setup() {
        new Random(228L).ints().distinct().limit(100000).forEach(i -> {
            smoothieMap.put(Integer.valueOf(i), String.valueOf(i));
            hashMap.put(Integer.valueOf(i), String.valueOf(i));
        });
        // Dirty hack as for me, but harmless
        System.gc();
        System.gc();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + SmoothieMapBenchmark.class.getSimpleName() + ".*")
                // .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void iteratorHashMap(Blackhole h) {
        for (Entry<Integer, String> entry : hashMap.entrySet()) {
            h.consume(entry.getKey());
            h.consume(entry.getValue());
        }
    }

    @Benchmark
    public void entrySetForeachHashMap(Blackhole h) {
        hashMap.entrySet().forEach(e -> {
            h.consume(e.getKey());
            h.consume(e.getValue());
        });
    }

    @Benchmark
    public void entrySetForeachSmoothieMap(Blackhole h) {
        smoothieMap.entrySet().forEach(e -> {
            h.consume(e.getKey());
            h.consume(e.getValue());
        });
    }

    @Benchmark
    public void iteratorSmoothieMap(Blackhole h) {
        for (Entry<Integer, String> entry : smoothieMap.entrySet()) {
            h.consume(entry.getKey());
            h.consume(entry.getValue());
        }
    }

    @Benchmark
    public void foreachSmoothieMap(Blackhole h) {
        smoothieMap.forEach((k, v) -> {
            h.consume(k);
            h.consume(v);
        });
    }

    @Benchmark
    public void foreachHashMap(Blackhole h) {
        hashMap.forEach((k, v) -> {
            h.consume(k);
            h.consume(v);
        });
    }
}
