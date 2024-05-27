public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DEL");

    public final String key;

    private HttpMethod(String key) {
        this.key = key;
    }
}
