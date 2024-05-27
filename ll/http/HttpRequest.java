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
    private static final Set<String> SUPPORTED_HTTP_VERSIONS = Set.of("HTTP/1.1");

    private String httpVersion;
    private HttpMethod method;
    private String path;
    private Map<String, String> headers;
    private String body;

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(inputStream));

        /* Cool regex ideia
            static private final Pattern REQUEST_LINE_REGEX = Pattern.compile("(HEAD|GET|POST|PUT|PATCH|DELETE|OPTIONS) (\\S+) HTTP/1.1");

            final Matcher requestLineMatcher = REQUEST_LINE_REGEX.matcher(reader.readLine());
            if (!requestLineMatcher.matches()) {
              throw new InvalidHttpRequestException();
            }

            .method(HttpMethod.valueOf(requestLineMatcher.group(1)))
            .path(requestLineMatcher.group(2))
         */

        var requestBuilder = HttpRequest.builder();

        String initialLine = reader.readLine();
        if (initialLine == null || initialLine.isEmpty()) {
            throw new InvalidHttpRequest("Request line is empty.");
        }
        String[] initialLineParts = initialLine.split(" ");
        if (initialLineParts.length != 3) {
            throw new InvalidHttpRequest("Request line does not have correct number of args.");
        }

        requestBuilder.method(HttpMethod.valueOf(initialLineParts[0]));

        var path = initialLineParts[1];
        if (!path.startsWith("/")) {
            throw new InvalidHttpRequest("Path does not start with '/'");
        }
        requestBuilder.path(path);

        var version = initialLineParts[2];
        if (!SUPPORTED_HTTP_VERSIONS.contains(version)) {
            throw new InvalidHttpRequest("Unsupported HTTP version: " + version);
        }
        requestBuilder.httpVersion(version);

        requestBuilder.headers(parseHeaders(reader));

        requestBuilder.body(parsePayload(reader, requestBuilder.headers));

        return requestBuilder.build();
    }

    private static TreeMap<String, String> parseHeaders(BufferedReader reader) throws IOException {
        var headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            var headerParts = line.split(":", 2);
            if (headerParts.length != 2) {
                throw new InvalidHttpRequest("Invalid header line: " + line);
            }
            headers.put(headerParts[0], headerParts[1].stripLeading());
        }
        return headers;
    }

    private static String parsePayload(BufferedReader reader, Map<String, String> headers) throws IOException {
        if (!headers.containsKey(HttpHeader.CONTENT_LENGTH.key)) {
            return null;
        }
        int maxLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.key));
        StringBuilder body = new StringBuilder(maxLength);
        while (reader.ready()) {
            body.append((char)reader.read());
        }
        return body.toString();
    }
}
