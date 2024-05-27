import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class HttpRequest {
    private static final byte[] CRLF_BYTES = "\r\n".getBytes(StandardCharsets.UTF_8);
    private static final Set<String> SUPPORTED_HTTP_VERSIONS = Set.of("HTTP/1.1");

    private String httpVersion;
    private HttpMethod method;
    private String path;
    private Map<String, String> headers;
    private Optional<String> body;

    public HttpRequest(String httpVersion, HttpMethod method, String path, Map<String, String> headers,
        Optional<String> body) {
        this.httpVersion = httpVersion;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public HttpRequest() {}

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

    public static HttpRequestBuilder builder() {return new HttpRequestBuilder();}

    public String getHttpVersion() {return this.httpVersion;}

    public HttpMethod getMethod() {return this.method;}

    public String getPath() {return this.path;}

    public Map<String, String> getHeaders() {return this.headers;}

    public Optional<String> getBody() {return this.body;}

    public void setHttpVersion(String httpVersion) {this.httpVersion = httpVersion;}

    public void setMethod(HttpMethod method) {this.method = method;}

    public void setPath(String path) {this.path = path;}

    public void setHeaders(Map<String, String> headers) {this.headers = headers;}

    public void setBody(Optional<String> body) {this.body = body;}

    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof HttpRequest))
            return false;
        final HttpRequest other = (HttpRequest) o;
        if (!other.canEqual((Object) this))
            return false;
        final Object this$httpVersion = this.getHttpVersion();
        final Object other$httpVersion = other.getHttpVersion();
        if (this$httpVersion == null ? other$httpVersion != null : !this$httpVersion.equals(other$httpVersion))
            return false;
        final Object this$method = this.getMethod();
        final Object other$method = other.getMethod();
        if (this$method == null ? other$method != null : !this$method.equals(other$method))
            return false;
        final Object this$path = this.getPath();
        final Object other$path = other.getPath();
        if (this$path == null ? other$path != null : !this$path.equals(other$path))
            return false;
        final Object this$headers = this.getHeaders();
        final Object other$headers = other.getHeaders();
        if (this$headers == null ? other$headers != null : !this$headers.equals(other$headers))
            return false;
        final Object this$body = this.getBody();
        final Object other$body = other.getBody();
        if (this$body == null ? other$body != null : !this$body.equals(other$body))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {return other instanceof HttpRequest;}

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $httpVersion = this.getHttpVersion();
        result = result * PRIME + ($httpVersion == null ? 43 : $httpVersion.hashCode());
        final Object $method = this.getMethod();
        result = result * PRIME + ($method == null ? 43 : $method.hashCode());
        final Object $path = this.getPath();
        result = result * PRIME + ($path == null ? 43 : $path.hashCode());
        final Object $headers = this.getHeaders();
        result = result * PRIME + ($headers == null ? 43 : $headers.hashCode());
        final Object $body = this.getBody();
        result = result * PRIME + ($body == null ? 43 : $body.hashCode());
        return result;
    }

    public String toString() {
        return "HttpRequest(httpVersion=" + this.getHttpVersion() + ", method=" + this.getMethod() + ", path="
            + this.getPath() + ", headers=" + this.getHeaders() + ", body=" + this.getBody() + ")";
    }

    public static class HttpRequestBuilder {

        private String httpVersion;
        private HttpMethod method;
        private String path;
        private Map<String, String> headers;
        private Optional<String> body;

        HttpRequestBuilder() {}

        public HttpRequestBuilder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public HttpRequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public HttpRequestBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequestBuilder body(Optional<String> body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(httpVersion, method, path, headers, body);
        }

        public String toString() {
            return "HttpRequest.HttpRequestBuilder(httpVersion=" + this.httpVersion + ", method=" + this.method
                + ", path="
                + this.path + ", headers=" + this.headers + ", body=" + this.body + ")";
        }
    }
}
