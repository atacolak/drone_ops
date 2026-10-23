# S4-prep: FridaBox on the Fold

status: **fridabox installed + fly imported. computer-mode attach hung. no hook bytes.**
bead: `dji-flip-g0-g1-research-j7k.22`

store `dji.go.v5` **not** replaced. no magisk. no guessed TX.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold |
| `source-implementation-only` | FridaBox docs |
| `untested-hypothesis` | virtual UsbManager vs RC |

## 1. install (done)

| | |
|---|---|
| apk | `FridaBox-v4.2.0-arm64-v8a.apk` sha256 `e37af8801a3bb12c04db2140c09dfb843f8e105c04aa0c0f88f5731fa3e0cabb` (matches upstream table) |
| package | `com.qm4rs.fridabox` 4.2.0 `versionCode=403` |
| path | `/data/app/~~zfa2w_YGw5NpcOD8cKuYng==/com.qm4rs.fridabox-1a3Dn_niE6kbixc6QrtrNw==/base.apk` |
| store Fly | **same** `/data/app/~~FGwrDP5qYKYtQB5rjDQfmA==/dji.go.v5-vKs1hua_ffY_Q5moCVOO6g==/base.apk` |

`adb install` was **only** FridaBox. `observed-on-Flip`

## 2. import (done)

Workspace → Import from apps → **DJI Fly** `dji.go.v5` 1.21.16.

snackbar: `DJI Fly imported from installed apps`. `observed-on-Flip`

that clones into FridaBox **virtual PM**. not `adb install -r dji.go.v5`.

## 3. gadget (done)

Gadgets → 17.24.3 arm64 → **Active**. host pipx `frida-tools` / `frida.__version__` **17.24.3** (match). `observed-on-Flip`

## 4. computer-mode launch (partial)

guest launch panel showed Computer selected. confirm dialog → LAUNCH.

processes **after** launch, all uid `u0_a388` (FridaBox), **not** store Fly’s old `u0_a382`:

```
com.qm4rs.fridabox
com.qm4rs.fridabox:black
dji.go.v5          (virtual guest)
```

`observed-on-Flip`

gadget listen `127.0.0.1:27042` on device. `adb forward tcp:27042 tcp:27042` on fold. ssh `-L 27043:127.0.0.1:27042 fold`.

`frida.get_device_manager().add_remote_device('127.0.0.1:27043')` **returns**. `enumerate_processes()` / `attach('Gadget')` **hang** (20s). CLI `frida-ps -H` same. `observed-on-Flip`

no `Java.perform`. **no bytes.**

## 5. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | virtual `dji.go.v5` **process exists**, Computer mode **pauses before `Application.onCreate()`**. we never got a class list / joystick load. **not proven** AppGuard decrypted midware. |
| **real usb** | **not this guest.** virtual `UsbManager` is not the RC-N3 fd. we did not `openAccessory` on the real accessory. usb `current_functions=ACCESSORY` was already up from earlier work; this guest was paused. **do not treat as Flip USB TX/RX.** `untested-hypothesis` that a resumed virtual Fly could even see AOA. |

hooks prepared on sfub `/tmp/fly-observe.js` (also `/sdcard/Download/fly-observe.js`): `UsbManager.openAccessory`, `FileOutputStream.write` (RCLink `55 cc` only logged), `JNIUsbAccessory` class scan, `DataFlycJoystick` / `SendVirtualStickFlightControlData` names. **not executed.**

## 6. on-device mode

not reached. after the hung attach, `fold-adb` dumps started timing out. no SAF pick of the js.

## 7. methods tried

- cover-screen rotation 3 ate taps; `wm user-rotation lock 0` fixed import UI
- import-from-apps clone (not file of the 744 MiB apk)
- computer attach via adb-forward + ssh `-L` + frida 17.24.3 python API
- did **not** magisk, did **not** overwrite store Fly, did **not** send duml

## residual

1. recycle virtual guest, **on-device** agent (logcat), no host handshake.
2. or FridaBox `tools/attach_guest.py` against fold’s adb (forwards 27042–27073, sets guest ClassLoader).
3. even a working class-load is still **not** RC usb until a separate row says `openAccessory` hit manufacturer `DJI` / `com.dji.logiclink` on the real fd.

## files

- `docs/s4-fly-observe-fold.md`
- FridaBox apk `/tmp` / fold home — **not git**
- `/tmp/fly-observe.js` — observe-only; not a TX script
