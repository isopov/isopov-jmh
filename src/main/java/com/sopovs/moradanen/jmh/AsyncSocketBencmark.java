package com.sopovs.moradanen.jmh;

import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
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

    private static final int PORT = 9999;

    private static final byte[] PING = "ping".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PONG = "pong".getBytes(StandardCharsets.UTF_8);
    private static final int MESSAGE_SIZE = PING.length;

    @Param({"1", "10", "100"})
    public int pings;
    @Param({"blocking", "async"})
//    @Param({"blocking", "async", "asyncHandlers"})
    public String type;

    private Server server;
    private PingClient pingClient;

    @Setup
    public void setup() throws Exception {
        server = new Server();
        server.start();
        switch (type) {
            case "blocking":
                pingClient = new BlockingPingClient();
                break;
            case "async":
                pingClient = new AsyncPingClient();
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
    public void ping(){
        pingClient.ping(pings);
    }

    static class Server extends Thread {
        final CompletableFuture<Void> started = new CompletableFuture<>();
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                this.serverSocket = serverSocket;
                started.complete(null);
                while (true) {
                    try (
                            Socket socket = serverSocket.accept();
                            DataInputStream in = new DataInputStream(socket.getInputStream());
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                        this.socket = socket;
                        while (true) {
                            in.skipBytes(PING.length);
                            out.write(PONG);
                        }
                    } catch (EOFException e) {
                        //ignore
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        BlockingPingClient() throws IOException {
            socket = new Socket("localhost", PORT);
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

//    @Benchmark
//    public void blockingPingClient() throws Exception {
//        try (Socket socket = new Socket("localhost", PORT);
//             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
//            for (int i = 0; i < pings; i++) {
//                out.writeInt(i);
//                out.write(PING);
//                out.flush();
//                if (i != in.readInt()) {
//                    throw new IllegalStateException();
//                }
//                in.skipBytes(PING.length);
//            }
//        }
//    }


    static class AsyncPingClient implements PingClient {

        private final AsynchronousSocketChannel channel;
        private final ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);

        AsyncPingClient() throws Exception {
            channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", PORT)).get();
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

//    @Benchmark
//    public void asyncPingClient() throws IOException, InterruptedException, ExecutionException {
//        try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open()) {
//            channel.connect(new InetSocketAddress("localhost", PORT)).get();
//
//            ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);
//            for (int i = 0; i < pings; i++) {
//                buffer
//                        .put(PING)
//                        .flip();
//                int written = 0;
//                while (written < MESSAGE_SIZE) {
//                    written += channel.write(buffer).get();
//                }
//
//                buffer.flip();
//                int read = 0;
//                while (read < MESSAGE_SIZE) {
//                    read += channel.read(buffer).get();
//                }
//                buffer.flip();
//                if (i != buffer.getInt()) {
//                    throw new IllegalStateException();
//                }
//
//
//                buffer.clear();
//            }
//        }
//    }

//    @Benchmark
//    public void asyncHandlersPingClient() throws Exception {
//        try (AsyncPingClient asyncPingClient = new AsyncPingClient(pings)) {
//            asyncPingClient.startPing();
//            asyncPingClient.waitCompletion();
//        }
//
//    }
//
//    static class AsyncHandlersPingClient implements PingClient {
//        private final int pings;
//        final AtomicInteger counter = new AtomicInteger();
//        final ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);
//        final AsynchronousSocketChannel channel;
//        final CompletableFuture<Void> complete = new CompletableFuture<>();
//        final CompletionHandler<Integer, Void> writeHandler = new CompletionHandler<Integer, Void>() {
//            @Override
//            public void completed(Integer result, Void ignored) {
//                buffer.flip();
//                channel.read(buffer, 0, readHandler);
//            }
//
//            @Override
//            public void failed(Throwable e, Void ignored) {
//                e.printStackTrace();
//            }
//        };
//        final CompletionHandler<Integer, Integer> readHandler = new CompletionHandler<Integer, Integer>() {
//            @Override
//            public void completed(Integer result, Integer alreadyRead) {
//                if (alreadyRead + result < MESSAGE_SIZE) {
//                    channel.read(buffer, alreadyRead + result, this);
//                    return;
//                }
//                buffer.flip();
//                int count = counter.getAndIncrement();
//                if (count >= pings) {
//                    complete.complete(null);
//                } else {
//                    ping(counter, count);
//                }
//            }
//
//            @Override
//            public void failed(Throwable e, Integer ignored) {
//                e.printStackTrace();
//            }
//        };
//
//
//        AsyncPingClient(int pings) throws IOException, ExecutionException, InterruptedException {
//            this.pings = pings;
//            channel = AsynchronousSocketChannel.open();
//            channel.connect(new InetSocketAddress("localhost", PORT)).get();
//        }
//
//        void startPing() {
//            ping(counter, counter.getAndIncrement());
//        }
//
//        private void ping(AtomicInteger counter, int count) {
//            buffer.clear();
//            buffer.putInt(count);
//            buffer.put(PING);
//            buffer.flip();
//
//            channel.write(buffer, null, writeHandler);
//        }
//
//
//        void waitCompletion() throws ExecutionException, InterruptedException {
//            complete.get();
//        }
//
//        @Override
//        public void close() throws IOException {
//            channel.close();
//        }
//    }

}
