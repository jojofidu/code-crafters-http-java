import java.io.IOException;
import java.net.ServerSocket;


public class Main {
    private static final int SERVER_PORT = 4221;
    public static String directory;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--directory".equals(args[i])) {
                directory = args[i+1];
                break;
            }
        }

        try (var serverSocket = new ServerSocket(SERVER_PORT)) {
            serverSocket.setReuseAddress(true);

            while (true) {
                //If in java 21, could use Thread.virtualThread() and Client would not extend thread
                new Client(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
