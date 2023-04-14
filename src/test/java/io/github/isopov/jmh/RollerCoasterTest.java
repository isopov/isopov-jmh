package io.github.isopov.jmh;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class RollerCoasterTest {
    @Rule
    public Timeout globalTimeout = new Timeout(5, TimeUnit.SECONDS);

    @Test
    public void testFive() {
        try (Scanner in = new Scanner(RollerCoaster.class.getResourceAsStream("/rollercoaster_five.txt"))) {
            assertEquals(4999975000L, RollerCoaster.doWork(in));
        }
    }

    @Test
    public void testSix() {
        try (Scanner in = new Scanner(RollerCoaster.class.getResourceAsStream("/rollercoaster_six.txt"))) {
            assertEquals(89744892565569L, RollerCoaster.doWork(in));
        }
    }

}
