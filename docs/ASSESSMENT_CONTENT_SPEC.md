# Assessment Content Specification — ACNX-v0

**Stage step:** 15G — Assessment content  
**Status:** ACCEPTED — independent 15G QA PASS  
**Decision:** `D-120`  
**Model:** `ACNX-v0 — Assessment content supplement`  
**Behaviour it implements:** `QAB-v0` §§8, 14, 17, 22; `MCA-v0` §§5–9 (§6.5 cross-topic transfer opportunities); `AIV-v0` §16; `WAAX-v0`; `GRE-v0`; `PDT-v0` §8.1 / `PBR-v0` (a declared trigger extension); `LFPS-v0` (a published version is never overwritten); `D-113` (incremental packages); `CDEX-v0`  
**Content:** `curriculum/content/15g_assessment/` → `android/app-wiring/src/main/assets/curriculum_package_v7.txt` (generated; never edit it by hand)  
**Persistence / ports / schema:** unchanged

## 1. Purpose

The six content packages ship with the items each lesson needs, but only five to eight outside the lesson per Objective. Every later check, review and repair needs an item the learner has not seen. 15G answers:

> **Yayımlanmış bir Objective'e, yayımlanmış hiçbir şeyi değiştirmeden, öğrencinin görmediği ve gerçekten aynı yeteneği ölçen yeni item'lar nasıl eklenir?**

Primary invariant:

> **An added item measures its Objective in a structure the learner has not seen, in a form that can produce the evidence the Objective requires, and is shipped only after an independent reviewer passed it.** A supplement adds items, new task versions, transfer items and wrong-option misconception keys; it never overwrites anything published. A transfer claim is checked, never assumed, and a transfer item is reserved for the month's transfer slot. A wrong option names a misconception only where it follows from exactly that misconception of the key's own Objective.

---

# 2. What was found while writing content

- **Leakage and near-variance, not wrong keys, are what an added item fails on.** The builder verified every key by running it (or, for English, by its cited source). The reviewers rejected items that renamed a lesson example, mirrored a published or sibling item, or put the answer in the prompt. Four review rounds were needed.
- **A choice item cannot carry authored code, hands-on work or a written production.** It would have been stored as the Objective's required evidence type. The builder now refuses it (`form_problems`).
- **A harder item of the same lesson is an application, not transfer** (`AIV-v0` §16). Only an item with a cross-topic profile, a named context and a declared Skill from another Topic carries the transfer role, and the composer refuses any other item for a transfer slot.
- **The 15F lexicon leaves room for fewer distinct English items.** More would be renamed copies, so most 15F Objectives keep an honest 10–12 (user decision).
- **A transfer item with no named context crashed the whole build** instead of being reported. The builder mutation run found it; it is fixed.

---

# 3. Decisions

User decisions (2026-10-04):

- **Pool:** 15–20 items outside the lesson per Objective.
- **Transfer:** content plus the monthly producer (`TRANSFER_OPPORTUNITY`). `professional_evidence_checkpoint` stays with AŞAMA 20.
- **Misconceptions:** option-level deterministic keys.
- **Authorship:** the assistant writes every item, and separate reviewer agents judge them.
- **15F:** an honest small pool. Only structurally distinct items are kept, the shortfall is recorded, and the lessons and the lexicon do not change.

Declared extensions (`D-120`, not silent edits of accepted specs):

- `NeedTrigger.TRANSFER_OPPORTUNITY` (`need.transfer_opportunity`) extends 3B §2.1 / `PDT-v0` §8.1. It takes integration's band row (`MCA-v0` §7 orders the two together).
- `BlueprintExclusion.TRANSFER_IS_MONTHLY` and `SlotItemRefusal.TRANSFER_CLAIM_UNSUPPORTED`.
- Package format:
  - `[answer_misconception]` (key, text, misconception);
  - `transfer_profile` and `context_family_id` on `[item]`;
  - a later package may publish a new task version and map an earlier package's key.

---

# 4. Scope

- **Supplement, version 7.** It adds 613 items, 240 task versions (v2 of every practice, check, review and repair task whose item list grew; no teach task changed), 21 transfer items and 144 wrong-option keys. Of the keys, 66 are on added items and 78 on published items.
- **Unchanged:** no Skill, Objective, edge, Topic, lesson or label is added. Packages 1–6 rebuild byte-identically.

| Source | Added items | Transfer | Task v2s | Objectives at ≥15 | Below 15 |
|---|---|---|---|---|---|
| 15A computing | 120 | 5 | 48 | 13 / 13 (two at 16) | — |
| 15B Python | 235 | 9 | 80 | 20 / 21 | scope_name_resolution 14 |
| 15C C | 112 | 4 | 36 | 9 / 10 | declaration_type_model 14 |
| 15D memory | 63 | 1 | 16 | 6 / 6 | — |
| 15E Linux/Git | 40 | 2 | 20 | 4 / 5 | repository_status_diff 14 |
| 15F English | 43 | 0 | 40 | 2 / 10 | noun_phrase 12; negation, follow, documentation 11; be_and, imperative, preposition, write_note 10 |

Pool counts are items outside the lesson: published plus added, with transfer items not counted. Where an Objective is below 15, every further candidate the reviewers saw was a near-variant or leaked the lesson, so it was dropped rather than shipped.

---

# 5. How an added item is checked

- **The key** is checked by the content directory it belongs to, with that directory's notation, lexicon, runner and prelude:
  - stdout, script, traceback and suite for Python;
  - c_stdout, c_stage, c_sanitize and suites for C;
  - shell for Linux and Git;
  - reference and lexicon for English.
