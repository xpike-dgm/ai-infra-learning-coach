---
type: session-log
status: completed
stage_step: 12A
model: MSTX-v0
decision: D-092
date: 2026-09-21
---

# 12A — Mastery Engine v1

11E main'deydi (1267d9d); fresh 12A PRE yapıldı ve beş kanonik kaynak `11E ✅ / 12A active-not-executed` gösterdi. Kullanıcı açık onay verdi.

## Result
- Canonical: `docs/MASTERY_ENGINE_IMPL_SPEC.md`
- Machine-readable: `arch/12a_mastery_engine/mastery_engine.yaml`
- QA: `arch/12a_mastery_engine/qa_report.yaml`
- Stale audit: `arch/12a_mastery_engine/stale_reference_audit.yaml`
- Research/synthesis: `research/12a_mastery_engine_research.md`
- Final: `MSTX-v0 / D-092` — AŞAMA 12 başladı
- Tests: MasteryEngineTest (23), EvidencePipelineTest, RebuildMasteryTest, EvidenceStorageTest (T2), EvidenceFactsTest
- Implementation mutation: 33/33 (G26, G33 survived the first run)
- Independent QA: 164/164 PASS; validator mutation 37/37
- T6: çalıştırılmadı

## Found
- Nothing had ever written evidence: 11B and 11D both refused, deliberately.
- `GRE-v0`'s Objective gate profile has no columns; it travels as authored content with GRE defaults.
- The group rubric does not exist yet; the mean of a group's rows stands in and cannot raise the group count.
- The validator's declaration reader stopped at a default parameter's `=` — the same class of defect 11D found — so nine checks read an empty string.

## Durable decisions
Mastery asks whether they can do it without help. Excluded evidence is returned with the rule it failed. A dependency group is one group; the window is the last five; the mean is equal-weighted. A Skill is non-compensatory. Both halves of hysteresis are implemented. An unmeasurable answer is `invalid` with no result. The projection is rebuilt, carries provenance, and writes only its own axis.

## Key tensions resolved
1. **The familiar idea is the dangerous one.** BKT/IRT/confidence% are well documented and wrong here; `GRE-v0` had already removed them.
2. **Hysteresis has two halves.** Implementing one produces a product that never updates or one that panics.
3. **A failed read and a zero look alike.** `not_reliably_measured` is `invalid` with no `q_g`.
4. **The proof had to be split** because `data-persistence` may not depend on `core-application`.

## Next
12B — Prerequisite Engine is active-not-executed after POST. Fresh PRE + explicit user approval required.
