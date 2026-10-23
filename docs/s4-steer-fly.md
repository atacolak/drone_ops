# steer DJI Fly in-process (no magisk)

Status: **no stick API reached.** no motors. no indoor flight.
Bead: `dji-flip-g0-g1-research-j7k.18`.
package: `dji.go.v5` on Fold. proto’s on-fold `fly-ext` / datajar **not deleted**.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold |
| `untested-hypothesis` | gadget wrap of a copy |

## 1. exported surfaces (first)

`dumpsys package dji.go.v5` resolver: **no** exported activity/service/action whose name looks like virtual-stick / gimbal / flight-mode. `[observed-on-Flip]`

closest, tried once each (not spam):

| try | result |
|---|---|
| `dji.go.v5.intent.action.NAVIGATE` → `DJIPureLaunchActivity` | started; landed `DJIDeviceActivity` |
| `VIEW djifly://flight` | started; still device page, not FPV stick API |
| `run-as dji.go.v5` | `package not debuggable` |

USB_ACCESSORY_ATTACHED is how Fly **takes the fd**, not a stick binder. `[observed-on-Flip]`

`com.AppGuard.AppGuard.XNXRT` is a provider on the live package. `[observed-on-Flip]`

**no in-process stick/gimbal attach here.** did not invoke a stick API, so **no center/neutral TX.**

## 2. frida gadget / wrap of a copy

host has **no** `frida` / `frida-ps` on PATH. `[observed-on-Flip]`

Fly is **not debuggable** → `frida -U -n` / spawn without gadget or root **will not attach**. `[observed-on-Flip]` as run-as; attach itself not run.

gadget wrap of a **copy** (sideload other package): would unpack, inject `frida-gadget.so`, resign. AppGuard is already in the live manifest (`XNXRT`). **expect the wrap to die or refuse USB.** `[untested-hypothesis]` as a completed wrap — **not performed** this bead (no frida-gadget on host; would fight a 100MB+ apk next to proto’s copy). AppGuard is the predicted kill. `[source-implementation-only]` from package dump.

did **not** magisk. did **not** overwrite `dji.go.v5` store install.

## 3. UI tap

not used as the path.

## not claimed

we can fly, we can inject sticks through Fly, O4 from this bead.

## files

`docs/s4-steer-fly.md`
