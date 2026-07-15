package flint.midp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Resolves MIDlet-suite resources from the FlintOS FAT filesystem.
 *
 * <p>This is a FlintOS implementation class, not a MIDP public API.</p>
 */
public final class ResourceLoader {
    private static final String RESOURCE_ROOT = "/res";
    private static String suiteDirectory;

    private ResourceLoader() {
    }

    /**
     * Selects the resource directory for the current MIDlet suite.
     *
     * @param directory directory name below {@code /res}, or {@code null} to
     *        use {@code /res} directly
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
        String currentSuiteDirectory = suiteDirectory;
        String path;
        if(currentSuiteDirectory == null || currentSuiteDirectory.length() == 0) {
            path = RESOURCE_ROOT + "/" + relativeName;
        }
        else {
            path = RESOURCE_ROOT + "/" + currentSuiteDirectory + "/" + relativeName;
        }
        return new FileInputStream(path);
    }
}
