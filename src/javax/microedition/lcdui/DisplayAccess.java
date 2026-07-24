package javax.microedition.lcdui;

/** FlintOS runtime bridge. This class is packaged in flintos.midp.jar. */
public final class DisplayAccess {
    private DisplayAccess() {
    }

    public static void initScreen() {
        Display.initScreen();
    }

    /** Set logical game geometry before creating any Display or Canvas. */
    public static void initScreen(int width, int height, String presentMode) {
        Display.initScreen(width, height, presentMode);
    }

    public static Graphics gameGraphics() {
        return Display.gameGraphics();
    }

    public static void flush() {
        Display.flush();
    }
}
