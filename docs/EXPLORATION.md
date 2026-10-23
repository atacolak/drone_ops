# DJI Flip / RC-N3 — exploration → exploitation handoff
Created: 2026-10-22
Status: exploration synthesis / NOT an execution approval
Owner of intent: operator
Intended consumer: one coordinating actor, then independently delegated actors and subagents

## why this document exists

This is not a generic "investigate drone API" prompt. It preserves a substantial exploratory conversation: hypotheses, existing implementations, rejected approaches, uncertainties, hardware sequencing, and the work-organization requirements the operator explicitly wants. It should allow a locally running actor to continue from the point exploration reached, without asking the operator to relay the conversation or redo the research.

The operator distinguishes EXPLORATION (research, compare approaches, pressure-test mechanisms and intent) from EXPLOITATION (turn the explored position into durable tasks, implement, test, independently verify, and respond to evidence). Exploration continues as exploitation raises new questions. Neither is a one-time step.

## product intent / constraints

- Aircraft: DJI Flip. Controller: DJI RC-N3. Available Android host: Samsung Galaxy Z Fold 5.
- Require software-programmatic control *through the RC-N3's long-range O4 radio path*, not short-range phone-to-aircraft Wi-Fi.
- Need both aircraft commands AND live video/telemetry. Treat each as an independent feasibility question until verified.
- Possible future dedicated, continuously powered Android host, but do not prematurely design it now.
- Entirely software-only. NO hardware joystick modification, electrical injection, soldering, custom controller board, or transmitter modifications.
- No radio power/region hacks, disabling flight safeguards, unattended autonomous flights, or motor/flight commands in the present exploratory phase.
- Prefer reusing an existing Android connection plus protocol implementation instead of building a broad new aircraft-control framework.
- Preserve direct physical RC-N3 manual input and characterize whether the phone can inject separate virtual-stick control; DO NOT assume these share the same command path.
- Treat long-range RF capability as an engineering characteristic, not permission to fly beyond line of sight. Investigate legal constraints before any flight test.

## findings and source leads from exploration

These are leads discussed in conversation. Before upgrading them to implementation facts, inspect CURRENT source code, evidence and compatibility on DJI Flip + RC-N3. Model/repository support for other aircraft is NOT proof of Flip support.

