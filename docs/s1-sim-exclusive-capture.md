# exclusive AOA read after force-stop sim

Status: **no aircraft**. S2 parked. Not Flip O4 control.
Bead: `dji-flip-g0-g1-research-j7k.7`.
Control: `scripts/fold-adb` / `ssh fold adb-wifi`.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + this RC-N3 this run |
| `source-implementation-only` | matches S0 source |
| `untested-hypothesis` | not evidenced |

**These bytes are RC→phone accessory traffic with Flip OFF. they are not O4 aircraft control.** 

## method

1. fold-adb / adb-wifi; pick `device` row (junk `offline` ignored).
2. `am force-stop com.fly.simulator` and `dji.go.v5`. pids gone.
3. `dumpsys usb` still `ACCESSORY` `DJI` / `com.dji.logiclink` / `v0.0.0.0`.
4. installed in-tree read-only client `tools/aoa-read` (`sh.colak.dji.aoaread`). **no accessory writes** (`FileInputStream` only).
5. USB permission dialog “Allow AoaRead to access DJI RC-N2/3?” → OK.
6. `openAccessory` succeeded. inbound stream without any bootstrap TX from us.
7. force-stop capture app after pull.

`[observed-on-Flip]`

## identity

same as S1: manufacturer `DJI`, model `com.dji.logiclink`, version `v0.0.0.0`, description `DJI RC-N2/3`. serial redacted. `[observed-on-Flip]`

## framing (inbound)

USB bulk reads were 512-byte (or 1–3×512) chunks. payload is **RCLink / DUSS composite**:

```
55 cc 49 57 | length u32 LE | inner DUML starting 55
```

type `0x5749` (`IW`) on **all 797** parsed units. `[observed-on-Flip]` same header S0 attributed to Mini-1 AOA and FreeFCC wrap. `[source-implementation-only]` for the name; **this RC-N3 on this Fold** for the bytes.

we did **not** send bootstrap/keepalive. the RC still pushed a continuous RCLink stream after `openAccessory`. `[observed-on-Flip]` that is **not** proof Fly-warm vs cold aircraft relay (no aircraft). it **is** proof inbound exists without our TX.

inner DUML (parsed):

| who | hex | count |
|---|---|---|
| sender RC-ish | `0x06` | 271 |
| sender `0x0e` | `0x0e` | 526 |
| recv phone/net | `0x02` | 526 |
| recv `0xee` | `0xee` | 271 |

hottest cmds (not mapped to sticks in this bead):

- `cmd_set 0x06 id 0x05` RC→`0x02` ×225, inner len 26
- `cmd_set 0x51 id 0x29` `0x0e`→`0xee` ×246 combined, lens 24 and 69
- `cmd_set 0x00 id 0x0e` `0x0e`→`0x02` ×65, len 13 (cmd_type `0x40`)

**do not call these virtual-stick or video without a labeled wiggle/decode.** `[untested-hypothesis]` if anyone does.

no `0x03/0x8E` TX from us. not searched as a “found command to send.”

## files

| path | what |
|---|---|
| `docs/s1-sim-exclusive-capture.md` | this |
| `docs/s1-captures/sim-exclusive/framing-stats.txt` | unit histogram |
| `docs/s1-captures/sim-exclusive/aoa-meta-head.txt` | first read heads (hex) |
| `tools/aoa-read/` | read-only capture client (not a product) |
| full `aoa.bin` (~400 KiB) | **not** in git (ascii runs; possible ids) |

## residual

- which inner cmd is stick (needs labeled physical wiggle + decode)
- whether TX bootstrap changes the stream
- Flip ON / O4 (S2 parked)
- concurrent ownership still forbidden

## not claimed

control, motors, RF, telemetry schema, video demux, Flip proof beyond RC-N3 phone accessory bytes on this Fold.
