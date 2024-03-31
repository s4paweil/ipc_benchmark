package org.betriebssysteme.zmq;

import org.zeromq.*;
import org.zeromq.ZMQ;

public class Server {

//    public static void main(String[] args) {
//        final String address = "tcp://*:12345"; // Adresse für die Verbindung
//
//        try (ZContext context = new ZContext()) {
//            // Erstelle einen ZeroMQ REP Socket
//            ZMQ.Socket socket = context.createSocket(SocketType.REP);
//            socket.bind(address);
//
//            System.out.println("Server gestartet und wartet auf Nachrichten...");
//
//            boolean running = true;
//            long totalBytesReceived = 0;
//
//            while (running && !Thread.currentThread().isInterrupted()) {
//                // Empfange Nachrichten
//                byte[] requestBytes = socket.recv(0);
//                if (requestBytes != null) {
//                    String request = new String(requestBytes, ZMQ.CHARSET);
//                    //System.out.println("Empfangene Anfrage: " + request);
//
//                    // Akkumuliere die Anzahl der empfangenen Bytes
//                    totalBytesReceived += requestBytes.length;
//
//                    // Überprüfe, ob das Ende des Datenstroms erreicht ist
//                    if ("END".equals(request)) {
//                        running = false;
//                    }
//
//                    // Sende eine leere Antwort zurück, um die Verbindung freizugeben
//                    socket.send("".getBytes(ZMQ.CHARSET), 0);
//                }
//            }
//
//            // Ausgabe der empfangenen Datenmenge
//            System.out.println("Total bytes received: " + totalBytesReceived);
//        } catch (ZMQException e) {
//            e.printStackTrace();
//        }
//    }





    public static void main(String[] args) {
        System.out.println("Server gestartet und wartet auf Nachrichten...");

        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung

        try (ZContext context = new ZContext()) {
            // Erstelle einen ZeroMQ SUB Socket
            org.zeromq.ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect(address);

            // Abonniere alle Nachrichten
            socket.subscribe("".getBytes(org.zeromq.ZMQ.CHARSET));

            // Warte einige Millisekunden, um sicherzustellen, dass der Server bereit ist, Nachrichten zu empfangen
            System.out.println("Start Reading...");

            boolean running = true;
            long totalBytesReceived = 0;

            while (running && !Thread.currentThread().isInterrupted()) {
                // Empfange Nachrichten
                byte[] messageBytes = socket.recv(0);
                if (messageBytes != null) {
                    String message = new String(messageBytes, ZMQ.CHARSET);
                    //System.out.println("Empfangen: " + message);

                    // Akkumuliere die Anzahl der empfangenen Bytes
                    totalBytesReceived += messageBytes.length;

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