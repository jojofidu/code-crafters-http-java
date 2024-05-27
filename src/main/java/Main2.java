import java.io.IOException;
import java.net.ServerSocket;


public class Main2 {
    private static final int SERVER_PORT = 4221;

    public static void main(String[] args) {
        try {
            var serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setReuseAddress(true);

            while (true) {
                new Client(serverSocket.accept()).run();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
