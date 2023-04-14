package io.github.isopov.jmh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BenderMoney {
    public static void main(String args[]) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println(doWork(in));
        }
    }

    static int doWork(Scanner in) {
        Room zero = getZeroRoom(in);
        Set<Integer> visited = new HashSet<>(20000);
        try {
            visited.add(zero.index);
            return zero.getMaxMoneyNoAlternativeWays(0, visited);
        } catch (CycleException c) {
            visited.clear();
            visited.add(zero.index);
            return zero.getMaxMoney(0, visited);
        }
    }

    static Room getZeroRoom(Scanner in) {
        int N = in.nextInt();
        in.nextLine();

        Map<Integer, Room> rooms = new HashMap<>();

        for (int i = 0; i < N; i++) {
            String[] roomString = in.nextLine().split(" ");
            Integer index = Integer.valueOf(roomString[0]);
            Room room = rooms.get(index);
            if (room == null) {
                room = new Room(index);
                rooms.put(index, room);
            }
            room.money = Integer.parseInt(roomString[1]);
            if (!"E".equals(roomString[2])) {
                Integer firstIndex = Integer.valueOf(roomString[2]);
                Room first = rooms.get(firstIndex);
                if (first == null) {
                    first = new Room(firstIndex);
                    rooms.put(firstIndex, first);
                }
                room.first = first;
            }

            if (!"E".equals(roomString[3])) {
                Integer secondIndex = Integer.valueOf(roomString[3]);
                Room second = rooms.get(secondIndex);
                if (second == null) {
                    second = new Room(secondIndex);
                    rooms.put(secondIndex, second);
                }
                room.second = second;
            }
        }

        Room zero = rooms.get(0);
        return zero;
    }

    static class CycleException extends RuntimeException {
        private static final long serialVersionUID = 1L;

    }

    static class Room {
        public final Integer index;
        public int maxMoneyNoAlternativeWays = 0;
        public int money;
        public Room first;
        public Room second;

        public Room(Integer index) {
            this.index = index;
        }

        public int getMaxMoneyNoAlternativeWays(int currentSum, Set<Integer> exclude) {
            if (maxMoneyNoAlternativeWays != 0) {
                return maxMoneyNoAlternativeWays + currentSum;
            }

            int firstMoney = getMaxMoneyNoAlternativeWaysThrough(first, currentSum, exclude);
            int secondMoney = getMaxMoneyNoAlternativeWaysThrough(second, currentSum, exclude);

            int result = Math.max(firstMoney, secondMoney);
            maxMoneyNoAlternativeWays = result - currentSum;
            return result;
        }

        private int getMaxMoneyNoAlternativeWaysThrough(Room next, int
                currentSum, Set<Integer> exclude) {
            int result = Integer.MIN_VALUE;
            if (next == null) {
                return currentSum + money;
            } else if (exclude.add(next.index)) {
                result = next.getMaxMoneyNoAlternativeWays(currentSum + money, exclude);
                exclude.remove(next.index);
            } else {
                System.err.println("Going to " + next.index + " for the second time");
                throw new CycleException();
            }
            return result;
        }

        public int getMaxMoney(int currentSum, Set<Integer> exclude) {
            int firstMoney = getMaxMoneyThrough(first, currentSum, exclude);
            int secondMoney = getMaxMoneyThrough(second, currentSum, exclude);

            return Math.max(firstMoney, secondMoney);
        }

        private int getMaxMoneyThrough(Room next, int currentSum, Set<Integer> exclude) {
            int result = Integer.MIN_VALUE;
            if (next == null) {
                result = currentSum + money;
            } else if (!exclude.contains(next.index)) {
                exclude.add(next.index);
                result = next.getMaxMoney(currentSum + money, exclude);
                exclude.remove(next.index);
            }
            return result;
        }

    }
}
