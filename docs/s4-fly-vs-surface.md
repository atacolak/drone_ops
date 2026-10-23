# P0: store Fly virtual-stick surface (no send)

status: **wifi/handheld UI names exist. RC FPV overlay: no VS chrome (j7k.32). RC path = api-only / unknown send.**
bead: `dji-flip-g0-g1-research-j7k.33`

no wrap.sh. no motors. no takeoff. **no stick TX.** store Fly not replaced. zz not started.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + store `dji.go.v5` |
| `source-implementation-only` | this apk `libdatajar.so` strings |
| `observed-other-model` | Mini 4 Pro / MSDK |

## 1. this session

`scripts/fold-adb shell id` ok.

| | |
|---|---|
| store Fly | pid 29660, **FpvComponentActivity** resumed, under `DJIDeviceActivity` |
| zz | **not running** |

did **not** tap FPV, **not** dump a11y (j7k.32 already: no VS chrome). usb owns Fly. `observed-on-Flip`

## 2. answer (explicit)

**ui surface exists** — for **Wi-Fi / handheld** FPV, in this apk’s names.

**not observed** on **this** Flip + RC-N3 USB FPV overlay (j7k.32).

**api-only** for RC/USB software send: ksdk / midware **names**, no UI chrome on this pair, **unknown** whether store Fly ever **sends** virtual-stick through RC-N3.

Mini 4 Pro MSDK virtual stick = **other-model**. not this aircraft.

## 3. apk names (`source-implementation-only`)

**Wi-Fi / handheld UI (layouts, icons, copy)**

- `VirtualStickView` under `fpv/.../mission/handheld/panel/stick/`
- `addVirtualStickShellToWifiHorizontal` / `wifiHorizontalVirtualStickLayout`
- `{UAVFLY_}wifi_virtual_joystick_control` (+ ptz, acrobatic flip, landscape)
- `fpv_wifi_fast_setting_virtual_stick_btn`
- `wifi_fpv_guide_virtual_joy_stick_bubble`
- `Flight.HandHeld.HandHeldControl.VirtualJoyStick`
- `IsSupportVirtualJoyStick` / `v1HandHeldControlIsSupportVirtualJoyStick`

**not** an RC overlay: filenames are `wifi_` / `handheld` / `portrait_virtual_stick_*`.

**RC settings that are not virtual sticks**

- `PortraitRCStickModeViewModel` / `subscribeRcStickMode` — **physical** stick mode 1/2/3 mapping
- `*InSportMode` speed keys — cine/sport **limits**, not on-screen sticks

**API / keys (no chrome)**

- `KeySendVirtualStickFlightControlData` / `SendVirtualStickFlightControlData`
- `KeyVirtualStickControlModeEnabled`
- `VirtualStickFlightControlParam(pitch=`
- `DataFlycJoystick` / `setJoyStick` (j7k.13/15) — still **no** Flip-confirmed TX

## 4. what we did **not** do

invoke send, takeoff, motors, wrap.sh, `-r` v5, treat Mini 4 Pro as Flip, steal FPV a11y from usb.

## residual

wifi/handheld VS UI on **this Flip without RC** is a different path (50 m, rejected as primary). RC software send still needs **outbound bytes**, not these names.

## files

`docs/s4-fly-vs-surface.md`
