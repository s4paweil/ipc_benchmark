package org.betriebssysteme.fifo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

    public static final String PIPE_NAME = "/tmp/test_pipe";

    public static void main(String[] args) throws IOException {

        //System.out.println("Server Test!");

        Path pipePath = Paths.get(PIPE_NAME);

        try (InputStream inputStream = new FileInputStream(pipePath.toFile())) {
            int bytesRead;
            long totalBytesRead = 0;
            byte[] buffer = new byte[1024];


            while (true) {
                if ((bytesRead = inputStream.read(buffer)) != -1) {
                    String data = new String(buffer, 0, bytesRead);
                    if (data.equals("END")) {
                        break;
                    }
                    totalBytesRead += bytesRead;
                }
            }

            System.out.println("Server received all data. Total bytes read: " + totalBytesRead);
        }
    }
}
