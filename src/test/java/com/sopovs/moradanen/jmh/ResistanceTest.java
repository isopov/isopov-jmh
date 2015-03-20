package com.sopovs.moradanen.jmh;

import static com.sopovs.moradanen.jmh.Resistance.toMorse;
import static org.junit.Assert.assertEquals;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

public class ResistanceTest {

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(1, TimeUnit.SECONDS));

    @Test
    public void testSimple() {
        Scanner in = new Scanner("......-...-..---.-----.-..-..-..\n5\nHELL\nHELLO\nOWORLD\nWORLD\nTEST");
        assertEquals(2L, Resistance.doWork(in));
    }

    @Test
    public void testEE() {
        String in = toMorse("EE") + "\n1\nE";
        assertEquals(1L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testAE() {
        String in = toMorse("AE") + "\n2\nA\nE";
        assertEquals(1L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testA() {
        String in = toMorse("A") + "\n1\nA";
        assertEquals(1L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testF4() {
        String in = toMorse("F") + "\n5\nA\nF\nE\nT\nI";
        assertEquals(4L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testAB() {
        String in = toMorse("AB") + "\n2\nA\nB";
        assertEquals(1L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testABCD6() {
        String in = toMorse("ABCD") + "\n8\nA\nB\nC\nD\nAB\nBC\nCD\nABC";
        assertEquals(6L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testABCD4() {
        String in = toMorse("ABCD") + "\n7\nA\nB\nD\nAB\nBC\nCD\nABC";
        assertEquals(4L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testABCD4_2() {
        String in = toMorse("ABCD") + "\n6\nA\nB\nD\nAB\nCD\nC";
        assertEquals(4L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testHABCD6() {
        String in = toMorse("HABCD") + "\n8\nH\nHA\n\nA\nB\nD\nAB\nCD\nC";
        assertEquals(6L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testABCDEF2() {
        String in = toMorse("ABCDEF") + "\n6\nABC\nDE\nF\nA\nBCD\nEF";
        assertEquals(2L, Resistance.doWork(new Scanner(in)));
    }

    @Test
    public void testLarge() {
        Scanner in = new Scanner(ResistanceTest.class.getResourceAsStream("/resistance_large.txt"));
        assertEquals(57330892800L, Resistance.doWork(in));
    }

    @Test
    public void testMorse() {
        assertEquals(toMorse("ABCDEF"), toMorse("ADECDEEAE"));
    }

}
