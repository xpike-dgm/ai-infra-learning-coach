# Explainable Planner & Decision Trace Specification — PDT-v0

**Adım:** 3G — Açıklanabilir planner  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `PDT-v0 — Planner Decision Trace`

Bu belge Adaptive Planner'ın bir görevi neden bugün seçtiğini, başka bir görevin neden seçilmediğini, bir branch'in neden beklediğini ve planın neden yeniden üretildiğini makine tarafından yeniden kurulabilir biçimde açıklamasını tanımlar.

Bağlayıcı kaynaklar:
- `docs/ADAPTIVE_PLANNER_SPEC.md` — 3A capacity
- `docs/TASK_TAXONOMY_SPEC.md` — 3B LearningNeed / TaskCandidate contract
- `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0
- `docs/MISSED_DAY_RECOVERY_SPEC.md` — SRR-v0
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0
- `docs/LEARNING_BEHAVIOR_RULES.md`

Ana ilke:

> **Açıklama planner kararından sonra uydurulmaz. Planner kararı verirken structured reason code ve decision trace üretir; kullanıcı açıklaması bu gerçek kayıttan türetilir.**

İkinci ilke:

> **Explainability, private chain-of-thought veya LLM düşünce kaydı değildir. Yalnız kararı belirleyen ürün state'i, policy sonucu, reason code ve seçme/eleme nedenleri saklanır.**

Üçüncü ilke:

> **Aynı state snapshot + aynı policy/config versions + aynı candidate set + aynı capacity input, aynı planı ve semantik olarak aynı karar trace'ini üretmelidir.**

---

# 1. 3G neyi çözer?

Planner artık şu sorulara canonical state üzerinden cevap verebilmelidir:

1. `Bu görev neden bugün geldi?`
2. `Diğer görev neden bugün gelmedi?`
3. `Bu konu/görev neden bekliyor veya bloke?`
4. `Neden retention/review yapıyorum?`
5. `Neden remediation/doğrulama yeni konunun önüne geçti?`
6. `Neden plan çalışma sırasında değişti?`
7. `Uzun aradan sonra neden eski görevler geri gelmedi?`
8. `Neden bildiğim kısmı atladım ama başka kısmı atlayamadım?`

3G bunları serbest metin sezgisiyle değil, 3A–3F karar zincirini kaydederek çözer.

---

# 2. Açıklamanın iki katmanı

## 2.1 Internal audit trace

Makine tarafından okunabilir, test edilebilir ve debug edilebilir ayrıntılı kayıttır.

İçerir:
- kullanılan state snapshot referansları,
- LearningNeed trigger'ları,
- validation/trust sonucu,
- PRG-v0 eligibility sonucu,
- PBR-v0 priority band + rank vector,
- duration/capacity fit sonucu,
- selected / blocked / deferred / superseded disposition,
- replan event chain,
- policy/config versions.

## 2.2 User-facing explanation

Kullanıcıya kısa, anlaşılır ve yargılayıcı olmayan açıklamadır.

Varsayılan UI açıklaması:
- 1 ana neden,
- gerekirse 1 yardımcı neden,
- gerektiğinde `neden değil?` ayrıntısına açılan ikinci katman

kullanır.

UI, internal rank vector'ün bütün alanlarını veya teknik metadata'yı dökmek zorunda değildir.

Kritik invariant:

```text
user_facing_explanation ⊆ facts_in_decision_trace
```

Açıklama trace'te olmayan yeni neden icat edemez.

---

# 3. LLM'nin rolü

V1 canonical planner kararını LLM vermez.

LLM ileride:
- reason code'ları doğal Türkçeye daha akıcı çevirebilir,
- aynı doğru nedeni kullanıcının seviyesine göre sadeleştirebilir,
- birden çok reason code'u kısa bir paragrafta özetleyebilir.

Ama LLM:
- priority band değiştiremez,
- blocked candidate'ı eligible yapamaz,
- trace'te olmayan bir prerequisite uyduramaz,
- `review_due` durumunu `unuttun` diye yeniden yorumlayamaz,
- capacity'yi keyfi değiştiremez,
- planner'ın gerçek nedenini başka bir nedenle değiştiremez.

LLM açıklaması kullanılırsa source reason code'lar ayrıca tutulur; template-based fallback her zaman mümkün olmalıdır.

---

# 4. Canonical `PlannerDecisionTrace`

Her plan generation veya replan için tek root trace üretilir.

```text
PlannerDecisionTrace
- trace_id
- trace_schema_version
- plan_id
- plan_version
- generated_at
- generation_kind: initial | replan | reentry
- previous_plan_id?
- replan_event_id?

