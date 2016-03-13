package com.sopovs.moradanen.jmh.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

//Benchmark                                 (distinctValues)  (index)  (rowsPerValue)  Mode  Cnt    Score     Error  Units
//LooseIndexScanBenchmark.selectDistinct                 100     true               1  avgt    5    0.181 ±   0.005  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              10  avgt    5    0.402 ±   0.057  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              20  avgt    5    0.662 ±   0.090  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              30  avgt    5    0.880 ±   0.137  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              40  avgt    5    1.188 ±   0.458  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              50  avgt    5    1.404 ±   0.193  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              60  avgt    5    1.644 ±   0.148  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              70  avgt    5    1.891 ±   0.213  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              80  avgt    5    2.045 ±   0.200  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true              90  avgt    5    2.277 ±   0.137  ms/op
//LooseIndexScanBenchmark.selectDistinct                 100     true             100  avgt    5    2.471 ±   0.308  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true               1  avgt    5    0.644 ±   0.065  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              10  avgt    5    1.814 ±   0.210  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              20  avgt    5    2.964 ±   0.084  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              30  avgt    5    4.200 ±   0.461  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              40  avgt    5    5.275 ±   0.220  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              50  avgt    5    6.469 ±   0.185  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              60  avgt    5    7.753 ±   0.588  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              70  avgt    5    8.789 ±   0.293  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              80  avgt    5    9.918 ±   0.256  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true              90  avgt    5   11.052 ±   0.695  ms/op
//LooseIndexScanBenchmark.selectDistinct                 500     true             100  avgt    5   12.618 ±   0.635  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true               1  avgt    5    1.012 ±   0.211  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              10  avgt    5    3.353 ±   0.236  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              20  avgt    5    5.798 ±   0.573  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              30  avgt    5    8.627 ±   1.911  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              40  avgt    5   11.256 ±   2.935  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              50  avgt    5   13.098 ±   1.333  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              60  avgt    5   15.185 ±   1.363  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              70  avgt    5   18.009 ±   2.990  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              80  avgt    5   20.275 ±   1.973  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true              90  avgt    5   22.966 ±   2.812  ms/op
//LooseIndexScanBenchmark.selectDistinct                1000     true             100  avgt    5   24.581 ±   1.081  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true               1  avgt    5    1.855 ±   0.159  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              10  avgt    5    6.852 ±   0.997  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              20  avgt    5   10.973 ±   1.215  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              30  avgt    5   15.750 ±   2.452  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              40  avgt    5   20.197 ±   0.748  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              50  avgt    5   25.026 ±   0.465  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              60  avgt    5   29.569 ±   0.829  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              70  avgt    5   34.144 ±   2.286  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              80  avgt    5   38.992 ±   1.058  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true              90  avgt    5   43.313 ±   2.682  ms/op
//LooseIndexScanBenchmark.selectDistinct                2000     true             100  avgt    5   50.028 ±   5.278  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true               1  avgt    5    3.790 ±   0.495  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              10  avgt    5   15.907 ±   3.353  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              20  avgt    5   29.974 ±   9.340  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              30  avgt    5   39.264 ±   2.521  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              40  avgt    5   50.684 ±   1.326  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              50  avgt    5   61.733 ±   2.808  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              60  avgt    5   74.566 ±   2.768  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              70  avgt    5   91.381 ±  22.146  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              80  avgt    5  102.263 ±  29.126  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true              90  avgt    5  110.540 ±   3.167  ms/op
//LooseIndexScanBenchmark.selectDistinct                5000     true             100  avgt    5  123.511 ±   3.550  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true               1  avgt    5    7.708 ±   0.651  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              10  avgt    5   29.588 ±   1.346  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              20  avgt    5   53.400 ±   1.896  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              30  avgt    5   79.112 ±   2.596  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              40  avgt    5  104.520 ±   2.598  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              50  avgt    5  125.705 ±   8.217  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              60  avgt    5  152.307 ±   5.050  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              70  avgt    5  176.805 ±   4.371  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              80  avgt    5  198.094 ±   2.081  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true              90  avgt    5  238.231 ± 123.978  ms/op
//LooseIndexScanBenchmark.selectDistinct               10000     true             100  avgt    5  248.547 ±   8.221  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true               1  avgt    5    0.598 ±   0.139  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              10  avgt    5    0.989 ±   0.137  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              20  avgt    5    1.036 ±   0.169  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              30  avgt    5    1.054 ±   0.138  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              40  avgt    5    1.044 ±   0.149  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              50  avgt    5    1.056 ±   0.147  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              60  avgt    5    1.054 ±   0.239  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              70  avgt    5    1.048 ±   0.135  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              80  avgt    5    1.052 ±   0.226  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true              90  avgt    5    1.062 ±   0.139  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               100     true             100  avgt    5    1.113 ±   0.238  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true               1  avgt    5    4.271 ±   0.338  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              10  avgt    5    4.685 ±   0.385  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              20  avgt    5    4.711 ±   0.215  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              30  avgt    5    4.751 ±   0.092  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              40  avgt    5    4.686 ±   0.254  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              50  avgt    5    4.793 ±   0.335  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              60  avgt    5    4.781 ±   0.320  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              70  avgt    5    4.786 ±   0.294  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              80  avgt    5    4.751 ±   0.223  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true              90  avgt    5    4.804 ±   0.283  ms/op
//LooseIndexScanBenchmark.selectLooseIndex               500     true             100  avgt    5    4.836 ±   0.269  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true               1  avgt    5    8.418 ±   0.486  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              10  avgt    5    8.917 ±   0.576  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              20  avgt    5    9.049 ±   0.451  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              30  avgt    5    9.087 ±   0.493  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              40  avgt    5    9.164 ±   0.695  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              50  avgt    5    9.096 ±   0.635  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              60  avgt    5    9.196 ±   0.628  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              70  avgt    5    9.176 ±   0.388  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              80  avgt    5    9.277 ±   0.786  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true              90  avgt    5    9.296 ±   0.590  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              1000     true             100  avgt    5    9.258 ±   0.515  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true               1  avgt    5   17.094 ±   0.748  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              10  avgt    5   17.477 ±   1.046  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              20  avgt    5   17.569 ±   0.946  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              30  avgt    5   17.689 ±   0.815  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              40  avgt    5   17.902 ±   1.383  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              50  avgt    5   17.882 ±   0.707  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              60  avgt    5   18.339 ±   0.918  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              70  avgt    5   18.520 ±   0.330  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              80  avgt    5   18.506 ±   0.966  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true              90  avgt    5   18.525 ±   0.643  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              2000     true             100  avgt    5   18.721 ±   1.177  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true               1  avgt    5   43.772 ±  15.661  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              10  avgt    5   43.059 ±   0.932  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              20  avgt    5   43.103 ±   1.345  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              30  avgt    5   44.561 ±   1.418  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              40  avgt    5   44.325 ±   1.817  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              50  avgt    5   44.676 ±   0.848  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              60  avgt    5   45.401 ±   3.145  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              70  avgt    5   45.077 ±   1.312  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              80  avgt    5   44.920 ±   0.954  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true              90  avgt    5   45.243 ±   1.059  ms/op
//LooseIndexScanBenchmark.selectLooseIndex              5000     true             100  avgt    5   46.080 ±   2.612  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true               1  avgt    5   83.577 ±   2.977  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              10  avgt    5   85.124 ±   2.290  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              20  avgt    5   88.118 ±   1.589  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              30  avgt    5   88.657 ±   3.410  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              40  avgt    5   88.954 ±   4.664  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              50  avgt    5   89.341 ±   3.615  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              60  avgt    5   89.622 ±   2.725  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              70  avgt    5   89.140 ±   4.267  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              80  avgt    5   89.870 ±   2.352  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true              90  avgt    5   90.547 ±   2.998  ms/op
//LooseIndexScanBenchmark.selectLooseIndex             10000     true             100  avgt    5   92.009 ±   7.566  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class LooseIndexScanBenchmark {

    // State
    private Connection con;

    // @Param({ "10", "100", "1000" })
    @Param({ "100", "500", "1000", "2000", "5000", "10000" })
    private int distinctValues;

    // @Param({ "1", "2", "4", "10", "25", "50", "100" })
    @Param({ "1", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" })
    private int rowsPerValue;

    // @Param({ "true", "false" })
    @Param({ "true" })
    private boolean index;

    @Setup
    public void setup() throws SQLException {
        con = DriverManager.getConnection("jdbc:postgresql://localhost/testdb", "postgres", "postgres");
        try (Statement st = con.createStatement()) {
            st.executeUpdate("drop table if exists test_table");
            st.executeUpdate("create table test_table(id bigserial primary key, foobar int not null)");
        }
        try (PreparedStatement pst = con.prepareStatement("insert into test_table(foobar) values(?)")) {
            for (int i = 0; i < rowsPerValue; i++) {
                for (int j = 0; j < distinctValues; j++) {
                    pst.setInt(1, j);
                    pst.addBatch();
                }
                pst.executeBatch();
            }
        }
        if (index) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("create index test_table_idx on test_table(foobar)");
            }
        }
    }

    private static final String SIMPLE_DISTINCT = "select distinct foobar from test_table";

    private static final String LOOSE_INDEX = "WITH RECURSIVE t AS ("
            + " (SELECT foobar FROM test_table ORDER BY foobar LIMIT 1)"
            + " UNION ALL"
            + " SELECT (SELECT foobar FROM test_table WHERE foobar > t.foobar ORDER BY foobar LIMIT 1)"
            + " FROM t"
            + " WHERE t.foobar IS NOT NULL"
            + " )"
            + " SELECT foobar FROM t WHERE foobar IS NOT NULL";

    private int selectInternal(String query) throws SQLException {
        int result = 0;
        try (PreparedStatement pst = con.prepareStatement(query);
                ResultSet res = pst.executeQuery()) {
            while (res.next()) {
                result += res.getInt(1);
            }
        }
        return result;
    }

    @Benchmark
    public int selectDistinct() throws SQLException {
        return selectInternal(SIMPLE_DISTINCT);
    }

    @Benchmark
    public int selectLooseIndex() throws SQLException {
        return selectInternal(LOOSE_INDEX);
    }

    @TearDown
    public void tearDown() throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("drop table test_table");
        }
        con.close();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + LooseIndexScanBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
