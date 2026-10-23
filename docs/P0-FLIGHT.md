# p0 — software flight authority on this Flip

**radio:** phone usb → **RC-N3** → **O4** → Flip. do **not** swap that for 50 m wifi.

**success:** software-originated command (not a physical stick) → aircraft accepts authority → bounded movement we can attribute → release/recover. outdoor, operator holding the rc. not the living room.

Flip is **not** on MSDK v5.18’s list. Mini 4 Pro + RC-N3 virtual sticks (MSDK / DroneLink) and Mini-1 DJI-Link / Neo wifi are **other aircraft**.

## five claims (keep separate)

transport ≠ routing ≠ ack ≠ **authority** ≠ **movement**.

## what we know (2026-10-24)

- transport works. we listen. we keepalive. we do not inject sticks.
- store fly **fpv on this rc has no virtual-stick chrome**. apk vs ui names are **wifi/handheld**.
- in-process observe of fly **failed** (gadget sigill, fridabox sigsegv). **no outbound `55 cc`.**
- msdk: no flip on the list; we did not connect (no app key). dronelink: login wall, no rc.
- operator: **no dji developer key.** do not gate the next step on one.

## remaining o4 bets

1. **see fly’s usb writes** — hardware sniffer or root/usbmon. this is how mini-1 actually got ids (decode, not brute force).
2. **one center-only 8e-shaped send**, operator holding the aircraft — hypothesis test, not discovery of the id space.
3. do **not** spray duml until something twitches.

## not remaining

blind wrap.sh, account-gated gcs unless operator logs in, treating wifi joystick layouts as the km-range path.
