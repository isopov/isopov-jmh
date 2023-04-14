package io.github.isopov.jmh.sort;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class CountingShortSortBenchmarkTest {

    @Test
    public void test() {

        CountingShortSortBenchmark benchmark = new CountingShortSortBenchmark();
        benchmark.setup();
        short[] expected = benchmark.input.clone();
        Arrays.sort(expected);

        short[] actual = benchmark.input.clone();

        CountingShortSortBenchmark.countingSort(actual);

        Assert.assertArrayEquals(expected, actual);

        actual = benchmark.input.clone();

        CountingShortSortBenchmark.myCountingSort(actual);

        Assert.assertArrayEquals(expected, actual);

    }

}
