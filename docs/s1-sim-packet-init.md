# S1-sim: `com.fly.simulator` accessory open + packet/init

Status: **no aircraft**. S2 (`j7k.4`) parked. Not Flip ON. Not O4 control.
Bead: `dji-flip-g0-g1-research-j7k.5`.
Control: `docs/FOLD-CONTROL.md` (`ssh fold` → fold-local adb `device` row).

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + this RC-N3, this run (identity / process only) |
| `source-implementation-only` | from the on-device apk |
| `untested-hypothesis` | not evidenced |

**Simulator stick path ≠ Flip O4 aircraft control.** `[source-implementation-only]` as campaign rule; this run does not contradict it.

## apk / package

| field | value | class |
|---|---|---|
| package | `com.fly.simulator` | observed-on-Flip |
| versionName | `1.4.1` | observed-on-Flip |
| versionCode | `200240` | observed-on-Flip |
| minSdk / targetSdk | 29 / 34 | observed-on-Flip |
| on-device apk | `/data/app/~~WKm2l2kyk2jliSNbctdkCg==/com.fly.simulator-Og6NtqQWMWgKROWPS21jsw==/base.apk` | observed-on-Flip |
| size | 169441947 bytes (~162 MiB) | observed-on-Flip |
| pulled into `docs/` | **no** (too large; path only) | |
| signing / appId | uid `10386` | observed-on-Flip |

Inspected on fold via `adb -s <device> shell pm path` / `dumpsys package`. extracted `AndroidManifest.xml` + `classes.dex` + `resources.arsc` + strings from `libil2cpp.so` / `libuavanalytics.so`. did **not** copy the 162 MiB apk into the repo.

## how the sim opens the accessory (from apk)

Unity player + **AppGuard** packer. `[source-implementation-only]`

Manifest (`aapt dump xmltree` on binary `AndroidManifest.xml`):

- `uses-feature android.hardware.usb.accessory` **required=true**
- `uses-feature android.hardware.usb.host` required=false
- `uses-permission android.permission.USB_PERMISSION`
- application `android:name="com.AppGuard.AppGuard.QLVRK"` (packer)
- activity `com.fly.simulator.SimulatorActivity` exported, launchMode singleTask
  - intent-filter: `MAIN`/`LAUNCHER` **and** `android.hardware.usb.action.USB_ACCESSORY_ATTACHED`
  - meta-data `unityplayer.UnityActivity` = true
  - meta-data `android.hardware.usb.action.USB_ACCESSORY_ATTACHED` → resource `@0x7f0f0000` = `xml/accessory_filter`

`[source-implementation-only]`

`resources.arsc` names `xml/accessory_filter` at `0x7f0f0000`. the compiled xml body was **not** recovered: AppGuard leaves a ResTable header whose inner chunks do not parse as a normal string pool. `[source-implementation-only]`

**Runtime filter that actually matched this RC** (S1 + this run `dumpsys usb`): manufacturer `DJI`, model `com.dji.logiclink`, version `v0.0.0.0`, default package `com.fly.simulator`. `[observed-on-Flip]`

`classes.dex` is packed. printable USB-related java names found: `Landroid/os/ParcelFileDescriptor;`, `ParcelFileDescriptor$AutoCloseOutputStream`, `Ljava/io/FileInputStream;`. **no** plaintext `UsbManager.openAccessory` / `getAccessoryList` / `requestPermission` / `logiclink` / RCLink / DUMPL in that dex. `[source-implementation-only]`

inferred open path (not a live java trace): AppGuard unpacks at runtime → Unity activity receives `USB_ACCESSORY_ATTACHED` → standard `UsbManager.openAccessory` → `ParcelFileDescriptor` streams. `[untested-hypothesis]` as bytecode; **consistent** with PFD/FIS strings + manifest + live default-handler. `[source-implementation-only]` for those pieces.

native:

- `libil2cpp.so` (~85 MiB) contains **libusb host** symbols (`usb_init`, `usb_claim_interface`, `usb_bulk_read`, `usb_bulk_write`) and the string `libusb0_dji.dll`. that is USB-**host**/PC-plugin shaped, not AOA. `[source-implementation-only]`
- no `openAccessory` / `logiclink` / `RCLink` / `DUMPL` strings in `libil2cpp.so` or `libuavanalytics.so` under those names. `[source-implementation-only]`
- `libuavanalytics.so` has `unable to keepalive` (analytics, not proven RC keepalive). `[source-implementation-only]`

init/keepalive **frames** (FreeFCC `55 CC` RCLink, bootstrap dest `0x1F`/`0x00`, 2.5s keepalive): **not found** in this apk's recoverable strings. do not assume this sim sends them. `[untested-hypothesis]`

## live RC + sim (read-only, no second client)

fold-local adb during capture: `127.0.0.1:41531` `device` `SM_F946B` (serial moved off `35131`; used the `device` row). `[observed-on-Flip]`

| observation | value | class |
|---|---|---|
| usb function | `ACCESSORY`, configured | observed-on-Flip |
| accessory | `DJI` / `com.dji.logiclink` / `v0.0.0.0` (serial redacted) | observed-on-Flip |
| accessory_description | `DJI RC-N2/3` | observed-on-Flip |
| default handler | `com.fly.simulator` uid 10386 | observed-on-Flip |
| sim pids | `31384` `31406` | observed-on-Flip |
| Fly | not this bead’s focus; do not assume concurrent fd | |
| this actor `openAccessory` | **not called** | |
| `/proc/<pid>/fd` usb nodes | shell cannot list another uid’s fds | observed-on-Flip |
| logcat `--pid` sim | firebase/unity init; **no** stick/DUML/RCLink lines in the USB/stick/connect filter | observed-on-Flip |

**Packet bytes / stick framing: not observed.** an independent app cannot read the accessory fd the sim already owns. S1/S0 already forbade a second `openAccessory`. logcat ≠ USB payload. `[source-implementation-only]` + `[observed-on-Flip]` for the negative.

what *was* observed of “init”: kernel/AOA `GETPROTOCOL` / `SENDSTRING` / `START` remains in `dumpsys usb` USB Event Log from the ACCESSORY session (same as S1). that is the **AOA handshake**, not DUML/stick frames. `[observed-on-Flip]`

## sim ≠ Flip O4

- this app is a **Unity simulator** talking to the RC as an Android accessory. `[source-implementation-only]`
- operator: physical sticks move the **sim**. that is RC→phone local, not RC→O4→Flip. `[untested-hypothesis]` if generalized; **not** claimed as Flip control here.
- no aircraft, no motors, no `0x03/0x8E` TX from this actor.

## residual

- accessory_filter XML body (packed arsc)
- actual `openAccessory` call site (AppGuard)
- stick report layout on this accessory stream (needs a later authorized exclusive client or in-process observe, **not** this bead)
- whether sim uses RCLink vs bare DUML vs something else
- Flip O4 (S2, parked)

## files / paths recorded

- on-device apk path above
- this document: `docs/s1-sim-packet-init.md`
- no 162 MiB apk in git
