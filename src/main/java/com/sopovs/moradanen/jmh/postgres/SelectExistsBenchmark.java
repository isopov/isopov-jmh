package com.sopovs.moradanen.jmh.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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

//Benchmark                        (distinctValues)  (rowsPerValue)  Mode  Cnt  Score   Error  Units
//SelectExistsBenchmark.select1                1000               1  avgt    5  0.070 ± 0.002  ms/op
//SelectExistsBenchmark.select1                1000              10  avgt    5  0.025 ± 0.005  ms/op
//SelectExistsBenchmark.select1                1000              50  avgt    5  0.024 ± 0.005  ms/op
//SelectExistsBenchmark.select1               10000               1  avgt    5  0.023 ± 0.003  ms/op
//SelectExistsBenchmark.select1               10000              10  avgt    5  0.024 ± 0.005  ms/op
//SelectExistsBenchmark.select1               10000              50  avgt    5  0.024 ± 0.004  ms/op
//SelectExistsBenchmark.selectAll              1000               1  avgt    5  0.024 ± 0.007  ms/op
//SelectExistsBenchmark.selectAll              1000              10  avgt    5  0.026 ± 0.007  ms/op
//SelectExistsBenchmark.selectAll              1000              50  avgt    5  0.035 ± 0.006  ms/op
//SelectExistsBenchmark.selectAll             10000               1  avgt    5  0.026 ± 0.005  ms/op
//SelectExistsBenchmark.selectAll             10000              10  avgt    5  0.027 ± 0.007  ms/op
//SelectExistsBenchmark.selectAll             10000              50  avgt    5  0.036 ± 0.005  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class SelectExistsBenchmark {
	// State
	private Connection con;

	@Param({ "1000" })
	private int distinctValues;

	@Param({ "1" })
	private int rowsPerValue;

	@Setup
	public void setup() throws SQLException {
		con = DriverManager.getConnection("jdbc:postgresql://localhost/testdb", "postgres", "postgres");
		try (Statement st = con.createStatement()) {
			st.executeUpdate("drop table  if exists test_table");
			st.executeUpdate("create table test_table(id bigserial, foobar int)");
		}
		try (PreparedStatement pst = con.prepareStatement("insert into test_table(foobar) values(?)")) {
			for (int i = 0; i < distinctValues; i++) {
				for (int j = 0; j < rowsPerValue; j++) {
					pst.setInt(1, i);
					pst.addBatch();
				}
				pst.executeBatch();
			}
		}
		try (Statement st = con.createStatement()) {
			st.executeUpdate("create index test_index on test_table(foobar)");
		}
	}

	@Benchmark
	public boolean select1() throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select 1 from test_table where foobar=? limit 1")) {
			pst.setInt(1, ThreadLocalRandom.current().nextInt(distinctValues * 2));
			try (ResultSet res = pst.executeQuery()) {
				return res.next();
			}
		}
	}

	@Benchmark
	public boolean selectAll() throws SQLException {
		List<FooBar> result = new ArrayList<>();
		try (PreparedStatement pst = con.prepareStatement("select id,foobar  from test_table where foobar=?")) {
			pst.setInt(1, ThreadLocalRandom.current().nextInt(distinctValues * 2));
			try (ResultSet res = pst.executeQuery()) {
				while (res.next()) {
					result.add(new FooBar(res.getLong(1), res.getInt(2)));
				}
			}
		}
		return result.isEmpty();
	}

	private static final class FooBar {
		private final long id;
		private final int foobar;

		public FooBar(long id, int foobar) {
			this.id = id;
			this.foobar = foobar;
		}

	}

	@TearDown
	public void tearDown() throws SQLException {
		try (Statement st = con.createStatement()) {
			st.executeUpdate("drop table test_table");
		}
		con.close();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + SelectExistsBenchmark.class.getSimpleName() + ".*").build();

		new Runner(opt).run();
	}

}
