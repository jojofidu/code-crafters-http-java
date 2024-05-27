import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class ResponseEntity {

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

    private static String $default$httpVersion() {return "HTTP/1.1";}

    public static ResponseEntityBuilder builder() {return new ResponseEntityBuilder();}

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

    public String getHttpVersion() {return this.httpVersion;}

    public HttpStatus getStatus() {return this.status;}

    public Map<String, String> getHeaders() {return this.headers;}

    public Object getBody() {return this.body;}

    public static class ResponseEntityBuilder {

        private String httpVersion$value;
        private boolean httpVersion$set;
        private HttpStatus status;
        private Map<String, String> headers;
        private Object body;

        ResponseEntityBuilder() {}

        public ResponseEntityBuilder httpVersion(String httpVersion) {
            this.httpVersion$value = httpVersion;
            this.httpVersion$set = true;
            return this;
        }

        public ResponseEntityBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ResponseEntityBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public ResponseEntityBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public ResponseEntity build() {
            String httpVersion$value = this.httpVersion$value;
            if (!this.httpVersion$set) {
                httpVersion$value = ResponseEntity.$default$httpVersion();
            }
            return new ResponseEntity(httpVersion$value, status, headers, body);
        }

        public String toString() {
            return "ResponseEntity.ResponseEntityBuilder(httpVersion$value=" + this.httpVersion$value + ", status="
                + this.status + ", headers=" + this.headers + ", body=" + this.body + ")";
        }
    }
}
