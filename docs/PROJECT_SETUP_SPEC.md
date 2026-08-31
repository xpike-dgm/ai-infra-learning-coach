# Mobile Project Skeleton Specification — MPSX-v0

**Stage step:** 10A — Proje kurulumu  
**Status:** ACCEPTED — independent 10A QA PASS  
**Decision:** `D-082`  
**Model:** `MPSX-v0 — Mobile Project Skeleton`  
**Boundaries:** `MSBX-v0 / D-078`  
**Verification strategy:** `TVSX-v0 / D-081`  
**Platform:** `AMTS-v0 / D-075`  
**Distribution scope:** `D-080`

## 1. Purpose

10A builds **the first executable thing in this repository**: a project in which `MSBX-v0`'s boundaries and `TVSX-v0`'s tiers are commands that run rather than statements in a document.

It answers one primary question:

> **Kabul edilmiş sınırları ve doğrulama katmanlarını çalıştırılabilir kılan somut proje nedir?**

Primary invariant:

> **Nothing here is claimed that was not run.** Every version was read from a current source, every structural rule fails a real build, and every result recorded below came from executing the command.

---

# 2. Why this step is different

Every step from 1A to 9F produced specifications validated against each other. 10A is the first step whose output a machine executes, which changes what "verified" means: a build either succeeds or it does not, and no amount of internal consistency substitutes for that.

It showed immediately. Two things this step would have asserted from memory were wrong, and the build found both:

- the AGP 9.3.0 compatibility table was read as requiring **Gradle 9.5.0**; no such distribution resolves and the wrapper failed with a 404. The pin is now Gradle 9.7.1, the version Gradle's own release service reports as current.
- the Android modules initially applied **`org.jetbrains.kotlin.android`**. AGP 9.0+ supplies Kotlin support built in and *rejects* the standalone plugin outright.

Both are recorded rather than quietly corrected, because they are the evidence that currency is verified here, not remembered.

---

# 3. Scope boundary

## 3.1 10A decides

- where the Gradle project lives and how the ten `MSBX-v0` modules map onto it,
- the pinned toolchain and platform levels,
- how the dependency rule fails the build,
- how the product builds **without** the AI adapter,
- the target device, closing the item `AMTS-v0` §8.1 left open,
- the six-item `AMTS-v0` §9 verification list,
- which dependencies are deliberately absent and why,
- credential hygiene in the repository,
- the CI jobs that run the tiers that can run without a device.

## 3.2 10A does not decide

- the navigation graph and destinations → 10B,
- design-system implementation and the Compose theme → 10C,
- physical schema, DDL, migrations and content loading → 10D,
- app health and diagnostics → 10E,
- AI call sites, prompt text and tutor UX → 14,
- performance budget calibration → 18E,
- any accepted semantic, persistence rule, data model, boundary, AI integration or verification decision.

No build-performance claim, APK-size claim, or claim that any pinned version is the newest that will ever exist is canonical in 10A.

---

# 4. Toolchain

Verified on **2026-08-31**. Per `AMTS-v0`, **currency is verified, not asserted**: these are configuration values, not canonical claims, and they carry the date they were read.

| Component | Pin |
|---|---|
| Android Gradle Plugin | 9.3.0 |
| Gradle | 9.7.1 |
| Kotlin | 2.4.0 |
| Compose BOM | 2026.08.00 (Compose 1.12.0 / Material3 1.4.0) |
| Compose Material 3 Adaptive | 1.3.0 |
| androidx.sqlite | 2.7.0 |
| JVM toolchain | 17 |
| `minSdk` / `targetSdk` / `compileSdk` | 26 / 36 / 37 |

`compileSdk = 37` is forced: Compose 1.12 compiles against API 37. `targetSdk = 36` is the target device's own level. `minSdk = 26` is `AMTS-v0` §8.1's policy — the **lowest** level that needs no weakening shim, which is exactly where `java.time` becomes native and the retention and review timing code stops needing desugaring.

---

# 5. Target device — the open item from `AMTS-v0` §8.1

| Field | Value |
|---|---|
| Device | **Poco M6 Pro** |
| Model | `2312FPCA6G` |
| Android | 16 → **API 36**, build `BP2A.250605.031.A3` |
| OS | HyperOS `3.0.304.0.WNFMIXM.C10` |
| Kernel | `6.12.30-android16-5-g6e872b4863d6-ab13847919-4k` |
| SoC / memory | Helio G99-Ultra, 8 × 2.2 GHz / 12 + 6 GB |

`AMTS-v0` §8.1 said the target device "is not yet recorded in this repo" and made `minSdk` conditional on it. It is recorded here. Under `D-080` this is the **single** target device: `TVSX-v0` T6 and V1 release criterion 9 mean this phone, not a matrix. API 36 ≥ `minSdk` 26, so the policy holds against the real hardware.

