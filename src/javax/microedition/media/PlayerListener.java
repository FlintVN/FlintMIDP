package javax.microedition.media;

public interface PlayerListener {
    String STARTED = "started", STOPPED = "stopped", END_OF_MEDIA = "endOfMedia",
           VOLUME_CHANGED = "volumeChanged", DURATION_UPDATED = "durationUpdated",
           DEVICE_UNAVAILABLE = "deviceUnavailable", DEVICE_AVAILABLE = "deviceAvailable";
    void playerUpdate(Player player, String event, Object eventData);
}
