import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {

  private static final int SERVER_PORT = 4221;
  private static final String CRLF = "\r\n";
  private static final String HTTP_VERSION = "HTTP/1.1";

  private enum HttpStatus {
    OK("200 OK"),
    NOT_FOUND("404 Not Found");

    private String key;
    HttpStatus(String status) {
      this.key = status;
    }
  }

  public static void main(String[] args) {
    System.out.println("Starting program");

    ServerSocket serverSocket;
    Socket clientSocket;
    try {
      serverSocket = new ServerSocket(SERVER_PORT);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      System.out.println("accepted new connection");

      String request = new String(clientSocket.getInputStream().readNBytes(clientSocket.getInputStream().available()));
      String response = handleRequestMappings(request);

      clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  static String handleRequestMappings(String request) {
    String[] requestLine = request.split(CRLF)[0].split(" ");
    if (requestLine[0].equals("GET") && requestLine[1].equals("/")) {
      return handleRoot();
    } else if (requestLine[0].equals("GET") && requestLine[1].startsWith("/echo")) {
      return handleEcho(requestLine[1]);
    } else {
      return createHttpResponse(HttpStatus.NOT_FOUND);
    }

  }

  static String handleRoot() {
    return createHttpResponse(HttpStatus.OK);
  }

  static String handleEcho(String requestPath) {
    // either split("/) and check it's only 2 or bad_request or this:
    String echo = requestPath.split("/", 3)[2];
    return createPlainTextHttpResponse(HttpStatus.OK, echo);
  }

  static String createHttpResponse(HttpStatus httpStatus) {
    return String.format("%s %s%s%s", HTTP_VERSION, httpStatus.key, CRLF, CRLF);
  }

  static String createPlainTextHttpResponse(HttpStatus httpStatus, String body) {
    return HTTP_VERSION + " "  + httpStatus.key + CRLF
        + "Content-Type: " + "text/plain" + CRLF
        + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + CRLF
        + CRLF
        + body;
  }
}
