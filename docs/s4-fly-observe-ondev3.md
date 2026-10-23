# S4-prep: FridaBox on-device after operator js pick

status: **agent still none.** did not launch. no hook bytes.
bead: `dji-flip-g0-g1-research-j7k.25`

store Fly path unchanged. no magisk. no TX. **no Open-with spam.**

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold |
| `untested-hypothesis` | virtual usb vs RC |

## 1. control

`scripts/fold-adb shell id` → `uid=2000(shell)`. `observed-on-Flip`

store `dji.go.v5` still `/data/app/~~FGwr…/base.apk`. FridaBox 4.2.0 still installed.

## 2. panel

launch panel dumped (cover display 0, FridaBox focused there):

- mode: **On-device**
- agent: **No JavaScript agent selected**

`am start --display 1` → `SecurityException` (shell uid 2000 cannot choose inner). inner `mCurrentFocus=null`. operator inner-screen pick **cannot hit this activity until the fold puts FridaBox on display 1**. `observed-on-Flip`

did **not** tap Select JavaScript / CHOOSE FILE this bead.

did **not** Launch (would run without our agent).

## 3. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | **not reached** — no on-device agent, no `logcat -s fly-observe` |
| **real usb** | **not attempted** — virtual UsbManager still ≠ RC-N3 even after a later successful Launch |

## 4. methods tried

- fold-adb id, dump launch panel, display-1 start (denied)
- **not** SAF, **not** Open-with, **not** ssh `-L`
- no magisk, no store Fly overwrite, no duml TX

## residual

operator: open fold so FridaBox is on the **inner** screen, CHOOSE FILE `fly-observe.js` until the panel names the agent, then this actor Launches on-device and logcats.

## files

`docs/s4-fly-observe-ondev3.md`
