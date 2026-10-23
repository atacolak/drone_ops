# S4-prep: FridaBox on-device attach

status: **control path down.** on-device agent **not run.** no hook bytes.
bead: `dji-flip-g0-g1-research-j7k.23`

store `dji.go.v5` not touched this bead. no magisk. no guessed TX. no datajar unpack.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold / this pc |
| `untested-hypothesis` | on-device agent vs RC usb |

## 1. recover fold-adb

`docs/FOLD-CONTROL.md`: ssh `fold` (10.77.206.52:8022) then `adb-wifi`.

| check | result |
|---|---|
| icmp 10.77.206.52 | **up** (rtt ~50–120 ms) `observed-on-Flip` |
| tcp 8022 / 22 | **connection refused** (repeated) `observed-on-Flip` |
| `ssh fold` | refused `observed-on-Flip` |

one in-flight `adb-wifi` from an earlier ssh session **did** print `reconnected cached 127.0.0.1:46331` / `SM_F946B device` (~110s), then the sshd socket went away. later `scripts/fold-adb` → `connection refused`. `observed-on-Flip`

not the rotating wireless-debug port. **termux sshd is not listening.** cannot reach adb, FridaBox UI, or logcat from sfub.

## 2. on-device attach — not started

did **not** use ssh `-L` as the primary path this bead.

did **not**: recycle guest, switch Computer → On-device, SAF-pick `/sdcard/Download/fly-observe.js`, `logcat -s fly-observe`, or run `attach_guest.py`.

observe script still on sfub `/tmp/fly-observe.js` (logcat via `android.util.Log` + `console.log`). not executed.

## 3. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | **not reached.** no on-device gadget run, no class list. leftover from j7k.22: virtual `dji.go.v5` under FridaBox uid **may** still be paused; we could not inspect. |
| **real usb** | **not attempted.** even a working on-device Fly is still FridaBox virtual `UsbManager`, **not** the RC-N3 fd. do not treat as Flip USB. `untested-hypothesis` until `openAccessory` logs manufacturer `DJI` / `com.dji.logiclink` on the real accessory. |

## 4. methods tried

- ping fold ipv4
- `nc` 8022 and 22
- `ssh fold` batch mode, several times
- no magisk, no store Fly `adb install`, no duml TX, no datajar unpack

## residual

needs **termux sshd on 8022** again (not an IP from the operator). then:

1. `scripts/fold-adb shell id`
2. FridaBox workspace → DJI Fly → **On-device** → select `fly-observe.js` → Launch
3. `logcat -s fly-observe` for class-load / `openAccessory` / `55 cc` writes
4. keep class-load and real-usb as two rows

`attach_guest.py` still wants local adb + `enumerate_processes` (that handshake **hung** in j7k.22). on-device/logcat is the front.

## files

`docs/s4-fly-observe-ondev.md`
