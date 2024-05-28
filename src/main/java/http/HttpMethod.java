package http;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DEL");

    public final String key;
}
