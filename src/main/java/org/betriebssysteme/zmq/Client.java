package org.betriebssysteme.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public class Client {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Bitte Paketgröße und Anzahl der Pakete angeben.");
            return;
        }

        final String address = "tcp://localhost:12345"; // Adresse für die Verbindung
        int packetSize = Integer.parseInt(args[0]);
        long TOTAL_DATA_SIZE = Long.parseLong(args[1]);

        try (ZContext context = new ZContext()) {
            // Erstelle einen ZeroMQ PUB Socket
            ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
            socket.connect(address);

            System.out.println("Client gestartet. Sende Nachrichten...");

            byte[] data = new byte[packetSize];
            long totalSent = 0;

            // Simuliere das Senden von Nachrichten
            while (totalSent < TOTAL_DATA_SIZE) {
                int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);
                //System.out.println(data.length);
                socket.send(data);
                totalSent += remaining;
            }


            // Send end signal
            socket.send("END".getBytes(ZMQ.CHARSET), 0);

            System.out.println("All data sent. Total bytes sent: " + totalSent);
        } catch (ZMQException e) {
            e.printStackTrace();
        }
    }
}
