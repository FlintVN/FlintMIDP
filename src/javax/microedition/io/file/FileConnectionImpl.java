package javax.microedition.io.file;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.IllegalModeException;

public final class FileConnectionImpl implements FileConnection {
    private static final String FILE_SCHEME = "file://";
    private static final String SDCARD_ROOT = "/sdcard";

    private File file;
    private String path;
    private final int mode;
    private boolean open = true;

    public FileConnectionImpl(String url, int mode) throws IOException {
        if(mode != Connector.READ && mode != Connector.WRITE && mode != Connector.READ_WRITE)
            throw new IllegalArgumentException("Invalid mode: " + mode);
        this.mode = mode;
        this.path = normalizeUrlPath(url);
        this.file = new File(path);
    }

    public long availableSize() {
        return -1;
    }

    public boolean canRead() {
        return open && file.exists() && file.canRead();
    }

    public boolean canWrite() {
        return open && (!file.exists() || file.canWrite());
    }

    public void close() throws IOException {
        open = false;
    }

    public void create() throws IOException {
        ensureOpen();
        ensureWritable();
        File parent = file.getParentFile();
        if(parent != null && !parent.exists())
            parent.mkdirs();
        if(!file.exists() && !file.createNewFile())
            throw new IOException("Create failed: " + path);
    }

    public void delete() throws IOException {
        ensureOpen();
        ensureWritable();
        if(file.exists() && !file.delete())
            throw new IOException("Delete failed: " + path);
    }

    public long directorySize(boolean includeSubDirs) throws IOException {
        ensureOpen();
        if(!file.exists())
            return 0;
        if(!file.isDirectory())
            return file.length();
        return directorySize(file, includeSubDirs);
    }

    public boolean exists() {
        return open && file.exists();
    }

    public long fileSize() throws IOException {
        ensureOpen();
        return file.exists() && file.isFile() ? file.length() : 0;
    }

    public String getName() {
        String name = file.getName();
        if(name.length() == 0)
            return "";
        return file.exists() && file.isDirectory() && !name.endsWith("/") ? name + "/" : name;
    }

    public String getPath() {
        String parent = file.getParent();
        if(parent == null)
            return "/";
        return parent.endsWith("/") ? parent : parent + "/";
    }

    public String getURL() {
        return toUrl(path);
    }

    public String getMountedRoot() {
        return path.startsWith(SDCARD_ROOT) ? "sdcard/" : "phone/";
    }

    public boolean isDirectory() {
        return open && file.exists() && file.isDirectory();
    }

    public boolean isHidden() {
        return open && file.exists() && file.isHidden();
    }

    public boolean isOpen() {
        return open;
    }

