package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HttpStatus {
    OK("200 OK"),
    CREATED("201 Created"),
    BAD_REQUEST("400 Bad Request"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_ERROR("500 Internal Error");

    public final String line;
}
