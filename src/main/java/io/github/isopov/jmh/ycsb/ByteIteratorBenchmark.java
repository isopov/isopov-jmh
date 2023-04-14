package io.github.isopov.jmh.ycsb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

//ByteIteratorBenchmark.NextByte.get     N/A  simple-random  avgt   15       4.157 ±    0.106  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A    ycsb-random  avgt   15       4.690 ±    0.031  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A   simple-array  avgt   15       3.652 ±    0.035  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A     ycsb-array  avgt   15       3.660 ±    0.050  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A  simple-stream  avgt   15      18.109 ±    0.054  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A    ycsb-stream  avgt   15      18.141 ±    0.046  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A  simple-string  avgt   15       3.441 ±    0.024  ns/op
//ByteIteratorBenchmark.NextByte.get     N/A    ycsb-string  avgt   15       3.420 ±    0.027  ns/op
//ByteIteratorBenchmark.ToArray.get      100  simple-random  avgt   15     169.133 ±    1.369  ns/op
//ByteIteratorBenchmark.ToArray.get      100    ycsb-random  avgt   15      96.131 ±    1.093  ns/op
//ByteIteratorBenchmark.ToArray.get      100   simple-array  avgt   15      22.545 ±    0.199  ns/op
//ByteIteratorBenchmark.ToArray.get      100     ycsb-array  avgt   15      97.380 ±    1.756  ns/op
//ByteIteratorBenchmark.ToArray.get      100  simple-stream  avgt   15      43.677 ±    0.351  ns/op
//ByteIteratorBenchmark.ToArray.get      100    ycsb-stream  avgt   15    1927.683 ±   24.593  ns/op
//ByteIteratorBenchmark.ToArray.get      100  simple-string  avgt   15      47.117 ±    0.423  ns/op
//ByteIteratorBenchmark.ToArray.get      100    ycsb-string  avgt   15     130.230 ±    1.266  ns/op
//ByteIteratorBenchmark.ToArray.get     1000  simple-random  avgt   15    1399.951 ±   10.556  ns/op
//ByteIteratorBenchmark.ToArray.get     1000    ycsb-random  avgt   15     898.596 ±   49.941  ns/op
//ByteIteratorBenchmark.ToArray.get     1000   simple-array  avgt   15     179.352 ±    3.746  ns/op
//ByteIteratorBenchmark.ToArray.get     1000     ycsb-array  avgt   15     935.627 ±    9.426  ns/op
//ByteIteratorBenchmark.ToArray.get     1000  simple-stream  avgt   15     196.419 ±    4.292  ns/op
//ByteIteratorBenchmark.ToArray.get     1000    ycsb-stream  avgt   15   19397.952 ±   56.055  ns/op
//ByteIteratorBenchmark.ToArray.get     1000  simple-string  avgt   15     424.087 ±    6.372  ns/op
//ByteIteratorBenchmark.ToArray.get     1000    ycsb-string  avgt   15    1053.745 ±   10.549  ns/op
//ByteIteratorBenchmark.ToArray.get    10000  simple-random  avgt   15   12817.802 ±   83.168  ns/op
//ByteIteratorBenchmark.ToArray.get    10000    ycsb-random  avgt   15    7504.547 ±   59.974  ns/op
//ByteIteratorBenchmark.ToArray.get    10000   simple-array  avgt   15     865.759 ±   15.291  ns/op
//ByteIteratorBenchmark.ToArray.get    10000     ycsb-array  avgt   15    8364.259 ±   91.066  ns/op
//ByteIteratorBenchmark.ToArray.get    10000  simple-stream  avgt   15     871.129 ±   11.263  ns/op
//ByteIteratorBenchmark.ToArray.get    10000    ycsb-stream  avgt   15  192780.211 ± 1401.873  ns/op
//ByteIteratorBenchmark.ToArray.get    10000  simple-string  avgt   15    3415.518 ±   41.562  ns/op
//ByteIteratorBenchmark.ToArray.get    10000    ycsb-string  avgt   15    9682.347 ±   91.884  ns/op

public class ByteIteratorBenchmark {

	public static final String SIMPLE_RANDOM = "simple-random";
	public static final String YCSB_RANDOM = "ycsb-random";

	public static final String SIMPLE_ARRAY = "simple-array";
	public static final String YCSB_ARRAY = "ycsb-array";

