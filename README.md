# FlintOS MIDP compatibility layer

This directory implements a subset of MIDP 2.0 (JSR 118) for FlintOS.

## API conventions

- Public `javax.microedition.*` APIs follow the Oracle MIDP 2.0 API reference.
- Signatures, visibility, constants, checked exceptions, and documented state
  transitions take precedence over compatibility shortcuts.
- FlintOS extensions must be documented explicitly and must not be required by
  portable MIDlets.
- Platform-specific Java declarations live under `board.*`; their native
  implementations live in the ESP-IDF board component.
- Unsupported optional facilities fail with their specified exception instead
  of silently reporting success.

## Implementation status

This is a compatibility layer, not yet a complete MIDP 2.0 implementation.
LCDUI, RMS, networking, media, security, and the application manager still
contain partial implementations. API completeness and behavioral compliance
must be tracked separately.

The authoritative API reference is:

https://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/overview-summary.html
