package javax.microedition.midlet;

/**
 * Indicates that a requested MIDlet lifecycle transition could not be
 * completed.
 */
public class MIDletStateChangeException extends Exception {
    /**
     * Creates an exception without a detail message.
     */
    public MIDletStateChangeException() {
        super();
    }

    /**
     * Creates an exception with a detail message.
     *
     * @param message detail message
     */
    public MIDletStateChangeException(String message) {
        super(message);
    }
}
