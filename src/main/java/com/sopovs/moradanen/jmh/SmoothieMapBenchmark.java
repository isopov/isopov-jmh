package com.sopovs.moradanen.jmh;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

//Benchmark                                 Mode  Cnt  Score   Error  Units
//SmoothieMapBenchmark.foreachSmoothieMap   avgt   15  3.059 ± 0.074  ms/op
//SmoothieMapBenchmark.iteratorHashMap      avgt   15  1.965 ± 0.189  ms/op
//SmoothieMapBenchmark.iteratorSmoothieMap  avgt   15  1.411 ± 0.128  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class SmoothieMapBenchmark {

    private SmoothieMap<Integer, String> smoothieMap = new SmoothieMap<>();
    private Map<Integer, String> hashMap = new HashMap<>();

    @Setup
    public void setup() {
        for (int i = 0; i < 100000; i++) {
            smoothieMap.put(Integer.valueOf(i), String.valueOf(i));
            hashMap.put(Integer.valueOf(i), String.valueOf(i));
        }
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
}
