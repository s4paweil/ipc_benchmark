package org.betriebssysteme.ZMQ;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.nio.ByteBuffer;

public class ZMQClient {

    long TOTAL_DATA_SIZE;
    int packetSize;


    public ZMQClient(long TOTAL_DATA_SIZE, int packetSize) {
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() {
        System.out.println("Client gestartet...");

        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung

        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
            socket.connect(address);

            System.out.println("Client gestartet. Sende Nachrichten...");

            byte[] data = new byte[packetSize];
            long totalSent = 0;

            // Simuliere das Senden von Nachrichten
            while (totalSent < TOTAL_DATA_SIZE) {
                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);

                // Reserviere die ersten 8 Bytes für den Zeitstempel
                if (remaining >= Long.BYTES) {
                    long sendTime = System.nanoTime();
                    //System.out.println(sendTime);
                    byte[] timestamp = ByteBuffer.allocate(Long.BYTES).putLong(sendTime).array();
                    System.arraycopy(timestamp, 0, data, 0, Long.BYTES);
                }

                socket.send(data);
                totalSent += remaining;
            }


            // Send end signal
            socket.send("END".getBytes(ZMQ.CHARSET), 0);

            System.out.println("All data sent. Total bytes sent: " + totalSent);
        } catch (ZMQException e) {
            e.printStackTrace();
        }
    }
}
