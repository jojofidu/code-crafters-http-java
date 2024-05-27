import http.HttpRequest;
import http.HttpStatus;
import http.ResponseEntity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    public ResponseEntity checkAlive() {
        return ResponseEntity.status(HttpStatus.OK);
    }

    public ResponseEntity echoRequestParam(String path) {
        String echo = path.split("/", 3)[2];
        return ResponseEntity.plainText(HttpStatus.OK, echo);
    }

    public ResponseEntity echoUserAgent(String userAgentHeaderValue) {
        return ResponseEntity.plainText(HttpStatus.OK, userAgentHeaderValue);
    }
}
