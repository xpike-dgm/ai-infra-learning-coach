# Progress / Weakness Presentation Specification — PWPX-v0

**Stage step:** 16C — Progress / weakness gösterim kuralları  
**Status:** ACCEPTED — independent 16C QA PASS  
**Decision:** `D-124`  
**Model:** `PWPX-v0 — Topic state, topic_detail, the weakness context and the Technical English profile, read from the Skill states the engines wrote`  
**Behaviour it implements:** `TSM-v0` §§2–16; `SPWX-v0` §§6, 8–11; `TEPM-v0` (base profile, review semantics, uneven profile, B2+ extension); `WLRM-v0` (localized weakness); `VDSX-v0` §topic_state_tones; `UXIA-v0` edges  
**Code:** `core-model` (`TopicFacts.kt`, `EnglishProfileFacts.kt`, `ProgressFacts.kt` fields), `core-ports` (two refinements), `data-persistence` (`attemptedResources`), `data-curriculum` (`lessonItemsFor`), `core-application` (`TopicQuery.kt`, `EnglishProfileQuery.kt`, `ProgressQuery.detail`), `core-presentation` (`TopicPresentation.kt`, `EnglishProfilePresentation.kt`, `ProgressPresentation.kt`, `Tone.kt`), `app-ui` (`TopicScreen.kt`, `EnglishProfileScreen.kt`, `ProgressScreen.kt`), `app-wiring`  
**Persistence / schema:** unchanged; nothing is written

## 1. Purpose

> **Bir konu, bir zayıflık ve Teknik İngilizce profili öğrenciye nasıl gösterilir — becerilerin kendi durumlarından, hiçbir ortalama, yüzde, seviye iddiası ya da kapı uydurmadan?**

Primary invariant:

> **A Topic's state is orchestration, not mastery.** It is `TSM-v0`'s first rule that holds over three things: its Skills' own states (16A's one function), whether work on it was ever recorded, and whether each Objective's lesson was worked or diagnostically waived. It is never an average, a percentage or a prerequisite claim. A Topic that has not been started is locked only when none of its Skills can start, and a started Topic never goes back to locked.
>
> **A weakness is shown where it was located.** A hypothesis appears only as the catalog's own question. A remediation is shown with what closes it. Neither describes the learner.
>
> **The Technical English profile is `TEPM-v0`'s band rule over the English Skills' states.** Its history is replayed from the evidence. It is never a general or official level and never a number, and English is never a gate.
>
> Nothing is written.

---

# 2. What was found

- **The first entry rule locked Topics the planner was offering.**
  - The user first chose: an unstarted Topic is locked until every hard edge from a Skill outside it is satisfied.
  - On the shipped graph, 12 of the 27 Topics would then show "Kilitli" while one of their Skills could already start. In `topic.python.values_types`, for example, `state_assignment_model` is offered on the first day.
  - The user revised the rule the same day (2026-10-24): an unstarted Topic is available when one of its Skills can start, and locked only when none can.
- **A new learner sees four Topics open and twenty-three waiting.** The four are the Topics of the four entry Skills: the planner's own set, seen per Topic.
- **Lesson items never appear in practice tasks.** The 67 teach-task items and the 1023 other task items are disjoint, and all 65 Objectives have a teach task. So "an attempt on a lesson item" says exactly that the lesson was worked.
- **The `topic_state` and `english_profile` tables have no writer, and need none.**
  - Both views are derived when they are read, and nothing reads a stored copy.
  - A Topic depends on outside Skills whose changes do not re-assemble it, which is the reverse-invalidation loop 16A named.

---

# 3. Decisions

User decisions (2026-10-24):

- **Scope:** Topic state, `topic_detail`, the weakness and misconception context in `skill_detail`, and the Technical English profile. Delivered as core code plus real Compose screens.
- **Topic entry:** an unstarted Topic is available when one of its Skills is open by the gate (`PRG-v0` §7). It is locked when none is, and a locked Topic names the outside Skills it waits for. This replaced the first choice, which required every outside edge to be satisfied (see §2).
- **Coverage gate:** an Objective is covered when its teach task has a recorded attempt or when a 13F diagnostic waived it. Mastery remains a separate gate.
- **English:**
  - The historical highest band is replayed from the evidence; no table is added.
  - An unpublished band is named "henüz yayımlanmadı", never incomplete or failed.
  - The CEFR anchors come from 7B's `alignment.yaml`, and a validator checks them.

Assistant defaults:

- **Every placed Skill is required.** Every Skill a Topic places is required for it.
- **Weakening:**
  - A Topic is weakening when it is mastered and one of its Skills has a retention signal: `at_risk`, or an open verification.
  - `review_due` alone keeps it mastered.
