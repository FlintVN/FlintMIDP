package javax.microedition.midlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * Parses the main attributes of a JAR manifest file according to the
 * JAR File Specification (Oracle) and MIDP 2.0 property rules.
 *
 * <p>Handles UTF-8, continuation lines, CR/LF/CRLF, trailing blank line,
 * duplicate-key rejection, and the colon-space separator requirement.</p>
 *
 * <p>This class is a FlintOS implementation extension and is not part of the
 * MIDP 2.0 application API.</p>
 */
final class ManifestParser {
    private ManifestParser() {}

    /**
     * Parses the main-section attributes from a raw manifest byte array.
     *
     * @param data raw bytes of the manifest file (UTF-8)
     * @return Hashtable of name → value pairs (main attributes only)
     * @throws NullPointerException if data is null
     * @throws IOException on malformed manifest (continuation without header,
     *         missing colon-space separator, duplicate keys, no blank line
     *         after main section)
     */
    static Hashtable<String, String> parseMainAttributes(byte[] data)
            throws IOException {
        if(data == null)
            throw new NullPointerException("data");

        String text;
        try {
            text = new String(data, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            throw new IOException("UTF-8 is not supported");
        }

        Hashtable<String, String> attrs = new Hashtable<>();
        String currentName = null;
        StringBuilder currentValue = null;
        int pos = 0;

        while(pos <= text.length()) {
            LineResult r = readLine(text, pos);
            String line = r.line;
            pos = r.nextPos;

            // Blank line = end of main section
            if(line.isEmpty()) {
                flush(attrs, currentName, currentValue);
                break;
            }

            // Continuation line: starts with space
            if(line.charAt(0) == ' ') {
                if(currentName == null || currentValue == null)
                    throw new IOException("Continuation without header");
                currentValue.append(line.substring(1));
                if(pos > text.length()) {
                    flush(attrs, currentName, currentValue);
                    break;
                }
                continue;
            }

            // New header
            flush(attrs, currentName, currentValue);
            int colon = line.indexOf(':');
            if(colon <= 0)
                throw new IOException("Invalid manifest header: " + line);
            if(colon + 1 >= line.length() || line.charAt(colon + 1) != ' ')
                throw new IOException("Missing space after ':' in: " + line);

            currentName = line.substring(0, colon);
            currentValue = new StringBuilder(line.substring(colon + 2));

            if(pos > text.length()) {
                flush(attrs, currentName, currentValue);
                break;
            }
        }

        return attrs;
    }

    private static void flush(Hashtable<String, String> attrs, String name, StringBuilder value)
            throws IOException {
        if(name == null)
            return;
        if(attrs.containsKey(name))
            throw new IOException("Duplicate manifest attribute: " + name);
        attrs.put(name, value.toString());
    }

    /** Reads one logical line (up to \n, \r, \r\n, or EOF). */
    private static LineResult readLine(String text, int start) {
        if(start >= text.length())
            return new LineResult("", text.length() + 1);

        int end = start;
        while(end < text.length()) {
            char c = text.charAt(end);
            if(c == '\r' || c == '\n')
                break;
            end++;
        }

        String line = text.substring(start, end);
        int next = end + 1;
        if(end < text.length() && text.charAt(end) == '\r'
                && next < text.length() && text.charAt(next) == '\n')
            next++;
        return new LineResult(line, next);
    }

    private static final class LineResult {
        final String line;
        final int nextPos;
        LineResult(String line, int nextPos) {
            this.line = line;
            this.nextPos = nextPos;
        }
    }
}
