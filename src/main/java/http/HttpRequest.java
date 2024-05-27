package http;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HttpRequest {
    private static final byte[] CRLF_BYTES = "\r\n".getBytes(StandardCharsets.UTF_8);
    private static final Set<String> SUPPORTED_HTTP_VERSIONS = Set.of("HTTP/1.1");

    private String httpVersion;
    private HttpMethod method;
    private String path;
    private Map<String, String> headers;
    private Optional<String> body;

    public static HttpRequest parse(InputStream inputStream) {
        var scanner = new Scanner(inputStream);

        var requestBuilder = HttpRequest.builder();

        requestBuilder.method(HttpMethod.valueOf(scanner.next()));

        var path = scanner.next();
        if (!path.startsWith("/")) {
            throw new InvalidHttpRequest("Path does not start with '/'");
        }
        requestBuilder.path(path);

        var version = scanner.next();
        if (!SUPPORTED_HTTP_VERSIONS.contains(version)) {
            throw new InvalidHttpRequest("Path does not start with '/'");
        }
        requestBuilder.httpVersion(version);

        final var remaining = scanner.nextLine();
        if (!remaining.isEmpty()) {
            throw new InvalidHttpRequest("content after version: " + remaining);
        }

        var headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        String line;
        // TODO unsure how this if behaves
        while(!(line = scanner.nextLine()).isEmpty()) {
            var headerParts = line.split(":", 2);
            headers.put(headerParts[0], headerParts[1].stripLeading());
        }
        requestBuilder.headers(headers);

        // TODO body

        return requestBuilder.build();
    }
}
