package javax.microedition.media;

import javax.microedition.media.control.VolumeControl;

final class PlayerVolumeControl implements VolumeControl {
    private final AbstractPlayer player;
    private int level = 100;
    private boolean muted;

    PlayerVolumeControl(AbstractPlayer player) {
        this.player = player;
    }

    public synchronized int setLevel(int value) {
        level = Math.max(0, Math.min(100, value));
        player.fireEvent(PlayerListener.VOLUME_CHANGED, this);
        return level;
    }

    public synchronized int getLevel() {
        return level;
    }

    public synchronized void setMute(boolean value) {
        if(muted != value) {
            muted = value;
            player.fireEvent(PlayerListener.VOLUME_CHANGED, this);
        }
    }

    public synchronized boolean isMuted() {
        return muted;
    }

    synchronized int scale(int sample) {
        if(muted)
            return 0;
        return sample * level / 100;
    }
}
