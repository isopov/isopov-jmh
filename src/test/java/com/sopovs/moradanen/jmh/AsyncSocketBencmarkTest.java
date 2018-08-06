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
    public void testBlocking2() throws Exception {
        test("blocking", 2);
    }


    @Test
    public void testAsync() throws Exception {
        test("async");
    }

    @Test
    public void testAsync2() throws Exception {
        test("async", 2);
    }


    @Test
    public void testAsyncHandlers() throws Exception {
        test("asyncHandlers");
    }

    @Test
    public void testAsyncHandlers2() throws Exception {
        test("asyncHandlers", 2);
    }


    @Test
    public void testNetty() throws Exception {
        test("netty");
    }

//    @Ignore
    @Test
    public void testNetty2() throws Exception {
        test("netty", 2);
    }

    private static void test(String type) throws Exception {
        test(type, 1);
    }

    private static void test(String type, int count) throws Exception {
        AsyncSocketBencmark benchmark = new AsyncSocketBencmark();
        benchmark.pings = 5;
        benchmark.type = type;

        benchmark.setup();
        for (int i = 0; i < count; i++) {
            benchmark.ping();
        }

        benchmark.tearDown();

        assertEquals(5 * count, benchmark.server.count);
    }

}