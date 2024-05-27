import http.HttpMethod;
import http.HttpRequest;
import http.HttpStatus;
import http.InvalidHttpRequest;
import http.ResponseEntity;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Client implements Runnable {
    private static final byte[] HTTP_VERSION_BYTES = "HTTP/1.1".getBytes(StandardCharsets.UTF_8);
    private static final byte[] CRLF_BYTES = "\r\n".getBytes(StandardCharsets.UTF_8);
    private static final byte[] WHITE_SPACE_BYTES = " ".getBytes(StandardCharsets.UTF_8);
    private static final byte[] COLON_BYTES = ":".getBytes(StandardCharsets.UTF_8);

    private static final Set<String> SUPPORTED_HTTP_ENCODINGS = Set.of("gzip");

    private final Socket socket;
    private final Controller controller = new Controller();

    @Override
    public void run() {
        try {
            var id = UUID.randomUUID();
            System.out.println("New client connection."+id);
            HttpRequest request = null;
            ResponseEntity response;
            try {
                request = HttpRequest.parse(socket.getInputStream());
                response = handleRequestMappings(request, id);
            } catch (InvalidHttpRequest e) {
                response = ResponseEntity.plainText(HttpStatus.BAD_REQUEST, e.getMessage());
            } catch (IOException e){
                throw new IOException(e);
            } catch (Exception e) {
                e.printStackTrace();
                response = ResponseEntity.plainText(HttpStatus.INTERNAL_ERROR, e.getMessage());
            }
            send(request, response, socket.getOutputStream());
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client disconnected.");
    }

    ResponseEntity handleRequestMappings(HttpRequest request, UUID id) {
        System.out.println(id +"   "+request);
        if (HttpMethod.GET.equals(request.getMethod()) && "/".equals(request.getPath())) {
            return controller.checkAlive();
        } else if (HttpMethod.GET.equals(request.getMethod()) && request.getPath().startsWith("/echo")) {
            return controller.echoRequestParam(request.getPath());
        } else if (HttpMethod.GET.equals(request.getMethod()) && request.getPath().startsWith("/user-agent")) {
            return controller.echoUserAgent(request.getHeaders().get("User-Agent"));
        } else if (HttpMethod.GET.equals(request.getMethod()) && request.getPath().startsWith("/files")) {
            // validate path breakage number (maybe separate method)
            var requestPathSplit = request.getPath().replace("/files/", "").split("/");
            if (Main.directory == null || requestPathSplit.length > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST);
            }
            /* If no need of validation above, just use (simplicity):
                var filePath = path.substring("/files/".length())
             */
            var filePath = requestPathSplit[0];
            return controller.getFileContents(filePath);
        } else if (HttpMethod.POST.equals(request.getMethod()) && request.getPath().startsWith("/files")) {
            // validate path breakage number (maybe separate method)
            var requestPathSplit = request.getPath().replace("/files/", "").split("/");
            if (Main.directory == null || requestPathSplit.length > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST);
            }
            var filePath = requestPathSplit[0];
            return controller.storeFile(filePath, request.getBody());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND);
    }

    public void send(HttpRequest request, ResponseEntity response, OutputStream outputStream) throws IOException {
        /* workds, but I don't think it its suposed to send entire thing straight out, maybe even use bufferedWriter

        var x = "HTTP/1.1 " + response.getStatus().getLine() + "\r\n";
        for (Entry<String, String> header : response.getHeaders().entrySet()) {
            x += header.getKey() + ": " + header.getValue() + "\r\n";
        }
        x += "\r\n";
        if (response.getBody() != null) {
            x += response.getBody();
        }
        outputStream.write(x.getBytes(StandardCharsets.UTF_8));
         */

        processEncoding(request, response);

        outputStream.write(HTTP_VERSION_BYTES);
        outputStream.write(WHITE_SPACE_BYTES);
        outputStream.write(response.getStatus().getLine().getBytes(StandardCharsets.UTF_8));
        outputStream.write(CRLF_BYTES);

        for (Entry<String, String> header : response.getHeaders().entrySet()) {
            outputStream.write(header.getKey().getBytes(StandardCharsets.UTF_8));
            outputStream.write(COLON_BYTES);
            outputStream.write(WHITE_SPACE_BYTES);
            outputStream.write(header.getValue().getBytes(StandardCharsets.UTF_8));
            outputStream.write(CRLF_BYTES);
        }
        outputStream.write(CRLF_BYTES);

        if (response.getBody() != null) {
            if (response.getBody() instanceof String) {
                // shows as text on curl, which is correct!
                outputStream.write(((String) response.getBody()).getBytes());
            } else {
                // shows as bytes on curl, so incorrect! but maybe useful for files? but what if body is not a string? or is it mandatory? its not...
                System.out.println("test this!");
                var objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(response.getBody());
            }
        }
    }

    private void processEncoding(HttpRequest request, ResponseEntity response) {
        var requestHttpEncoding = request != null
            ? request.getHeaders().get(HttpHeader.ACCEPT_ENCODING.key)
            : null;
        if (requestHttpEncoding != null) {
            if (SUPPORTED_HTTP_ENCODINGS.contains(requestHttpEncoding.toLowerCase(Locale.ROOT))) {
                response.addHeader(HttpHeader.CONTENT_ENCODING.key, requestHttpEncoding);
            }
        }
    }
}
