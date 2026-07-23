package javax.microedition.io;

import java.io.IOException;

/* Stub: the game calls Connector.open once. We don't support GCF connections, so report
 * not-found and let the game's try/catch handle it. */
public class Connector {
    public static final int READ = 1, WRITE = 2, READ_WRITE = 3;

    private Connector() {
    }

    public static Connection open(String name) throws IOException {
        throw new ConnectionNotFoundException("Connector not supported: " + name);
    }
    public static Connection open(String name, int mode) throws IOException {
        throw new ConnectionNotFoundException("Connector not supported: " + name);
    }
    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        throw new ConnectionNotFoundException("Connector not supported: " + name);
    }
}
