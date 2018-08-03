package com.sopovs.moradanen.jmh;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @Test
    @Ignore //TODO
    public void testNetty() throws Exception {
        test("netty");
    }

    private void test(String type) throws Exception {
        AsyncSocketBencmark benchmark = new AsyncSocketBencmark();
        benchmark.pings = 5;
        benchmark.type = type;

        benchmark.setup();
        benchmark.ping();
        benchmark.tearDown();

        assertEquals(5, benchmark.server.count);
    }
}