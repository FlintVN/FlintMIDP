package javax.microedition.midlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import flint.midp.ResourceLoader;
import javax.microedition.io.ConnectionNotFoundException;

/**
 * Base class for an application that uses the Mobile Information Device
 * Profile.
 *
 * <p>The application management software creates a MIDlet, starts it, pauses
 * it, and destroys it through the lifecycle callbacks declared by this class.
 * Applications signal voluntary state changes through {@link
 * #notifyPaused()} and {@link #notifyDestroyed()}.</p>
 */
public abstract class MIDlet {
    private static final String MANIFEST_RESOURCE = "/MANIFEST.MF";

    private Hashtable<String, String> applicationProperties;

    /**
     * Creates a MIDlet in the paused state.
     */
    protected MIDlet() {
    }

    /**
     * Signals the MIDlet to enter the active state.
     *
     * @throws MIDletStateChangeException if the MIDlet cannot be started now
     */
    protected abstract void startApp() throws MIDletStateChangeException;

    /**
     * Signals the MIDlet to stop active processing and release shared
     * resources where possible.
     */
    protected abstract void pauseApp();

    /**
     * Signals the MIDlet to save state and release all resources.
     *
     * @param unconditional {@code true} when the MIDlet must be destroyed
     * @throws MIDletStateChangeException if a conditional destroy is rejected
     */
    protected abstract void destroyApp(boolean unconditional)
            throws MIDletStateChangeException;

    /**
     * Notifies the application management software that this MIDlet has
     * entered the destroyed state.
     */
    public final void notifyDestroyed() {
        MIDletLifecycle.notifyDestroyed(this);
    }

    /**
     * Notifies the application management software that this MIDlet has
     * entered the paused state.
     */
    public final void notifyPaused() {
        MIDletLifecycle.notifyPaused(this);
    }

    /**
     * Requests that the application management software start this MIDlet
     * again.
     */
    public final void resumeRequest() {
        MIDletLifecycle.resumeRequest(this);
    }

    /**
     * Returns an application property from the current MIDlet suite.
     *
     * @param key case-sensitive property name
     * @return the property value, or {@code null} when the property is absent
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public final String getAppProperty(String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        if (applicationProperties == null) {
            applicationProperties = loadApplicationProperties();
        }
        return applicationProperties.get(key);
    }

    /**
     * Requests that the device handle a URL using an external application.
     *
     * @param url URL to handle
     * @return {@code true} if the MIDlet suite must exit before the request
     *         can be completed
     * @throws ConnectionNotFoundException if no handler is available
     * @throws NullPointerException if {@code url} is {@code null}
     */
    public final boolean platformRequest(String url)
            throws ConnectionNotFoundException {
        if (url == null) {
            throw new NullPointerException("url");
        }
        return MIDletLifecycle.platformRequest(this, url);
    }

    /**
     * Checks the status of a protected permission for this MIDlet suite.
     *
     * @param permission permission name
     * @return {@code 1} when allowed, {@code 0} when denied, or {@code -1}
     *         when the status is unknown
     * @throws NullPointerException if {@code permission} is {@code null}
     */
    public final int checkPermission(String permission) {
        if (permission == null) {
            throw new NullPointerException("permission");
        }
        return MIDletLifecycle.checkPermission(this, permission);
    }

    private Hashtable<String, String> loadApplicationProperties() {
        Hashtable<String, String> properties = new Hashtable<>();

        try (InputStream stream = ResourceLoader.open(MANIFEST_RESOURCE)) {
            parseProperties(new String(stream.readAllBytes()), properties);
        } catch (IOException ignored) {
            // An absent or unreadable manifest behaves as an empty property set.
        }
        return properties;
    }

    private static void parseProperties(
            String content, Hashtable<String, String> properties) {
        int start = 0;
        while (start < content.length()) {
            int end = content.indexOf('\n', start);
            if (end < 0) {
                end = content.length();
            }

            String line = content.substring(start, end).trim();
            int separator = line.indexOf(':');
            if (separator > 0) {
                String key = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                properties.put(key, value);
            }
            start = end + 1;
        }
    }
}
