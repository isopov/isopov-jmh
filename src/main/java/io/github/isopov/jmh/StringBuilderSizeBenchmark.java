package io.github.isopov.jmh;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by isopov on 2/24/14.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StringBuilderSizeBenchmark {


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public String predefinedSize() {
        StringBuilder builder = new StringBuilder(1000);
        for (int i = 0; i < 1000; i++) {
            builder.append("*");
        }
        return builder.toString();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public String expandingSize() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            builder.append("*");
        }
        return builder.toString();
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + StringBuilderSizeBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }


}
