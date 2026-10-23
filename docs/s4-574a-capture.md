# 0x574a retain (read-only, server is the consumer)

Status: **no fold video player.** no stick TX. no Duml.java edits.
Bead: `dji-flip-g0-g1-research-j7k.19`.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this capture |
| `source-implementation-only` | S0 `0x574a` = video-class |

## method

1. fold-adb. Fly started (`DJIDeviceActivity` resumed — **not** `FpvComponentActivity` this warm).
2. force-stop Fly + sim. exclusive `aoa-read`. keepalive `0x06/0x77` only. **no** stick frames.
3. `CaptureActivity` now writes **complete RCLink units** (`55 cc …`) to `units.rclink` plus `units.idx` (`ts_ms type len`). resync/oversize counted in process (log `loop done` may miss on force-stop).
4. **45.4 s**. fold does **not** display video.

`[observed-on-Flip]`

## counts

| | |
|---|---|
| span | 45369 ms |
| units | 14337 |
| `0x574a` | **2481** |
| `0x5749` | 11856 |
| other | 0 |
| first | `type=574a len=4080` |
| head | `55 cc 4a 57 f0 0f 00 00` (len 4080) |

original frame boundaries preserved as concatenated complete RCLink packets. `[observed-on-Flip]`

`0x574a` still present after this Fly-warm even though UI stayed on the **device** page. not a demux. not “we have a player.” `[observed-on-Flip]`

## where the bytes live

**not in git** (~10 MiB):

- fold ` /sdcard/Android/data/sh.colak.dji.aoaread/files/units.rclink` (10240120)
- fold `.../units.idx` (557896)

repo: `docs/s4-captures/574a-stats.txt` `docs/s4-captures/574a-idx-head.txt`

server (not this fold) is the consumer.

## not claimed

playable video, telemetry decode, motors, stick inject.
