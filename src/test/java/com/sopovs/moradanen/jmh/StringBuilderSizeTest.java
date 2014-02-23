package com.sopovs.moradanen.jmh;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by isopov on 2/24/14.
 */
public class StringBuilderSizeTest {

    @Test
    public void test(){
        StringBuilderSize bench = new StringBuilderSize();
        assertEquals(bench.expandingSize(),bench.predefinedSize());
    }

}