- policy_versions:
    mastery_policy
    retention_policy
    capacity_policy
    task_taxonomy_policy
    priority_policy
    prerequisite_policy
    diagnostic_policy
    reentry_policy
    explainability_policy

- state_snapshot_ref
- curriculum_version
- content_bank_version?
- planner_config_version

- capacity_trace
- need_traces[]
- candidate_traces[]
- selected_task_refs[]
- plan_level_reason_codes[]
- invariant_checks[]
```

Trace full historical attempt log'u kopyalamaz. İlgili canonical state/evidence snapshot'larına referans verir.

---

# 5. `NeedDecisionTrace`

Priority task ID'den değil LearningNeed'den başladığı için explainability de önce need seviyesini kaydeder.

```text
NeedDecisionTrace
- need_key
- trigger_kind
- target_skill_ids[]
- target_objective_ids[]
- source_state_refs[]
- open_at_generation: true | false
- trigger_reason_codes[]
- priority_band
- priority_rank_vector
- priority_reason_codes[]
- starvation_state
- selected_candidate_id?
- final_disposition
- final_reason_codes[]
```

`final_disposition` baseline:

```text
selected
partially_served
eligible_not_selected
blocked
resolved_before_selection
no_valid_candidate
```

Bu sayede `neden bu ihtiyaç bugün var?` ile `neden bu task seçildi?` soruları birbirine karışmaz.

---

# 6. `CandidateDecisionTrace`

Her ciddi candidate alternative için bounded karar izi:

```text
CandidateDecisionTrace
- candidate_id
- need_key
- validation_status
- validation_reason_codes[]
- prerequisite_decision_ref
- eligibility
- priority_band
- planning_cost_minutes
- duration_fit_class
- final_disposition
- alternative_of_candidate_id?
- reason_codes[]
```

Candidate disposition baseline:

```text
selected
selected_split
selected_smaller_alternative
blocked_prerequisite
invalid_candidate
conditional_not_selected
eligible_lower_priority
eligible_capacity_deferred
superseded_same_need_alternative
duplicate_suppressed
resolved_before_selection
```

Aynı need için 20 alternative üretip hepsini sonsuza kadar trace'e koymak zorunlu değildir. Candidate generation bounded olmalı; trace planner'ın gerçekten değerlendirdiği bounded set'i kaydeder.

---

# 7. Reason code tasarım kuralları

Reason code:
- stable machine-readable kimliktir,
- UI string değildir,
- versioned semantics taşır,
- parametre/ref ile spesifikleşebilir,
- tek başına serbest metin açıklama değildir.

Format:

```text
namespace.reason_name
```

Örnek:

```text
need.verification_due
eligibility.blocked_hard_prerequisite
priority.p0_integrity_blocker
capacity.deferred_not_enough_time
reentry.stale_plan_not_replayed
```

Reason event gerekirse parametre taşır:

```text
ReasonRef
- code
- subject_ref
- related_refs[]
- values{}
```

Örnek:

```text
code = eligibility.blocked_hard_prerequisite
subject_ref = linked_list_candidate_07
related_refs = [skill.pointer_dereference]
```

---

# 8. Canonical reason-code family'leri

Aşağıdaki liste V1 baseline'dır. UI string'leri 7A–7G'de değişebilir; semantic code kimlikleri implementasyon için kararlı tutulur.

## 8.1 LearningNeed / state

```text
need.new_learning_available
need.continue_learning_active
need.weakness_detected
need.remediation_required
need.retention_review_due
need.verification_due
need.diagnostic_opportunity
need.reinforcement_opportunity
need.parallel_track_due
need.integration_opportunity
```

## 8.2 Validation / trust

```text
candidate.validated
candidate.invalid_content
candidate.invalid_evidence_contract
candidate.invalid_prerequisite_metadata
candidate.untrusted_for_high_stakes_use
candidate.duplicate_suppressed
candidate.same_need_alternative_superseded
```

## 8.3 Prerequisite / PRG-v0

```text
eligibility.ready
eligibility.ready_due_allowed
eligibility.conditional_uncertain
eligibility.soft_gap_support
eligibility.blocked_hard_prerequisite
eligibility.blocked_critical_verification
eligibility.blocked_strict_prerequisite_confidence
eligibility.invalid_prerequisite_metadata
eligibility.prerequisite_contamination_guard
```

`ready_due_allowed` açıklaması asla `beceriyi unuttun ama yine de geç` şeklinde sunulmaz. Doğru anlam: review due olmasına rağmen negatif evidence olmadığı için prerequisite hazır kabul edilir.

## 8.4 Retention / RVR-v0

```text
retention.review_due_not_failure
retention.overdue_priority_signal
retention.verification_due
retention.at_risk
retention.remediation_confirmed
retention.natural_reuse_satisfied
retention.no_auto_decay
```

`review_due` için kullanıcı açıklamasında `unutma` iddiası yasaktır.

## 8.5 Priority / PBR-v0

```text
priority.p0_integrity_blocker
priority.p1_repair_or_verify
priority.p2_maintain_or_continue
priority.p3_planned_progress
priority.p4_reinforce_or_optimize
priority.blocks_current_required_path
priority.blocks_next_ready_dependency
priority.criticality_advantage
priority.evidence_severity_advantage
priority.temporal_urgency_advantage
priority.starvation_promoted
priority.continuation_value
priority.decision_value
priority.track_balance_pressure
priority.stable_tie_break
```

User-facing explanation genellikle bütün rank vector'ü değil, sıralamayı gerçekten açıklayan ilk decisive difference'i kullanır.

## 8.6 Capacity / fit

```text
capacity.source_today_override
capacity.source_short_profile
capacity.source_normal_profile
capacity.source_intensive_profile
capacity.source_scheduled_default
capacity.selected_within_budget
capacity.split_to_fit
capacity.smaller_alternative_to_fit
capacity.deferred_not_enough_time
capacity.reserve_applied
capacity.reserve_relaxed_for_microtask
capacity.hard_budget_not_exceeded
capacity.no_penalty_user_stopped
```

## 8.7 Diagnostic / VDW-v0

```text
diagnostic.user_requested_fast_path
diagnostic.probe_selected
diagnostic.confirm_needed
diagnostic.critical_confirm_needed
diagnostic.partial_coverage_waiver
diagnostic.full_coverage_waiver
diagnostic.no_waiver
diagnostic.h0_required_for_waiver
diagnostic.prerequisite_blocked
diagnostic.component_not_separately_attributable
```

## 8.8 Re-entry / SRR-v0

```text
reentry.return_after_absence
reentry.absence_not_failure
reentry.absence_not_task_debt
reentry.stale_plan_not_replayed
reentry.current_state_regenerated
reentry.paused_checkpoint_candidate
reentry.incomplete_high_stakes_attempt_not_scored
reentry.due_inventory_not_daily_plan
```

## 8.9 Selection outcome

```text
selection.selected
selection.selected_split
selection.selected_smaller_alternative
selection.not_selected_lower_priority
selection.not_selected_capacity
selection.blocked_prerequisite
selection.invalid_candidate
selection.same_need_alternative_not_used
selection.resolved_before_selection
```

## 8.10 Replan

```text
replan.capacity_changed
replan.remaining_time_changed
replan.task_finished_early
replan.task_overran_estimate
replan.new_remediation_created
replan.new_verification_created
replan.evidence_state_changed
replan.prerequisite_state_changed
replan.topic_state_changed
replan.return_after_absence
replan.user_requested_extra_time
replan.user_stopped_session
replan.curriculum_version_changed
```

---

# 9. “Neden bugün bu görev?” açıklaması

Seçili task açıklaması şu zincirden türetilir:

```text
LearningNeed trigger
    + decisive priority reason
    + prerequisite eligibility if relevant
    + capacity/fit outcome if relevant
