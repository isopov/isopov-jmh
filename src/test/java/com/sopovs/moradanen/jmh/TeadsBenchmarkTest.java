package com.sopovs.moradanen.jmh;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

import org.junit.Test;

public class TeadsBenchmarkTest {

    @Test
    public void testOne() {
        int distance = Teads.distanceFromCenter(new Scanner(TeadsBenchmark.ONE.replace('#', '\n')));
        assertEquals(2, distance);
    }

    @Test
    public void testFour() {
        int distance = Teads.distanceFromCenter(new Scanner(TeadsBenchmark.FOUR.replace('#', '\n')));
        assertEquals(5, distance);
    }

    @Test
    public void testSix() {
        int distance = Teads.distanceFromCenter(new Scanner(TeadsBenchmark.SIX.replace('#', '\n')));
        assertEquals(7, distance);
    }
}
