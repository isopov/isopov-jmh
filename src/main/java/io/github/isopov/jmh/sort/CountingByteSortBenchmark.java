package io.github.isopov.jmh.sort;

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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

//Benchmark                                    (size)     (type)  Mode  Cnt     Score    Error  Units
//CountingByteSortBenchmark.baseline               10  presorted  avgt   15     0.014 ±  0.001  us/op
//CountingByteSortBenchmark.baseline               10     random  avgt   15     0.014 ±  0.001  us/op
//CountingByteSortBenchmark.baseline              100  presorted  avgt   15     0.019 ±  0.001  us/op
//CountingByteSortBenchmark.baseline              100     random  avgt   15     0.019 ±  0.001  us/op
//CountingByteSortBenchmark.baseline             1000  presorted  avgt   15     0.130 ±  0.001  us/op
//CountingByteSortBenchmark.baseline             1000     random  avgt   15     0.131 ±  0.001  us/op
//CountingByteSortBenchmark.baseline            10000  presorted  avgt   15     1.244 ±  0.027  us/op
//CountingByteSortBenchmark.baseline            10000     random  avgt   15     1.251 ±  0.012  us/op
//CountingByteSortBenchmark.baseline           100000  presorted  avgt   15    13.272 ±  0.121  us/op
//CountingByteSortBenchmark.baseline           100000     random  avgt   15    13.291 ±  0.141  us/op
//CountingByteSortBenchmark.baseline          1000000  presorted  avgt   15   152.017 ± 18.211  us/op
//CountingByteSortBenchmark.baseline          1000000     random  avgt   15   152.384 ± 20.279  us/op
//CountingByteSortBenchmark.messyCounting          10  presorted  avgt   15     0.367 ±  0.008  us/op
//CountingByteSortBenchmark.messyCounting          10     random  avgt   15     0.367 ±  0.005  us/op
//CountingByteSortBenchmark.messyCounting         100  presorted  avgt   15     0.768 ±  0.024  us/op
//CountingByteSortBenchmark.messyCounting         100     random  avgt   15     0.731 ±  0.018  us/op
//CountingByteSortBenchmark.messyCounting        1000  presorted  avgt   15     2.944 ±  0.117  us/op
//CountingByteSortBenchmark.messyCounting        1000     random  avgt   15     2.839 ±  0.118  us/op
//CountingByteSortBenchmark.messyCounting       10000  presorted  avgt   15    26.138 ±  0.400  us/op
//CountingByteSortBenchmark.messyCounting       10000     random  avgt   15    17.930 ±  0.383  us/op
//CountingByteSortBenchmark.messyCounting      100000  presorted  avgt   15   334.577 ±  9.515  us/op
//CountingByteSortBenchmark.messyCounting      100000     random  avgt   15   169.153 ±  1.864  us/op
//CountingByteSortBenchmark.messyCounting     1000000  presorted  avgt   15  3437.854 ± 31.011  us/op
//CountingByteSortBenchmark.messyCounting     1000000     random  avgt   15  1786.129 ± 56.413  us/op
//CountingByteSortBenchmark.readableCounting       10  presorted  avgt   15     0.348 ±  0.006  us/op
//CountingByteSortBenchmark.readableCounting       10     random  avgt   15     0.353 ±  0.009  us/op
//CountingByteSortBenchmark.readableCounting      100  presorted  avgt   15     1.284 ±  0.028  us/op
//CountingByteSortBenchmark.readableCounting      100     random  avgt   15     1.304 ±  0.025  us/op
//CountingByteSortBenchmark.readableCounting     1000  presorted  avgt   15     3.184 ±  0.091  us/op
//CountingByteSortBenchmark.readableCounting     1000     random  avgt   15     3.088 ±  0.068  us/op
//CountingByteSortBenchmark.readableCounting    10000  presorted  avgt   15    20.589 ±  0.262  us/op
//CountingByteSortBenchmark.readableCounting    10000     random  avgt   15    12.853 ±  0.210  us/op
//CountingByteSortBenchmark.readableCounting   100000  presorted  avgt   15   255.774 ±  1.763  us/op
//CountingByteSortBenchmark.readableCounting   100000     random  avgt   15   106.445 ±  3.752  us/op
//CountingByteSortBenchmark.readableCounting  1000000  presorted  avgt   15  2753.751 ± 13.697  us/op
//CountingByteSortBenchmark.readableCounting  1000000     random  avgt   15  1162.660 ± 60.701  us/op
//CountingByteSortBenchmark.standard               10  presorted  avgt   15     0.022 ±  0.001  us/op
//CountingByteSortBenchmark.standard               10     random  avgt   15     0.075 ±  0.002  us/op
//CountingByteSortBenchmark.standard              100  presorted  avgt   15     0.756 ±  0.013  us/op
//CountingByteSortBenchmark.standard              100     random  avgt   15     0.755 ±  0.029  us/op
//CountingByteSortBenchmark.standard             1000  presorted  avgt   15     2.967 ±  0.051  us/op
//CountingByteSortBenchmark.standard             1000     random  avgt   15     2.814 ±  0.070  us/op
//CountingByteSortBenchmark.standard            10000  presorted  avgt   15    25.986 ±  0.409  us/op
//CountingByteSortBenchmark.standard            10000     random  avgt   15    17.845 ±  0.308  us/op
//CountingByteSortBenchmark.standard           100000  presorted  avgt   15   335.359 ±  3.634  us/op
//CountingByteSortBenchmark.standard           100000     random  avgt   15   172.181 ±  4.883  us/op
//CountingByteSortBenchmark.standard          1000000  presorted  avgt   15  3460.273 ± 32.755  us/op
//CountingByteSortBenchmark.standard          1000000     random  avgt   15  1753.658 ± 37.842  us/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class CountingByteSortBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + CountingByteSortBenchmark.class.getSimpleName() + ".*")
                // .addProfiler(GCProfiler.class)
                // .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    // @Param({ "10", "100", "1000", "10000", "100000", "1000000" })
    @Param({ "10000", "100000", "1000000" })
    public int size;

    @Param({ "presorted", "random" })
    // @Param({ "random" })
    public String type;

    byte[] input;

    @Setup
    public void setup() {
        Random r = new Random(228L);
        input = new byte[size];
        r.nextBytes(input);
        if ("presorted".equals(type)) {
            Arrays.sort(input);
        }
    }

    // @Benchmark
    // public byte[] baseline() {
    // return input.clone();
    // }

    @Benchmark
    public byte[] standard() {
        byte[] clone = input.clone();
        Arrays.sort(clone);
        return clone;
    }

    // @Benchmark
    // public byte[] messyCounting() {
    // byte[] clone = input.clone();
    // messyCountingSort(clone);
    // return clone;
    // }

    @Benchmark
    public byte[] readableCounting() {
        byte[] clone = input.clone();
        readableCountingSort(clone);
        return clone;
    }

    private static final int NUM_BYTE_VALUES = 1 << 8;

    static void messyCountingSort(byte[] a) {
        messyCountingSort(a, 0, a.length - 1);
    }

    static void messyCountingSort(byte[] a, int left, int right) {
        int[] count = new int[NUM_BYTE_VALUES];

        for (int i = left - 1; ++i <= right; count[a[i] - Byte.MIN_VALUE]++) {
            // no code
        }

        for (int i = NUM_BYTE_VALUES, k = right + 1; k > left;) {
            while (count[--i] == 0) {
                // no code
            }
            byte value = (byte) (i + Byte.MIN_VALUE);
            int s = count[i];

            do {
                a[--k] = value;
            } while (--s > 0);
        }
    }

    static void readableCountingSort(byte[] a) {
        readableCountingSort(a, 0, a.length - 1);
    }

    static void readableCountingSort(byte[] a, int left, int right) {
        int[] count = new int[NUM_BYTE_VALUES];

        for (int i = left; i <= right; i++) {
            count[a[i] - Byte.MIN_VALUE]++;
        }

        for (int i = 0; i < NUM_BYTE_VALUES; i++) {
            while (i < NUM_BYTE_VALUES && count[i] == 0) {
                i++;
            }
            if (i >= NUM_BYTE_VALUES) {
                break;
            }
            byte value = (byte) (i + Byte.MIN_VALUE);
            Arrays.fill(a, left, left + count[i], value);
            left += count[i];
        }
    }

}
