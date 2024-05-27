import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
  private static int SERVER_PORT = 4221;
  public static void main(String[] args) {
    System.out.println("Starting program");

    ServerSocket serverSocket;
    Socket clientSocket;
    try {
      serverSocket = new ServerSocket(SERVER_PORT);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      System.out.println("accepted new connection");

      clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