---

# 6. The `AMTS-v0` §9 verification list — all six closed

1. **Adaptive navigation.** `WindowSizeClass` from `androidx.compose.material3.adaptive:adaptive`; `NavigationSuiteScaffold` from `androidx.compose.material3:material3-adaptive-navigation-suite`, which selects the navigation presentation from the window size class at runtime; pane scaffolds from `adaptive-navigation`. `WFPX-v0`'s geometry stays canonical — the library supplies the mechanism, not the layout decisions.
2. **Disabling dynamic colour.** Dynamic colour is opt-in: it exists only where `dynamicLightColorScheme()` / `dynamicDarkColorScheme()` are called. Disabling it means never calling them and building the theme from `lightColorScheme()` / `darkColorScheme()` with the `WFPX-v0` measured tokens. Because that is an absence, it is enforced by a source scan rather than by discipline.
3. **`minSdk`.** 26, confirmed against the recorded device.
4. **Screen-reader semantics.** `Modifier.semantics` with `stateDescription`, `contentDescription`, `heading()` and live regions; traversal order via `isTraversalGroup` and `traversalIndex`. These carry `SPWX-v0`'s state text and `VDSX-v0`'s rule that state is never conveyed by colour alone.
5. **Reduced motion.** Android exposes no dedicated flag; `Settings.Global.ANIMATOR_DURATION_SCALE` reads `0f` when animations are disabled. Read in one place so `VDSX-v0`'s motion rules do not scatter `Settings` lookups through the UI.
6. **Compatibility library at `minSdk` 26?** **None.** `java.time` is native from 26, `Settings.Global` predates it by far, Compose semantics and the adaptive APIs are library-level, and `androidx.sqlite` needs API 23.

---

# 7. Module layout

The Gradle project lives in `android/`. The ten modules are exactly `MSBX-v0`'s, and each module's declared project dependencies are exactly that contract's `depends_on` — checked mechanically, so the build and the accepted boundary cannot drift.

| Module | Gradle kind |
|---|---|
| `core-model`, `core-ports`, `core-engines`, `core-application`, `core-presentation` | Kotlin JVM |
| `data-persistence`, `data-curriculum` | Kotlin JVM |
| `ai-adapter` | Kotlin JVM |
| `app-ui` | Android library |
| `app-wiring` | Android application |

Only the two `app-*` modules are Android modules. That is deliberate: keeping `data-*` and `ai-*` off the Android plugin is what lets `TVSX-v0` tier T2 run against a real storage engine — and the adapter's outcome handling run against recorded responses — **off-device**.

---

# 8. The dependency rule fails the build

`MSBX-v0` calls the rule "checkable, not aspirational". `TVSX-v0` T3 requires it to fail at build time. `verifyModuleBoundaries` in the root build does that, and it is wired into `check` so it is not optional.

It computes, rather than assumes:

- every module sits in a known layer (`core-` / `data-` / `ai-` / `app-`),
- no forbidden edge exists — `core` may never reach `data`, `ai` or `app`,
- only `:app-wiring` reaches every layer,
- the graph is acyclic, found by a coloured depth-first search.

No architecture-rule library is used. The rule is fully expressible over Gradle's own project dependencies, and a third-party analyser would add a currency risk for nothing.

**Run:** `./gradlew verifyModuleBoundaries` → *10 modules, no forbidden edge, no cycle.*

**Mutation-tested.** Declaring `core-model → data-persistence` made the build fail with the forbidden edge *and* the cycle it creates. The rule was then restored. While reading that output the check's own cycle **reporting** was found to be wrong — after the first cycle the traversal stack is no longer a faithful path, so later chains printed were misleading. It now reports the first cycle only.

---

# 9. Building without the AI adapter

`MSBX-v0` requires the product to build and run with no adapter present, and `TVSX-v0` refuses a stub as sufficient. The seam is a source-set selection, not a runtime branch:

- `:ai-adapter` is conditionally included in `settings.gradle.kts`,
- `app-wiring/src/withAi/kotlin` provides the adapter-backed evaluator,
- `app-wiring/src/withoutAi/kotlin` provides `NullEvaluator`, which ships in `core-application` and is **not** a test fixture.

**Run:** `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` → BUILD SUCCESSFUL, with 9 modules configured instead of 10. An open-ended attempt on that path becomes `evaluation_pending` and writes no evidence; nothing deterministic degrades.

This is V1 criterion 8 satisfied by wiring rather than by hope, and it is demonstrable in one command.

---

