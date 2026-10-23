# S4-prep: accessory TX path through RC (no motors)

Status: **write path + session ping only**. not flight. not `0x03/0x8E`.
Bead: `dji-flip-g0-g1-research-j7k.9`.
Operator goal: software into RC so O4 can carry it. **usb ACK ≠ motion.**

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this run |
| `source-implementation-only` | S0 keepalive definition |
| `untested-hypothesis` | O4 forwarding of this ping |

## client write path

`tools/aoa-read`: `FileOutputStream` on the accessory PFD. TX = RCLink `55 cc 49 57` + inner DUML. `Duml.build` throws on `cmd_set=0x03` and `cmd_id` `0x8E` or `0x2A`. `[source-implementation-only]`

## sequence

1. Fly FPV was up (uiautomator FPV/OSD). `[observed-on-Flip]`
2. force-stop `dji.go.v5` + sim. exclusive `openAccessory`.
3. **one** keepalive pair (S0 session ping, dest `0x06` and `0x0E`, `cmd_set 0x06` `cmd_id 0x77`). not RF unlock. not sticks.
4. inbound kept flowing (`0x5749` and `0x574a`). `[observed-on-Flip]`

## TX hex

see `docs/s4-captures/tx-hex.txt`. `[observed-on-Flip]`

## what this does **not** prove

- aircraft moved (it must not have; we did not command motors)
- O4 forwarded the ping (no radio sniffer)
- virtual sticks / takeoff

usb write succeeding + inbound continuing = **path exists**. that is all. `[observed-on-Flip]`

## files

- `docs/s4-tx-path.md`
- `docs/s4-captures/tx-hex.txt`
- `tools/aoa-read/` (`Duml.java` + keepalive after open)
