# 15E — Linux / Git / Shell Foundations — research and synthesis

**Step:** 15E · **Model:** LGSX-v0 · **Decision:** D-118 · **Date:** 2026-10-03

## 1. What had to be found out

1. **What is 15E's scope?** FBB-v0 §6.4 lists five seeds; 6C decomposed them into six Skills by splitting `command_options_redirection_basic` into itself and `pipeline_redirection`.
   - `terminal_filesystem_navigation` was already published by 15C (D-114), because the first C Skill depends on it.
   - 15E publishes the other five: `process_exit_stdout_stderr_basic`, `command_options_redirection_basic`, `pipeline_redirection`, `repository_status_diff` and `stage_commit_history_basic`.
   - That gives 5 Objectives and 6 edges, 3 of them from published Skills: 15A's `program_execution_model` and 15C's terminal Skill twice.
2. **Can those Skills ever become ready?** Yes, with nothing added. Every hard prerequisite is published or in this package. Nobody has to make a closure decision, and 15E adds no new entry point.
3. **How is a key about a command checked?** The commands are run in bash in an empty directory in Linux, the same WSL Ubuntu as 15C and 15D. In 15C a shell item's check ran a separate script. In 15E the check runs **exactly the commands the item shows**, followed by an optional hidden probe that prints only what the question asks about. The learner and the check therefore see the same commands, and the notation scans exactly those commands.
4. **What does Git make up for itself on each machine?** Several things, and no key may depend on any of them:
   - commit numbers (hashes), dates and the author;
   - the default branch name (`master`, with a hint that it will become `main` in Git 3.0);
   - the user's own configuration.

   The package's `shell_prelude` gives every check the same first lines: Git ignores the user's and the system's configuration (`GIT_CONFIG_GLOBAL=/dev/null`, `GIT_CONFIG_NOSYSTEM=1`), every commit gets a fixed identity, and messages and sort order use `C.UTF-8`. Keys ask only for file names, status letters, diff lines, counts and messages.
5. **Repository status before commits are taught.** `repository_status_diff` comes before `stage_commit_history_basic` (6C), but a modified file only shows up after a first commit.
   - The status items give that starting commit as lines marked `# hazırlık` ("setup"). The learner copies them as given and no question asks about them.
   - The notation strips these lines before scanning, and the lesson says what they are.
   - This is an assistant default under D-117.

## 2. Sources read (2026-10-03)

The sources were read with a plain web fetch. Nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| GNU Bash manual — https://www.gnu.org/software/bash/manual/bash.html | Three things: "The standard output of command1 is connected via a pipe to the standard input of command2"; a pipeline's return status "is the exit status of the last command, unless the pipefail option is enabled"; and "The order of redirections is significant", with `ls > dirlist 2>&1` contrasted to `ls 2>&1 > dirlist`. Grounds the pipeline lesson, its misconceptions and items f04, f07 and f08. gnu.org answered with HTTP 429 (rate limit) during the session, so the same text was read from the bash 5.3 manual page installed in WSL Ubuntu (`/usr/share/man/man1/bash.1.gz`). |
| git-status — https://git-scm.com/docs/git-status | In the short format, X is the index (staging area) and Y the working tree; `??` means untracked, ` M` modified in the working tree, `M ` and `A ` staged. Unmodified files are not listed. Grounds both Git lessons and the status items. |
| git-commit — https://git-scm.com/docs/git-commit | A commit contains "the current contents of the index". `-m` gives the message, and `-q` suppresses the commit summary. A commit with nothing staged is refused. Grounds the stage/commit lesson and items f04–f07. |
| git-diff — https://git-scm.com/docs/git-diff | `git diff` compares the working tree with the index; `git diff --staged` compares the index with HEAD. Untracked files are not shown. Grounds the diff lessons and items. |

## 3. What was decided

The user's approval is standing (D-117: merge each finished step and continue until told to stop). No product decision for the user came up. The assistant chose these defaults under D-117, recorded as such:

1. **Setup lines** (`# hazırlık`) for the starting commit in the repository-status items.
2. **The check runs the shown commands** plus a probe, never a separate script.
3. **A larger pool:** 8 keyed items per Objective (plus one rubric item for the process Objective), more than the ~5 of earlier steps. This answers the user's 2026-10-03 concern that a learner who struggles can run out of fresh items.

The decisions of 15A–15D carry over:
- keys are executed and reviewed independently;
- Objectives are refined and recorded, with identities unchanged;
- packages are incremental, and every entity is version 1;
- Linux keys are checked in Linux.

## 4. Measured while authoring (bash 5.3, git 2.53, WSL Ubuntu)

- `ls` on a missing file exits with 2, `mkdir` on an existing folder with 1, and `grep` with no match with 1.
- `git commit` with nothing staged prints its message on stdout and exits with 1.
- `git init` prints "Initialized empty Git repository…" on stdout; `-q` silences it. The default-branch hint goes to stderr.
- `git status --short` shows `?? a.txt`, `A  a.txt` after `git add`, nothing in a clean repository, and ` M a.txt` after a change.
- `git diff` shows nothing once the change is staged; `git diff --staged` then shows it.
- `sort` compares text: `40` sorts before `5`.

## 5. Findings for later steps

- **Making a file to inspect is not a graph prerequisite of Git.** 6C links `repository_status_diff` only to the terminal Skill, yet every Git item creates files with `echo > file`. The Git items and tasks declare `command_options_redirection_basic` (3B §10), as 15C and 15D did. Whether this belongs in the graph is 15H's to decide.
- **The learner's own Git needs an identity.** The lesson asks the learner to set `git config --global user.name/email` once. The checks use a fixed identity that no key reveals.
- **The terminal stays on the computer.** As in 15C, moving the learner's answers between the phone and the computer is 16D's.
