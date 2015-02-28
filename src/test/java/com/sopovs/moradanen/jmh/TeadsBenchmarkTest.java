package com.sopovs.moradanen.jmh;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TeadsBenchmarkTest {

    @Rule
    public Timeout globalTimeout = new Timeout(2, TimeUnit.SECONDS);

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

    @Test
    public void testEight() {
        try (Scanner in = new Scanner(TeadsBenchmarkTest.class.getResourceAsStream("/teads_eight.txt"))) {
            int distance = Teads.distanceFromCenter(in);
            assertEquals(9, distance);
        }
    }

    @Test
    public void testNine() {
        try (Scanner in = new Scanner(TeadsBenchmarkTest.class.getResourceAsStream("/teads_nine.txt"))) {
            int distance = Teads.distanceFromCenter(in);
            assertEquals(15, distance);
        }
    }

}
