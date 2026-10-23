# S4-prep: frida gadget on a Fly COPY

status: **wrap installed as `dji.go.zz`. died before class-load. no hook bytes.**
bead: `dji-flip-g0-g1-research-j7k.27`

**never** `adb install -r dji.go.v5`. store Fly path unchanged. no magisk. no FridaBox relaunch. no TX.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold |
| `source-implementation-only` | frida-apk / wrap.sh |

## 1. control

`scripts/fold-adb shell id` → `uid=2000(shell)`. `observed-on-Flip`

store Fly **still**:
`/data/app/~~FGwrDP5qYKYtQB5rjDQfmA==/dji.go.v5-vKs1hua_ffY_Q5moCVOO6g==/base.apk` 1.21.16

wrap **also**:
`/data/app/~~SPko52T19xpgUKGpo8D_VQ==/dji.go.zz-dBSZaXni6_Z1bdXt75cvKA==/base.apk`

two packages. store not replaced. `observed-on-Flip`

## 2. wrap (copy only)

source: fold `dji.go.v5.apk` 743994244 B → `/tmp/dji.go.v5.full.apk`

`frida-apk` 17.24.3:

- `android:debuggable` on application
- `lib/arm64-v8a/wrap.sh` (`LD_PRELOAD=libfridagadget.so`)
- `libfridagadget.so` + `libfridagadget.config.so` (`type=script`, `path=libflyobserve.js.so`)
- observe js as `libflyobserve.js.so`

package id: UTF-16LE `dji.go.v5` → `dji.go.zz` in `AndroidManifest.xml` (29) + `resources.arsc`; utf-8 once in stub `classes.dex`. **did not** rewrite `libdatajar.so`. debug-signed v2/v3.

`aapt`: `package: name='dji.go.zz'`. `adb install` **without** `-r`, **not** `dji.go.v5`. Success. `observed-on-Flip`

## 3. exact death

`am start -n dji.go.zz/com.dji.component.application.activity.DJIPureLaunchActivity`

`wrap.sh` **did** run, then:

```
ClassNotFoundException: Didn't find class "com.AppGuard.AppGuard.RGTYT"
Failure to verify dex file '.../dji.go.zz-.../base.apk': Bad checksum (063d3e6a, expected d8f43e21)
wrap.sh : Killed
wrap.sh terminated by exit(137)
Process dji.go.zz (pid 13307) has died
```

`observed-on-Flip`

dex checksum broke because the stub `classes.dex` was rewritten for the package id. AppGuard’s Application (`RGTYT`) never instantiated. **not** a live AppGuard unpack then SEGV (that was FridaBox). this copy **dies at dex verify**, before midware.

no crash loop (one shot). pid gone after ~8s. no `fly-observe` logcat. no `openAccessory`. no `55 cc`.

## 4. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | **fail.** stub dex rejected. AppGuard Application class missing. joystick / JNIUsbAccessory **not** loaded. |
| **real usb** | **not attempted.** wrap never got a running process with an accessory fd. not RC-N3. |

## 5. methods tried

- frida-apk + same-length package rename + debug sign + `adb install` of **`dji.go.zz` only**
- **not** `install -r dji.go.v5`, **not** magisk, **not** FridaBox, **not** datajar unpack as the job

## residual

a wrap that **does not touch** `classes.dex` bytes (manifest-only rename, or no rename + refuse same-id install) might get past dex verify and then hit AppGuard for real. still not Flip USB until `openAccessory` on manufacturer `DJI` / `com.dji.logiclink`.

apk copies **not git** (`/tmp/dji.go.zz.gadget.apk` ~783 MiB).

## files

`docs/s4-fly-gadget-copy.md`
