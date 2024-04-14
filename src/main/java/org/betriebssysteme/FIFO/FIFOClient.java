package org.betriebssysteme.FIFO;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FIFOClient {

    long TOTAL_DATA_SIZE;
    int packetSize;

    public static final String PIPE_NAME = "/tmp/test_pipe";

    public FIFOClient(long TOTAL_DATA_SIZE, int packetSize){
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() throws IOException {
        Path pipePath = Paths.get(PIPE_NAME);
        FileOutputStream outputStream = new FileOutputStream(pipePath.toFile());

        byte[] data = new byte[packetSize];
        long totalSent = 0;

        long startTime = System.nanoTime();

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
        outputStream.close();

        System.out.println("All data sent. Total bytes sent: " + totalSent);
    }
}
