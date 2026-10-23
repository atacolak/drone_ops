# S4-prep: live FridaBox guest (operator launched)

status: **crash loop. SIGSEGV. no hook bytes.** FridaBox force-stopped. store Fly stays.
bead: `dji-flip-g0-g1-research-j7k.26`

no magisk. no TX.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold |
| `untested-hypothesis` | virtual usb vs RC |

## 1. control

`scripts/fold-adb shell id` → `uid=2000(shell)`. `observed-on-Flip`

store `dji.go.v5` path **unchanged** after: `/data/app/~~FGwrDP5qYKYtQB5rjDQfmA==/dji.go.v5-vKs1hua_ffY_Q5moCVOO6g==/base.apk`

## 2. what was running

uid **`u0_a388` = FridaBox**, not store Fly (`u0_a382` historically):

```
com.qm4rs.fridabox
com.qm4rs.fridabox:black
dji.go.v5          (virtual guest, several pids)
```

focus: `com.qm4rs.fridabox/top.niunaijun.blackbox.proxy.ProxyActivity$P0` on **cover display 0**. inner focus null. `observed-on-Flip`

that proxy is the **black screen**. guest never kept a surface.

## 3. why black

**crash loop ~1 Hz.** `BProcessManager: App Died: dji.go.v5` then `initProcess` again.

```
Fatal signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x79c
tid … (dji.go.v5)
Process com.qm4rs.fridabox:p0 has died
```

`observed-on-Flip`

before each death, FridaBox **did** get past Application create:

- `openDexFileNative` on store Fly `base.apk`
- `libAppGuard.so` load **ok**
- `caller=AnoymousDex` (packer unpacked *something*)
- `beforeCreateApplication` / `beforeApplicationOnCreate` `package=dji.go.v5`

then native SEGV. AppGuard vs BlackBox virtualizer, not a missing activity. `observed-on-Flip` for the sequence; **why** 0x79c is `untested-hypothesis`.

## 4. agent / gadget / fly-observe

`logcat -s fly-observe` → **empty** (no `[fly-observe]` / `Log.i`). `observed-on-Flip`

so either the on-device agent was **not** actually imported, or it never reached `Java.perform` before SEGV. **no** `openAccessory` logs, **no** `55 cc` writes, **no** `DataFlycJoystick` / `JNIUsbAccessory` class-load lines.

## 5. class-load vs real usb — **separated**

| plane | this bead |
|---|---|
| **class-load** | packer path only: AppGuard + AnonymousDex + `onCreate` **entry**. midware names **not** observed. fly-observe **silent**. |
| **real usb** | **not attempted.** virtual guest ≠ RC-N3 fd. no `UsbManager.openAccessory` log. do not treat as Flip USB. |

## 6. crash loop stop

bead: do not kill unless crash loop. this **was** a crash loop.

`am force-stop com.qm4rs.fridabox` only. store Fly **not** force-stopped (no store pid). after: no fridabox/fly pids; store apk path still there. `observed-on-Flip`

## residual

FridaBox+AppGuard on this fold SEGV’s in the virtual process. product 2 (observe Fly bytes) still needs a host that **stays alive** after `onCreate` — not this crash loop, not cover-only SAF.

## files

`docs/s4-fly-observe-live.md`
