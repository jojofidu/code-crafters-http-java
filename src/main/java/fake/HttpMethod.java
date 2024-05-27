package fake;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DEL");

    private final String key;

    private HttpMethod(String key) {
        this.key = key;
    }
}
