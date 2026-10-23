# drone_ops

**the remote is the radio. the phone is just a usb parasite.**

research dump for **DJI Flip + RC-N3**: own the **phone-facing USB accessory**, let the **RC’s O4** keep the km-range link. not DJI Fly’s 50 m wifi. not a flight controller. **listen-only** until we have fly’s real write ids.

```
AoaRead on fold  --usb, TOP cradle-->  RC-N3  --o4-->  Flip
```

this is **not** consent to fly indoors, inject sticks, or arm motors.

## start here

| read | why |
|---|---|
| [`docs/PIPE.md`](docs/PIPE.md) | the pipe, what a “channel” is, **when** to turn Fly on/off |
| [`docs/GAPS.md`](docs/GAPS.md) | what blocked us — delegate research from here |
| [`docs/FOLD-CONTROL.md`](docs/FOLD-CONTROL.md) | how we talk to the fold (`fold-adb`, never pair this pc) |
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

numeric fly **write** cmd ids (virtual stick, C/N/S, gimbal). AppGuard ate the dex. see gaps.

## hardware

- aircraft: DJI Flip. controller: RC-N3.
- fold on **top** cradle. charge the rc on the **bottom**.
- Graphene/user fold, **no magisk**.
