package org.betriebssysteme.zmq;

import org.zeromq.*;
import org.zeromq.ZMQ;

public class Server {
    public static void main(String[] args) {
        System.out.println("Server gestartet und wartet auf Nachrichten...");

        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung

        try (ZContext context = new ZContext()) {
            // Erstelle einen ZeroMQ SUB Socket
            org.zeromq.ZMQ.Socket socket = context.createSocket(SocketType.ROUTER);
            socket.bind(address);

            // Warte einige Millisekunden, um sicherzustellen, dass der Server bereit ist, Nachrichten zu empfangen
            System.out.println("Start Reading...");

            boolean running = true;
            long totalBytesReceived = 0;

            while (running && !Thread.currentThread().isInterrupted()) {
                // Empfange Nachrichten
                byte[] messageBytes = socket.recv(0);
                if (messageBytes != null) {
                    String message = new String(messageBytes, ZMQ.CHARSET);
                    //System.out.println("Empfangen: " + message.length());

                    // Akkumuliere die Anzahl der empfangenen Bytes
                    if(messageBytes.length > 5) totalBytesReceived += messageBytes.length;

                    // Überprüfung, ob das Ende des Datenstroms erreicht ist
                    if ("END".equals(message)) {
                        running = false;
                    }
                }
            }

            // Ausgabe der empfangenen Datenmenge
            System.out.println("Total bytes received: " + totalBytesReceived);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}