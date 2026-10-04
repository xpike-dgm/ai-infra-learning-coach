# 15G — Assessment content — research and synthesis

**Step:** 15G · **Model:** ACNX-v0 · **Decision:** D-120 · **Date:** 2026-10-04

## 1. What had to be found out

1. **What is "assessment content" once 15A–15F have shipped?** Each content package already carries the items its lesson needs, but only about five to eight outside the lesson per Objective. A planner that re-checks, reviews and repairs needs unseen items for every one of those occasions (`QAB-v0` §§8, 14; `WBA-v0` §9; `MCA-v0` §9: a re-measurement is an unseen structure). The user's open concern (15F handoff) was exactly this pool size. **15G adds items to Objectives that are already published, and changes nothing that was published.**
2. **How can a package add to published Objectives?** `LFPS-v0`/`LDBX-v0`: a published version is never overwritten. So 15G is a **supplement** — package version 7 that carries new items, a **new version** of every task whose item list grows (task v1 stays readable by its pinned reference; only the highest version is offered), and wrong-option misconception keys for earlier keys. It adds no Skill, Objective, edge, Topic, lesson or label.
3. **What did `MCA-v0` §6.5 leave unbuilt?** The monthly `cross_topic_transfer` role had no producer (13B). A transfer opportunity needs content that really asks a learned Skill in a context from another Topic (`QAB-v0` §17), and an owner that opens the need only when a trustworthy, unseen, admitted item exists and no clean transfer measurement does. `professional_evidence_checkpoint` stays with AŞAMA 20.
4. **Can a wrong option say which misconception it follows from?** Concept inventories build distractors from the misconceptions students actually hold, so a chosen distractor gives a *hypothesis* about the learner's thinking — but a single response can over- or under-estimate. `WAAX-v0` already says a label is only as strong as its evidence and an AI proposal never exceeds a hypothesis. So a key maps a wrong option to one catalogued label of the key's own Objective, only where that option follows from exactly that misconception, and only where an independent reviewer passed the mapping.
5. **What makes an added item worth having?** It must measure the Objective in a structure the learner has not seen: not a lesson example renamed, not a near-variant of a published or sibling item, not a prompt that gives the answer, and of a form that can produce the evidence the Objective requires (a chosen option is never authored code, hands-on work or a written production).

## 2. Sources read (2026-10-04)

The sources were read with a plain web fetch. Nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| Wikipedia — Transfer of learning — https://en.wikipedia.org/wiki/Transfer_of_learning | Transfer is applying what was learned to a new situation or context; near transfer shares many elements with the learning context, far transfer differs substantially. Grounds `cross_topic_context` as the profile that can carry the month's transfer slot and `same_context`/`near_context` as not transfer. |
| Wikipedia — Multiple choice — https://en.wikipedia.org/wiki/Multiple_choice | Distractors should be plausible yet clearly incorrect; obviously wrong options are a defect. Grounds the review rule that every option is defensible only as a wrong answer, and a single defensible key. |
| Wikipedia — Concept inventory — https://en.wikipedia.org/wiki/Concept_inventory | Distractors built from students' misconceptions help reveal their thinking; inventories can over- or under-estimate and may measure test-taking or language ability instead. Grounds option-level misconception keys as hypotheses only, and the reviewer's "follows from exactly this misconception" test. |

Internal contracts read again: `QAB-v0` §§8, 14, 17, 22 (pools, exposure, transfer profiles, roles); `MCA-v0` §§5–9 (monthly roles, cross-topic transfer, unseen structure); `AIV-v0` §16 (a transfer claim is checked, not assumed); `WAAX-v0` (misconception labels); `GRE-v0` (two variant families); `PDT-v0` §8.1 and `PBR-v0` (need triggers and bands); `LFPS-v0` (never overwrite a published version); D-113 (incremental packages); `CDEX-v0` (code runs on the learner's computer).

## 3. Decisions taken (user)

- **Pool:** 15–20 items outside the lesson per Objective.
- **Transfer:** content and the monthly producer (`TRANSFER_OPPORTUNITY`); `professional_evidence_checkpoint` stays with AŞAMA 20.
- **Misconceptions:** option-level deterministic keys.
- **Authorship:** the assistant writes every item; separate reviewer agents judge them.
- **15F:** an honest small pool — only structurally distinct items, the shortfall recorded; the lessons and the lexicon do not change.

## 4. What was found

- **Leakage and near-variance are the dominant defects, not wrong keys.** Every key the builder checked was right; the reviewers rejected items for repeating a lesson example, mirroring a published or sibling item, or cueing the answer. Four rounds were needed; a final pool was reached by rewriting into new formats or dropping.
- **A choice item cannot carry authored code, hands-on or written evidence.** The builder stored such items as the Objective's required type; it now refuses them (`form_problems`).
- **A transfer claim must be checkable.** A harder item of the same lesson is an application, not transfer: a supplement gives the transfer role only to an item with a cross-topic profile, a named context and a declared Skill from another Topic, and reserves it for the monthly slot (no task presents it).
- **A taught lexicon limits structurally distinct English items.** With 15F's lexicon, most Objectives hold 10–12 honest items; more would be renamed copies.
- **A transfer item without a named context crashed the whole build** instead of being reported (found by the builder mutation run; fixed).
