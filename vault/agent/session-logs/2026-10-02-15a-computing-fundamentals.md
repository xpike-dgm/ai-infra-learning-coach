---
type: session-log
status: completed
stage_step: 15A
model: CPFX-v0
decision: D-112
date: 2026-10-02
---

# 15A — Computer / Programming Fundamentals — AŞAMA 15 başladı

14G (#51) main'e merge edilmişti (32db8cb). Fresh 15A PRE yapıldı; beş kanonik kaynak `14G ✅ / 15A active-not-executed` gösterdi. Kullanıcı açık onay verdi ("15A ile devam et") ve üç ürün sorusunu cevapladı.

## Result
- Canonical: `docs/COMPUTING_FUNDAMENTALS_CONTENT_SPEC.md`
- Machine-readable: `arch/15a_computing_fundamentals/computing_fundamentals.yaml`
- Content verification: `arch/15a_computing_fundamentals/content_verification.yaml`
- QA: `arch/15a_computing_fundamentals/qa_report.yaml`
- Stale audit: `arch/15a_computing_fundamentals/stale_reference_audit.yaml`
- Research/synthesis: `research/15a_computing_fundamentals_research.md`
- Content source: `curriculum/content/15a_computing_fundamentals/` → `tools/build_curriculum_package.py` → `android/app-wiring/src/main/assets/curriculum_package.txt`
- Code: core-model (AuthoredTaskFacts); data-curriculum (PackageFormat `[task]`, whole-line comments, prompt line breaks; FileContentSource.taskCandidates); app-wiring (first JVM test)
- Final: `CPFX-v0 / D-112`
- T6 and the first SQLite ingestion of the shipped package: not run (the learner's, on the device)

## User decisions
- Validation: executed keys plus an independent review.
- Notation: a readable Python subset, taught for reading.
- Objectives: refine and record (identities unchanged).

## Found
- No task format: every need had no candidate.
- The 6C subgraph all `draft`; Objective metadata templated.
- `#` cut values; prompts did not decode line breaks.
- `while` needs comparisons the graph does not state; `requires_transfer` not stored.

## Independent review
- First pass: all 94 keys correct; 32 items failed for lesson leakage (the problem already solved in the Skill's own lesson, worked example or contrast), one explanation had an arithmetic error.
- All 32 rewritten; in-lesson items removed from later pools; a new item failed once and was rewritten. Final: 95/95 items, 79/79 explanations.

## Durable decisions
Content is only as trustworthy as what checked it. AI-written content is never trusted by itself.

## Next
15B — Python Foundations is active-not-executed after POST. Fresh PRE + explicit user approval required.
