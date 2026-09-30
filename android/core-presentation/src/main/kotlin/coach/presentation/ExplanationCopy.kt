package coach.presentation

import coach.model.ReasonCatalog

/**
 * `PDT-v0` §3's template fallback (12E): one Turkish sentence per reason code, so the explanation works
 * with no model at all. An LLM may later paraphrase these; it may not replace the code they come from.
 *
 * Wording is working microcopy owned by 14. What is canonical here is what the sentences may and may not
 * claim, and a check holds every one of them to it:
 * - `review_due` is due, never forgotten (`RVR-v0`, `PDT-v0` §12),
 * - absence is not failure, debt or falling behind (`SRR-v0`, §14),
 * - work deferred for time is never called less important (§18),
 * - a waiting task names its real Skill blocker when the trace has one (§11),
 * - no percentage, score or streak.
 *
 * It lives in core, not in the UI, for the same reason `DayCopy` does (11E): what an explanation claims is
 * the safety-critical part of it, and that has to be testable as plain JVM code.
 */
object ExplanationCopy {

    /** Every catalogue code, in the catalogue's order. */
    val codeTemplates: Map<String, String> = linkedMapOf(
        // PDT-v0 §8.1 — why the need exists
        "need.new_learning_available" to "Öğrenme yolunda sıradaki yeni bir beceri.",
        "need.continue_learning_active" to "Üzerinde çalışmakta olduğun bir beceriyi sürdürüyor.",
        "need.weakness_detected" to "Son sonuçlar bu beceride bir zorluk olabileceğini gösteriyor; önce bunu netleştirmek gerekiyor.",
        "need.remediation_required" to "Bu beceride doğrulanmış bir eksik var; hedefli bir onarım çalışması gerekiyor.",
        "need.retention_review_due" to "Bu beceriyi yeniden yoklama zamanı geldi. Bu, unuttuğun anlamına gelmez.",
        "need.verification_due" to "Önceki sonuçla çelişen yeni bir sonuç var; farklı bir görevle yeniden doğrulamak gerekiyor.",
        "need.diagnostic_opportunity" to "Kısa bir tespit çalışması, bazı kısımları güvenle atlayıp atlayamayacağını gösterebilir.",
        "need.reinforcement_opportunity" to "Öğrendiğin bir beceriyi pekiştirme fırsatı.",
        "need.parallel_track_due" to "Paralel yürüyen hattaki çalışmanın sırası geldi.",
        "need.integration_opportunity" to "Öğrendiğin becerileri birlikte kullanma fırsatı.",
        // §8.2 — validation / trust
        "candidate.validated" to "Bu görev doğrulanmış içerik.",
        "candidate.invalid_content" to "Bu ihtiyaç için hazırlanmış görev şu an kullanılabilir durumda değil.",
        "candidate.invalid_evidence_contract" to "Görevin kanıt tanımı bu ihtiyaca uymuyor; bu yüzden kullanılmadı.",
        "candidate.invalid_prerequisite_metadata" to "Görevin ön koşul bilgisi eksik ya da tutarsız; düzeltilene kadar kullanılmıyor.",
        "candidate.untrusted_for_high_stakes_use" to "Bu ihtiyaç bir ölçüm gerektiriyor ve eldeki görev henüz ölçüm için doğrulanmadı.",
        "candidate.duplicate_suppressed" to "Aynı ihtiyaç için yeterince seçenek vardı; bu seçenek değerlendirmeye alınmadı.",
        "candidate.same_need_alternative_superseded" to "Aynı öğrenme ihtiyacını karşılayan daha uygun bir görev seçildi.",
        // §8.3 — prerequisite
        "eligibility.ready" to "Gerekli ön koşullar hazır.",
        "eligibility.ready_due_allowed" to "Bir ön koşulun tekrar zamanı geldi; olumsuz bir kanıt olmadığı için hazır sayılıyor.",
        "eligibility.conditional_uncertain" to "Bir ön koşul henüz kesinleşmedi; bu görev yine de yapılabilir.",
        "eligibility.soft_gap_support" to "Destekleyici bir ön koşul eksik; görev destekle yapılabilir.",
        "eligibility.blocked_hard_prerequisite" to "Bu çalışma, gerekli bir ön koşul hazır olana kadar bekliyor.",
        "eligibility.blocked_critical_verification" to "Bu çalışma, kritik bir ön koşulun doğrulaması tamamlanana kadar bekliyor.",
        "eligibility.blocked_strict_prerequisite_confidence" to "Bu görev ön koşullarından emin olmayı gerektiriyor; o güven oluşana kadar bekliyor.",
        "eligibility.invalid_prerequisite_metadata" to "Görevin ön koşul bilgisi tutarsız; düzeltilene kadar kullanılmıyor.",
        "eligibility.prerequisite_contamination_guard" to "Ön koşul hazır değilken yapılan iş bu beceriye yazılmaz; sonuç yanlış yere yazılmasın diye.",
        // §8.4 — retention
        "retention.review_due_not_failure" to "Tekrar zamanının gelmesi bir başarısızlık değildir.",
        "retention.overdue_priority_signal" to "Tekrar zamanı bir süredir geldiği için bu çalışma öne alındı.",
        "retention.verification_due" to "Önceki performansla çelişen yeni bir sonuç olduğu için yeniden doğrulama gerekiyor.",
        "retention.at_risk" to "Birden fazla sinyal bu beceriyi yeniden kontrol etmeyi değerli kılıyor.",
        "retention.remediation_confirmed" to "Doğrulanmış bir eksik olduğu için onarım gerekiyor.",
        "retention.natural_reuse_satisfied" to "Bu beceri başka çalışmalarda zaten kullanıldığı için ayrıca tekrar gerekmedi.",
        "retention.no_auto_decay" to "Geçen zaman tek başına bir beceriyi zayıflatmaz.",
        // §8.5 — priority
        "priority.p0_integrity_blocker" to "Bu çalışma, bağlı işleri bekleten bir durumu gideriyor.",
        "priority.p1_repair_or_verify" to "Onarım ya da doğrulama işi yeni konuların önüne alındı.",
        "priority.p2_maintain_or_continue" to "Süren ya da bakım gerektiren bir çalışma.",
        "priority.p3_planned_progress" to "Planlı ilerlemenin bir parçası.",
        "priority.p4_reinforce_or_optimize" to "Pekiştirme ya da iyileştirme çalışması.",
        "priority.blocks_current_required_path" to "Şu an üzerinde çalıştığın yol bu beceriye bağlı.",
        "priority.blocks_next_ready_dependency" to "Sıradaki çalışma bu beceriye bağlı.",
        "priority.criticality_advantage" to "Kritik bir beceri olduğu için sıralamada öne geçti.",
        "priority.evidence_severity_advantage" to "Kanıt durumu bu işi daha önce ele almayı gerektirdi.",
        "priority.temporal_urgency_advantage" to "Zamanı geldiği için sıralamada öne geçti.",
        "priority.starvation_promoted" to "Uygun olduğu hâlde birkaç kez planlanamadığı için öne alındı.",
        "priority.continuation_value" to "Süren bir çalışmayı kesmeden sürdürmek için öne alındı.",
        "priority.decision_value" to "Kısa bir kontrol, sonraki planlama kararını netleştirecek.",
        "priority.track_balance_pressure" to "Paralel hat bir süredir ertelendiği için dengelemek amacıyla öne alındı.",
        "priority.stable_tie_break" to "Eşit durumdaki işler sabit bir sırayla dizildi.",
        // §8.6 — capacity
        "capacity.source_today_override" to "Bugün için ayrılan süre, bugüne özel belirlediğin süre.",
        "capacity.source_short_profile" to "Bugün için ayrılan süre kısa gün ayarından geliyor.",
        "capacity.source_normal_profile" to "Bugün için ayrılan süre normal gün ayarından geliyor.",
        "capacity.source_intensive_profile" to "Bugün için ayrılan süre yoğun gün ayarından geliyor.",
        "capacity.source_scheduled_default" to "Bugün için ayrılan süre, bu gün için belirlediğin varsayılan süre.",
        "capacity.selected_within_budget" to "Bugünkü süreye sığıyor.",
        "capacity.split_to_fit" to "Tamamı bugüne sığmadığı için güvenli bir parçası planlandı; kalanı kaldığı yerden sürer.",
        "capacity.smaller_alternative_to_fit" to "Bugünkü süreye sığan daha kısa bir görev seçildi.",
        "capacity.deferred_not_enough_time" to "Bu çalışma hâlâ gerekli; ancak bugünkü süreye güvenli biçimde sığmadı. Bu bir borç veya başarısızlık değildir.",
        "capacity.reserve_applied" to "Sürenin küçük bir kısmı geçişler için boş bırakıldı.",
        "capacity.reserve_relaxed_for_microtask" to "Bugünkü süre kısa olduğu için ara pay bırakılmadı; yalnız kısa bir çalışma sığabilir.",
        "capacity.hard_budget_not_exceeded" to "Plan, bugün için ayırdığın süreyi aşmıyor.",
        "capacity.no_penalty_user_stopped" to "Çalışmayı durdurman bir ceza doğurmaz.",
        // §8.7 — diagnostic
        "diagnostic.user_requested_fast_path" to "Hızlı ilerleme isteğin üzerine bir tespit çalışması planlandı.",
        "diagnostic.probe_selected" to "Bildiğin kısımları ayırmak için kısa bir yoklama seçildi.",
        "diagnostic.confirm_needed" to "Atlama kararı için bir doğrulama daha gerekiyor.",
        "diagnostic.critical_confirm_needed" to "Kritik bir beceri olduğu için atlamadan önce ayrıca doğrulama gerekiyor.",
        "diagnostic.partial_coverage_waiver" to "Bağımsız olarak gösterdiğin kısımlar atlanıyor; gösterilmeyen kısımlar normal akışta kalıyor.",
        "diagnostic.full_coverage_waiver" to "Bu bölümün tamamını bağımsız olarak gösterdiğin için başlangıç anlatımları atlanıyor.",
        "diagnostic.no_waiver" to "Tespit çalışması bazı kısımları güvenle atlamak için yeterli kanıt üretmedi; normal akıştan devam ediliyor.",
        "diagnostic.h0_required_for_waiver" to "Atlama yalnız yardımsız yapılan çalışmayla mümkün.",
        "diagnostic.prerequisite_blocked" to "Tespit çalışması, ön koşullar hazır olana kadar bekliyor.",
        "diagnostic.component_not_separately_attributable" to "Bu bölüm ayrıca ölçülemediği için atlanmıyor.",
        // §8.8 — re-entry
        "reentry.return_after_absence" to "Bir aradan sonra döndün; plan bugünkü duruma göre kuruldu.",
        "reentry.absence_not_failure" to "Ara vermek bir başarısızlık değildir.",
        "reentry.absence_not_task_debt" to "Eski günlük görevler borç olarak taşınmadı.",
        "reentry.stale_plan_not_replayed" to "Önceki günün planı yeniden oynatılmadı.",
        "reentry.current_state_regenerated" to "Bugünkü plan, mevcut beceri durumun ve ayırdığın süre üzerinden yeniden oluşturuldu.",
        "reentry.paused_checkpoint_candidate" to "Güvenle duraklattığın bir çalışma, sürdürülebilecek işler arasında değerlendirildi.",
        "reentry.incomplete_high_stakes_attempt_not_scored" to "Yarım kalan bir ölçüm puanlanmadı ve bağımsız çalışma olarak sürdürülmedi.",
        "reentry.due_inventory_not_daily_plan" to "Tekrar zamanı gelen beceriler bir liste olarak yüklenmedi; bugünkü önceliklerle değerlendirildi.",
        // §8.9 — selection
        "selection.selected" to "Bugünkü plana alındı.",
        "selection.selected_split" to "Güvenli bir parçası bugünkü plana alındı.",
        "selection.selected_smaller_alternative" to "Daha kısa bir seçeneği bugünkü plana alındı.",
        "selection.not_selected_lower_priority" to "Bugün daha öncelikli işler bulunduğu için bu çalışma sonraki planlarda yeniden değerlendirilecek.",
        "selection.not_selected_capacity" to "Bugünkü süreye sığmadığı için bugüne alınmadı. Bu bir borç veya başarısızlık değildir.",
        "selection.blocked_prerequisite" to "Bir ön koşul hazır olmadığı için bekliyor.",
        "selection.invalid_candidate" to "Eldeki görev şu an kullanılabilir değil.",
        "selection.same_need_alternative_not_used" to "Aynı ihtiyaç için başka bir görev seçildi.",
        "selection.resolved_before_selection" to "Bu ihtiyaç planlama sırasında zaten karşılanmıştı.",
        // §8.10 — replan
        "replan.capacity_changed" to "Bugün için ayrılan süreyi değiştirdiğin için plan yeniden kuruldu.",
        "replan.remaining_time_changed" to "Kalan süre değiştiği için plan yeniden kuruldu.",
        "replan.task_finished_early" to "Bir görev tahminden kısa sürdüğü için plan yeniden kuruldu.",
        "replan.task_overran_estimate" to "Bir görev tahminden uzun sürdüğü için plan yeniden kuruldu.",
        "replan.new_remediation_created" to "Yeni bir onarım ihtiyacı oluştuğu için plan yeniden kuruldu.",
        "replan.new_verification_created" to "Yeni bir doğrulama ihtiyacı oluştuğu için plan yeniden kuruldu.",
        "replan.evidence_state_changed" to "Yeni bir sonuç beceri durumunu değiştirdiği için plan yeniden kuruldu.",
        "replan.prerequisite_state_changed" to "Bir ön koşulun durumu değiştiği için plan yeniden kuruldu.",
        "replan.topic_state_changed" to "Bir konunun durumu değiştiği için plan yeniden kuruldu.",
        "replan.return_after_absence" to "Bir aradan sonra döndüğün için plan bugünkü durumdan kuruldu.",
        "replan.user_requested_extra_time" to "Ek süre ayırdığın için plan yeniden kuruldu.",
        "replan.user_stopped_session" to "Çalışmayı durdurduğun için kalan plan yeniden kuruldu. Bu bir ceza doğurmaz.",
        "replan.curriculum_version_changed" to "Müfredat güncellendiği için plan yeniden kuruldu.",
        // PRG-v0 §20 — prerequisite explainability inputs
        "blocked_missing_hard_prerequisite" to "Gerekli bir ön koşul henüz hazır değil.",
        "blocked_critical_prerequisite_verification_due" to "Kritik bir ön koşulun doğrulaması bekliyor.",
        "blocked_prerequisite_remediation_required" to "Bir ön koşulda onarım gerekiyor.",
        "eligible_prerequisite_review_due_not_blocking" to "Bir ön koşulun tekrar zamanı geldi; bu, çalışmayı bekletmiyor.",
        "eligible_with_soft_prerequisite_gap" to "Destekleyici bir ön koşul eksik; çalışma destekle yapılabilir.",
        "conditional_prerequisite_uncertain" to "Bir ön koşul henüz kesinleşmedi.",
        "independent_branch_available" to "Bekleyen çalışmalar olsa da onlara bağlı olmayan işler devam ediyor.",
        "prerequisite_repaired_replan" to "Bir ön koşul onarıldığı için plan yeniden kuruldu.",
        "invalid_due_to_prerequisite_contamination" to "Ön koşul hazır değilken yapılan iş bu beceriye yazılmadı.",
    )

