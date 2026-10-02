package coach.wiring

import coach.ai.AiEvaluator
import coach.model.EvaluatorAvailability
import coach.ports.EvaluatorPort

/** Selected when the build includes :ai-adapter: the provider client, with the learner's key (14G). */
private val adapter by lazy { AiEvaluator(AiWiring.client, AiWiring.keySource()) }

internal fun provideEvaluator(): EvaluatorPort = adapter

/** Available only while a key is saved; it reads the in-memory flag, never the disk (14G). */
internal fun evaluatorAvailability(): EvaluatorAvailability =
    if (AiWiring.keySource().isPresent()) EvaluatorAvailability.AVAILABLE else EvaluatorAvailability.UNAVAILABLE
