package org.betriebssysteme.uds;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {
    public static void main(String[] args) throws IOException {
        Path socketFile = Path.of("/tmp/unixdomainsocket");
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
            serverChannel.bind(address);
            System.out.println("Server waiting for connection...");

            long totalBytesReceived = 0;

            try (SocketChannel socketChannel = serverChannel.accept()) {
                System.out.println("Client connected.");
                ByteBuffer buffer = ByteBuffer.allocate(2048);
                while (socketChannel.read(buffer) > 0) {
                    buffer.flip();
                    String message = new String(buffer.array(), buffer.position(), buffer.limit());
                    totalBytesReceived += message.getBytes().length;
                    if ("END".equals(message)) {
                        break;
                    }
                    buffer.clear();
                }
            }
            totalBytesReceived -= "END".getBytes().length;

            // Ausgabe der empfangenen Datenmenge
            System.out.println("Total bytes received: " + totalBytesReceived);
        } finally {
            Files.deleteIfExists(socketFile); // Clean up
        }
    }
}