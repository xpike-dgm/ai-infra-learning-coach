package coach.wiring

import coach.ai.AiEvaluator
import coach.model.EvaluatorAvailability
import coach.ports.EvaluatorPort

private val adapter = AiEvaluator()

/** Selected when the build includes :ai-adapter. */
internal fun provideEvaluator(): EvaluatorPort = adapter

/** The adapter reports its own availability; until 14 gives it call sites it is unavailable. */
internal fun evaluatorAvailability(): EvaluatorAvailability = adapter.availability
