import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

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

    public ResponseEntity getFileContents(String rawFilePath) {
        var filePath = Paths.get(Main.directory, rawFilePath);
        if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
            try {
                var fileContents = Files.readString(filePath);
                // TODO counter-intuitive, fix
                return ResponseEntity.plainText(HttpStatus.OK, fileContents)
                    .addHeader("Content-Type", "application/octet-stream");
            } catch (IOException | OutOfMemoryError | SecurityException e) {
                System.out.println("ERROR reading file in getFileContents...");
                e.printStackTrace();
                return ResponseEntity.plainText(HttpStatus.BAD_REQUEST, "Contact Admin!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND);
        }
    }
}
