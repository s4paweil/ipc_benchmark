package org.betriebssysteme.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        final int PORT = 12345; // Portnummer für die TCP-Verbindung

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server gestartet. Warte auf Verbindung...");

            // Warte auf Client-Verbindung
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client verbunden.");

            // Einlesen der Daten vom Client
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            InputStream inputStream = clientSocket.getInputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Daten in den Puffer lesen
                String data = new String(buffer, 0, bytesRead);

                // Überprüfung, ob das Ende des Datenstroms erreicht ist
                if (data.contains("END")) {
                    break;
                }

                totalBytesRead += bytesRead;
            }

            // Ausgabe der empfangenen Daten
            System.out.println("Total bytes read: " + totalBytesRead);

            // Schließen der Verbindungen
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
