package javax.microedition.midlet;

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
    // Properties are cached in MIDletLifecycle.getSuiteProperty().
    // MIDlet holds no property storage — it delegates to the AMS.

    /**
     * Creates a MIDlet in the paused state.
     *
     * @throws SecurityException if the application management software is
     *         not creating the MIDlet
     */
    @SuppressWarnings("this-escape")
    protected MIDlet() {
        /*
         * Intentional controlled escape:
         * MIDlet constructors may call MIDP services such as getAppProperty().
         * MIDletLifecycle keeps this instance private until the complete
         * constructor chain has returned.
         *
         * MIDP 2.0 §1.1.2: Only the AMS may create MIDlets.
         */
        if(!MIDletLifecycle.isAMSCreating())
            throw new SecurityException(
                    "MIDlets should not attempt to create other MIDlets");
        MIDletLifecycle.attachFromConstructor(this);
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

    /** FlintOS launcher entry point for invoking the protected lifecycle API. */
    public final void startApp0() throws MIDletStateChangeException {
        startApp();
    }

    /** FlintOS debugger entry point for invoking the protected pause callback. */
    public final void pauseApp0() {
        pauseApp();
    }

    /** FlintOS debugger entry point for invoking the protected destroy callback. */
    public final void destroyApp0(boolean unconditional)
            throws MIDletStateChangeException {
        destroyApp(unconditional);
    }
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
     *
     * <p>Invoking this method will have no effect if the MIDlet is
     * destroyed, or if it has not yet been started.</p>
     */
    public final void notifyPaused() {
        int currentState = MIDletLifecycle.getState(this);
        if (currentState == MIDletLifecycle.DESTROYED
                || currentState == MIDletLifecycle.PAUSED) {
            return;
        }
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
        return MIDletLifecycle.getSuiteProperty(key);
    }

    /**
     * Requests that the device handle a URL using an external application.
     *
     * @param url URL to handle; an empty string cancels any pending requests
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
        // Empty string cancels pending requests
        if (url.isEmpty()) {
            return MIDletLifecycle.cancelPendingRequest(this);
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

}