package com.sopovs.moradanen.jmh;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.sopovs.moradanen.jmh.BenderMoney.Room;

public class BenderMoneyBenchmark {
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public int four() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_four.txt"))) {
            return BenderMoney.doWork(in);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public Room fourRoom() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_four.txt"))) {
            return BenderMoney.getZeroRoom(in);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public Room fiveRoom() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_five.txt"))) {
            return BenderMoney.getZeroRoom(in);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + BenderMoneyBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(3)
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
