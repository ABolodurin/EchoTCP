import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EchoTCP extends Thread {
    private final Queue<Socket> queue = new ConcurrentLinkedQueue<>();
    private final EchoTCPWorker worker;
    private final ServerSocket serverSocket;

    public EchoTCP(int port, int backlog, int threads) {
        this.worker = new EchoTCPWorker(queue, threads);
        try {
            this.serverSocket = new ServerSocket(port, backlog);
        }catch (IOException e){
            throw new RuntimeException("Could not create server socket: " + e);
        }
    }

    @Override
    public void run() {
        worker.start();

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                queue.add(socket);
            } catch (IOException e) {
                throw new RuntimeException("Connection error: " + e);
            }
        }
    }

}
