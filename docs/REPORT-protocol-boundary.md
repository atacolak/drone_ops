# external review — blocked at protocol, not transport

ingested 2026-10-23. operator: “we have a report arrived. note it down.”

this is **not** a new charter and **not** approval to fly, inject sticks, or replace the installed DJI Fly app.

## the claim

architecture already substantially validated at the **transport** layer:

- usb accessory ownership
- frame decode
- physical-stick **reports**
- permitted keepalive write
- video-class traffic surviving after fly is stopped

**not** demonstrated: that arbitrary flight-control commands injected through the rc-n3 are **accepted by the aircraft**.

| blocker | actual missing evidence |
|---|---|
| appguard | decrypted fly implementation and numeric command identifiers |
| video startup | the fly-to-controller **initialization transcript** (not the inbound `0x574a` type name) |
| virtual sticks | command structure, authority negotiation, and evidence the **flip** accepts programmatic flight control |
| continuous operation | video/telemetry across disconnect, reconnect, long sessions |

**stop** treating unpacking `libdatajar.so` as the objective. it is one source. prefer **observing what fly actually sends**.

## 1. observe fly at runtime (highest information)

refine `j7k.18`, do **not** duplicate it.

- try **fridabox** first (unmodified apk in an instrumented vm; no root; do not replace the store fly install). usb accessory in a vm may fail even if classes load — keep those two results separate.
- **frida gadget** on a **copy** is fallback. signature change may trip appguard. never uninstall the working fly.
- instrument narrowly: `UsbManager.openAccessory` + outbound writes, `JNIUsbAccessory` send, class load of `DataFlycJoystick` / `CmdIdFlyc` / `SendVirtualStickFlightControlData`, video-start and gimbal dispatch.
- want: timestamped, direction-tagged **packet bytes**. names are not the protocol.

## 2. video pipeline in parallel (does not need appguard)

`CaptureActivity.onUnit()` currently **drops** everything except `0x5749` stick reports. `0x574a` is already on the wire after fly-warm.

next experiment (grounded, no motors):

1. proven fly-warm (live fpv → kill fly → aoaread)
2. record 30–60s of complete `0x574a` payloads with timestamps and original frame boundaries
3. **server** inspect h.264/h.265, try decode. fold does not display video.
4. separately hunt aircraft telemetry inside `0x5749`

two products: **o4 sensor ingestion** (close) vs **programmatic flight control** (feasibility still uncertain). do not stall the first on the second.

## 3. virtual sticks may not even be on this path

`DataFlycJoystick` in the apk does **not** prove fly sends sticks for **this** flip. the rc-n3 may send physical sticks in firmware; fly may only do video/settings.

two questions, separately:

1. can **fly** send arbitrary virtual-stick to **this** flip? (call path / authority / observed outbound)
2. can **our** app reproduce them? (packet + session + aircraft acceptance)

a numeric `cmdId` answers neither. observe benign camera/gimbal/video-init **before** any flight-control experiment. do not transmit guessed stick frames. a successful usb write is not an aircraft ack.

## 4. fail-closed TX before expanding writes

`Duml.build()` is a **denylist** of two ids. it can still construct arbitrary commands.

replace with an **allowlist** below the ui:

- allowed: session keepalive
- everything else: reject
- unknown: reject
- future cmds: separate implementation + explicit operator authorization

also: move the long-running reader out of `CaptureActivity` into a managed service; bounded buffers; disconnect/reconnect tests. the activity is a research ui, not a 24/7 host.

## 5. evidence integrity

a finish comment is not evidence until the artifact is on the branch others can read. do not advance on comments alone.

## decision tree (from the report)

```
instrument official fly?
  yes → capture real outbound + inspect authority → flip-specific feasibility
  no  → stop blind appguard unpacking; ask for an alternate instrumented host
either way → continue read-only video + telemetry extraction
```

## our disposition (flip, 2026-10-23)

- `j7k.17` already closed: `0x574a` is inbound type; enable duml still unknown.
- `j7k.18` still in isolated review at ingest time — **refine after verdict**, do not mint a twin.
- do **not** cargo-cult mini-1 `0x03/0x8E`.
- do **not** implement product in the lead session; next executable frontier gets its own beads after `.18` settles.
- conservative: no indoor takeoff; no guessed stick TX.
