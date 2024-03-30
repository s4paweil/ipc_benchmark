package org.betriebssysteme.fifo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

    public static final String PIPE_NAME = "/tmp/test_pipe";


    public static void main(String[] args) throws IOException {


        if (args.length < 2) {
            System.out.println("Bitte Paketgröße und gesamte Datengröße angeben.");
            return;
        }

        int packetSize = Integer.parseInt(args[0]);
        long TOTAL_DATA_SIZE = Long.parseLong(args[1]);

        Path pipePath = Paths.get(PIPE_NAME);
        FileOutputStream outputStream = new FileOutputStream(pipePath.toFile());

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
        outputStream.close();

        //System.out.println("All data sent. Total bytes sent: " + totalSent);


    }
}
