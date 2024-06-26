package org.betriebssysteme;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class IPCBase {
    public abstract void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE, Path execPath) throws IOException, InterruptedException;

    public void toCSV(String name, int packetSize, int iterations, long totalDataSize, double avgLatencySeconds, double avgMessagesPerSecond, double avgThroughputMBps, double minLatency, double maxLatency) {
        try (FileWriter writer = new FileWriter("output.csv", true)) {
            writer.write(name + "," + packetSize + "," + iterations + "," + totalDataSize + "," + avgLatencySeconds + "," + avgMessagesPerSecond
                    + "," + avgThroughputMBps + "," + minLatency + "," + maxLatency + "\n");
        }catch(Exception e){
            //
        }
    }

    public void toCSV(String name, int packetSize, int iterations, long totalDataSize, double avgLatencySeconds, double avgMessagesPerSecond, double avgThroughputMBps) {
        try (FileWriter writer = new FileWriter("output.csv", true)) {
            writer.write(name + "," + packetSize + "," + iterations + "," + totalDataSize + "," + avgLatencySeconds + "," + avgMessagesPerSecond
                    + "," + avgThroughputMBps + "\n");
        }catch(Exception e){
            //
        }
    }


}