```

Örnek internal:

```text
need.verification_due
priority.p0_integrity_blocker
priority.blocks_next_ready_dependency
eligibility.ready
selection.selected
```

User-facing örnek:

> `Pointer doğrulaması bugün öne alındı; çünkü bu beceride çözülmemiş bir doğrulama var ve sıradaki çalışma buna bağlı.`

Bu cümlede `unutmuşsun`, `başarısızsın` gibi trace'in desteklemediği ek iddia yoktur.

---

# 10. “Neden diğer görev gelmedi?” açıklaması

Eligible fakat seçilmemiş need/candidate için final disposition zorunludur.

Örnek 1:

```text
priority.p3_planned_progress
selection.not_selected_capacity
capacity.deferred_not_enough_time
```

User-facing:

> `Bu çalışma hâlâ gerekli; ancak bugünkü süre daha yüksek öncelikli işlerle dolduğu için bugüne alınmadı. Bu bir borç veya başarısızlık değildir.`

Örnek 2:

```text
selection.not_selected_lower_priority
```

User-facing:

> `Bugün daha yüksek öncelikli doğrulama ve onarım işleri bulunduğu için bu çalışma sonraki planlarda yeniden değerlendirilecek.`

Örnek 3:

```text
candidate.same_need_alternative_superseded
```

User-facing:

> `Aynı öğrenme ihtiyacını karşılayan daha uygun bir görev seçildi.`

---

# 11. “Neden bu branch/görev bekliyor?” açıklaması

Blocked candidate exact blocker'a referans vermelidir.

Internal:

```text
eligibility.blocked_hard_prerequisite
related_refs = [skill.pointer_dereference]
```

User-facing:

> `Linked List çalışması şimdilik bekliyor; çünkü bu görev için Pointer Dereference becerisi gerekli ve henüz hazır değil.`

Critical unresolved verification örneği:

> `Bu yeni çalışma, kritik prerequisite doğrulaması tamamlanana kadar bekliyor. Diğer bağımsız çalışmalar devam edebilir.`

Kural:

> `Topic locked` gibi kaba bir açıklama tek başına yeterli değildir; mümkünse gerçek canonical Skill blocker gösterilir.

---

# 12. “Neden retention yapıyorum?” açıklaması

`review_due` durumunda canonical ifade:

> `Bu beceriyi yeniden doğrulama zamanı geldi. Bu, unuttuğun anlamına gelmez.`

`verification_due` durumunda:

> `Önceki performansla çelişen yeni bir sonuç olduğu için farklı bir görevle yeniden doğrulama gerekiyor.`

`at_risk` durumunda:

> `Birden fazla sinyal bu beceriyi yeniden kontrol etmeyi değerli kılıyor.`

Planner evidence'ın desteklemediği `X gündür çalışmadığın için %Y unuttun` gibi metin üretmez.

---

# 13. Diagnostic açıklaması

Partial waiver örneği:

> `Address ve pointer declaration bölümlerini bağımsız olarak gösterdiğin için bu başlangıç anlatımlarını atlıyoruz. Dereference hâlâ doğrulanmadığı için o bölüm normal akışta kalacak.`

No-waiver:

> `Bu diagnostic bazı kısımları güvenilir biçimde atlamak için yeterli kanıt üretmedi; normal öğrenme akışından devam ediyoruz.`

Bu sonuç otomatik `başarısız oldun` veya `remediation_required` anlamına gelmez.

---

# 14. Re-entry açıklaması

Uzun aradan dönüşte plan-level reason code'lar:

```text
reentry.return_after_absence
reentry.absence_not_task_debt
reentry.stale_plan_not_replayed
reentry.current_state_regenerated
```

User-facing:

> `Eski günlük görevleri borç olarak taşımadık. Bugünkü plan mevcut beceri durumun ve ayırdığın süre üzerinden yeniden oluşturuldu.`

Absence tek başına mastery düşüşü olarak açıklanamaz.

---

# 15. Replan event contract

Her runtime replan kendi event kaydını üretir:

```text
PlannerReplanEvent
- event_id
- event_type
- occurred_at
- previous_plan_id
- previous_plan_version
- changed_input_refs[]
- preserved_completed_task_refs[]
- preserved_evidence_refs[]
- invalidated_unstarted_task_refs[]
- new_trace_id
- reason_codes[]
```

Replan eski planı sessizce mutate etmek yerine yeni `plan_version` üretir.

Kritik invariant:
- tamamlanmış evidence korunur,
- yalnız kalan/unstarted plan yeniden çözülür,
- replan trigger trace'te görünür,
- user capacity azaldı diye completed task geri alınmaz,
- new remediation yeni güne otomatik ek süre eklemez.

---

# 16. End-to-end deterministic planner pseudocode

Aşağıdaki pseudocode 3A–3G karar sırasının canonical orkestrasyonudur. Uygulama dili değildir; davranış sözleşmesidir.

```text
function build_daily_plan(input, previous_plan = null, replan_event = null):
    snapshot = load_current_bounded_state(input.user_id, input.date)

    trace = begin_trace(
        snapshot_ref = snapshot.ref,
        policy_versions = current_policy_versions(),
        generation_kind = classify_generation(previous_plan, replan_event)
    )

    if is_reentry(snapshot, input):
        apply_SRR_v0_reentry_refresh(snapshot, trace)
        # stale unstarted plans/candidates are not replayed

    capacity = resolve_3A_capacity(input.capacity, previous_plan, replan_event)
    trace.capacity_trace = capacity.trace

    mastery_state = read_GRE_v0_state(snapshot)
    retention_state = refresh_RVR_v0_due_states(snapshot.now, snapshot)
    remediation_state = read_current_remediation_and_verification(snapshot)

    prereq_readiness = resolve_PRG_v0_skill_readiness(
        mastery_state,
        retention_state,
        remediation_state
    )

    topic_states = derive_topic_states(
        mastery_state,
        retention_state,
        remediation_state,
        prereq_readiness,
        current_curriculum_version
    )

    needs = generate_current_learning_needs(
        mastery_state,
        retention_state,
        remediation_state,
        topic_states,
        current_curriculum,
        diagnostic_waivers,
        paused_progress
    )

    needs = dedupe_semantic_needs(needs)
    record_need_triggers(trace, needs)

    candidate_pool = []

    for need in needs:
        candidates = generate_bounded_candidates(need)

        for candidate in candidates:
            validation = validate_candidate(candidate)
            if validation.invalid:
                record_invalid(trace, need, candidate, validation)
                continue

            prereq_decision = evaluate_PRG_v0(candidate, prereq_readiness)
            record_prereq(trace, candidate, prereq_decision)

            if prereq_decision.blocked:
                record_blocked(trace, need, candidate, prereq_decision)
                continue

            candidate_pool.add(candidate)

    ranked_needs = rank_open_needs_PBR_v0(needs, candidate_pool)
    record_priority(trace, ranked_needs)

    plan = empty_plan(capacity)

    for need in ranked_needs:
        alternatives = eligible_candidates_for_need(candidate_pool, need)
        alternatives = stable_candidate_order(alternatives)

        choice = choose_best_candidate_alternative(need, alternatives)

        if choice == none:
            record_no_valid_candidate(trace, need)
            continue

        if fits(choice, plan.remaining_planning_budget):
            add(plan, choice)
            record_selected(trace, need, choice)
            suppress_same_need_alternatives(trace, alternatives, choice)
            continue

        split = safe_split_if_allowed(choice, plan.remaining_planning_budget)
        if split != none:
            add(plan, split)
            record_selected_split(trace, need, choice, split)
            continue

        smaller = find_smaller_eligible_alternative(
            need,
            alternatives,
            plan.remaining_planning_budget
        )
        if smaller != none:
            add(plan, smaller)
            record_smaller_alternative(trace, need, smaller)
            continue

        record_capacity_deferred(trace, need, choice)
        # need remains open; this is not tomorrow debt

    mark_remaining_eligible_needs_with_disposition(trace, plan, ranked_needs)
    run_planner_invariant_checks(plan, trace)

    return PlanResult(plan, finalize_trace(trace))
