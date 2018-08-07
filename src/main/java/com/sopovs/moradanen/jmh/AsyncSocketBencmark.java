package com.sopovs.moradanen.jmh;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//Linux on laptop
//Benchmark                 (pings)         (type)  Mode  Cnt     Score     Error  Units
//AsyncSocketBencmark.ping        1       blocking  avgt   15    30.421 ±   1.444  us/op
//AsyncSocketBencmark.ping        1          async  avgt   15    86.396 ±   6.434  us/op
//AsyncSocketBencmark.ping        1  asyncHandlers  avgt   15    52.085 ±   3.315  us/op
//AsyncSocketBencmark.ping        1          netty  avgt   15    62.704 ±   1.878  us/op
//AsyncSocketBencmark.ping       10       blocking  avgt   15   303.597 ±   8.171  us/op
//AsyncSocketBencmark.ping       10          async  avgt   15   858.335 ±  69.169  us/op
//AsyncSocketBencmark.ping       10  asyncHandlers  avgt   15   769.964 ±  62.751  us/op
//AsyncSocketBencmark.ping       10          netty  avgt   15   456.217 ±  15.359  us/op
//AsyncSocketBencmark.ping      100       blocking  avgt   15  3038.410 ±  95.339  us/op
//AsyncSocketBencmark.ping      100          async  avgt   15  8399.860 ± 801.164  us/op
//AsyncSocketBencmark.ping      100  asyncHandlers  avgt   15  7668.647 ± 712.955  us/op
//AsyncSocketBencmark.ping      100          netty  avgt   15  4980.081 ± 626.558  us/op

//Windows on Desktop
//Benchmark                 (pings)         (type)  Mode  Cnt     Score     Error  Units
//AsyncSocketBencmark.ping        1       blocking  avgt   15    16,638 ±   1,798  us/op
//AsyncSocketBencmark.ping        1          async  avgt   15    22,580 ±   0,725  us/op
//AsyncSocketBencmark.ping        1  asyncHandlers  avgt   15    19,277 ±   1,081  us/op
//AsyncSocketBencmark.ping        1          netty  avgt   15    41,107 ±   1,429  us/op
//AsyncSocketBencmark.ping       10       blocking  avgt   15   170,164 ±  24,544  us/op
//AsyncSocketBencmark.ping       10          async  avgt   15   227,543 ±   9,525  us/op
//AsyncSocketBencmark.ping       10  asyncHandlers  avgt   15   174,062 ±  30,002  us/op
//AsyncSocketBencmark.ping       10          netty  avgt   15   224,965 ±  25,093  us/op
//AsyncSocketBencmark.ping      100       blocking  avgt   15  1558,272 ± 149,727  us/op
//AsyncSocketBencmark.ping      100          async  avgt   15  2320,424 ±  79,510  us/op
//AsyncSocketBencmark.ping      100  asyncHandlers  avgt   15  1570,477 ± 131,221  us/op
//AsyncSocketBencmark.ping      100          netty  avgt   15  2211,000 ± 543,706  us/op



@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
public class AsyncSocketBencmark {
    private static final byte[] PING = "ping".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PONG = "pong".getBytes(StandardCharsets.UTF_8);
    private static final int MESSAGE_SIZE = PING.length;

    @Param({"1", "10", "100"})
    public int pings;
    @Param({"blocking", "async", "asyncHandlers", "netty"})
    public String type;

    Server server;
    private PingClient pingClient;

