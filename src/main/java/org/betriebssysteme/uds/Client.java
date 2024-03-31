package org.betriebssysteme.uds;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

public class Client {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Bitte Paketgröße und gesamte Datengröße angeben.");
            return;
        }

        int packetSize = Integer.parseInt(args[0]);
        long TOTAL_DATA_SIZE = Long.parseLong(args[1]);

        Path socketFile = Path.of("/tmp/unixdomainsocket");
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);

        try (SocketChannel socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
            socketChannel.connect(address);

            byte[] data = new byte[packetSize];
            long totalSent = 0;

            // Send data packets
            while (totalSent < TOTAL_DATA_SIZE) {
                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);
                socketChannel.write(ByteBuffer.wrap(data));
                totalSent += remaining;
            }

            // Send end signal
            socketChannel.write(ByteBuffer.wrap("END".getBytes()));

            System.out.println("All data sent. Total bytes sent: " + totalSent);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}