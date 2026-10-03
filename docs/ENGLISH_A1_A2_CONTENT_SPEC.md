# English A0→A1/A2 Starter Content Specification — EAAX-v0

**Stage step:** 15F — English A0→A1/A2 başlangıç paketi  
**Status:** ACCEPTED — independent 15F QA PASS  
**Decision:** `D-119`  
**Model:** `EAAX-v0 — English A0→A1/A2 starter content`  
**Behaviour it implements:** 6C's Technical English route (D01); `EED-v0`; `TECP-v0` §3, §7–§9; `TEIP-v0` (no global English gate; translation/gloss integrity §10.1); `ENGLISH_FOUNDATION_RULES`; `KGC-v0` §27–§28; `AIV-v0`; `GRE-v0`; `D-113` (incremental packages); `D-117` (the user's standing approval)  
**Content:** `curriculum/content/15f_english_a1_a2/` → `android/app-wiring/src/main/assets/curriculum_package_v6.txt` (generated; never edit it by hand)  
**Persistence / ports / schema / Kotlin main code:** unchanged

## 1. Purpose

English is new for this learner, and the binding rule says an English item may never ask for a word or a structure that has not been taught. 15F answers:

> **Bir İngilizce item'ın öğrenciden öğretilmemiş bir kelime ya da yapı istemediği nasıl garanti edilir?**

Primary invariant:

> **Every English word an item shows is a word that a lesson the item may rely on teaches.** That means the item's own lesson, a hard prerequisite's lesson, or a lesson the item declares. A word no lesson teaches is refused by the build. A meaning key cites its source; a tool message is produced by the real tool and shown verbatim. English is never a gate for anything technical.

---

# 2. What was found while writing content

- **A per-lesson lexicon is the only honest guard for vocabulary.** The earlier notations listed code constructs; English needs words. 15F adds `lexicon`, the words each lesson teaches. The builder reads every English word an item shows and refuses unknown words and words the item may not rely on.
- **Literals are not vocabulary.** These are `code` in backticks, 'quoted' names, tokens with non-letters (notes.txt, -h) and mixed-case identifiers (NameError). Teaching `IsADirectoryError` as a word would also leak an answer.
- **YAML reads `yes`, `no` and `on` as booleans,** so they are quoted in the lexicon.
- **Tool messages differ by tool version and shell.**
  - This Ubuntu's coreutils are uutils, while GNU is used elsewhere.
  - `mkdir` and `git` (outside a repository) print different messages, and bash's "command not found" differs between interactive and non-interactive shells.
  - Only stable messages are used: ls, cat, Python exceptions, and gcc's "undeclared" and "expected".
- **Real help pages contain far more words than an A2 learner has.** The documentation items use short authored excerpts that keep the real conventions (man-pages(7) headings, "[ ] optional", `(default: N)`).

---

# 3. Decisions

- **The user's approval is standing (D-117).** No product decision for the user came up.
- **Assistant defaults under D-117:**
  - the ten A1/A2 Skills;
  - the per-lesson lexicon enforced by the builder;
  - Turkish prompts and options, with the English stimulus measured (TEIP-v0's bilingual scaffold);
  - authored help excerpts;
  - only stable tool messages;
  - 8 keyed items per Objective.

---

# 4. Scope

- **Skills:** 7B's A1 and A2 anchor Skills of D01 as 6C decomposed them.
- **Size:** 10 Skills, 10 Objectives and 9 hard edges, all inside English (TECP-v0 §7 band monotonicity). B1 and the B2+ extension come later.
- **Entry point:** `recognize_core_technical_labels` has no prerequisite, so it is a **fourth entry point**.
- **No additions,** and nothing technical waits on English.

| Band | Skill | Objective direct type | Items |
|---|---|---|---|
| A1 | recognize_core_technical_labels | recognition | 8 |
| A1 | technical_noun_phrase_recognition | reading_comprehension | 8 |
| A1 | be_and_simple_present_comprehension | reading_comprehension | 8 |
| A1 | imperative_instruction_comprehension | reading_comprehension | 8 |
| A1 | preposition_function_word_comprehension | reading_comprehension | 8 |
| A2 | negation_question_comprehension | reading_comprehension | 8 |
| A2 | follow_bilingual_technical_instruction | reading_comprehension | 8 |
| A2 | read_simple_terminal_error_fragments | reading_comprehension | 8 (tool messages) |
| A2 | documentation_navigation | reading_comprehension | 8 |
| A2 | write_command_result_note | written_or_spoken_production | 8 controlled + 2 rubric |

---

# 5. How a key is checked

| Kind | What is required |
|---|---|
| `reference` | The key's meaning cites its source: Wiktionary for words; Wikipedia's articles on the imperative mood, do-support, the simple present and English prepositions for structures; man-pages(7) and man(1) for help conventions. The independent review decides it. |
| `shell` | The tool's message is produced by the real tool in Linux (C.UTF-8), and the shown line must appear verbatim. |
| `rubric` | A free command note, judged by its rubric (OREX-v0). Provisional at most. |

Controlled production (fill-in, word order, a short pattern note) is judged against an accepted-answer list that covers every reasonable correct form. Turkish options are not scanned; English options are.

Result:
- 82 items: 72 reference, 8 shell and 2 rubric.
- 50 explanations, 20 misconceptions and 50 tasks.
- Every item and explanation was independently reviewed and passed.

---

# 6. Changes to accepted code

- `tools/build_curriculum_package.py`:
  - `english_words`;
  - the lexicon check (`unknown_word:` / `word:` hidden prerequisites);
  - `sources_of`.

  15A–15E rebuild byte-identically.
- `app-wiring`: the generated sixth asset.
- Kotlin main code, the runner, the schema, the ports and the store are unchanged.

---

# 7. Verification

- **Suites:**
  - `ShippedEnglishPackageTest` reads the real sixth asset with the first five and checks that:
    - it is published and closed inside English;
    - the first English Skill has no prerequisite;
    - a tool's message is shown word for word;
    - every Objective is measurable;
    - every item is judged by exactly one thing;
    - no English item allows the terminal;
    - every Skill is served, and everything is validated.
  - `ShippedCourseTest` publishes all six assets into the real SQLite schema on the JVM. A learner with no history may start four entry Skills, now including the first English lesson, and nothing technical waits on English.
- **Mutation:** 8/8 detected. It targets the word scanner and the lexicon check, using the real build plus two planted vocabulary errors.
- **Validator:** `tools/validate_english_a1_a2.py` rebuilds all six packages: 119/119. Its own mutation test detected 36/36, and the sweep passed 61/61. 1025 JVM tests passed.
- **Not run: T6.** Nothing ran on the device. SQLite publication ran on the JVM only.

---

# 8. Open loops

| Loop | Owner |
|---|---|
| documentation_navigation's graph (help text uses imperatives, prepositions and noun phrases; soft edges?) | 15H |
| generous difficulty labels on some isomorph items | 15H |
| item pool size in 15A–15D (~5 per Objective) | the user (pool-expansion step awaiting decision) |
| listening, speaking, B1 and the pre-A1 bridge context | later steps |
| minute calibration | 18B |
| T6 and on-device ingestion | 19 |

---

# 9. Anti-patterns explicitly rejected

- An English word in an item that no lesson the item may rely on teaches.
- A lexicon word its lesson never explains.
- A tool message shown that the tool does not print.
- A meaning key with no source, or a source credited with a claim it does not make.
- Refusing a correct learner because the accept list was too narrow.
- English as a gate for anything technical.

---

**Next step:** **15G — Assessment content** (fresh PRE; the user's approval is standing under D-117).
