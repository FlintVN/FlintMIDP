package flint.midp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Internal stream helpers for Java 8-compatible builds. */
public final class Streams {
    private Streams() {
    }

    public static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while((read = input.read(buffer)) >= 0) {
            if(read > 0) {
                output.write(buffer, 0, read);
            }
        }
        return output.toByteArray();
    }
}
