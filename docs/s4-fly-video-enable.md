# S4-prep: how Fly starts `0x574a` (video-class mux)

status: **mux type named. enable duml still-unknown.**
bead: `dji-flip-g0-g1-research-j7k.17`

no accessory TX. no demux. no frida/su.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold + RC-N3 + Flip |
| `source-implementation-only` | this `dji.go.v5` `libdatajar.so` strings |
| `observed-other-model` | Mini-1 / DJI-Link smali |
| `untested-hypothesis` | not in these bytes |

## what `0x574a` **is**

RCLink/DUSS unit type, not a duml `cmd_set/cmd_id`.

```
55 cc | type u16 le | length u32 le | payload
55 cc 4a 57  => type 0x574a
```

this Fly apk contains the **same log strings** Mini-1 used for that type (`source-implementation-only`):

| string | decimal | hex |
|---|---|---|
| `UsbAccessoryService.onGetBody(dataType==22346)` | 22346 | **0x574a** |
| `…dataType==22347\|\|22350` | 22347 / 22350 | 0x574b / 0x574e |
| `…dataType==22352` | 22352 | 0x5750 |

also in this apk: `AoaRawChannelHandler`, `VideoDataTransferor`, `UsbAccessoryService$VideoStreamSource`, `putVideoBuffer`, `toTransVideoData`, `LB2VideoController`, `SdrLteVideoController`. `source-implementation-only`

S3 already saw `0x574a` on **this** RC after Fly-warm (`observed-on-Flip`). the name match is this apk; the bytes on the wire are S3.

**Fly does not “write type 0x574a” to turn the mux on.** outbound duml rides `0x5749` (`SendPack` exists here; Mini-1 constructor default `x=0x5749` is `observed-other-model`, not a number recovered from this build’s bytecode).

## what actually turns it on — **when** (already proven)

| | `0x574a` |
|---|---|
| Flip OFF, exclusive aoa-read, Fly never live-FPV | **absent** (j7k.7) `observed-on-Flip` |
| Fly `FpvComponentActivity` live camera, then force-stop Fly, then our apk | **present** (S3) `observed-on-Flip` |

so the enable is **state on the RC / O4 / camera**, sticky after Fly dies. not a continuous Fly write of video-class units. `observed-on-Flip`

the **boot ritual** (launch Fly → wait **live** FPV, not splash → force-stop → AoaRead) is still the only proven way. `observed-on-Flip`

## what Fly **writes** to create that state

**still-unknown.** packed AppGuard: no inner dex, runtime dump blocked (j7k.15/16). no numeric `cmd_set/cmd_id` for an enable.

### names in this apk that sit on the path (`source-implementation-only`)

**ui / liveview (matches the ritual)**

- `com.uav.component.fpv.FpvComponentActivity` — S2/S3 focused activity `observed-on-Flip`
- `LiveviewModel`, `GroundLiveViewModel`, `getGroundLiveView`
- `LIVE_VIEW` / `LIVEVIEW` / `SINGLE_LIVE_VIEW` / `NO_LIVE_VIEW`

**local usb rx thread — not the air enable**

- `startStream` / `stopStream` / `startThreads` on `UsbAccessoryService`
- Mini-1 smali: these start the **AOA recv thread**, not camera streaming (`observed-other-model`). same **names** here; do not TX them as if they were duml.

**camera / format (candidates, ids unknown)**

- `DataCameraSetMode` / `DataCameraGetMode`
- `DataCameraSetVideoTransferFormat` (+ `$VideoTransferFormat`)
- `CmdIdCamera` / `CmdIdCamera$CmdIdType`
- `setStreamEnabled`
- `VIDEOSTREAMSOURCETYPE_LIVEVIEW`

none of those strings contain `0x02/…` or any hex. **do not send them.**

## method tried

1. strings on on-fold `libdatajar.so` (already pulled `dji.go.v5`). class names + `dataType==22346`.
2. no jadx of constructors (no dex).
3. no frida/su (forbidden this bead; dump-blocked earlier).
4. no accessory capture/TX this bead.

## residual

1. dump decrypted dex (root/frida — **not** this fold right now) → `CmdIdCamera$CmdIdType` + whatever `FpvComponentActivity` sends at resume.
2. **labeled TX capture** of Fly’s accessory **writes** during FPV start (usb owns fd; Fly would have to be instrumented or a tap). still not Flip-confirmed until we reproduce without Fly.
3. until then AoaRead boot = Fly-warm ritual.

## files

- this: `docs/s4-fly-video-enable.md`
- so/apk still **not git**
