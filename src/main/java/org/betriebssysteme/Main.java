package org.betriebssysteme;

import org.betriebssysteme.fifo.Fifo;
import org.betriebssysteme.tcp.TCP;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final int[] packetSizes = {32768, 65536};
        final int iterations = 1;
        final long TOTAL_DATA_SIZE = 2L * 1024 * 1024 * 1024; // 2 GB

        // Run Fifo (NamedPipes)
        IPCBase fifo = new Fifo();
        fifo.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE);

        // Run Pipes (UnnamedPipes)
        IPCBase tcp = new TCP();
        tcp.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE);
    }
}