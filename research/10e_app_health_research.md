# 10E App Health — Research & Decision Synthesis

**Stage step:** 10E — Temel uygulama sağlığı  
**Purpose:** Give the app a truthful startup: open the store off the main thread, check its integrity, turn every way it can fail into an accepted state instead of a crash, and build the verified restore mechanism that `data_recovery_required` ultimately points to.

## 1. Research-need decision

**No web research pass was needed.** Every behaviour 10E implements is already accepted: the six cross-cutting states and their meaning (`UXIA-v0`, `THUX-v0`), their tones (`VDSX-v0`), integrity-on-open, no silent reset, backup/export and atomic verified restore (`LFPS-v0`), the negative checks (`TVSX-v0`) and where code may live (`MSBX-v0`). The SQLite facilities used — `PRAGMA quick_check`, `PRAGMA integrity_check`, `PRAGMA foreign_key_check`, `VACUUM INTO` — are part of the bundled engine 10A pinned. They were not taken on trust: every one is exercised by a T2 check against the bundled engine, including one fixture built specifically to show that `quick_check` and `integrity_check` really differ.

## 2. Canonical source set reviewed

- `LFPS-v0 / D-076` §10–§12 — integrity checked on open, after migration and after restore; corruption surfaces `data_recovery_required`; **silent progress reset forbidden**; backup/export user-initiated and complete enough to reconstruct the profile, recording schema and policy version; restore atomic, verified before replacing, forward-migrating older archives, refusing newer ones, never merging.
- `UXIA-v0 / D-068` §14 — six cross-cutting surface states: `loading`, `empty_valid`, `error_recoverable`, `offline_local_available`, `ai_unavailable_core_available`, `data_recovery_required`; AI unavailability must not make the destinations unusable; data problems route to explicit recovery actions rather than resets; backup/export/restore lives in Profile.
- `THUX-v0 / D-069` — `data_recovery_required` supersedes normal work and may block the plan until safe; `error_recoverable` offers a safe retry; `ai_unavailable_core_available` does not supersede a valid deterministic plan; state changes are announced without relying on animation.
- `VDSX-v0 / D-073` — `system_fault` is permitted for exactly `error_recoverable` and `data_recovery_required`; offline and AI-unavailable states are neutral.
- `MSBX-v0 / D-078` — four ports, not five; UI never calls persistence; presentation state is computed in `core-presentation`; the composition root holds no domain logic.
- `AIAX-v0 / D-079` — every non-answer, unavailability included, degrades to `evaluation_pending`; the key never appears in logs, exports, backups or diagnostics.
- `TVSX-v0 / D-081` §8.3–§8.4 — interrupted migration surfaces `data_recovery_required`; restore from a newer schema refused; corrupted or newer archive leaves the profile untouched; export carries no API key.
- `LDBX-v0 / D-085` — its two handed-over open loops: the main-thread open and `DataRecoveryRequired` as a crash.

## 3. What was actually wrong before 10E

Reading the code against those contracts, not the handoff summary, found four problems — two more than the handoff named:

1. **The database was opened synchronously in `MainActivity.onCreate`** — disk work, possibly a migration, on the thread that draws the first frame — and it was opened again every time the activity was recreated.
2. **`DataRecoveryRequired` was an uncaught exception.** A newer schema or a failed migration crashed the app instead of showing the state `LFPS-v0` requires.
3. **Integrity was never checked on open.** `LFPS-v0` §12 requires it; the store was trusted as found. A corrupted file would have been migrated, or would have failed somewhere later with no state at all.
4. **The default build's AI adapter crashed when called.** `AiEvaluator.evaluate` was `TODO()`. The build with the adapter — the one CI calls the normal build — would have crashed on the first open-ended attempt, which is the "core fails because AI is absent" outcome V1 criterion 8 rules out. The no-adapter build was the safe one.

## 4. Synthesis problems 10E has to solve

1. **"Off the main thread" is easy to claim and hard to see.** On a JVM there is no main thread; on a device a regression is a jank report, not a failing test.
2. **The open has to happen once per process, not once per activity.** Recreating the activity is routine.
3. **A failure must not be classified by parsing an exception message.** Messages change between SQLite versions; the reason has to travel as a type.
4. **Recovery and retry are different claims.** A failure before anything was read is operational; a failure while reading or checking puts integrity in question.
5. **"Nothing was reset" is only true if nothing moved.** A status assertion passes an opener that reports recovery and then recreates an empty file.
6. **An integrity check run only on open is not the full check.** `quick_check` skips index-content verification; claiming the full check after a migration needs a fixture that only the full check can see.
7. **A recovery screen must not be able to offer a reset.** Reviewing for it every time is weaker than making it unwritable.
8. **A restore that verifies in place, or migrates the archive in place, damages the user's backup.** And a journal left beside the old live file would be replayed into the new one.

## 5. Positions taken

- **Opening belongs to the process.** `CoachApplication` starts a `StoreStartup` once; activities only observe. The orchestration lives in `core-application` so "never on the caller's thread", idempotence and "an opener defect never crashes" are JVM tests: the opener is held on a latch, so a synchronous start would hang the test rather than observe `Opening`. Debug builds also turn on StrictMode disk detection so a device regression shows in logcat.
- **The reason is a core type.** `StoreStatus` and `RecoveryReason` live in `core-model`; `Migrations.DataRecoveryRequired` now carries its reason. No new port was added.
- **`StoreOpener` never throws and checks before it writes:** `quick_check` + `foreign_key_check` → forward migration → full `integrity_check` if a migration ran. A failure to open the file at all is `error_recoverable`; anything that fails while reading or checking is `data_recovery_required`.
- **No-reset is proven by bytes.** Every recovery case compares the file byte for byte before and after, and checks no sidecar file was created.
- **The difference between the two checks is proven by a fixture.** An index whose stored definition no longer matches its entries passes `quick_check` (asserted as a precondition) and is caught by the post-migration `integrity_check`.
- **`HealthAction` has exactly one value, `RECHECK`,** which re-runs the non-mutating open. There is no reset, wipe, delete or recreate value to render.
- **`ai_unavailable_core_available` is only produced when the core works**, and never blocks. The adapter now returns `evaluation_pending(unavailable)` and reports itself unavailable until 14 gives it call sites.
- **Restore mechanism in 10E, controls in 16D** — the user's decision in this step. Export is `VACUUM INTO` plus the same verification a restore applies, so an unrestorable backup is found when it is made. Restore copies the archive to a staging file, verifies and forward-migrates only the copy, replaces the live file with one atomic rename, moves any old journal aside first (and back if the rename fails), and reopens through `StoreOpener`. The archive format is the product's own schema, so it is complete by construction and needs no second serialisation that could drift.

## 6. Explicitly not decided in 10E

- the Profile controls for export and restore — file choice, replacement confirmation, refusal wording, and whether a replaced profile is kept aside → 16D,
- detecting whole-profile loss: a zero-length or missing database is indistinguishable from a first launch or an interrupted first creation, and holds no data to protect; telling "never had data" from "lost everything" needs a marker outside the database → 19B,
- `offline_local_available` and `empty_valid` are declared but not produced: nothing in the product uses the network yet (AI call sites are 14), and no destination has content yet (11),
- `loading_projection` / `recomputing_projection` → 12, when engines build projections,
- startup and integrity-check time budgets → 18E,
- final microcopy → 14,
- the device run itself: the phone was not connected in this step, so nothing here claims a T6 result.

No latency, database-size or startup-time claim is made.
