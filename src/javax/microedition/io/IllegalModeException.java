package javax.microedition.io;

import java.io.IOException;

public class IllegalModeException extends IOException {
    public IllegalModeException() { super(); }
    public IllegalModeException(String s) { super(s); }
}