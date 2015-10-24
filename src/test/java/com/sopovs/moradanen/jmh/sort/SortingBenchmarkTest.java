package com.sopovs.moradanen.jmh.sort;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class SortingBenchmarkTest {

    @Test
    public void test0() {
        int[] input = new int[] { 1, 2, 3 };
        SortingBenchmark.insertion(input);
        Assert.assertArrayEquals(new int[] { 1, 2, 3 }, input);
    }

    @Test
    public void test1() {
        int[] input = new int[] { 3, 2, 1 };
        SortingBenchmark.insertion(input);
        Assert.assertArrayEquals(new int[] { 1, 2, 3 }, input);
    }

    @Test
    public void test3() {
        int[] input = new int[] { 1, 2, 2, 1 };
        SortingBenchmark.insertion(input);
        Assert.assertArrayEquals(new int[] { 1, 1, 2, 2 }, input);
    }

    @Test
    public void test4() {
        int[] input = new int[] { 1, 2, 3, 1 };
        SortingBenchmark.insertion(input);
        Assert.assertArrayEquals(new int[] { 1, 1, 2, 3 }, input);
    }

    @Test
    public void testHuge() {
        int[] input = new int[100000];
        Random r = new Random(228L);
        for (int i = 0; i < input.length; i++) {
            input[i] = r.nextInt();
        }
        int[] input2 = input.clone();
        SortingBenchmark.insertion(input);
        Arrays.sort(input2);
        Assert.assertArrayEquals(input2, input);
    }

}
