package org.betriebssysteme.UDS;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class UDSServer {

    long TOTAL_DATA_SIZE;
    int packetSize;

    public UDSServer(long TOTAL_DATA_SIZE, int packetSize){
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() throws IOException {
        Path socketFile = Path.of("/tmp/unixdomainsocket");
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
            serverChannel.bind(address);
            System.out.println("Server waiting for connection...");

            long totalBytesReceived = 0;

            // Initialisiere minLatency und maxLatency mit Extremwerten
            double minLatency = Double.MAX_VALUE;
            double maxLatency = Double.MIN_VALUE;

            try (SocketChannel socketChannel = serverChannel.accept()) {
                System.out.println("Client connected.");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (socketChannel.read(buffer) > 0) {
                    buffer.flip();
                    // Convert byte buffer to String
                    String message = new String(buffer.array(), buffer.position(), buffer.limit());

                    // Extract timestamp from the first 8 bytes
                    if (buffer.limit() >= Long.BYTES) {
                        long receivedTimestamp = ByteBuffer.wrap(buffer.array(), buffer.position(), Long.BYTES).getLong();
                        if (receivedTimestamp != 0) {
                            // Calculate current time and latency
                            long currentTime = System.nanoTime();
                            double latency = (currentTime - receivedTimestamp) / 1_000_000.0; // Convert nanoseconds to milliseconds

                            if (latency < minLatency) {
                                minLatency = latency;
                            }
                            if (latency > maxLatency) {
                                maxLatency = latency;
                            }
                        }
                    }
                    totalBytesReceived += buffer.remaining();
                    if ("END".equals(message)) {
                        break;
                    }
                    buffer.clear();
                }
            }

            totalBytesReceived -= "END".getBytes().length;

            // Output received data amount
            System.out.println("Total bytes received: " + totalBytesReceived);

            // Output minimum and maximum latency
            System.out.printf("Minimale Latenz: %.3f ms\n", minLatency);
            System.out.printf("Maximale Latenz: %.3f ms\n", maxLatency);
        } finally {
            Files.deleteIfExists(socketFile); // Clean up
        }
    }
}