- **The form** must be able to produce the evidence the Objective requires (`form_problems`). Authored code needs a suite; hands-on needs `hands_on` or a suite; a written production has no options; a declared evidence type must be one the Objective accepts.
- **Untaught constructs and words** are refused, exactly as for the directory's own items. A declared Skill is gated like every requirement.
- **A transfer item** must meet all of these (`transfer_problems`):
  - a cross-topic profile and a named context;
  - a declared Skill from another Topic, not its own;
  - difficulty transfer_integration, outside the lesson.
  It is reserved: monthly scope, `cross_topic_transfer` only, in no task.
- **A wrong-option key** must be on a choice item judged by an answer key. It must be an existing wrong option, name a catalogued label of the item's own Objective, and have a pass verdict from the review.
- **Independent review.** One reviewer agent per package judged every added item and every mapping on:
  - the key;
  - leakage from every lesson explanation form;
  - near-variance to published and sibling items;
  - cues in the prompt;
  - the construct measured;
  - hidden prerequisites;
  - the difficulty label.

  Failed items were rewritten or dropped and reviewed again, over four rounds. Final verdicts by round: 425 passed in round 1, 158 in round 2, 25 in round 3 and 5 in round 4. All 613 items and 144 mappings pass (`curriculum/content/15g_assessment/independent_review.yaml`).

---

# 6. Changes to accepted code

- **`tools/build_curriculum_package.py`:**
  - the supplement build (`build_supplement`, `build(..., supplement=)`);
  - `form_problems`;
  - `transfer_problems` and transfer reservation;
  - `answer_misconception` sections;
  - task v2 only when grown;
  - the transfer-context crash fix.
- **core-model:**
  - `TransferProfile`;
  - `AssessmentItem.transferProfile` / `contextFamilyId`;
  - `NeedTrigger.TRANSFER_OPPORTUNITY`;
  - `BlueprintExclusion.TRANSFER_IS_MONTHLY`, `SlotItemRefusal.TRANSFER_CLAIM_UNSUPPORTED`;
  - `AcceptedAnswers.wrongAnswerMisconceptions` / `misconceptionFor`, which proposes a hypothesis for the key's own Objective;
  - the reason code and its copy.
- **core-engines:**
  - `TransferEngine`, the owner;
  - monthly role mapping, weekly exclusion, planner band;
  - the composer's transfer-claim refusal.
- **core-application:** `TransferPlanning` reads store trust, the gate, exposures and clean measurements. Its needs reach the planner and the composer, as the weakness owner's do.
- **data-curriculum:**
  - `PackageFormat.parse(text, earlier)` checks a later package's task and mapping references against everything already read;
  - `FileContentSource` offers only the highest task version and attaches mapped wrong answers to their keys.

---

# 7. Verification

- **Suites:**
  - `ShippedAssessmentPackageTest` reads the real seventh asset after the six and checks that:
    - it adds no graph and carries nothing again;
    - every added item measures a published Objective and is validated;
    - every item is judged by exactly one thing;
    - a task is republished only as a grown v2, and only v2 is offered;
    - transfer items are reserved and never in a task;
    - every wrong-option key names its own Objective's label, and no accepted answer maps;
    - no need is answered differently.
  - `ShippedCourseTest` publishes all seven assets into the real SQLite schema on the JVM:
    - the store trusts all 613 added items;
    - the learner with no history starts the same entry lessons;
    - no transfer opens before anything is learned;
    - later work is the grown v2.
  - Engine and application tests: `TransferEngineTest`, `TransferPlanningTest`, monthly, weekly and planner tests, `OpenResponseFactsTest` and the `PackageFormatTest` supplement tests.
- **Mutation:**
  - builder 22/22, with a fixture build through the real supplement path;
  - Kotlin 32/32.
  - Both control mutants survived.
- **Validator:** `tools/validate_assessment_content.py` rebuilds all seven packages: 108/108 PASS. Its own mutation run detected 40/40.
- **Gradle and APKs:** T1, T2, T3, `verifyModuleBoundaries`, and `assembleDebug` with and without the AI adapter all passed. The seven packages inside each APK have the same SHA-256 as the source, and the APK without the adapter has no network permission. 1056 JVM tests ran.
- **Not run: T6.** Nothing ran on the device. SQLite publication ran on the JVM only.

---

# 8. Open loops

| Loop | Owner |
|---|---|
| Objectives below 15 (3 technical at 14; 8 English at 10–12) | 15H content QA / a later content step (English needs a wider taught lexicon first) |
| `professional_evidence_checkpoint` producer | AŞAMA 20 |
| reviewers' generous labels and isomorph notes recorded in the review findings | 15H |
| documentation_navigation's graph; notation coverage | 15H |
| minute calibration (added items use authoring estimates) | 18B |
| T6 and on-device ingestion | 19 |

---

# 9. Anti-patterns explicitly rejected

- An added item that renames a lesson example, or mirrors a published or sibling item.
- A choice item stored as authored code, hands-on or written evidence.
- A harder item of the same lesson claimed as transfer.
- A transfer item spent by a task before its slot.
- A wrong option mapped to a misconception it does not follow from, or to another Objective's label.
- Overwriting a published task, item or key.
- Padding an English pool with words the lessons never taught.

---

**Next step:** **15H — Content QA** (fresh PRE and the user's new explicit approval; D-117 ended with 15F).