```

---

# 17. Selection sırasında önemli sıra

Canonical gate order:

```text
current state
→ LearningNeed generation
→ bounded TaskCandidate generation
→ validation / trust
→ PRG-v0 eligibility
→ PBR-v0 priority/rank
→ 3A capacity fit / split / smaller alternative / defer
→ PlannedTask
→ PlannerDecisionTrace
```

Priority hiçbir zaman invalid veya blocked candidate'ı kurtaramaz.

Capacity ise semantic priority'yi yeniden yazmaz; yalnız seçimin bugünkü bütçeye fiziksel olarak sığıp sığmadığını belirler.

---

# 18. Daha yüksek priority task sığmıyorsa trace davranışı

Örnek:
- P1 task = 25 dk, kalan capacity = 12 dk,
- task güvenli bölünemiyor,
- aynı need için 10 dk eligible alternative yok,
- P3 task = 10 dk ve eligible.

Planner P1'i bugün seçemeyip P3'ü seçebilir.

Trace açıkça göstermelidir:

```text
P1 need:
  priority.p1_repair_or_verify
  capacity.deferred_not_enough_time

P3 need:
  priority.p3_planned_progress
  capacity.selected_within_budget
```

Böylece plan sırası `P3, P1'den daha önemliydi` diye yanlış açıklanmaz. Doğru neden `P1 bugünkü kalan bütçeye güvenli biçimde sığmadı`dır.

