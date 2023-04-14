package io.github.isopov.jmh;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddExactBenchmarkTest {

    public static class StandardTest extends AbstractTest {
        public StandardTest() {
            super(new StandardSummator());
        }
    }

    public static class LongTest extends AbstractTest {
        public LongTest() {
            super(new LongSummator());
        }
    }

    public static abstract class AbstractTest {

        private final Summator summator;

        public AbstractTest(Summator summator) {
            this.summator = summator;
        }

        @Test
        public void testNoOverflow() {
            assertEquals(2, summator.sum(1, 1));
            assertEquals(Integer.MAX_VALUE - 1, summator.sum(Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2));

            assertEquals(-1, summator.sum(Integer.MAX_VALUE, Integer.MIN_VALUE));

            assertEquals(Integer.MIN_VALUE, summator.sum(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2));
        }

        @Test(expected = ArithmeticException.class)
        public void testOverflow1() {
            summator.sum(Integer.MIN_VALUE, Integer.MIN_VALUE);
        }

        @Test(expected = ArithmeticException.class)
        public void testOverflow2() {
            summator.sum(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2 - 1);
        }

        @Test(expected = ArithmeticException.class)
        public void testOverflow3() {
            summator.sum(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Test(expected = ArithmeticException.class)
        public void testOverflow4() {
            summator.sum(Integer.MAX_VALUE / 2 + 1, Integer.MAX_VALUE / 2 + 1);
        }

        @Test(expected = ArithmeticException.class)
        public void testOverflow5() {
            summator.sum(Integer.MAX_VALUE / 4 * 3, Integer.MAX_VALUE / 4 * 3);
        }

        @Test(expected = ArithmeticException.class)
        public void testOverflow6() {
            summator.sum(Integer.MIN_VALUE / 4 * 3, Integer.MIN_VALUE / 4 * 3);
        }

    }

    private interface Summator {
        int sum(int x, int y);
    }

    private static class StandardSummator implements Summator {

        @Override
        public int sum(int x, int y) {
            return Math.addExact(x, y);
        }
    }

    private static class LongSummator implements Summator {

        @Override
        public int sum(int x, int y) {
            return AddExactBenchmark.addWithLongs(x, y);
        }

    }

}
