package com.sopovs.moradanen.jmh;

import org.junit.Test;

public class ArrayCopyTest {

    @Test
    public void test() {
        ArrayCopy bench = new ArrayCopy();
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
