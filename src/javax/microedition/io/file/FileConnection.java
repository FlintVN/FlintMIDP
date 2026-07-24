package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection {
    long availableSize();
    boolean canRead();
    boolean canWrite();
    void create() throws IOException;
    void delete() throws IOException;
    long directorySize(boolean includeSubDirs) throws IOException;
    boolean exists();
    long fileSize() throws IOException;
    String getName();
    String getPath();
    String getURL();
    String getMountedRoot();
    boolean isDirectory();
    boolean isHidden();
    boolean isOpen();
    long lastModified();
    @SuppressWarnings({"rawtypes"})
    Enumeration list() throws IOException;
    @SuppressWarnings({"rawtypes"})
    Enumeration list(String filter, boolean includeHidden) throws IOException;
    void mkdir() throws IOException;
    InputStream openInputStream() throws IOException;
    DataInputStream openDataInputStream() throws IOException;
    OutputStream openOutputStream() throws IOException;
    OutputStream openOutputStream(long byteOffset) throws IOException;
    DataOutputStream openDataOutputStream() throws IOException;
    void rename(String newName) throws IOException;
    void setFileConnection(String fileName) throws IOException;
    void setHidden(boolean hidden) throws IOException;
    void setReadable(boolean readable) throws IOException;
    void setWritable(boolean writable) throws IOException;
    long totalSize();
    void truncate(long byteOffset) throws IOException;
    long usedSize();
}