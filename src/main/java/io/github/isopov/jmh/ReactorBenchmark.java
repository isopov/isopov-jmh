package io.github.isopov.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import reactor.core.publisher.Flux;

//Benchmark                                              Score    Error   Units
//ReactorBenchmark.fluxVersion                          16.368 ±  0.742   ms/op
//ReactorBenchmark.fluxVersion:·gc.alloc.rate.norm 6264914.246 ± 38.917    B/op
//ReactorBenchmark.listVersion                          14.692 ±  0.974   ms/op
//ReactorBenchmark.listVersion:·gc.alloc.rate.norm 3206097.468 ± 39.057    B/op
@State(Scope.Benchmark)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ReactorBenchmark {

	@Param({ "100" })
	public int books;

	@Param({ "1000" })
	public int bookSize;

	@Param({ "10" })
	public int shelves;

	public List<String> lines;

	@Setup
	public void setup() {
		lines = new ArrayList<>();
		for (int shelf = 0; shelf < shelves; shelf++) {
			for (int book = 0; book < books; book++) {
				lines.add("Title: " + UUID.randomUUID());
				lines.add("Author: " + UUID.randomUUID());
				for (int line = 0; line < bookSize; line++) {
					lines.add(UUID.randomUUID().toString());
				}
			}
			lines.add("##BOOKSHELF##");
		}
	}

	@Benchmark
	public void listVersion(Blackhole bh) {
		listVersion(bh::consume);
	}

	void listVersion(Consumer<List<String>> booksConsumer) {
		List<String> books = new ArrayList<>();
		String title = null;
		for (String line : lines) {
			if (line.startsWith("Title: ")) {
				title = line.replaceFirst("Title: ", "");
			} else if (line.startsWith("Author: ")) {
				String author = line.replaceFirst("Author: ", " by ");
				books.add(title.concat(author));
				title = null;
			} else if (line.equalsIgnoreCase("##BOOKSHELF##")) {
				booksConsumer.accept(books);
				books = new ArrayList<>();
			}
		}
	}

	@Benchmark
	public void fluxVersion(Blackhole bh) {
		fluxVersion(bh::consume);
	}
	
	void fluxVersion(Consumer<List<String>> booksConsumer) {
		Flux.fromIterable(lines)
		.filter(s -> s.startsWith("Title: ") || s.startsWith("Author: ")
				|| s.equalsIgnoreCase("##BOOKSHELF##"))
		.map(s -> s.replaceFirst("Title: ", ""))
		.map(s -> s.replaceFirst("Author: ", " by "))
		.windowWhile(s -> !s.contains("##"))
		.flatMap(
			bookshelf -> bookshelf
				.window(2)
				.flatMap(bookInfo -> bookInfo.reduce(String::concat))
				.collectList()
		)
		.filter(books -> !books.isEmpty())
		.doOnNext(booksConsumer)
		.blockLast();
	}
	
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReactorBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
	

}
