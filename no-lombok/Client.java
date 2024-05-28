import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class Client implements Runnable {
    private static final byte[] HTTP_VERSION_BYTES = "HTTP/1.1".getBytes(StandardCharsets.UTF_8);
    private static final byte[] CRLF_BYTES = "\r\n".getBytes(StandardCharsets.UTF_8);
    private static final byte[] WHITE_SPACE_BYTES = " ".getBytes(StandardCharsets.UTF_8);
    private static final byte[] COLON_BYTES = ":".getBytes(StandardCharsets.UTF_8);

    private static final Set<String> SUPPORTED_HTTP_ENCODINGS = Set.of("gzip");

    private final Socket socket;
    private final Controller controller = new Controller();

    public Client(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            var id = UUID.randomUUID();
            System.out.println("V2:: New client connection."+id);
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
        var encodedBody = processEncoding(request, response);

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
                outputStream.write(encodedBody.orElse(((String) response.getBody()).getBytes()));
            }
            // TODO it could be bytes[], not as of right now tho
        }
    }

    private Optional<byte[]> processEncoding(HttpRequest request, ResponseEntity response) {
        var encodedBody = checkEncoding(request, response)
            .map(acceptedSupportedEncoding -> {
                response.addHeader(HttpHeader.CONTENT_ENCODING.key, acceptedSupportedEncoding);
                if (acceptedSupportedEncoding.toLowerCase(Locale.ROOT).equals("gzip")) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                        gzipOutputStream.write(((String) response.getBody()).getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return byteArrayOutputStream.toByteArray();
                }
                return null;
            });

        encodedBody.ifPresent(b -> response.addHeader(HttpHeader.CONTENT_LENGTH.key, String.valueOf(b.length)));

        return encodedBody;
    }

    private Optional<String> checkEncoding(HttpRequest request, ResponseEntity response) {
        return Optional.ofNullable(request)
            .map(httpRequest -> httpRequest.getHeaders().get(HttpHeader.ACCEPT_ENCODING.key))
            .flatMap(e -> Arrays.stream(e.split(","))
                .map(String::trim)
                .map(encoding -> encoding.toLowerCase(Locale.ROOT))
                .filter(SUPPORTED_HTTP_ENCODINGS::contains)
                .findFirst());
    }
}
