package org.betriebssysteme.tcp;

import org.betriebssysteme.IPCBase;
import org.betriebssysteme.StreamGobbler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TCP extends IPCBase {
    @Override
    public void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE) throws IOException, InterruptedException {

        compileServerAndClient(); // Compile Server and Client

        for (int packetSize : packetSizes) {
            double totalLatencySeconds = 0;
            double totalMessagesPerSecond = 0;
            double totalThroughputMBps = 0;

            for (int i = 0; i < iterations; i++) {

                // Start Server
                Process serverProcess = startServerProcess();
//                StreamGobbler outputGobbler = new StreamGobbler(serverProcess.getInputStream(), "OUTPUT_SERVER");
//                outputGobbler.start();

                Thread.sleep(250);

                // Startzeit für die Benchmark-Iteration
                long startTime = System.nanoTime();

                // Start Client
                Process clientProcess = startClientProcess(packetSize, TOTAL_DATA_SIZE);
//                StreamGobbler clientOutput = new StreamGobbler(clientProcess.getInputStream(), "OUTPUT_CLIENT");
//                clientOutput.start();

                // Wait for Client to finish
                clientProcess.waitFor();
                serverProcess.waitFor();

                // Endzeit für die Benchmark-Iteration
                long endTime = System.nanoTime();

                // Destroy Client and Server Process
                serverProcess.destroy();
                clientProcess.destroy();

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
            System.out.println("TCP-Socket");
            System.out.println("Packet Size: " + packetSize + ", Iterations: " + iterations + ", Total Data sent: " + TOTAL_DATA_SIZE / (1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Latenz: " + avgLatencySeconds + " Sekunden für " + TOTAL_DATA_SIZE / (1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Nachrichten pro Sekunde (NPS): " + avgMessagesPerSecond);
            System.out.println("Durchschnittlicher Durchsatz: " + avgThroughputMBps + " MB/s");
            System.out.println("--------------------------------------------");

            toCSV("TCP", packetSize, iterations, TOTAL_DATA_SIZE / (1024 * 1024), avgLatencySeconds, avgMessagesPerSecond, avgThroughputMBps);

        }

    }

    private Process startClientProcess(int packetSize, long TOTAL_DATA_SIZE) throws IOException {
        return Runtime.getRuntime().exec("java -cp src/main/java org.betriebssysteme.tcp.Client " + packetSize + " " + TOTAL_DATA_SIZE);
    }

    private Process startServerProcess() throws IOException {
        return Runtime.getRuntime().exec("java -cp src/main/java org.betriebssysteme.tcp.Server");
    }

    @Override
    protected void compileServerAndClient() throws IOException, InterruptedException {
        Process compileServer = Runtime.getRuntime().exec("javac -cp src src/main/java/org/betriebssysteme/tcp/Server.java");
        compileServer.waitFor();
        compileServer.destroy();

        Process compileClient = Runtime.getRuntime().exec("javac -cp src src/main/java/org/betriebssysteme/tcp/Client.java");
        compileClient.waitFor();
        compileClient.destroy();
    }
}
