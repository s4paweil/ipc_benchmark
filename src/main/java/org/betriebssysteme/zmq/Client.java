package org.betriebssysteme.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public class Client {
//    public static void main(String[] args) {
//        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung
//        int packetSize = 2048;
//        long TOTAL_DATA_SIZE = 2L * 1024 * 1024 * 1024;
//
//        try (ZContext context = new ZContext()) {
//            // Erstelle einen ZeroMQ REQ Socket
//            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
//            socket.connect(address);
//
//            System.out.println("Client gestartet. Sende Nachrichten...");
//
//            byte[] data = new byte[packetSize];
//            long totalSent = 0;
//
//            // Simuliere das Senden von Nachrichten
//            while (totalSent < TOTAL_DATA_SIZE) {
//                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);
//                socket.send(data, 0);
//                totalSent += remaining;
//
//                // Warte auf die Antwort
//                byte[] replyBytes = socket.recv(0);
//                String reply = new String(replyBytes, ZMQ.CHARSET);
//                //System.out.println("Erhaltene Antwort: " + reply);
//            }
//
//            // Send end signal
//            socket.send("END".getBytes(ZMQ.CHARSET), 0);
//
//            System.out.println("All data sent. Total bytes sent: " + totalSent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }




    public static void main(String[] args) {

//        if (args.length < 2) {
//            System.out.println("Bitte Paketgröße und Anzahl der Pakete angeben.");
//            return;
//        }

        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung
        int packetSize = 2048;
        long TOTAL_DATA_SIZE = 2L * 1024 * 1024 * 1024;;

        try (ZContext context = new ZContext()) {
            // Erstelle einen ZeroMQ PUB Socket
            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.bind(address);

            System.out.println("Client gestartet. Sende Nachrichten...");

            byte[] data = new byte[packetSize];
            long totalSent = 0;

            Thread.sleep(1300);

            // Simuliere das Senden von Nachrichten
            while (totalSent < TOTAL_DATA_SIZE) {
                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);
                socket.send(data, 0);
                totalSent += remaining;
            }


            // Send end signal
            socket.send("END".getBytes(ZMQ.CHARSET), 0);

            System.out.println("All data sent. Total bytes sent: " + totalSent);
        } catch (ZMQException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