- **Started:** a Topic is started by any recorded attempt on an item of one of its Skills, or by any evidence on one of its Objectives.
- **Recomputing:** a started Topic with a Skill whose state cannot be told is recomputing.
- **No lesson:** an Objective with no teach task is never covered, and the screen says so.
- **Metadata problems:** a Skill whose prerequisite metadata the gate cannot read is not startable. A locked Topic names this as a metadata problem.
- **Historical band:** the band uses the replayed mastery decision.
- **B2+ extension:** only `no_extension_evidence` is produced, because no extension item is authored yet.
- **English profile links:** the profile names its Skills as text, because there is no accepted edge to `skill_detail`.

---

# 4. Topic state (`TSM-v0` §10)

| Started? | First rule that holds | State | `TSM-v0` §14 code |
|---|---|---|---|
| no | one of its Skills can start | `available` · Hazır | `hard_prerequisites_satisfied` |
| no | otherwise | `locked` · Kilitli | — |
| yes | a Skill is `remediation_required` | `remediation_required` · Pekiştirme Gerekli | `remediation_triggered` |
| yes | every Skill confirmed, every Objective covered, a retention signal on one | `weakening` · Tekrar Gerekebilir | `retention_risk_confirmed` |
| yes | every Skill confirmed, every Objective covered | `mastered` · Öğrenildi | `mastery_gate_satisfied` |
| yes | otherwise | `learning` · Öğreniliyor | `topic_started` |

- **Confirmed** means the mastery engine says `confirmed_current` or `confirmation_verification_due`. An open verification keeps mastery (`GRE-v0` §16.2).
- **Mastery does not decay with time.** So "was mastered" is read from the Skills' current state; no stored Topic value is consulted.
- **Coverage** is one of `lesson_worked`, `diagnostic_waiver`, `not_covered` or `no_lesson_authored`. Only the first two count as covered.
- **Tones** follow `VDSX-v0`: locked and available are neutral, learning active, mastered positive-confirmed, weakening neutral, and remediation_required attention. No Topic state can wear the fault tone.

`topic_detail` shows:

- the state and what it means;
- the facts behind it:
  - for a locked Topic, the Skills it waits for;
  - the Skills that need remediation;
  - the Skills with a retention signal;
  - the Skills not yet confirmed;
- each of its Skills, opening `skill_detail`;
- each Objective's coverage;
- that placement is not a prerequisite.

---

# 5. Weakness context (`SPWX-v0` §10)

Each catalogued misconception that 14B's memory holds a signal for is shown on its Objective:

| Signal | Shown as |
|---|---|
| hypothesis | "Açık soru: …" — the catalog's own question, nothing more |
| supported | "Yerel bir sinyal: …" |
| confirmed | "Bu hedefte pekiştirme: …" |
| resolved | "Geçmişte: … — artık güncel bir sorun değil" |

The Skill's weakness axis adds a short context block. For a remediation, the block also says what closes it: fresh, clean evidence that is unassisted, direct, verified, prerequisite-valid and in another context. Finishing a task never closes it.

The context uses no failure language and no ranking. A weakness is never broadcast up to a Topic or a domain, or down to dependent Skills.

---

# 6. Technical English profile (`TEPM-v0`)

- **Rule:** the profile shows the highest band all of whose anchor Skills at or below it are currently confirmed. Confirmed means the Skill's state is `confirmed_current`, `confirmed_review_due` or `confirmation_verification_due`; `remediation_required` never counts.
  - The same rule gives the current band from the Skills' states now.
  - It gives the historical band from the mastery engine replayed over each English Skill's evidence in recording order.
  - The historical band is never below the current one.
- **Status:** `not_yet_complete`, `complete_current`, `complete_review_due` or `complete_verification_due`. A review due never demotes the band.
- **Bands:** each band is `complete`, `incomplete` (listing the exact Skills not yet confirmed), `not_published` or `recomputing`. Published now: A1 5/5, A2 5/5, B1 0/5.
- **Confirmation source:** a confirmed Skill whose Objective a diagnostic waived shows `validated_entry_evidence`.
- **Wording:**
  - The profile uses `TEPM-v0`'s allowed wording, for example "Teknik İngilizce — A1 temel profili doğrulandı" or "A1 temeli tamam; A2 becerileri gelişiyor".
  - The scope note says it is not a general English level, an official CEFR level or a certificate.
  - The gate note says English gates no technical work.

---

# 7. Not done here

| Loop | Owner |
|---|---|
| Write the `topic_state` and `english_profile` projections | 18E |
| Planner reading of Topic state (`TSM-v0` §13) | 18B |
| B2+ extension content | 20 |
| `technical_english_profile → skill_detail` edge | 17 |
| Topic transition history (`TSM-v0` §14) | 18E |
| `TEIP-v0` integration context | 18D |
| Cost of the English replay | 18E |
| The disposition reason in traces (13D sent it to 16C; not shown here) | 16D |
| T6 and device | 19 |

**Not run: T6.** The screens have not been seen on a device.

**Next step:** **16D — Ayarlar**. It needs a fresh PRE and the user's new explicit approval.
