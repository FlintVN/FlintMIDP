package board;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

/** Compatibility facade for applications using the legacy board audio API. */
public final class Audio {
    private Audio() {
    }

    /** FlintOS initializes the audio device before starting applications. */
    public static void init() {
    }

    /** Plays a MIDI note through the MMAPI tone player. */
    public static void playTone(int note, int duration, int volume) {
        try {
            Manager.playTone(note, duration, volume);
        }
        catch(MediaException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
