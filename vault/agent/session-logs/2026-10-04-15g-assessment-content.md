---
type: session-log
status: completed
stage_step: 15G
model: ACNX-v0
decision: D-120
date: 2026-10-04
---

# 15G — Assessment content — unseen items for every published Objective, nothing published overwritten

15F (#57) was merged into main (2ba31dc). The fresh 15G PRE showed `15F ✅ / 15G active-not-executed`. The standing approval (D-117) had ended with 15F; on 2026-10-04 the user gave a new explicit approval ("15G ye başla").

## Result
- Canonical: `docs/ASSESSMENT_CONTENT_SPEC.md`
- Machine-readable: `arch/15g_assessment_content/assessment_content.yaml`
- Content verification: `arch/15g_assessment_content/content_verification.yaml`
- QA: `arch/15g_assessment_content/qa_report.yaml`
- Stale audit: `arch/15g_assessment_content/stale_reference_audit.yaml`
- Research/synthesis: `research/15g_assessment_content_research.md`
- Content: `curriculum/content/15g_assessment/` → `tools/build_curriculum_package.py` (`build_supplement`) → `android/app-wiring/src/main/assets/curriculum_package_v7.txt`
- Final: `ACNX-v0 / D-120`
- Not run: T6 and on-device ingestion. SQLite publication ran on the JVM only.

## User decisions
- 15–20 items outside the lesson per Objective.
- Transfer content plus the monthly producer (`TRANSFER_OPPORTUNITY`); `professional_evidence_checkpoint` stays with AŞAMA 20.
- Option-level deterministic misconception keys.
- The assistant writes every item; separate reviewer agents judge them.
- 15F: an honest small pool, shortfall recorded, lessons and lexicon unchanged.

## Scope
- 613 items, 240 grown task v2s, 21 transfer items reserved for the month's slot, 144 wrong-option keys; no graph, lesson or label added; packages 1–6 rebuild byte-identically.
- 54 of 65 Objectives at ≥15; recorded shortfall: three technical Objectives at 14, eight English Objectives at 10–12.

## Found
- Added items fail on leakage and near-variance, not wrong keys.
- A choice item cannot carry authored code, hands-on or written evidence; the builder now refuses it.
- A harder item of the same lesson is not transfer.
- A transfer item without a named context crashed the whole build; the builder mutation run found it.
- Three Kotlin mutants survived at first (the planner and the composer asking the transfer owner; a wrong answer mapped again by a later package; a label of another Objective); each now has a test.

## Next
`15H — Content QA` — fresh PRE and the user's new explicit approval.
