# how this pipe works (do not lose)

operator 2026-10-23: `0x574a` is for **server spatial mapping** later. do not forget it. phone stays on the RC 24/7. our apk (AoaRead), not DJI Fly, is the runtime ui.

## the chain

```
our apk on fold  --usb cable, TOP cradle-->  RC-N3  --o4 radio-->  Flip
     (android)         accessory mode              long range
```

the **phone never talks rf to the drone**. the **remote** does. software on the fold only talks **usb** to the remote. that is why this is the km-range path, not fly’s 50 m wifi.

two usb-c on the rc:

- **top** (phone cradle) = this pipe. RC is usb **host**, phone is **accessory**. name: manufacturer `DJI`, model `com.dji.logiclink`, version `v0.0.0.0`.
- **bottom** (between sticks) = charge / pc sim. different. do not put the fold there.

charge the rc on the **bottom**. leave the fold on the **top**.

## what a “channel” is

usb bulk reads are just bytes. they are chopped into **RCLink / DUSS** units:

```
55 cc | type u16 le | length u32 le | payload
```

type is stored little-endian, so on the wire:

- `55 cc 49 57` = type `0x5749` (`IW`) = **duml** (commands, telemetry-ish, stick **reports**)
- `55 cc 4a 57` = type `0x574a` = **video-class** (fat payloads; we have not demuxed)

so “channel” = that type field. not a wifi ssid. not a dji fly setting named channel.

inside `0x5749` is **duml**: another `55 …` with sender, receiver, `cmd_set`, `cmd_id`, payload.

inbound stick **report** we proved: `cmd_set 0x06` `cmd_id 0x05`, five u16le, 364 / 1024 / 1684. that is the **physical sticks telling the phone**. that is **not** us flying the drone.

## how `0x574a` gets turned on — **when**

this is a **boot ritual**, not a runtime dependency.

**you need fly on** only to *start* o4 + the video mux. **you need fly off** before our apk can own usb.

| when | fly | why |
|---|---|---|
| fold on rc, flip **off** | off | sticks/keepalive only. **no** `0x574a`. |
| want video-class / o4 mux | **on until live FPV** | launch `dji.go.v5`, wait until **live camera**, not the splash. fly owns the fd here. |
| the **instant** FPV is actually live | **force-stop** fly (and sim) | o4 is already up. kill fly so we can `openAccessory`. |
| immediately after that | our apk | `0x574a` is in the firehose if the warm worked. |
| rc reboot / flip reboot / mux dies | warm **again** | 24/7 without re-warm is **unproven**. |
| just want stick **bars** | fly can stay off | `0x06/0x05` does not need fly-warm. |

do **not** leave fly running. two apps cannot hold the accessory. fly is a match, not the fireplace.

we still do **not** know which duml (if any) fly **writes** to enable `0x574a`. until we reverse that, this ritual is the only proven way. next research: reproduce the enable in AoaRead so fly is not required even as boot.

we **do not** have fly’s **write** commands (virtual stick, cine/normal/sport). names are in packed `libdatajar.so`; **numeric ids were not recovered** (AppGuard). do not inject.

## our apk vs fly

- **runtime:** `sh.colak.dji.aoaread` (AoaRead). opens accessory, keepalive `0x06/0x77` only, draws report bars. refuses stick/takeoff ids.
- **boot (o4 / 0x574a):** launch fly → **wait live FPV** → force-stop fly → start AoaRead. not a 24/7 ui.
- **never:** two apps holding the accessory at once. android gives one fd.

## how we reverse (so we remember)

1. **listen** — own the usb, dump bytes, parse `55 cc` types. compare flip off vs fly-warm vs fly-stop.
2. **wiggle** — move physical sticks, see which duml payload changes. that labels **reports**, not inject.
3. **apk** — pull `dji.go.v5` (fly) and the **simulator** separately. strings / jadx. fly’s real stick **send** is packed in `libdatajar.so` (AppGuard ELF, no inner dex on disk). runtime dump of decrypted dex is blocked on a user/non-debuggable fold (no su/frida).
4. **do not** cargo-cult mini-1 `0x03/0x8E` as flip until fly’s bytes say so.
5. **spatial mapping later:** take `0x574a` payloads off the phone (adb pull / network) to a **server**. do not block the control surface on a fold video player.

## how to talk to the fold from sfub

`docs/FOLD-CONTROL.md` / `scripts/fold-adb` / `ssh fold adb-wifi`.
port **rotates**. never `adb connect` from sfub. never pair this pc.

## not done

inject sticks, set C/N/S in software, gimbal command, **reproduce `0x574a` enable without fly**, demux `0x574a` on a server, indoor flight.
