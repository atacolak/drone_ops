# S1 TOP-port USB enum (Flip OFF, read-only)

Status: **S1 capture**. Procedure: `docs/s0-usb-protocol-inventory.md` §6 only.
Bead: `dji-flip-g0-g1-research-j7k.3`.
Not S2. Not accessory open. Not payload. Not close.

Control path: `ssh fold` then fold-local `adb` to `127.0.0.1:44377` (SM_F946B). Not sfub `adb connect`. Not phonectl.

Captures: `docs/s1-captures/` (serials redacted).

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + this RC-N3 run (identity logs only) |
| `observed-other-model` | not used for new hardware claims here |
| `source-implementation-only` | matches S0 source |
| `untested-hypothesis` | not settled by this run |

## §6.6 verdict: **PASS**

| check | result |
|---|---|
| Flip remained OFF | operator-scheduled S1 requires OFF. at capture, Fly task was **not visible**; last activity `DJIDeviceActivity`, not live `FpvComponentActivity`. no optical aircraft check from this actor. `[observed-on-Flip]` for activity state; Flip power itself is **operator attestation** for this bead |
| local/wireless adb survived while RC occupied USB | `127.0.0.1:44377 device` **while** `current_functions=ACCESSORY` `[observed-on-Flip]` |
| TOP cradle recorded | AOA accessory role (`ACCESSORY`, RC host / phone accessory, `usb_charging=false`, `source_power=true`). physical TOP is operator/work-order; role matches S0 TOP not bottom CDC `[observed-on-Flip]` + `[source-implementation-only]` |
| `dumpsys usb` captured | `docs/s1-captures/05-dumpsys-usb-redacted.txt` |
| manufacturer/model/version | **DJI** / **com.dji.logiclink** / **v0.0.0.0** `[observed-on-Flip]` |
| no openAccessory / DUML / sim-enable / candidate TX / RF from this actor | none performed |
| serials redacted | `serial=[REDACTED]` in capture |

Not FAIL: not wired-sfub adb; no writes; did not treat bottom CDC as AOA; did not claim logcat payload.

Not INCONCLUSIVE: identity **did** appear in dumpsys even with Fly + `com.fly.simulator` running.

Bottom-port contrast: **not run** (would need unplug/replug; no operator wait). `[untested-hypothesis]` that bottom on this Fold is non-AOA.

## identity (unknown A, this firmware)

From `dumpsys usb` `current_accessory` `[observed-on-Flip]`:

| field | value |
|---|---|
| manufacturer | `DJI` |
| model | `com.dji.logiclink` |
| description | `DJI` |
| version | `v0.0.0.0` |
| uri | `www.dji.com` |
| serial | `[REDACTED]` |
| permissions accessory_description | `DJI RC-N2/3` |
| current_functions | `ACCESSORY` |
| kernel_state | `CONFIGURED` |
| host_connected | false (phone is not USB host) |
| usb_charging | false |

AOA handshake visible in UsbDeviceManager UEVENT log: `GETPROTOCOL` → multiple `SENDSTRING` → `START` → disconnect/re-enumerate `CONNECTED`/`CONFIGURED` → `USB_ACCESSORY_HANDSHAKE` / `USB_ACCESSORY_ATTACHED`. `[observed-on-Flip]` System logs ≠ payload. `[source-implementation-only]`

This model string is in FreeFCC-USB `accessory_filter.xml` and DJI-Link Fly filter notes. `[source-implementation-only]` **That does not prove Flip command/video/telemetry.**

## host

| field | value | class |
|---|---|---|
| utc | 2026-10-23T00:38:31Z | observed-on-Flip |
| phone | SM-F946B (Galaxy Z Fold 5) | observed-on-Flip |
| android | 16 / sdk 36 | observed-on-Flip |
| build | BP4A.251205.006.F946BXXS7GZI1 | observed-on-Flip |
| adb | fold-local `127.0.0.1:44377` `device` transport_id 42 | observed-on-Flip |
| RC | RC-N3, work-order TOP cradle | operator + accessory_description `DJI RC-N2/3` observed-on-Flip |

## who showed up besides dumpsys

| process | pid | note | class |
|---|---|---|---|
| `dji.go.v5` | 26862 | running; task not visible; last `DJIDeviceActivity`; history includes `FpvComponentActivity` (not current) | observed-on-Flip |
| `dji.go.v4` | none | | observed-on-Flip |
| `com.fly.simulator` | 31384, 31406 | default `USB_ACCESSORY_ATTACHED` handler for this accessory filter; uid 10386 holds accessory permission `DJI RC-N2/3` | observed-on-Flip |

`UsbProfileGroupSettingsManager` accessory preference: manufacturer `DJI` model `com.dji.logiclink` version `v0.0.0.0` → package `com.fly.simulator`. `[observed-on-Flip]`

This actor did **not** call `openAccessory`. whether simulator or Fly currently holds the fd is not proven by dumpsys identity alone. `[untested-hypothesis]` concurrent ownership still assumed impossible. `[source-implementation-only]`

logcat USB filter on last 800 rows: only `UsbDeviceManager` ignore-missing-sysfs + `UsbPortManager` HAL version. handshake is in dumpsys USB Event Log, not those 800 lines. `[observed-on-Flip]`

## §6.5 in-scope observations

- accessory **appeared**
- strings as above
- Fly auto-launch: Fly **was already running**; attach default is `com.fly.simulator` not `dji.go.v5`
- fold-local adb **survived** accessory mode
- no permission dialog observed by this actor (settings already had accessory permission for simulator)
- mode is accessory, not MTP/charging (`usb_charging=false`)

out of scope (not claimed): byte stream, DUML, video, telemetry, sticks, TX.

## still unproven (§6.7 / C–G)

- **B** cold vs Fly-warmed aircraft relay (needs aircraft, S2)
- PFD bulk endpoints (needs S3 `openAccessory`, not this bead)
- bootstrap/keepalive required on this firmware
- command forwarding / virtual stick / video / telemetry
- O4 from USB identity
- `0x03/0x8E` on Flip
- concurrent Fly + custom fd
- bottom vs top dumpsys contrast on this Fold

## files

| path | contents |
|---|---|
| `docs/s1-captures/00-utc.txt` | capture timestamp |
| `docs/s1-captures/01-adb-devices.txt` | fold-local adb while accessory up |
| `docs/s1-captures/02-getprop.txt` | model/build |
| `docs/s1-captures/03-pids.txt` | Fly / simulator pids |
| `docs/s1-captures/04-dumpsys-battery.txt` | USB powered false |
| `docs/s1-captures/05-dumpsys-usb-redacted.txt` | full dumpsys usb, serials redacted |
| `docs/s1-captures/06-activity-dji-snippet.txt` | Fly/simulator activity lines |
| `docs/s1-captures/07-logcat-usb-filtered.txt` | filtered logcat (thin) |

## next question this run does settle vs not

settled: this RC-N3 advertises **`DJI` / `com.dji.logiclink` / `v0.0.0.0`** as an Android USB accessory on this Fold in ACCESSORY function. `[observed-on-Flip]`

not settled: anything past identity.
