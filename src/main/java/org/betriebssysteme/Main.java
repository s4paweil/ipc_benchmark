package org.betriebssysteme;

import org.betriebssysteme.FIFO.*;
import org.betriebssysteme.PIPE.*;
import org.betriebssysteme.TCP.*;
import org.betriebssysteme.UDS.*;
import org.betriebssysteme.ZMQ.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {

        Path execPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path parentPath = execPath.getParent();
        if (parentPath != null && execPath.endsWith("classes")) {
            execPath = parentPath.resolve("ipc-benchmarktest.jar");
        }

        if (args.length != 3 || args[2].equals("pipe")) {
            final long TOTAL_DATA_SIZE = Long.valueOf(args[0]);
            final int packetSize = Integer.valueOf(args[1]);
            switch (args[2]) {
                case "tcp": {
                    if (args[3].equals("s")) {
                        TCPServer server = new TCPServer(TOTAL_DATA_SIZE, packetSize);
                        server.start();
                    } else {
                        TCPClient client = new TCPClient(TOTAL_DATA_SIZE, packetSize);
                        client.start();
                    }
                    break;
                }
                case "fifo": {
                    if (args[3].equals("s")) {
                        FIFOServer server = new FIFOServer(TOTAL_DATA_SIZE, packetSize);
                        server.start();
                    } else {
                        FIFOClient client = new FIFOClient(TOTAL_DATA_SIZE, packetSize);
                        client.start();
                    }
                    break;
                }
                case "uds": {
                    if (args[3].equals("s")) {
                        UDSServer server = new UDSServer(TOTAL_DATA_SIZE, packetSize);
                        server.start();
                    } else {
                        UDSClient client = new UDSClient(TOTAL_DATA_SIZE, packetSize);
                        client.start();
                    }
                    break;
                }
                case "pipe": {
                    System.out.println("STARTING PIPE SERVER");
                    PIPEServer server = new PIPEServer(TOTAL_DATA_SIZE, packetSize);
                    server.start();
                    break;
                }
                case "zmq": {
                    if (args[3].equals("s")) {
                        System.out.println("SERVER STARTED");
                        ZMQServer server = new ZMQServer(TOTAL_DATA_SIZE, packetSize);
                        server.start();
                    } else {
                        System.out.println("CLIENT STARTED");
                        ZMQClient client = new ZMQClient(TOTAL_DATA_SIZE, packetSize);
                        client.start();
                    }
                    break;
                }
            }
        } else {
            final long TOTAL_DATA_SIZE = Long.valueOf(args[0]) * 1024 * 1024 * 1024;
            final int[] packetSizes = parseToIntArray(args[1]);
            final int iterations = Integer.valueOf(args[2]);

            //Init Output
            try (FileWriter writer = new FileWriter("output.csv", false)) {
                writer.write("Name,PacketSize,Iterations,TotalDataSize,AvgLatencySeconds,AvgMessagesPerSecond,AvgThroughputMBps,MinLatencyMilliseconds,MaxLatencyMilliseconds\n");
            }

            // Run TCP (TCP Socket)
            TCP tcp = new TCP();
            tcp.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE, execPath);

            // Run Fifo (NamedPipes)
            FIFO fifo = new FIFO();
            fifo.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE, execPath);

            // Run Unix Domain Sockets
            IPCBase uds = new UDS();
            uds.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE, execPath);

            // Run Pipe (anonymous - stdin)
            PIPE pipe = new PIPE();
            pipe.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE, execPath);

            // Run ZMQ (DEALER/ROUTER)
            ZMQ zmq = new ZMQ();
            zmq.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE, execPath);

        }


    }


    public static int[] parseToIntArray(String input) {
        if (input == null || input.isEmpty()) {
            return new int[0]; // Leeres Array, wenn keine Eingabe vorliegt.
        }

        String[] parts = input.split(",");
        int[] numbers = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                numbers[i] = Integer.parseInt(parts[i].trim()); // trim entfernt führende/abschließende Leerzeichen.
            } catch (NumberFormatException e) {
                System.err.println("Fehler beim Parsen der Zahl: " + parts[i]);
                // Optional kann hier auch eine andere Fehlerbehandlung erfolgen oder das Array bis dahin zurückgegeben werden.
                throw e; // oder `continue;` um mit dem nächsten Element fortzufahren
            }
        }

        return numbers;
    }
}