package io.github.isopov.jmh;

import java.util.Arrays;
import java.util.Scanner;

public class RollerCoaster {
    public static void main(String args[]) {
        try (Scanner in = new Scanner(System.in)) {
            long result = doWork(in);
            System.out.println(result);
        }
    }

    static long doWork(Scanner in) {
        int L = in.nextInt();
        int C = in.nextInt();
        int N = in.nextInt();
        int[] groups = new int[N];
        for (int i = 0; i < N; i++) {
            groups[i] = in.nextInt();
        }
        int sum = Arrays.stream(groups).sum();
        if (sum < L) {
            return (long) C * (long) sum;
        }

        long result = 0L;
        int next = 0;

        Cycle[] cycles = new Cycle[N];
        for (int i = 0; i < C; i++) {
            Cycle cycle = cycles[next];
            if (cycle != null) {
                next = cycle.next;
                result += cycle.rideResult;
            } else {
                int currentPlaces = 0;
                long rideResult = 0L;
                int position = next;
                while (currentPlaces < L) {
                    currentPlaces += groups[next];
                    if (currentPlaces <= L) {
                        rideResult += groups[next];
                        next++;
                        if (next >= N) {
                            next = 0;
                        }
                    }
                }
                result += rideResult;
                cycles[position] = new Cycle(next, rideResult);
            }
        }

        return result;
    }

    static class Cycle {
        public final int next;
        public final long rideResult;

        public Cycle(int next, long rideResult) {
            this.next = next;
            this.rideResult = rideResult;
        }
    }

}
