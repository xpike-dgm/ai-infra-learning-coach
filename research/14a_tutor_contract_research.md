# 14A Tutor Behaviour Contract — Research & Decision Synthesis

**Stage step:** 14A — Tutor davranış sözleşmesi  
**Purpose:** Fix how the AI Tutor behaves before any kind of help gets its own prompt (14B–14F) or a real provider call (14G): what can be asked, how much an answer may reveal, what leaves the device, what happens without an answer, and what help means for evidence.

## 1. Research-need decision

**One targeted external check was made; no separate Research AI pass was needed.** What the tutor may and may not do is fixed by accepted contracts — `AIAX-v0` (authority, outcome taxonomy, privacy, credentials), `2D` (H0–H4, timing, provenance, evidence-use classes), `TRUX-v0` §8–§9 and `ASUX-v0` §8 (assistance choreography), `TEIP-v0` §5 (instruction modes, gloss), `VDWX-v0` (help ends a fast path). The work was to give those contracts one enforceable home in code and to settle three product questions they leave open, which went to the user (§4).

The external check confirmed the reason the contract is guardrail-first rather than answer-first: in a field experiment with high school mathematics students, unrestricted access to a general-purpose model improved practice performance but harmed later unassisted performance, while a tutor configured to give hints and promote reasoning instead of answers largely removed that harm (Bastani et al., *Generative AI without guardrails can harm learning*, PNAS 122(26), 2025, doi:10.1073/pnas.2422633122). This agrees with what the repo already decided in prose (`2D`: help is allowed, a shown solution is not independent evidence; `TRUX-v0` §8.2: no auto-reveal); it did not add a rule.

## 2. Canonical source set reviewed

- `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` (`AIAX-v0`) §4–§13; `arch/9e_ai_integration/`.
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` (`2D`) §2–§17.
- `docs/LEARNING_BEHAVIOR_RULES.md` §9, §12–§18.
- `docs/DAILY_WORKING_FLOW_SPEC.md` (`TRUX-v0`) §6.4–§6.5, §8–§9, §12.2; `docs/TASK_RUNNER_SPEC.md` (`RNRX-v0`).
- `docs/ASSESSMENT_SESSION_UX_SPEC.md` (`ASUX-v0`) §8.
- `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` (`TEIP-v0`) §5, §10.1.
- `docs/SERVICE_BOUNDARIES_SPEC.md` and `arch/9d_service_boundaries/boundaries.yaml` (ports, `ai-adapter` responsibility, "any tutor port").
- `docs/INFORMATION_ARCHITECTURE_SPEC.md` §6.3 (tutor is contextual, never a destination).
- `docs/V1_SCOPE.md` §12, `docs/V1_SUCCESS_CRITERIA.md` SC-029–SC-031, `docs/PRODUCT_REQUIREMENTS.md` (AI's role), `docs/NON_GOALS.md` NG-V1-04.
- `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md` (help ends the fast path), `docs/RETENTION_IMPL_SPEC.md` (solution-exposure loop → 14), `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md` (guidance fading → 14).

## 3. What was found

1. No tutor existed in code: no port, request, reply, null implementation or adapter — only `EvaluatorPort`.
2. Recorded help did not reach evidence: callers of `RecordEvidence` passed an independence class they chose; nothing derived it from the recorded assistance events.
3. `evidenceFor` returned every row unexposed, although solution exposures could be recorded; the engines that honour `solutionExposed` never saw one.
4. The runner's H3/H4 disclosure is untrue after an answer is frozen (the attempt is untouched then).
5. The first harness run classified mutants as detected after about seven seconds each; this was checked by hand before any result was trusted (see the contract's `mutation_results`).

## 4. Positions taken

- **User decisions (2026-10-01):** a separate `TutorPort` (declared extension under `D-105`, 9D unedited); free questions, with the learner choosing the level while an answer is open; no network in 14A, the first real call is 14G's.
- **Five closed intents; help always requestable;** a mismatched ask is redirected to the intent that fits its moment, never refused.
- **The record never claims less help than was allowed:** the learner's ceiling is recorded, not the reply's own declaration, which can only refuse a reply; after an answer is frozen, H4 is recorded because no level was asked.
- **The message that leaves the device is built in core**, so the privacy boundary is tested off the device; material is data and cannot close a section.
- **Nothing unshown is recorded;** authored help is the only fallback and is recorded at its own known level.
- **Independence is derived** from recorded help and the learner's provenance answer by `2D`'s table.
- **A shown solution is an exposure from that moment,** and later evidence reads it by the attempt's own sequence.
- **Guidance fading is not the tutor's:** it never withholds; scaffold decreases through task selection and the task's instruction mode (`TEIP-v0` §5.4).

## 5. Explicitly not decided in 14A

Wrong-answer analysis and misconception memory (14B); alternative-explanation content (14C); code evaluation (14D); comprehension check of AI-written code (14E); open-ended evaluation (14F); the adapter's call site, router and model currency (14G); authored hint ladders and task instruction modes (15); rendering and calling from the app (16D); conversation history (16B); calibrating replies against their declared level (18D). T6 was not run.
