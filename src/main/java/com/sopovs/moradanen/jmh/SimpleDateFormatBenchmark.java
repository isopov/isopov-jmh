package com.sopovs.moradanen.jmh;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class SimpleDateFormatBenchmark {

    @State(Scope.Benchmark)
    public static class SharedSimpleDateFormat {
        SimpleDateFormat format = new SimpleDateFormat();
        Date date = new Date();

        public synchronized String format(SimpleDateFormat format, Date date) {
            return format.format(date);
        }
    }

    @State(Scope.Thread)
    public static class DateState {
        Date date = new Date();
    }

    @State(Scope.Thread)
    public static class SeparateSimpleDateFormat {
        SimpleDateFormat format = new SimpleDateFormat();
        Date date = new Date();
    }

    // Tests for synchronized SimpleDateFormat
    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(1)
    public synchronized String format_syncrhonized_1(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(2)
    public synchronized String format_syncrhonized_2(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(3)
    public synchronized String format_syncrhonized_3(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(4)
    public synchronized String format_syncrhonized_4(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(5)
    public synchronized String format_syncrhonized_5(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(6)
    public synchronized String format_syncrhonized_6(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(7)
    public synchronized String format_syncrhonized_7(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(8)
    public synchronized String format_syncrhonized_8(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(9)
    public synchronized String format_syncrhonized_9(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(10)
    public synchronized String format_syncrhonized_10(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(11)
    public synchronized String format_syncrhonized_11(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(12)
    public synchronized String format_syncrhonized_12(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(13)
    public synchronized String format_syncrhonized_13(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(14)
    public synchronized String format_syncrhonized_14(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(15)
    public synchronized String format_syncrhonized_15(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(16)
    public synchronized String format_syncrhonized_16(SharedSimpleDateFormat sharedState) {
        return sharedState.format(sharedState.format, sharedState.date);
    }

    // Tests for new SimpleDateFormat every invocation
    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(1)
    public String format_per_invocation_1(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(2)
    public String format_per_invocation_2(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(3)
    public String format_per_invocation_3(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(4)
    public String format_per_invocation_4(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(5)
    public String format_per_invocation_5(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(6)
    public String format_per_invocation_6(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(7)
    public String format_per_invocation_7(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(8)
    public String format_per_invocation_8(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(9)
    public String format_per_invocation_9(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(10)
    public String format_per_invocation_10(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(11)
    public String format_per_invocation_11(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(12)
    public String format_per_invocation_12(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(13)
    public String format_per_invocation_13(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(14)
    public String format_per_invocation_14(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(15)
    public String format_per_invocation_15(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(16)
    public String format_per_invocation_16(DateState dateState) {
        return new SimpleDateFormat().format(dateState.date);
    }

    // ThreadLocal SimpleDateFormat
    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(1)
    public String threadLocal_format_1(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(2)
    public String threadLocal_format_2(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(3)
    public String threadLocal_format_3(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(4)
    public String threadLocal_format_4(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(5)
    public String threadLocal_format_5(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(6)
    public String threadLocal_format_6(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(7)
    public String threadLocal_format_7(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(8)
    public String threadLocal_format_8(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(9)
    public String threadLocal_format_9(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(10)
    public String threadLocal_format_10(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(11)
    public String threadLocal_format_11(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(12)
    public String threadLocal_format_12(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(13)
    public String threadLocal_format_13(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(14)
    public String threadLocal_format_14(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(15)
    public String threadLocal_format_15(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    @GenerateMicroBenchmark()
    @BenchmarkMode(Mode.AverageTime)
    @Threads(16)
    public String threadLocal_format_16(SeparateSimpleDateFormat separateFormatState) {
        return separateFormatState.format.format(separateFormatState.date);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + SimpleDateFormatBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }

}