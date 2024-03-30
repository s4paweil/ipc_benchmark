package org.betriebssysteme.fifo;

import org.betriebssysteme.IPCBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Fifo extends IPCBase{

    public static final String PIPE_NAME = "/tmp/test_pipe";

    @Override
    protected Process startServerProcess() throws IOException {
        return Runtime.getRuntime().exec("java -cp src/main/java org.betriebssysteme.fifo.Server");
    }

    @Override
    protected Process startClientProcess(int packetSize, long TOTAL_DATA_SIZE) throws IOException {
        return Runtime.getRuntime().exec("java -cp src/main/java org.betriebssysteme.fifo.Client " + packetSize + " " + TOTAL_DATA_SIZE);
    }

    @Override
    protected void compileServerAndClient() throws IOException, InterruptedException {
        Process compileServer = Runtime.getRuntime().exec("javac -cp src src/main/java/org/betriebssysteme/fifo/Server.java");
        compileServer.waitFor();
        compileServer.destroy();

        Process compileClient= Runtime.getRuntime().exec("javac -cp src src/main/java/org/betriebssysteme/fifo/Client.java");
        compileClient.waitFor();
        compileClient.destroy();
    }

    @Override
    protected void setupCommunication() throws IOException {
        Path pipePath = Paths.get(PIPE_NAME);
        Files.deleteIfExists(pipePath); // Delete the pipe if it already exists
        Files.createFile(pipePath); // Create a new pipe
    }

    @Override
    protected void disassembleCommunication() throws IOException {
        Path pipePath = Paths.get(PIPE_NAME);
        Files.deleteIfExists(pipePath); // Delete the pipe if it exists
    }


}
