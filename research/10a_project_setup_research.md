# 10A Project Setup — Research & Decision Synthesis

**Stage step:** 10A — Proje kurulumu  
**Purpose:** Pin the concrete toolchain, module layout, dependency-rule enforcement and CI that make `MSBX-v0`'s boundaries and `TVSX-v0`'s tiers executable, and close the six-item verification list `AMTS-v0` §9 handed to this step.

**Verification date:** 2026-08-31. Every version below was read from a current source on that date and is recorded with it. Per `AMTS-v0`, currency is **verified, not asserted**; these pins are configuration values, not canonical claims, and must be re-verified when they are next changed.

## 1. Research-need decision

Unlike 9B–9F, this step genuinely required external data: `AI_AGENT_WORKFLOW.md` §3 assigns "framework/library currency" to research, and `AMTS-v0` §9 left six items explicitly unresolved because they depend on the current ecosystem rather than on accepted contracts. None of them can change the `AMTS-v0` §4 selection; they determine how it is implemented.

The target device was also required and was previously unrecorded in the repo. It is now recorded (§3).

## 2. Toolchain versions verified

| Component | Pinned | Source |
|---|---|---|
| Android Gradle Plugin | 9.3.0 (July 2026) | AGP 9.3.0 release notes |
| Gradle | 9.5.0 | AGP 9.3.0 compatibility table (minimum **and** default) |
| JDK | 17 minimum; 21 present locally | AGP 9.3.0 compatibility table |
| SDK Build Tools | 36.0.0 minimum | AGP 9.3.0 compatibility table |
| Kotlin | 2.4.0 (11 August 2026) | Kotlin release documentation |
| Compose BOM | 2026.08.00 → Compose 1.12.0, Material3 1.4.0 | Compose August '26 release post + BOM mapping |
| Compose Material 3 Adaptive | 1.3.0 (12 August 2026) | compose-material3-adaptive release page |
| androidx.sqlite | 2.7.0 (1 July 2026), Android minSdk 23 | androidx.sqlite release page |

**Consequence recorded:** Compose 1.12 compiles against **API 37**, so `compileSdk` must be 37 and AGP ≥ 9.1.1. AGP 9.3 supports API 37 as its maximum. The local SDK currently holds platforms 34–36.1, so platform 37 must be present before the first build; AGP downloads it when licences are accepted, otherwise it is a one-time SDK Manager install. This is a **build prerequisite, not a product decision**, and it is recorded rather than hidden.

## 3. Target device — now recorded

- **Poco M6 Pro**, model `2312FPCA6G`
- **Android 16**, build `BP2A.250605.031.A3` → **API 36**
- HyperOS `3.0.304.0.WNFMIXM.C10`, kernel `6.12.30-android16-5-g6e872b4863d6-ab13847919-4k`
- Helio G99-Ultra, 8 cores @ 2.2 GHz, 12 + 6 GB RAM

This is the single target device under `D-080`. `TVSX-v0` T6 and V1 release criterion 9 mean this device, not a matrix.

## 4. The six-item `AMTS-v0` §9 list — closed

### 4.1 Adaptive navigation APIs for the three window classes
`androidx.compose.material3.adaptive:adaptive` supplies `WindowSizeClass` computation; `androidx.compose.material3:material3-adaptive-navigation-suite` supplies `NavigationSuiteScaffold`, which selects the navigation presentation from the current window size class and changes it at runtime. `adaptive-navigation` adds the pane scaffolds for list-detail surfaces. This maps directly onto `WFPX-v0`'s three window classes without a custom breakpoint mechanism.

**Adopted:** window class comes from `WindowSizeClass`; navigation presentation comes from `NavigationSuiteScaffold`. `WFPX-v0`'s geometry stays canonical — the library supplies the mechanism, not the layout decisions.

### 4.2 Disabling dynamic colour and supplying a fixed palette
Dynamic colour in Compose Material 3 is **opt-in**: it exists only where `dynamicLightColorScheme()` / `dynamicDarkColorScheme()` are called. Disabling it therefore means never calling them, and building the theme from `lightColorScheme(...)` / `darkColorScheme(...)` populated with the `WFPX-v0` measured tokens, passed to `MaterialTheme(colorScheme = …)`.

**Adopted:** neither dynamic function may appear anywhere in the project. This is checkable by a source scan rather than by discipline, so 10A makes it a check.

