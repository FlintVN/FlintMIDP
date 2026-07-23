package javax.microedition.midlet;

import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;

/**
 * FlintOS bridge between its application manager and the protected MIDlet
 * lifecycle callbacks.
 *
 * <p>This class is a FlintOS implementation extension and is not part of the
 * MIDP 2.0 application API.</p>
 */
public final class MIDletLifecycle {
    /** MIDlet has been constructed or paused. */
    public static final int PAUSED = 0;

    /** MIDlet is running. */
    public static final int ACTIVE = 1;

    /** MIDlet has terminated. */
    public static final int DESTROYED = 2;

    private static MIDlet currentMIDlet;
    private static int state = DESTROYED;
    private static boolean resumeRequested;
    private static boolean amsCreating;

    /** Suite-level property table (AMS provides, shared across all MIDlets in the JAR). */
    private static MIDletSuiteProperties suiteProperties;

    private MIDletLifecycle() {
    }

    /**
     * Returns the value of a MIDlet suite property, or null if absent.
     *
     * <p>Property table must have been prepared by {@link #launch} before
     * the MIDlet subclass constructor runs.</p>
     */
    static synchronized String getSuiteProperty(String key) {
        if(key == null)
            throw new NullPointerException("key");
        MIDletSuiteProperties p = suiteProperties;
        if(p == null)
            throw new IllegalStateException("MIDlet suite properties not initialized — "
                    + "AMS must call prepareSuite() before constructing the MIDlet");
        return p.get(key);
    }

    /** FlintOS entry point for launching the MIDlet selected from its manifest. */
    public static void main(String[] args) throws Exception {
        if(args == null || args.length != 1 || args[0] == null) {
            throw new IllegalArgumentException("Missing MIDlet class name");
        }

        // Property table loaded BEFORE constructor (games may call
        // getAppProperty() from within the constructor chain).
        prepareSuite();

        beginMIDletCreation();

        Object instance;
        try {
            instance = Class.forName(args[0].replace('/', '.'))
                    .getConstructor()
                    .newInstance();
        } catch(Exception exception) {
            abortMIDletCreation();
            throw exception;
        } catch(Error error) {
            abortMIDletCreation();
            throw error;
        }

        if(!(instance instanceof MIDlet)) {
            abortMIDletCreation();
            throw new IllegalArgumentException(
                    "MIDlet class does not extend MIDlet");
        }

        MIDlet midlet = (MIDlet)instance;
        completeMIDletCreation(midlet);
        start(midlet);
    }

    /**
     * Loads and validates suite properties from the current JAR manifest.
     * Called once per launch.  Exposed publicly so the FlintOS App Manager
     * can pre-load properties before the MIDlet is constructed.
     *
     * @throws IOException when the manifest is missing or unparseable
     */
    public static synchronized void prepareSuite() throws IOException {
        // Allow re-load after hot-reload (new JAR may have different properties).
        suiteProperties = MIDletSuitePropertyLoader.loadCurrentSuite();
    }

    /**
     * Registers a newly constructed MIDlet with the FlintOS application
     * manager.
     *
     * <p>Normal MIDlet construction is registered automatically from the
     * {@link MIDlet} constructor. This method remains available for FlintOS
     * integration code that already owns a MIDlet instance.</p>
     *
     * @param midlet MIDlet to manage
     */
    public static synchronized void attach(MIDlet midlet) {
        if(midlet == null) {
            throw new NullPointerException("midlet");
        }
        if(currentMIDlet == midlet) {
            return;
        }
        if(currentMIDlet != null && state != DESTROYED) {
            throw new IllegalStateException("A MIDlet is already active");
        }

        currentMIDlet = midlet;
        state = PAUSED;
        resumeRequested = false;
    }

    /**
     * Registers the MIDlet while its superclass constructor is running.
     *
     * @param midlet MIDlet currently being constructed by the AMS
     * @throws SecurityException if construction was not initiated by the AMS
     */
    static synchronized void attachFromConstructor(MIDlet midlet) {
        if(midlet == null)
            throw new NullPointerException("midlet");
        if(!amsCreating)
            throw new SecurityException(
                    "MIDlets should not attempt to create other MIDlets");
        if(currentMIDlet != null)
            throw new SecurityException("A MIDlet is already being created");

        currentMIDlet = midlet;
        state = PAUSED;
        resumeRequested = false;
    }

