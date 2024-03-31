package org.betriebssysteme;

import org.betriebssysteme.fifo.Fifo;
import org.betriebssysteme.tcp.TCP;
import org.betriebssysteme.zmq.ZMQ;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final int[] packetSizes = {1024, 2048};
        final int iterations = 5;
        final long TOTAL_DATA_SIZE = 2L * 1024 * 1024 * 1024; // 2 GB

//        // Run Fifo (NamedPipes)
//        IPCBase fifo = new Fifo();
//        fifo.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE);

        // Run Pipes (UnnamedPipes)
        IPCBase tcp = new TCP();
        tcp.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE);

        // Run ZMQ (DEALER/ROUTER)
        IPCBase zmq = new ZMQ();
        zmq.runBenchmark(packetSizes, iterations, TOTAL_DATA_SIZE);
    }
}