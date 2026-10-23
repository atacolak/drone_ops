# P0.1: MSDK v5 read-only probe

status: **sdk not installed on fold. Flip not on v5.18.0 product list. no connect. no flight cmds.**
bead: `dji-flip-g0-g1-research-j7k.31`

no wrap.sh. no FridaBox. never `-r dji.go.v5`. **no VirtualStick send. no takeoff. no motors.**

## claim tags

| tag | meaning |
|---|---|
| `observed-on-Flip` | this Fold session |
| `source-implementation-only` | MSDK 5.18.0 README / sample |
| `observed-other-model` | Mini 4 Pro listed, not this aircraft |

## 1. control

`scripts/fold-adb shell id` → `uid=2000(shell)`. `observed-on-Flip`

store Fly **not** overwritten.

## 2. five distinctions

| claim | result | tag |
|---|---|---|
| sdk **installed** on fold | **no.** no MSDK sample/probe apk. no `com.dji:dji-sdk-v5-aircraft` in this repo | `observed-on-Flip` |
| sdk **talks to RC-N3** | **not reached** | — |
| sdk **identifies Flip** | public 5.18.0 supported list has **no Flip, no Neo, no RC-N3** | `source-implementation-only` |
| virtual-stick **API exists** | `IVirtualStickManager` / `VirtualStickVM.kt` for **listed** aircraft | `source-implementation-only` |
| **we moved the aircraft** | **no** | `observed-on-Flip` |

Mini 4 Pro **is** on that list. that is **other-model**, not Flip proof. `observed-other-model` as “MSDK documents Mini 4 Pro”; **not** observed on this Flip.

## 3. official list (v5.18.0 README)

https://github.com/dji-sdk/Mobile-SDK-Android-V5/blob/master/README.md

listed: Mini 3 / Mini 3 Pro / **Mini 4 Pro**, Mavic 3 Enterprise / 3TA, M30, M300/350, Matrice 4 / 4D / 400, H30.

**absent:** DJI Flip, DJI Neo, RC-N3.

github code search `Flip` in that repo hits font-awesome `.fa-flip-horizontal`, not a product enum. `source-implementation-only`

virtual-stick sample (listed products only):  
`SampleCode-V5/android-sdk-v5-sample/.../VirtualStickVM.kt` — `enableVirtualStick()`, `sendVirtualStickAdvancedParam`. **not called.** no raw `0x03/0x8E`. `source-implementation-only`

## 4. why no on-fold connect

to register v5 you need a **DJI developer APP_KEY** bound to the app id ([run notice](https://developer.dji.com/doc/mobile-sdk-tutorial/en/quick-start/user-project-caution.html)).

this host: android sdk platforms 34–36 present; **no** gradle MSDK project; **no** dji app key in `secret list mcp` (name grep). did **not** invent a key. did **not** clone/build the production sample.

**blocker:** APP_KEY + sample build, not fold-adb.

even with a key, README still **omits Flip** — connect would be expected to fail or mis-identify. that remains `untested-hypothesis` until a registered v5 app logs product type on **this** RC+Flip.

## 5. methods tried

- fold-adb id
- README 5.18.0 + github code search Flip / MINI_4
- secret **names** only for a dji key
- **not** VirtualStick send, **not** wrap.sh, **not** FridaBox, **not** store Fly replace

## residual

if operator supplies an APP_KEY (bao path, never print): build a **read-only** probe (register, `ProductType`, connection callbacks, capability errors). still **no** `sendVirtualStick*`. Mini 4 Pro docs stay other-model.

## files

`docs/s4-msdk-probe.md`
