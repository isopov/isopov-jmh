package com.sopovs.moradanen.jmh;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

//Benchmark                                    Mode  Samples    Score  Score error  Units
//c.s.m.j.ThrowNullBenchmark.baseline          avgt        3    0.265        0.006  ns/op
//c.s.m.j.ThrowNullBenchmark.realNPE           avgt        3    2.445        0.180  ns/op
//c.s.m.j.ThrowNullBenchmark.requireNotNull    avgt        3  916.473      246.440  ns/op
//c.s.m.j.ThrowNullBenchmark.throwNewNPE       avgt        3  884.765      137.583  ns/op
//c.s.m.j.ThrowNullBenchmark.throwNull         avgt        3    2.368        0.126  ns/op
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ThrowNullBenchmark {

    @Benchmark
    public void baseline() {
    }

    @Benchmark
    public void realNPE(Blackhole bh) {
        try {
            Object o = null;
            o.hashCode();
        } catch (NullPointerException npe) {
            bh.consume(npe);
        }
    }

    @Benchmark
    public void throwNull(Blackhole bh) {
        try {
            throw null;
        } catch (NullPointerException npe) {
            bh.consume(npe);
        }
    }

    @Benchmark
    public void requireNotNull(Blackhole bh) {
        try {
            Objects.requireNonNull(null);
        } catch (NullPointerException npe) {
            bh.consume(npe);
        }
    }

    @Benchmark
    public void throwNewNPE(Blackhole bh) {
        try {
            throw new NullPointerException();
        } catch (NullPointerException npe) {
            bh.consume(npe);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ThrowNullBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }
}
