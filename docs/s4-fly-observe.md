# S4-prep: Fly observe attach boundary

status: **did not attach.** class-load not reached. real usb not reached.
bead: `dji-flip-g0-g1-research-j7k.21`

refine of `j7k.18`, not a twin. **store `dji.go.v5` on the fold was not replaced.** no magisk. no guessed TX.

usb owns the real accessory this bead (`current_functions=ACCESSORY`, `sh.colak.dji.aoaread` pid 3888). we did not steal it. `observed-on-Flip`

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold session |
| `source-implementation-only` | FridaBox README / Fly apk facts |
| `untested-hypothesis` | what would happen if we had a spare ARM64 host |

## 1. what “FridaBox first” is

[QM4RS/FridaBox](https://github.com/QM4RS/FridaBox) 4.0.0 — **not** “install frida-server on an AVD.”

- import **original** APK byte-for-byte into a **private virtual PM** (`com.qm4rs.fridabox`)
- no root, no magisk, no patch/repack/resign of the guest
- gadget loads in the **virtual process** before guest `Application.onCreate()`
- **ARM64** guests only; split/x86 rejected
- host `frida -U gadget` in Computer mode

that is the unmodified-apk instrumented workspace the report meant. `source-implementation-only`

## 2. exact attach boundary (this host / this fold)

| check | result |
|---|---|
| `com.qm4rs.fridabox` on fold | **not installed** (`pm path` empty) `observed-on-Flip` |
| store `dji.go.v5` | still the same `/data/app/~~FGwr…/base.apk` `observed-on-Flip` |
| host `frida` / `frida-tools` | **absent** (same as j7k.18) |
| host AVD / system-images | **absent** (`~/.local/opt/android-sdk` has platforms/ndk/build-tools, **no** `emulator/`, **no** `system-images/`) |
| qemu x86 + Fly arm64 | FridaBox would **reject** the guest (`source-implementation-only` ABI gate). not attempted |
| spare ARM64 phone | **none** used (sfpix is the wrong plane; fold usb is busy) |
| fold Fly process | pid 28419 **seen**, **not** spawned/killed by this bead |

**attach: no.** no `openAccessory` hook, no write hook, no `JNIUsbAccessory`, no joystick class-load, **no bytes.**

## 3. class-load vs real usb — **separated** (not collapsed)

these are two experiments. neither ran. do not read “didn’t attach” as “usb failed.”

| plane | what it would prove | this bead |
|---|---|---|
| **class-load** | guest `PathClassLoader` can see `UsbManager.openAccessory`, `JNIUsbAccessory`, `DataFlycJoystick`, `SendVirtualStickFlightControlData` after AppGuard `makeApplication` | **not reached** — no FridaBox process, no gadget |
| **real usb** | those calls hit the **RC-N3** accessory and we dump outbound RCLink | **not attempted** — usb actor holds the real fd; a FridaBox *virtual* `UsbManager` is **not** that fd anyway |

even a future successful FridaBox class-load on fold/emulator is **not** Flip USB. virtual Binder `UsbManager` / no AOA host = fake accessory. say so then too. `untested-hypothesis` until measured.

AppGuard in a BlackBox-style virtualizer may still refuse to unpack. that is a **class-load** fail, still not a usb fail. `untested-hypothesis`

## 4. gadget-on-a-copy fallback — **not built**

would unpack, inject `frida-gadget.so`, resign, sideload **other** package. AppGuard `XNXRT` is on the live manifest (j7k.18). predicted kill. `source-implementation-only`

not built because:

1. no second ARM64 install target
2. sideload **same** id `dji.go.v5` = replace store Fly — **forbidden**
3. 744 MiB + AppGuard wrap is the fight 18 already declined

did **not** magisk. did **not** `adb install` over store Fly.

## 5. hooks we would run **if** class-load attach existed

observe only, no send:

- `android.hardware.usb.UsbManager.openAccessory`
- accessory `FileOutputStream.write` / `UsbAccessoryService.sendmessage`
- `uav.jni.JNIUsbAccessory` (`native_OnUsbConnected` and send)
- class load: `uav.midware.data.model.P3.DataFlycJoystick`, `SendVirtualStickFlightControlData`

dump timestamped direction-tagged **bytes**. no guessed TX to the real RC.

not run.

## residual (not this bead)

needs a **spare ARM64** (or the fold **after** usb is done with the accessory): install FridaBox, SAF-import the **already pulled** `dji.go.v5` apk (not uninstall store), Computer mode, hooks above.

class-load result and usb result stay two rows.

## files

`docs/s4-fly-observe.md`
