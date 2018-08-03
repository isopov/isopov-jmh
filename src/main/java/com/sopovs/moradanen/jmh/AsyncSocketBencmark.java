package com.sopovs.moradanen.jmh;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
import java.util.concurrent.TimeUnit;

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
    //TODO netty
    @Param({"blocking", "async", "asyncHandlers"})
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
        private final int port;

        public NettyPingClient(int port) {
            this.port = port;

            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyPingClientInitializer());
        }

        @Override
        public void ping(int times) {
            //TODO
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

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            //TODO
        }
    }

    static class NettyPingClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            //TODO
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
