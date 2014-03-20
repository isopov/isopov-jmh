package com.sopovs.moradanen.jmh;

import java.util.Random;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class ArrayCopyBenchmark {

    int[] state = new int[7654231];
    {
        Random r = new Random(228L);
        for (int i = 0; i < state.length; i++) {
            state[i] = r.nextInt();
        }
    }

    @GenerateMicroBenchmark
    @BenchmarkMode({ Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime })
    public int[] loopcopy() {
        int[] result = new int[1234567];
        for (int i = 1234567; i < 1234567 * 2; i++) {
            result[i - 1234567] = state[i];
        }
        return result;
    }

    @GenerateMicroBenchmark
    @BenchmarkMode({ Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime })
    public int[] systemcopy() {
        int[] result = new int[1234567];
        System.arraycopy(state, 1234567, result, 0, 1234567);
        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ArrayCopyBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }
}
