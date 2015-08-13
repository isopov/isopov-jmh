package com.sopovs.moradanen.jmh;

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
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class AddExactBenchmark {

    private int x = Integer.MAX_VALUE / 3, y = Integer.MAX_VALUE / 4;

    @Benchmark
    public int mathAddExact() {
        return Math.addExact(x, y);
    }

    @Benchmark
    public int addWithLongs() {
        return addWithLongs(x, y);
    }

    private static int addWithLongs(int x, int y) {
        long xl = x;
        long yl = y;
        long result = xl + yl;

        if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return (int) result;

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + AddExactBenchmark.class.getSimpleName() + ".*")
                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