# 10. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew verifyModuleBoundaries` | T3 | PASS — 10 modules, no forbidden edge, no cycle |
| RUN-02 | `./gradlew :core-model:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-03 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS — `app-wiring-debug.apk` produced |
| RUN-04 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS — V1 criterion 8 |

The first tests are small on purpose and already guard real contracts: a reference always carries a version, a timestamp carries instant, study day and offset, there are exactly eight Skill presentation states with a declared precedence, and an absent evaluator yields `evaluation_pending` rather than a verdict.

---

# 11. Dependencies deliberately absent

- **No DI framework.** `app-wiring` is a hand-written composition root. One object graph, one user, and a core that must stay free of framework annotations. A framework would add annotation processing across ten modules for nothing.
- **No ORM.** `DDM-v0` requires a library-neutral physical schema; `TVSX-v0` requires append-only to be enforced by the storage layer and proven by attempted UPDATE and DELETE. Both point at hand-written SQL and triggers. `androidx.sqlite` with the bundled driver is the choice, and the bundled driver is what lets T2 run on the JVM.
- **No architecture-rule library.** `verifyModuleBoundaries` needs no dependency and already fails the build.
- **No HTTP client.** `AIAX-v0`'s adapter has no call sites until 14. Nothing speculative is declared.
- **No `org.jetbrains.kotlin.android`.** AGP 9.0+ supplies Kotlin and rejects it.

---

# 12. Credentials in the repository

Under `D-080` the product is personal and undistributed, and `AIAX-v0`'s key-storage invariant is untouched: the key lives in platform secure storage and never enters the repository.

`.gitignore` covers `android/local.properties`, `android/secrets.properties`, `*.keystore` and `*.jks`. The reason is narrow and concrete: git history is permanent even in a private repository, and a leaked key is billed to the owner. This is one `.gitignore` rule, not a security programme.

---

# 13. CI

`.github/workflows/android.yml` runs T3, then T1, then both the no-adapter and the with-adapter builds, and separately runs the repository's standing validator sweep over the **full** `tools/validate_*.py` glob — never a hand-maintained subset.

T6 is deliberately absent from CI. The device tier runs on the Poco M6 Pro by hand; presenting an emulator job as the device tier would make the release gate claim something it does not check.

---

# 14. Anti-patterns explicitly rejected

- naming a library version without verifying it,
- declaring a dependency the skeleton does not use,
- applying the Android plugin to a core module,
- calling the dynamic colour functions anywhere,
- enforcing the dependency rule by convention rather than by the build,
- stubbing the AI adapter instead of building without it,
- computing presentation state inside `app-ui`,
- reading the system clock anywhere but the `ClockPort` implementation,
- committing an API key or a keystore,
- claiming a build result that was not run,
- treating an emulator run as the device tier.

---

# 15. 10A acceptance contract

1. `MPSX-v0` is the accepted project skeleton and toolchain pin set.
2. Nothing is claimed that was not run; the two corrections the build forced are recorded.
3. The Gradle project lives in `android/` and holds exactly `MSBX-v0`'s ten modules.
4. Every module's declared project dependencies equal that contract's `depends_on`.
5. Only `app-*` modules apply an Android plugin, so T2 and adapter verification stay off-device.
6. `verifyModuleBoundaries` fails the build on a forbidden edge, a cycle or an unknown module, and is mutation-tested.
7. The product builds without `:ai-adapter`, selecting the shipped null evaluator; V1 criterion 8 is wiring.
8. The target device is recorded, closing the `AMTS-v0` §8.1 open item; `minSdk` 26 holds against it.
9. All six `AMTS-v0` §9 items are answered from current sources.
10. Dynamic colour appears nowhere, and its absence is checked by a source scan.
11. The system clock is read in exactly one place, the `ClockPort` implementation.
12. No DI framework, ORM, HTTP client or architecture-rule library is declared, each for a recorded reason.
13. No key, keystore or local property file can enter the repository.
14. CI runs T3, T1 and both builds, plus the full validator glob; it does not pretend to run T6.
15. 10A changes no accepted semantic, boundary, persistence rule, data model, AI integration or verification decision.
16. Independent 10A QA must pass, and Stage 6, Stage 7, AŞAMA 8 and AŞAMA 9 regressions must pass.

---

# 16. Handoff after acceptance

If accepted, 10A becomes `MPSX-v0 / D-082`.

Next numbered step: **10B — Navigation**. 10B builds the `UXIA-v0` four-destination shell on the adaptive APIs pinned here, with `WFPX-v0`'s three window classes and geometry, and `SPWX-v0`'s state text carried through the semantics APIs named in §6. It must receive a fresh PRE-STEP and explicit user approval before execution.
