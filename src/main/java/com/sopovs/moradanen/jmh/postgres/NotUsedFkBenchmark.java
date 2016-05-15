package com.sopovs.moradanen.jmh.postgres;

//Benchmark                  (fks)  (values)  Mode  Cnt   Score   Error  Units

//NotUsedFkBenchmark.delete      0    100000  avgt    5   0.873 ± 0.002  ms/op
//NotUsedFkBenchmark.delete   2000    100000  avgt    5  89.341 ± 4.680  ms/op
//NotUsedFkBenchmark.insert      0    100000  avgt    5   0.436 ± 0.006  ms/op
//NotUsedFkBenchmark.insert   2000    100000  avgt    5   0.467 ± 0.015  ms/op
//NotUsedFkBenchmark.update      0    100000  avgt    5   0.442 ± 0.050  ms/op
//NotUsedFkBenchmark.update   2000    100000  avgt    5   0.812 ± 0.194  ms/op

import static com.google.common.base.Preconditions.checkState;

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
 * @author isopov
 * 
 *         Recreation of the
 *         http://bonesmoses.org/2014/05/14/foreign-keys-are-not-free/
 * 
 *         With extensions based on the discussion in
 *         https://www.facebook.com/groups/postgresql
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

    // To see difference from such benchmark (where prepared_statements
    // introduce significant overhead) we need more fks
    @Param({ "0", "2000" })
    private int fks;

    @Param({ "100000" })
    private int values;

    @Setup
    public void setup() throws SQLException {
        con = DriverManager.getConnection("jdbc:postgresql://localhost/testdb", "postgres", "postgres");
        try (Statement st = con.createStatement()) {
            for (int i = 0; i < 2000; i++) {
                st.executeUpdate("drop table if exists test_table_ref_" + i);
            }
            st.executeUpdate("drop table if exists test_table");
            st.executeUpdate("create table test_table(id bigserial primary key, junk text)");

            for (int i = 0; i < fks; i++) {
                st.executeUpdate("create table test_table_ref_" + i
                        + "(id bigint references test_table(id) ON UPDATE NO ACTION)");
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
    public int update() throws SQLException {

        try (PreparedStatement pst = con.prepareStatement("update test_table set junk=? where id=?")) {
            pst.setString(1, String.valueOf(r.nextInt()));
            pst.setLong(2, r.nextInt(values) + 1);
            return pst.executeUpdate();
        }

    }

    @Benchmark
    public int insert() throws SQLException {

        try (PreparedStatement pst = con.prepareStatement("insert into test_table(junk) values(?)")) {
            pst.setString(1, String.valueOf(r.nextInt()));
            return pst.executeUpdate();
        }

    }

    @Benchmark
    public void delete() throws SQLException {

        long newID = r.nextInt(values) + values;

        try (PreparedStatement pst = con.prepareStatement("insert into test_table(id, junk) values(?,?)")) {
            pst.setLong(1, newID);
            pst.setString(2, String.valueOf(r.nextInt()));
            checkState(1 == pst.executeUpdate());
        }

        try (PreparedStatement pst = con.prepareStatement("delete from test_table where id=?")) {
            pst.setLong(1, newID);
            checkState(1 == pst.executeUpdate());
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + NotUsedFkBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
