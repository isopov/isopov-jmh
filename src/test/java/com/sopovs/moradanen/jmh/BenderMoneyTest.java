package com.sopovs.moradanen.jmh;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.sopovs.moradanen.jmh.BenderMoney.Room;

public class BenderMoneyTest {

    @Rule
    public Timeout globalTimeout = new Timeout(5, TimeUnit.SECONDS);

    private static final String ONE_INPUT = "3\n" +
            "0 20 1 2\n" +
            "1 17 E E\n" +
            "2 20 E E\n";

    @Test
    public void testOne() {
        try (Scanner in = new Scanner(ONE_INPUT)) {
            assertEquals(40, BenderMoney.doWork(in));
        }
    }

    @Test
    public void testFour() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_four.txt"))) {
            assertEquals(358, BenderMoney.doWork(in));
        }
    }

    @Test
    public void testFourNoCycles() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_four.txt"))) {
            Room zeroRoom = BenderMoney.getZeroRoom(in);
            assertEquals(358,
                    zeroRoom.getMaxMoneyNoAlternativeWays(0, new HashSet<>(asList(zeroRoom.index))));
        }
    }

    @Test
    public void testFourWithPossibleCycles() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_four.txt"))) {
            Room zeroRoom = BenderMoney.getZeroRoom(in);
            assertEquals(358,
                    zeroRoom.getMaxMoney(0, new HashSet<>(asList(zeroRoom.index))));
        }
    }

    @Test
    public void testFive() {
        try (Scanner in = new Scanner(BenderMoney.class.getResourceAsStream("/bendermoney_five.txt"))) {
            assertEquals(1928, BenderMoney.doWork(in));
        }
    }
}