    /**
     * Starts or resumes the managed MIDlet.
     *
     * @param midlet MIDlet to start
     * @throws MIDletStateChangeException if the MIDlet cannot start now
     */
    public static synchronized void start(MIDlet midlet)
            throws MIDletStateChangeException {
        requireManaged(midlet);
        if(state == DESTROYED) {
            throw new IllegalStateException("MIDlet has been destroyed");
        }
        if(state == ACTIVE) {
            return;
        }

        state = ACTIVE;
        resumeRequested = false;
        try {
            midlet.startApp();
        } catch(MIDletStateChangeException exception) {
            state = PAUSED;
            throw exception;
        } catch(RuntimeException exception) {
            state = DESTROYED;
            throw exception;
        }
    }

    /**
     * Pauses the managed MIDlet.
     *
     * @param midlet MIDlet to pause
     */
    public static synchronized void pause(MIDlet midlet) {
        requireManaged(midlet);
        if(state != ACTIVE) {
            return;
        }

        midlet.pauseApp();
        state = PAUSED;
    }

    /**
     * Destroys the managed MIDlet.
     *
     * @param midlet MIDlet to destroy
     * @param unconditional whether destruction may not be rejected
     * @throws MIDletStateChangeException if conditional destruction is
     *         rejected
     */
    public static synchronized void destroy(
            MIDlet midlet, boolean unconditional)
            throws MIDletStateChangeException {
        requireManaged(midlet);
        if(state == DESTROYED) {
            return;
        }

        try {
            midlet.destroyApp(unconditional);
        } catch(MIDletStateChangeException exception) {
            if(!unconditional) {
                throw exception;
            }
        } finally {
            if(unconditional) {
                state = DESTROYED;
            }
        }

        state = DESTROYED;
    }

    /** Returns the state of the managed MIDlet. */
    public static synchronized int getState(MIDlet midlet) {
        requireManaged(midlet);
        return state;
    }

    public static synchronized boolean isAMSCreating() {
        return amsCreating;
    }

    private static synchronized void beginMIDletCreation() {
        if(currentMIDlet != null && state != DESTROYED) {
            throw new IllegalStateException("A MIDlet is already active");
        }

        currentMIDlet = null;
        state = DESTROYED;
        resumeRequested = false;
        amsCreating = true;
    }

    private static synchronized void completeMIDletCreation(MIDlet midlet) {
        if(!amsCreating) {
            throw new IllegalStateException("MIDlet creation is not in progress");
        }
        if(currentMIDlet != midlet) {
            abortMIDletCreation();
            throw new IllegalStateException(
                    "MIDlet was not registered during construction");
        }

        amsCreating = false;
    }

    private static synchronized void abortMIDletCreation() {
        amsCreating = false;
        currentMIDlet = null;
        state = DESTROYED;
        resumeRequested = false;
    }

    static synchronized boolean cancelPendingRequest(MIDlet midlet) {
        requireManaged(midlet);
        boolean requested = resumeRequested;
        resumeRequested = false;
        return requested;
    }

    static synchronized boolean isTrusted(MIDlet midlet) {
        requireManaged(midlet);
        return false;
    }

    /** Returns and clears a pending resume request. */
    public static synchronized boolean consumeResumeRequest(MIDlet midlet) {
        requireManaged(midlet);
        boolean requested = resumeRequested;
        resumeRequested = false;
        return requested;
    }

    static synchronized void notifyDestroyed(MIDlet midlet) {
        requireManaged(midlet);
        state = DESTROYED;
        currentMIDlet = null;
        resumeRequested = false;
    }

    static synchronized void notifyPaused(MIDlet midlet) {
        requireManaged(midlet);
        if(state == ACTIVE) {
            state = PAUSED;
        }
    }

    static synchronized void resumeRequest(MIDlet midlet) {
        requireManaged(midlet);
        if(state == PAUSED) {
            resumeRequested = true;
        }
    }

    static boolean platformRequest(MIDlet midlet, String url)
            throws ConnectionNotFoundException {
        requireManaged(midlet);
        throw new ConnectionNotFoundException("No platform handler for " + url);
    }

    static int checkPermission(MIDlet midlet, String permission) {
        requireManaged(midlet);
        return -1;
    }

    private static void requireManaged(MIDlet midlet) {
        if(midlet == null) {
            throw new NullPointerException("midlet");
        }
        if(midlet != currentMIDlet) {
            throw new IllegalStateException("MIDlet is not managed");
        }
    }
}