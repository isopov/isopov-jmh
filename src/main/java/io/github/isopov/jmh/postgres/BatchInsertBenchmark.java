package io.github.isopov.jmh.postgres;

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

//For postgresql in the local network
//Benchmark                                        (batchSize)  Mode  Cnt   Score    Error  Units
//BatchInsertBenchmark.batchInsertDefault                    1  avgt   15   1.208 ±  0.183  ms/op
//BatchInsertBenchmark.batchInsertDefault                    2  avgt   15   1.324 ±  0.029  ms/op
//BatchInsertBenchmark.batchInsertDefault                    4  avgt   15   1.541 ±  0.153  ms/op
//BatchInsertBenchmark.batchInsertDefault                    8  avgt   15   1.950 ±  0.439  ms/op
//BatchInsertBenchmark.batchInsertDefault                   16  avgt   15   2.694 ±  0.564  ms/op
//BatchInsertBenchmark.batchInsertDefault                   32  avgt   15   2.999 ±  0.071  ms/op
//BatchInsertBenchmark.batchInsertDefault                   64  avgt   15   5.212 ±  1.218  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated                1  avgt   15   2.183 ±  0.741  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated                2  avgt   15   1.944 ±  0.472  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated                4  avgt   15   2.348 ±  0.618  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated                8  avgt   15   1.654 ±  0.110  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated               16  avgt   15   2.425 ±  0.408  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated               32  avgt   15   3.042 ±  0.206  ms/op
//BatchInsertBenchmark.batchInsertNoGenerated               64  avgt   15   4.219 ±  0.478  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated            1  avgt   15   2.324 ±  0.355  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated            2  avgt   15   3.658 ±  0.549  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated            4  avgt   15   6.967 ±  0.914  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated            8  avgt   15  13.111 ±  2.741  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated           16  avgt   15  21.976 ±  0.874  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated           32  avgt   15  42.193 ±  1.495  ms/op
//BatchInsertBenchmark.batchInsertReturnGenerated           64  avgt   15  98.080 ± 12.128  ms/op

@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class BatchInsertBenchmark {

	private static final String INSERT_SQL = "insert into test_table(foo) values(?)";
	private Connection con;

	@Param({ "1", "2", "4", "8", "16", "32", "64" })
	private String batchSize;

	@Setup
	public void setup() throws SQLException {
		// Preferably remote host - for real batch complexity of writing to db
		// is linear regarding batch size and almost constant regarding
		// network-calls. But for pseudo-batch where for every row there is
		// separate network-call complexity is also linear regarding
		// network-calls - so we need to be network-bound and database host must
		// not be local.
		con = DriverManager.getConnection("jdbc:postgresql://some_host/test_db", "test_user", "test_pas");
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
		try (PreparedStatement pst = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
			return batchInsert(pst);
		}
	}

	@Benchmark
	public int[] batchInsertNoGenerated() throws SQLException {
		try (PreparedStatement pst = con.prepareStatement(INSERT_SQL, Statement.NO_GENERATED_KEYS)) {
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
		Options opt = new OptionsBuilder().include(".*" + BatchInsertBenchmark.class.getSimpleName() + ".*").build();

		new Runner(opt).run();
	}

}
