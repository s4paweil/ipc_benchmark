# ipc_benchmark
Ein kleiner IPC-Benchmarktest in dem ein Client Prozess unidirektional Daten an einen Server sendet.
Es werden folgende IPC-Verfahren verglichen:
- Named Pipes (FIFO)
- (Unnamed) Pipes
- TCP
- Unix Domain Sockets
- ZeroMQ


**1. Kompilieren**
```bash
mvn package
```

**2. Ausführen**
```bash
# java -jar ./target/ipc-benchmarktest.jar [totalDataSizeInGB] [packageSizes] [#iteratoins]
# for example packageSizes = 1024,2048,4096
java -jar ./target/ipc-benchmarktest.jar 2 2048,4096 2
```

**3. Beispielausgabe**
Die Ergebnisse eines Durchlaufs werden außerden in output.csv gespeichert.
```text
java -jar target/ipc-benchmarktest.jar 2 4096 1
TCP-Socket
Packet Size: 4096, Iterations: 1, Total Data sent: 2048 MB
Durchschnittliche Gesamtdauer: 6.623994152 Sekunden für 2048 MB
Durchschnittliche Nachrichten pro Sekunde (NPS): 79149.82833155134
Durchschnittlicher Durchsatz: 309.1790169201224 MB/s
Durchschnittliche Minimale Latenz: 0.909 ms
Durchschnittliche Maximale Latenz: 23.221 ms
--------------------------------------------
Fifo (Named Pipes)
Packet Size: 4096, Iterations: 1, Total Data sent: 2048 MB
Durchschnittliche Gesamtdauer: 7.115364223 Sekunden für 2048 MB
Durchschnittliche Nachrichten pro Sekunde (NPS): 73683.93009387623
Durchschnittlicher Durchsatz: 287.82785192920403 MB/s
Durchschnittliche Minimale Latenz: 5.07 ms
Durchschnittliche Maximale Latenz: 1221.461 ms
--------------------------------------------
Packet Size: 4096, Iterations: 1, Total Data sent: 2048 MB
Durchschnittliche Gesamtdauer: 6.778863289 Sekunden für 2048 MB
Durchschnittliche Nachrichten pro Sekunde (NPS): 77341.58038719521
Durchschnittlicher Durchsatz: 302.1155483874813 MB/s
Durchschnittliche Minimale Latenz: 0.014 ms
Durchschnittliche Maximale Latenz: 15.743 ms
--------------------------------------------
Unnames Pipes (StdIn/StdOut)
Packet Size: 4096, Iterations: 1, Total Data sent: 2048 MB
Durchschnittliche Gesamtdauer: 4.497910297 Sekunden für 2048 MB
Durchschnittliche Nachrichten pro Sekunde (NPS): 116562.5735910491
Durchschnittlicher Durchsatz: 455.32255309003557 MB/s
Durchschnittliche Minimale Latenz: 0.01 ms
Durchschnittliche Maximale Latenz: 64.797 ms
--------------------------------------------
ZeroMQ-Socket
Packet Size: 4096, Iterations: 1, Total Data sent: 2048 MB
Durchschnittliche Gesamtdauer: 9.036027585 Sekunden für 2048 MB
Durchschnittliche Nachrichten pro Sekunde (NPS): 58021.95655869061
Durchschnittlicher Durchsatz: 226.6482678073852 MB/s
Durchschnittliche Minimale Latenz: 28.209 ms
Durchschnittliche Maximale Latenz: 324.576 ms
--------------------------------------------
```
