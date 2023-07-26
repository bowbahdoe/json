package dev.mccue.json;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HangingReaderTest {
    @Test
    public void hangingReaderTest() throws IOException, InterruptedException {
        var read = Collections.synchronizedList(new ArrayList<Json>());

        int serverPort = Math.max((int) (Math.random() * 10000), 1000);

        try (var serverSocket = new ServerSocket(serverPort)) {
            var serverThread = new Thread(() -> {
                try {
                    var client = serverSocket.accept();
                    var is = client.getInputStream();
                    var os = client.getOutputStream();

                    var reader = Json.reader(new InputStreamReader(is));
                    var writer = new OutputStreamWriter(os);

                    while (true) {
                        var ping = reader.read();
                        if (ping != null) {
                            read.add(ping);
                            Json.write(
                                    Json.objectBuilder()
                                            .put("pong", "yes")
                                            .build(),
                                    writer
                            );

                            writer.flush();
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new UncheckedIOException(e);
                }
            });
            serverThread.setDaemon(true);
            serverThread.start();

            Thread.sleep(100);

            try (var client = new Socket("0.0.0.0", serverPort)) {
                var is = client.getInputStream();
                var os = client.getOutputStream();

                var reader = Json.reader(new InputStreamReader(is));
                var writer = new OutputStreamWriter(os);

                for (int i = 0; i < 5; i++) {
                    Json.write(
                            Json.objectBuilder()
                                    .put("ping", "yes")
                                    .build(),
                            writer
                    );
                    writer.flush();
                    var pong = reader.read();
                    read.add(pong);
                }
            }

            assertEquals(
                    List.of(
                            Json.objectBuilder()
                                    .put("ping", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("pong", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("ping", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("pong", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("ping", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("pong", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("ping", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("pong", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("ping", "yes")
                                    .build(),
                            Json.objectBuilder()
                                    .put("pong", "yes")
                                    .build()
                    ),

                    read
            );
        }
    }
}
