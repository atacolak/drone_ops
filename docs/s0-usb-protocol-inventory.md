# S0 USB protocol inventory + read-only S1 diagnostic spec

Status: **S0 offline reconnaissance only**. Not hardware consent. Not an S1 license.
Authority: `docs/EXPLORATION.md`.
Bead: `dji-flip-g0-g1-research-j7k.1`.
Sibling (out of scope): capability matrix `dji-flip-g0-g1-research-j7k.2`.

Inspected clones under `/tmp/dji-flip-s0-usb/` on 2026-10-22. SHAs below are those HEADs. No accessory I/O, no command TX, no Flip/RC attachment was performed for this document.

## claim classification

Every factual claim is tagged:

| tag | meaning |
|---|---|
| `observed-on-Flip` | measured on DJI Flip + this RC-N3 + Fold. **none in this document.** |
| `observed-other-model` | author or third party reports hardware on a named non-Flip pair |
| `source-implementation-only` | current source/docs implement or state this; not live-checked here |
| `untested-hypothesis` | plausible, not evidenced by source or hardware in this campaign |

Confidence (`high` / `med` / `low`) is about how tightly current source supports the row, not about Flip.

## non-goals

- capability matrix (flight / telemetry / video claims across projects)
- live hardware, S1 execution, ADB against a live RC-N3
- accessory writes, candidate command TX (including `0x03/0x8E`)
- RF / region changes, motor / arming / flight commands
- treating neo-control aircraft Wi-Fi as USB evidence
- claiming concurrent DJI Fly + custom-app accessory ownership

---

## 1. two physical USB-C roles on RC-N1/N2/N3

RC-N series controllers have **two USB-C ports that are not equivalent**. `[source-implementation-only]`

```
                    phone cradle (TOP)
                    AOA / MFi accessory
   [phone/Fold] <====================> [RC]  ))) O4/OcuSync ))) [aircraft]
                    RC is USB host
                    phone is accessory

                    charging / sim (BOTTOM, between sticks)
                    USB device / CDC ACM "VCOM For Protocol"
   [PC/host]     <====================> [RC]
                    PC is USB host
                    RC is CDC serial gadget @ 115200
```

| port | where | USB role | what talks | used by |
|---|---|---|---|---|
| **TOP / phone-facing** | cradle where the phone sits | RC = USB **host**; phone = **AOA accessory** (Android) or **MFi EASession** (iOS) | `UsbManager.openAccessory` / `EASession` on `com.dji.logiclink` | DJI Fly; FreeFCC-USB; DJI-FCC-Unlock-iOS; DJI-Link Pi gadget (phone impersonation) |
| **BOTTOM / charging+simulator** | between stick mounts | RC = USB **device**; PC = host | CDC ACM serial, Windows name `DJI USB VCOM For Protocol`, macOS `cu.usbmodem*`, Linux `ttyACM*` @ 115200 | eraycn/dji-rc-n3; GXO JoyStick; charging |

Sources for the split:

- FreeFCC-USB README table "The RC-N1 has two USB-C ports that work differently" plus `DumplTransport.kt` comments that AOA is the TOP port. `[source-implementation-only]` README also asserts Mini 3 firmware only accepts DUMPL over TOP AOA. `[untested-hypothesis]` for Flip; `[source-implementation-only]` as that project's claim. Repo itself says **untested on real hardware**. `[source-implementation-only]`
- GXO README: "Plug the DJI RC into the computer using its **bottom** USB-C port (between the stick mounts — not the top port)". `[source-implementation-only]` Button masks "verified on RC-N3". `[observed-other-model]` (RC-N3 as simulator, no aircraft required)
- eraycn README: "Connect via bottom type-C". `[source-implementation-only]`
- DJI-FCC-Unlock-iOS README: "Cable the iPhone to the **TOP** USB port of the controller, the phone-cradle port". Hardware-confirmed RC-N3 + Neo. `[observed-other-model]`
- DJI-Link Pi: USB gadget impersonates the **phone**, so it plugs into the remote's phone-facing USB, not a PC VCOM port. `[source-implementation-only]` Target aircraft is Mavic Mini 1 (WM160). `[observed-other-model]` for that stack; **not Flip**.

