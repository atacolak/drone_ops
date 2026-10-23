# S3 read-only accessory stream (Flip ON, after Fly-warm)

Status: **S3**. not S4. not motors. not candidate TX.
Bead: `dji-flip-g0-g1-research-j7k.8`.
Control: fold-adb / adb-wifi.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + RC-N3 + Flip ON this run |
| `source-implementation-only` | S0 channel names |
| `untested-hypothesis` | codec / stick mapping |

stream ≠ virtual-stick. `0x574a` ≠ demuxed playable video unless demuxed (we did not). 

## method

1. Fly FPV was **up** (`FpvComponentActivity` resumed, accessory uid 10382). `[observed-on-Flip]`
2. `am force-stop dji.go.v5` and `com.fly.simulator`. Fly pid gone.
3. exclusive `tools/aoa-read` **read-only** (`openAccessory` + `FileInputStream`). no accessory writes.
4. ~3.5 MiB inbound in a few seconds. then force-stop capture app.

## did the stream die when Fly died?

**no.** `[observed-on-Flip]`

it got **busier** than j7k.7 (Flip OFF exclusive).

## vs j7k.7 (Flip OFF)

| | j7k.7 Flip OFF | this S3 Flip ON after Fly-warm then Fly stop |
|---|---|---|
| accessory | DJI / logiclink / v0.0.0.0 | same |
| RCLink magic | `55 cc 49 57` only (`0x5749`) | `0x5749` **and** `0x574a` (`55 cc 4a 57`) |
| units | 797 | 6013 |
| DUML senders | `0x06` `0x0e` only | also `0x92` `0x03` `0x01` `0x04` … |
| S0 name for `0x574a` | video-class `[source-implementation-only]` | present, 58 units, median payload 4080 `[observed-on-Flip]` |

j7k.7 idle cmds still here (`0x06/0x05`, `0x51/0x29`, `0x00/0x0e`, …). `[observed-on-Flip]`

new traffic includes sender `0x03` (S0: FC) and `0x01` (S0: camera). `[source-implementation-only]` for those device ids; **presence on this capture** `[observed-on-Flip]`. not a decoded OSD table.

`0x574a` heads are not the Mini-1 `0x6d` liveview header from DJI-Link; some begin `00 00 00 01` (annex-b-shaped). **not demuxed, not claimed playable.** `[untested-hypothesis]` as h264; `[observed-on-Flip]` as extra channel + size.

## not claimed

motors, takeoff, `0x03/0x8E` TX (none sent), stick mapping, video playback, Fly TX handshake bytes (Fly was already stopped).

## files

- `docs/s3-stream.md`
- `docs/s3-captures/framing-stats.txt`
- full `aoa.bin` (~3.5 MiB) **not** in git
