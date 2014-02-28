package com.sopovs.moradanen.jmh;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by isopov on 2/27/14.
 */
public class FastMapBenchmarkTest {

    @Test
    public void test() {

        FastMapBenchmark bench = new FastMapBenchmark();
        Assert.assertEquals(FastMapBenchmark.KEYS_NUMBER, bench.fastMap.size());
        Assert.assertEquals(FastMapBenchmark.KEYS_NUMBER, bench.concurrentHashMap.size());
        Assert.assertEquals(FastMapBenchmark.KEYS_TO_TEST_NUMBER, bench.keysToTest.size());
    }

}
