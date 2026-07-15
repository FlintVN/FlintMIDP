package javax.microedition.media;

import java.util.Vector;
import javax.microedition.media.control.VolumeControl;

/** Shared MMAPI state machine used by FlintMIDP players. */
abstract class AbstractPlayer implements Player, Runnable {
    private final Vector<PlayerListener> listeners = new Vector<>();
    private final PlayerVolumeControl volumeControl = new PlayerVolumeControl(this);

    private volatile int state = UNREALIZED;
    private volatile boolean stopRequested;
    private volatile long mediaTime;
    private int loopCount = 1;
    private Thread playbackThread;
    private TimeBase timeBase = Manager.getSystemTimeBase();

    public synchronized void realize() throws MediaException {
        requireOpen();
        if(state == UNREALIZED) {
            doRealize();
            state = REALIZED;
        }
    }

    public synchronized void prefetch() throws MediaException {
        requireOpen();
        if(state == UNREALIZED)
            realize();
        if(state == REALIZED) {
            doPrefetch();
            state = PREFETCHED;
        }
    }

    public synchronized void start() throws MediaException {
        requireOpen();
        if(state == STARTED)
            return;
        if(state < PREFETCHED)
            prefetch();
        stopRequested = false;
        state = STARTED;
        playbackThread = new Thread(this);
        playbackThread.start();
        fireEvent(PlayerListener.STARTED, Long.valueOf(mediaTime));
    }

    public synchronized void stop() throws MediaException {
        requireOpen();
        if(state != STARTED)
            return;
        requestStop();
        state = PREFETCHED;
        fireEvent(PlayerListener.STOPPED, Long.valueOf(mediaTime));
    }

    public synchronized void deallocate() {
        requireOpen();
        requestStop();
        if(state >= PREFETCHED) {
            doDeallocate();
            state = REALIZED;
        }
    }

    public synchronized void close() {
        if(state == CLOSED)
            return;
        requestStop();
        doClose();
        state = CLOSED;
        fireEvent(PlayerListener.CLOSED, null);
    }

    public synchronized void setLoopCount(int count) {
        requireOpen();
        if(state == STARTED)
            throw new IllegalStateException();
        if(count == 0 || count < -1)
            throw new IllegalArgumentException();
        loopCount = count;
    }

    public synchronized long setMediaTime(long now) throws MediaException {
        requireOpen();
        if(now < 0)
            now = 0;
        mediaTime = seek(now);
        return mediaTime;
    }

    public long getMediaTime() {
        return mediaTime;
    }

    public int getState() {
        return state;
    }

    public String getContentType() {
        requireOpen();
        return contentType();
    }

    public synchronized void setTimeBase(TimeBase master) throws MediaException {
        requireOpen();
        if(state == STARTED)
            throw new MediaException("Cannot change time base while started");
        timeBase = master == null ? Manager.getSystemTimeBase() : master;
    }

    public synchronized TimeBase getTimeBase() {
        requireOpen();
        return timeBase;
    }

    public void addPlayerListener(PlayerListener playerListener) {
        requireOpen();
        if(playerListener != null && !listeners.contains(playerListener))
            listeners.addElement(playerListener);
    }

    public void removePlayerListener(PlayerListener playerListener) {
        requireOpen();
        listeners.removeElement(playerListener);
    }

    public Control getControl(String controlType) {
        requireOpen();
        if(controlType == null)
            throw new IllegalArgumentException();
        if("VolumeControl".equals(controlType)
                || "javax.microedition.media.control.VolumeControl".equals(controlType))
            return volumeControl;
        return null;
    }

    public Control[] getControls() {
        requireOpen();
        return new Control[] {volumeControl};
    }

    public final void run() {
        int remainingLoops = loopCount;
        try {
            do {
                playOnce();
                if(stopRequested)
                    return;
                if(remainingLoops > 0)
                    remainingLoops--;
                if(remainingLoops != 0)
                    setMediaTime(0);
            } while(remainingLoops != 0);

            synchronized(this) {
                if(state == STARTED)
                    state = PREFETCHED;
            }
            fireEvent(PlayerListener.END_OF_MEDIA, Long.valueOf(mediaTime));
        }
        catch(Throwable throwable) {
            if(stopRequested)
                return;
            synchronized(this) {
                if(state != CLOSED)
                    state = PREFETCHED;
            }
            fireEvent(PlayerListener.ERROR, throwable.toString());
        }
    }

    final PlayerVolumeControl getVolumeControl() {
        return volumeControl;
    }

    final boolean isStopRequested() {
        return stopRequested;
    }

    final void setPlaybackTime(long value) {
        mediaTime = value;
    }

    final void fireEvent(String event, Object data) {
        PlayerListener[] snapshot;
        synchronized(listeners) {
            snapshot = listeners.toArray(new PlayerListener[listeners.size()]);
        }
        for(int i = 0; i < snapshot.length; i++) {
            try {
                snapshot[i].playerUpdate(this, event, data);
            }
            catch(Throwable ignored) {
            }
        }
    }

    protected void doRealize() throws MediaException {
    }

    protected void doPrefetch() throws MediaException {
    }

    protected void doDeallocate() {
    }

    protected void doClose() {
    }

    protected abstract void playOnce() throws Exception;

    protected abstract long seek(long mediaTime) throws MediaException;

    protected abstract String contentType();

    private void requestStop() {
        stopRequested = true;
        if(playbackThread != null)
            playbackThread.interrupt();
        playbackThread = null;
    }

    private void requireOpen() {
        if(state == CLOSED)
            throw new IllegalStateException("Player is closed");
    }
}
