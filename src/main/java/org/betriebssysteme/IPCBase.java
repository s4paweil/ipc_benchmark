package org.betriebssysteme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class IPCBase {

    protected abstract Process startServerProcess() throws IOException;
    protected abstract Process startClientProcess(int packetSize, long TOTAL_DATA_SIZE) throws IOException;
    protected abstract void compileServerAndClient() throws IOException, InterruptedException;
    protected abstract void setupCommunication() throws IOException;
    protected abstract void disassembleCommunication() throws IOException;

    public void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE) throws IOException, InterruptedException {

        compileServerAndClient(); // Compile Server and Client

        setupCommunication(); // Setup Communication f.e. NamedPipe

        for(int packetSize : packetSizes){
            double totalLatencySeconds = 0;
            double totalMessagesPerSecond = 0;
            double totalThroughputMBps = 0;

            for (int i = 0; i < iterations; i++) {

                // Start Server
                Process serverProcess = startServerProcess();
                StreamGobbler outputGobbler = new StreamGobbler(serverProcess.getInputStream(), "OUTPUT_SERVER");
                outputGobbler.start();

                // Startzeit für die Benchmark-Iteration
                long startTime = System.nanoTime();

                // Start Client
                Process clientProcess = startClientProcess(packetSize, TOTAL_DATA_SIZE);
                StreamGobbler clientOutput = new StreamGobbler(clientProcess.getInputStream(), "OUTPUT_CLIENT");
                clientOutput.start();

                // Wait for Client to finish
                clientProcess.waitFor();

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
            System.out.println("Packet Size: " + packetSize + ", Iterations: " + iterations + ", Total Data sent: " + TOTAL_DATA_SIZE/(1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Latenz: " + avgLatencySeconds + " Sekunden für " + TOTAL_DATA_SIZE/(1024 * 1024) + " MB");
            System.out.println("Durchschnittliche Nachrichten pro Sekunde (NPS): " + avgMessagesPerSecond);
            System.out.println("Durchschnittlicher Durchsatz: " + avgThroughputMBps + " MB/s");
            System.out.println("--------------------------------------------");

        }


        disassembleCommunication(); // Disassemble Communication f.e. NamedPipe
    }




    private static class StreamGobbler extends Thread {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + "> " + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