### 4.3 `minSdk` against the actual device
`AMTS-v0` §8.1 defines `minSdk` as the **lowest** API level that supports the required accessibility, locale, adaptive-layout and date/time behaviour without shims that weaken any of them — with API 26 as the working default because `java.time` is native from that level, which matters for retention and review timing.

The device runs API 36, so it satisfies any `minSdk` at or below 36. Nothing in §4.1–§4.5 below requires a level above 26.

**Adopted:** `minSdk = 26`, now confirmed against the recorded device. `targetSdk = 36` (the device's own level). `compileSdk = 37` (forced by Compose 1.12). Raising `minSdk` toward 36 would be preference, not policy, and §8.1 asks for the lowest level that needs no weakening shim.

### 4.4 Screen-reader semantics APIs
State text is exposed with `Modifier.semantics { stateDescription = … }` alongside `contentDescription`, `heading()` and live-region announcements. Traversal order is controlled with `isTraversalGroup` on the grouping node and `traversalIndex` (lower first) on children.

**Adopted:** these APIs carry `SPWX-v0`'s state text and `VDSX-v0`'s requirement that state never be conveyed by colour alone. They are Compose-level, so no platform API level is implied.

### 4.5 Reduced-motion detection
Android exposes no dedicated reduced-motion flag; the recommended signal is `Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE)`, which reads `0f` when the user has disabled animations. Available far below API 26.

**Adopted:** a single platform reader behind an app-level accessor, so `VDSX-v0`'s motion rules can be honoured without scattering `Settings` reads through the UI.

### 4.6 Compatibility library needed at `minSdk = 26`?
**No.** `java.time` is native from 26 (this is precisely why 26 was the working default, and it removes core-library desugaring from the retention/review code). `Settings.Global.ANIMATOR_DURATION_SCALE` predates 26 by a wide margin. Compose semantics and the adaptive APIs are library-level and independent of platform level. `androidx.sqlite` requires Android API 23. No accessibility or locale behaviour named in `AMTS-v0` §6–§7 needs a compatibility shim at 26.

## 5. Decisions this step had to make beyond the list

### 5.1 No DI framework — `app-wiring` wires by hand
`MSBX-v0` makes `app-wiring` the only module that knows every implementation and forbids domain logic inside it. A DI framework would add annotation processing across all ten modules and pull generated Android-aware code toward the core, for a single-user app with exactly one object graph. Manual constructor injection in a composition root keeps the graph readable, keeps `core-*` free of any framework annotation, and adds no build step.

**Adopted:** manual constructor injection. No DI library is declared.

### 5.2 `androidx.sqlite` with the bundled driver and hand-written SQL — no ORM
Three accepted requirements point the same way. `DDM-v0` requires a library-neutral physical schema with composite primary keys and version-carrying foreign keys. `TVSX-v0` requires append-only to be **enforced by the storage layer** and proven by attempted UPDATE/DELETE, which is a trigger-level concern best written directly. And `TVSX-v0` T2 must run against a real storage engine **off-device**, which `sqlite-bundled` supports on the JVM.

**Adopted:** `androidx.sqlite` 2.7.0 with `sqlite-bundled` for JVM execution and hand-written SQL, DDL and migrations. No ORM is declared. Schema authoring itself belongs to 10D.

### 5.3 The dependency rule is enforced by the build, not by a third-party analyser
`TVSX-v0` T3 requires the dependency rule to fail at **build time**. A third-party architecture-rule library would add a currency risk and another dependency for a rule that is fully expressible over Gradle's own project dependencies. A verification task that reads each module's declared project dependencies and fails on a forbidden edge, a cycle or an undeclared module needs no dependency at all and runs in every build.

**Adopted:** a `verifyModuleBoundaries` Gradle task, plus the repo-level validator that checks the same rule against `boundaries.yaml` so the build and the accepted contract cannot drift apart.

### 5.4 No speculative dependencies
Nothing is declared that the skeleton does not use: no coroutines, no serialization, no networking, no image loading, no analytics. `AIAX-v0`'s adapter has no HTTP client yet because it has no call sites yet.

## 6. Explicitly not decided in 10A

Navigation graph and destinations (10B), design-system implementation and Compose theme population (10C), physical schema, DDL and migrations (10D), app health and diagnostics (10E), feature code (11–18), evaluator calibration (18) and performance budgets (18E).

No claim is made here about build performance, APK size, or that any pinned version is the newest that will ever exist — only that each was current on the recorded verification date.
