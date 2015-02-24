package com.sopovs.moradanen.jmh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Teads {
    public static void main(String args[]) {
        System.out.println(distanceFromCenter(new Scanner(System.in)));
    }

    static int distanceFromCenter(Scanner in) {
        Map<Integer, Node> nodes = getNodes(in);
        byte[][] d = getDistances(nodes);
        nodes = null;

        return distanceFromCenter(d);
    }

    private static int distanceFromCenter(byte[][] d) {
        floydWarshall(d);

        int size = d.length;
        byte[] maxes = new byte[size];
        for (int i = 0; i < size; i++) {
            maxes[i] = max(d[i]);
        }

        return min(maxes);
    }

    private static void floydWarshall(byte[][] d) {
        for (int k = 0; k < d.length; k++) {
            floydWarshallIteration(d, k);
        }
    }

    private static void floydWarshallIteration(byte[][] d, int k) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                d[i][j] = (byte) Math.min(d[i][j], d[i][k] + d[k][j]);
            }
        }
    }

    private static byte[][] getDistances(Map<Integer, Node> nodes) {
        int size = nodes.size();
        // System.err.println(counter + " nodes");
        byte[][] d = new byte[size][];
        for (int i = 0; i < size; i++) {
            d[i] = new byte[size];
            Arrays.fill(d[i], Byte.MAX_VALUE);
        }
        for (int i = 0; i < size; i++) {
            d[i][i] = 0;
        }

        for (Node node : nodes.values()) {
            if (node.nodes != null) {
                for (Node other : node.nodes) {
                    d[node.index][other.index] = 1;
                    d[other.index][node.index] = 1;
                }
            }
        }
        return d;
    }

    private static Map<Integer, Node> getNodes(Scanner in) {
        int n = in.nextInt(); // the number of adjacency relations
        Map<Integer, Node> nodes = new HashMap<>();
        int counter = 0;
        for (int i = 0; i < n; i++) {
            int xi = in.nextInt(); // the ID of a person which is adjacent to yi
            Node xnode = nodes.get(xi);
            if (xnode == null) {
                xnode = new Node(counter);
                nodes.put(xi, xnode);
                counter++;
            }

            int yi = in.nextInt(); // the ID of a person which is adjacent to xi
            Node ynode = nodes.get(yi);
            if (ynode == null) {
                ynode = new Node(counter);
                nodes.put(yi, ynode);
                counter++;
            }

            if (xnode.nodes == null) {
                if (ynode.nodes != null) {
                    ynode.nodes.add(xnode);
                } else {
                    xnode.nodes = new ArrayList<>();
                    xnode.nodes.add(ynode);
                }
            } else {
                xnode.nodes.add(ynode);
            }
        }
        return nodes;
    }

    private static byte max(byte[] a) {
        byte result = Byte.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            byte next = a[i];
            if (next > result) {
                result = next;
            }
        }
        return result;
    }

    private static byte min(byte[] a) {
        byte result = Byte.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            byte next = a[i];
            if (next < result) {
                result = next;
            }
        }
        return result;
    }

    private static class Node {
        private final int index;

        public Node(int index) {
            this.index = index;
        }

        private List<Node> nodes;

    }

}
