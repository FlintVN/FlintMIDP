package javax.microedition.midlet;

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

    private MIDletLifecycle() {
    }

    /**
     * Registers a newly constructed MIDlet with the FlintOS application
     * manager.
     *
     * @param midlet MIDlet to manage
     */
    public static synchronized void attach(MIDlet midlet) {
        if(midlet == null) {
            throw new NullPointerException("midlet");
        }
        if(currentMIDlet != null && state != DESTROYED) {
            throw new IllegalStateException("A MIDlet is already active");
        }

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
