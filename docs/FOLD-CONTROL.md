# Fold control path (dji-flip)

this is how we talk to the Galaxy Z Fold 5 from sfub. read it before any S1+ hardware work.

the wireless-debug **port rotates**. that is normal. it is **not** a human dependency and **not** a `br` BLOCKED.

## the path

```
sfub  --ssh-->  fold (Termux sshd)
                  u0_a368 @ 10.77.206.52:8022
                  Host fold in ~/.ssh/config
                    |
                    v
              adb-wifi          # discovers current 127.0.0.1:<port>
              adb-keep          # already looping on the phone
                    |
                    v
              fold-local adb
              127.0.0.1:<ROTATING PORT>  device  SM_F946B  uid 2000
```

wireless debugging stays **on the phone**, for Termux's localhost client (`u0_a368@localhost`). that is **not** sfub.

**never hardcode a port.** 44377 / 35131 / 41531 / whatever you saw last time is already stale.

## every adb call

from sfub, do this **every time**, including when `adb devices` shows `offline`:

```bash
ssh fold adb-wifi
ssh fold 'adb devices -l'     # pick the line with `device`, ignore `offline`
# or:
scripts/fold-adb shell id     # wrapper: adb-wifi then adb -s <live serial>
```

`offline` leftovers are junk. `adb-wifi` is discovery. if you `br comments BLOCKED` because of a stale port without running `adb-wifi`, you are wrong.

## do not

- `adb connect` / `adb pair` / mDNS / port-scan from **sfub**
- ask the operator for IP:port or a pairing code
- treat netbird `Idle` as down (lazy handshake; ping IPv4 `10.77.206.52` first)
- use **phonectl** / **ando** / `ssh sfpix` (Pixel 8 Pro)
- plug the RC into sfub USB
- BLOCKED on rotating ports

## the only remaining human toggle

`adb-wifi` prints `wireless debugging is off` **after it actually ran**. then the Fold screen: Wireless debugging **On**. no IP, no code, no pair this pc.

if it was on and `adb-wifi` still fails, run it again. do not ping the operator.

## how to tell you are on the wrong plane

| you see | meaning |
|---|---|
| `u0_a368@localhost` currently connected | Termux local adb. correct. |
| sfub `adb devices` empty | expected. sfub is not a wireless-debug client. |
| fold `127.0.0.1:NNNN offline` | stale. run `adb-wifi`. not BLOCKED. |
| `phonectl` / `ando` / `husky` | wrong phone. |
