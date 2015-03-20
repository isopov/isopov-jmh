package com.sopovs.moradanen.jmh;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Resistance {

    static final Map<Character, String> ALPHABET = new HashMap<>();
    static {
        ALPHABET.put('A', ".-");
        ALPHABET.put('B', "-...");
        ALPHABET.put('C', "-.-.");
        ALPHABET.put('D', "-..");
        ALPHABET.put('E', ".");
        ALPHABET.put('F', "..-.");
        ALPHABET.put('G', "--.");
        ALPHABET.put('H', "....");
        ALPHABET.put('I', "..");
        ALPHABET.put('J', ".---");
        ALPHABET.put('K', "-.-");
        ALPHABET.put('L', ".-..");
        ALPHABET.put('M', "--");
        ALPHABET.put('N', "-.");
        ALPHABET.put('O', "---");
        ALPHABET.put('P', ".--.");
        ALPHABET.put('Q', "--.-");
        ALPHABET.put('R', ".-.");
        ALPHABET.put('S', "...");
        ALPHABET.put('T', "-");
        ALPHABET.put('U', "..-");
        ALPHABET.put('V', "...-");
        ALPHABET.put('W', ".--");
        ALPHABET.put('X', "-..-");
        ALPHABET.put('Y', "-.--");
        ALPHABET.put('Z', "--..");
    }

    public static void main(String args[]) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println(doWork(in));
        }
    }

    static long doWork(Scanner in) {
        String line = in.next();

        int n = in.nextInt();
        Node root = new Node();
        Node current = root;
        for (int i = 0; i < n; i++) {
            String word = in.next();
            String morseWord = toMorse(word);
            for (int j = 0; j < morseWord.length(); j++) {
                char c = morseWord.charAt(j);
                if (c == '.') {
                    if (current.dot == null) {
                        current.dot = new Node();

                    }
                    current = current.dot;
                } else {
                    if (current.dash == null) {
                        current.dash = new Node();

                    }
                    current = current.dash;
                }
            }
            current.endsOfWord++;
            // if (current.wordsEndingHere == null) {
            // current.wordsEndingHere = new HashSet<>();
            // }
            // current.wordsEndingHere.add(word);
            current = root;
        }

        return decode(0, line, root, new HashMap<>());
    }

    private static long decode(int start, String line, Node root, Map<Integer, Long> cache) {
        Long preComputed = cache.get(line.length() - start);
        if (preComputed != null) {
            return preComputed.longValue();
        }
        long result = 0L;
        Node current = root;
        for (int i = start; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '.') {
                current = current.dot;
            } else {
                current = current.dash;
            }
            if (current == null) {
                break;
            }
            if (current.endsOfWord != 0) {
                result += current.endsOfWord * decode(i + 1, line, root, cache);
            }
            if (i == line.length() - 1 && current.endsOfWord != 0) {
                result += current.endsOfWord;
            }
        }
        cache.put(line.length() - start, Long.valueOf(result));
        return result;
    }

    static String toMorse(String word) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            result.append(ALPHABET.get(word.charAt(i)));
        }
        return result.toString();
    }

    private static class Node {
        public int endsOfWord = 0;
        // public Set<String> wordsEndingHere;
        public Node dot;
        public Node dash;

        // @Override
        // public String toString() {
        // return wordsEndingHere.toString();
        // }

    }

}
