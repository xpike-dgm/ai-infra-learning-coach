# 16C — Progress / weakness presentation: research synthesis

**Step:** 16C — Progress / weakness gösterim kuralları (`PWPX-v0` / `D-124`)  
**Question:** how should a Topic, a located weakness and a Technical English profile be shown — read from the Skill states the engines wrote, without an average, a percentage, a level claim or an invented gate?

## 1. Internal contracts (binding)

- **`TSM-v0`** (`docs/TOPIC_STATE_MACHINE.md`):
  - six states; Topic state is derived orchestration, never mastery;
  - §10's resolution order; a started Topic never becomes `locked` again;
  - §12 keeps coverage separate from mastery;
  - §14's reason codes;
  - §16's invariants: completion alone never masters, and time alone changes nothing.
- **`SPWX-v0 / D-072`** (`ux/8e_progress_skill_weakness/progress.yaml`):
  - §6 the locked Topic labels, with no percentage and no average;
  - §9 `topic_detail`, where placement is not a dependency chain;
  - §8 `skill_detail` keeps its `remediation_context_optional` and `integration_or_english_context_optional`;
  - §10's weakness display rules: a hypothesis is at most an open question, and finishing a task never closes a remediation;
  - §11 the English profile uses the same eight states.
- **`TEPM-v0 / D-067`** (`curriculum/english/7e_mastery_profile/policy.yaml`):
  - the base band rule and the four status values;
  - review due never demotes a band;
  - an uneven profile is first-class;
  - the historical band is kept;
  - B2+ is a per-capability extension;
  - allowed and forbidden wording, and fixtures E7E-F01..F18.
- **`TECP-v0`** (`curriculum/english/7b_cefr_progression/alignment.yaml`): each canonical English Skill's anchor band, five per band.
- **`VDSX-v0`** `topic_state_tones`: one tone per state, and no fault tone.
- **`UXIA-v0`**:
  - accepted edges `progress → topic_detail`, `topic_detail → skill_detail` and `progress → technical_english_profile`;
  - no `technical_english_profile → skill_detail` edge.
- **16A `SKAX-v0`**: one Skill derivation, and "recomputing" rather than a guess.
- **16B `LHRX-v0`**: state is replayed from evidence, and `MasteryEngine.replay` decides mastery in recording order.

## 2. External sources

- **Common European Framework of Reference for Languages.** https://en.wikipedia.org/wiki/Common_European_Framework_of_Reference_for_Languages, read 2026-10-24.
  - The six levels are described as can-do statements of what a learner can do in reception, production, interaction and mediation.
  - The authors stressed that the framework was not written mainly as a basis for testing.
  - Certificates and exams are issued by separate bodies that map their exams to the levels.
  - Used for:
    - the profile is a qualified, text-first Technical English description and never "your English is B1";
    - no official level and no certificate is claimed;
    - the band is shown as the Skills behind it, never as a number.
- **Mastery learning.** https://en.wikipedia.org/wiki/Mastery_learning, read 2026-10-24.
  - A unit is mastered against a fixed standard, not by time spent.
  - A learner moves on once the unit's objectives are demonstrated.
  - Formative checks diagnose, and the correctives that follow are aimed at the particular difficulty, ideally without grades.
  - Used for:
    - a Topic is mastered only when every Skill is confirmed and every Objective is covered, never by finished lessons or elapsed days;
    - a remediation names the Objective it is about and what evidence would close it, not the learner.

## 3. Synthesis

- **A Topic reads its Skills; it never averages them.** Every Skill state is 16A's row, so a Topic can never contradict the Skills shown beside it. The Topic only adds what `TSM-v0` needs on top:
  - whether work started;
  - whether each Objective's lesson was worked or waived.
- **Entry is read through the gate.** The first rule, "every outside edge satisfied", disagreed with the planner on the shipped graph: 12 of 27 Topics. "One Skill can start" can never disagree, because it asks the same gate the planner asks.
- **History without a stored band.** A remembered "highest band" would be a second truth. Replaying the mastery engine over the English Skills' evidence gives the same band rule over time, and it can never sit below the current band.
- **Weakness at the place it was found.** The catalog's own question is the only form a hypothesis takes. The closure condition is stated, so a finished task is never read as a fixed one.
