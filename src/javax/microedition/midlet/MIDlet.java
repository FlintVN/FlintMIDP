package javax.microedition.midlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.ConnectionNotFoundException;

/**
 * Base class for an application that uses the Mobile Information Device Profile.
 *
 * Reference: Oracle MIDP 2.0 / JSR-118, Section 1.2
 * https://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/
 */
public abstract class MIDlet {
    private static final String MANIFEST_RESOURCE = "/MANIFEST.MF";
    private Hashtable<String, String> applicationProperties;

    protected MIDlet() {}

    protected abstract void startApp() throws MIDletStateChangeException;
    protected abstract void pauseApp();
    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public final void startApp0() throws MIDletStateChangeException { startApp(); }
    public final void pauseApp0() { pauseApp(); }
    public final void destroyApp0(boolean unconditional) throws MIDletStateChangeException { destroyApp(unconditional); }

    public final void notifyDestroyed() {}
    public final void notifyPaused() {}
    public final void resumeRequest() {}

    public final String getAppProperty(String key) {
        if(key == null) throw new NullPointerException("key");
        if(applicationProperties == null) {
            applicationProperties = new Hashtable<>();
            InputStream stream = getClass().getResourceAsStream(MANIFEST_RESOURCE);
            if(stream != null) {
                try {
                    parseProperties(new String(stream.readAllBytes()), applicationProperties);
                    stream.close();
                } catch(IOException ignored) {}
            }
        }
        return applicationProperties.get(key);
    }

    public final boolean platformRequest(String url) throws ConnectionNotFoundException {
        if(url == null) throw new NullPointerException("url");
        return false;
    }

    public final int checkPermission(String permission) {
        if(permission == null) throw new NullPointerException("permission");
        return -1;
    }

    private static void parseProperties(String content, Hashtable<String, String> properties) {
        int start = 0;
        while(start < content.length()) {
            int end = content.indexOf('\n', start);
            if(end < 0) end = content.length();
            String line = content.substring(start, end).trim();
            int separator = line.indexOf(':');
            if(separator > 0) {
                properties.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
            }
            start = end + 1;
        }
    }
}
