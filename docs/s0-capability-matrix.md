# s0 capability matrix — Flip + RC-N3 USB/O4 research

status: S0 OFFLINE RECONNAISSANCE (source inspect). not hardware consent. not an execution approval.
authority: `docs/EXPLORATION.md`
bead: `dji-flip-g0-g1-research-j7k.2`
owner: `hcom:flip-proto`
created: 2026-10-22

this document answers: which exploration sources actually demonstrate USB bridging, virtual-stick/command injection, telemetry, and video; on which precise aircraft/controller pair; and which cells are gaps for **DJI Flip + RC-N3**.

commands, telemetry, and video are independent feasibility questions. other-model support is not Flip proof. a USB handshake is not aircraft control. a protocol ack is not actuation or authority.

## claim classification (required on every factual sentence)

| tag | meaning |
|---|---|
| `observed-on-Flip` | measured or confirmed against DJI Flip (+ RC-N3 when relevant) |
| `observed-other-model` | measured or confirmed on a different aircraft/controller pair |
| `source-implementation-only` | present in current source/docs of a lead; not hardware-validated here |
| `untested-hypothesis` | exploration claim, README hope, or inference not backed by current source or Flip evidence |

this S0 pass produced **zero** `observed-on-Flip` cells. Flip hardware was not touched. `untested-hypothesis` remaining after source inspect stays hypothesis.

## method

- inspected **current** public source/docs of the 11 exploration leads in `docs/EXPLORATION.md`.
- did not clone-and-patch, did not talk to accessories, did not TX candidate commands, did not change RF/region.
- USB framing/init deep-dive and the S1 diagnostic spec are **out of scope** (sibling `dji-flip-g0-g1-research-j7k.1`, `hcom:flip-usb`).
- unknowns C–G from exploration (routing, flight authority, telemetry schema, video demux/keys, safety/keepalive) are **not collapsed**.

## rejected primary paths

### DJI Fly on-screen sticks / UI automation — rejected as primary architecture

DJI Fly on-screen sticks in **direct phone-to-aircraft Wi-Fi** are a short-range path. official Flip help (`repair.dji.com` customId=01700011389): app-control max distance 50 m, max altitude 30 m, RTH unsupported. Flip specs: Wi-Fi "Effective Operating Range" 50 m vs O4 RC "Max Transmission Distance" up to 13 km FCC. `source-implementation-only` of official Flip docs; not our measurement (`observed-on-Flip` remains empty).

automating those gestures (Kotlin AccessibilityService, minitouch, mobile-agent / sealgate-style UI driving of Fly) does **not** put commands onto the RC-N3 O4 radio (`untested-hypothesis` as a Flip/O4 measurement; `source-implementation-only` that these tools drive a phone UI). it drives a phone UI that is itself on the short-range path (`source-implementation-only`).

additional rejection: sealgate authors tried leaving RC-N3 connected while driving Fly UI and reported that "the important navigation inputs are handled directly by the physical controller rather than being exposed as usable on-screen controls" (`observed-other-model` on their Neo+RC-N3 setup; Flip stick-suppression `untested-hypothesis`). UI automation is therefore range-wrong for the O4 requirement, and likely stick-suppressed once an RC owns the session.

product intent requires software-programmatic control **through the RC-N3 long-range O4 path**, plus independent feasibility of commands AND live video/telemetry. UI automation of Fly is a useful Android-automation reference and an explicit **non-primary** path.


### official mobile SDK virtual-stick — not presumed

