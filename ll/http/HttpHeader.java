package http;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    USER_AGENT("User-Agent");

    public final String key;

    private HttpHeader(String key) {
        this.key = key;
    }
}