	public static final String SIMPLE_STREAM = "simple-stream";
	public static final String YCSB_STREAM = "ycsb-stream";

	public static final String SIMPLE_STRING = "simple-string";
	public static final String YCSB_STRING = "ycsb-string";

	@State(Scope.Thread)
	@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
	@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
	@Fork(3)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public static abstract class AbstractByteIteratorBenchmark {

		@Param({ SIMPLE_RANDOM, YCSB_RANDOM, SIMPLE_ARRAY, YCSB_ARRAY, SIMPLE_STREAM, YCSB_STREAM, SIMPLE_STRING,
				YCSB_STRING })
		public String type;

		protected ByteIterator iterator;

		@Setup
		public void setup() {
			switch (type) {
			case SIMPLE_RANDOM:
				iterator = new SimpleRandomByteIterator(getSize());
				break;
			case YCSB_RANDOM:
				iterator = new RandomByteIterator(getSize());
				break;

			case SIMPLE_ARRAY: {
				byte[] bytes = new byte[(int) getSize()];
				ThreadLocalRandom.current().nextBytes(bytes);
				iterator = new SimpleByteArrayByteIterator(bytes);
				break;
			}
			case YCSB_ARRAY: {
				byte[] bytes = new byte[(int) getSize()];
				ThreadLocalRandom.current().nextBytes(bytes);
				iterator = new ByteArrayByteIterator(bytes);
				break;
			}

			case SIMPLE_STREAM: {
				byte[] bytes = new byte[(int) getSize()];
				ThreadLocalRandom.current().nextBytes(bytes);
				iterator = new SimpleInputStreamByteIterator(new ByteArrayInputStream(bytes), getSize());
				break;
			}
			case YCSB_STREAM: {
				byte[] bytes = new byte[(int) getSize()];
				ThreadLocalRandom.current().nextBytes(bytes);
				iterator = new InputStreamByteIterator(new ByteArrayInputStream(bytes), getSize());
				break;
			}

			case SIMPLE_STRING: {
				StringBuilder builder = new StringBuilder();
				int size = (int) getSize();
				for (int i = 0; i < size; i++) {
					builder.append((char) ThreadLocalRandom.current().nextInt());
				}
				iterator = new SimpleStringByteIterator(builder.toString());
				break;
			}
			case YCSB_STRING: {
				StringBuilder builder = new StringBuilder();
				int size = (int) getSize();
				for (int i = 0; i < size; i++) {
					builder.append((char) ThreadLocalRandom.current().nextInt());
				}
				iterator = new StringByteIterator(builder.toString());
				break;
			}
			default:
				throw new IllegalStateException();
			}
		}

		protected abstract long getSize();

	}

	public static class ToArray extends AbstractByteIteratorBenchmark {
		@Param({ "100", "1000", "10000" })
		public long size;

		@Benchmark
		public byte[] get() {
			iterator.reset();
			return iterator.toArray();
		}

		@Override
		protected long getSize() {
			return size;
		}
	}

	public static class NextByte extends AbstractByteIteratorBenchmark {

		@Benchmark
		public byte get() {
			if (!iterator.hasNext()) {
				iterator.reset();
			}
			return iterator.nextByte();
		}

