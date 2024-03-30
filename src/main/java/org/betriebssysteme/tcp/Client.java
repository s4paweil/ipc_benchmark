package org.betriebssysteme.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Bitte Paketgröße und gesamte Datengröße angeben.");
            return;
        }

        String serverAddress = "localhost";
        int serverPort = 12345;
        int packetSize = Integer.parseInt(args[0]);
        long TOTAL_DATA_SIZE = Long.parseLong(args[1]);

        try (Socket socket = new Socket(serverAddress, serverPort);
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] data = new byte[packetSize];
            long totalSent = 0;

            long startTime = System.nanoTime();

            // Send data packets
            while (totalSent < TOTAL_DATA_SIZE) {
                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);
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