    /** Codes whose sentence names the Skills the trace related to them (`PDT-v0` §11), used when it has any. */
    val skillTemplates: Map<String, String> = linkedMapOf(
        "eligibility.ready_due_allowed" to "{skills} için tekrar zamanı geldi; olumsuz bir kanıt olmadığı için ön koşul hazır sayılıyor.",
        "eligibility.conditional_uncertain" to "{skills} henüz kesinleşmedi; bu görev yine de yapılabilir.",
        "eligibility.soft_gap_support" to "Destekleyici ön koşul {skills} eksik; görev destekle yapılabilir.",
        "eligibility.blocked_hard_prerequisite" to "Bu çalışma şimdilik bekliyor; çünkü {skills} gerekli ve henüz hazır değil.",
        "eligibility.blocked_critical_verification" to "Bu çalışma, {skills} doğrulaması tamamlanana kadar bekliyor.",
        "eligibility.blocked_strict_prerequisite_confidence" to "Bu görev {skills} konusunda emin olmayı gerektiriyor; o güven oluşana kadar bekliyor.",
    )

    val factTemplates: Map<TraceFact, String> = linkedMapOf(
        TraceFact.KEPT_FROM_EARLIER_VERSION to "Bu işe bugün daha önce başlandı; plan değişirken olduğu gibi korundu.",
        TraceFact.NO_TASK_FOR_NEED to "Bu ihtiyaç için henüz hazırlanmış bir görev yok. İhtiyaç açık kalıyor.",
        TraceFact.NOT_TAKEN_TODAY to "Bu ihtiyaç bugüne alınmadı. İhtiyaç açık kalıyor.",
        TraceFact.WAITING to "Bu çalışma bir ön koşulu bekliyor.",
        TraceFact.PLAN_REPLACED to "Plan bugün yeniden kuruldu.",
        TraceFact.DAY_OVER_BUDGET_AFTER_KEEPING to
            "Korunan işlerle birlikte bugünkü toplam, ayırdığın süreyi aşıyor; başlanmış iş geri alınmadı.",
    )

