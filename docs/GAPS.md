# gaps — delegate from here

not a wishlist. these are the walls we hit. evidence in the linked notes.

## 1. AppGuard: names ≠ protocol

**want:** what fly **actually writes** on usb (timestamped, direction-tagged bytes), then whether the **flip** accepts programmatic sticks.

**have:** names only inside packed `libdatajar.so`. [`docs/s4-fly-datajar-ids.md`](s4-fly-datajar-ids.md)

**wall:** user fold, not debuggable, no su/frida, no exported stick api. gadget wrap of a copy **not built**. [`docs/s4-steer-fly.md`](s4-steer-fly.md) [`docs/s4-fly-runtime-dex.md`](s4-fly-runtime-dex.md)

**do not** keep unpacking the so as the job. **do not** cargo-cult mini-1 `0x03/0x8E`.

**research to delegate:** fridabox (unmodified apk, don’t replace store fly) then gadget-on-a-copy. instrument `openAccessory` / outbound writes / `JNIUsbAccessory` / joystick class load. usb accessory in a vm may fail even if classes load — report those separately.

full write-up: [`docs/REPORT-protocol-boundary.md`](REPORT-protocol-boundary.md)

## 2. `0x574a` is inbound; enable still unknown

**want:** video + telemetry on a **server** for mapping. fold does not need a player.

**have:** after fly-warm, `0x574a` units exist. fly logs `dataType==22346`. fly does **not** “write 0x574a” to enable. [`docs/s4-fly-video-enable.md`](s4-fly-video-enable.md)

**wall:** aoaread currently **drops** non-stick units. no retained 30–60s payload dump. no server decode yet.

**research to delegate:** record complete `0x574a` with timestamps + original boundaries; decode off-phone. hunt telemetry in `0x5749` separately.

## 3. reports ≠ inject (and inject may not even be fly)

inbound `0x06/0x05` is the rc **telling** us sticks. keepalive is a ping. [`docs/s4-stick-wiggle.md`](s4-stick-wiggle.md)

`DataFlycJoystick` in the apk does **not** prove this flip takes virtual sticks from the phone. the remote’s firmware may own physical sticks. two proofs, separately: fly outbound on **this** aircraft, then our reproduction. **no guessed stick TX.**

## 4. sim is the wrong apk

`com.fly.simulator` is Unity/il2cpp. Fly is AppGuard + datajar. do not mix them. ChannelParam ids from the sim are not Flip inject facts. [`docs/s4-channelparam-ids.md`](s4-channelparam-ids.md)

## out of scope until asked

rooting the fold, indoor flight, 8E-on-the-wire, a video player in AoaRead.

## 5. two products

1. **o4 sensor ingestion** (video + telemetry, read-only) — can move now.
2. **programmatic flight control** — feasibility still uncertain. do not stall (1) on (2).

TX must be an **allowlist** (keepalive only) before anyone adds commands.
