package javax.microedition.midlet;

import java.util.Hashtable;

/**
 * Immutable snapshot of the MIDlet suite properties supplied by the AMS.
 *
 * <p>Properties belong to the <strong>MIDlet suite</strong> (the JAR), not to
 * any one MIDlet instance.  A single JAR may contain several MIDlets sharing
 * the same property table.</p>
 *
 * <p>This class is a FlintOS implementation extension and is not part of the
 * MIDP 2.0 application API.</p>
 */
final class MIDletSuiteProperties {
    private final Hashtable<String, String> values;

    MIDletSuiteProperties(Hashtable<String, String> values) {
        this.values = values;
    }

    /** Returns the value for the given key, or null when absent. */
    String get(String key) {
        return values.get(key);
    }

    /** Returns true when the given key is present. */
    boolean contains(String key) {
        return values.containsKey(key);
    }

    /** Returns an enumeration of all property keys. */
    java.util.Enumeration<String> keys() {
        return values.keys();
    }

    /** Exposed for MIDletLifecycle.isTrusted queries. */
    boolean isEmpty() {
        return values.isEmpty();
    }
}
