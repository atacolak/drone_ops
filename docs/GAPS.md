# gaps — delegate from here

not a wishlist. these are the walls we hit. evidence in the linked notes.

## 1. AppGuard: no numbers without a dump

**want:** `cmdSet` / `cmdId` for Fly virtual stick, flight mode (C/N/S), gimbal.

**have:** names only (`DataFlycJoystick`, `SendVirtualStickFlightControlData`, `CmdIdFlyc`, gimbal enums) inside `dji.go.v5` → `libdatajar.so` (~219 MiB).

**wall:**

- on disk the so is an **AppGuard ELF**, not a zip-of-dex. no `dex\n` magic. [`docs/s4-fly-datajar-ids.md`](s4-fly-datajar-ids.md)
- outer `classes.dex` is an AppGuard stub. jadx is amap + guard. [`docs/s4-fly-usb-tx.md`](s4-fly-usb-tx.md)
- runtime dump **blocked** on this fold: `user` / non-debuggable, SELinux hides `/proc/<pid>/maps`, `am dumpheap` SecurityException, **no su**, **no frida**. [`.dex mmap` ~1.7 MiB = stub only](s4-fly-runtime-dex.md)

**research to delegate:** zygisk/dumpdex / frida-server / debuggable wrap of `dji.go.v5` that still decrypts datajar. then jadx `CmdIdFlyc$CmdIdType` constructors. **do not** cargo-cult Mini-1 `0x03/0x8E` as Flip.

## 2. `0x574a` exists; we did not turn it on ourselves

**want:** video-class mux without launching DJI Fly, then ship payloads to a **server** for spatial mapping.

**have:** Fly live-FPV → force-stop Fly → our apk still sees `0x574a` (~4k, some annex-b-shaped). [`docs/PIPE.md`](PIPE.md), [`docs/s3-stream.md`](s3-stream.md)

**wall:** unknown **which duml (if any)** Fly writes to enable that mux. we only have the boot ritual.

**research to delegate:** reverse Fly’s video-start / accessory-enable path; reproduce in AoaRead. demux is a **later server** problem, not a fold player.

## 3. reports ≠ inject

inbound `0x06/0x05` is the rc **telling** us stick positions. keepalive `0x06/0x77` is a session ping. neither is virtual stick. [`docs/s4-stick-wiggle.md`](s4-stick-wiggle.md)

until (1) lands, **no TX** of stick/takeoff/sport.

## 4. sim is the wrong apk

`com.fly.simulator` is Unity/il2cpp. Fly is AppGuard + datajar. do not mix them. ChannelParam ids from the sim are not Flip inject facts. [`docs/s4-channelparam-ids.md`](s4-channelparam-ids.md)

## out of scope until asked

rooting the fold, indoor flight, 8E-on-the-wire, a video player in AoaRead.
