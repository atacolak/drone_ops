# drone_ops

we want to fly a **DJI Flip** from **our** software, through the **physical remote**, so we keep the remote’s long-range radio instead of the phone’s short wifi.

today we can **listen** to the remote over USB (stick positions show up in a tiny app of ours). we cannot yet send our own stick, gimbal, or mode commands. the official DJI Fly app already can — it talks USB to the remote, and the remote talks to the drone.

**what this is for:** our own control surface (and later, software that flies with obstacle avoidance on). not their UI. two bets in parallel:

1. copy what Fly writes, and send it ourselves.
2. **steer Fly itself** so it keeps writing, and we just tell Fly what to do.

this repo is the research dump. it is **not** a “go fly in the living room” kit.

## start here

| read | why |
|---|---|
| [`docs/PIPE.md`](docs/PIPE.md) | how the phone, remote, and drone actually connect; when to open/close DJI Fly |
| [`docs/GAPS.md`](docs/GAPS.md) | what blocked us — start here to delegate research |
| [`docs/FOLD-CONTROL.md`](docs/FOLD-CONTROL.md) | how we talk to the phone from this pc |
| [`docs/EXPLORATION.md`](docs/EXPLORATION.md) | original intent / constraints |

stage notes `docs/s0-*` … `docs/s4-*` are evidence, not tutorials.

## what we actually have

- accessory identity: `DJI` / `com.dji.logiclink` / `v0.0.0.0` (top port)
- our apk `tools/aoa-read` owns usb **without Fly as the ui**
- keepalive TX only (`0x06/0x77`). **no stick inject**
- inbound sticks show on bars: duml `0x06/0x05`, five u16, 364 / 1024 / 1684
- after **Fly-warm** (live FPV → kill Fly): fat **`0x574a`** video-class payloads appear. **not demuxed.** parked for a **server** spatial-mapping later
- beads: `.beads/issues.jsonl` (epic `dji-flip-g0-g1-research-j7k`)

## what we do not have

numeric command ids from inside DJI Fly (the bytes it sends for sticks / cine vs sport / gimbal). the app is packed. see gaps.

## hardware

- aircraft: DJI Flip. controller: RC-N3.
- fold on **top** cradle. charge the rc on the **bottom**.
- Graphene/user fold, **no magisk**.