**Do not treat bottom-port stick reads as proof that a phone app can inject virtual sticks on the top port.** `[untested-hypothesis]` if anyone claims they share a command path. Exploration already forbids that collapse.

VID/PID on the **phone** after AOA START are Google accessory IDs `18d1:2d00/2d01/2d04/2d05`. `[source-implementation-only]` (AOA spec + DJI-Link `99-dji-aoa.rules` + `aoa.py`). Matching the RC is **not** done by those PIDs on Android accessory APIs; Android matches `UsbAccessory` manufacturer/model strings. `[source-implementation-only]` (Android accessory docs + DJI-Link `DOMAIN_transport_usb_aoa.md`)

---

## 2. cold independent client vs DJI-Fly-initialized session

These are different questions. A successful `openAccessory` / `EASession` is not aircraft-link init. `[source-implementation-only]` as a logical split; Flip behavior `[untested-hypothesis]`.

| mode | what it means | who claims it |
|---|---|---|
| **Cold independent accessory client** | custom app opens the phone-facing accessory with Fly not running (force-stopped), implements its own handshake, and talks to the RC without Fly having warmed the RC↔aircraft radio | FreeFCC-USB *assumes* this (bootstrap + keepalive in source). **Untested on real hardware** per its own README. DJI-Link Pi is a cold phone-stand-in for **WM160 after one official activation**, no Fly at runtime. `[source-implementation-only]` / `[observed-other-model]` (WM160) |
| **DJI-Fly-initialized (warm) session** | Fly must first bring up RC↔aircraft (live camera). Then Fly is closed and a custom client inherits a still-warm radio relay window | DJI-FCC-Unlock-iOS, hardware-confirmed RC-N3 + Neo. Issue #4 is explicitly "drop the open DJI Fly first step" — **missing**. `[observed-other-model]` |
| **Concurrent Fly + custom app** | both hold the accessory fd at once | **forbidden assumption**. Android accessory docs: only one connected accessory at a time. FreeFCC-USB and iOS both require closing Fly before the custom session. Whether two apps can `openAccessory` the same accessory is not documented as supported. `[source-implementation-only]` for exclusive-use instructions in those apps; concurrent ownership on Fold `[untested-hypothesis]` and must not be planned. |

iOS warmth details that matter for S2 later, **not S1** (S1 is Flip OFF):

- RC-N3 + Neo: frames reach the controller and stop there until it is relaying to a drone. `[observed-other-model]`
- Reconnecting the controller without Fly does not re-warm. `[observed-other-model]`
- Mini 5 Pro on RC-N3: link healthy, RCLink framing correct, aircraft linked, **zero acks** on all six paths. `[observed-other-model]` Other-model success/failure is **not Flip proof**.

S1 (Flip OFF) can only observe **transport identity**, not session warmth. `[untested-hypothesis]` until executed.

---

## 3. per-source table

