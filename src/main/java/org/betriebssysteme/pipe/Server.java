package org.betriebssysteme.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {

    public static void main(String[] args) throws IOException {

        System.out.println("Server gestartet. Warte auf Nachrichten...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        byte[] buffer = new byte[1024]; // Puffer zum Lesen von Daten
        int bytesRead;
        long totalBytesRead = 0;

        while ((bytesRead = System.in.read(buffer)) != -1) {
            // Daten in den Puffer lesen
            String data = new String(buffer, 0, bytesRead);

            // Überprüfung, ob das Ende des Datenstroms erreicht ist
            if (data.contains("END")) {
                break;
            }

            // Zählen der gelesenen Bytes
            totalBytesRead += bytesRead;
        }

        // Ausgabe der Gesamtzahl der gelesenen Bytes
        System.out.println("Total bytes read: " + totalBytesRead);

        reader.close();
    }
}
