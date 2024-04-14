package org.betriebssysteme.PIPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class PIPEServer {

    long TOTAL_DATA_SIZE;
    int packetSize;

    public PIPEServer(long TOTAL_DATA_SIZE, int packetSize){
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() throws IOException {
        System.out.println("Server gestartet. Warte auf Nachrichten...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        byte[] buffer = new byte[1024]; // Puffer zum Lesen von Daten
        int bytesRead;
        long totalBytesRead = 0;

        // Initialisiere minLatency und maxLatency mit Extremwerten
        double minLatency = Double.MAX_VALUE;
        double maxLatency = Double.MIN_VALUE;

        while ((bytesRead = System.in.read(buffer)) != -1) {
            // Daten in den Puffer lesen
            String data = new String(buffer, 0, bytesRead);

            // Überprüfung, ob das Ende des Datenstroms erreicht ist
            if (data.contains("END")) {
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

            // Zählen der gelesenen Bytes
            totalBytesRead += bytesRead;
        }

        // Ausgabe der Gesamtzahl der gelesenen Bytes
        System.out.println("Total bytes read: " + totalBytesRead);

        // Ausgabe der minimalen und maximalen Latenz
        System.out.printf("Minimale Latenz: %.3f ms\n", minLatency);
        System.out.printf("Maximale Latenz: %.3f ms\n", maxLatency);

        reader.close();
    }
}
