package io.github.isopov.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class MapMapMapBenchmark {

    @Benchmark
    public Map<Integer, Map<Integer, Map<Integer, String>>> mapMapMap() {
        var firsts = new HashMap<Integer, Map<Integer, Map<Integer, String>>>();
        test(((first, second, third, value) -> {
            var seconds = firsts.computeIfAbsent(first, unused -> new HashMap<>());
            var thirds = seconds.computeIfAbsent(second, unused -> new HashMap<>());
            thirds.put(third, value);
        }));
        return firsts;
    }

    @Benchmark
    public Map<Integer, Map<Integer, Map<Integer, String>>> mapMapMapExact() {
        var firsts = HashMap.<Integer, Map<Integer, Map<Integer, String>>>newHashMap(FIRSTS);
        test(((first, second, third, value) -> {
            var seconds = firsts.computeIfAbsent(first, unused -> HashMap.newHashMap(SECONDS));
            var thirds = seconds.computeIfAbsent(second, unused -> HashMap.newHashMap(THIRDS));
            thirds.put(third, value);
        }));
        return firsts;
    }

    record IntKey(int first, int second, int third) {
    }

    @Benchmark
    public Map<IntKey, String> singleMap() {
        var map = new HashMap<IntKey, String>();
        test((first, second, third, value) -> map.put(new IntKey(first, second, third), value));
        return map;
    }


    @Benchmark
    public Map<IntKey, String> singleMapExact() {
        var map = HashMap.<IntKey, String>newHashMap(FIRSTS * SECONDS * THIRDS);
        test((first, second, third, value) -> map.put(new IntKey(first, second, third), value));
        return map;
    }

    interface Tester {
        void accept(int first, int second, int third, String value);
    }

    //not static, not final to prevent constant folding (see JMHSample_10_ConstantFold)
    private int FIRSTS = 100;
    private int SECONDS = 10;
    private int THIRDS = 10;

    private void test(Tester tester) {
        for (int first = 0; first < FIRSTS; first++) {
            for (int second = 0; second < SECONDS; second++) {
                for (int third = 0; third < THIRDS; third++) {
                    tester.accept(first, second, third, first + "-" + second + "-" + third);
                }
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MapMapMapBenchmark.class.getSimpleName() + ".*")
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}





