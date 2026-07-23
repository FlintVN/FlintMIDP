package javax.microedition.lcdui;

/** FlintOS runtime bridge. This class is packaged in flintos.midp.jar. */
public final class DisplayAccess {
    private DisplayAccess() {
    }

    public static void initScreen() {
        Display.initScreen();
    }

    public static Graphics gameGraphics() {
        return Display.gameGraphics();
    }

    public static void flush() {
        Display.flush();
    }
}
