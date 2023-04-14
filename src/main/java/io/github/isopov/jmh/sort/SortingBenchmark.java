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
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

//Benchmark                        Mode  Cnt   Score    Error  Units
//SortingBenchmark.baseline        avgt   15   0.005 ±  0.001  ms/op
//SortingBenchmark.bubble          avgt   15  61.567 ±  0.669  ms/op
//SortingBenchmark.heap            avgt   15   1.133 ±  0.014  ms/op
//SortingBenchmark.insertion       avgt   15   5.037 ±  0.433  ms/op
//SortingBenchmark.insertionDummy  avgt   15  15.551 ±  0.107  ms/op
//SortingBenchmark.standard        avgt   15   0.726 ±  0.004  ms/op
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class SortingBenchmark {

    private int[] input;

    @Benchmark
    public int[] baseline() {
        return input.clone();
    }

    @Benchmark
    public int[] standard() {
        int[] clone = input.clone();
        Arrays.sort(clone);
        return clone;
    }

    @Benchmark
    public int[] heap() {
        int[] clone = input.clone();
        heap(clone);
        return clone;
    }

    @Benchmark
    public int[] bubble() {
        int[] clone = input.clone();
        bubble(clone);
        return clone;
    }

    @Benchmark
    public int[] insertionDummy() {
        int[] clone = input.clone();
        insertionDummy(clone);
        return clone;
    }

    @Benchmark
    public int[] insertion() {
        int[] clone = input.clone();
        insertion(clone);
        return clone;
    }

    @Setup
    public void setup() {
        Random r = new Random(228L);
        input = new int[10000];
        for (int i = 0; i < input.length; i++) {
            input[i] = r.nextInt();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + SortingBenchmark.class.getSimpleName() + ".*")
                // .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    static void insertionDummy(int[] input) {

        for (int j = 1; j < input.length; j++) // Start with 1 (not 0)
        {
            int key = input[j];
            int i;
            // Smaller values are moving up
            for (i = j - 1; (i >= 0) && (input[i] < key); i--) {
                input[i + 1] = input[i];
            }
            input[i + 1] = key; // Put the key in its proper location
        }
    }

    static void insertion(int[] input) {
        for (int j = 1; j < input.length; j++) {
            int key = input[j];
            int i = Arrays.binarySearch(input, 0, j, key);
            if (i >= 0) {
                System.arraycopy(input, i, input, i + 1, j - i);
                input[i] = key;
            } else {
                i = -i - 1;
                System.arraycopy(input, i, input, i + 1, j - i);
                input[i] = key;
            }
        }
    }

    static void bubble(int[] input) {
        for (int i = 0; i < input.length - 1; i++)
            for (int j = 0; j < input.length - i - 1; j++)
                if (input[j] > input[j + 1]) {
                    int temp = input[i]; // change for elements
                    input[i] = input[i + 1];
                    input[i + 1] = temp;
                }
    }

    static void heap(int[] input) {
        int i;
        int temp;

        for (i = input.length / 2 - 1; i >= 0; i--) {
            shiftDown(input, i, input.length);
        }

        for (i = input.length - 1; i >= 1; i--) {
            temp = input[0];
            input[0] = input[i];
            input[i] = temp;
            shiftDown(input, 0, i);
        }
    }

    private static void shiftDown(int[] input, int i, int j) {
        boolean done = false;
        int maxChild;
        int temp;

        while ((i * 2 + 1 < j) && (!done)) {
            if (i * 2 + 1 == j - 1)
                maxChild = i * 2 + 1;
            else if (input[i * 2 + 1] > input[i * 2 + 2])
                maxChild = i * 2 + 1;
            else
                maxChild = i * 2 + 2;

            if (input[i] < input[maxChild]) {
                temp = input[i];
                input[i] = input[maxChild];
                input[maxChild] = temp;
                i = maxChild;
            } else {
                done = true;
            }
        }
    }

}
