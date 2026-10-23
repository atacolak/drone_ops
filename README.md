# drone_ops

software flight of a **DJI Flip** through the **RC-N3’s long-range radio** (usb phone → remote → o4 → aircraft). **not** dji fly’s 50 m wifi.

this is a research dump. **not** a flight kit. nothing here is a license to take off indoors.

## start here

| | |
|---|---|
| [`docs/P0-FLIGHT.md`](docs/P0-FLIGHT.md) | p0: flight authority. what success is. |
| [`docs/PIPE.md`](docs/PIPE.md) | how the usb/o4 pipe actually works |
| [`docs/GAPS.md`](docs/GAPS.md) | walls, tagged claims |
| [`.beads/issues.jsonl`](.beads/issues.jsonl) | epic `dji-flip-g0-g1-research-j7k` |

evidence notes: `docs/s0-*` … `docs/s4-*`.

## proven on this Flip + RC-N3

- accessory: `DJI` / `com.dji.logiclink` / `v0.0.0.0` (phone on **top** cradle)
- our apk `tools/aoa-read` can own usb with fly killed
- **inbound** stick **reports**: duml `0x06/0x05`, 364/1024/1684. that is the rc telling the phone, **not** us flying
- keepalive **tx** only: `0x06/0x77` (allowlist in `Duml.java` — everything else throws)
- after fly-warm, **`0x574a`** video-class units exist (not demuxed). p1, not p0
- a fly **clone** (`dji.go.zz`) lived, asked for the **real** accessory; still **no outbound `55 cc` dump**

## dead on this box (do not rerun blindly)

| path | result |
|---|---|
| frida gadget / `wrap.sh` in a copy | **sigill** in `gum-js-loop`. java never ready. [`docs/s4-fly-observe-bytes.md`](docs/s4-fly-observe-bytes.md) |
| fridabox virtual fly | **sigsegv** after appguard `onCreate` |
| msdk v5.18 | **flip not on the product list.** mini 4 pro is other-model. no app key, no connect. [`docs/s4-msdk-probe.md`](docs/s4-msdk-probe.md) |
| dronelink | installed; **login wall**; never saw the rc. [`docs/s4-dronelink-probe.md`](docs/s4-dronelink-probe.md) |
| fly fpv sidecar | camera tap only. **no virtual-stick chrome** on this rc overlay. [`docs/s4-fly-sidecar.md`](docs/s4-fly-sidecar.md) |
| apk “virtual stick” ui names | **`wifi_` / `handheld`**. not the rc fpv overlay. **not** the o4 path. [`docs/s4-fly-vs-surface.md`](docs/s4-fly-vs-surface.md) |

mini-1 `0x03/0x8E` and neo wifi are **other aircraft**. do not cargo-cult them as flip facts.

## the actual wall

**we have never captured what store dji fly writes on usb** to this rc.

android will not let a second app sniff fly’s exclusive accessory fd. this fold is graphene **user**, **not debuggable**, **no magisk**. so: **no-root software usb sniffer = no.**

without those bytes we do not have stick/gimbal/mode **send** ids for **this** pair. inbound reports ≠ inject.

## next experiments (o4 only)

1. **hardware usb tap** in the top-cradle cable (cynthion / beagle / real usb2 bulk analyzer — not a charger dongle). watch fly’s writes. no root.
2. **root / usbmon** on the fold — same bytes, software.
3. **one** supervised **center-only** mini-1-shaped joystick frame, operator holding the bird — a single hypothesis, **not** a frame lottery. not indoor fishing.

**not next:** another wrap.sh, another account-gated app unless the operator logs in themselves, 50 m wifi as a silent replace for o4.

## hardware

- flip + rc-n3. fold on **top**. charge rc on **bottom**.
- talk to the phone: [`docs/FOLD-CONTROL.md`](docs/FOLD-CONTROL.md) (`ssh fold`, never pair this pc).
