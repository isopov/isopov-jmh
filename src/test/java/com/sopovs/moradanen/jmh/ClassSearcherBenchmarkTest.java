package com.sopovs.moradanen.jmh;

import org.junit.Assert;
import org.junit.Test;

import com.sopovs.moradanen.jmh.ClassSearcherBenchmark.ISearcher;

public class ClassSearcherBenchmarkTest {

    @Test
    public void testDummy4() {
        ISearcher searcher = new ClassSearcherBenchmark.DumbClassSearcher();
        searcher.refresh(new String[] { "a", "ab", "abc", "abcd" }, new long[] { 1, 2, 3, 4 });
        Assert.assertArrayEquals(new String[] { "abcd", "abc", "ab", "a" }, searcher.guess("a"));
    }

    @Test
    public void testDummy1() {
        ISearcher searcher = new ClassSearcherBenchmark.DumbClassSearcher();
        searcher.refresh(new String[] { "a" }, new long[] { 1 });
        Assert.assertArrayEquals(new String[] { "a" }, searcher.guess("a"));
    }
    
    
    @Test
    public void testLucene1() {
        ISearcher searcher = new ClassSearcherBenchmark.LuceneClassSearcher();
        searcher.refresh(new String[] { "a" }, new long[] { 1 });
        Assert.assertArrayEquals(new String[] { "a" }, searcher.guess("a"));
    }

}
