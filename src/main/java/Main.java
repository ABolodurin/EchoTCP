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

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        Map<String, String> map = new ConcurrentHashMap<>();
//        List<Thread> list = new ArrayList<>(10);

        EchoTCP tcp = new EchoTCP(8);
        tcp.start();

//        for (int i = 0; i < 10; i++) {
//            list.add(new Thread(() -> {
        Socket socket;
        try {
            socket = new Socket("localhost", 8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                     socket) {
            String request = Thread.currentThread() + ": " + Math.random();

            writer.write(request);
            writer.flush();

            String response = reader.readLine();
            System.out.println(response);
//                    map.put(request, reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//            }));
//        }

//        list.forEach(Thread::start);
//
//        Thread.sleep(10_000);
//        System.out.println(map);
    }
}
