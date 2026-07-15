# FlintMIDP

FlintMIDP is the MIDP 2.0 compatibility profile for FlintJVM. It is kept
separate from FlintJDK so devices that only need the Java base runtime do not
carry MIDP, LCDUI, RMS, or MMAPI classes.

## Source conventions

Java sources follow the conventions used by FlintJDK:

- four spaces per indentation level;
- opening braces on the declaration line;
- no space between a control-flow keyword and `(`;
- braces may be omitted for a single simple statement;
- one public top-level type per source file;
- imports are explicit and grouped after the package declaration;
- platform extensions use `flint.*` or `board.*`, not undocumented additions
  to portable MIDP types.

Public `javax.microedition.*` signatures, constants, exceptions, and lifecycle
rules follow the Oracle MIDP 2.0 / JSR-118 API. Optional MMAPI extensions follow
JSR-135 and are only advertised when a working backend exists.

## Persistent RMS

Record stores are persisted under:

```text
/data/rms/<suite-id>/<store-name>.rms
```

The suite id comes from `ResourceLoader.setSuiteDirectory`. Updates are written
to a temporary file and renamed into place. Record identifiers remain
one-based and deleted identifiers are not reused.

## Media support

Implemented:

- MMAPI player states and lifecycle events;
- per-player volume and mute;
- loop count, media time, duration, seek, stop, and close;
- PCM WAV, 8-bit or 16-bit, mono or stereo;
- conversion to the FlintOS mono PCM output rate;
- MIDI-note tone playback;
- `file://` and `resource://` WAV locators;
- JSR-135 `VideoControl` API declaration without advertising a video codec.

Planned playback order:

1. Stream WAV data without retaining the complete file in heap.
2. Add MIDP tone-sequence parsing through `ToneControl`.
3. Add a lightweight MIDI synthesizer if required by target games.
4. Add MJPEG demux/decode and a real `VideoControl` implementation.
5. Add audio/video clock synchronization and frame dropping.

Video content is not returned by `Manager.getSupportedContentTypes` until a
decoder and renderer pass device tests.

## Build

FlintMIDP requires `java.base.jar`, `flint.drawing.jar`, and
`flintos.device.jar` from the matching FlintOS/FlintJDK version.

The authoritative API reference is:

https://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/overview-summary.html
