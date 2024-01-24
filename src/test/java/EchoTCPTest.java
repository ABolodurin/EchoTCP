import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EchoTCPTest {
    @Test
    void echoTCPTest() {
        Map<String, String> map = new ConcurrentHashMap<>();
        List<Thread> list = new ArrayList<>(50);
        EchoTCP tcp = new EchoTCP(8080, 50, 8);

        tcp.start();

        for (int i = 0; i < 50; i++) {
            list.add(new Thread(() -> {
                Socket socket;
                try {
                    socket = new Socket("localhost", 8080);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                     socket) {
                    String request = String.valueOf(Math.random());

                    writer.write(request + "\n");
                    writer.flush();

                    map.put(request, reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        for (String key : map.keySet()) {
            assertEquals(key, map.get(key));
        }
    }

}