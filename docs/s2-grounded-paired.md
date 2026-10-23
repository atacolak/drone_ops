# S2 grounded paired (Flip ON, no motors)

Status: **S2 read-only**. Operator scheduled Flip ON, grounded, props off. Not S3/S4. Not virtual-stick.
Bead: `dji-flip-g0-g1-research-j7k.4`.
Control: `docs/FOLD-CONTROL.md` / `scripts/fold-adb`. no sfub pair.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + RC-N3 + this run |
| `source-implementation-only` | prior S0/S1 |
| `untested-hypothesis` | not evidenced |

camera/O4 **availability** ≠ telemetry schema ≠ virtual sticks. `[source-implementation-only]` as campaign split.

## method

1. fold-adb / adb-wifi; `device` row `127.0.0.1:46331` SM_F946B.
2. look-before: ACCESSORY already up; Fly/sim pids **empty**.
3. launched `dji.go.v5` (MAIN/LAUNCHER). **did not** force-stop Fly. **did not** `openAccessory` from `tools/aoa-read`.
4. Fly showed USB dialog “Allow DJI Fly to access DJI RC-N2/3?” — OK, so Fly can hold the session.
5. recorded usb + activity. no candidate TX, no motors.

`[observed-on-Flip]`

## usb identity (paired)

unchanged from S1:

| field | value | class |
|---|---|---|
| current_functions | ACCESSORY | observed-on-Flip |
| manufacturer | `DJI` | observed-on-Flip |
| model | `com.dji.logiclink` | observed-on-Flip |
| version | `v0.0.0.0` | observed-on-Flip |
| accessory_description | `DJI RC-N2/3` | observed-on-Flip |
| serial | `[REDACTED]` | |
| usb_charging | false | observed-on-Flip |
| accessory uid | **10382** (`dji.go.v5`) | observed-on-Flip |

S1-sim exclusive: same strings, uid **10386** (`com.fly.simulator`). `[observed-on-Flip]`

**usb identity does not change when Flip is on.** who owns the fd does. `[observed-on-Flip]`

## Fly session / O4 availability

after USB grant:

- `dji.go.v5` pids `23902` `23922`
- **resumed / focused:** `com.uav.component.fpv.FpvComponentActivity`
- underneath: `DJIDeviceActivity`
- task visible

`[observed-on-Flip]`

that is Fly’s FPV / live-camera UI. treat as **passive video/O4 availability: yes**, on this paired grounded run. `[observed-on-Flip]` not a demux, not a latency number, not a codec claim.

## controller-alone vs paired

| | S1 / sim (Flip OFF) | this S2 (Flip ON, Fly FPV) |
|---|---|---|
| accessory strings | DJI / logiclink / v0.0.0.0 | same |
| fd owner | sim 10386 (j7k.7 exclusive also after force-stop) | Fly 10382 |
| inbound RCLink `55 cc 49 57` | captured exclusive, no Fly | **not captured** (would steal Fly’s fd) |
| live camera UI | no | FpvComponentActivity |

rclink **byte** compare vs s1-sim: **not done**. Fly is the link we needed; work order: do not fight Fly for the usb fd. `[observed-on-Flip]` for the policy; stream-diff `[untested-hypothesis]` until a later S3 that is allowed to capture alongside or after Fly.

## cold vs Fly-initialized

- j7k.7: exclusive `openAccessory` after force-stop Fly/sim, Flip OFF → inbound RCLink without our TX. cold-ish **controller accessory**.
- this S2: Fly launched → USB grant → **FPV**. Fly-initialized **aircraft-facing** session.

`[observed-on-Flip]` as two different owners/UIs. not a packet-level handshake dump of Fly TX.

## not claimed

motors, takeoff, `0x03/0x8E`, RF, stick inject, video codec/keys, telemetry schema, concurrent Fly+custom fd.

## files

`docs/s2-captures/00-utc.txt` `01-adb.txt` `02-pids.txt` `03-usb-identity.txt` `04-activity.txt`
