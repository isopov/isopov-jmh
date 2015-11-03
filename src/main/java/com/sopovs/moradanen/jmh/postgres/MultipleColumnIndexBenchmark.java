package com.sopovs.moradanen.jmh.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

//Benchmark                                           (index)  Mode  Cnt  Score   Error  Units
//MultipleColumnIndexBenchmark.bench                   single  avgt    5  0.039 ± 0.004  ms/op
//MultipleColumnIndexBenchmark.bench             multi-unique  avgt    5  0.038 ± 0.010  ms/op
//MultipleColumnIndexBenchmark.bench          multi-nonunique  avgt    5  0.038 ± 0.005  ms/op
//MultipleColumnIndexBenchmark.bench                 no-index  avgt    5  6.656 ± 1.191  ms/op
//MultipleColumnIndexBenchmark.bench     reverse-multi-unique  avgt    5  6.579 ± 0.661  ms/op
//MultipleColumnIndexBenchmark.bench  reverse-multi-nonunique  avgt    5  6.886 ± 1.285  ms/op
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class MultipleColumnIndexBenchmark {

	private static final int BATCH_SIZE = 1000;

	private static final int FOO_VALUES = 10000;
	private static final int BAR_VALUES = 10;

	// State
	private Connection con;

	@Param({ "single", "multi-unique", "multi-nonunique", "no-index", "reverse-multi-unique",
			"reverse-multi-nonunique" })
	private String index;

	@Setup
	public void setup() throws SQLException {
		con = DriverManager.getConnection("jdbc:postgresql://localhost/testdb", "postgres", "postgres");
		switch (index) {
		case "single":
			prepareSingleIndexTable();
			break;
		case "multi-unique":
			prepareMultiUniqueIndexTable();
			break;
		case "multi-nonunique":
			prepareMultiNonUniqueIndexTable();
			break;
		case "no-index":
			prepareNoIndexTable();
			break;
		case "reverse-multi-unique":
			prepareReverseUniqueIndexTable();
			break;
		case "reverse-multi-nonunique":
			prepareReverseNonUniqueIndexTable();
			break;
		default:
			throw new IllegalStateException();
		}
	}

	@Benchmark
	public List<Long> bench() throws SQLException {
		List<Long> result = new ArrayList<>();
		try (PreparedStatement pst = con.prepareStatement("select id from test_table where foo=?")) {
			pst.setInt(1, ThreadLocalRandom.current().nextInt(FOO_VALUES));
			try (ResultSet res = pst.executeQuery()) {
				while (res.next()) {
					result.add(res.getLong(1));
				}
			}
		}
		return result;
	}

	private void prepareNoIndexTable() throws SQLException {
		createTable();
		populateData();
	}

	private void prepareMultiUniqueIndexTable() throws SQLException {
		createTable();
		try (Statement st = con.createStatement()) {
			st.executeUpdate("create unique index test_index on test_table(foo, bar);");
		}
		populateData();
	}

	private void prepareMultiNonUniqueIndexTable() throws SQLException {
		createTable();
		try (Statement st = con.createStatement()) {
			st.executeUpdate("create index test_index on test_table(foo, bar);");
		}
		populateData();
	}

	private void prepareReverseUniqueIndexTable() throws SQLException {
		createTable();
		try (Statement st = con.createStatement()) {
			st.executeUpdate("create unique index test_index on test_table(bar, foo);");
		}
		populateData();
	}

	private void prepareReverseNonUniqueIndexTable() throws SQLException {
		createTable();
		try (Statement st = con.createStatement()) {
			st.executeUpdate("create index test_index on test_table(bar, foo);");
		}
		populateData();
	}

	private void prepareSingleIndexTable() throws SQLException {
		createTable();
		try (Statement st = con.createStatement()) {
			st.executeUpdate("create index test_index on test_table(foo);");
		}
		populateData();
	}

	private void populateData() throws SQLException {
		List<FooBar> inputs = new ArrayList<>(FOO_VALUES * BAR_VALUES);
		for (int i = 0; i < FOO_VALUES; i++) {
			for (int j = 0; j < BAR_VALUES; j++) {
				inputs.add(new FooBar(i, j));
			}
		}
		Collections.shuffle(inputs, new Random(228L));

		try (PreparedStatement pst = con.prepareStatement("insert into test_table(foo,bar) values(?,?)")) {
			for (int i = 0; i < FOO_VALUES * BAR_VALUES; i++) {
				FooBar fooBar = inputs.get(i);
				pst.setInt(1, fooBar.foo);
				pst.setInt(2, fooBar.bar);
				pst.addBatch();
				if (i != 0 && i % BATCH_SIZE == 0) {
					pst.executeBatch();
				}
			}
			pst.executeBatch();
		}

	}

	private static final class FooBar {
		private final int foo;
		private final int bar;

		public FooBar(int foo, int bar) {
			this.foo = foo;
			this.bar = bar;
		}
	}

	private void createTable() throws SQLException {
		try (Statement st = con.createStatement()) {
			st.executeUpdate("drop table  if exists test_table");
			st.executeUpdate("create table test_table(id bigserial, foo bigint, bar int)");
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
		Options opt = new OptionsBuilder().include(".*" + MultipleColumnIndexBenchmark.class.getSimpleName() + ".*")
				.build();

		new Runner(opt).run();
	}

}
