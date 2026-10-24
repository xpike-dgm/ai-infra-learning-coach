---
type: session-log
status: completed
stage_step: 16C
model: PWPX-v0
decision: D-124
date: 2026-10-24
---

# 16C — Progress / weakness gösterim kuralları — Topics, the weakness context and the Technical English profile

On 2026-10-24 the user said "16B yi merge et 16C ye başla". 16B (#61) was squash-merged after CI went green (0f52522). The fresh 16C PRE showed `16B ✅ / 16C active-not-executed`. That message was also the approval for 16C.

## Result
- Canonical: `docs/PROGRESS_WEAKNESS_SPEC.md`
- Machine-readable: `arch/16c_progress_weakness/progress_weakness.yaml`
- QA: `arch/16c_progress_weakness/qa_report.yaml`
- Stale audit: `arch/16c_progress_weakness/stale_reference_audit.yaml`
- Research/synthesis: `research/16c_progress_weakness_research.md`
- Final: `PWPX-v0 / D-124`
- Not run: T6. The screens have not been seen on a device.

## User decisions
- Scope: Topic state, `topic_detail`, the weakness and misconception context in `skill_detail`, and the Technical English profile. Delivered as core code plus real screens.
- Coverage: an Objective counts as covered when its lesson has a recorded attempt or a 13F diagnostic waived it.
- English:
  - the historical band is replayed from the evidence;
  - B1 is named "not published yet";
  - the anchors come from 7B's alignment file.
- Topic entry, decided twice:
  - First choice: "every outside edge satisfied".
  - The shipped graph contradicted it: 12 of 27 Topics would show locked while the planner offered one of their Skills.
  - Revised the same day: a Topic that has not been started is available when one of its Skills can start, and locked only when none can.

## Found
- **Topic entry:** the first entry rule disagreed with the planner. The case that showed it is `topic.python.values_types`: it holds `state_assignment_model`, a first-day lesson, so the Topic showed locked while that lesson was being offered.
- **New learner:** four Topics are open and twenty-three wait. These are exactly the gate's set, seen per Topic.
- **Lesson items:** the 67 lesson items never appear in the 1023 practice or assessment items, and every one of the 65 Objectives has a lesson.
- **Unwritten tables:** `topic_state` and `english_profile` have no writer and need none, because both are derived when read.
- **Mutation runner:**
  - The first attempt never reached Gradle: `gradlew.bat` was called by a bare name, the baseline exited 1, and the runner accepted it.
  - The runner now refuses a baseline that does not exit 0, and any run that writes no test results.
- **Validator mutation:** the first run left V16 alive. Two expression-bodied functions were read with a brace scan that ran into the next function. Both are now read to their own end.
- **Shell pitfall, again:** a heredoc wrote regex word boundaries as backspace characters. A syntax error and a scan for the character caught it.

## Verification
- Kotlin mutation 31/31 (single clean run; negative control survived).
- Validator mutation 46/46 (single clean run; negative control survived).
- 16C QA 117/117 PASS.
- Sweep 66/66 (56 light + 10 heavy).
- 1177 JVM tests, `verifyModuleBoundaries`, and both APKs. The eight packages are identical to the source assets, and the APK without the adapter has no INTERNET permission.

## Next
`16D — Ayarlar`. It needs a fresh PRE and the user's new explicit approval.
