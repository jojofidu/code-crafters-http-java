package http;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DEL");

    private final String key;
}
