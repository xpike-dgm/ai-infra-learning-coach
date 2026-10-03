# 15F — English A0→A1/A2 starter package — research and synthesis

**Step:** 15F · **Model:** EAAX-v0 · **Decision:** D-119 · **Date:** 2026-10-03

## 1. What had to be found out

1. **What is 15F's scope?** The Technical English route (6C's D01, aligned to CEFR descriptor families in 7B TECP-v0) has 15 Skills: 5 A1, 5 A2 and 5 B1. "A0→A1/A2" means the ten A1 and A2 anchor Skills, with 10 Objectives.
   - **A1:** recognize_core_technical_labels, technical_noun_phrase_recognition, be_and_simple_present_comprehension, imperative_instruction_comprehension, preposition_function_word_comprehension.
   - **A2:** negation_question_comprehension, follow_bilingual_technical_instruction, read_simple_terminal_error_fragments, documentation_navigation, write_command_result_note.
   - B1 and the B2+ professional extension come later.
2. **Can those Skills ever become ready?** Yes. Every hard edge into them comes from another A1 or A2 Skill (TECP-v0 §7: a prerequisite's band never exceeds its target's).
   - `recognize_core_technical_labels` has no prerequisite, so it becomes a fourth entry point.
   - 6C's soft edge from `engineering.reproducible_run_notes` to `write_command_result_note` points at an unpublished Skill, so it does not enter the package. A soft edge never blocks anyway.
3. **How do we keep the main English rule?** The rule (ENGLISH_FOUNDATION_RULES; EED-v0; TECP-v0 §9) is that an item never asks for a word or a structure that has not been taught. Earlier notations listed code constructs. 15F adds a **lexicon**: for each Skill, exactly the words its lesson teaches, with meanings given in the lesson.
   - The builder reads every English word an item shows and refuses the item if a word is unknown to every lesson, or known only to a lesson the item may not rely on.
   - Literals are not vocabulary: `code` in backticks, a 'quoted' name, tokens with non-letters, and mixed-case identifiers such as NameError.
   - Grammar the patterns cannot see — word order, question inversion, plurals — is the independent reviewer's to judge.
4. **How is a meaning key checked?** English meaning cannot be executed.
   - A meaning key cites its source (Wiktionary for words; Wikipedia's articles on the imperative mood, do-support, the simple present and English prepositions for structures; man-pages(7) and man(1) for help-page conventions), and the independent review decides it.
   - Where a stimulus is a tool's own message (terminal, compiler, Python), the check produces it with the real tool in Linux and requires the shown line verbatim.

## 2. Sources read (2026-10-03)

The sources were read with a plain web fetch. Nothing was sent anywhere and no account was used. Cambridge Dictionary returned HTTP 403 and the British Council site was refused by the fetch policy, so neither is cited.

| Source | Used for |
|---|---|
| Wikipedia — Imperative mood — https://en.wikipedia.org/wiki/Imperative_mood | English imperatives use the bare infinitive and usually omit the subject; they are negated with do-support ("Don't work!"). Grounds the imperative lesson and why negative imperatives wait for negation. |
| Wikipedia — Do-support — https://en.wikipedia.org/wiki/Do-support | Negation and questions with lexical verbs use do/does ("Does he laugh?", "She does not laugh"); be inverts and negates directly ("Is she home?"). Grounds the negation/question lesson. |
| Wikipedia — Simple present — https://en.wikipedia.org/wiki/Simple_present | Third-person singular -s; the tense is used for habits, facts and general realities. Grounds the be/present lesson and the command-note pattern. |
| Wikipedia — English prepositions — https://en.wikipedia.org/wiki/English_prepositions | Prepositions most typically denote relations in space and time: location (in, at), goal and source (to, from), accompaniment (with). Grounds the preposition lesson. |
| Wiktionary — https://en.wiktionary.org/ | Word meanings for every label and term key (e.g. directory: "a virtual container in a computer's file system"). |
| man-pages(7) and man(1), read in WSL Ubuntu (`/usr/share/man/man7/man-pages.7.gz`) — https://man7.org/linux/man-pages/man7/man-pages.7.html | The conventional sections NAME, SYNOPSIS, DESCRIPTION, OPTIONS, EXIT STATUS, EXAMPLES and SEE ALSO; "any or all arguments within [ ] are optional". Grounds the documentation-navigation lesson and its authored excerpts. |

## 3. What was decided

The user's approval is standing (D-117); no product decision for the user came up. The assistant chose these defaults under D-117, recorded as such:

1. **Scope:** the ten A1 and A2 Skills.
2. **A lexicon per lesson,** enforced by the builder for every English word an item shows.
3. **Prompts and options in Turkish,** with the English stimulus measured (TEIP-v0's bilingual scaffold). Turkish options are not scanned.
4. **Authored help excerpts** that keep the real conventions, with fictional tool names that contain a dash so they are literals. Real man pages contain far more words than an A2 learner has been taught.
5. **Error-message stimuli only from stable messages:** ls, cat, Python exceptions, gcc's "undeclared" and "expected", and bash's "command not found". Messages that differ between this Ubuntu's uutils and GNU coreutils (mkdir), between gcc versions (a missing file), or by mount point (git outside a repository) are not used.
6. **8 keyed items per Objective,** plus two rubric items for the free command note.

## 4. Measured while authoring (WSL Ubuntu, bash 5.3, Python 3.14, gcc 15.2)

- `ls notes.txt` and `cat report.txt` print the same message under uutils and GNU coreutils; `mkdir` on an existing folder does not (uutils: `mkdir: src: File exists`).
- gcc's "‘count’ undeclared (first use in this function)" and "expected ‘,’ or ‘;’ before ‘return’" use typographic quotes, which the word scanner treats as literals.
- Python's last lines (`NameError: name 'total' is not defined`, `FileNotFoundError: [Errno 2] No such file or directory: 'data.txt'`, `IsADirectoryError: [Errno 21] Is a directory: 'logs'`) are stable from 3.10 on.
- YAML reads `yes`, `no` and `on` in a plain list as booleans, so they are quoted in the lexicon.

## 5. Findings for later steps

- **English is never a gate for the technical route,** and nothing technical gates English here: every edge stays inside D01 (TEIP-v0).
- **A fourth entry point.** A learner with no history may start the first English lesson on day one.
- **The free command note is provisional at most** (rubric, OREX-v0). Keyed controlled production carries the measurable evidence. ENGLISH_FOUNDATION_RULES' order (recognition → controlled → free) is kept.
- **B1, listening and speaking, and the pre-A1 bridge context are later steps** (TECP-v0 §8, §11).
