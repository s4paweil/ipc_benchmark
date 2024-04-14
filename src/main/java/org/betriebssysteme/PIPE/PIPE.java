package org.betriebssysteme.PIPE;

import org.betriebssysteme.IPCBase;
import org.betriebssysteme.StreamGobbler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class PIPE extends IPCBase {
    @Override
    public void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE, Path execPath) throws IOException, InterruptedException {
        for (int packetSize : packetSizes) {
            double totalLatencySeconds = 0;
            double totalMessagesPerSecond = 0;
            double totalThroughputMBps = 0;
            double totalMinLatency = 0;
            double totalMaxLatency = 0;

            for (int i = 0; i < iterations; i++) {

                // Startzeit für die Benchmark-Iteration
                long startTime = System.nanoTime();

                // Start Server
                Process serverProcess = startServerProcess(packetSize, TOTAL_DATA_SIZE, execPath);
                StreamGobbler outputGobbler = new StreamGobbler(serverProcess.getInputStream(), "OUTPUT_SERVER");
                outputGobbler.start();

                long totalSent = writeDataToPipe(serverProcess.getOutputStream(),packetSize, TOTAL_DATA_SIZE);
                serverProcess.waitFor();

                // Endzeit für die Benchmark-Iteration
                long endTime = System.nanoTime();

                //System.out.println("OUTPUT_PIPE> Total bytes sent: " + totalSent);

                // Destroy Client and Server Process
                serverProcess.destroy();

                // Extrahieren der Minimale und Maximale Latenz aus dem Server Gobbler
                double iterationMinLatency = outputGobbler.getMinLatency();
                double iterationMaxLatency = outputGobbler.getMaxLatency();

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
                totalMinLatency += iterationMinLatency;
                totalMaxLatency += iterationMaxLatency;
            }

            // Durchschnittliche Latenz über alle Iterationen
            double avgLatencySeconds = totalLatencySeconds / iterations;

            // Durchschnittliche Nachrichten pro Sekunde über alle Iterationen
            double avgMessagesPerSecond = totalMessagesPerSecond / iterations;

            // Durchschnittlicher Durchsatz über alle Iterationen
            double avgThroughputMBps = totalThroughputMBps / iterations;

            // Durchschnittliche Latenz der Packete
            double avgMinLatency = totalMinLatency / iterations;
            double avgMaxLatency = totalMaxLatency / iterations;

            // Ausgabe der Ergebnisse für die aktuelle Paketgröße
            System.out.println("Unnames Pipes (StdIn/StdOut)");
            System.out.println("Packet Size: " + packetSize + ", Iterations: " + iterations + ", Total Data sent: " + TOTAL_DATA_SIZE / (1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Gesamtdauer: " + avgLatencySeconds + " Sekunden für " + TOTAL_DATA_SIZE / (1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Nachrichten pro Sekunde (NPS): " + avgMessagesPerSecond);
            System.out.println("Durchschnittlicher Durchsatz: " + avgThroughputMBps + " MB/s");
            System.out.println("Durchschnittliche Minimale Latenz: " + avgMinLatency + " ms");
            System.out.println("Durchschnittliche Maximale Latenz: " + avgMaxLatency + " ms");
            System.out.println("--------------------------------------------");

            toCSV("Pipe", packetSize, iterations, TOTAL_DATA_SIZE / (1024 * 1024), avgLatencySeconds, avgMessagesPerSecond, avgThroughputMBps, avgMinLatency, avgMaxLatency);

        }
    }

    private Process startServerProcess(int packetSize, long TOTAL_DATA_SIZE, Path execPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", execPath.toString(), String.valueOf(TOTAL_DATA_SIZE), String.valueOf(packetSize), "pipe", "s");
        Process process = processBuilder.start();
        return process;
    }

    private static long writeDataToPipe(OutputStream outputStream, int packetSize, long TOTAL_DATA_SIZE) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        byte[] data = new byte[packetSize];
        long totalSent = 0;

        // Send data packets
        while (totalSent < TOTAL_DATA_SIZE) {
            int remaining = (int) Math.min(packetSize, TOTAL_DATA_SIZE - totalSent);

            // Reserviere die ersten 8 Bytes für den Zeitstempel
            if(remaining >= Long.BYTES){
                long sendTime = System.nanoTime();
                byte[] timestamp = ByteBuffer.allocate(Long.BYTES).putLong(sendTime).array();
                System.arraycopy(timestamp, 0, data, 0, Long.BYTES);
            }

            outputStream.write(data, 0, remaining);
            outputStream.flush();
            totalSent += remaining;
        }

        // Send end signal
        writer.write("END\n"); // Beenden des Schreibens durch Senden von "END"
        writer.flush();
        writer.close();

        return totalSent;
    }
}
