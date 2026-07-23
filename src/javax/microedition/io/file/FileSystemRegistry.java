package javax.microedition.io.file;

import java.util.Enumeration;
import java.util.Vector;

public final class FileSystemRegistry {
    private FileSystemRegistry() {
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static Enumeration listRoots() {
        Vector roots = new Vector(1);
        roots.addElement("mnt/sd0/");
        return roots.elements();
    }

    public static boolean addFileSystemListener(FileSystemListener listener) {
        return listener != null;
    }

    public static boolean removeFileSystemListener(FileSystemListener listener) {
        return listener != null;
    }
}