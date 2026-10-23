# lean surface: our apk owns RC usb (no Fly at runtime)

Status: **session owner**. not stick inject. not indoor flight. not 8E.
Bead: `dji-flip-g0-g1-research-j7k.14`.
apk: `tools/aoa-read` package `sh.colak.dji.aoaread`.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + RC |
| `source-implementation-only` | keepalive / stick-range decode |
| `untested-hypothesis` | whether O4 stays up forever without Fly |

## what it is

phone stays on the RC **TOP** cradle. **AoaRead** is the runtime UI, not DJI Fly.

- opens AOA exclusive (`DJI` / `com.dji.logiclink`)
- **TX:** keepalive `cmd_set 0x06 id 0x77` every 2.5s only (`Duml.keepalive`). refuses `0x03/0x8E` and `0x03/0x2A`. **no stick frames.**
- **RX report:** inbound `0x06/0x05` drawn as five bars (364 / 1024 / 1684). visual feedback only. not inject.

`[source-implementation-only]` for the app; open without Fly `[observed-on-Flip]` this run (`fly=` empty, `openAccessory ok lean owner`).

## how to run without Fly

control path: `docs/FOLD-CONTROL.md` / `scripts/fold-adb`.

```
# one-shot Fly-warm IF you still need O4 video (S3: 0x574a showed up after Fly FPV then Fly stop)
scripts/fold-adb shell monkey -p dji.go.v5 -c android.intent.category.LAUNCHER 1
# wait FPV, then:
scripts/fold-adb shell am force-stop dji.go.v5
scripts/fold-adb shell am force-stop com.fly.simulator

# lean owner
scripts/fold-adb install -r tools/aoa-read/build/aoa-read.apk
scripts/fold-adb shell am start -n sh.colak.dji.aoaread/.CaptureActivity
```

build: `tools/aoa-read/build.sh` (jdk-17 + android-34).

RC charger = **bottom** usb-c. top stays Fold.

if USB dialog: allow AoaRead for DJI RC-N2/3.

## Fly-warm vs runtime

- **runtime:** Fly must not hold the fd. force-stop it. `[observed-on-Flip]`
- **O4 / 0x574a:** S3 needed a Fly FPV session first, then Fly stop, then video-class units still flowed. treat Fly-warm as **boot**, not UI. whether that lasts 24/7 = `[untested-hypothesis]`

## not this bead

stick inject, motors, takeoff, using Fly as the cockpit.