    val reconsiderationTemplates: Map<Reconsideration, String> = linkedMapOf(
        Reconsideration.NEXT_PLAN to "Sonraki planda yeniden değerlendirilecek. Bu bir tarih sözü değil.",
        Reconsideration.WHEN_PREREQUISITE_READY to "Ön koşul hazır olduğunda yeniden değerlendirilecek.",
        Reconsideration.WHEN_A_TASK_IS_AVAILABLE to "Uygun bir görev olduğunda planlanabilir.",
    )

    val stateTemplates: Map<ExplanationState, String> = linkedMapOf(
        ExplanationState.EXPLAINED to "Bugünkü plan, planlayıcının kaydettiği kararlardan açıklanıyor.",
        ExplanationState.NO_PLAN_YET to "Bugün için henüz bir plan üretilmedi; açıklanacak bir karar yok.",
        ExplanationState.PLAN_FROM_ANOTHER_DAY to "En son plan başka bir güne ait; bugünün planı olarak açıklanmıyor.",
        ExplanationState.UNREADABLE to "Bugünün planı okunamadı. Yerine tahmini bir açıklama gösterilmiyor.",
    )

    fun text(statement: Statement): String {
        val code = statement.code
        return when {
            code == null -> factTemplates.getValue(requireNotNull(statement.fact))
            statement.skills.isNotEmpty() && code in skillTemplates ->
                skillTemplates.getValue(code).replace("{skills}", names(statement.skills))
            else -> codeTemplates.getValue(code)
        }
    }

    fun text(reconsideration: Reconsideration, skills: List<SkillMention> = emptyList()): String =
        if (reconsideration == Reconsideration.WHEN_PREREQUISITE_READY && skills.isNotEmpty()) {
            "${names(skills)} hazır olduğunda yeniden değerlendirilecek."
        } else {
            reconsiderationTemplates.getValue(reconsideration)
        }

    fun text(state: ExplanationState): String = stateTemplates.getValue(state)

    /** A Skill is named by its published name, and by its reference when none is published. */
    fun names(skills: List<SkillMention>): String = skills.joinToString(", ") { it.name ?: it.ref.logicalId }

    /**
     * A grouped entry's Skills for a screen: the first [shown] by name and the rest as a labelled count
     * (12F). A count of Skills is inventory, not a score, a percentage or a debt.
     */
    fun shortNames(skills: List<SkillMention>, shown: Int = 3): String =
        if (skills.size <= shown) names(skills) else "${names(skills.take(shown))} ve ${skills.size - shown} beceri daha"

    /** Every catalogue code has a sentence; a code outside the catalogue has none to be shown through. */
    fun coversCatalogue(): Boolean = codeTemplates.keys.toList() == ReasonCatalog.all
}