---

# 19. Explainability için yasak anti-pattern'ler

```text
LLM: "Bence bugün pointer çalışmak daha iyi"
```
Trace yoksa canonical planner açıklaması değildir.

Yasaklar:
- freeform LLM rationale'ını source of truth yapmak,
- private chain-of-thought kaydetmek/göstermek,
- task seçilmediğinde sebep uydurmak,
- `review_due = unuttun` demek,
- `absence = geri kaldın/borcun var` demek,
- `critical` etiketini gerçek blocker olmadan P0 diye açıklamak,
- duration fit nedeniyle seçilmeyen yüksek-priority işi `düşük öncelik` diye etiketlemek,
- Topic-level kaba kilidi gerçek Skill blocker yerine kullanmak,
- yüzlerce rejected candidate için sınırsız trace biriktirmek.

---

# 20. Performance / storage

D-028 gereği trace sistemi uygulamayı ağırlaştırmamalıdır.

V1 beklentileri:
- current plan için bounded trace,
- full attempt history kopyası yerine refs/snapshot IDs,
- reason code + küçük değerler,
- indexed plan/trace lookup,
- UI thread'de ağır JSON diff veya LLM explanation zorunluluğu yok,
- eski trace'ler analytics/debug ihtiyacına göre compact saklanabilir,
- user-facing explanation template/local mapping ile anlık üretilebilir.