    @Setup
    public void setup() throws Exception {
        server = new Server();
        server.start();
        switch (type) {
            case "blocking":
                pingClient = new BlockingPingClient(server.getPort());
                break;
            case "async":
                pingClient = new AsyncPingClient(server.getPort());
                break;
            case "asyncHandlers":
                pingClient = new AsyncHandlersPingClient(server.getPort());
                break;
            case "netty":
                pingClient = new NettyPingClient(server.getPort());
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @TearDown
    public void tearDown() throws Exception {
        pingClient.close();
        server.close();
    }

    @Benchmark
    public void ping() {
        pingClient.ping(pings);
    }

    static class Server extends Thread {
        final CompletableFuture<Void> started = new CompletableFuture<>();
        Socket socket;
        ServerSocket serverSocket;
        private final byte[] buffer = new byte[MESSAGE_SIZE];
        int count = 0;

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(0)) {
                this.serverSocket = serverSocket;
                started.complete(null);
                while (true) {
                    try (
                            Socket socket = serverSocket.accept();
                            DataInputStream in = new DataInputStream(socket.getInputStream());
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                        this.socket = socket;
                        while (true) {
                            int read = in.read(buffer);
                            if (read == -1) {
                                return;
                            }
                            if (read != MESSAGE_SIZE) {
                                throw new IllegalStateException();
                            }
                            if (!Arrays.equals(buffer, PING)) {
                                throw new IllegalStateException();
                            }
                            out.write(PONG);
                            count++;
                        }
                    } catch (EOFException e) {
                        //ignore
                    }
                }
            } catch (SocketException e) {
                if (!"Socket closed".equals(e.getMessage())) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getPort() {
            return serverSocket.getLocalPort();
        }

        public void close() throws IOException, InterruptedException {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
            this.join();
        }

        @Override
        public synchronized void start() {
            super.start();
            try {
                started.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    interface PingClient extends Closeable {
        void ping(int times);
    }


    static class BlockingPingClient implements PingClient {
        private final Socket socket;
        private final DataInputStream in;
        private final DataOutputStream out;
        private final byte[] buffer = new byte[MESSAGE_SIZE];

        BlockingPingClient(int port) throws IOException {
            socket = new Socket("localhost", port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }


        @Override
        public void ping(int times) {
            for (int i = 0; i < times; i++) {
                try {
                    out.write(PING);
                    out.flush();

                    if (MESSAGE_SIZE != in.read(buffer)) {
                        throw new IllegalStateException();
                    }
                    if (!Arrays.equals(buffer, PONG)) {
                        throw new IllegalStateException();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        @Override
        public void close() throws IOException {
            try (Socket closingSocket = socket;
                 DataInputStream closingIn = in;
                 DataOutputStream closingOut = out) {
                //empty
            }
        }
    }

    static class AsyncPingClient implements PingClient {

        private final AsynchronousSocketChannel channel;
        private final ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);

        AsyncPingClient(int port) throws Exception {
            channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", port)).get();
        }


        @Override
        public void ping(int times) {
            try {
                for (int i = 0; i < times; i++) {
                    buffer.put(PING).flip();
                    int written = 0;
                    while (written < MESSAGE_SIZE) {
                        written += channel.write(buffer).get();
                    }

                    buffer.flip();
                    int read = 0;
                    while (read < MESSAGE_SIZE) {
                        read += channel.read(buffer).get();
                    }
                    buffer.flip();
                    if (!Arrays.equals(PONG, buffer.array())) {
                        throw new IllegalStateException();
                    }

                    buffer.clear();
                }
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }


        @Override
        public void close() throws IOException {
            channel.close();
        }
    }


    static class NettyPingClient implements PingClient {
        private final EventLoopGroup workerGroup = new NioEventLoopGroup();
        private final Channel channel;
        private final Semaphore semaphore = new Semaphore(0);
        private final AtomicInteger pingsLeft = new AtomicInteger();

        public NettyPingClient(int port) throws Exception {
            Bootstrap b = new Bootstrap();
            channel = b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyPingClientInitializer(semaphore, pingsLeft))
                    .connect("localhost", port)
                    .await().channel();

        }

        @Override
        public void ping(int times) {
            if (0 != semaphore.availablePermits()) {
                throw new IllegalStateException();
            }
            if (!pingsLeft.compareAndSet(0, times - 1)) {
                throw new IllegalStateException();
            }
            channel.writeAndFlush(PING);
            try {
                semaphore.acquire(times);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                workerGroup.shutdownGracefully().get();
            } catch (Exception e) {
                if (e instanceof RuntimeException) throw (RuntimeException) e;
                throw new RuntimeException(e);
            }
        }
    }

    static class NettyPingClientInitializer extends ChannelInitializer<SocketChannel> {
        private final Semaphore semaphore;
        private final AtomicInteger pingsLeft;

        public NettyPingClientInitializer(Semaphore semaphore, AtomicInteger pingsLeft) {
            this.semaphore = semaphore;
            this.pingsLeft = pingsLeft;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new FixedLengthFrameDecoder(MESSAGE_SIZE));
            pipeline.addLast(new ByteArrayDecoder());
            pipeline.addLast(new ByteArrayEncoder());
            pipeline.addLast(new NettyPingClientHandler(semaphore, pingsLeft));
        }
    }

    static class NettyPingClientHandler extends SimpleChannelInboundHandler<byte[]> {
        private final Semaphore semaphore;
        private final AtomicInteger pingsLeft;

        public NettyPingClientHandler(Semaphore semaphore, AtomicInteger pingsLeft) {
            this.semaphore = semaphore;
            this.pingsLeft = pingsLeft;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            if (!Arrays.equals(PONG, msg)) {
                throw new IllegalArgumentException();
            }
            if (pingsLeft.getAndDecrement() > 0) {
                ctx.channel().writeAndFlush(PING);
            } else {
                pingsLeft.incrementAndGet();
            }

            semaphore.release();
        }
    }

    static class AsyncHandlersPingClient implements PingClient {
        final ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);
        final AsynchronousSocketChannel channel;
        final CompletionHandler<Integer, PingContext> writeHandler = new CompletionHandler<Integer, PingContext>() {
            @Override
            public void completed(Integer result, PingContext pingContext) {
                if (buffer.hasRemaining()) {
                    channel.write(buffer, pingContext, this);
                    return;
                }


                buffer.flip();

                pingContext.pingsLeft--;
                channel.read(buffer, pingContext, readHandler);
            }

            @Override
            public void failed(Throwable e, PingContext ignored) {
                e.printStackTrace();
            }
        };
        final CompletionHandler<Integer, PingContext> readHandler = new CompletionHandler<Integer, PingContext>() {
            @Override
            public void completed(Integer result, PingContext pingContext) {
                if (buffer.hasRemaining()) {
                    channel.read(buffer, pingContext, this);
                    return;
                }

                buffer.flip();

                if (!Arrays.equals(PONG, buffer.array())) {
                    throw new IllegalStateException();
                }

                if (pingContext.pingsLeft < 1) {
                    pingContext.complete.complete(null);
                } else {
                    ping(pingContext);
                }
            }

            @Override
            public void failed(Throwable e, PingContext ignored) {
                e.printStackTrace();
            }
        };


        AsyncHandlersPingClient(int port) throws IOException, ExecutionException, InterruptedException {
            channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", port)).get();
        }


        private void ping(PingContext pingContext) {
            buffer.clear();
            buffer.put(PING);
            buffer.flip();

            channel.write(buffer, pingContext, writeHandler);
        }

        @Override
        public void ping(int times) {
            PingContext pingContext = new PingContext(times);
            ping(pingContext);
            try {
                pingContext.complete.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() throws IOException {
            channel.close();
        }
    }

    static class PingContext {
        final CompletableFuture<Void> complete = new CompletableFuture<>();
        int pingsLeft;

        public PingContext(int pingsLeft) {
            this.pingsLeft = pingsLeft;
        }
    }

}
