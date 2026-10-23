# S4-prep: dump zz openAccessory / 55 cc

status: **no 55 cc bytes.** gadget `libc.write` hooked then **SIGILL** in `gum-js-loop`. Java never came up.
bead: `dji-flip-g0-g1-research-j7k.30`

never `-r dji.go.v5`. no magisk. no FridaBox. **no stick TX.**

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold |
| `untested-hypothesis` | 55 cc on the real fd |

## 1. prior grant (operator)

operator finished ToS + **Allow** usb for `dji.go.zz` (real `DJI` / `com.dji.logiclink`). that session was already **dead** when 30 started (`pidof` empty). wrap.sh had `TypeError hexdump` then `exit(137)`. **permission ≠ fd, ≠ byte log.** `observed-on-Flip`

could **not** hot-swap js (script is `libflyobserve.js.so` in the apk).

## 2. rebuilds (dex stock)

`classes.dex` sha256 stayed

`de53d434b5166ef5c2df9853e1cbed9c260a2b1fef5f1bfc8b492c1056443fff`

| apk | change |
|---|---|
| nodex2 | `hexdump` → `toHex` |
| nodex3 | delayed `Java` + early `Interceptor` |
| nodex4b | `wrap.sh` **`exec "$@"`** so LD_PRELOAD is the app; `Process.getModuleByName('libc.so').getExportByName('write')` |

reinstall **`dji.go.zz` only** (`install -r` of the wrap package, **not** v5). store Fly path unchanged. `observed-on-Flip`

## 3. nodex2 launch (hexdump gone)

```
ReferenceError: 'Java' is not defined   /libflyobserve.js:109
```

AppGuard still loaded. wrap.sh **Killed**. no `fly-observe` hooks. no `USB_ACCESSORY_ATTACHED` this launch. `observed-on-Flip`

## 4. nodex4b launch (exec wrap)

```
[fly-observe] hooked libc.write
[fly-observe] boot
[fly-observe] Java not ready, retry
…
Fatal signal 4 (SIGILL) ILL_ILLOPC tid gum-js-loop pid dji.go.zz
```

**no** `write fd= … 55 cc`. **no** `openAccessory`. Java never `available` before SIGILL (~2s). `observed-on-Flip`

gadget js engine died in-process. leftover `dji.go.zz` pid 27679; store Fly not running.

## 5. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | **fail.** Java never ready in gadget. no joystick/gimbal/camera class list. |
| **real usb** | earlier **permission** on logiclink RC (j7k.29 / operator Allow). **this** bead: no `openAccessory` log, no `55 cc` dump. permission ≠ fd ≠ bytes. |

## 6. USB Allow

if a new Allow dialog appears after reinstall, operator tap on **inner**. this 4b launch died before that log line.

## residual

LD_PRELOAD gadget + AppGuard on this binary **SIGILL**s the js loop. next observe path is not more wrap.sh. do not guess sticks.

## files

`docs/s4-fly-observe-bytes.md`