    public long lastModified() {
        return open && file.exists() ? file.lastModified() : 0;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public Enumeration list() throws IOException {
        return list("*", false);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public Enumeration list(String filter, boolean includeHidden) throws IOException {
        ensureOpen();
        if(!file.exists() || !file.isDirectory())
            throw new IOException("Not a directory: " + path);

        String[] names = file.list();
        Vector result = new Vector();
        if(names != null) {
            for(int i = 0; i < names.length; i++) {
                File child = new File(file, names[i]);
                if(!includeHidden && child.isHidden())
                    continue;
                if(matches(filter, names[i]))
                    result.addElement(child.isDirectory() ? names[i] + "/" : names[i]);
            }
        }
        return result.elements();
    }

    public void mkdir() throws IOException {
        ensureOpen();
        ensureWritable();
        if(!file.exists() && !file.mkdirs())
            throw new IOException("Mkdir failed: " + path);
    }

    public InputStream openInputStream() throws IOException {
        ensureOpen();
        ensureReadable();
        return new FileInputStream(file);
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    public OutputStream openOutputStream() throws IOException {
        ensureOpen();
        ensureWritable();
        File parent = file.getParentFile();
        if(parent != null && !parent.exists())
            parent.mkdirs();
        return new FileOutputStream(file);
    }

    public OutputStream openOutputStream(long byteOffset) throws IOException {
        ensureOpen();
        ensureWritable();
        if(byteOffset < 0)
            throw new IOException("Negative offset");
        if(byteOffset == 0)
            return openOutputStream();
        if(file.exists() && byteOffset == file.length())
            return new FileOutputStream(file, true);
        throw new IOException("Offset output unsupported: " + byteOffset);
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    public void rename(String newName) throws IOException {
        ensureOpen();
        ensureWritable();
        if(newName == null)
            throw new NullPointerException();
        File parent = file.getParentFile();
        File dest = (parent == null) ? new File(newName) : new File(parent, newName);
        if(!file.renameTo(dest))
            throw new IOException("Rename failed: " + path);
        file = dest;
        path = dest.getPath();
    }

    public void setFileConnection(String fileName) throws IOException {
        ensureOpen();
        if(fileName == null)
            throw new NullPointerException();
        if(fileName.startsWith("/"))
            path = normalizePlainPath(fileName);
        else {
            File parent = file.isDirectory() ? file : file.getParentFile();
            if(parent == null)
                parent = new File("/");
            path = new File(parent, fileName).getPath();
        }
        file = new File(path);
    }

    public void setHidden(boolean hidden) throws IOException {
        ensureOpen();
    }

    public void setReadable(boolean readable) throws IOException {
        ensureOpen();
        if(!readable)
            throw new IOException("Read disable unsupported");
    }

    public void setWritable(boolean writable) throws IOException {
        ensureOpen();
        if(!writable)
            throw new IOException("Write disable unsupported");
    }

    public long totalSize() {
        return -1;
    }

    public void truncate(long byteOffset) throws IOException {
        ensureOpen();
        ensureWritable();
        if(byteOffset < 0 || byteOffset > file.length())
            throw new IOException("Invalid truncate offset: " + byteOffset);
        if(byteOffset == file.length())
            return;
        if(byteOffset > Integer.MAX_VALUE)
            throw new IOException("File too large");

        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)byteOffset);
        byte[] buffer = new byte[256];
        long remaining = byteOffset;
        try {
            while(remaining > 0) {
                int request = remaining > buffer.length ? buffer.length : (int)remaining;
                int read = in.read(buffer, 0, request);
                if(read < 0)
                    break;
                out.write(buffer, 0, read);
                remaining -= read;
            }
        }
        finally {
            in.close();
        }

        FileOutputStream fout = new FileOutputStream(file);
        try {
            fout.write(out.toByteArray());
        }
        finally {
            fout.close();
        }
    }

    public long usedSize() {
        return -1;
    }

    private void ensureOpen() throws IOException {
        if(!open)
            throw new IOException("Connection closed");
    }

    private void ensureReadable() throws IOException {
        if(mode != Connector.READ && mode != Connector.READ_WRITE)
            throw new IllegalModeException("Connection is not readable");
    }

    private void ensureWritable() throws IOException {
        if(mode != Connector.WRITE && mode != Connector.READ_WRITE)
            throw new IllegalModeException("Connection is not writable");
    }

    private static long directorySize(File dir, boolean includeSubDirs) {
        String[] names = dir.list();
        long size = 0;
        if(names == null)
            return 0;
        for(int i = 0; i < names.length; i++) {
            File child = new File(dir, names[i]);
            if(child.isDirectory()) {
                if(includeSubDirs)
                    size += directorySize(child, true);
            }
            else
                size += child.length();
        }
        return size;
    }

    private static boolean matches(String filter, String name) {
        if(filter == null || filter.length() == 0 || "*".equals(filter))
            return true;
        return wildcard(filter, 0, name, 0);
    }

    private static boolean wildcard(String filter, int fi, String name, int ni) {
        while(fi < filter.length()) {
            char c = filter.charAt(fi);
            if(c == '*') {
                while(fi + 1 < filter.length() && filter.charAt(fi + 1) == '*')
                    fi++;
                if(fi + 1 == filter.length())
                    return true;
                for(int i = ni; i <= name.length(); i++) {
                    if(wildcard(filter, fi + 1, name, i))
                        return true;
                }
                return false;
            }
            if(ni >= name.length())
                return false;
            if(c != '?' && Character.toLowerCase(c) != Character.toLowerCase(name.charAt(ni)))
                return false;
            fi++;
            ni++;
        }
        return ni == name.length();
    }

    private static String normalizeUrlPath(String url) throws IOException {
        if(url == null)
            throw new NullPointerException();
        if(!url.regionMatches(true, 0, FILE_SCHEME, 0, FILE_SCHEME.length()))
            throw new IOException("Unsupported URL: " + url);

        String path = url.substring(FILE_SCHEME.length());
        if(path.regionMatches(true, 0, "localhost/", 0, 10))
            path = path.substring(9);
        if(path.length() == 0)
            path = "/";
        if(!path.startsWith("/"))
            path = "/" + path;
        return normalizePlainPath(path);
    }

    private static String normalizePlainPath(String path) {
        String lower = path.toLowerCase();
        if(lower.equals("/sdcard") || lower.startsWith("/sdcard/"))
            return SDCARD_ROOT + path.substring("/sdcard".length());
        if(lower.equals("/sd") || lower.startsWith("/sd/"))
            return SDCARD_ROOT + path.substring("/sd".length());
        if(lower.equals("/memorycard") || lower.startsWith("/memorycard/"))
            return SDCARD_ROOT + path.substring("/memorycard".length());
        if(lower.equals("/phone"))
            return "/";
        if(lower.startsWith("/phone/"))
            return path.substring("/phone".length());
        return path;
    }

    private static String toUrl(String path) {
        return FILE_SCHEME + path;
    }
}