package fake;

public class InvalidHttpRequest extends RuntimeException {

    public InvalidHttpRequest(String msg) {
        super(msg);
    }
}