		@Override
		protected long getSize() {
			return 1000_000;
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(".*" + ByteIteratorBenchmark.class.getSimpleName() + ".*").build();

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

	public static class ByteArrayByteIterator extends ByteIterator {
		private final int originalOffset;
		private byte[] str;
		private int off;
		private final int len;

		public ByteArrayByteIterator(byte[] s) {
			this.str = s;
			this.off = 0;
			this.len = s.length;
			originalOffset = 0;
		}

		public ByteArrayByteIterator(byte[] s, int off, int len) {
			this.str = s;
			this.off = off;
			this.len = off + len;
			originalOffset = off;
		}

		@Override
		public boolean hasNext() {
			return off < len;
		}

		@Override
		public byte nextByte() {
			byte ret = str[off];
			off++;
			return ret;
		}

		@Override
		public long bytesLeft() {
			return len - off;
		}

		@Override
		public void reset() {
			off = originalOffset;
		}
	}

	public static class SimpleByteArrayByteIterator extends ByteIterator {
		private final int originalOffset;
		private byte[] str;
		private int off;
		private final int len;

		public SimpleByteArrayByteIterator(byte[] s) {
			this.str = s;
			this.off = 0;
			this.len = s.length;
			originalOffset = 0;
		}

		public SimpleByteArrayByteIterator(byte[] s, int off, int len) {
			this.str = s;
			this.off = off;
			this.len = off + len;
			originalOffset = off;
		}

		@Override
		public boolean hasNext() {
			return off < len;
		}

		@Override
		public byte nextByte() {
			byte ret = str[off];
			off++;
			return ret;
		}

		@Override
		public long bytesLeft() {
			return len - off;
		}

		@Override
		public void reset() {
			off = originalOffset;
		}

		@Override
		public byte[] toArray() {
			int size = (int) bytesLeft();
			byte[] bytes = new byte[size];
			System.arraycopy(str, off, bytes, 0, size);
			off = len;
			return bytes;
		}
	}

	public static class InputStreamByteIterator extends ByteIterator {
		private final long len;
		private InputStream ins;
		private long off;
		private final boolean resetable;

		public InputStreamByteIterator(InputStream ins, long len) {
			this.len = len;
			this.ins = ins;
			off = 0;
			resetable = ins.markSupported();
			if (resetable) {
				ins.mark((int) len);
			}
		}

		@Override
		public boolean hasNext() {
			return off < len;
		}

		@Override
		public byte nextByte() {
			int ret;
			try {
				ret = ins.read();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			if (ret == -1) {
				throw new IllegalStateException("Past EOF!");
			}
			off++;
			return (byte) ret;
		}

		@Override
		public long bytesLeft() {
			return len - off;
		}

		@Override
		public void reset() {
			if (resetable) {
				try {
					ins.reset();
					ins.mark((int) len);
					off = 0;
				} catch (IOException e) {
					throw new IllegalStateException("Failed to reset the input stream", e);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

	}

	public static class SimpleInputStreamByteIterator extends ByteIterator {
		private final long len;
		private InputStream ins;
		private long off;
		private final boolean resetable;

		public SimpleInputStreamByteIterator(InputStream ins, long len) {
			this.len = len;
			this.ins = ins;
			off = 0;
			resetable = ins.markSupported();
			if (resetable) {
				ins.mark((int) len);
			}
		}

		@Override
		public byte[] toArray() {
			int size = (int) bytesLeft();
			byte[] bytes = new byte[size];
			try {
				if (ins.read(bytes) < size) {
					throw new IllegalStateException("Past EOF!");
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			off = len;
			return bytes;
		}

		@Override
		public boolean hasNext() {
			return off < len;
		}

		@Override
		public byte nextByte() {
			int ret;
			try {
				ret = ins.read();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			if (ret == -1) {
				throw new IllegalStateException("Past EOF!");
			}
			off++;
			return (byte) ret;
		}

		@Override
		public long bytesLeft() {
			return len - off;
		}

		@Override
		public void reset() {
			if (resetable) {
				try {
					ins.reset();
					ins.mark((int) len);
					off = 0;
				} catch (IOException e) {
					throw new IllegalStateException("Failed to reset the input stream", e);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}
	}

	public static class StringByteIterator extends ByteIterator {
		private String str;
		private int off;

		public StringByteIterator(String s) {
			this.str = s;
			this.off = 0;
		}

		@Override
		public boolean hasNext() {
			return off < str.length();
		}

		@Override
		public byte nextByte() {
			byte ret = (byte) str.charAt(off);
			off++;
			return ret;
		}

		@Override
		public long bytesLeft() {
			return str.length() - off;
		}

		@Override
		public void reset() {
			off = 0;
		}
	}

	public static class SimpleStringByteIterator extends ByteIterator {
		private String str;
		private int off;

		public SimpleStringByteIterator(String s) {
			this.str = s;
			this.off = 0;
		}

		@Override
		public byte[] toArray() {
			byte[] bytes = new byte[(int) bytesLeft()];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) str.charAt(off + i);
			}
			off = str.length();
			return bytes;
		}

		@Override
		public boolean hasNext() {
			return off < str.length();
		}

		@Override
		public byte nextByte() {
			byte ret = (byte) str.charAt(off);
			off++;
			return ret;
		}

		@Override
		public long bytesLeft() {
			return str.length() - off;
		}

		@Override
		public void reset() {
			off = 0;
		}
	}

}
