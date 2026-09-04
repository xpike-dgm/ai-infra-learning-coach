package coach.wiring

import coach.ai.AiEvaluator
import coach.ports.EvaluatorPort

/** Selected when the build includes :ai-adapter. */
internal fun provideEvaluator(): EvaluatorPort = AiEvaluator()
