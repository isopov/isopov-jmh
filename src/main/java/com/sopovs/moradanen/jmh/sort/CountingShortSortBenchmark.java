package com.sopovs.moradanen.jmh.sort;

import java.util.Arrays;
import java.util.Random;
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
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

//Benchmark                             (size)     (type)  Mode  Cnt     Score    Error  Units
//CountingShortSortBenchmark.baseline       10  presorted  avgt   15     0.015 ±  0.001  us/op
//CountingShortSortBenchmark.baseline       10     random  avgt   15     0.015 ±  0.001  us/op
//CountingShortSortBenchmark.baseline      100  presorted  avgt   15     0.027 ±  0.002  us/op
//CountingShortSortBenchmark.baseline      100     random  avgt   15     0.027 ±  0.001  us/op
//CountingShortSortBenchmark.baseline     1000  presorted  avgt   15     0.259 ±  0.002  us/op
//CountingShortSortBenchmark.baseline     1000     random  avgt   15     0.260 ±  0.004  us/op
//CountingShortSortBenchmark.baseline    10000  presorted  avgt   15     2.609 ±  0.039  us/op
//CountingShortSortBenchmark.baseline    10000     random  avgt   15     2.604 ±  0.021  us/op
//CountingShortSortBenchmark.baseline   100000  presorted  avgt   15    28.723 ±  0.294  us/op
//CountingShortSortBenchmark.baseline   100000     random  avgt   15    28.480 ±  0.411  us/op
//CountingShortSortBenchmark.baseline  1000000  presorted  avgt   15   302.439 ±  4.206  us/op
//CountingShortSortBenchmark.baseline  1000000     random  avgt   15   305.680 ±  3.998  us/op
//CountingShortSortBenchmark.counting       10  presorted  avgt   15    78.221 ±  8.024  us/op
//CountingShortSortBenchmark.counting       10     random  avgt   15    78.427 ±  7.666  us/op
//CountingShortSortBenchmark.counting      100  presorted  avgt   15    82.286 ±  2.762  us/op
//CountingShortSortBenchmark.counting      100     random  avgt   15    82.395 ±  3.443  us/op
//CountingShortSortBenchmark.counting     1000  presorted  avgt   15   104.917 ±  1.690  us/op
//CountingShortSortBenchmark.counting     1000     random  avgt   15   103.616 ±  3.318  us/op
//CountingShortSortBenchmark.counting    10000  presorted  avgt   15   217.209 ±  3.662  us/op
//CountingShortSortBenchmark.counting    10000     random  avgt   15   228.369 ±  3.023  us/op
//CountingShortSortBenchmark.counting   100000  presorted  avgt   15   831.172 ±  9.342  us/op
//CountingShortSortBenchmark.counting   100000     random  avgt   15   993.181 ± 10.776  us/op
//CountingShortSortBenchmark.counting  1000000  presorted  avgt   15  3268.396 ± 22.072  us/op
//CountingShortSortBenchmark.counting  1000000     random  avgt   15  4540.957 ± 42.622  us/op
//CountingShortSortBenchmark.standard       10  presorted  avgt   15     0.024 ±  0.001  us/op
//CountingShortSortBenchmark.standard       10     random  avgt   15     0.074 ±  0.001  us/op
//CountingShortSortBenchmark.standard      100  presorted  avgt   15     0.223 ±  0.005  us/op
//CountingShortSortBenchmark.standard      100     random  avgt   15     1.432 ±  0.032  us/op
//CountingShortSortBenchmark.standard     1000  presorted  avgt   15     0.756 ±  0.009  us/op
//CountingShortSortBenchmark.standard     1000     random  avgt   15    16.805 ±  0.268  us/op
//CountingShortSortBenchmark.standard    10000  presorted  avgt   15   213.864 ±  3.459  us/op
//CountingShortSortBenchmark.standard    10000     random  avgt   15   225.817 ±  2.079  us/op
//CountingShortSortBenchmark.standard   100000  presorted  avgt   15   839.129 ± 55.629  us/op
//CountingShortSortBenchmark.standard   100000     random  avgt   15  1008.507 ± 12.983  us/op
//CountingShortSortBenchmark.standard  1000000  presorted  avgt   15  3237.572 ± 23.793  us/op
//CountingShortSortBenchmark.standard  1000000     random  avgt   15  4493.070 ± 20.381  us/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class CountingShortSortBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + CountingShortSortBenchmark.class.getSimpleName() + ".*")
                .addProfiler(GCProfiler.class)
                // .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    // @Param({ "10", "100", "1000", "10000", "100000", "1000000" })
    @Param({ "100000" })
    public int size;

    // @Param({ "presorted", "random" })
    @Param({ "random" })
    public String type;

    short[] input;

    @Setup
    public void setup() {
        Random r = new Random(228L);
        input = new short[size];
        for (int i = 0; i < input.length; i++) {
            input[i] = nextShort(r);
        }
        if ("presorted".equals(type)) {
            Arrays.sort(input);
        }
    }

    private static short nextShort(Random r) {
        return (short) (r.nextInt(NUM_SHORT_VALUES) + Short.MIN_VALUE);

    }

    @Benchmark
    public short[] baseline() {
        return input.clone();
    }

    @Benchmark
    public short[] standard() {
        short[] clone = input.clone();
        Arrays.sort(clone);
        return clone;
    }

    @Benchmark
    public short[] counting() {
        short[] clone = input.clone();
        countingSort(clone);
        return clone;
    }

    // @Benchmark
    // public short[] myCounting() {
    // short[] clone = input.clone();
    // myCountingSort(clone);
    // return clone;
    // }

    private static final int NUM_SHORT_VALUES = 1 << 16;

    static void countingSort(short[] a) {
        countingSort(a, 0, a.length - 1);
    }

    static void countingSort(short[] a, int left, int right) {
        int[] count = new int[NUM_SHORT_VALUES];

        for (int i = left - 1; ++i <= right; count[a[i] - Short.MIN_VALUE]++) {
            // no code
        }

        for (int i = NUM_SHORT_VALUES, k = right + 1; k > left;) {
            while (count[--i] == 0) {
                // no code
            }
            short value = (short) (i + Short.MIN_VALUE);
            int s = count[i];

            do {
                a[--k] = value;
            } while (--s > 0);
        }
    }

    static void myCountingSort(short[] a) {
        myCountingSort(a, 0, a.length - 1);
    }

    static void myCountingSort(short[] a, int left, int right) {
        int[] count = new int[NUM_SHORT_VALUES];

        for (int i = left; i <= right; i++) {
            count[a[i] - Short.MIN_VALUE]++;
        }

        for (int i = 0; i < NUM_SHORT_VALUES; i++) {
            while (i < NUM_SHORT_VALUES && count[i] == 0) {
                i++;
            }
            if (i >= NUM_SHORT_VALUES) {
                break;
            }
            short value = (short) (i + Short.MIN_VALUE);
            Arrays.fill(a, left, left + count[i], value);
            left += count[i];

        }
    }

}
