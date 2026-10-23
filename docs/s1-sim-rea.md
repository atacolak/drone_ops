# S1-sim: rea on `com.fly.simulator`

status: **no aircraft**. not Flip ON. not O4 control.
bead: `dji-flip-g0-g1-research-j7k.6`
tool: `rea` 6.7.0 via `PATH="$HOME/.local/bin:$PATH"` (no `rea-agents setup`)
apk: `/tmp/com.fly.simulator.apk` (not git)

**simulator stick path ≠ Flip O4 aircraft control.**

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + this pull |
| `source-implementation-only` | bytes/names in the apk as inspected here |
| `untested-hypothesis` | not evidenced |

## how rea was run

| step | result | class |
|---|---|---|
| `scripts/fold-adb shell pm path com.fly.simulator` | `package:/data/app/~~WKm2l2kyk2jliSNbctdkCg==/com.fly.simulator-Og6NtqQWMWgKROWPS21jsw==/base.apk` | observed-on-Flip |
| fold-adb pull → Termux home → scp → `/tmp/com.fly.simulator.apk` | 169441947 bytes, sha256 `7447ca1f4f7ac5d7b6dc8485cd813a8d93efb46c8d4a013d2e2db50e100c1966` | observed-on-Flip (size/path); source-implementation-only (digest via rea) |
| `rea inspect-artifact /tmp/com.fly.simulator.apk` | format `apk`, 2925 graph nodes | source-implementation-only |
| `rea inspect-android-package` | **fail** `jadx` not configured (`REA_JADX_MCP_JAR` missing 0.7.1 headless MCP JAR) | source-implementation-only (tool gap) |
| `rea search-android-classes … Usb` | **fail** same jadx gap | source-implementation-only (tool gap) |
| `rea decode-android-resources` | **fail** apktool not on PATH (`REA_APKTOOL_COMMAND`) | source-implementation-only (tool gap) |
| `rea inspect-il2cpp-metadata` | **not a command** on this `rea` wrapper catalog | source-implementation-only (tool gap) |
| no `rea setup` | not run | |

java/dex decompile via rea is **opaque** without jadx. native Hopper/Ghidra not configured. remaining evidence is apk member inventory + IL2CPP `global-metadata.dat` string table + packed dex/so names.

## packed: AppGuard + Unity IL2CPP

`source-implementation-only`

| member | size | note |
|---|---:|---|
| `classes.dex` | 4658224 | AppGuard class names `Lcom/AppGuard/AppGuard/QLVRK;` etc. **no** plaintext `UsbManager.openAccessory` / `logiclink` / `RCLink` / `DUML` |
| `lib/arm64-v8a/libAppGuard.so` | 983565 | packer |
| `lib/arm64-v8a/libil2cpp.so` | 85089584 | Unity IL2CPP |
| `assets/bin/Data/Managed/Metadata/global-metadata.dat` | 13634264 | type/script names below |
| `res/xml/*` | **absent** | no `accessory_filter.xml` member in the zip |

dex *does* contain `Landroid/os/ParcelFileDescriptor;` and `Ljava/io/FileInputStream;` — stream types, not the open call. `source-implementation-only`

## accessory-open

`source-implementation-only` from `global-metadata.dat` (Unity script paths):

- `Assets\SimulatorCore\simulatorcore\Scripts\Core\USB\AndroidOpenAccessory.cs` — type `AndroidOpenAccessory`
- `…\USB\USBBulk.cs`, `USBDevice.cs`, `USBMonitor.cs`, `USBEndPoint.cs`, `MFIMonitor.cs`
- `GetUsbAccessoryManager`, `_usbAccessoryManager`
- logs: `AOA Start Session, fd`, `OnAOAConnected, fd =`, `AOA Read Thread Start...`, `Android Open Accessory Start Failed....`, `kAOAAccessoryOpenFailed`, `ReportAOALinkOccupied`

inferred java path (not decompiled): AppGuard unpack → Unity `SimulatorActivity` `USB_ACCESSORY_ATTACHED` → `UsbManager.openAccessory` → PFD. **bytecode untested-hypothesis**; names + PFD strings + sibling live `dumpsys usb` default-handler `com.fly.simulator` are consistent. sibling live enum is `observed-on-Flip` in `docs/s1-sim-packet-init.md`, not re-measured here.

