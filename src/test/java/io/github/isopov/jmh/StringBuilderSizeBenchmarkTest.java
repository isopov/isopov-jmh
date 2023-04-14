package io.github.isopov.jmh;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by isopov on 2/24/14.
 */
public class StringBuilderSizeBenchmarkTest {

    @Test
    public void test(){
        StringBuilderSizeBenchmark bench = new StringBuilderSizeBenchmark();
        assertEquals(bench.expandingSize(),bench.predefinedSize());
    }

}
