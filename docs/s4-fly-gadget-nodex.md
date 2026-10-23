# S4-prep: gadget wrap, `classes.dex` not touched

status: **wrap built. dex hash matches original. fold unreachable before install.**
bead: `dji-flip-g0-g1-research-j7k.28`

never `adb install -r dji.go.v5`. no magisk. no FridaBox. no TX. no utf-8 inside `classes.dex`.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold / this pc |
| `untested-hypothesis` | AppGuard after a clean dex wrap |

## 1. control

`scripts/fold-adb shell id` → `uid=2000(shell)`. `pm uninstall dji.go.zz` **Success**. store Fly still:

`/data/app/~~FGwrDP5qYKYtQB5rjDQfmA==/dji.go.v5-vKs1hua_ffY_Q5moCVOO6g==/base.apk`

`observed-on-Flip`

later: icmp 10.77.206.52 **destination host unreachable**; ssh 8022 **no route**. install/launch **not run**. `observed-on-Flip`

## 2. wrap (dex intact)

from j7k.27 `frida-apk` output `/tmp/dji.go.v5.debuggable.apk` (gadget + `wrap.sh` only; **same** `classes.dex` as stock).

stock / frida-apk / nodex `classes.dex` sha256:

`de53d434b5166ef5c2df9853e1cbed9c260a2b1fef5f1bfc8b492c1056443fff`

**match.** crc `0x63367250`. `observed-on-Flip` (local hashes)

then **only** `AndroidManifest.xml` UTF-16LE `dji.go.v5` → `dji.go.zz` (29 hits). **not** `classes.dex`, **not** `resources.arsc`, **not** `libdatajar.so`.

`aapt`: `package: name='dji.go.zz'`. debug-signed v2. `/tmp/dji.go.zz.nodex.apk` ~783 MiB. **not git.**

native bits: `libfridagadget.so`, `wrap.sh`, `libflyobserve.js.so`. `source-implementation-only`

## 3. install / death / bytes

**not reached.** scp to fold timed out (`Timeout, server 10.77.206.52 not responding`) then netbird/ipv4 **no route**.

no `adb install` of this nodex apk. no launch. no logcat. no AppGuard. no `fly-observe`.

## 4. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | **not reached** (wrap never started on device) |
| **real usb** | **not attempted** |

j7k.27 death (dex **Bad checksum**) does **not** apply to this file. whether AppGuard kills a **checksum-valid** wrap is `untested-hypothesis`.

## 5. methods tried

- uninstall previous broken `dji.go.zz`
- manifest-only rename; dex hash checked
- **not** `install -r dji.go.v5`
- scp then fold went dark

## residual

when `ssh fold` works: `adb install` **`dji.go.zz.nodex.apk` only**, launch, logcat. keep the two rows.

## files

`docs/s4-fly-gadget-nodex.md`
