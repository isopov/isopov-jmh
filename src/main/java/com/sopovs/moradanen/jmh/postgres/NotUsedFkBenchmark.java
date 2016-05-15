package com.sopovs.moradanen.jmh.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author isopov Recreation of the
 *         http://bonesmoses.org/2014/05/14/foreign-keys-are-not-free/
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class NotUsedFkBenchmark {

    // State
    private Connection con;
    private Random r = new Random();

    // @Param({ "0", "1", "2", "5", "10", "20" })
    @Param({ "0", "20" })
    private int fks;

    // @Param({ "100", "1000", "10000", "100000" })
    @Param({ "100000" })
    private int values;

    @Setup
    public void setup() throws SQLException {
        con = DriverManager.getConnection("jdbc:postgresql://localhost/testdb", "postgres", "postgres");
        try (Statement st = con.createStatement()) {
            st.executeUpdate("drop table if exists test_table cascade");
            for (int i = 0; i < 20; i++) {
                st.executeUpdate("drop table if exists test_table_ref_" + i + " cascade");
            }
            st.executeUpdate("create table test_table(id bigserial primary key, junk text)");

            for (int i = 0; i < fks; i++) {
                st.executeUpdate("create table test_table_ref_" + i + "(id bigint references test_table(id))");
            }
        }
        try (PreparedStatement pst = con.prepareStatement("insert into test_table(junk) values(?)")) {
            for (int i = 0; i < values; i++) {
                pst.setString(1, String.valueOf(r.nextInt()));
                pst.addBatch();
            }
            pst.executeBatch();
        }

    }

    @Benchmark
    public int insert() throws SQLException {

        try (PreparedStatement pst = con.prepareStatement("update test_table set junk=? where id=?")) {
            pst.setString(1, String.valueOf(r.nextInt()));
            pst.setLong(2, r.nextInt(values) + 1);
            return pst.executeUpdate();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + NotUsedFkBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
