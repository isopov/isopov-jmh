package com.sopovs.moradanen.jmh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Teads {
    public static void main(String args[]) {
        System.out.println(distanceFromCenter(new Scanner(System.in)));
    }

    static int distanceFromCenter(Scanner in) {
        Set<Node> nodes = getNodesWithoutRoots(in);
        CenterFinder finder = new CenterFinder();
        for (Node node : nodes) {
            int dist = node.findMaxDistance(null, 0, finder);
            if (dist < finder.currentMin) {
                finder.currentMin = dist;
            }
        }
        return finder.currentMin;
    }

    static Set<Node> getNodesWithoutRoots(Scanner in) {
        int n = in.nextInt();
        Map<Integer, Node> nodes = new HashMap<>();
        Set<Node> roots = new HashSet<>();
        int counter = 0;
        for (int i = 0; i < n; i++) {
            int xi = in.nextInt();
            Node xnode = nodes.get(xi);
            if (xnode == null) {
                xnode = new Node(counter);
                nodes.put(xi, xnode);
                counter++;
                roots.add(xnode);
            } else {
                roots.remove(xnode);
            }

            int yi = in.nextInt();
            Node ynode = nodes.get(yi);
            if (ynode == null) {
                ynode = new Node(counter);
                nodes.put(yi, ynode);
                counter++;
                roots.add(ynode);
            } else {
                roots.remove(ynode);
            }

            ynode.nodes.add(xnode);
            xnode.nodes.add(ynode);
        }

        Set<Node> nodesWithoutRoots = new HashSet<>(nodes.values());
        nodesWithoutRoots.removeAll(roots);
        return nodesWithoutRoots;
    }

    static class CenterFinder {
        public int currentMin = Integer.MAX_VALUE;
    }

    static class Node {
        public final int index;

        public Node(int index) {
            this.index = index;
        }

        public List<Node> nodes = new ArrayList<>();

        public int findMaxDistance(Node exclude, int plus, CenterFinder finder) {
            if (plus > finder.currentMin) {
                return Integer.MAX_VALUE;
            }
            if (nodes.size() == 1 && nodes.get(0) == exclude) {
                return plus;
            }
            int result = Integer.MIN_VALUE;
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node == exclude) {
                    continue;
                }
                int dist = node.findMaxDistance(this, plus + 1, finder);
                if (dist > result) {
                    result = dist;
                }
            }
            return result;
        }

    }
}
