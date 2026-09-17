package coach.wiring

import coach.application.NullEvaluator
import coach.model.EvaluatorAvailability
import coach.ports.EvaluatorPort

/**
 * Selected when the build excludes :ai-adapter. This source set is why
 * `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` compiles at all: with no adapter
 * on the classpath, the product still has a shipped evaluator and stays fully usable.
 */
internal fun provideEvaluator(): EvaluatorPort = NullEvaluator

/** With no adapter there is nothing to evaluate with; the deterministic core is unaffected. */
internal fun evaluatorAvailability(): EvaluatorAvailability = EvaluatorAvailability.UNAVAILABLE