DJI Mobile SDK V5 5.18.0 supported-product list does **not** include DJI Flip, DJI Neo, or RC-N3 (`source-implementation-only`: https://github.com/dji-sdk/Mobile-SDK-Android-V5/blob/master/README.md). official `IVirtualStickManager` is therefore **not presumed available** on this pair. other listed aircraft (e.g. Mini 4 Pro) are not Flip proof.


## candidates ≠ confirmed write targets

command IDs found in third-party source are **candidates to investigate**, not permitted TX targets, not confirmed Flip features, and not proof that RC-N3 firmware will forward/honor/authorize them.

recorded so far (S0 source inspect; none are Flip-confirmed; this bead does not TX):

- DJI-Link `0x03/0x8E` (`DataFlycJoystick`, 17 bytes: flag `u8` + pitch/roll/throttle/yaw `f32` LE, flag `0x4A`, ~20 Hz) — **hardware-verified on Mavic Mini 1 (WM160) + MR1SD25 only**. CANDIDATE for Flip, **not** a Flip write target, **do not TX**. gating on that pair: `0x03/0x80` NavigationSwitch. `observed-other-model`. Flip/RC-N3 forwarding is `untested-hypothesis`.

- MSDK V5 `IVirtualStickManager` (`enableVirtualStick`, `sendVirtualStickAdvancedParam`) — compiled SDK path for **listed** products only; exposes **no** raw `0x03/0x8E`. `source-implementation-only`. not a Flip write target.
- neo-control (Neo **Wi-Fi**, not RC-N3 USB): sticks `cmdSet 0x01` / `cmdId 0x0a` (4×11-bit channels 364..1024..1684); discrete takeoff/land `0x03/0x2a` tunneled in `0x51/0x01`; authority `0x51/0x13`. `0x03/0x8E` **absent**. `source-implementation-only`. `0x03/0x2a` is a flight command class — S4-gated even as analysis.
- RC-N3 bottom VCOM (leads 6–7, no aircraft): DUML cmd_set `0x06` ids `0x24` (sim-enable), `0x01` (stick poll), `0x27` (buttons), unsolicited `0x26`. RC-local, not aircraft inject. `source-implementation-only`.
- FreeFCC-USB / FCC-Unlock-iOS session IDs (CANDIDATES, not Flip-confirmed, **do not TX**): bootstrap `0x00/0x00`; keepalive `0x06/0x77`; OSD push `0x03/0x43`; limit-state `0x03/0x55`. `source-implementation-only`. Flip presence `untested-hypothesis`. RF/region IDs in the same repos (`0x03/0xDF` assistant-unlock, `0x03/0xF9` param-by-hash, country-code / FCC profile frames) are **out of campaign scope** — not write targets. `0x03/0x8E` is **absent** from both codebases (`source-implementation-only`).


S4 (separately gated) is the earliest stage that may even *analyze* flight-control feasibility against observed safe responses.


## matrix

one row per exploration source. cells: what was **shown**, not hoped.

legend for USB/Wi-Fi columns: `yes` / `no` / `unknown` plus classification.

Flip+RC-N3 applicability: `gap` (no Flip evidence; would have to be proven) / `possible` (transport or pair is close enough that S1+ could settle) / `contradicted` (current source is a different link class than RC-N3 USB/O4).

| # | source | pair evidenced | USB phone accessory | USB sim/charge port | aircraft-facing Wi-Fi | command inject / virtual sticks | telemetry | video | Flip+RC-N3 applicability | notes / paths |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | DJI Mobile SDK V5 5.18.0 | **no Flip pair**. listed: Mini 3/3 Pro/4 Pro, Mavic 3 Enterprise/3TA, M30, M300/350, Matrice 4/4D/400, H30. no Flip, Neo, RC-N3 (`source-implementation-only`) | unknown for Flip (`untested-hypothesis`). SDK hides USB (`source-implementation-only`) | **no** (`source-implementation-only`) | unknown for Flip (`untested-hypothesis`) | official virtual-stick **not presumed**. `IVirtualStickManager` on listed products only; no raw cmd IDs (`source-implementation-only`) | SDK telemetry on listed products only — **not Flip** (`source-implementation-only`) | SDK liveview on listed products only — **not Flip** (`source-implementation-only`) | **contradicted** as official Flip virtual-stick path (`source-implementation-only`) | https://github.com/dji-sdk/Mobile-SDK-Android-V5 |
| 2 | DJI Fly on-screen sticks | Flip **documented** Wi-Fi app-control (50 m / 30 m, no RTH) (`source-implementation-only` of official Flip help). Neo virtual joysticks in same article (`source-implementation-only` of official Neo help) | **no** — this path is phone Wi-Fi, not RC USB (`source-implementation-only`) | **no** (`source-implementation-only`) | **yes** — Flip Wi-Fi 50 m (`source-implementation-only` of Flip specs) | Fly on-screen / takeoff-hold UI over Wi-Fi. **rejected as primary path**. does not ride O4 (`source-implementation-only` of docs; O4 routing `untested-hypothesis` only in the negative sense that this path is specified as Wi-Fi) | Fly HUD on Wi-Fi app-control (`source-implementation-only`); not our client | Fly liveview on Wi-Fi app-control (`source-implementation-only`); not our client | **contradicted** as primary (short-range Wi-Fi, not RC-N3 O4) (`source-implementation-only`) | https://repair.dji.com/help/content?customId=01700011389 ; https://www.dji.com/flip/specs |
| 3 | [Edison-Watch/mobile](https://github.com/Edison-Watch/mobile) (sealgate) | DJI Neo + phone Wi-Fi + stock DJI Fly; **not** Flip; RC-N3 abandoned (`observed-other-model` author flight) | **no** AOA. USB host API for generic OTG, unused for flight (`source-implementation-only`) | **no** (`source-implementation-only`) | **yes** — Fly uses Neo Wi-Fi; this app only drives Fly UI (`source-implementation-only`) | **UI gestures only** (`dispatchGesture`). no DUML / `0x03/0x8E`. **rejected as primary path** (`source-implementation-only`) | screenshots + a11y tree, not radio telemetry (`source-implementation-only`) | periodic JPEG screenshots of Fly, not demuxed video (`source-implementation-only`) | **contradicted** as primary (`source-implementation-only` of this repo; `observed-other-model` that authors flew Neo via Fly UI). Android automation reference only | blog 2026-09-08; `AndroidComputerSource.kt`; `AndroidUsbSource.kt` |
| 4 | [lslv1243/neo-control](https://github.com/lslv1243/neo-control) | first-gen **DJI Neo** + iPhone + DualSense; **RC-N3 not used** (`source-implementation-only`) | **no**. USB hits are Mac capture of the iPhone (`source-implementation-only`) | **no** (`source-implementation-only`) | **yes** — UDP `192.168.2.1:9003` + BLE GATT `0xFFF0` (`source-implementation-only`) | **yes on Neo Wi-Fi**: DUML `0x01/0x0a` sticks. NOT `0x03/0x8E`. takeoff/land `0x03/0x2a` in `0x51/0x01` — CANDIDATES, not Flip write targets (`source-implementation-only`) | **absent** in client (open-loop). gating story `untested-hypothesis` for Flip | **absent** (`source-implementation-only`) | **contradicted**. Neo aircraft-facing Wi-Fi, **not** RC-N3 USB (`source-implementation-only`) | `DroneLink.swift`, `Control.swift`, `BLELink.swift` |
| 5 | [Kolya080808/DJI-Link](https://github.com/Kolya080808/DJI-Link) | **Mavic Mini 1 (WM160) + RC MR1SD25**. Flip and RC-N3 **absent** (`observed-other-model`). radio is Mini-era enhanced Wi-Fi, **not O4** (`observed-other-model`) | **yes** AOA: RC is USB host; Pi Zero raw_gadget accessory VID `0x18D1` PID `0x2D00`; strings manufacturer DJI, model `WM160` / `com.dji.logiclink` (`source-implementation-only` / `observed-other-model`) | Mini RC micro-USB serial `2CA3:0008` is one-way, no telemetry/video. `--sim` is a UI mock. **not** an inject port (`observed-other-model`) | **no** phone-to-aircraft Wi-Fi. PC↔Pi TCP 9910 only (`source-implementation-only`) | **yes on WM160**: `0x03/0x8E` sticks after `0x03/0x80` GS-open; `0x01/0x0A` ignored. takeoff/motors `0x03/0x2A` — CANDIDATES, **do not TX**, not Flip-confirmed (`observed-other-model`) | OSD `0x03/0x43` (+ home `0x03/0x44`, battery `0x0D/0x02`) (`observed-other-model`) | H.265 Annex-B on composite `0x574A`/`0x574D`, ffmpeg decode (`observed-other-model`) | **gap**. other-model USB-AOA + sticks is not Flip/O4 proof (`untested-hypothesis` for Flip). Mini 5 Pro already failed RC-N3 relay in lead 9 (`observed-other-model`) | `src/core/drone.cpp`; `src/pi/aoa_device.cpp`; `VIRTUAL_STICK_RESEARCH_2026.md` |
| 6 | [eraycn/dji-rc-n3](https://github.com/eraycn/dji-rc-n3) | RC-N3 standalone; no aircraft. README: PC sim "DJI Virtual Flight" (`source-implementation-only`) | **no** (`source-implementation-only`) | **yes** — bottom USB-C CDC-ACM/VCOM (`source-implementation-only`) | **no** (`source-implementation-only`) | **none**. reads physical sticks → vgamepad. host→RC DUML `0x06/0x24` sim-enable, poll `0x06/0x01` sticks, `0x06/0x27` buttons. these are RC-local CANDIDATES, not aircraft inject, not Flip-confirmed (`source-implementation-only`) | **no** aircraft telemetry; stick ADC 364..1684 only (`source-implementation-only`) | **no** (`source-implementation-only`) | **possible** for bottom sim/charge port identity only; **gap** for O4 command/video/telemetry; **contradicted** as inject path (`source-implementation-only`) | `main.py`; README "Connect via bottom type-C" |
| 7 | [gladiusxosmium/DJI-RC-N3-Joystick_MACOS](https://github.com/gladiusxosmium/DJI-RC-N3-Joystick_MACOS) | RC-N1/N2/N3 standalone; README: "No drone needed" (`source-implementation-only`) | **no** — code unused; README: use bottom port, not top (`source-implementation-only`). top=AOA is `untested-hypothesis` here | **yes** — bottom USB-C between stick mounts, CDC serial 115200 (`source-implementation-only`) | **no** aircraft Wi-Fi. BLE gamepad is host-side output (`source-implementation-only`) | **none**. same DUML read path plus unsolicited `0x26` stick frames. no `0x03/0x8E` (`source-implementation-only`) | **no** (`source-implementation-only`) | **no** (`source-implementation-only`) | **possible** for bottom-vs-top port distinction; **gap** for phone accessory / O4 / Flip (`source-implementation-only`) | `README.md` §4 step 3; `dji_rcn3_mac.py`; `dji_rcn3_gui.py` |
| 8 | [doesthings/FreeFCC-USB](https://github.com/doesthings/FreeFCC-USB) | README **marketing** lists Flip among "universal" aircraft (`untested-hypothesis`). author: "Untested on real hardware". community: RC-N3+Lito mixed; RC-N3+Mini 5 Pro 0 responses (`observed-other-model` issues). **no Flip hardware evidence** (`untested-hypothesis`) | **yes in source**: AOA `UsbManager.openAccessory()` TOP cradle; filter manufacturer DJI, models `T600` / `com.dji.logiclink` / `WM160` / `AG410` (`source-implementation-only`). our Fold/RC-N3 enum `untested-hypothesis` | **distinguished**: README bottom = charging, not AOA (`source-implementation-only`) | **no** phone-to-aircraft Wi-Fi (`source-implementation-only`) | **none**. no virtual sticks, no `0x03/0x8E`. purpose is FCC radio unlock — **forbidden here** (`source-implementation-only`) | serial-number regex only (`source-implementation-only`) | **none**; stream drained (`source-implementation-only`) | **possible** as Android AOA transport + exclusive-handoff ref. **gap** for Flip (`untested-hypothesis`). **contradicted** as stick/video path (`source-implementation-only`). **no RF work** | `DumplTransport.kt`; `accessory_filter.xml`; README TOP vs BOTTOM |
| 9 | [andreapianidev/DJI-FCC-Unlock-iOS](https://github.com/andreapianidev/DJI-FCC-Unlock-iOS) | **RC-N3 (`DJI RC151`) + DJI Neo** author-confirmed (`observed-other-model`). Mini 5 Pro 0 ACKs (`observed-other-model`). README lists RC-N2 as untested, not a 0-ACK report (`untested-hypothesis`). Flip **absent** (`source-implementation-only`) | **yes in source**: iOS MFi `EASession`, protocol `com.dji.logiclink` (`source-implementation-only` / `observed-other-model` on Neo) | README: TOP cradle, not bottom (`source-implementation-only`) | **no** phone Wi-Fi; RC radio to aircraft (`source-implementation-only`) | **none**. no `0x03/0x8E`. only config writes (`0xF9`) + FCC profile — **forbidden RF/limit work**. mode byte `0x11` "Joystick" is telemetry, not inject (`observed-other-model` on Neo) | OSD `0x03/0x43` decoder + `0x03/0x55` (`source-implementation-only`; `observed-other-model` on Neo). Flip schema `untested-hypothesis` | interleaved >1 MB/s on logiclink, **parser discards** (`skippedBytes`). not demuxed (`source-implementation-only`) | **possible** as accessory-session / warm-link / OSD-ref. **gap** for Flip (Mini 5 Pro already failed identical RC-N3 transport — `observed-other-model`; Flip itself `untested-hypothesis`). **contradicted** as virtual-stick (`source-implementation-only`). **no RF work** | `ExternalAccessoryTransport.swift`; `Experimental.swift`; README "Open DJI Fly first" |
| 10 | Android Frida on DJI Fly | method, not a pair. stock Fold root **not presumed** (`untested-hypothesis`) | cannot ordinarily sniff another app's owned USB fd (`source-implementation-only` of Android UID/SELinux + Frida-needs-root docs) | n/a (`source-implementation-only`) | n/a (`source-implementation-only`) | not a command path (`source-implementation-only`) | not a telemetry path (`source-implementation-only`) | not a video path (`source-implementation-only`) | **possible** as observation **only if** rooted or Fly is repackaged; gadget/repack may break Fly (`untested-hypothesis`). **not** a control architecture (`source-implementation-only`) | https://frida.re/docs/android/ |
| 11 | Android `UsbManager` accessory / AOA | platform API, not a Flip pair | **possible** `openAccessory()` → exclusive `ParcelFileDescriptor` **if** RC-N3 enumerates as AOA. RC-N3-is-AOA is `untested-hypothesis` until S1. kernel `f_accessory.c` `open_excl` → `-EBUSY` (`source-implementation-only` of AOA). **no concurrent Fly + custom client** | unknown (different port) (`untested-hypothesis`) | n/a (`source-implementation-only`) | none shown. independent client must implement init/framing itself (`untested-hypothesis` on Flip) | none shown (`source-implementation-only`) | none shown (`source-implementation-only`) | **possible** as independent client **after** Fly releases the accessory; **gap** until S1 enum (`untested-hypothesis`) | https://developer.android.com/develop/connectivity/usb/accessory |

### per-source evidence


#### 1 — DJI Mobile SDK V5

- current README v5.18.0: https://github.com/dji-sdk/Mobile-SDK-Android-V5/blob/master/README.md — Flip, Neo, RC-N3 **absent**. `source-implementation-only`.
- virtual stick sample (listed products): `SampleCode-V5/android-sdk-v5-sample/src/main/java/dji/sampleV5/aircraft/models/VirtualStickVM.kt` — `VirtualStickManager.getInstance()`, `enableVirtualStick()`, `sendVirtualStickAdvancedParam`. raw USB / `0x03/0x8E` not exposed. `source-implementation-only`.
- Flip applicability: official virtual-stick **contradicted** by product list (`source-implementation-only`). do not treat Mini 4 Pro support as Flip support.

#### 2 — DJI Fly on-screen sticks (rejected primary)

- Flip help: https://repair.dji.com/help/content?customId=01700011389 — "When using app control, the max flight altitude of the aircraft is 30 m and the max flight distance is 50 m"; RTH unsupported. `source-implementation-only` of official Flip docs.
- Flip specs: https://www.dji.com/flip/specs — Wi-Fi effective range 50 m; O4 RC max transmission (unobstructed) FCC 13 km. `source-implementation-only`. O4 range is an engineering characteristic, not BVLOS permission.
- Neo section of the same help article describes **virtual joysticks** in Fly after Wi-Fi connect. `source-implementation-only` of official Neo help. Flip help describes takeoff-hold / landing-hold and modes; it does not, in the text retrieved here, name "virtual joysticks" for Flip as explicitly as for Neo. whether Flip Fly shows analog on-screen sticks is still `untested-hypothesis` at UI granularity; the **range cap** is documented for Flip app control either way (`source-implementation-only`).
- **rejected as primary** because this is the short-range Wi-Fi path, not RC-N3 O4 (`source-implementation-only` of official Flip docs).

#### 3 — Edison-Watch/mobile (sealgate)

- code: https://github.com/Edison-Watch/mobile
- author report: https://sealgate.ai/blog/mobile-tunnel-agents-control-android-devices (2026-09-08)
- pair shown: DJI Neo piloted by driving stock DJI Fly on-screen sticks over phone Wi-Fi. Flip is not mentioned. `observed-other-model` (authors report a table-landing). not Flip proof.
- USB accessory: not used for flight. `AndroidUsbSource.kt` is USB **host** (`UsbManager.getDeviceList()`), not `openAccessory`. `source-implementation-only`.
- command inject: `ComputerAccessibilityService` + `dispatchGesture`. no candidate DUML IDs. `source-implementation-only`.
- telemetry/video: `takeScreenshot()` JPEGs + a11y tree. not radio demux. `source-implementation-only`.
- RC-N3: authors explicitly chose **not** to use it. quote: "the USB protocol between the DJI RC-N3 and its host is proprietary… communication was sufficiently obfuscated". they also report on-screen nav inputs vanish while RC-N3 is connected. `observed-other-model` (their Neo setup). does **not** prove Flip USB is undecodable; it does prove UI-automation-while-RC-connected is a bad primary architecture.
- Flip applicability: **contradicted** as the O4 path (`source-implementation-only` of this architecture; `observed-other-model` that authors flew Neo via Fly UI). keep as Android automation reference only.

#### 4 — lslv1243/neo-control — Neo Wi-Fi, NOT RC-N3 USB

- URL: https://github.com/lslv1243/neo-control
- pair shown: first-gen DJI Neo + custom iOS app + PS5 DualSense / extended gamepad. RC-N3 is not in the runtime. `source-implementation-only`.
- transport: BLE QuickConnect (GATT service `0xFFF0`, write `0xFFF5`, notify `0xFFF4`, `NEHotspotConfiguration`) then Wi-Fi UDP `192.168.2.1:9003`. `ios-app/Sources/BLELink.swift`, `DroneLink.swift`. `source-implementation-only`.
- USB: every "USB" hit is host-Mac capture of a trusted iPhone (`capture.sh`, `capture-both.sh`) or a generic DUML-on-serial/USB/Wi-Fi comment in `duml.py`. **not** an RC-N3 or aircraft USB path. `source-implementation-only`.
- sticks: `Control.swift` `cmdSet 0x01`, `cmdId 0x0a`, sender `0x02` APP, receiver `0xa9`, 28-byte payload, 4 channels LE 11-bit, range 364..1024..1684, ~20 Hz. CANDIDATE for **Neo Wi-Fi**, not Flip USB. `source-implementation-only`.
- other IDs (CANDIDATES, flight-class, do not TX): inner `0x03/0x2a` (0x01 takeoff, 0x02 land, 0x1e force-land) inside `0x51/0x01` tunnel; authority `0x51/0x13`. `0x03/0x8E` is **absent**. `source-implementation-only`.
- telemetry/video: client has none. `DroneLink.swift` comment: standalone gets no type-0x02 stream, no OSD, no 0x00 accept — open-loop. README attributes gating to account-binding keys in Fly secure storage. mechanism is `untested-hypothesis` for Flip/RC-N3; absence in this client is `source-implementation-only`.
- Flip applicability: **contradicted** for the required RC-N3 O4 USB path (`source-implementation-only`). packet encoding may still inform later DUML decoding, but other-model Wi-Fi support is not Flip proof.

#### 5 — Kolya080808/DJI-Link — Mavic Mini 1 USB-AOA; `0x03/0x8E` is a CANDIDATE

- URL: https://github.com/Kolya080808/DJI-Link
- pair shown: DJI Mavic Mini 1 (`WM160`) + Mini RC (`MR1SD25`, fw cited v4.2.2.60). greps for Flip / RC-N as models: none. `observed-other-model`.
- host path: PC app `--TCP:9910-->` Pi Zero 2 W `raw_gadget` AOA accessory, **not** an Android `UsbManager` client. same AOA *class* as leads 8/11, different gadget. `source-implementation-only`.
- RC is USB host. AOA GET_PROTOCOL/SEND_STRING/START. accessory strings include manufacturer `DJI`, model `WM160` / `com.dji.logiclink`. `source-implementation-only` / `observed-other-model`.
- mux: DUSS `[0x55, 0xCC, type u16 LE, len u32 LE, payload]`. type `0x5749` DUML; `0x574A`/`0x574D` video. USB framing inventory is the sibling bead. `source-implementation-only`.
- sticks: `src/core/drone.cpp` `0x03/0x8E` 17-byte joystick; authority `0x03/0x80`. author research: `0x01/0x0A` ignored on WM160; sticks accepted after auto-takeoff / airborne. `observed-other-model`. **CANDIDATE on Flip. not confirmed. not authorized TX.**
- other flight-class IDs (CANDIDATES, S4-gated, do not TX): `0x03/0x2A` takeoff/land/RTH/motors; `0x03/0xFE` motor force-disable; param `0x03/0xF8`/`0x03/0xF9`. `observed-other-model` on WM160; Flip `untested-hypothesis`.
- telemetry: `src/core/telemetry.cpp` `0x03/0x43` OSD (incl. FLYC_STATE 17 Joystick, SDKCtrlDevice APP=1). `observed-other-model`. same cmd id as lead 9 OSD decoder — still not Flip proof.
- video: H.265 slices + ffmpeg. `observed-other-model`. lead 9 on RC-N3+Neo *saw* high-rate interleaved bytes but did not demux. Flip video is unknown **F**.
- radio: Mini 1 RC-to-aircraft is **not O4** (`observed-other-model` / `source-implementation-only` of that stack). do not treat this stack as an O4 demonstration.
- Flip applicability: **gap**. richest USB-bridge + sticks + telemetry + video *on another model* (`observed-other-model`). RC-N3 + Mini 5 Pro already returned 0 ACKs (lead 9) (`observed-other-model`). collapsing C on the back of WM160 would be cope. Flip `untested-hypothesis`.


#### 10 — Frida

- docs: https://frida.re/docs/android/ — attaching to arbitrary apps typically needs `frida-server` as root. `source-implementation-only`.
- stock Fold root is **not presumed**. an independent app cannot read another UID's USB fd. gadget/repack of Fly may trip integrity checks. `untested-hypothesis` that Fly would still talk to RC-N3 after resigning.
- not a command/telemetry/video implementation. observation method only, and a weak one without root (`source-implementation-only`).

#### 11 — Android UsbManager accessory

- https://developer.android.com/develop/connectivity/usb/accessory — `UsbManager.openAccessory()` → `ParcelFileDescriptor` / bulk streams. `source-implementation-only` of the platform.
- AOA gadget exclusive open: `f_accessory.c` `acc_open` `atomic_xchg(&dev->open_excl, 1)` → `-EBUSY`. `source-implementation-only` of the kernel AOA gadget. **whether RC-N3 phone-facing port is AOA is S1**, not a current fact (`untested-hypothesis`).
- cannot presume DJI Fly and a custom app own the same accessory concurrently (`source-implementation-only` of AOA exclusivity; RC-N3-is-AOA `untested-hypothesis`). independent client is "after Fly releases", then it must implement init, keepalive, telemetry, video itself. unknowns B–G stay open.

#### 6 — eraycn/dji-rc-n3

- URL: https://github.com/eraycn/dji-rc-n3 (`README.md`, `main.py`).
- pair shown: RC-N3 plugged into a PC. no aircraft in the pipeline. `source-implementation-only`.
- USB: README instructs "Connect via bottom type-C". Windows VCOM via DJI Assistant 2 consumer-drones driver. not Android accessory. `source-implementation-only`.
- inject: not implemented. `0x03/0x8E` absent. `0x06/0x24` enables simulator mode on the RC MCU — that is a write to the controller, not virtual-stick to an aircraft, and is still a CANDIDATE not a Flip write target. `source-implementation-only`.
- telemetry/video: absent. `source-implementation-only`.
- Flip applicability: does not prove phone-facing accessory, O4 forwarding, or Flip control (`source-implementation-only`). S1 may *enumerate* this bottom port as a distinct interface from the top port; that is USB identity, not command path.

#### 7 — gladiusxosmium/DJI-RC-N3-Joystick_MACOS

- URL: https://github.com/gladiusxosmium/DJI-RC-N3-Joystick_MACOS (`README.md` protocol notes; `dji_rcn3_mac.py`; `dji_rcn3_gui.py`).
- pair shown: RC-N1/N2/N3, no drone, no DJI account. `source-implementation-only`.
- USB: "Plug the DJI RC into the computer using its **bottom** USB-C port (between the stick mounts — not the top port)". transport USB CDC 115200; Windows "DJI USB VCOM For Protocol". `source-implementation-only`.
- inject: not implemented. outputs are macOS keyboard, or ESP32 BLE gamepad named "RCN3 by.GXO". `source-implementation-only`.
- command IDs in this source (RC-local DUML cmd_set `0x06`, CANDIDATES, not Flip-confirmed, not authorized TX): `0x24` sim-enable payload `0x01`; poll `0x01` (38-byte stick reply); poll `0x27` (58-byte buttons); unsolicited `0x26` 21-byte stick push. `source-implementation-only`.
- Flip applicability: strongest S0 evidence that **sim/charge (bottom) ≠ phone-facing (top)**. does not implement or prove the top accessory path. `source-implementation-only`.

#### 8 — doesthings/FreeFCC-USB — AOA transport reference ONLY; no RF

- URL: https://github.com/doesthings/FreeFCC-USB
- product of this repo is FCC/CE radio-power switching. **out of campaign scope.** we take USB/session facts only.
- transport shown in source: Android AOA on **TOP** phone-cradle port. `AndroidManifest.xml` `android.hardware.usb.accessory`; `res/xml/accessory_filter.xml`; `DumplTransport.kt` `UsbManager.openAccessory()` → `ParcelFileDescriptor`. RC is USB host, phone is accessory. `source-implementation-only`.
- README: close DJI Fly first (exclusive accessory); `releaseUsb()` hands back. `source-implementation-only`. matches lead 11 kernel exclusivity.
- README TOP vs BOTTOM table: top AOA for Fly video/telemetry/flight; bottom charging / different USB mode. `source-implementation-only`. this is the same port split as lead 7, from the opposite (phone) side.
- framing claimed: DUML inside RCLink envelope; bootstrap + keepalive (`source-implementation-only`). USB framing deep-dive is the sibling bead. IDs above are CANDIDATES, not Flip write targets.
- virtual sticks / `0x03/0x8E` / video demux: **absent**. telemetry: ASCII serial regex only. `source-implementation-only`.
- Flip: README compatibility table marks Flip ✅ as part of a "universal" profile. that is marketing, not a test log. author banner: "Untested on real hardware." **do not upgrade to Flip proof.** `untested-hypothesis`.
- Flip applicability: **possible** AOA client shape for S1 enum to confirm or refute on Fold (`untested-hypothesis` on this Fold). **gap** for commands/video/telemetry. **no RF**.

#### 9 — andreapianidev/DJI-FCC-Unlock-iOS — session/OSD reference ONLY; no RF

- URL: https://github.com/andreapianidev/DJI-FCC-Unlock-iOS
- again an FCC/altitude project. **out of campaign scope** to apply those writes. useful for accessory init/response investigation.
- pair **evidenced by author**: RC-N3 + DJI Neo, FCC power verified on Fly transmission graph. `observed-other-model`. not Flip.
- pair **failures** (same transport class): RC-N3 + Mini 5 Pro → 0 ACKs with healthy RCLink (`observed-other-model`). README lists RC-N2 as untested — not a Mini 4 Pro 0-ACK (`untested-hypothesis`). this is why unknown **C** must not collapse: RC-N3 USB working on Neo is not Flip (or Mini 5 Pro) proof.
- iOS channel: undocumented MFi string `com.dji.logiclink` in `Info.plist`. accessory presents as `DJI RC151`. `source-implementation-only` / `observed-other-model`.
- **warm link** (unknown B): README — open DJI Fly until live camera, close it, then companion app; cold apply gets 0 responses; issue #4 still open ("initialise the link ourselves"). `observed-other-model` on Neo. Flip cold-start `untested-hypothesis`. S1 (Flip off) cannot settle aircraft relay.
- telemetry: `OsdGeneral` on `cmdSet 0x03` `cmdId 0x43`; limit state `0x03/0x55`; unit tests in `FreeFCCTests/OsdGeneralTests.swift`. `source-implementation-only`. Flip may use different addressing (Mini 5 Pro census differed). `untested-hypothesis`.
- video: present as high-rate interleaved bytes on logiclink; parser skips non-DUML. **not a video client.** `source-implementation-only`.
- virtual sticks: **absent**. `0x03/0x8E` absent. `0xF9` write of `flying_limit.max_height` to 500 was ACKed then clamped to 120 on Neo — that is a limit/RF-adjacent write, **forbidden**, and also not stick inject. `observed-other-model`.
- Flip applicability: **possible** for phone-facing accessory + session-state questions (`observed-other-model` on Neo; Flip `untested-hypothesis`). **gap** for Flip command/video. **contradicted** as a stick solution (`source-implementation-only`). **no RF**.



## highest-impact gaps (S1 vs S2+/S4)

filled from current-source inspect. still **zero** `observed-on-Flip` cells. unknowns C–G stay open.

### S1-settleable (RC-N3 + Fold, Flip OFF, read-only)

unknown **A**: what the **TOP phone-facing** USB-C port actually enumerates as on *this* firmware + Fold (AOA accessory strings vs CDC VCOM vs neither). leads 7–8/11 *claim* top=AOA, bottom=charge/VCOM; that is `source-implementation-only`, not our enum.
unknown **B** (Android layer only): which process owns the accessory with DJI Fly running vs force-stopped; whether `openAccessory()` even appears; exclusivity / `-EBUSY`. no aircraft required.

S1 can also *see* whether bottom and top are different devices. that is USB identity, not a command path.

S1 **cannot** settle: command forwarding through O4, virtual-stick authority, telemetry schema vs Fly, video demux, warm-link-with-aircraft, keepalive/safety. do not TX candidates during S1. USB framing/init procedure itself is sibling `dji-flip-g0-g1-research-j7k.1`.

### S2+ (aircraft paired, grounded, read-only) or later

unknown **B** remainder: Fly-initialized **warm** session vs cold independent client (lead 9: Neo needed Fly live-camera first; issue #4 still open — `observed-other-model`). Flip off cannot answer this (`untested-hypothesis` for Flip).
unknown **C**: does RC-N3 firmware relay software-authored DUML to **Flip**, or drop it? (`untested-hypothesis`). lead 9: same RC-N3 transport ACKed Neo and returned **0 ACKs** on Mini 5 Pro (`observed-other-model`). other-model yes is not Flip yes.
unknown **E**: which pushes a client can receive (`0x03/0x43` OSD is a candidate schema from Mini 1 and Neo — `observed-other-model`); corroboration vs Fly; addressing may differ (Mini 5 Pro census already differed — `observed-other-model`). Flip `untested-hypothesis`.
unknown **F**: video container on RC-N3 logiclink / composite mux; codec; keys; latency; coexistence with commands. lead 5 demuxed H.265 on Mini 1 (`observed-other-model`); lead 9 discarded interleaved bytes on Neo (`observed-other-model` / `source-implementation-only`). Flip `untested-hypothesis`.

### S4 only (separately safety-gated; not authorized now)

unknown **D**: is virtual-stick even a Flip feature over RC-N3 O4? if so: `0x03/0x8E` vs `0x01/0x0a` vs neither; `0x03/0x80` / `0x51/0x13` authority; arming; can physical RC sticks override. all `untested-hypothesis` on Flip.
unknown **G**: keepalive / expiration / lost-link / neutral fallback / operator takeover.
any TX of `0x03/0x8E`, `0x03/0x2A`, `0x01/0x0a`, motor/arming, or RF/region IDs.

## resulting next question for Flip

S1, Flip OFF, read-only: on the Fold, with wireless ADB, cable in the RC-N3 **TOP** phone-cradle port — what USB identity does Android actually report (AOA accessory manufacturer/model vs something else), and who owns it when Fly is/isn't running?

that question settles A and the Android slice of B. it does **not** authorize command TX, does not settle C–G, and does not treat DJI-Link or neo-control as Flip-confirmed control. sibling `hcom:flip-usb` owns the diagnostic procedure. this matrix's job is to stop the next experiment from being the wrong port, the Wi-Fi UI path, or a candidate write.


## source URL index

1. https://github.com/dji-sdk/Mobile-SDK-Android-V5 (v5.18.0 README); virtual-stick sample `SampleCode-V5/android-sdk-v5-sample/src/main/java/dji/sampleV5/aircraft/models/VirtualStickVM.kt`
2. https://repair.dji.com/help/content?customId=01700011389 — https://www.dji.com/flip/specs
3. https://github.com/Edison-Watch/mobile — https://sealgate.ai/blog/mobile-tunnel-agents-control-android-devices
4. https://github.com/lslv1243/neo-control
5. https://github.com/Kolya080808/DJI-Link
6. https://github.com/eraycn/dji-rc-n3
7. https://github.com/gladiusxosmium/DJI-RC-N3-Joystick_MACOS
8. https://github.com/doesthings/FreeFCC-USB
9. https://github.com/andreapianidev/DJI-FCC-Unlock-iOS
10. https://frida.re/docs/android/
11. https://developer.android.com/develop/connectivity/usb/accessory


## what this bead did not do

- no hardware I/O, no accessory open, no ADB-to-device USB payload capture
- no TX of `0x03`/`0x8E` or any other candidate
- no RF/region changes, no motor/arming/flight commands
- no USB framing/init inventory (sibling bead)
- no collapsing of unknowns C–G
- no treating DJI-Link or neo-control as Flip-confirmed control