second USB plane in the same metadata: libusb **host** symbols (`usb_init`, `usb_claim_interface`, `usb_bulk_read`, `usb_bulk_write`) and Windows-style `USB\VID_2CA3&PID_…` strings. that is **not** AOA. `source-implementation-only`. do not collapse with the phone-cradle accessory path.

## init / keepalive

`source-implementation-only`

- session: `V1AOASession` (`…\Core\V1Session\V1AOASession.cs`), `V1USBSession`
- read loop: `[V1AOASession] ReadDataThread: buffer or fd invalid, exiting.` / `AOA Read Thread Aborted.`
- **no** strings `RCLink`, `logiclink`, `DUML`, `DUMPL` (except unrelated `DumpLabel`)
- `KeepAlive` / `SetTcpKeepAlive` / `unable to keepalive` in `libuavanalytics.so` look like TCP/analytics, **not** proven RC keepalive frames. treating them as FreeFCC `0x06/0x77` is `untested-hypothesis`

## stick / report framing

`source-implementation-only` of **names**. wire layout **not** recovered (no jadx/ghidra decompile).

protocol object: `V1Packet` (`…\Core\V1Protocol\V1Packet.cs`) with `CmdSet` / `CmdId`. encode/get errors: `Payload Size Exceed Maximum(1023Bytes), DataLength:{0}, CmdSet:{1}, CmdId:{2}`. CRC helper: `…\V1Protocol\CRC.cs`.

command-set types in metadata:

| set | types seen |
|---|---|
| `RCCommandSet` | `ChannelParam`, `ButtonStatus`, `RCCtrlSettings`, `AndroidSystemStatus`, `SimulatorFlightMode` |
| `GeneralCommandSet` | `REPORT_DATA_REQ`, `GET_DEVICE_STATE_RSP`, `GET_DEVICE_VERSION_RSP`, `GET_VERSION_CONFIG_REQ` |
| `MCCommandSet` | `PUSH_OSD_REQ`, `SET_FDI_PARAMS_REQ` |
| `SpecialCommandSet` | `PUSH_RC221_STATUS` |
| `LBCommandSet` | `RC_INFOS` |

also: `PUSH_RC_CHANNEL_PARAM`, `PUSH_REPORT_DATA`.

stick consumption (sim-local, not Flip TX): `JoystickManager`, `OnRCJoystickValueUpdated`, `OnRCStickValueUpdated`, `RCStickChannel`, `RCLogicChannel`, `DJIRC151` (`…\RemoteController\RCProduct\DJIRC151.cs`). `VirtualJoystick` is a Unity UI/virtual-remote type, not proof of accessory inject.

**no** metadata string `0x8E` / `DataFlyc`. do not treat this as Flip-confirmed `0x03/0x8E`. candidate IDs remain untyped `CmdSet`/`CmdId` enums until a later exclusive capture or decompile. `untested-hypothesis` as wire values.

## RCLink vs DUML vs other

| hypothesis | verdict |
|---|---|
| FreeFCC `55 CC` RCLink envelope | **not named** in this apk. `untested-hypothesis` |
| DUML (`cmd_set`/`cmd_id`) | **shape-rhymes** with `V1Packet` `CmdSet`/`CmdId` + 1023-byte payload cap. not proven identical. `untested-hypothesis` |
| this apk's own name | **V1Protocol / V1Packet over V1AOASession**. `source-implementation-only` |

packed gap: cannot dump accessory_filter XML or java `openAccessory` callsite without jadx/apktool.

## sim ≠ Flip O4

- this is DJI Fly **Simulator** (Unity), talking to an RC as a phone accessory / optional USB-host gadget. `source-implementation-only`
- `DJIRC151` is an RC product class in the sim (RC-N3 presented as RC151 in S0). still **sim**, not aircraft O4. `source-implementation-only` of the name; Flip forwarding `untested-hypothesis` and out of this bead
- teaching-copy mentions `DJI Mini 5 Pro` as lesson content, not this Fold's aircraft. `source-implementation-only`
- this actor did not open the accessory, did not TX, Flip stayed off

## residual

- jadx 0.7.1 JAR + apktool would unlock `inspect-android-package` / resource XML
- `libil2cpp.so` method bodies (need a native provider; not configured)
- exact V1Packet header bytes vs RCLink/DUML
- stick channel units/order on the wire (needs exclusive client, sibling `j7k.7`, not this bead)

## files

- on-device apk path above; local copy `/tmp/com.fly.simulator.apk` **not in git**
- extracted scratch (also not git): `/tmp/fly-sim-extract/`
- this document: `docs/s1-sim-rea.md`
