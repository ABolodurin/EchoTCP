import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class EchoTCPWorker extends Thread {
    private final Queue<Socket> queue;
    private final int threads;

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        while (true) {
            Socket socket = queue.poll();
            if (socket == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else executorService.execute(echo(socket));
        }
    }

    private Runnable echo(Socket socket) {
        return () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                String response = reader.readLine() + "\n";
                writer.write(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
