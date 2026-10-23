# S4-prep: Fly runtime dex dump

status: **dump-blocked**. numeric cmdSet/cmdId **not** recovered.
bead: `dji-flip-g0-g1-research-j7k.16`

Fly ran on fold. **no accessory TX.** even a recovered `0x03/0x8E` would still be Fly bytes, not Flip TX. none recovered.

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold session |
| `source-implementation-only` | apk/so from j7k.13/15 |
| `observed-other-model` | Mini-1 8E |
| `untested-hypothesis` | not in these bytes |

## Fly process (`observed-on-Flip`)

| | |
|---|---|
| package | `dji.go.v5` 1.21.16 `versionCode=3132284` |
| start | `am start -n dji.go.v5/com.dji.component.application.activity.DJIPureLaunchActivity` via `scripts/fold-adb` |
| pid | 25391 uid `u0_a382` / 10382 |
| helper | 25411 `perfetto_hprof_` (dumpheap leftover, not a dex loader) |
| build | `ro.debuggable=0` `ro.build.type=user` |

`dumpsys meminfo dji.go.v5`: `.dex mmap` **1756 kB RSS** (packed stub `classes.dex` ~2.2 MiB). `.so mmap` ~312 MiB RSS (fits `libdatajar.so` 219 MiB). native heap ~105 MiB. **no extra .dex mmap the size of an unpacked midware dex.** AppGuard payload stays native/anonymous, not a file-backed dex map. `observed-on-Flip`

## methods tried (all failed)

| method | result |
|---|---|
| `/proc/25391/maps` | unix mode `r--r--r--`, **SELinux** `cat: Permission denied` (shell uid 2000) |
| `/proc/25391/mem` | mode 600 root, unreadable |
| `/proc/25391/map_files` | `dr-x------` root |
| `am dumpheap dji.go.v5 /data/local/tmp/fly.hprof` | `SecurityException: Process not debuggable: dji.go.v5` |
| `run-as dji.go.v5` | `package not debuggable` |
| `su` / magisk bins | no `su`. `/data/adb/magisk` permission denied (not a working root shell) |
| frida / frida-server / dumpdex | **not on box.** not installed this bead |
| oat | `/data/app/…/oat/arm64/` permission denied |
| sdcard `Android/data/dji.go.v5` | caches/logs only. **no** `.dex`/`.vdex` dump |
| jadx inner dex | nothing to jadx |

no ptrace (`TracerPid: 0`). no interactive gadget/repack.

## numeric cmdSet / cmdId

**not recovered.** cannot jadx `CmdIdFlyc$CmdIdType`, `DataFlycJoystick`, `SendVirtualStickFlightControlData`, gimbal, or flight-mode classes without the decrypted dex.

`0x03/0x8E` stays **Mini-1 / DJI-Link** (`observed-other-model`). Fly still having those **names** in `libdatajar.so` (j7k.15) is not a number. `untested-hypothesis`. **do not TX.**

## residual (not this bead)

needs **one** of: root/zygisk dumpdex, a debuggable wrap, or frida-server already on the fold. then jadx the dumped dex. still not Flip-confirmed TX.

## files

- this: `docs/s4-fly-runtime-dex.md`
- empty failed `/data/local/tmp/fly.hprof` on fold — not git
- **no dex dumped**
