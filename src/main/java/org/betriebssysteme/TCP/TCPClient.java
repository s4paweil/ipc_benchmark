package org.betriebssysteme.TCP;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TCPClient {

    long TOTAL_DATA_SIZE;
    int packetSize;

    String serverAddress = "localhost";
    int serverPort = 12345;

    public TCPClient(long TOTAL_DATA_SIZE, int packetSize){
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             OutputStream outputStream = socket.getOutputStream()) {

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

                outputStream.write(data, 0, remaining);
                totalSent += remaining;
            }

            // Send end signal
            outputStream.write("END".getBytes());
            outputStream.flush();

            System.out.println("All data sent. Total bytes sent: " + totalSent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
