package com.sopovs.moradanen.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class DistinctToSetBenchmark {

    private List<String> input;

    @Setup
    public void setup() {
        input = new ArrayList<>(100 * 100);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                input.add("Value + " + i);
            }
        }
    }

    @Benchmark
    public Set<String> toSet() {
        return input.stream().collect(Collectors.toSet());
    }

    @Benchmark
    public Set<String> toDistinctSet() {
        return input.stream().distinct().collect(Collectors.toSet());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DistinctToSetBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
