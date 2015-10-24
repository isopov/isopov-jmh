package com.sopovs.moradanen.jmh.sort;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class CountingByteSortBenchmarkTest {

    @Test
    public void testRandom() {
        CountingByteSortBenchmark benchmark = new CountingByteSortBenchmark();
        benchmark.setup();
        byte[] expected = benchmark.input.clone();
        Arrays.sort(expected);

        byte[] actual = benchmark.input.clone();

        CountingByteSortBenchmark.messyCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);

        actual = benchmark.input.clone();

        CountingByteSortBenchmark.readableCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testOnlyPositiveOdd() {
        CountingByteSortBenchmark benchmark = new CountingByteSortBenchmark();
        benchmark.setup();
        for (int i = 0; i < benchmark.input.length; i++) {
            if (benchmark.input[i] < 0) {
                benchmark.input[i] = (byte) (0 - benchmark.input[i]);
            }

            if (benchmark.input[i] % 2 == 1) {
                benchmark.input[i] = (byte) (benchmark.input[i] + 1);
            }
        }
        byte[] expected = benchmark.input.clone();
        Arrays.sort(expected);

        byte[] actual = benchmark.input.clone();

        CountingByteSortBenchmark.messyCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);

        actual = benchmark.input.clone();

        CountingByteSortBenchmark.readableCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSparse() {
        CountingByteSortBenchmark benchmark = new CountingByteSortBenchmark();
        // benchmark.setup();
        benchmark.input = new byte[1000];
        int val = 0;
        for (int i = 0; i < benchmark.input.length; i++) {
            benchmark.input[i] = (byte) val;
            val++;
            if (val >= 10) {
                val = 0;
            }
        }
        Collections.shuffle(Arrays.asList(benchmark.input));
        byte[] expected = benchmark.input.clone();
        Arrays.sort(expected);

        byte[] actual = benchmark.input.clone();

        CountingByteSortBenchmark.messyCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);

        actual = benchmark.input.clone();

        CountingByteSortBenchmark.readableCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);
    }

}
