# S4-prep: FridaBox on-device (sshd up)

status: **on-device UI reached. agent not imported. no hook bytes.**
bead: `dji-flip-g0-g1-research-j7k.24`

store `dji.go.v5` path **unchanged**. no magisk. no guessed TX. no ssh `-L`.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold session |
| `source-implementation-only` | FridaBox 4.2.0 source |
| `untested-hypothesis` | virtual usb vs RC |

## 1. control path

`scripts/fold-adb shell id` → `uid=2000(shell)` `u:r:shell:s0`. `observed-on-Flip`

## 2. what was already there (j7k.22)

| | |
|---|---|
| FridaBox | `com.qm4rs.fridabox` 4.2.0 still installed |
| store Fly | `/data/app/~~FGwr…/dji.go.v5-…/base.apk` |
| virtual guest | DJI Fly 1.21.16 in FridaBox workspace |
| gadget | 17.24.3 was Active last bead |
| script | `/sdcard/Download/fly-observe.js` pushed (logcat `Log.i`) |

## 3. on-device this bead

did:

1. `am start` FridaBox (task already front)
2. tap DJI Fly → launch panel **On-device** already selected
3. Select JavaScript → CHOOSE FILE (trust dialog)
4. SAF DocumentsUI → Downloads → list view → `fly-observe.js` (`JS document`, 3.15 kB)

did **not** get `importAgent`:

- after tapping the file, panel still says **No JavaScript agent selected**
- FridaBox `scriptPicker` (`OpenDocument`) only calls `importAgent` if `uri != null && pendingScriptPackage != null`; otherwise **silent** (`FridaBoxActivity.kt`)
- one sequence opened android **Open with** (ChatGPT / Termux) — that is `ACTION_VIEW`, not a picker result. dismissed with BACK

so the observe script never copied into FridaBox private storage. **Launch was not pressed** (would have been gadget-without-agent / not our hooks).

no `logcat -s fly-observe` hits worth claiming — agent never loaded. `observed-on-Flip`

## 4. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | **not reached.** no on-device gadget+agent, no `Java.perform`, no `DataFlycJoystick` / `JNIUsbAccessory` list |
| **real usb** | **not attempted.** even a working virtual Fly is FridaBox `UsbManager`, **not** the RC-N3 accessory. `current_functions=ACCESSORY` was already set; we did not `openAccessory` as store Fly or as the guest. virtual usb ≠ RC. `untested-hypothesis` until logs show manufacturer `DJI` / `com.dji.logiclink` on the **real** fd |

## 5. methods tried

- fold-adb id / dumpsys / SAF taps (cover, `wm user-rotation lock 0`)
- **not** ssh `-L`, **not** `frida -H`
- no magisk, no `adb install` over store Fly, no duml TX

## residual

SAF must return a `.js` uri into `importAgent` (or operator taps CHOOSE FILE on the **inner** screen). then Launch on-device, `logcat -s fly-observe`. keep the two rows.

## files

- `docs/s4-fly-observe-ondev2.md`
- `/sdcard/Download/fly-observe.js` on fold — not git
