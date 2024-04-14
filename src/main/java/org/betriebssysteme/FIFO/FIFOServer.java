package org.betriebssysteme.FIFO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FIFOServer {

    long TOTAL_DATA_SIZE;
    int packetSize;

    public static final String PIPE_NAME = "/tmp/test_pipe";

    public FIFOServer(long TOTAL_DATA_SIZE, int packetSize){
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() {
        Path pipePath = Paths.get(PIPE_NAME);

        try (InputStream inputStream = new FileInputStream(pipePath.toFile())) {
            int bytesRead;
            long totalBytesRead = 0;
            byte[] buffer = new byte[1024];

            // Initialisiere minLatency und maxLatency mit Extremwerten
            double minLatency = Double.MAX_VALUE;
            double maxLatency = Double.MIN_VALUE;


            while (true) {
                if ((bytesRead = inputStream.read(buffer)) != -1) {
                    String data = new String(buffer, 0, bytesRead);
                    if (data.equals("END")) {
                        break;
                    }

                    // Extrahiere den Zeitstempel aus den ersten 8 Bytes
                    if (bytesRead >= Long.BYTES) {
                        long receivedTimestamp = ByteBuffer.wrap(buffer, 0, Long.BYTES).getLong();
                        if (receivedTimestamp != 0){
                            // Berechne die aktuelle Zeit und damit die Latenz
                            long currentTime = System.nanoTime();
                            double latency = (currentTime - receivedTimestamp) / 1_000_000.0; // Konvertiere Nanosekunden in Millisekunden

                            if(latency < minLatency) {minLatency = latency;}
                            if(latency > maxLatency) {maxLatency = latency;}
                        }

                    }


                    totalBytesRead += bytesRead;
                }
            }

            System.out.println("Server received all data. Total bytes read: " + totalBytesRead);

            // Ausgabe der minimalen und maximalen Latenz
            System.out.printf("Minimale Latenz: %.3f ms\n", minLatency);
            System.out.printf("Maximale Latenz: %.3f ms\n", maxLatency);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
