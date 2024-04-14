package org.betriebssysteme.UDS;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

public class UDSClient {

    long TOTAL_DATA_SIZE;
    int packetSize;

    public UDSClient(long TOTAL_DATA_SIZE, int packetSize){
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }



    public void start(){
        System.out.println("STarted Client");
        Path socketFile = Path.of("/tmp/unixdomainsocket");
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);

        try (SocketChannel socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
            socketChannel.connect(address);

            byte[] data = new byte[packetSize];
            long totalSent = 0;

            // Send data packets
            while (totalSent < TOTAL_DATA_SIZE) {
                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);

                // Reserviere die ersten 8 Bytes für den Zeitstempel
                if(remaining >= Long.BYTES){
                    long sendTime = System.nanoTime();
                    byte[] timestamp = ByteBuffer.allocate(Long.BYTES).putLong(sendTime).array();
                    System.arraycopy(timestamp, 0, data, 0, Long.BYTES);
                }

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
