package board;

/* Native bridge to the board's on-screen touch keypad (backed by flint_native_touch.cpp,
 * registered as NATIVE_CLASS("board/Touch", ...)). The keypad is drawn natively in the
 * bottom strip of the panel; poll() reports the key currently under the finger. */
public final class Touch {
    /** Bring up the I2C touch controller and draw the on-screen keypad. */
    public static native void init();

    /** ASCII code of the keypad button currently under the finger ('0'..'9', '*', '#'),
     *  or -1 if nothing is pressed. Non-blocking. */
    public static native int poll();

    /** Raise the calling thread's scheduler priority above the game thread, so the input poll
     *  loop is never starved by the game's render loop. Call once from the input thread. */
    public static native void boost();

    private Touch() {}
}
