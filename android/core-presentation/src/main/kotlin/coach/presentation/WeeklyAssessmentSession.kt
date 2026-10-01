package coach.presentation

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentScope
import coach.model.IndependenceMode
import coach.model.WeeklyAssessmentBlueprint
import coach.model.WeeklyAssessmentResult
import coach.model.WeeklySessionStatus

/**
 * The weekly session inside the **one** assessment interior (13A, `ASUX-v0`). Weekly differs from daily
 * only in how its slots were composed; the interior, its freezing, skipping, help and recomposition rules
 * are the same code, and `weekly_blueprint` is displayed context that adds no evidence weight.
 */
object WeeklySessionPresentation {

    /**
     * The blueprint's blocks as the interior's blocks, each ready slot one atomic evidence boundary. A
     * testlet is still one boundary because the blueprint holds a dependency group in one slot.
     *
     * The allowed-tools disclosure is what **every** item in the session allows: when items differ, the
     * session never claims a tool is allowed that one of its items forbids, and a tool the learner then
     * leaves unused costs no evidence.
     */
    fun view(blueprint: WeeklyAssessmentBlueprint): AssessmentSessionView {
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
            scope = AssessmentScope.WEEKLY_BLUEPRINT,
            intents = blueprint.readySlots.map { it.role.intent }.distinct(),
            blocks = blocks,
            independenceMode = IndependenceMode.H0_REQUIRED,
            allowedTools = AllowedToolsPolicy(tools.sorted()),
            state = if (blocks.isEmpty()) SessionState.BLOCKED_NOT_STARTABLE else SessionState.SESSION_ORIENTATION,
        )
    }

    /**
     * The semantic result (`ASUX-v0` §13, `WBA-v0` §30). Everything that could not be measured reliably —
     * an unusable or contaminated answer, a provisional evaluation, help that made an attempt assisted —
     * is first class; a state change is claimed only when an engine reported one.
     */
    fun result(
        result: WeeklyAssessmentResult,
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

    /** What the session status says, in words that are never a verdict (`WBA-v0` §18). */
    fun statusText(status: WeeklySessionStatus): String = when (status) {
        WeeklySessionStatus.COMPLETE -> WeeklyCopy.COMPLETE
        WeeklySessionStatus.PARTIAL -> WeeklyCopy.PARTIAL
        WeeklySessionStatus.DEFERRED -> WeeklyCopy.DEFERRED
        WeeklySessionStatus.INVALIDATED -> WeeklyCopy.INVALIDATED
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
