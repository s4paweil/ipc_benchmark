package org.betriebssysteme.pipe;

import org.betriebssysteme.IPCBase;
import org.betriebssysteme.StreamGobbler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Pipe extends IPCBase {
    @Override
    public void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE) throws IOException, InterruptedException {

        compileServerAndClient(); // Compile only Server, no Client in this IPC-Method


        for (int packetSize : packetSizes) {
            double totalLatencySeconds = 0;
            double totalMessagesPerSecond = 0;
            double totalThroughputMBps = 0;

            for (int i = 0; i < iterations; i++) {

                // Startzeit für die Benchmark-Iteration
                long startTime = System.nanoTime();

                // Start Server
                Process serverProcess = startServerProcess();
                StreamGobbler outputGobbler = new StreamGobbler(serverProcess.getInputStream(), "OUTPUT_SERVER");
                outputGobbler.start();

                long totalSent = writeDataToPipe(serverProcess.getOutputStream(),packetSize, TOTAL_DATA_SIZE);
                serverProcess.waitFor();

                // Endzeit für die Benchmark-Iteration
                long endTime = System.nanoTime();

                System.out.println("OUTPUT_PIPE> Total bytes sent: " + totalSent);

                // Destroy Client and Server Process
                serverProcess.destroy();

                // Berechnung der Latenz
                double latencySeconds = (endTime - startTime) / 1e9;

                // Berechnung der Nachrichten pro Sekunde (NPS)
                double messagesPerSecond = (double) TOTAL_DATA_SIZE / packetSize / latencySeconds;

                // Berechnung des Durchsatzes (MB/s)
                double throughputMBps = (double) TOTAL_DATA_SIZE / latencySeconds / (1024 * 1024);

                // Addieren der Ergebnisse für die Durchschnittsberechnung
                totalLatencySeconds += latencySeconds;
                totalMessagesPerSecond += messagesPerSecond;
                totalThroughputMBps += throughputMBps;
            }

            // Durchschnittliche Latenz über alle Iterationen
            double avgLatencySeconds = totalLatencySeconds / iterations;

            // Durchschnittliche Nachrichten pro Sekunde über alle Iterationen
            double avgMessagesPerSecond = totalMessagesPerSecond / iterations;

            // Durchschnittlicher Durchsatz über alle Iterationen
            double avgThroughputMBps = totalThroughputMBps / iterations;

            // Ausgabe der Ergebnisse für die aktuelle Paketgröße
            System.out.println("Fifo (Named Pipes)");
            System.out.println("Packet Size: " + packetSize + ", Iterations: " + iterations + ", Total Data sent: " + TOTAL_DATA_SIZE / (1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Latenz: " + avgLatencySeconds + " Sekunden für " + TOTAL_DATA_SIZE / (1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Nachrichten pro Sekunde (NPS): " + avgMessagesPerSecond);
            System.out.println("Durchschnittlicher Durchsatz: " + avgThroughputMBps + " MB/s");
            System.out.println("--------------------------------------------");

        }
    }

    private Process startServerProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "src/main/java", "org.betriebssysteme.pipe.Server");
        return processBuilder.start();
    }

    private static long writeDataToPipe(OutputStream outputStream, int packetSize, long TOTAL_DATA_SIZE) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        byte[] data = new byte[packetSize];
        long totalSent = 0;

        // Send data packets
        while (totalSent < TOTAL_DATA_SIZE) {
            int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);
            outputStream.write(data, 0, remaining);
            totalSent += remaining;
        }

        // Send end signal
        writer.write("END\n"); // Beenden des Schreibens durch Senden von "END"
        writer.flush();
        writer.close();

        return totalSent;
    }

    @Override
    protected void compileServerAndClient() throws IOException, InterruptedException {
        // Only compiling Server to receive Data sent from this process!!!
        Process compileServer = Runtime.getRuntime().exec("javac -cp src src/main/java/org/betriebssysteme/pipe/Server.java");
        compileServer.waitFor();
        compileServer.destroy();
    }
}
