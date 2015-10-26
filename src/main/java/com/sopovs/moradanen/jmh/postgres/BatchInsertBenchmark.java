package com.sopovs.moradanen.jmh.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

//Benchmark                                        (batchSize)  Mode  Cnt     Score    Error  Units
//BatchInsertBenchmark.batchInsertDefault                   10  avgt   15     0.463 ±  0.024  ms/op
//BatchInsertBenchmark.batchInsertDefault                  100  avgt   15     2.072 ±  0.229  ms/op
//BatchInsertBenchmark.batchInsertDefault                 1000  avgt   15    19.132 ±  0.827  ms/op
//BatchInsertBenchmark.batchInsertDefault                10000  avgt   15   196.600 ± 13.655  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated               10  avgt   15     0.452 ±  0.007  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated              100  avgt   15     1.931 ±  0.099  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated             1000  avgt   15    19.344 ±  0.932  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated            10000  avgt   15   199.749 ± 18.892  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated           10  avgt   15     4.371 ±  0.014  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated          100  avgt   15    43.772 ±  0.159  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated         1000  avgt   15   437.625 ±  1.189  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated        10000  avgt   15  4370.616 ±  9.442  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class BatchInsertBenchmark {

    private static final String INSERT_SQL = "insert into test_table(foo) values(?)";
    private Connection con;

    @Param({ "10", "100", "1000", "10000" })
    private String batchSize;

    @Setup
    public void setup() throws SQLException {
        con = DriverManager.getConnection("jdbc:postgresql:testdb", "postgres", "postgres");
        try (Statement st = con.createStatement()) {
            st.executeUpdate("drop table  if exists test_table");
            st.executeUpdate("create table test_table(id bigserial, foo varchar)");
        }
    }

    @Benchmark
    public int[] batchInsertDefault() throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(INSERT_SQL)) {
            return batchInsert(pst);
        }
    }

    @Benchmark
    public int[] batchInsertReturnGenerated() throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            return batchInsert(pst);
        }
    }

    @Benchmark
    public int[] batchInsertNoGenerated() throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(INSERT_SQL,
                Statement.NO_GENERATED_KEYS)) {
            return batchInsert(pst);
        }
    }

    private int[] batchInsert(PreparedStatement pst) throws SQLException {
        for (int i = 0; i < Integer.parseInt(batchSize); i++) {
            pst.setString(1, "foo" + i);
            pst.addBatch();
        }
        return pst.executeBatch();
    }

    @TearDown
    public void tearDown() throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("drop table test_table");
        }
        con.close();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + BatchInsertBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
