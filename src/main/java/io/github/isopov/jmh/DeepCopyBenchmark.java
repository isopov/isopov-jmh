package io.github.isopov.jmh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

/**
 * @author isopov
 * @since 19.03.2014
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class DeepCopyBenchmark {

    @VisibleForTesting
    HashSet<Copyable> state = Sets.newHashSetWithExpectedSize(10);

    {
        for (int i = 0; i < 10; i++) {
            state.add(new Copyable(i, i, "Some String " + i));
        }
    }

    @Benchmark
    public Set<Copyable> loopCopy() {
        return copySet(state);
    }

    @Benchmark
    public Set<Copyable> deepCopy() {
        return deepCopy(state);
    }

    @Benchmark
    public Object cloneCopy() {
        return state.clone();
    }

    // Benchmark Mode Samples Mean Mean error Units
    // c.s.m.j.DeepCopyBenchmark.cloneCopy avgt 15 201.253 1.316 ns/op
    // c.s.m.j.DeepCopyBenchmark.deepCopy avgt 15 11895.934 398.229 ns/op
    // c.s.m.j.DeepCopyBenchmark.loopCopy avgt 15 193.952 2.393 ns/op
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + DeepCopyBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();
        new Runner(opt).run();
    }

    private static Set<Copyable> copySet(Set<Copyable> setToCopy) {
        Set<Copyable> result = Sets.newHashSetWithExpectedSize(setToCopy.size());
        for (Copyable copyable : setToCopy) {
            result.add(new Copyable(copyable));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T deepCopy(T obj) {
        try {
            // tried different values of the initial byte buffer size - no
            // difference observed
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class Copyable implements Serializable, Cloneable {
        private static final long serialVersionUID = 1L;
        private final int field1;
        private final long field2;
        private final String field3;

        public Copyable(Copyable other) {
            this.field1 = other.field1;
            this.field2 = other.field2;
            this.field3 = other.field3;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public Copyable(int field1, long field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        public int getField1() {
            return field1;
        }

        public long getField2() {
            return field2;
        }

        public String getField3() {
            return field3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Copyable copyable = (Copyable) o;

            if (field1 != copyable.field1)
                return false;
            if (field2 != copyable.field2)
                return false;
            if (field3 != null ? !field3.equals(copyable.field3) : copyable.field3 != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = field1;
            result = 31 * result + (int) (field2 ^ (field2 >>> 32));
            result = 31 * result + (field3 != null ? field3.hashCode() : 0);
            return result;
        }
    }

}
