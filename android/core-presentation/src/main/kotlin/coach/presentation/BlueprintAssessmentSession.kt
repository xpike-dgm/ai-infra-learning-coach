package coach.presentation

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentBlueprint
import coach.model.AssessmentBlueprintResult
import coach.model.AssessmentScope
import coach.model.BlueprintSessionStatus
import coach.model.IndependenceMode

/**
 * The weekly (13A) and monthly (13B) session inside the **one** assessment interior (`ASUX-v0`). They differ
 * from daily only in how their slots were composed; the interior, its freezing, skipping, help and
 * recomposition rules are the same code, and `weekly_blueprint` / `monthly_capability` is displayed context
 * that adds no evidence weight (`ASUX-v0` §4: `monthly_scope != stronger_numeric_weight`).
 */
object BlueprintSessionPresentation {

    /**
     * The blueprint's blocks as the interior's blocks, each ready slot one atomic evidence boundary. A
     * testlet is still one boundary because the blueprint holds a dependency group in one slot.
     *
     * The allowed-tools disclosure is what **every** item in the session allows: when items differ, the
     * session never claims a tool is allowed that one of its items forbids, and a tool the learner then
     * leaves unused costs no evidence.
     */
    fun view(blueprint: AssessmentBlueprint): AssessmentSessionView {
        val slots = blueprint.readySlots.associateBy { it.slotId }
        val blocks = blueprint.blocks.map { block ->
            Block(
                id = block.id,
                boundaries = block.slotIds.map { id ->
                    val slot = slots.getValue(id)
                    Boundary(id = slot.slotId, kind = BoundaryKind.ITEM, items = listOf(slot.item!!),
                        dependencyGroupId = slot.dependencyGroupId)
                },
            )
        }
        val tools = blueprint.readySlots.map { it.allowedTools.toSet() }
            .reduceOrNull { a, b -> a intersect b }.orEmpty()
        return AssessmentSessionView(
            scope = blueprint.scope,
            intents = blueprint.readySlots.map { it.role.intent }.distinct(),
            blocks = blocks,
            independenceMode = IndependenceMode.H0_REQUIRED,
            allowedTools = AllowedToolsPolicy(tools.sorted()),
            state = if (blocks.isEmpty()) SessionState.BLOCKED_NOT_STARTABLE else SessionState.SESSION_ORIENTATION,
        )
    }

    /**
     * The semantic result (`ASUX-v0` §13, `WBA-v0` §30, `MCA-v0` §30). Everything that could not be measured
     * reliably — an unusable or contaminated answer, a provisional evaluation, help that made an attempt
     * assisted — is first class; a state change is claimed only when an engine reported one. A monthly
     * session has no overall monthly success percentage to show, and cannot.
     */
    fun result(
        result: AssessmentBlueprintResult,
        session: AssessmentSessionView,
        canonicalChanges: Map<ResultFamily, List<String>> = emptyMap(),
    ): SessionResult = SessionResults.of(
        session = session,
        canonicalChanges = canonicalChanges,
        unreliable = buildSet {
            if (result.invalidOrUnusableEvidenceIds.isNotEmpty()) add(NotReliablyMeasured.INVALID_ITEM)
            if (result.provisionalEvidenceIds.isNotEmpty()) add(NotReliablyMeasured.PROVISIONAL_EVALUATION)
            if (result.assistanceRecheckObjectives.isNotEmpty()) add(NotReliablyMeasured.ASSISTED_ATTEMPT)
        },
    )

    /** What the session status says, in words that are never a verdict (`WBA-v0` §18, `MCA-v0` §17). */
    fun statusText(scope: AssessmentScope, status: BlueprintSessionStatus): String = when (scope) {
        AssessmentScope.MONTHLY_CAPABILITY -> when (status) {
            BlueprintSessionStatus.COMPLETE -> MonthlyCopy.COMPLETE
            BlueprintSessionStatus.PARTIAL -> MonthlyCopy.PARTIAL
            BlueprintSessionStatus.DEFERRED -> MonthlyCopy.DEFERRED
            BlueprintSessionStatus.INVALIDATED -> MonthlyCopy.INVALIDATED
        }
        AssessmentScope.WEEKLY_BLUEPRINT -> when (status) {
            BlueprintSessionStatus.COMPLETE -> WeeklyCopy.COMPLETE
            BlueprintSessionStatus.PARTIAL -> WeeklyCopy.PARTIAL
            BlueprintSessionStatus.DEFERRED -> WeeklyCopy.DEFERRED
            BlueprintSessionStatus.INVALIDATED -> WeeklyCopy.INVALIDATED
        }
        AssessmentScope.DAILY_MICRO -> error("a daily measurement composes no blueprint")
    }
}

/** Working sentences whose meaning is canonical; their wording is 14's. */
object WeeklyCopy {
    const val COMPLETE = "Bu haftanın ölçümleri yapıldı. Neyin değiştiğini aşağıda motorların söylediği kadarıyla görüyorsun."

    const val PARTIAL =
        "Bu haftanın ölçümlerinin bir kısmı yapıldı. Yapılmayan kısım yanlış sayılmadı ve bir borç olarak taşınmıyor."

    const val DEFERRED = "Bu hafta ölçüm yapılmadı. Bu bir başarısızlık değil; gelecek hafta o günkü duruma göre yeniden kurulur."

    const val INVALIDATED = "Bu ölçüm güvenilir biçimde okunamadı. Ne lehine ne aleyhine sayıldı."

    const val CONTAMINATED =
        "Bir soru, bu oturumda eksik görünen bir ön koşula dayandığı için hedefine karşı sayılmadı; ilerlemeni etkilemedi."
}

/**
 * Working sentences whose meaning is canonical; their wording is 14's. A missed month is not a failure and
 * leaves no second month to make up (`MCA-v0` §3, §17); a monthly session is not a final exam.
 */
object MonthlyCopy {
    const val COMPLETE = "Bu ayın ölçümleri yapıldı. Neyin değiştiğini aşağıda motorların söylediği kadarıyla görüyorsun."

    const val PARTIAL =
        "Bu ayın ölçümlerinin bir kısmı yapıldı. Yapılmayan kısım yanlış sayılmadı ve bir borç olarak taşınmıyor."

    const val DEFERRED = "Bu ay ölçüm yapılmadı. Bu bir başarısızlık değil; gelecek ay o günkü duruma göre yeniden kurulur."

    const val INVALIDATED = "Bu ölçüm güvenilir biçimde okunamadı. Ne lehine ne aleyhine sayıldı."

    /** `MCA-v0` §14: a professional evidence checkpoint produces evidence; it is never a readiness verdict. */
    const val CHECKPOINT_NOT_A_GATE =
        "Bu bölüm uygulamalı çalışmana dair kanıt toplar; bir yeterlilik ya da hazır olma kararı vermez."
}
