package com.sopovs.moradanen.jmh;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.collect.Maps;

@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MapWithPerfBenchmark {

    @Param({ "1", "10", "100", "1000" })
    public int size;

    private Map<Integer, String> map;

    @Setup
    public void setup() {
        map = Maps.newHashMapWithExpectedSize(size);
        for (int i = 0; i < size; i++) {
            map.put(Integer.valueOf(i), String.valueOf(i));
        }
    }

    @Benchmark
    public void entrySet(Blackhole bh) {
        for (Entry<Integer, String> entry : map.entrySet()) {
            bh.consume(entry.getKey());
            bh.consume(entry.getValue());
        }
    }

    @Benchmark
    public void keySet(Blackhole bh) {
        for (Integer i : map.keySet()) {
            bh.consume(i);
            bh.consume(map.get(i));
        }
    }

    public static void main(String[] args) throws RunnerException {
        System.setProperty("jmh.perfnorm.events",
                "cycles,instructions,branches,branch-misses,L1-dcache-loads,L1-dcache-load-misses");
        Options opt = new OptionsBuilder()
                .include(MapWithPerfBenchmark.class.getSimpleName())
                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

}
