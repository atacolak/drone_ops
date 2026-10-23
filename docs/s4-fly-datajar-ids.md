# S4-prep: Fly `libdatajar.so` — DataFlycJoystick cmd ids

status: **static unpack incomplete**. names recovered. **numeric cmdSet/cmdId not in these bytes.**
bead: `dji-flip-g0-g1-research-j7k.15`

no accessory TX. even if `0x03/0x8E` showed up here it would still be **Fly apk bytes**, not Flip-confirmed TX. it did not show up.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold `dji.go.v5` apk / extracted `.so` |
| `source-implementation-only` | strings / ELF of that apk |
| `observed-other-model` | Mini-1 / DJI-Link `provided.jar` |
| `untested-hypothesis` | not in these bytes |

## blob

| | |
|---|---|
| apk | `dji.go.v5` on fold, 743994244 B `observed-on-Flip` |
| member | `lib/arm64-v8a/libdatajar.so` 219237712 B `observed-on-Flip` |
| git | **no** |
| packer | `libAppGuard.so` + this datajar. **no** `libDexHelper.so`, **no** `assets/classes0.jar` (not the APKiD SecNeo.C triple). `source-implementation-only` |

ELF arm64, stripped, **bad shstrndx**. program headers:

| ph | type | fileoff | filesz |
|---|---|---|---|
| 0 | PT_LOAD RX | 0 | 0x790 |
| 1 | PT_LOAD RW | 0x3db0 | 219206236 |
| 2 | PT_DYNAMIC | 0xd114ae0 | 0x1f0 |
| 4 | PT_LOAD RX | 0xd114078 | 0xa50 |

symbols in dynstr only: `_binary_dexdata0_{start,end,size}`. those labels are **not** a usable dex at file start. `source-implementation-only`

## what static unpack did **not** find

scanned the RW blob (fold `strings` / `grep -aob` + 16 MiB window at 100–116 MiB on sfub):

- no `dex\n035`–`039` magic
- no `cdex`
- `PK\x03\x04` hits exist (first at 44054846) but local headers are not a valid stored/deflate zip of dex
- no `header_size=0x70` + endian `0x12345678`
- 1-byte / 4-byte xor of 4 KiB-aligned starts did not recover dex magic
- zlib inflate of high-entropy 64 KiB slices in that window failed

jadx was **not** run on an inner dex because there isn't one on disk. outer `classes.dex` remains AppGuard stub (j7k.13).

entropy in the 100–116 MiB window is mostly 1.5–5.8 (strings / mixed), with ~8.0 islands. **not** a uniformly encrypted blob. still not a dex file.

## names that **are** in this apk (`source-implementation-only`)

uleb+MUTF-8 string pool (dex-like, no header). two copies (~84 MiB and ~112 MiB). 112 MiB walk:

**legacy midware**

- `Luav/midware/data/model/P3/DataFlycJoystick;`
- `setJoyStick`
- `Luav/midware/data/model/P3/DataFlycSetJoyStickParams;` (+ `$FlycMode`)
- `Luav/midware/data/config/P3/CmdIdFlyc;` / `CmdIdFlyc$CmdIdType`
- `CmdIdFlyc2` / `CmdIdFlyc2$CmdIdType`
- `Luav/midware/data/config/P3/CmdSet;`
- sibling cmd-id enums: Common, Camera, Gimbal, Rc, Simulator, Wifi, …

**ksdk / newer**

- `SendVirtualStickFlightControlData`
- `VirtualStickControlModeEnabled`
- `Luav/ksdk/keyvalue/value/flightcontroller/VirtualStickFlightControlParam;`
- toString prefix `VirtualStickFlightControlParam(pitch=` (length 37 — **only** that prefix, not a full field list)
- `RcChannelParamsMsg`
- `SwUdpJoyStickSend`

**named constants, still no hex**

- `JOYSTICK` `JOYSTICKFLY` `JOYSTICK_PITCH_ADD` `JOYSTICK_THROTTLE_ADD`
- `COMBINED_STICK_COMMAND`
- `FLYC` among `COMMON` `CAMERA` `GIMBAL` `RC` `WIFI` `SIMULATOR` …

## numeric cmdSet / cmdId

**not recovered from this apk.**

no string `cmdId=142`, no `0x8E`, no `0x8e`. format strings ` cmdId=` / ` mCmdId=` / `cmdSet=` exist without values. values live in constructors / enum `value` fields, i.e. bytecode we do not have.

`0x03` `CmdSet.FLYC` + `0x8E` `CmdIdFlyc.JoyStick` / `DataFlycJoystick` is **Mini-1 / DJI-Link `provided.jar`** (`observed-other-model`). **do not treat as Fly-apk-proven. do not TX.**

Fly still containing the **class name** `DataFlycJoystick` does not bind this build to 8E. `untested-hypothesis`.

## payload layout

**not recovered.** `setJoyStick` is a method name only. kotlin toString prefix only names `pitch`. no float/byte layout from this pass.

## residual (not this bead)

1. runtime dump of decrypted dex after AppGuard (`InMemoryDexClassLoader` / maps) — needs Fly running on fold, still no accessory TX.
2. then jadx `CmdIdFlyc$CmdIdType` / `DataFlycJoystick.<init>` for the actual `value` / `_cmdId`.
3. that still would not be Flip-confirmed until a later **gated** TX on this RC+Flip.

## files

- this: `docs/s4-fly-datajar-ids.md`
- so on fold `…/fly-ext/lib/arm64-v8a/libdatajar.so` — **not git**
- sfub windows `/tmp/datajar-100-16.bin` `/tmp/datajar-106-3.bin` — **not git**
