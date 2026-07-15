package javax.microedition.media.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

/** Optional JSR-135 control for a player with a video renderer. */
public interface VideoControl extends Control {
    int USE_GUI_PRIMITIVE = 0;
    int USE_DIRECT_VIDEO = 1;

    Object initDisplayMode(int mode, Object arg);

    void setDisplayLocation(int x, int y);

    int getDisplayX();

    int getDisplayY();

    void setDisplaySize(int width, int height) throws MediaException;

    int getDisplayWidth();

    int getDisplayHeight();

    void setDisplayFullScreen(boolean fullScreenMode) throws MediaException;

    void setVisible(boolean visible);

    int getSourceWidth();

    int getSourceHeight();

    byte[] getSnapshot(String imageType) throws MediaException;
}
