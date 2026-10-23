# DroneLink connect-only (Flip + RC-N3)

Status: **no mission. no virtual-stick send. no takeoff. no motors.**
Bead: `dji-flip-g0-g1-research-j7k.34`.
no our APP_KEY. Mini 4 Pro success is **`observed-other-model`**, not Flip proof.

## claim tags

live fold facts: `observed-on-Flip`. Mini 4 Pro: `observed-other-model`.

## install

Play `com.dronelink.dronelink` installed. **never** `-r dji.go.v5`. `[observed-on-Flip]`

## usb owner

- accessory `DJI` / `com.dji.logiclink` / `v0.0.0.0` still ACCESSORY
- `dji.go.zz` force-stopped (pid empty)
- **`dji.go.v5` still running** — Fly kept the fd. DroneLink launched without stealing it. `[observed-on-Flip]`

did not stop Fly: DroneLink never got past login, so accessory handoff was not required yet.

## connect

launched `com.dronelink.dronelink/.MainActivity`.

permissions: location **while using the app**; audio **don't allow**; phone **don't allow**.

a11y: **Welcome to Dronelink** / Sign Up / Login / Learn / Dashboard.

**did not** tap Login (would need operator credentials). **did not** start a mission, send virtual sticks, or takeoff.

RC seen? **no** (login wall). aircraft / product name / unsupported? **not reached.** `[observed-on-Flip]`

## Mini 4 Pro

other people flying Mini 4 Pro + RC-N3 via DroneLink is **`observed-other-model`**. not this Flip.

## not used

APP_KEY, wrap.sh, magisk, 8E, wifi as primary path.

## files

`docs/s4-dronelink-probe.md`