Exact retention/log compaction policy 8C/15B/17E'de kesinleşir.

---

# 21. User-facing explanation contract

Bir selected PlannedTask için V1 en az şunu gösterebilir:

```text
why_today.primary
why_today.secondary?
```

Bir blocked/deferred öğe detay ekranında:

```text
why_not_today.primary
related_skill?
reconsideration_condition?
```

Örnek reconsideration condition:
- `Pointer doğrulaması tamamlanınca yeniden değerlendirilecek.`
- `Bugünkü kapasite dolduğu için sonraki planda yeniden değerlendirilecek.`

Bu tarih sözü değildir. Need açık kaldığı sürece fresh replanning yapılır.

---

# 22. Audit / reconstructability

Debug veya Test AI şu soruya cevap verebilmelidir:

> `Bu plan neden böyle çıktı?`

Bunun için yalnız şu veriler yeterli olmalıdır:
- canonical state snapshot ref,
- candidate metadata/version,
- policy/config versions,
- capacity input,
- decision trace.

Aynı input tekrar çalıştırıldığında semantik karar eşdeğer olmalıdır. Timestamp/UUID gibi nondeterministic alanların aynı olması gerekmez.

---

# 23. 3H simulation invariants

3H en az aşağıdaki invariant'ları doğrulamalıdır:

1. Aynı canonical input + versions → aynı selected task sırası ve eşdeğer reason trace.
2. `blocked` veya `invalid` candidate asla selected olmaz.
3. User explicit extension yoksa hard capacity aşılmaz.
4. Her selected task bir açık LearningNeed'e ve en az bir gerçek selection reason'a bağlanır.
5. Değerlendirilen fakat seçilmeyen eligible need/candidate final disposition taşır.
6. Hard prerequisite block exact Skill ref ile açıklanabilir.
7. `review_due` kullanıcı açıklamasında forgetting/failure diye yanlış sunulmaz.
8. Absence negative evidence/debt/starvation olarak açıklanmaz.
9. Re-entry eski unstarted PlannedTask backlog'unu replay etmez.
10. High-priority task capacity'ye sığmadığında lower-priority seçim yapılırsa trace gerçek `capacity fit` nedenini korur.
11. Aynı semantic need için duplicate selected task oluşmaz; izinli mini-chain dışında alternatives suppress edilir.
12. Partial diagnostic yalnız validated Objective'leri waive eder ve açıklama bunu doğru yansıtır.
13. Bir blocked Skill yalnız bağımlı branch'i bekletir; independent branch açıklanabilir biçimde devam eder.
14. Replan completed evidence'ı korur ve yalnız remaining planı yeniden çözer.
15. User-facing açıklamadaki her factual neden internal trace'te bulunur.
16. LLM olmadan template fallback ile temel açıklama üretilebilir.
17. Trace bounded/incremental tutulabilir; full-history scan gerektirmez.
18. Priority `critical` etiketi tek başına P0 açıklaması üretmez.
19. Review due nedeniyle prerequisite otomatik hard-block olmaz.
20. Deferred task `tomorrow debt` olarak açıklanmaz.

---

# 24. 3G acceptance criteria

3G PASS için:

1. Planner decision trace schema tanımlı.
2. Need-level ve candidate-level decision disposition ayrılmış.
3. Stable reason-code family'leri tanımlı.
4. Selected / blocked / invalid / lower-priority / capacity-deferred / alternative-superseded durumları açıklanabiliyor.
5. PBR-v0 rank ile PRG-v0 eligibility ve 3A capacity birbirine karıştırılmıyor.
6. RVR-v0 `review_due` semantics korunuyor.
7. VDW-v0 partial/full/no-waiver kararları açıklanabiliyor.
8. SRR-v0 no-debt re-entry davranışı açıklanabiliyor.
9. Replan event chain/version contract var.
10. User-facing explanation trace'ten türetiliyor; LLM source of truth değil.
11. End-to-end deterministic planner pseudocode mevcut.
12. D-028 performans kuralına uygun bounded trace yaklaşımı var.
13. 3H simulation için test edilebilir invariant set'i tanımlı.

---

# 25. Final 3G kararı

**Final model:** `PDT-v0 — Planner Decision Trace`

Özet:

```text
Current State
    ↓
LearningNeed
    ↓
Candidate validation
    ↓
PRG-v0 eligibility
    ↓
PBR-v0 priority
    ↓
3A capacity fit
    ↓
PlannedTask
    ↓
PDT-v0 structured decision trace
    ↓
Template / optional LLM paraphrase
    ↓
User-facing "Neden?" explanation
```

Planner'ın gerçek kararı ile kullanıcıya anlattığı neden aynı canonical structured kaynaktan gelmek zorundadır.
