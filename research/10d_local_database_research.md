# 10D Local Database — Research & Decision Synthesis

**Stage step:** 10D — Local database  
**Purpose:** Turn `DDM-v0`'s physical schema into a real SQLite database whose guarantees the storage engine enforces, and prove each one by attempting to break it.

## 1. Research-need decision

**No web research pass was needed; the library API was read from the artifact itself.** 10A pinned `androidx.sqlite` 2.7.0 with the bundled driver. Documentation pages for the driver did not render, and writing calls from memory would have repeated exactly the mistake 10A caught twice. So the resolved jars were located through Gradle and inspected with `javap`, which gives the real signatures: `BundledSQLiteDriver().open(path): SQLiteConnection`, `SQLiteConnection.prepare(sql): SQLiteStatement`, the `bind*`/`get*`/`step()` statement methods and the `execSQL` extension.

## 2. Canonical source set reviewed

- `DDM-v0 / D-077` — three store regions; 11 curriculum entities, 12 truth entities, 8 projection entities; `(logical_id, version)` composite identity with version-carrying foreign keys and no curriculum→user foreign key; the four evidence axes with their allowed value sets; three-value time on every timestamped row, with the offset in **minutes**; a monotonic truth sequence used as the projection watermark; projection provenance fields; a metadata table for schema and policy version; the index intent.
- `LFPS-v0 / D-076` — append-only truth, one action one transaction, forward-only migration that never rewrites evidence, failure leaving the previous state intact and surfacing `data_recovery_required`, refusal of a newer schema.
- `TVSX-v0 / D-081` — every prohibition verified by attempting it at the layer that forbids it; migration verified against populated fixtures; no fake standing in for the guarantee under test; mutation discipline.
- `MPSX-v0 / D-082` — the bundled driver chosen precisely so T2 could run on the JVM.

## 3. What the first draft got wrong

The first draft of this step's schema was written from the design memory of earlier steps rather than from the contract, and it diverged from `DDM-v0` in ways a green test suite did not reveal — because the tests had been written against the same draft:

- the evidence **outcome** axis used `met / partially_met / not_met / not_reliably_measured`, which is the evaluator *signal* enum introduced at 10A, not the evidence outcome axis `DDM-v0` defines (`positive / negative / partial / invalid`),
- `evaluator_status` lacked `invalid`, and `independence_class` lacked `practice_only` and `requires_independent_recheck`,
- the UTC offset was stored in **seconds**; `DDM-v0` says minutes,
- evidence was keyed to a single objective; `DDM-v0` keys it to a Skill with plural pinned objective references,
- four truth tables existed where `DDM-v0` names twelve,
- time columns used invented `recorded_*` names.

What exposed it was cross-reading the draft against `data_model.yaml` while writing this step's validator. The schema was rewritten to the contract before anything was accepted. The draft's divergences are recorded in the contract rather than quietly erased, because the lesson — a suite written against the same draft cannot catch the draft's misreading of the contract — is the durable part.

## 4. Synthesis problems 10D actually has to solve

1. **Append-only has to be the engine's refusal, not the adapter's restraint.** An adapter without an update method still leaves every other code path, and every future one, free to issue an UPDATE.
2. **A second column list drifts.** If the adapter keeps its own map of required columns, the schema and the map disagree the first time one changes and the other does not.
3. **An offset conversion can lose information silently.** The core time type carries seconds; storage carries minutes. Truncating would be invisible.
4. **A watermark has to see every kind of truth.** A per-table id cannot tell a projection that an exposure arrived after it was built.
5. **A migration test that fails too early proves nothing.** If a sabotaged migration fails before changing anything, a missing rollback still looks correct.
6. **A JVM build succeeding says nothing about the device.** The data module is a JVM library consumed by an Android app; the wrong artifact variant would build and then crash on the phone for want of a native library.

## 5. Positions taken

- **BEFORE UPDATE and BEFORE DELETE triggers that abort** on every truth table and every curriculum table, generated from the table inventories so no table can be left out.
- **The adapter asks SQLite for each table's columns** and derives what is required from the DDL itself.
- **A non-whole-minute offset is refused**, never truncated. Every real zone offset is a whole number of minutes, so refusal only ever fires on corrupt input.
- **One global truth sequence** across all truth tables is the watermark.
- **`evidence_event_objective`** stores the plural objective references relationally, so each keeps its own version pin and `evidence_by_objective` is indexable. It is not a new entity.
- **Where `DDM-v0` names an entity but not its fields**, only identity, sequence, time and DDM-implied references are fixed, plus the smallest content column the row needs — each disclosed with the step that owns completing it.
- **The migration failure is injected after the step has already changed something**, so only a real rollback can pass.
- **The Android variant and the arm64-v8a native library are checked inside the built APK**, not assumed from a successful build.

## 6. Explicitly not decided in 10D

Engines that compute projections (12), curriculum content loading (11), backup/export and atomic verified restore implementation, moving the database open off the main thread and surfacing `data_recovery_required` as a state (10E), assessment-session fields (13), and index tuning or query budgets (18E).

No row-count, latency or database-size claim is made.
