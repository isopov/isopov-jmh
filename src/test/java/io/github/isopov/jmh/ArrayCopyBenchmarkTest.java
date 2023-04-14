package io.github.isopov.jmh;

import org.junit.Test;

public class ArrayCopyBenchmarkTest {

    @Test
    public void test() {
        ArrayCopyBenchmark bench = new ArrayCopyBenchmark();
        int[] loopcopy = bench.loopcopy();
        int[] systemcopy = bench.systemcopy();
        if (loopcopy.length != systemcopy.length) {
            throw new RuntimeException();
        }
        for (int i = 0; i < systemcopy.length; i++) {
            if (loopcopy[i] != systemcopy[i]) {
                throw new RuntimeException();
            }
        }
    }

}
