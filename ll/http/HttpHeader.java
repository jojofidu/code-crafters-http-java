package http;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    USER_AGENT("User-Agent"),
    CONTENT_ENCODING("Content-Encoding"),
    ACCEPT_ENCODING("Accept-Encoding");

    public final String key;

    private HttpHeader(String key) {
        this.key = key;
    }
}