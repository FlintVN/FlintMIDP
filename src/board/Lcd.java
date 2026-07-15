package board;

/* Native bridge to the board LCD (backed by boards/.../flint_native_lcd.cpp,
 * registered as NATIVE_CLASS("board/Lcd", ...)). Lives in the MIDP library jar so the
 * shim is self-contained; the game app only ever touches javax.microedition.*. */
public final class Lcd {
    public static native void init();
    public static native int  width();
    public static native int  height();
    /** Push a full big-endian RGB565 framebuffer (width*height*2 bytes) to the panel. */
    public static native void present(byte[] fb);

    /** Next typed console byte (from the serial monitor) as a game key, or -1 if none.
     *  Non-blocking; bytes are queued by the firmware's serial RX path. */
    public static native int readKey();

    private Lcd() {}
}
