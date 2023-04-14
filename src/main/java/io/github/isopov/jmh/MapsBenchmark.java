package io.github.isopov.jmh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;

public class MapsBenchmark {

    @Benchmark
    public Iterator<Entry<Object, Object>> emptyCollectionsMap() {
        return newEmptyCollectionsMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<Object, Object> newEmptyCollectionsMap() {
        return Collections.emptyMap();
    }

    @Benchmark
    public Iterator<Entry<String, String>> emptyHashMap() {
        return newEmptyHashMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public HashMap<String, String> newEmptyHashMap() {
        return new HashMap<String, String>();
    }

    @Benchmark
    public UnmodifiableIterator<Entry<Object, Object>> emptyImmutableMap() {
        return newEmptyImmutableMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public ImmutableMap<Object, Object> newEmptyImmutableMap() {
        return ImmutableMap.of();
    }

    @Benchmark
    public Iterator<Entry<String, String>> singletonCollectionsMap() {
        return newSingletonMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newSingletonMap() {
        return Collections.singletonMap("foo1", "bar1");
    }

    @Benchmark
    public boolean singletonHashMap() {
        HashMap<String, String> map = newSingletonHashMap();
        return map.containsKey("foo1");
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public HashMap<String, String> newSingletonHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        return map;
    }

    @Benchmark
    public UnmodifiableIterator<Entry<String, String>> singletonImmutableMap() {
        return newSingletonImmutableMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public ImmutableMap<String, String> newSingletonImmutableMap() {
        return ImmutableMap.of("foo1", "bar1");
    }

    @Benchmark
    public Iterator<Entry<String, String>> doubleHashMap() {
        HashMap<String, String> map = newDoubleHashMap();
        return map.entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public HashMap<String, String> newDoubleHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        return map;
    }

    @Benchmark
    public Iterator<Entry<String, String>> doubleUnmodifiableHashMap() {
        Map<String, String> map = newDoubleUnmodifiableHashMap();
        return map.entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newDoubleUnmodifiableHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        return Collections.unmodifiableMap(map);
    }

    @Benchmark
    public UnmodifiableIterator<Entry<String, String>> doubleImmutableMap() {
        return newDoubleImmutableMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public ImmutableMap<String, String> newDoubleImmutableMap() {
        return ImmutableMap.of("foo1", "bar1", "foo2", "bar2");
    }

    @Benchmark
    public Iterator<Entry<String, String>> trippleHashMap() {
        Map<String, String> map = newTrippleHashMap();
        return map.entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newTrippleHashMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        map.put("foo3", "bar3");
        return map;
    }

    @Benchmark
    public Iterator<Entry<String, String>> trippleUnmodifiableHashMap() {
        Map<String, String> map = newTrippleUnmodifiableHashMap();
        return Collections.unmodifiableMap(map).entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newTrippleUnmodifiableHashMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        map.put("foo3", "bar3");
        return map;
    }

    @Benchmark
    public UnmodifiableIterator<Entry<String, String>> trippleImmutableMap() {
        return newTrippleImmutableMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public ImmutableMap<String, String> newTrippleImmutableMap() {
        return ImmutableMap.of("foo1", "bar1", "foo2", "bar2", "foo3", "bar3");
    }

    @Benchmark
    public Iterator<Entry<String, String>> quadroHashMap() {
        Map<String, String> map = newQuadroHashMap();
        return map.entrySet().iterator();
    }

    @Benchmark
    public Iterator<Entry<String, String>> quadroUnmodifiableHashMap() {
        Map<String, String> map = newQuadroUnmodifiableHashMap();
        return map.entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newQuadroHashMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        map.put("foo3", "bar3");
        map.put("foo4", "bar4");
        return map;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newQuadroUnmodifiableHashMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        map.put("foo3", "bar3");
        map.put("foo4", "bar4");
        return Collections.unmodifiableMap(map);
    }

    @Benchmark
    public UnmodifiableIterator<Entry<String, String>> quadroImmutableMap() {
        return newQuadroImmutableMap().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public ImmutableMap<String, String> newQuadroImmutableMap() {
        return ImmutableMap.of("foo1", "bar1", "foo2", "bar2", "foo3", "bar3", "foo4", "bar4");
    }

    @Benchmark
    public UnmodifiableIterator<Entry<String, String>> immutable128Map() {
        return newImmutable128Map().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public ImmutableMap<String, String> newImmutable128Map() {
        Builder<String, String> builder = ImmutableMap.builder();
        for (int i = 1; i <= 128; i++) {
            builder.put("foo".concat(String.valueOf(i)), "bar".concat(String.valueOf(i)));
        }
        return builder.build();
    }

    @Benchmark
    public Iterator<Entry<String, String>> hashExpected128Map() {
        return newHash128Map().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newHashExpected128Map() {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(128);
        for (int i = 1; i <= 128; i++) {
            map.put("foo".concat(String.valueOf(i)), "bar".concat(String.valueOf(i)));
        }
        return map;
    }

    @Benchmark
    public Iterator<Entry<String, String>> hash128Map() {
        return newHash128Map().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newHash128Map() {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 1; i <= 128; i++) {
            map.put("foo".concat(String.valueOf(i)), "bar".concat(String.valueOf(i)));
        }
        return map;
    }

    @Benchmark
    public Iterator<Entry<String, String>> hashUnmodifiable128Map() {
        return newHashUnmodifiable128Map().entrySet().iterator();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Map<String, String> newHashUnmodifiable128Map() {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(128);
        for (int i = 1; i <= 128; i++) {
            map.put("foo".concat(String.valueOf(i)), "bar".concat(String.valueOf(i)));
        }
        return Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MapsBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }

}