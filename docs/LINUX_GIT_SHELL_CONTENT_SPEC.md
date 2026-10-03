# Linux / Git / Shell Foundations Content Specification — LGSX-v0

**Stage step:** 15E — Linux / Git / Shell Foundations  
**Status:** ACCEPTED — independent 15E QA PASS  
**Decision:** `D-118`  
**Model:** `LGSX-v0 — Linux / Git / Shell Foundations content`  
**Behaviour it implements:** `FBB-v0` §6.4 (the Linux, shell and Git seeds) and §16; `KGC-v0` §8, §25, §27–§28; `LFPS-v0` and `D-113` (incremental packages; a published version is never overwritten); `D-114` (Linux keys are checked in Linux); `D-117` (the user's standing approval); `QAB-v0` §21–§22; `AIV-v0` §2–§3, §23–§25; `GRE-v0`; `TASK_TAXONOMY_SPEC` (3B) §3, §10, §15, §19  
**Content:** `curriculum/content/15e_linux_git_shell/` → `android/app-wiring/src/main/assets/curriculum_package_v5.txt` (generated; never edit it by hand)  
**Persistence / ports / schema / Kotlin main code:** unchanged

## 1. Purpose

15C and 15D checked programs. 15E is about commands: what a process leaves behind, how commands are combined, and how Git records work. A key about a command is only as good as two things: the commands that were actually run, and how independent the result is of the machine that ran them. 15E answers:

> **Bir komut hakkındaki anahtar nasıl doğrulanır — öğrencinin gördüğü komutların ta kendisi çalıştırılarak ve makineden makineye değişen hiçbir şeye dayanmadan?**

Primary invariant:

> **A key about a command is what bash and git really print when exactly the shown commands run in an empty directory in Linux.** A probe may only print what the question asks about. No key depends on what the checking machine's Git makes up for itself — a hash, a date, an identity, a branch name. A setup line never teaches what its item asks. No earlier package reads differently.

---

# 2. What was found before and during writing content

- **The terminal Skill is already published.** 15C published `terminal_filesystem_navigation` (D-114), so 15E publishes the other five Skills of FBB-v0 §6.4 as 6C decomposed them.
- **A check could run something other than what the learner sees.** In 15C a shell item's check was a separate script. Now the check runs the item's shown commands, followed by an optional probe.
- **Git makes things up per machine:** commit hashes, dates, the author, the default branch name, and the user's configuration. The package's shell prelude fixes the identity and ignores every configuration, and no key asks about any of them.
- **A modified file needs a commit, and commits come later.** The status items give their starting commit as lines marked `# hazırlık`. The learner copies them as given, no question asks about them, and the notation does not scan them.
- **Some Git messages go to stdout.** "Initialized empty Git repository" and "nothing to commit" both do, so keys take the probe's last line.
- **Git work creates files, but the graph does not say so.** Git items and tasks declare `command_options_redirection_basic`; the graph question goes to 15H.

---

# 3. Decisions

- **The user's approval is standing (D-117).** No product decision for the user came up.
- **Assistant defaults under D-117:**
  - setup lines for the starting commit;
  - the check runs the shown commands plus a probe;
  - 8 keyed items per Objective, which answers the user's concern about small pools.
- **Carried over from earlier steps:**
  - keys are executed and reviewed independently;
  - Objectives are refined and recorded, with identities unchanged;
  - packages are incremental, and every entity is version 1;
  - Linux keys are checked in Linux.

---

# 4. Scope

- **Skills:** FBB-v0 §6.4's seeds as 6C decomposed them, without the terminal (15C): `process_exit_stdout_stderr_basic`, `command_options_redirection_basic`, `pipeline_redirection`, `repository_status_diff` and `stage_commit_history_basic`.
- **Size:** 5 Skills, 5 Objectives and 6 edges, 3 of them from 15A and 15C.
- **No additions:** nothing is added and there is no new entry point.
- **Still forbidden in every item:** control flow, shell variables, `rm` and `sudo`.

| Skill | Objective | Direct type | Items |
|---|---|---|---|
| process_exit_stdout_stderr_basic | classify_process_outputs | system_observation | 8 observation + 1 rubric |
| command_options_redirection_basic | construct_simple_command | hands_on_system_task | 8 hands-on |
| pipeline_redirection | demonstrate_capability | hands_on_system_task | 8 hands-on |
| repository_status_diff | inspect_change_state | hands_on_system_task | 8 hands-on |
| stage_commit_history_basic | commit_and_verify_history | hands_on_system_task | 8 hands-on |

The process items are observation items: the learner predicts what a process leaves behind, and the terminal, which would simply answer, is not allowed. Every other item is done at the learner's own terminal.

---

# 5. How a key is checked (in Linux)

| Kind | What is run and required |
|---|---|
| `shell` | The item's shown commands run in bash in an empty directory in Linux, after the package's prelude, followed by an optional probe that prints only what the question asks about. The key is what they print, either the whole output or the last line. For a multiple-choice item, the outcome the probe prints must make the keyed option the only true one. |
| `rubric` | An open response judged by its rubric (OREX-v0). Provisional at most. |

Lesson checks are shell commands, run the same way. Validation records name `15e/executed_key` or `15e/rubric_reviewed`, each `+independent_review`. The validator rebuilds the package and all four earlier packages in Linux and compares the bytes.

Result:
- 41 items: 40 shell and 1 rubric.
- 35 explanations, 15 misconceptions and 25 tasks.
- 23 lesson claims were executed.
- Every item and explanation was independently reviewed and passed.

---

# 6. Changes to accepted code

- `tools/build_curriculum_package.py`:
  - a shell item without a `script` runs its shown `code` plus a `probe`;
  - the per-package `shell_prelude` reaches every shell run.

  15A–15D rebuild byte-identically.
- `app-wiring`: the generated fifth asset, read by the existing `curriculum_package_v<N>.txt` loader.
- Kotlin main code, the runner, the schema, the ports and the store are unchanged.
- One living gate was narrowed: 15C's `E15C-10_keys_in_linux` read the builder's old shell call word for word. It now requires the prelude form, and that 15C has no prelude, so 15C's shell keys still run as plain bash in Linux.

---

# 7. Verification

- **Suites:**
  - `ShippedShellGitPackageTest` reads the real fifth asset with the first four and checks that:
    - it is published, closed, and built on 15A and 15C;
    - the commands, setup lines included, are shown exactly as they run;
    - every Objective is measurable;
    - every item is judged by exactly one thing;
    - the terminal is allowed only for hands-on work, never for observation;
    - every Skill is served, and the Git work declares file creation;
    - everything is validated.
  - `ShippedCourseTest` publishes all five assets into the real SQLite schema on the JVM. A learner with no history still starts only the three entry Skills, and every 15E Skill waits.
- **Mutation:** 5/5 detected. It targets the builder's new checking, using the real build plus two planted-error fixtures.
- **Validator:** `tools/validate_linux_git_shell.py` rebuilds all five packages in Linux: 114/114. Its own mutation test detected 36/36, and the sweep passed 60/60. 1017 JVM tests passed.
- **Not run: T6.** Nothing ran on the device. SQLite publication ran on the JVM only.

---

# 8. Open loops

| Loop | Owner |
|---|---|
| Git work declares `command_options_redirection_basic` (graph) | 15H |
| item pool size in 15A–15D (~5 per Objective) | the user (pool-expansion step awaiting decision) |
| notation coverage; lesson checks and probes not scanned | 15H |
| moving the learner's answers between phone and computer | 16D |
| minute calibration | 18B |
| T6 and on-device ingestion | 19 |

---

# 9. Anti-patterns explicitly rejected

- A check that runs something other than the shown commands.
- A key that depends on a hash, a date, an identity or a branch name.
- A setup line that teaches what its item asks.
- A pipe, a commit or a `$?` in an item before its Skill teaches it.
- Letting the terminal answer an observation item.
- An item validated without an independent verdict.

---

**Next step:** **15F — English A0→A1/A2 başlangıç paketi** (fresh PRE; the user's approval is standing under D-117).
