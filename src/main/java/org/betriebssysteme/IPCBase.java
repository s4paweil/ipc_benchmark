package org.betriebssysteme;

import java.io.*;

public abstract class IPCBase {
    public abstract void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE) throws IOException, InterruptedException;

    protected abstract void compileServerAndClient() throws IOException, InterruptedException;

    public void toCSV(String name, int packetSize, int iterations, long totalDataSize, double avgLatencySeconds, double avgMessagesPerSecond, double avgThroughputMBps) {
        try (FileWriter writer = new FileWriter("output.csv", true)) {
            writer.write(name + "," + packetSize + "," + iterations + "," + totalDataSize + "," + avgLatencySeconds + "," + avgMessagesPerSecond
                    + "," + avgThroughputMBps + "\n");
        }catch(Exception e){
            //
        }
    }

}
