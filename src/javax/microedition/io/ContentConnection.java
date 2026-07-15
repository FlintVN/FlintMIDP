package javax.microedition.io;

import java.io.IOException;

public interface ContentConnection extends StreamConnection {
    String getType();

    String getEncoding();

    long getLength();
}
