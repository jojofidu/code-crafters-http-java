public enum HttpStatus {
    OK("200 OK"),
    BAD_REQUEST("400 Bad Request"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_ERROR("500 Internal Error");

    private final String line;

    private HttpStatus(String line) {
        this.line = line;
    }

    public String getLine() {return this.line;}
}