| source | URL | HEAD inspected | key files | interface / init | framing | port | session model | conf. | class |
|---|---|---|---|---|---|---|---|---|---|
| DJI-Link | https://github.com/Kolya080808/DJI-Link | `cd39ec52f6861e0d91ef48aeb307c6e07fbfb6ee` (2026-09-02) | `dji_link_beta/reverse_docs/DOMAIN_transport_usb_aoa.md`; `dji_link_beta/aoa.py`; `dji_link_beta/pi/aoa_device.py`; `dji_link_beta/pi/bridge.py`; `dji_link_beta/composite.py`; `dji_link_beta/99-dji-aoa.rules`; `dji_link_beta/frida/dumpduml.js`; `src/pi/` C++ bridge | **Phone role:** Pi USB gadget; RC host sends AOA 51/52/53; gadget re-enumerates `18d1:2d00` bulk IN/OUT. Identity RC sends: manufacturer `DJI`, model `com.dji.logiclink` (also `WM160`, `com.dji.link` in Fly `accessory_filter.xml`). **Inverted role** in `aoa.py`: Linux host switches a phone into AOA. | Composite DUSS mux: `55 CC \| type u16 LE \| length u32 LE \| payload`. DUML channel type `0x5749` (`IW`). Inner DUML is `55` + 10-bit len + ver + CRC8 + addrs + seq + cmd + CRC16. | phone-facing (Pi as phone) | cold independent **after** one official Fly activation of WM160; Fly not required at runtime | high for WM160 source; **low for Flip** | source-implementation-only; observed-other-model (WM160 / Mini 1 RC) |
| eraycn/dji-rc-n3 | https://github.com/eraycn/dji-rc-n3 | `44f51b09bc9d2a3d4fdc68189c4f40412822a469` (2025-05-12) | `README.md`; `main.py` | `pyserial` open of COM port whose description contains `For Protocol`, 115200. Requires DJI Assistant 2 install then close (Windows driver). | Bare DUML on CDC (no `55 CC` mux). Writes cmd_set `0x06` id `0x24` payload `01` (simulator enable), then polls id `0x01` (38-byte stick) and `0x27` (58-byte buttons). Stick u16 LE, min 364 / center 1024 / max 1684. | **bottom** charging/sim | N/A (no aircraft, no Fly, no AOA) | high that it is CDC-VCOM read+sim-enable write; **not** aircraft inject | source-implementation-only; README "tested with DJI Virtual Flight" is observed-other-model (sim, not Flip) |
| GXO JoyStick | https://github.com/gladiusxosmium/DJI-RC-N3-Joystick_MACOS | `a2ecb987f9102662678fffa930a646b43d963f7b` (2026-07-05) | `README.md`; `dji_rcn3_mac.py`; `dji_rcn3_gui.py` | same CDC 115200; auto-pick lowest `usbmodem` / `DJI USB VCOM For Protocol`. Explicit **bottom** port. | same DUML as eraycn; FrameReader resyncs on `0x55`, CRC8 hdr seed `0x77`. Simulator enable `0x06/0x24`. Also mentions RC push 21-byte `0x26` frames. | **bottom** | N/A | high for RC-N3 sim-port reads | observed-other-model (RC-N3 buttons verified); source-implementation-only for code |
| FreeFCC-USB | https://github.com/doesthings/FreeFCC-USB | `55da2d3bc6c59d33f8e9423f43dd5b2f91abbe61` (2026-08-12) | `app/src/main/AndroidManifest.xml`; `res/xml/accessory_filter.xml`; `DumplTransport.kt`; `MainActivity.kt` | Android AOA: `uses-feature usb.accessory`; intent `USB_ACCESSORY_ATTACHED`; `UsbManager.accessoryList`; manufacturer must be `"DJI"`; `requestPermission`; `openAccessory` → `ParcelFileDescriptor` → `FileInputStream`/`FileOutputStream`. Filter models: `T600`, `com.dji.logiclink`, `WM160`, `AG410`. | RCLink envelope `55 CC 49 57 + u32 LE length + DUML`. Bootstrap dest `0x1F` then `0x00`, payload `{00 00 01}`, cmd_set/id `0x00/0x00`. Keepalive every 2.5s dest `0x06` and `0x0E`, cmd_set `0x06` id `0x77`. **FCC payloads out of scope.** Also contains a **VCOM/host-mode** path (`VcomTransport`) aimed at a **drone** USB-C, VID `0x2CA3` — not the RC phone port. | **TOP** AOA (claimed); VCOM class is a different device | assumes cold client after force-stopping Fly; exclusive accessory | high that the app *implements* AOA this way; **low** that RC-N3+Flip accepts it (README: untested on real hardware; Flip listed in a universal-profile marketing table) | source-implementation-only; Flip rows in the drone table are untested-hypothesis |
| DJI-FCC-Unlock-iOS | https://github.com/andreapianidev/DJI-FCC-Unlock-iOS | `5ef860adafd11329d5af1141dc71c076ca1484ae` (2026-09-20, v1.8.1) | `FreeFCC/Info.plist`; `ExternalAccessoryTransport.swift`; `DumplTransport.swift`; `RCLink.swift`; `FccController.swift`; `docs/TECHNICAL-DOCUMENTATION.md`; README | iOS MFi: `UISupportedExternalAccessoryProtocols` = `com.dji.logiclink`, `com.dji.protocol`, `com.dji.common`, `com.dji.fly`, `com.dji.video`. `EAAccessoryManager.connectedAccessories` hides accessories whose protocols are undeclared. Open `EASession` on ranked protocol, logiclink first. TOP cradle port. | same RCLink envelope; same bootstrap/keepalive as FreeFCC-USB; inbound parser CRC8/CRC16; stream also carries video on logiclink. **RF/region out of scope.** | **TOP** phone-facing | **Fly-warmed** then Fly closed; cold init is issue #4, not implemented | high for RC-N3+Neo MFi identity and warm-window | observed-other-model (RC-N3 + Neo FW v00.05.00.12); Mini 5 Pro failure also observed-other-model; Flip untested-hypothesis |
| Android UsbManager AOA | https://developer.android.com/develop/connectivity/usb/accessory | docs "Last updated 2024-01-03 UTC" | platform API `android.hardware.usb.UsbManager` / `UsbAccessory` | see §5 | bulk IN/OUT behind one PFD; logical packets up to 16384 bytes | phone as accessory | OS-level; no DJI session | high for API | source-implementation-only |
| Frida Android | https://frida.re/docs/android/ plus DJI-Link `dumpduml.js` | Frida docs as of fetch 2026-10-22; script in DJI-Link HEAD above | `dji_link_beta/frida/dumpduml.js` | observe **inside a process you can instrument** | hooks DJI Fly `uav.midware.data.manager.P3.DataBase` TX/RX in **WM160** Fly | n/a | observation of Fly's DUML, not a second USB client | high that stock Fold root is not to be presumed; script is WM160 Fly | source-implementation-only; attaching to Fly on Fold is untested-hypothesis and may break Fly |

