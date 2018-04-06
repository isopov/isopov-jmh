package com.sopovs.moradanen.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

//Benchmark                                               Mode  Cnt     Score     Error   Units
//NewIntegerBenchmark.returnObject                        avgt    5     8.792 ±   0.438   ns/op
//NewIntegerBenchmark.returnObject:·gc.alloc.rate.norm    avgt    5    16.000 ±   0.001    B/op
//NewIntegerBenchmark.returnPrimitive                     avgt    5     6.261 ±   0.321   ns/op
//NewIntegerBenchmark.returnPrimitive:·gc.alloc.rate.norm avgt    5    ≈ 10⁻⁶              B/op
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NewIntegerBenchmark {

    @Benchmark
    public int returnPrimitive() {
        return new Integer(ThreadLocalRandom.current().nextInt());
    }

    @Benchmark
    public Integer returnObject() {
        return new Integer(ThreadLocalRandom.current().nextInt());
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NewIntegerBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
