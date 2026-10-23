# S4-prep: DJI Fly (`dji.go.v5`) USB TX / stick path

status: **Fly apk RE**. no accessory TX. not proven Flip 8E.
bead: `dji-flip-g0-g1-research-j7k.13`

Fly is the app that already writes onto the RC accessory so O4 can carry sticks. this is **not** the simulator bead.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold `pm path` / pull |
| `source-implementation-only` | strings / jadx of this apk |
| `observed-other-model` | Mini-1 8E |
| `untested-hypothesis` | id not in these bytes |

## apk

| | |
|---|---|
| package | `dji.go.v5` `observed-on-Flip` |
| on-device | `/data/app/~~FGwrDP5qYKYtQB5rjDQfmA==/dji.go.v5-vKs1hua_ffY_Q5moCVOO6g==/base.apk` |
| zip size | 743994244 B (~744 MiB) `observed-on-Flip` |
| git | **no** (too large; `/tmp` / fold home only) |

members (`unzip -l` on fold): **one** `classes.dex` 2206532 B; `libAppGuard.so`; **`libdatajar.so` 219237712 B** (packed payload); `libGroudStation.so` 36256 B. no `libil2cpp`. `source-implementation-only`

## tools

- `scripts/fold-adb` / `adb-wifi` pull. never sfub pair.
- jadx **1.5.6** + Temurin 17 (`/tmp/jadx`, `/tmp/jdk17`) on extracted `classes.dex` → `/tmp/jadx-fly` (982 java files, **AMap + AppGuard only**).
- `PATH="$HOME/.local/bin:$PATH" rea` 6.7.0: `inspect-android-package` on the dex → `unsupported binary format`. no interactive setup. full-apk `inspect-artifact` not run (744 MiB copy did not finish; analysis used on-fold `unzip -p`).

## how Fly opens AOA

`source-implementation-only` from `libdatajar.so` strings (not jadx java):

- `Landroid/hardware/usb/UsbManager;` `Landroid/hardware/usb/UsbAccessory;`
- `openAccessory` `getAccessoryList`
- `android.hardware.usb.action.USB_ACCESSORY_ATTACHED` (+ `com.uav.accessory.USB_ACCESSORY_ATTACHED`, `com.uav.v4.accessory.USB_ACCESSORY_ATTACHED`)
- `com.dji.logiclink` / `["com.dji.logiclink"]`
- `Luav/midware/usb/P3/UsbAccessoryService;` (`handleNewMethod` / `handleOldMethod` / `onGetBody`)
- `Luav/jni/JNIUsbAccessory;`

outer `classes.dex` only has AppGuard + `ParcelFileDescriptor` — same packer pattern as the sim. the USB client is **inside datajar**, not in the jadx tree. `source-implementation-only`

## what it writes (names, not hex)

midware TX stack in datajar:

- `Luav/midware/data/config/P3/CmdSet;` `getCmdSet` `setCmdSet` `mCmdSet=`
- `onV1Packet`
- `, cmdId=` (format string; **value not in the string**)
- `Luav/midware/data/model/P3/DataFlycJoystick;` **and** `setJoyStick`
- ksdk keys: `KeySendVirtualStickFlightControlData`, `SendVirtualStickFlightControlData`, `KeyVirtualStickControlModeEnabled`, `VirtualStickFlightControlParam(pitch=`
- RC report/set: `RcChannelParamsMsg`, `GetChannelParams`, `SetChannelParams`, `KeyRcChannelParams`

`libGroudStation.so` JNI: `native_rcDataDeal`, CRC8/CRC16 (`UAVCalcChecksumCrc8` / `Crc16`) — codec helpers, not cmd ids. `source-implementation-only`

UI `VirtualStickView` / `wifiHorizontalVirtualStick` is **on-screen Fly UI**, not the USB write by itself. `source-implementation-only`

## 8E

Fly **contains** the class `uav.midware.data.model.P3.DataFlycJoystick`. `source-implementation-only`

that is the **same class name** DJI-Link used for Mini-1 `cmd_set 0x03` `cmd_id 0x8E`. **the hex is not in these strings.** packed datajar was not decompiled to bytecode. **do not promote 8E to a Flip fact.** Mini-1 layout stays `observed-other-model`. Fly-id-is-still-8E is `untested-hypothesis`.

## vs this-path AOA histograms

j7k.7/S3 inbound hottest `0x06/0x05` is RC→phone. Fly’s `DataFlycJoystick` / `SendVirtualStickFlightControlData` are **phone→RC** names. not matched to a counted histogram cmd. `untested-hypothesis`

## later TX (not this bead)

if usb ever sends stick-like frames: **center/neutral only**, never `0x03/0x2A`. this bead sent nothing.

## residual

- unpack `libdatajar.so` → dex → jadx `DataFlycJoystick` ctor for actual cmdSet/cmdId
- that still would not be Flip-confirmed until a labeled TX on this RC+Flip

## files

- this: `docs/s4-fly-usb-tx.md`
- apk on fold `/data/data/com.termux/files/home/dji.go.v5.apk` — **not git**
- jadx `/tmp/jadx-fly` — not git
