package http;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class ResponseEntity {

    @Default
    private String httpVersion = "HTTP/1.1";
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final Object body;

    public ResponseEntity(String httpVersion, HttpStatus status, Map<String, String> headers, Object body) {
        this.httpVersion = httpVersion;
        this.status = status;
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) {
            this.headers.putAll(headers);
        }
        this.body = body;
    }

    public ResponseEntity addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public static ResponseEntity status(HttpStatus status) {
        return ResponseEntity.builder()
            .status(status)
            .build();
    }

    public static ResponseEntity plainText(HttpStatus status, String body) {
        return ResponseEntity.builder()
            .status(status)
            .headers(Map.of(
                "Content-Type", "text/plain",
                "Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length)
            ))
            .body(body)
            .build();
    }
}
