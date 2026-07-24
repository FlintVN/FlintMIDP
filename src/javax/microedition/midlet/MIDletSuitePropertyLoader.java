package javax.microedition.midlet;

import java.io.IOException;
import java.util.Hashtable;
import flint.midp.ResourceLoader;

/**
 * Loads and validates MIDlet suite properties from the current JAR.
 *
 * <p>In the current FlintOS version (JAR-only, no JAD), only
 * {@code /META-INF/MANIFEST.MF} is read.  When JAD support is added, this
 * loader will also parse {@code /application.jad} and apply the MIDP 2.0
 * trusted/untrusted merge rules ({@link #merge}).</p>
 *
 * <p>This class is a FlintOS implementation extension and is not part of the
 * MIDP 2.0 application API.</p>
 */
final class MIDletSuitePropertyLoader {
    private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
    private static final String JAD_PATH = "application.jad";

    private MIDletSuitePropertyLoader() {}

    /**
     * Loads, validates, and returns properties for the current MIDlet suite.
     *
     * <p>The current program / JAR context must have been set by the native
     * App Manager <strong>before</strong> this call.  This guarantees that
     * property data is available before the first MIDlet constructor runs.</p>
     *
     * @throws IOException when the manifest is missing, unparseable, or lacks
     *         required attributes
     */
    static MIDletSuiteProperties loadCurrentSuite() throws IOException {
        byte[] manifestData = ResourceLoader.readProgramResource(MANIFEST_PATH);
        if(manifestData == null)
            throw new IOException("Missing META-INF/MANIFEST.MF in suite JAR");

        Hashtable<String, String> manifest = ManifestParser.parseMainAttributes(manifestData);

        // MIDP 2.0 §1.1.3: required properties
        require(manifest, "MIDlet-Name");
        require(manifest, "MIDlet-Version");
        require(manifest, "MIDlet-Vendor");

        return new MIDletSuiteProperties(manifest);
    }

    /**
     * Loads properties and merges with JAD according to trusted/untrusted rules.
     * <p>Not yet used — reserved for when FlintOS supports signed suites.</p>
     */
    static MIDletSuiteProperties loadAndMerge(boolean trusted) throws IOException {
        MIDletSuiteProperties base = loadCurrentSuite();
        Hashtable<String, String> fromJad = readJad();

        if(fromJad == null || fromJad.isEmpty())
            return base;

        return merge(base, fromJad, trusted);
    }

    private static Hashtable<String, String> readJad() throws IOException {
        byte[] jadData = ResourceLoader.readProgramResource(JAD_PATH);
        if(jadData == null)
            return null;
        return ManifestParser.parseMainAttributes(jadData);
    }

    /**
     * Merges JAD properties into the base manifest according to MIDP 2.0
     * trusted/untrusted rules.
     *
     * <ul>
     *   <li><strong>Untrusted:</strong> JAD overrides manifest.</li>
     *   <li><strong>Trusted (signed):</strong> JAD must not conflict with
     *       manifest; a mismatch is a security error (throw IOException).</li>
     * </ul>
     */
    static MIDletSuiteProperties merge(MIDletSuiteProperties base,
                                        Hashtable<String, String> jad,
                                        boolean trusted) throws IOException {
        Hashtable<String, String> result = new Hashtable<>();
        // Copy base
        for(java.util.Enumeration<String> keys = base.keys(); keys.hasMoreElements();) {
            String k = keys.nextElement();
            result.put(k, base.get(k));
        }

        for(java.util.Enumeration<String> keys = jad.keys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            String jadVal = jad.get(key);
            String manVal = base.get(key);
            if(trusted && manVal != null) {
                if(!manVal.equals(jadVal))
                    throw new IOException("Trusted suite property mismatch: " + key);
                continue; // keep manifest value
            }
            result.put(key, jadVal);
        }
        return new MIDletSuiteProperties(result);
    }

    private static void require(Hashtable<String, String> props, String key)
            throws IOException {
        if(!props.containsKey(key) || props.get(key) == null || props.get(key).isEmpty())
            throw new IOException("Missing required manifest property: " + key);
    }

    // Expose readProgramResource to MIDletLifecycle for backward compat
    static byte[] readManifestRaw() throws IOException {
        return ResourceLoader.readProgramResource(MANIFEST_PATH);
    }
}
