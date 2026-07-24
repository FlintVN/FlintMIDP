package javax.microedition.lcdui;

/** FlintOS input bridge. This class is packaged in flintos.midp.jar. */
public final class CanvasAccess {
    private CanvasAccess() {
    }

    public static void keyPressed(Canvas canvas, int keyCode) {
        if(canvas != null) canvas.dispatchKeyPressed(keyCode);
    }

    public static void keyReleased(Canvas canvas, int keyCode) {
        if(canvas != null) canvas.dispatchKeyReleased(keyCode);
    }
}
