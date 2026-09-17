# App Health Specification — APHX-v0

**Stage step:** 10E — Temel uygulama sağlığı  
**Status:** ACCEPTED — independent 10E QA PASS  
**Decision:** `D-086`  
**Model:** `APHX-v0 — App Health`  
**Persistence:** `LFPS-v0 / D-076`  
**Surface states:** `UXIA-v0 / D-068`, `THUX-v0 / D-069`, `VDSX-v0 / D-073`  
**Boundaries:** `MSBX-v0 / D-078`  
**Verification strategy:** `TVSX-v0 / D-081`

## 1. Purpose

10E gives the app a truthful startup and closes AŞAMA 10.

It answers one primary question:

> **Uygulama nasıl dürüstçe açılır, ve yerel veri güvenle kullanılamıyorsa ne olur?**

Primary invariant:

> **No failure of the store is a crash, and no failure of the store is a reset.** Every way opening can go wrong becomes an accepted state; none of them deletes, truncates, recreates or merges the learner's data — and "nothing moved" is proven by comparing bytes, not by trusting a status.

---

# 2. What was wrong before 10E

The handoff named two problems. Reading the code against the contracts found two more.

| Found | Contract it broke |
|---|---|
| the store was opened synchronously in `MainActivity.onCreate`, and again on every activity recreation | main-thread disk work; `LDBX-v0` open loop |
| `DataRecoveryRequired` was an uncaught exception — a newer schema or a failed migration crashed the app | `LFPS-v0` §10: surface `data_recovery_required` |
| integrity was never checked on open | `LFPS-v0` §12: integrity is checked on open |
| the default build's `AiEvaluator.evaluate` was `TODO()` and would crash on the first open-ended attempt | `AIAX-v0`: unavailability degrades to `evaluation_pending`; V1 criterion 8 |

The last one is worth dwelling on: the build *with* the AI adapter — the one CI calls the normal build — was the unsafe one. The no-adapter build was fine.

---

# 3. Scope boundary

## 3.1 10E decides

- where and when the store is opened, and that it happens once per process,
- the store status and recovery reasons as core types,
- the order of integrity checks, migration and the full check,
- how each failure maps onto `UXIA-v0`'s cross-cutting states,
- which actions a health state offers,
- the export and verified atomic restore **mechanism**,
- that the AI adapter reports itself unavailable instead of throwing.

## 3.2 10E does not decide

- Profile controls for export and restore, and whether a replaced profile is kept aside → 16D,
- detecting whole-profile loss → 19B,
- `empty_valid` content → 11; `offline_local_available` → 14; `loading_projection` / `recomputing_projection` → 12,
- startup and integrity-check budgets → 18E,
- final microcopy → 14,
- any accepted semantic, state, tone, persistence rule, schema, or port.

No startup time, check duration or database size is claimed.

---

# 4. Startup

**The store belongs to the process, not to an activity.** `CoachApplication` creates a `StoreStartup` once and starts it; activities only observe. Rotation or a theme change recreates the activity and reopens nothing.

`StoreStartup` lives in `core-application`, so its guarantees are JVM tests rather than things noticed on a phone:

- **the open never runs on the caller's thread** — proven with an opener held on a latch: a synchronous `start()` would hang the test instead of observing `Opening`, and the thread that ran the opener is asserted to differ from the caller,
- **the status is `Opening` until an outcome exists**, and no store is handed out before then,
- **`start()` is idempotent**, and `recheck()` is ignored while opening or once ready,
- **an opener that throws becomes `RecoverableFailure`**, never a crash; an opener that reports `Ready` without a store is not believed,
- results reach listeners only through the delivery executor (the main thread on Android).

The database path is resolved inside the background opener too, because resolving it touches the filesystem. Debug builds enable StrictMode disk-read and disk-write detection with a log penalty, so a regression that moves store work back to the main thread shows up in logcat.

No new port was added. `MSBX-v0`'s four ports are unchanged.

---

# 5. Store status

`StoreStatus` and `RecoveryReason` are `core-model` types:

| Status | Meaning |
|---|---|
| `Opening` | being opened, checked and migrated |
| `Ready` | integrity checked, schema current |
| `RecoveryRequired(reason)` | continuing could put evidence at risk |
| `RecoverableFailure` | an operational failure before any data could be read |

| Reason | When |
|---|---|
| `INTEGRITY_CHECK_FAILED` | the file is not a readable database, or `quick_check`, `integrity_check` or `foreign_key_check` found a problem |
| `NEWER_SCHEMA` | written by a newer build; downgrade is not supported |
| `MIGRATION_INCOMPLETE` | a forward migration could not complete; the previous state was left intact |

