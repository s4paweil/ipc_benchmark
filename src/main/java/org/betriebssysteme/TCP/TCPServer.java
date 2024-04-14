package org.betriebssysteme.TCP;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class TCPServer {

    long TOTAL_DATA_SIZE;
    int packetSize;

    final int PORT = 12345; // Portnummer für die TCP-Verbindung


    public TCPServer(long TOTAL_DATA_SIZE, int packetSize) {
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server gestartet. Warte auf Verbindung...");

            // Warte auf Client-Verbindung
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client verbunden.");

            // Einlesen der Daten vom Client
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            // Initialisiere minLatency und maxLatency mit Extremwerten
            double minLatency = Double.MAX_VALUE;
            double maxLatency = Double.MIN_VALUE;

            InputStream inputStream = clientSocket.getInputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Daten in den Puffer lesen
                String data = new String(buffer, 0, bytesRead);

                // Überprüfung, ob das Ende des Datenstroms erreicht ist
                if (data.contains("END")) {
                    break;
                }

                // Extrahiere den Zeitstempel aus den ersten 8 Bytes
                if (bytesRead >= Long.BYTES) {
                    long receivedTimestamp = ByteBuffer.wrap(buffer, 0, Long.BYTES).getLong();
                    if (receivedTimestamp != 0) {
                        // Berechne die aktuelle Zeit und damit die Latenz
                        long currentTime = System.nanoTime();
                        double latency = (currentTime - receivedTimestamp) / 1_000_000.0; // Konvertiere Nanosekunden in Millisekunden

                        if (latency < minLatency) {
                            minLatency = latency;
                        }
                        if (latency > maxLatency) {
                            maxLatency = latency;
                        }
                    }
                }

                totalBytesRead += bytesRead;
            }

            // Ausgabe der empfangenen Daten
            System.out.println("Total bytes read: " + totalBytesRead);

            // Ausgabe der minimalen und maximalen Latenz
            System.out.printf("Minimale Latenz: %.3f ms\n", minLatency);
            System.out.printf("Maximale Latenz: %.3f ms\n", maxLatency);

            // Schließen der Verbindungen
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
