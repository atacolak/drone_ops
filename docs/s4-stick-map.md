# S4-prep: stick/channel map (no TX)

status: **mapping only**. not flight. not accessory writes this bead.
bead: `dji-flip-g0-g1-research-j7k.10`
path: RC-N3 phone-facing AOA (`com.dji.logiclink`) on this Fold.

**mapping ≠ we flew.** usb ACK ≠ motion. Mini-1 `0x03/0x8E` is **not** a Flip fact.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + this RC-N3, cited capture |
| `observed-other-model` | Mini-1 / other aircraft |
| `source-implementation-only` | sim apk / S0 source names |
| `untested-hypothesis` | not evidenced on this path |

## two directions (do not collapse)

| direction | what it is | this-path status |
|---|---|---|
| **report** RC→phone | physical sticks as DUML inside RCLink inbound | histograms exist. which cmd is sticks = **not labeled** |
| **inject** phone→RC→O4 | software channels toward Flip | **no this-path cmd identified.** write *path* exists (`j7k.9` keepalive only) |

physical sticks moving the **sim** is report, local, Flip OFF. not O4. `source-implementation-only` of that architecture (`docs/s1-sim-packet-init.md`); not this bead's capture.

## histograms (inbound only, no writes)

sources: `docs/s1-captures/sim-exclusive/framing-stats.txt` (j7k.7, Flip OFF) vs `docs/s3-captures/framing-stats.txt` (S3, Flip ON after Fly-warm then Fly stop). `observed-on-Flip`

| | j7k.7 Flip OFF | S3 Flip ON |
|---|---|---|
| RCLink types | all `0x5749` | `0x5749` + `0x574a` |
| units | 797 | 6013 |
| senders | `0x06` `0x0e` only | also `0x92` `0x03` `0x01` `0x04` … |
| hottest | `0x06/0x05` ×225, then `0x51/0x29` | idle cmds **still present**; new-ish `0x23/0xb2` snd=`0x92` ×1149, `0x03/0xd7` snd=`0x03` ×303 |

j7k.7 Flip-OFF hottest inner (`observed-on-Flip`):

- `cmd_set=0x06 id=0x05` snd=`0x06` rcv=`0x02` ×225, payload len 26
- `cmd_set=0x51 id=0x29` snd=`0x0e` rcv=`0xee` ×246 combined
- `cmd_set=0x00 id=0x0e` snd=`0x0e` rcv=`0x02` ×65, len 13

S3 adds FC/camera-class senders. that is aircraft-present traffic, **not** a stick-inject decode. `observed-on-Flip` for presence; mapping `untested-hypothesis`.

**neither histogram lists `0x03/0x8E` as a counted stick command.** S3's `0x03/0xd7` is inbound from snd=`0x03`, not Mini-1 joystick. `observed-on-Flip`

j7k.7 already: do not call `0x06/0x05` / `0x51/0x29` virtual-stick without a labeled wiggle. still true. `untested-hypothesis` if anyone does.

## sim names (not wire ids)

`docs/s1-sim-rea.md` / `global-metadata.dat`. `source-implementation-only`

- envelope the sim talks: `V1Packet` with `CmdSet`/`CmdId`, 1023-byte payload cap, `V1AOASession`
- channel semantics: `RCCommandSet.ChannelParam`, `PUSH_RC_CHANNEL_PARAM`, `RCStickChannel`, `OnRCStickValueUpdated`
- **no** metadata string `0x8E` / `DataFlyc` / `RCLink` / `DUML`

numeric `CmdId` for `ChannelParam` was **not** recovered (jadx missing). so sim names do **not** assign `0x06/0x05` or `0x03/0x8E`. `untested-hypothesis` to equate them.

## Mini-1 `0x03/0x8E` — candidate only, other model

`docs/s0-capability-matrix.md` / DJI-Link: `DataFlycJoystick`, 17 bytes `flag u8` + pitch/roll/throttle/yaw `f32` LE, flag `0x4A`, ~20 Hz, gated `0x03/0x80`, WM160+MR1SD25. `observed-other-model`

on **this** RC-N3+Fold AOA path: not in sim metadata, not identified in j7k.7/S3 histograms, not authorized. **do not promote to Flip fact.** `untested-hypothesis` that Flip/RC-N3 even accepts it.

`tools/aoa-read` `Duml.build` already throws on `cmd_set=0x03` and `cmd_id` `0x8E` or `0x2A`. `source-implementation-only` (`docs/s4-tx-path.md`)

## best candidate **on this path**

split, because collapsing report vs inject is how you TX the wrong thing.

### report (RC→phone) — best *hypothesis*, not proven

**`cmd_set 0x06` `cmd_id 0x05`**, snd `0x06` → rcv `0x02`, inner len 26, Flip-OFF exclusive, 225/797 units.

why this and not the others:

- only high-rate cmd from sender `0x06` (S0 RC-ish) while Flip is OFF — i.e. controller-alone report, not FC/camera
- survives into S3 (`docs/s3-stream.md`: j7k.7 cmds still present)
- sim's `RCCommandSet.ChannelParam` is the *name* for channels; **id not bound**

**not proven sticks.** needs a labeled physical wiggle on a future exclusive read (not this bead). `untested-hypothesis` as stick report. `observed-on-Flip` as the hottest Flip-OFF RC→phone repeating cmd.

`0x51/0x29` is hotter in some slices but sender `0x0e` and S0 `0x51` is WLM/authority-class on Neo Wi-Fi — worse stick fit. `untested-hypothesis`

### inject (phone→RC→O4) — **unknown**

no this-path evidence for a stick **write** cmd_set/cmd_id.

do **not** fill that hole with Mini-1 `0x03/0x8E`.

if a later gated experiment sends anything channel-shaped, it is a **new** claim, not this map.

## payload layout

**unknown on this path.** `untested-hypothesis` to copy Mini-1 17-byte float layout or bottom-port ADC 364..1024..1684 onto AOA.

what is known:

- inner DUML sits in RCLink `55 cc 49 57` (`0x5749`). `observed-on-Flip` (j7k.7/S3)
- `0x06/0x05` inner length **26** bytes (histogram). `observed-on-Flip`
- channel **order / center / units**: not decoded. no wiggle. no decompile.

## later TX constraint (not this bead)

when (if) usb is allowed to send stick-like frames:

- **center / neutral only**
- **never** `0x03/0x2A` takeoff/land/motors
- **never** treat `0x03/0x8E` as Flip-confirmed
- usb write ≠ aircraft motion (`docs/s4-tx-path.md`)

this bead sent **nothing**.

## residual

- labeled stick wiggle on exclusive read to confirm or kill `0x06/0x05` as report
- numeric `ChannelParam` CmdId from sim (jadx) or from that wiggle
- inject cmd on this path: still open. unknown **D** from S0 stays open

## files

- this: `docs/s4-stick-map.md`
- evidence: `docs/s3-captures/framing-stats.txt`, `docs/s1-captures/sim-exclusive/framing-stats.txt`, `docs/s1-sim-rea.md`, `docs/s0-capability-matrix.md`
