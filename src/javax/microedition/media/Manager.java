package javax.microedition.media;

import java.io.InputStream;
import javax.microedition.media.control.VolumeControl;

/* No-op audio: createPlayer returns a silent Player so the game's sound code runs without
 * crashing. Real I2S playback is a later increment. */
public class Manager {
    public static final String TONE_DEVICE_LOCATOR = "device://tone";

    public static Player createPlayer(InputStream stream, String type) throws MediaException {
        System.out.println("[media] createPlayer " + type);
        return new NullPlayer();
    }
    public static Player createPlayer(String locator) throws MediaException {
        System.out.println("[media] createPlayer " + locator);
        return new NullPlayer();
    }
    public static void playTone(int note, int duration, int volume) throws MediaException {}
    public static String[] getSupportedContentTypes(String protocol) { return new String[0]; }
    public static String[] getSupportedProtocols(String contentType) { return new String[0]; }
}

/* Silent Player + VolumeControl. Fires STARTED then END_OF_MEDIA on start() so games that
 * block waiting for a one-shot sound to finish (e.g. intro/splash) proceed immediately. */
class NullPlayer implements Player {
    private int state = UNREALIZED;
    private int loopCount = 1;
    private final VolumeControl vol = new NullVolume();
    private final java.util.Vector<PlayerListener> listeners = new java.util.Vector<>();

    public void realize()  { state = REALIZED; }
    public void prefetch() { state = PREFETCHED; }
    public void start() {
        /* Stay in STARTED so games that poll getState()==STARTED proceed. (Earlier we
         * immediately went PREFETCHED + fired END_OF_MEDIA, which can wedge such polls.) */
        state = STARTED;
        fire(PlayerListener.STARTED, Long.valueOf(0));
    }
    public void stop()     { state = PREFETCHED; }
    public void deallocate() { state = REALIZED; }
    public void close()    { state = CLOSED; }
    public void setLoopCount(int count) { loopCount = count; }
    public long setMediaTime(long now) { return now; }
    public long getMediaTime() { return 0; }
    public int  getState() { return state; }
    public long getDuration() { return TIME_UNKNOWN; }
    public String getContentType() { return null; }
    public void addPlayerListener(PlayerListener l) { if (l != null) listeners.addElement(l); }
    public void removePlayerListener(PlayerListener l) { listeners.removeElement(l); }
    public Control getControl(String controlType) { return vol; }
    public Control[] getControls() { return new Control[] { vol }; }

    private void fire(String event, Object data) {
        for (int i = 0; i < listeners.size(); i++) {
            try { listeners.elementAt(i).playerUpdate(this, event, data); } catch (Throwable t) {}
        }
    }
}

class NullVolume implements VolumeControl {
    private int level = 100; private boolean muted;
    public int setLevel(int l) { level = l; return level; }
    public int getLevel() { return level; }
    public void setMute(boolean m) { muted = m; }
    public boolean isMuted() { return muted; }
}
