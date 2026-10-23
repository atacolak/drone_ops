# p0 — software flight authority on this Flip

auditor 2026-10-23. **flight is the critical path. video is not.**

goal: Flip executes **software-defined** commands through **RC-N3 / O4**, later autonomous under a supervised fail-safe. Flip is **not** in DJI MSDK. Mini 4 Pro + RC-N3 virtual sticks (MSDK / DroneLink) and Mini-1 DJI-Link / Neo wifi are **other aircraft**.

## five claims (keep separate)

transport ≠ routing ≠ ack ≠ **authority** ≠ **movement**.

## three paths

1. **msdk** — read-only v5 probe on RC-N3 + Flip. connect / model / capabilities / unsupported. **no flight cmds.**
2. **dji fly** — can Fly issue **software** movement (virtual stick / modes), or does RC firmware own physical sticks only? sidecar / instrument Fly / high-level modes. ui-automation of a QuickShot is **not** arbitrary trajectory.
3. **raw usb** — does RC forward phone-originated flight cmds, and what session/auth they need.

## order

1. install the **checksum-valid** gadget copy (`j7k.28` apk). capture outbound around **camera/gimbal**, not guessed sticks.
2. no more blind appguard without a new falsifiable hypothesis.
3. outdoor bounded movement only with operator holding the RC. not this indoor session.

## contingency

if raw O4 inject fails: instrument Fly or Flip wifi. **do not** silently swap km-range O4 for 50 m wifi. if firmware rejects external authority, show that evidence.

## success

linked Flip + RC-N3/O4 → software cmd (not physical stick) → authority accepted → bounded movement attributable → release/recover. independently verified, or a evidenced barrier + alternatives.
