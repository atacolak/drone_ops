# S4-prep: ChannelParam CmdSet/CmdId from sim apk

status: **ids from sim binary / jadx**. not Flip inject. no accessory TX.
bead: `dji-flip-g0-g1-research-j7k.11`
apk: `/tmp/com.fly.simulator.apk` (169441947 B, not git)

**numeric CmdId for `ChannelParam` was not recovered.** names + struct fields were. this is not a Flip write target.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold pull / prior AOA histograms |
| `source-implementation-only` | jadx output or IL2CPP metadata bytes |
| `untested-hypothesis` | not evidenced |

## jadx

installed user-local (no sudo): Temurin JRE 17.0.21 under `/tmp/jdk17`, jadx **1.5.6** (`https://github.com/skylot/jadx/releases/download/v1.5.6/jadx-1.5.6.zip`).

```
JAVA_HOME=/tmp/jdk17 /tmp/jadx/bin/jadx --no-res -d /tmp/jadx-sim /tmp/com.fly.simulator.apk
```

result: **7** `.java` files. `source-implementation-only`

- `com.AppGuard.AppGuard.QLVRK` extends `Application` (packer bootstrap, native `hn`/`pn`)
- `com.fly.simulator.R` — `R.xml.accessory_filter = 0x7f0f0000`
- no `ChannelParam`, `RCCommandSet`, `V1Packet`, `CmdSet` in any jadx java

`rea inspect-android-package` still needs `REA_JADX_MCP_JAR` (headless MCP 0.7.1), not this CLI. not set. `source-implementation-only`

**packed gap:** AppGuard + Unity IL2CPP. ChannelParam lives in C# metadata (`global-metadata.dat`), not in `classes.dex`. jadx cannot emit those CmdIds. `source-implementation-only`

## IL2CPP metadata v29 (not jadx)

file: `assets/bin/Data/Managed/Metadata/global-metadata.dat` (version 29, sanity `0xFAB11BAF`). `source-implementation-only`

`RCCommandSet` nested `CommandID` field **names** (global field indices 955–962):

| field | name |
|---|---|
| 955 | `CommandSet` (the set byte on the command-set class) |
| 956 | `PUSH_RC_CHANNEL_PARAM` |
| 957 | `PUSH_BUTTON_STATUS` |
| 958 | `SET_RC_CTRL_MODE` |
| 959 | `GET_RC_CTRL_MODE` |
| 960 | `SET_SIMULATOR_MODE` |
| 961 | `GET_SIMULATOR_MODE` |
| 962 | `SET_ANDROID_SYSTEM_STATUS` |

**no field-default I4/U1 values** in `fieldDefaultValues` for these indices. so the actual numbers are in `libil2cpp.so` cctor, not the metadata table. **CmdSet/CmdId numeric = unknown.** `source-implementation-only` for names; `untested-hypothesis` for any hex.

`ChannelParam` is a **struct** (typeDef fieldStart 974, count 8), not the cmd id:

`CH_A`, `CH_E`, `CH_T`, `CH_R`, `CH_Pitch`, `WheelState`, `ButtonState1`, `ButtonState2`

plus adjacent `CH0`–`CH5`. AETR naming is sim-local stick axes. layout size / endian / center **not** in metadata. `source-implementation-only`

`V1Packet` backing fields include `CommandSet`, `CommandID`, `SenderType`, `SenderID`, `ReceiverType`, `ReceiverID`, `SequenceNumber`, `Payload`. that is the inner packet, still without numbers. `source-implementation-only`

other command-set names (also no defaults): `GeneralCommandSet` `PING` / `PUSH_HEARTBEAT` / `PUSH_REPORT_DATA`; `PUSH_OSD` on MC; `WL_MANAGE_PKT_FORWARD`.

## vs our AOA histograms

j7k.7 Flip-OFF hottest: `cmd_set=0x06 id=0x05` snd=`0x06` rcv=`0x02` len 26. `observed-on-Flip` (`docs/s1-captures/sim-exclusive/framing-stats.txt`)

j7k.10 already called that the best **report** hypothesis, not proven sticks.

this bead **cannot bind** `PUSH_RC_CHANNEL_PARAM` → `0x05` or `RCCommandSet` → `0x06`. that would be `untested-hypothesis`. do not treat as Flip inject.

S3 Flip-ON adds `0x03/0xd7`, `0x23/0xb2` — not these CommandID names. `observed-on-Flip` for presence.

`0x03/0x8E` still absent from sim names and from these histograms. Mini-1 only. **not Flip.**

## later TX (not this bead)

if usb ever sends channel-shaped frames: **center/neutral only**, never `0x03/0x2A`. this bead sent nothing.

## residual

- `libil2cpp.so` cctor for the const bytes (needs a native dumper / ghidra; not configured)
- labeled stick wiggle to prove or kill `0x06/0x05` ↔ `PUSH_RC_CHANNEL_PARAM`

## files

- this: `docs/s4-channelparam-ids.md`
- apk `/tmp/com.fly.simulator.apk` not git
- jadx out `/tmp/jadx-sim/` not git