---

## 4. source notes (transport / init only)

### 4.1 DJI-Link — legacy Mini 1 accessory stack

Pi pretends to be the phone in front of the **Mavic Mini 1 remote** (source names MR1SS5/MR1SD25, micro-USB), **not** RC-N3. The repo does not mention Flip or RC-N3. `[source-implementation-only]`

AOA device-side sequence in `aoa_device.py` / `src/pi/aoa_device.cpp`:

1. gadget phase 1 presents as Android (`18d1:4ee1`)
2. RC (USB host) `GET_PROTOCOL` (51) → gadget replies AOA version 2
3. RC `SEND_STRING` (52) with **its** strings (`DJI` / `com.dji.logiclink`)
4. RC `START` (53) → gadget re-enumerates accessory `18d1:2d00` (accessory-only, one iface, bulk IN `0x81` / OUT `0x01`, HS 512) `[source-implementation-only]`
5. DUML+video flow on bulk `[source-implementation-only]`

Fly accessory filter (from DJI-Link's decompile notes, WM160):

```
manufacturer="DJI" model="com.dji.logiclink"
manufacturer="DJI" model="WM160"
manufacturer="DJI" model="com.dji.link"
```

`[source-implementation-only]` cited to `DOMAIN_transport_usb_aoa.md` / `res/xml/accessory_filter.xml` of a decompiled Fly APK. **Those model strings are Mini-1-era. Flip's RC-N3 advertisement is unknown.** `[untested-hypothesis]`

Composite mux (`composite.py` + domain doc):

```
55 CC | type u16 LE | length u32 LE | payload[length]
type 0x5749 = DUML (ASCII "IW")
type 0x574A/574D/… = video family
type 0x7530 = aux DUML
```

FreeFCC-USB / iOS RCLink `55 CC 49 57` **is this same header** with type `0x5749` stored little-endian (`49 57`). `[source-implementation-only]`

`aoa.py` is the **opposite** role (Linux as RC, phone as accessory) and is easy to misread as the Fold path. The Fold already **is** the phone; it does not run `aoa.py`. `[source-implementation-only]`

README claims hardware-verified continuous virtual-stick on WM160 via DUML `0x03/0x8E`. `[observed-other-model]` That ID is a **candidate to investigate later**, not a Flip fact and **not authorized to transmit**. `[untested-hypothesis]` on Flip.

Frida script `dumpduml.js` is log-only hooks on Fly's Java DUML base class, written for a **rooted emulator or rooted phone**, package `dji.go.v5`. `[source-implementation-only]` It does not sniff another app's USB fd from the outside.

### 4.2 eraycn/dji-rc-n3 — bottom-port stick reader

`main.py` opens serial 115200 on the first port whose description contains `For Protocol`. `[source-implementation-only]`

It **writes** DUML:

- enable simulator: source `0x0a` (PC) → target `0x06` (RC), cmd_type `0x40`, cmd_set `0x06`, cmd_id `0x24`, payload `01`
- poll sticks `cmd_id 0x01`, buttons `0x27`

`[source-implementation-only]`

This is **not** aircraft virtual-stick inject. It is RC-local sim-mode + HID emulation to Windows (`vgamepad`). `[source-implementation-only]` Reads ≠ inject on the phone-facing O4 path. `[untested-hypothesis]` if equated.

Parser is fragile (reads until one `55` frame then breaks inner loop). `[source-implementation-only]`

### 4.3 GXO JoyStick — same bottom port, cleaner parser, RC-N3 verified buttons

Credits eraycn + mDjiController + DJI_RC-N1_SIMULATOR. `[source-implementation-only]`

README protocol notes (CDC, 115200, sim enable, 38/58/21-byte frames, axis range, button masks) match the code in `dji_rcn3_mac.py`. `[source-implementation-only]` Button masks "verified on RC-N3". `[observed-other-model]`

Still bottom port, still writes sim-enable, still not AOA. `[source-implementation-only]`

### 4.4 FreeFCC-USB — Android AOA transport (RF out of scope)

`accessory_filter.xml` models: `T600`, `com.dji.logiclink`, `WM160`, `AG410`, all manufacturer `DJI`. Comment claims this matches DJI's SDK filter. `[source-implementation-only]` **Flip/RC-N3 model string may or may not be in this list.** `[untested-hypothesis]`

`AccessoryTransport.open`:

1. `getSystemService(USB_SERVICE)`
2. `usbManager.accessoryList` (first accessory)
3. reject if `manufacturer != "DJI"`
4. `hasPermission` else `requestPermission` + return null (retry later)
5. `openAccessory` → PFD → streams
6. TX thread, 3 ms inter-frame delay
7. bootstrap then keepalives then whatever the profile queues

`[source-implementation-only]`

Default RCLink route is `0x49 0x57` (`IW` = composite type `0x5749` LE). `AccessoryTransport` copies route bytes out of inbound envelopes into `lastRoute` and echoes them on later TX, because "the controller may change route mid-session". `[source-implementation-only]`

Manifest `queries` DJI Fly packages (`dji.go.v5`, `dji.go.v4`, …) so it can see Fly; it does **not** share the fd. README: force-stop Fly; "Android only allows one app at a time to hold the USB accessory"; `Release USB for DJI Fly`. `[source-implementation-only]`

README lists Flip in a "universal profile" drone table with checkmarks. That is marketing, and the same README says **untested on real hardware**. Treat Flip rows as `[untested-hypothesis]`.

Do not use this repo as an RF/region cookbook. Transport-only.

### 4.5 DJI-FCC-Unlock-iOS — MFi init/response (RF out of scope)

Hardware: RC-N3 + DJI Neo, FW v00.05.00.12, `com.dji.logiclink` read off device via `ideviceinstaller list -b com.dji.golite -a UISupportedExternalAccessoryProtocols`. `[observed-other-model]`

Public SDK protocol trio (`protocol`/`common`/`video`) was **not enough** to even *see* the RC-N3. `[observed-other-model]`

Session order (README): drone+RC on → **open Fly until live camera** → **close Fly** → cable TOP port → custom `EASession` → bootstrap. `[observed-other-model]`

Issue #4: capturing Fly's outgoing init to drop that step is **not done**. Dump-all in the app shows frames **this** app receives, not Fly TX. `[source-implementation-only]`

Mini 5 Pro counterexample: same RC family, healthy link, zero acks. `[observed-other-model]` **Do not upgrade Neo success to Flip.**

### 4.6 Android AOA (official)

Required app pieces `[source-implementation-only]`:

- `<uses-feature android:name="android.hardware.usb.accessory" />`
- min SDK 12 for `android.hardware.usb` (or 10 + `com.android.future.usb.accessory`)
- activity intent-filter `android.hardware.usb.action.USB_ACCESSORY_ATTACHED`
- meta-data → `res/xml/accessory_filter.xml` with manufacturer / model / (optional, discouraged) version
- `UsbManager` via `getSystemService(Context.USB_SERVICE)`
- discover: attached intent extra `UsbManager.EXTRA_ACCESSORY` **or** `getAccessoryList()`
- `hasPermission` / `requestPermission(accessory, pendingIntent)`
- `openAccessory` → `ParcelFileDescriptor` → `FileInputStream` / `FileOutputStream` on that fd
- close PFD on `USB_ACCESSORY_DETACHED`

Docs: "Only one connected accessory is supported at a time." `[source-implementation-only]` That is accessory-count, not a fully explicit two-app mutex, but `openAccessory` yields the exclusive bulk streams. Planning concurrent Fly+custom ownership is `[untested-hypothesis]` and rejected by this campaign.

`UsbManager.openAccessory` docs: if the `InputStream` reads only part of a USB transfer, the rest of that transfer is dropped. `[source-implementation-only]` Any later S3 reader must drain full transfers (logical packets up to 16384 bytes). Not an S1 action.

No VID/PID in the accessory API. Identity is manufacturer/model/version strings the **host accessory** (here: RC) advertised during AOA handshake. `[source-implementation-only]`

### 4.7 Frida observation note

Official Frida Android tutorial: simplest path is **root** + `frida-server`; also possible via gadget / repackaging / debugger, which can break Play-integrity apps. `[source-implementation-only]`

An independent Android app **cannot** ordinarily read another app's `openAccessory` fd. `[source-implementation-only]` (OS process isolation; not a Flip measurement)

Stock Galaxy Z Fold 5 root must **not** be presumed. `[untested-hypothesis]` if someone assumes it. Exploration: "Do not presume stock Fold root." Repackaging Fly may break it. `[untested-hypothesis]`

DJI-Link's `dumpduml.js` observes Fly's Java DUML codec on WM160, not the USB bulk layer, and not Flip's Fly build. `[source-implementation-only]`

S1 must not require Frida.

---

## 5. independent Android accessory-open steps (no concurrent Fly)

From Android docs + FreeFCC-USB implementation. `[source-implementation-only]` Fold/RC-N3/Flip behavior `[untested-hypothesis]`.

**Do not run these in S1.** Listed so S3 has a source-backed checklist. S1 is enumeration-only (see §6).

1. **Release Fly.** Settings → Apps → DJI Fly → Force Stop. Clear "Open by default" if Fly auto-launches on accessory attach. `[source-implementation-only]` (FreeFCC-USB README)
2. Manifest: `uses-feature android.hardware.usb.accessory`; intent-filter `USB_ACCESSORY_ATTACHED`; `accessory_filter.xml` manufacturer/model **once S1 records the real strings**. Filtering the wrong model means Android will not deliver the attach intent to the app. `[source-implementation-only]`
3. User plugs **TOP / cradle** USB-C (data cable). Bottom port will not present as `UsbAccessory`. `[source-implementation-only]` from the port table; Flip `[untested-hypothesis]`
4. `UsbManager.getAccessoryList()` / attach intent → log `getManufacturer()`, `getModel()`, `getVersion()`, `getUri()`, `getSerial()` (redact serial in any shared log). `[source-implementation-only]`
5. `hasPermission`; else `requestPermission`. Attach-intent path may already have been granted if the user accepted the system dialog. `[source-implementation-only]`
6. `openAccessory`. Null PFD: Fly still owns it, bad cable, wrong port, or permission. `[source-implementation-only]` / remaining causes on Fold `[untested-hypothesis]`
7. Streams on the PFD. Optional: send **only** the published bootstrap/keepalive **in a later authorized S3**, never in S1. iOS/Neo source treats bootstrap as required before later commands; that is `[observed-other-model]`, not a Flip MUST. `[untested-hypothesis]` on Flip.
8. Close/release before starting Fly. Exclusive. `[source-implementation-only]`

AOA handshake itself (51/52/53) is performed by the **RC as USB host** against the phone's device stack, not by the app. The app only sees the accessory after Android re-enumerates. `[source-implementation-only]` (AOA spec + DJI-Link `aoa.py` comments)

---

## 6. read-only S1 diagnostic spec

**Not authorization to run.** Flip independently schedules hardware.

### 6.1 purpose

Settle unknown **A** (what the phone-facing RC-N3 actually enumerates on this Fold/firmware) as far as **identity logs** can, with Flip **OFF**. Does **not** settle unknown B (who initializes RC↔aircraft). `[untested-hypothesis]` until run.

### 6.2 hardware / mode

| item | required |
|---|---|
| aircraft | **Flip OFF**, props off, not paired for this test |
| controller | RC-N3 powered on |
| phone | Samsung Galaxy Z Fold 5 |
| cable | USB-C **data** cable to RC **TOP / cradle** port first |
| host debug | **`ssh fold` + fold-local adb** (`docs/FOLD-CONTROL.md`) so the Fold USB-C is free for the RC. not sfub `adb connect`. |
| apps | DJI Fly installed; S1 does **not** `openAccessory` from a custom app |
| writes | **none**. no accessory fd, no DUML, no sim-enable `0x06/0x24`, no `0x03/0x8E`, no RF |

### 6.3 debug while USB is occupied

Fold 5 has a single USB-C data port. Wired adb from sfub and the RC cannot share it.

**Control path (this campaign, do not rediscover):**

1. Wireless debugging **On** on the Fold. The paired client is Termux `u0_a368@localhost`, which speaks adb to `127.0.0.1:<port>`. that is not sfub.
2. From sfub: `ssh fold` (Termux sshd, `Host fold` → `u0_a368@10.77.206.52:8022`).
3. Then fold-local: `adb devices -l` must show `127.0.0.1:* device` `SM_F946B` / `q5q`. `adb shell id` → `uid=2000(shell)`.
4. Only then plug RC into the Fold **TOP cradle** USB-C.
5. Re-check `ssh fold adb devices -l` **after** plug. Pass requires that localhost transport still `device`.

**Forbidden in S1 (measured 2026-10-23: wasted a full hop):**

- sfub `adb pair` / `adb connect` / mDNS / port-scan to the Fold overlay or LAN IP
- asking the operator for IP:port or a 6-digit pairing code so sfub can become a wireless-debug client
- `phonectl` / `ando` / `ssh sfpix` (Pixel 8 Pro, different phone)

If `ssh fold` works but fold-local `adb devices` is empty/`offline`: run `ssh fold adb-wifi` (or `scripts/fold-adb`) and pick the `device` row. port rotation is normal. **not** a human dependency. **not** `br` BLOCKED. do not pair sfub. only if `adb-wifi` itself says wireless debugging is off do you stop.

If fold-local adb dies when the RC enumerates, record that as an S1 finding. `[untested-hypothesis]` until seen.

### 6.4 log fields (redact serials, account ids, tokens)

Capture timestamped artifacts on the workstation via wireless adb. **System logs ≠ USB payload capture.** `[source-implementation-only]` (Exploration S1)

| field | how | why |
|---|---|---|
| utc timestamp | operator | join later |
| Fold model / Android / One UI / build | `getprop ro.product.model` `ro.build.display.id` `ro.build.version.release` | identity |
| adb transport | `adb devices -l` before and after RC plug | prove wireless survived |
| RC port used | operator photo or note: TOP cradle vs BOTTOM | port distinction |
| Flip power | operator attestation: OFF | bound the test |
| USB dump | `adb shell dumpsys usb` | accessory vs device, Audio/MIDI, functions |
| accessory identity | from dumpsys and/or logcat: manufacturer, model, version, uri; **redact serial** | unknown A |
| kernel/usb logcat | `adb logcat -d -b all` filtered to `UsbDeviceManager`, `UsbAlsaManager`, `UsbPortManager`, `USB`, `AOA`, `accessory` | enumeration |
| Fly process | `adb shell pidof dji.go.v5 dji.go.v4`; `dumpsys activity processes` / `package` | did Fly auto-start / claim |
| attach intent | logcat `ActivityManager` / `UsbAccessoryUriActivity` / Fly activity | default handler |
| charging vs accessory | dumpsys battery / usb charging flag | role |
| permissions | whether a USB permission dialog appeared, which package | ownership |
| dmesg on Fold | `adb shell dmesg` USB lines if permitted | extra; may be empty without root |

**Do not** run `UsbManager.openAccessory` from a throwaway app in S1. That claims the exclusive fd and is an accessory open, not a log. `[source-implementation-only]` as API effect; campaign treats it as out of S1.

**Do not** tcpdump USB payloads, Frida Fly, or write udev on the RC.

Optional **contrast pass** (still Flip OFF, still no writes): unplug TOP, plug **BOTTOM** into the Fold if physically possible, repeat `dumpsys usb`. Expected: **not** an AOA `UsbAccessory`, possibly charging-only or some device-mode gadget the phone hosts. `[untested-hypothesis]` on Fold (phone-as-host vs accessory). Purpose is to prove the two ports differ in logs, not to talk VCOM.

### 6.5 non-payload observations that are in-scope

- accessory appeared / did not
- manufacturer/model/version strings
- Fly auto-launch or not
- wireless adb survived or not
- permission dialog package
- bottom vs top dumpsys difference
- whether `dumpsys usb` shows accessory mode vs MTP/charging

Out of scope even if tempting: byte streams, DUML decode, video, telemetry, stick values, any TX.

### 6.6 pass / fail

**PASS** (S1 complete, still not Flip USB proof of control):

- [ ] Flip remained OFF
- [ ] `ssh fold adb devices` stayed `device` (localhost transport) while RC occupied USB
- [ ] TOP port test recorded
- [ ] `dumpsys usb` (or equivalent) captured
- [ ] manufacturer/model/version logged **or** a well-formed negative (no accessory, with logs proving Fly didn't eat the event unseen)
- [ ] no accessory open, no DUML, no sim-enable, no candidate command
- [ ] serials redacted in the stored artifact

**FAIL:**

- wired adb from sfub, or sfub `adb connect`/`adb pair`, so the Fold USB-C was not free / wrong plane
- any write to accessory/CDC
- Flip powered / paired
- claimed "payload captured" from logcat
- treated bottom-port CDC as phone-facing AOA

**INCONCLUSIVE:** `ssh fold` ok but fold-local adb dropped and no dumpsys; or Fly grabbed the accessory so fast that identity never appeared in dumpsys — then S1 may need a second pass with Fly force-stopped **before** cable, still without `openAccessory`.

### 6.7 still unproven after a clean S1 PASS

- unknown **B**: cold vs Fly-warmed aircraft relay (needs aircraft, S2)
- bulk endpoint behavior inside the PFD (needs S3 open, still read-oriented)
- whether Flip's RC-N3 model string matches FreeFCC filters
- whether bootstrap/keepalive are required on this firmware
- command forwarding / virtual stick / video / telemetry
- O4 vs anything USB identity implies
- Mini-1 `0x03/0x8E` relevance
- concurrent Fly ownership (must remain assumed impossible)

---

## 7. resulting next question

Highest-impact uncertainty this inventory **does not** settle:

**What manufacturer / model / version strings does this RC-N3 advertise as an Android USB accessory on the Fold when attached to the TOP cradle port with Flip OFF, and does that enumeration survive with DJI Fly force-stopped?**

That is S1. Everything else (warmth, Flip pairing, command relay) is later stages.

---

## 8. mapping back to exploration unknowns

| unknown | this S0 | needs |
|---|---|---|
| A transport | two-port model sourced; AOA vs CDC split sourced; **Flip strings unknown** | S1 logs |
| B session | Fly-warm vs cold distinguished in **other-model** source; Flip unknown | S2+ |
| C routing | not this bead | later |
| D authority | not this bead; `0x03/0x8E` remains candidate only | later + safety gate |
| E/F telemetry/video | mux types exist on WM160 AOA; not Flip | later |
| G safety | not this bead | later |

---

## 9. clone provenance

| repo | SHA | date |
|---|---|---|
| Kolya080808/DJI-Link | `cd39ec52f6861e0d91ef48aeb307c6e07fbfb6ee` | 2026-09-02 |
| eraycn/dji-rc-n3 | `44f51b09bc9d2a3d4fdc68189c4f40412822a469` | 2025-05-12 |
| gladiusxosmium/DJI-RC-N3-Joystick_MACOS | `a2ecb987f9102662678fffa930a646b43d963f7b` | 2026-07-05 |
| doesthings/FreeFCC-USB | `55da2d3bc6c59d33f8e9423f43dd5b2f91abbe61` | 2026-08-12 |
| andreapianidev/DJI-FCC-Unlock-iOS | `5ef860adafd11329d5af1141dc71c076ca1484ae` | 2026-09-20 |

Android accessory overview: https://developer.android.com/develop/connectivity/usb/accessory
Android adb wireless: https://developer.android.com/tools/adb
Frida Android: https://frida.re/docs/android/
