package board;

/* Native bridge to the board LCD (backed by boards/.../flint_native_lcd.cpp,
 * registered as NATIVE_CLASS("board/Lcd", ...)). Lives in the MIDP library jar so the
 * shim is self-contained; the game app only ever touches javax.microedition.*. */
public final class Lcd {
    /** Initialize using physical panel dimensions. */
    public static native void init();

    /**
     * Initialize logical MIDP geometry selected by Java launcher code. Native
     * code only maps this generic framebuffer to its physical panel.
     */
    public static native void init(int width, int height, String mode);

    public static native int  width();
    public static native int  height();
    /** Push current logical RGB565 framebuffer to the panel. */
    public static native void present(byte[] fb);

    /** Next typed console byte (from the serial monitor) as a game key, or -1 if none.
     *  Non-blocking; bytes are queued by the firmware's serial RX path. */
    public static native int readKey();

    private Lcd() {}
}
