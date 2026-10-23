# sidecar store DJI Fly (software tells Fly)

Status: **Fly owns USB.** we poke Fly. not inject. not motors.
Bead: `dji-flip-g0-g1-research-j7k.32`.
store package: **`dji.go.v5`**. **`dji.go.zz` force-stopped** (was default accessory preference).

## claim tags

all live facts here: `observed-on-Flip` unless noted.

## usb / who owns fd

- accessory still `DJI` / `com.dji.logiclink` / `v0.0.0.0`
- `dji.go.zz` pid empty after `am force-stop`
- `dji.go.v5` pids live; **FpvComponentActivity** resumed
- default accessory preference in settings still lists `dji.go.zz` (OS default). runtime zz not running so it cannot steal the fd this session.
- did **not** `openAccessory` from aoa-read. Fly keeps the session.

`usb_charging=true` / `source_power=false` this run (cable power role differed from earlier accessory-host captures). still ACCESSORY function.

## software-originated non-stick

uiautomator dump (inner FPV, width ~2231): **no** `virtual` / `stick` / `joystick` chrome.

nodes: FPV Back, flight status, OSD, RC signal, **Takeoff And Land** (not tapped), **Camera Operate Layout**, Camera Mode Switch, HyperLapse, map, height/speed.

**poked Fly:** `input tap` on **Camera Operate Layout** (2136, 451). `[observed-on-Flip]`

**Fly wrote usb:** not observed (would require sniffing Fly’s exclusive fd). `[untested-hypothesis]`

**aircraft moved:** **no.** no takeoff tap, no 8E, no motors. `[observed-on-Flip]` as “we did not command motion”; physical motion not instrumented.

QuickShot **skipped** — unsure it cannot spin motors indoors.

## virtual-stick chrome

**not present** in this FPV a11y tree while physical sticks idle. `[observed-on-Flip]`

## not used

wrap.sh, magisk, `-r` overwrite of Fly, aoa-read TX, Mini 4 Pro as Flip.

## files

`docs/s4-fly-sidecar.md`
