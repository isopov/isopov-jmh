package io.github.isopov.jmh;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class RollerCoasterBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public long five() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/rollercoaster_five.txt"))) {
            return RollerCoaster.doWork(in);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public long six() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/rollercoaster_six.txt"))) {
            return RollerCoaster.doWork(in);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + RollerCoasterBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(3)
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
