package com.sopovs.moradanen.jmh;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class EnumMapBenchmark {

    private static final String VALUE = "SomeTestValue";
    final Map<SmallEnum, String> smallFastMap = new FastMap<>();
    final Map<SmallEnum, String> smallHashMap = new HashMap<>();
    final Map<SmallEnum, String> smallEnumMap = new EnumMap<>(SmallEnum.class);

    final Map<LargeEnum, String> largeFastMap = new FastMap<>();
    final Map<LargeEnum, String> largeHashMap = new HashMap<>();
    final Map<LargeEnum, String> largeEnumMap = new EnumMap<>(LargeEnum.class);

    public EnumMapBenchmark() {
        SmallEnum[] smallEnums = SmallEnum.values();
        for (int i = 0; i < smallEnums.length; i++) {
            if (i % 2 == 0) {
                smallHashMap.put(smallEnums[i], VALUE);
                smallFastMap.put(smallEnums[i], VALUE);
                smallEnumMap.put(smallEnums[i], VALUE);
            }
        }

        LargeEnum[] largeEnums = LargeEnum.values();

        for (int i = 0; i < largeEnums.length; i++) {
            if (i % 2 == 0) {
                largeHashMap.put(largeEnums[i], VALUE);
                largeFastMap.put(largeEnums[i], VALUE);
                largeEnumMap.put(largeEnums[i], VALUE);
            }
        }

    }

    @Benchmark
    public void getFromSmallFastMap(Blackhole bh) {
        for (SmallEnum key : SmallEnum.values()) {
            bh.consume(smallFastMap.get(key));
        }
    }

    @Benchmark
    public void getFromSmallHashMap(Blackhole bh) {
        for (SmallEnum key : SmallEnum.values()) {
            bh.consume(smallHashMap.get(key));
        }
    }

    @Benchmark
    public void getFromSmallEnumMap(Blackhole bh) {
        for (SmallEnum key : SmallEnum.values()) {
            bh.consume(smallEnumMap.get(key));
        }
    }

    @Benchmark
    public void getFromLargeFastMap(Blackhole bh) {
        for (LargeEnum key : LargeEnum.values()) {
            bh.consume(largeFastMap.get(key));
        }
    }

    @Benchmark
    public void getFromLargeHashMap(Blackhole bh) {
        for (LargeEnum key : LargeEnum.values()) {
            bh.consume(largeHashMap.get(key));
        }
    }

    @Benchmark
    public void getFromLargeEnumMap(Blackhole bh) {
        for (LargeEnum key : LargeEnum.values()) {
            bh.consume(largeEnumMap.get(key));
        }
    }

    // Benchmark Mode Samples Mean Mean error Units
    // c.s.m.j.EnumMapBenchmark.getFromLargeEnumMap thrpt 15 4838.191 101.897
    // ops/ms
    // c.s.m.j.EnumMapBenchmark.getFromLargeFastMap thrpt 15 2045.161 16.507
    // ops/ms
    // c.s.m.j.EnumMapBenchmark.getFromLargeHashMap thrpt 15 1858.291 77.654
    // ops/ms
    // c.s.m.j.EnumMapBenchmark.getFromSmallEnumMap thrpt 15 14333.940 438.573
    // ops/ms
    // c.s.m.j.EnumMapBenchmark.getFromSmallFastMap thrpt 15 6271.272 127.502
    // ops/ms
    // c.s.m.j.EnumMapBenchmark.getFromSmallHashMap thrpt 15 6129.163 203.297
    // ops/ms
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + EnumMapBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }

    public enum SmallEnum {
        A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25;
    }

    public enum LargeEnum {
        A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, A31, A32,
        A33, A34, A35, A36, A37, A38, A39, A40, A41, A42, A43, A44, A45, A46, A47, A48, A49, A50, A51, A52, A53, A54, A55, A56, A57, A58, A59, A60, A61, A62,
        A63, A64, A65, A66, A67, A68, A69, A70, A71, A72, A73, A74, A75, A76, A77, A78, A79, A80;
    }
}