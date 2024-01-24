import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class EchoTCP extends Thread {
    private final int threads;

    @Override
    public void run() {
        ServerSocket serverSocket;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        try {
            serverSocket = new ServerSocket(8080, 50);
        } catch (IOException e) {
            throw new RuntimeException("Can not create server socket: " + e);
        }

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executorService.execute(echo(socket));
            } catch (IOException e) {
                throw new RuntimeException("Connection error: " + e);
            }
        }
    }

    private Runnable echo(Socket socket) {
        return () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                 socket) {
                String response = Thread.currentThread() + ": " + reader.readLine();
                System.out.println("writing response: " + response);
                writer.write(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