`Migrations.DataRecoveryRequired` now carries its reason. **The reason travels as a type; nothing parses an exception message**, because messages change between SQLite versions and a misparse would look like a diagnosis.

The line between the two failure statuses is where integrity comes into question: if the file cannot be opened at all, nothing was read and a retry is meaningful (`error_recoverable`); if anything fails while reading or checking, integrity is exactly what is uncertain (`data_recovery_required`).

---

# 6. Opening: check before anything writes

`StoreOpener.open` is the product's only way to open the store. It never throws, and its order is the point:

1. **`PRAGMA quick_check` and `PRAGMA foreign_key_check`** — before anything can write, so a corrupted file is reported exactly as it was found rather than "repaired" by a migration,
2. **forward migration** — refused for a newer schema, all-or-nothing otherwise (`LDBX-v0`),
3. **`PRAGMA integrity_check` if a migration ran** — the full check, which also verifies index content against table content, because a migration is the one moment this build rewrote the file.

`quick_check` is used on every open because it is the check that scales with years of history; the full check is used where the file was just rewritten and on every restore candidate. The difference is not assumed — §9 shows a fixture only the full check can see.

**No failure path deletes, truncates or recreates the file.** Every recovery case is tested by comparing the database file **byte for byte** before and after the refused open, and by checking that no journal or WAL sidecar was created. A test that only asserted the status would pass an opener that reported recovery and then quietly started from an empty database.

The shapes tested: a file that is not a database, a truncated database, a corrupted b-tree page behind a valid header, a foreign-key violation written behind the engine's back, an index whose content no longer matches its definition, a newer schema, and a migration that fails after it has already changed something.

---

# 7. What the learner sees

`core-presentation` computes `AppHealth.of(storeStatus, evaluatorAvailability)`; `app-ui` renders it.

The vocabulary is `UXIA-v0`'s six cross-cutting states, in its order, with `VDSX-v0`'s tones:

| State | Tone | Produced in 10E |
|---|---|---|
| `loading` | neutral | while opening |
| `empty_valid` | neutral | no — 11 |
| `error_recoverable` | system_fault | open failed before reading |
| `offline_local_available` | neutral | no — 14 |
| `ai_unavailable_core_available` | neutral | ready store, evaluator unavailable |
| `data_recovery_required` | system_fault | any recovery reason |

Rules:

- **Precedence follows `THUX-v0`:** `data_recovery_required`, then `error_recoverable`, then `loading`, then normal use.
- **The shell is shown exactly when normal use is available.** Destinations drawn over a store that is not open would show content that is not true.
- **`ai_unavailable_core_available` is produced only when the core actually works**, and never blocks. Its name is a claim about the core.
- **Every state is said in words**, rendered through the state chip, announced through a polite live region, and every recovery reason has its own sentence. The recovery screen says that nothing was deleted, reset or recreated, and that restore controls are not in this build yet.

**`HealthAction` has exactly one value: `RECHECK`**, which re-runs the same non-mutating open. There is no reset, wipe, delete or recreate value, so a screen that offers to "start fresh" over uncertain data cannot be written. An unsilent button that resets progress is still a reset.

---

# 8. AI absence

`AiEvaluator.evaluate` returns `evaluation_pending(unavailable)` until 14 gives it call sites, and reports itself `UNAVAILABLE`. Both evaluator source sets report availability, so both builds show `ai_unavailable_core_available` alongside a working core — which is the truth in 10E.

---

# 9. Backup, export and restore — the mechanism

This is the user's decision in 10E: **the mechanism is built and verified here; the Profile controls belong to 16D.**

**Format.** An archive is a SQLite database of the product's own schema. It is complete by construction — truth, exposure, provenance, curriculum pins, and schema and policy version all live in the store — and there is no second serialisation to drift from the schema. No credential can be in it: the key lives in platform secure storage, and a test enumerates every archive column to confirm none could hold one.

**Export** uses `VACUUM INTO`, which SQLite produces from a single read transaction, so an archive never holds half of a learner action. The archive is then **verified exactly as a restore would verify it**, so a backup that could not be restored is discovered when it is made, not on the day it is needed. It never overwrites an existing file.

**Restore:**

