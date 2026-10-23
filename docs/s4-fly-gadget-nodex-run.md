# S4-prep: checksum-valid gadget copy launched

status: **alive (ToS). AppGuard did not kill. observe script failed. no 55 cc bytes.**
bead: `dji-flip-g0-g1-research-j7k.29`

never `adb install -r dji.go.v5`. no magisk. no FridaBox. **no stick TX.**

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + RC accessory present |
| `untested-hypothesis` | fd ownership / 55 cc |

## 1. install

`scripts/fold-adb shell id` ok.

installed **`dji.go.zz.nodex.apk` only** (`adb install`, not `-r dji.go.v5`).

| | |
|---|---|
| store Fly | `/data/app/~~FGwr…/dji.go.v5-…/base.apk` still |
| wrap | `/data/app/~~vK38sYzpleTSSCN9EDvx-g==/dji.go.zz-15cAu78CeQBpCRyD-Q1O_w==/base.apk` |

`observed-on-Flip`

## 2. launch — **not** the j7k.27 death

`am start -n dji.go.zz/…DJIPureLaunchActivity`

process **stayed up**: `dji.go.zz` pid 20631 / 20652 uid `u0_a389`. store Fly **not** running.

AppGuard **loaded**:

```
com.AppGuard.AppGuard.GXNGN.load(...)
Load …/libAppGuard.so … ok
```

UI: `DJIPrivacyFlowTermsActivity` (then `DJILauncherActivity`). **not** `ClassNotFound RGTYT`, **not** dex `Bad checksum`, **not** SIGSEGV loop. `observed-on-Flip`

## 3. gadget / observe script

`wrap.sh` ran. gadget tried `libflyobserve.js.so` and **died in the script**:

```
TypeError: cannot define variable 'hexdump'
```

frida already binds `hexdump`. our function name collided. **no** `[fly-observe]` logs. hooks never installed. `observed-on-Flip`

wrap.sh then `Initialize end` — process continued without our agent.

## 4. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | AppGuard Application **yes**. midware names (`DataFlycJoystick`, `JNIUsbAccessory`) **not** logged (agent failed). privacy/launcher activities **yes**. |
| **real usb** | wrap received `USB_ACCESSORY_ATTACHED` and `UsbUserPermissionManager.requestPermission` for **`UsbAccessory[mManufacturer=DJI, mModel=com.dji.logiclink, mDescription=DJI RC-N2/3, mVersion=v0.0.0.0]`** package `dji.go.zz`. that is the **real RC**, not a fake. **not** proven we got the fd. **no** outbound `55 cc` dump. `untested-hypothesis` for bytes. |

no camera/gimbal cmd ids. no sticks.

## 5. methods tried

- nodex install + launch + logcat
- **not** overwrite store Fly, **not** magisk, **not** FridaBox, **not** guessed `0x03/0x8E`

left `dji.go.zz` running on ToS (not a crash loop).

## residual

rename `hexdump` in the observe js, rebuild **nodex** wrap (still no dex patch), reinstall **`dji.go.zz` only**. then log `openAccessory` / `55 cc`. keep the two rows.

## files

`docs/s4-fly-gadget-nodex-run.md`
