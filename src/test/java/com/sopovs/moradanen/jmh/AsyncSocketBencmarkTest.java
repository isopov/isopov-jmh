package com.sopovs.moradanen.jmh;

import org.junit.Test;

public class AsyncSocketBencmarkTest {

    @Test
    public void testBlocking() throws Exception {
        test("blocking");
    }

    @Test
    public void testAsync() throws Exception {
        test("async");
    }

    @Test
    public void testAsyncHandlers() throws Exception {
        test("asyncHandlers");
    }

    private void test(String type) throws Exception {
        AsyncSocketBencmark benchmark = new AsyncSocketBencmark();
        benchmark.pings = 5;
        benchmark.type = type;

        benchmark.setup();
        benchmark.ping();
        benchmark.tearDown();
    }
}