1. the archive is **copied** to a staging file beside the live database — the archive itself is never opened, so the user's backup cannot be damaged by the attempt,
2. the staging copy gets the full integrity and foreign-key check, must carry a schema version (else `NOT_A_PROFILE_ARCHIVE`), is refused if newer (`NEWER_SCHEMA`), is forward-migrated if older and checked again, and must contain every table of the profile schema,
3. only then is the live file replaced by **one atomic rename**,
4. a journal left beside the old live file is moved aside first and put back if the rename fails — otherwise SQLite would replay the old database's journal into the restored one,
5. the result is reopened through `StoreOpener`, which is the integrity check after restore.

A refused archive leaves **both the live profile and the archive byte-for-byte unchanged** and removes the staging copy. There is no merge.

---

# 10. Mutation testing

Sixteen deliberate mutations were applied to the implementation, and **all sixteen were caught** — but not all at the first attempt, and the record says so:

- **M08 (old journal carried over) survived at first.** The test wrote a garbage journal; SQLite never treats an invalid header as hot, so removing the set-aside still passed. The test now captures a real crash image mid-transaction and proves, as a precondition, that its journal is replayed.
- **M09 (incomplete profile schema accepted) survived at first.** The only not-a-profile test had no metadata table, so an earlier check refused it and the table-set check was never exercised. A new test uses an archive with intact metadata and integrity but no `exposure_record` table — restoring it would silently lose every exposure record.
- **M02's first version did not compile**, which is not a detection. It was rewritten to compile and rerun.
- Before mutation, reviewing the claims found that nothing proved the post-migration full check; the index-content fixture was added, and M04 is caught by it.

The sixteen: no integrity check before migration; silent reset on an unreadable store; unreadable file called recoverable; no full check after migration; restore without verification; archive migrated in place; newer archive not refused; old hot journal carried over; incomplete profile schema accepted; open on the caller's thread; start not idempotent; opener defect crashes; AI context claimed without a working core; shell drawn over recovery; reset action added; AI adapter throws again.

---

# 11. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS — 46 tests |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Not run: T6.** The Poco M6 Pro was not connected during 10E. Nothing here claims that the app reaches `Ready` on the device, that the recovery screen renders or is announced by TalkBack there, that StrictMode stays quiet on the device's main thread, or that the atomic rename behaves on the device filesystem. A real process kill in the middle of a restore was not tested; the set-aside and rename ordering is reasoned, not crash-tested.

---

# 12. Anti-patterns explicitly rejected

- opening the store on the main thread, or on every activity recreation,
- crashing on `data_recovery_required`,
- a reset or start-fresh action on a recovery screen,
- migrating before checking integrity,
- classifying a failure by parsing its message,
- drawing the shell over a store that is not ready,
- claiming `ai_unavailable_core_available` while the core is not available,
- an AI adapter that throws where `AIAX-v0` says unavailable,
- restoring by merging,
- verifying or migrating the archive in place,
- replacing the live profile before verification,
- leaving the old journal beside a restored profile,
- a backup that is not verified when made,
- asserting "nothing was reset" without comparing bytes,
- claiming a device result that was not run,
- adding a port for startup.

---

# 13. 10E acceptance contract

1. `APHX-v0` is the accepted app health model.
2. The store opens once per process, never on the caller's thread, and this is a JVM test.
3. Store status and recovery reasons are core types; the reason travels as a type.
4. Integrity is checked before migration, and fully after a migration; the difference is proven by a fixture.
5. Every store failure is a state; none is a crash.
6. No failure path deletes, truncates, recreates or merges data, proven byte for byte.
7. The six cross-cutting states and their tones equal `UXIA-v0` and `VDSX-v0`; precedence follows `THUX-v0`.
8. The shell is shown exactly when normal use is available; AI unavailability never blocks and is claimed only with a working core.
9. The only health action re-runs a non-mutating open; no destructive action is representable.
10. The AI adapter degrades to `evaluation_pending(unavailable)`.
11. Export is verified when made; restore verifies a copy, refuses newer, foreign, incomplete and corrupted archives, replaces atomically and never merges.
12. Controls for export and restore belong to 16D; whole-profile loss detection to 19B.
13. 16/16 mutations are caught, and the tests strengthened to catch them are recorded.
14. `MSBX-v0`'s four ports and `LDBX-v0`'s schema are unchanged.
15. Nothing is claimed that was not run; T6 was not run.
16. Independent 10E QA must pass, and Stage 6, 7, 8, 9 and 10A–10D regressions must pass.

---

# 14. Handoff after acceptance

If accepted, 10E becomes `APHX-v0 / D-086` and **AŞAMA 10 is complete**.

Next numbered step: **11A — Today ekranı**. It must receive a fresh PRE-STEP and explicit user approval before execution.
