package com.sopovs.moradanen.jmh.ycsb;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class RandomByteIteratorBenchmark {

	@State(Scope.Thread)
	@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
	@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
	@Fork(3)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public static class ToArray {
		@Param({ "100", "1000", "10000" })
		public int size;

		@Param({ "simple", "ycsb" })
		public String type;

		private ByteIterator iterator;

		@Setup
		public void setup() {
			switch (type) {
			case "simple":
				iterator = new SimpleRandomByteIterator(size);
				break;
			case "ycsb":
				iterator = new RandomByteIterator(size);
				break;
			default:
				throw new IllegalStateException();
			}
		}

		@Benchmark
		public byte[] get() {
			iterator.reset();
			return iterator.toArray();
		}
	}
	
	@State(Scope.Thread)
	@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
	@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
	@Fork(3)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public static class NextByte{
		@Param({ "simple", "ycsb" })
		public String type;
		
		private ByteIterator iterator;

		@Setup
		public void setup() {
			switch (type) {
			case "simple":
				iterator = new SimpleRandomByteIterator(Long.MAX_VALUE);
				break;
			case "ycsb":
				iterator = new RandomByteIterator(Long.MAX_VALUE);
				break;
			default:
				throw new IllegalStateException();
			}
		}
		
		@Benchmark
		public byte get() {
			return iterator.nextByte();
		}
		
	}
	

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + RandomByteIteratorBenchmark.class.getSimpleName() + ".*")
				.build();

		new Runner(opt).run();
	}

	public static abstract class ByteIterator implements Iterator<Byte> {

		@Override
		public abstract boolean hasNext();

		@Override
		public Byte next() {
			throw new UnsupportedOperationException();
		}

		public abstract byte nextByte();

		/** @return byte offset immediately after the last valid byte */
		public int nextBuf(byte[] buf, int bufOff) {
			int sz = bufOff;
			while (sz < buf.length && hasNext()) {
				buf[sz] = nextByte();
				sz++;
			}
			return sz;
		}

		public abstract long bytesLeft();

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Resets the iterator so that it can be consumed again. Not all implementations
		 * support this call.
		 * 
		 * @throws UnsupportedOperationException
		 *             if the implementation hasn't implemented the method.
		 */
		public void reset() {
			throw new UnsupportedOperationException();
		}

		/** Consumes remaining contents of this object, and returns them as a string. */
		public String toString() {
			Charset cset = Charset.forName("UTF-8");
			CharBuffer cb = cset.decode(ByteBuffer.wrap(this.toArray()));
			return cb.toString();
		}

		/**
		 * Consumes remaining contents of this object, and returns them as a byte array.
		 */
		public byte[] toArray() {
			long left = bytesLeft();
			if (left != (int) left) {
				throw new ArrayIndexOutOfBoundsException("Too much data to fit in one array!");
			}
			byte[] ret = new byte[(int) left];
			int off = 0;
			while (off < ret.length) {
				off = nextBuf(ret, off);
			}
			return ret;
		}

	}

	public static class RandomByteIterator extends ByteIterator {
		private final long len;
		private long off;
		private int bufOff;
		private byte[] buf;

		@Override
		public boolean hasNext() {
			return (off + bufOff) < len;
		}

		private void fillBytesImpl(byte[] buffer, int base) {
			int bytes = ThreadLocalRandom.current().nextInt();

			switch (buffer.length - base) {
			default:
				buffer[base + 5] = (byte) (((bytes >> 25) & 95) + ' ');
			case 5:
				buffer[base + 4] = (byte) (((bytes >> 20) & 63) + ' ');
			case 4:
				buffer[base + 3] = (byte) (((bytes >> 15) & 31) + ' ');
			case 3:
				buffer[base + 2] = (byte) (((bytes >> 10) & 95) + ' ');
			case 2:
				buffer[base + 1] = (byte) (((bytes >> 5) & 63) + ' ');
			case 1:
				buffer[base + 0] = (byte) (((bytes) & 31) + ' ');
			case 0:
				break;
			}
		}

		private void fillBytes() {
			if (bufOff == buf.length) {
				fillBytesImpl(buf, 0);
				bufOff = 0;
				off += buf.length;
			}
		}

		public RandomByteIterator(long len) {
			this.len = len;
			this.buf = new byte[6];
			this.bufOff = buf.length;
			fillBytes();
			this.off = 0;
		}

		public byte nextByte() {
			fillBytes();
			bufOff++;
			return buf[bufOff - 1];
		}

		@Override
		public int nextBuf(byte[] buffer, int bufOffset) {
			int ret;
			if (len - off < buffer.length - bufOffset) {
				ret = (int) (len - off);
			} else {
				ret = buffer.length - bufOffset;
			}
			int i;
			for (i = 0; i < ret; i += 6) {
				fillBytesImpl(buffer, i + bufOffset);
			}
			off += ret;
			return ret + bufOffset;
		}

		@Override
		public long bytesLeft() {
			return len - off - bufOff;
		}

		@Override
		public void reset() {
			off = 0;
		}
	}

	public static class SimpleRandomByteIterator extends ByteIterator {
		private final long len;
		private long off;

		@Override
		public boolean hasNext() {
			return off < len;
		}

		public SimpleRandomByteIterator(long len) {
			this.len = len;
			this.off = 0;
		}

		public byte nextByte() {
			off++;
			return (byte) ThreadLocalRandom.current().nextInt();
		}

		@Override
		public long bytesLeft() {
			return len - off;
		}

		@Override
		public byte[] toArray() {
			byte[] result = new byte[(int) (len - off)];
			ThreadLocalRandom.current().nextBytes(result);
			off = len;
			return result;
		}

		@Override
		public void reset() {
			off = 0;
		}
	}
}
