# stick wiggle exclusive READ (no TX)

Status: **no accessory writes.** not motors. not 8E.
Bead: `dji-flip-g0-g1-research-j7k.12`.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + RC accessory |
| `untested-hypothesis` | which u16 is pitch vs roll vs yaw vs throttle |

## method

exclusive `aoa-read` read-only after Fly stop. labeled windows: idle 5s, then 8s pitch / roll / yaw / throttle. second run after operator pane was told the timing. `[observed-on-Flip]`

## 0x06/0x05

inner payload = **five little-endian u16** then 3 zero-ish bytes. values sit in **364 / 1024 / 1684** — the same raw stick range as the RC-N3 **bottom-port** sim readers (S0). `[observed-on-Flip]`

| phase | what moved (min–max) |
|---|---|
| idle | ch4 364–1024; ch0–3 parked 1024 |
| pitch | **only ch4** 364–1684 |
| roll | **ch0** 364–1684; ch4 1024–1684 |
| yaw | ch0, ch2, **ch3** 364–1684 |
| throttle | **ch1** 364–1684; ch0/ch3 small |

**hypothesis: CONFIRMED as the stick-range report on this AOA stream.** `[observed-on-Flip]`

**not** a clean 1:1 axis map. operator may have mixed sticks or leftover motion. which channel is pitch/roll/yaw/throttle = `[untested-hypothesis]`

first run (before pane timing) had all channels frozen at 1024 — that was true no-wiggle, not a kill.

physical sticks **do** show up on phone-facing AOA as `cmd_set 0x06 id 0x05`. that is still **not** inject / not O4 command TX. `[observed-on-Flip]`

## files

`docs/s4-stick-wiggle.md` `docs/s4-captures/stick-wiggle-stats.txt`  
phase bins off git.
