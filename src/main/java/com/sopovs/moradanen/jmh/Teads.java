package com.sopovs.moradanen.jmh;

import java.nio.ByteBuffer;
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
        ByteBuffer d = getDistances(nodes);
        int size = nodes.size();
        nodes = null;

        return distanceFromCenter(d, size);
    }

    private static int distanceFromCenter(ByteBuffer d, int size) {
        floydWarshall(d, size);

        byte min = Byte.MAX_VALUE;
        byte[] tmp = new byte[size];
        d.reset();
        for (int i = 0; i < size; i++) {
            d.get(tmp);
            byte max = max(tmp);
            if (max < min) {
                min = max;
            }
        }

        return min;
    }

    private static void floydWarshall(ByteBuffer d, int size) {
        for (int k = 0; k < size; k++) {
            floydWarshallIteration(d, size, k);
        }
    }

    private static void floydWarshallIteration(ByteBuffer d, int size, int k) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int was = d.get(i * size + j);
                int next = d.get(i * size + k) + d.get(k * size + j);
                if (next < was) {
                    d.put(i * size + j, (byte) next);
                }
            }
        }
    }

    private static ByteBuffer getDistances(Map<Integer, Node> nodes) {
        int size = nodes.size();
        ByteBuffer d = ByteBuffer.allocate(size * size);
        d.mark();
        byte[] m = new byte[size];
        Arrays.fill(m, Byte.MAX_VALUE);
        for (int i = 0; i < size; i++) {
            m[i] = 0;
            d.put(m);
            m[i] = Byte.MAX_VALUE;
        }

        for (Node node : nodes.values()) {
            if (node.nodes != null) {
                for (Node other : node.nodes) {
                    d.put(node.index * size + other.index, (byte) 1);
                    d.put(other.index * size + node.index, (byte) 1);
                }
            }
        }
        return d;
    }

    static Map<Integer, Node> getNodes(Scanner in) {
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

    static class Node {
        public final int index;

        public Node(int index) {
            this.index = index;
        }

        public List<Node> nodes;

    }

}