1. DJI's mobile SDK does not officially list DJI Flip support. Therefore official virtual-stick control is not presumed available.
2. DJI Fly offers on-screen sticks in DIRECT phone Wi-Fi control, but this uses a short-range path (previously identified as roughly 50 m distance / 30 m altitude). Automating those gestures through Kotlin AccessibilityService, minitouch, or mobile-agent tools DOES NOT provide RC-N3/O4 range. It is NOT the chosen primary path.
3. sealgate's mobile tunnel / Edison-Watch/mobile demonstrated UI automation of a DJI Neo through DJI Fly and agent control; useful reference for Android automation but not the preferred range-compatible solution.
4. lslv1243/neo-control (https://github.com/lslv1243/neo-control): reverse-engineered first-generation Neo aircraft-facing Wi-Fi protocol / joystick packet encoding; reported direct control but lacks its own usable video/telemetry. It is NOT an RC-N3 USB implementation.
5. Kolya080808/DJI-Link (https://github.com/Kolya080808/DJI-Link): legacy Mavic Mini / USB accessory → RC → aircraft stack. Discussion identified reported hardware-verified continuous virtual-stick control using command 0x03/0x8E, plus video and telemetry on THAT platform. Key uncertainty: command forwarding, authority, encoding and compatibility with MODERN Flip + RC-N3. Treat 0x03/0x8E as a candidate to INVESTIGATE, not as a permitted command to transmit or a confirmed Flip solution.
6. eraycn/dji-rc-n3 (https://github.com/eraycn/dji-rc-n3): RC-N3 USB protocol work, reads physical controller inputs for simulators. Reads are not proof that joystick inputs can be injected through USB.
7. gladiusxosmium/DJI-RC-N3-Joystick_MACOS (https://github.com/gladiusxosmium/DJI-RC-N3-Joystick_MACOS): additional RC-N3 USB interface/physical channel interpretation; distinguish simulator-oriented port and app-facing phone port.
8. doesthings/FreeFCC-USB (https://github.com/doesthings/FreeFCC-USB): Android USB Accessory transport references, including UsbManager.openAccessory and command framing. Transport reference ONLY; do not implement the radio-power alteration. Hardware validation and applicability require confirmation.
9. andreapianidev/DJI-FCC-Unlock-iOS (https://github.com/andreapianidev/DJI-FCC-Unlock-iOS): reports hardware-validated bidirectional RC-N3 + Neo command traffic under specific session state, with different behavior on other aircraft. Useful for accessory initialization/response investigation, not for changing region or RF settings.
10. Android Frida instrumentation (https://frida.re/docs/android/): possible observation of DJI Fly's internal transport on a rooted/debuggable environment. An independent app cannot ordinarily sniff another app's owned USB accessory stream. App repackaging/instrumentation may break DJI Fly. Do not presume stock Fold root.
11. Android UsbManager accessory APIs: an independent Android app might become the host-side client when DJI Fly releases the accessory, but then it must implement initialization, telemetry/video handling and protocol details; cannot presume DJI Fly and a custom app own the same accessory concurrently.

## critical unknowns (do not collapse)

A. USB transport: which interface and endpoints does phone-facing RC-N3 actually expose on this specific firmware/Fold?
B. Session: who initializes/authenticates the RC-N3 ↔ aircraft connection? Cold independent client versus session initialized by DJI Fly?
C. Routing: can software-authored aircraft flight-control commands be relayed through RC-N3 O4, or does RC-N3 firmware prohibit/ignore them?
D. Flight authority: is virtual-stick control supported on Flip and, if so, which arming/mode/permissions/handshake? Can manual sticks override it deterministically?
E. Telemetry: which packet streams can our client receive; data schema; confidence/validation against DJI Fly?
F. Video: demux, codec, encryption, keys/session authority, actual usable latency; can it coexist with flight commands in one software client?
G. Safety: keepalive / expiration / lost-link behavior, neutral-stick fallback, secure ownership and operator takeover.

A successful USB handshake alone does NOT prove aircraft control; a protocol acknowledgement does NOT prove physical actuation or authority. Model-specific compatibility has to be tested.

## hardware experiment sequence / boundaries

S0: OFFLINE RECONNAISSANCE. All actors can inspect repositories, APK structure/documentation, parser implementations and tests. Preserve source URLs and exact file locations; assign confidence levels. No hardware needed.
S1: RC-N3 + Fold, FLIP OFF. Read-only. Turn controller on; use its phone-facing USB-C connection, not blindly assuming its charging/simulator port is equivalent. Inspect Android USB state via **`ssh fold` then fold-local adb** (`docs/FOLD-CONTROL.md`) so the Fold USB-C stays free for the RC. Do **not** `adb connect`/`adb pair` from sfub. Device/interface identity, DJI Fly process startup, logs, accessory enumeration. System logs do not constitute USB payload capture.
S2: RC-N3 + Fold + Flip paired, GROUNDED. Read-only. Establish a normal DJI Fly connection; compare controller-alone versus aircraft-paired, initial cold session versus DJI-Fly-initialized session, passive telemetry / video availability. Do not run motors.
S3: Custom read-only Android accessory client / independent protocol decoder. Record timestamped byte-stream direction/framing, source transport, version, precise connection state. Attempt to decode and corroborate telemetry and video independently. Never stream unredacted device/account identifiers or credentials into public repos.
S4: Flight-control FEASIBILITY ANALYSIS. Only source/code and observed safe responses until a separately approved experiment is prepared. Candidate 0x03/0x8E and other messages are not authorized write targets. If ever testing actuation, require direct human supervision and a separate safety gate.

A source-only report can complete S0; live telemetry/video cannot be claimed without actual hardware evidence.

## ownership of work, NOT another orchestration framework

There is already working actor-to-actor messaging; do not waste the campaign proving that one actor can message two others. Multiple actors may work concurrently.

An EXPLORER (this conversation, or future exploration actor) produces and updates the exploration artifact. It explains alternatives and WHY some were excluded.

A COORDINATING/EXPLOITING ACTOR reads the exploration artifact, owns the outcome, selects the next executable frontier, creates project issues in the existing beads/br graph, directs peer actors over existing messaging, requests independent review, and updates the exploration artifact when new evidence changes the plan.

A DURABLE BEAD represents a commitment/outcome that must survive context loss or handoff. Use beads for actor/lead owned durable work and cross-actor dependencies. Ephemeral subagents responding within one actor turn do not have to operate beads themselves; their delegating actor records the work/evidence/outcome into its bead. Do not require issue creation for every thought or tool call.

An ACTOR accepts an assigned bead only after actually receiving, understanding and acknowledging it; it stays responsible for publishing evidence and the transition when it is finished, blocked or failed. "Message sent" does not imply "work begun"; "pane exists" does not imply "task progressed".

VERIFICATION is distinct from completion claims. An independent reviewer checks the bead's acceptance conditions against code/test results/raw captures and can reject the result, causing correction or new work.

The COORDINATOR itself materializes the NEXT FRONTIER whenever new accepted results or blocked questions change what is possible. This is cognition exercised at meaningful transitions, not a static issue graph authored entirely upfront. It may ask for more exploration when warranted.

## the continuity problem to PROVE in the current stack

The user's bottleneck is NOT agent communication. It is reliable progression when agents are idle/stopped, tasks finish, or discoveries change the plan, without the user becoming a manual message proxy.

Use the EXISTING durable work graph and whichever EXISTING scheduler/runtime wake mechanism actually works. Do not invent a second state store, second actor scheduler or another all-encompassing CLI.

One crucial end-to-end test:
- an actor owns a bead; another actor depends on its completed result;
- completion is published to durable state;
- the waiting coordinator/actor becomes runnable because of that transition (event / message / existing automation, with bounded reconciliation fallback);
- it ACKNOWLEDGES, inspects evidence, produces a newly materialized eligible task or correct decision, and WORK ACTUALLY CONTINUES;
- kill/restart the coordinating process once; demonstrate continued progress from durable state without operator message-copying.

If event wake is not actually implemented: state that precisely. First prove a bounded poll/reconcile using currently operational primitives; do not pretend a new watcher exists. No need to keep an LLM sampling while there are no new facts. The watcher can be deterministic; cognition happens at meaningful events and recovery checks.

A useful optional heuristic for detecting actor drift is comparing recent observed tool intents, eval/title markers, and session activity SINCE the last assignment against the bead's objective. This is NOT acceptance proof. It can detect inactivity or suspected misalignment and trigger a targeted review. Do not use it instead of artifact verification.

Never treat issues as done because an actor self-reported success. Never assume observed runtime state equals actual progress.

## smallest first research frontier

Suggested initial DURABLE outcomes for actors, NOT a cap of two workers:
- protocol source inventory: USB phone-side startup, initialization and read framing with accurate source locations and confidence.
- capability matrix: which projects prove USB bridging, virtual sticks, telemetry, video; on what precise drone/controller pair; which gaps for Flip.
- Android diagnostic specification: read-only controller-only experiment, log fields, tests, device-side permissions, how to attach Fold over wireless ADB while RC uses USB.
- independent synthesis/verification: identify unsupported extrapolations and decide next hardware-access request.

Workers may investigate more in parallel when the coordinator judges work sufficiently independent. An actor can use multiple ephemeral subagents inside its task.

Acceptance of the first frontier: the coordinator shows an evidence-backed, appropriately prioritized next hardware experiment; durable issues are updated; one independent verification occurred; and a follow-on commitment was autonomously created or resumed when a dependency settled. Do not measure success by number of agents, panes or prompts.

## required evidence on each durable issue

- objective and acceptance criteria
- source links or recorded-input IDs
- owner and actual acknowledgment
- state and explicit blockers
- artifacts and tests, where relevant
- claim classification: observed on Flip / observed on different model / source implementation only / untested hypothesis
- verifier outcome and rejection reason
- resulting next question, if any

Do not bury the provenance in chat history. Avoid a gratuitous new schema/database: use fields/comments/artifact links already supported by beads, supplement with project files if needed.

## action/authority policy

Research, local parser tests, source inspection, and safe read-only device diagnostics may proceed under existing authorized access.
DO NOT send undocumented control/arming/motor/takeoff/landing commands, modify RF/region constraints, or carry out unattended flight.
Physical device connection and any active control experiments require the operator at the hardware, with explicit procedural approval.
Never automatically infer consent to physical tests from approval of this document.

## criteria for a successful handoff

The exploiting actor should be able to explain:
1. Why short-range UI automation was rejected as the primary architecture.
2. Which links actually speak RC-N3 phone-facing USB vs Neo Wi-Fi vs legacy Mavic Mini.
3. The separate blockers for programmatic flight, video and telemetry.
4. Why discovered command IDs are candidates rather than confirmed Flip features.
5. Which stage can start with the Flip off and when the aircraft must be paired.
6. Who is responsible for creating the next bead, and who records ephemeral subagent work.
7. How new bead evidence causes a sleeping/waiting actor to ACT, not just receive a message.
8. Which next experiment will most efficiently settle the highest-impact uncertainty.

If it cannot, it has not absorbed the exploration. Stop before dispatching hardware-sensitive work.
