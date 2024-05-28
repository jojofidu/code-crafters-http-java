public enum HttpStatus {
    OK("200 OK"),
    CREATED("201 Created"),
    BAD_REQUEST("400 Bad Request"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_ERROR("500 Internal Error");

    public final String line;

    private HttpStatus(String line) {
        this.line = line;
    }

    public String getLine() {return this.line;}
}
