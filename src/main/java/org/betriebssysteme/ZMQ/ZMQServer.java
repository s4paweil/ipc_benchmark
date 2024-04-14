package org.betriebssysteme.ZMQ;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class ZMQServer {

    long TOTAL_DATA_SIZE;
    int packetSize;

    public ZMQServer(long TOTAL_DATA_SIZE, int packetSize) {
        this.TOTAL_DATA_SIZE = TOTAL_DATA_SIZE;
        this.packetSize = packetSize;
    }

    public void start() {
        System.out.println("Server gestartet und wartet auf Nachrichten...");

        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung

        try (ZContext context = new ZContext()) {
            // Erstelle einen ZeroMQ SUB Socket
            ZMQ.Socket socket = context.createSocket(SocketType.ROUTER);
            socket.bind(address);

            // Warte einige Millisekunden, um sicherzustellen, dass der Server bereit ist, Nachrichten zu empfangen
            System.out.println("Start Reading...");

            boolean running = true;
            long totalBytesReceived = 0;

            // Initialisiere minLatency und maxLatency mit Extremwerten
            double minLatency = Double.MAX_VALUE;
            double maxLatency = Double.MIN_VALUE;

            while (running && !Thread.currentThread().isInterrupted()) {
                // Empfange Nachrichten
                byte[] messageBytes = socket.recv(0);

                if (messageBytes != null) {
                    String message = new String(messageBytes, ZMQ.CHARSET);

                    // Akkumuliere die Anzahl der empfangenen Bytes
                    // Akkumuliere die Anzahl der empfangenen Bytes
                    if (messageBytes.length > 5) totalBytesReceived += messageBytes.length;

                    // Überprüfung, ob das Ende des Datenstroms erreicht ist
                    if ("END".equals(message)) {
                        running = false;
                    } else {
                        // Lese den Zeitstempel aus den empfangenen Daten
                        if (messageBytes.length >= Long.BYTES) {
                            byte[] timestampBytes = new byte[Long.BYTES];
                            System.arraycopy(messageBytes, 0, timestampBytes, 0, Long.BYTES);
                            long receivedTimestamp = ByteBuffer.wrap(timestampBytes).getLong();

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
                    }
                }
            }

            // Ausgabe der empfangenen Datenmenge
            System.out.println("Total bytes received: " + totalBytesReceived);

            // Ausgabe der minimalen und maximalen Latenz
            System.out.printf("Minimale Latenz: %.3f ms\n", minLatency);
            System.out.printf("Maximale Latenz: %.3f ms\n", maxLatency);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
