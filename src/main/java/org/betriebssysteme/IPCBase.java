package org.betriebssysteme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class IPCBase {
    public abstract void runBenchmark(int[] packetSizes, int iterations, long TOTAL_DATA_SIZE) throws IOException, InterruptedException;

    protected abstract void compileServerAndClient() throws IOException, InterruptedException;

}
