package javax.microedition.io;

import java.io.IOException;

public interface HttpConnection extends ContentConnection {
    String HEAD = "HEAD";
    String GET = "GET";
    String POST = "POST";

    int HTTP_OK = 200;

    long getDate() throws IOException;

    long getExpiration() throws IOException;

    String getFile();

    String getHeaderField(String name) throws IOException;

    String getHost();

    long getLastModified() throws IOException;

    int getPort();

    String getProtocol();

    String getQuery();

    String getRef();

    String getRequestMethod();

    String getRequestProperty(String key);

    int getResponseCode() throws IOException;

    String getResponseMessage() throws IOException;

    String getURL();

    void setRequestMethod(String method) throws IOException;

    void setRequestProperty(String key, String value) throws IOException;
}
