package com.sopovs.moradanen.jmh;

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

        for (int i = 0; i < C; i++) {
            int currentPlaces = 0;
            while (currentPlaces < L) {
                currentPlaces += groups[next];
                if (currentPlaces <= L) {
                    result += groups[next];
                    next++;
                    if (next >= N) {
                        next = 0;
                    }
                }
            }
        }

        return result;
    }
}
