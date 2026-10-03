---
type: session-log
status: completed
stage_step: 15E
model: LGSX-v0
decision: D-118
date: 2026-10-03
---

# 15E — Linux / Git / Shell Foundations — a command's key is what the shown commands really print

15D (#55) was merged into main (6172fa7). The user had given standing approval (D-117) to merge each finished step and continue without asking. A fresh 15E PRE showed `15D ✅ / 15E active-not-executed` in all five canonical sources. No product decision for the user came up.

## Result
- Canonical: `docs/LINUX_GIT_SHELL_CONTENT_SPEC.md`
- Machine-readable: `arch/15e_linux_git_shell/linux_git_shell.yaml`
- Content verification: `arch/15e_linux_git_shell/content_verification.yaml`
- QA: `arch/15e_linux_git_shell/qa_report.yaml`
- Stale audit: `arch/15e_linux_git_shell/stale_reference_audit.yaml`
- Research/synthesis: `research/15e_linux_git_shell_research.md`
- Content pipeline: `curriculum/content/15e_linux_git_shell/` → `tools/build_curriculum_package.py` (Linux through WSL) → `android/app-wiring/src/main/assets/curriculum_package_v5.txt`
- Code: builder only. A shell item without a `script` runs its shown `code` plus a `probe`, and the package's `shell_prelude` reaches every shell run. Kotlin main code, schema, ports and store are unchanged.
- Final: `LGSX-v0 / D-118`
- Not run: T6 and on-device ingestion. SQLite publication ran on the JVM only.

## Scope
- FBB-v0 §6.4's seeds, as 6C decomposed them, without the terminal Skill (published in 15C): process exit/stdout/stderr, commands with options and redirection, pipelines, repository status/diff, and stage/commit/history.
- 5 Skills, 5 Objectives and 6 edges, 3 of the edges from 15A and 15C. Nothing was added and there is no entry point.
- 41 items (40 shell — 8 observation, 32 hands-on — and 1 rubric), 35 explanations, 15 misconceptions and 25 tasks.

## Assistant defaults under D-117
- `# hazırlık` setup lines give the starting commit before commits are taught. They are shown, never asked about, and not scanned.
- The check runs the shown commands plus a probe, never a separate script.
- 8 keyed items per Objective, in answer to the user's concern that the item pool is small.

## Found
- A 15C shell check was a separate script; now what is shown is what runs.
- Git makes up hashes, dates, identity and branch names per machine. The prelude fixes the identity and ignores all configuration, and no key asks for any of them.
- Some Git messages ("nothing to commit", "Initialized…") go to stdout.
- This Ubuntu's default coreutils are uutils (Rust). The reviewer re-ran everything with GNU coreutils too, and every key is the same.
- Git work creates files, but the graph does not say so. The work declares it; the graph question goes to 15H.

## Independent review
- **First pass:** 39/41 items and 34/35 explanations passed. All 40 keys were correct. The failures were:
  - f07 asked for mkdir's exact code, which the canonical stated.
  - The r1 rubric criterion was not asked for by the prompt.
  - The `git init` explanation omitted its hint lines.
- The reviewer also flagged the learner's missing Git identity; the identity setup is now repeated in both Git lessons.
- **Second pass:** 41/41 items, 35/35 explanations.

## Verification
- Mutation 5/5: the builder's new checking, against the real build and two planted-error fixtures.
- Validator: 114/114. Its own mutation test: 36/36.
- Sweep: 60/60. The first sweep was 59/60: 15C's `E15C-10_keys_in_linux` read the builder's old shell call word for word. It was narrowed (declared in the contract and D-118), and 15C is 155/155 again.
- JVM tests: 1017. Both APK builds passed, and all five packages in the APK match their source by SHA-256.

## Next
After POST, 15F — English A0→A1/A2 başlangıç paketi is active-not-executed. It needs a fresh PRE; the user's approval is standing (D-117).
