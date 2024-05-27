import http.HttpStatus;
import http.ResponseEntity;

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
