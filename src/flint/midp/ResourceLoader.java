package flint.midp;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Resolves MIDlet-suite resources directly from the running application JAR.
 *
 * <p>This is a FlintOS implementation class, not a MIDP public API.</p>
 */
public final class ResourceLoader {
    private static String suiteDirectory;

    private ResourceLoader() {
    }

    /**
     * Selects the filesystem-safe identifier for the current MIDlet suite.
     *
     * @param directory suite identifier, or {@code null} to use
     *        {@code default}
     */
    public static synchronized void setSuiteDirectory(String directory) {
        suiteDirectory = directory;
    }

    /** Returns the filesystem-safe identifier of the current MIDlet suite. */
    public static synchronized String getSuiteDirectory() {
        if(suiteDirectory == null || suiteDirectory.length() == 0)
            return "default";
        return suiteDirectory;
    }

    /** Opens a resource belonging to the current MIDlet suite. */
    public static InputStream open(String name) throws FileNotFoundException {
        if(name == null) {
            throw new NullPointerException("name");
        }

        String relativeName = name.startsWith("/") ? name.substring(1) : name;
        byte[] data = readProgramResource(relativeName);
        if(data == null)
            throw new FileNotFoundException(name);
        return new ByteArrayInputStream(data);
    }

    public static native byte[] readProgramResource(String name);
}
