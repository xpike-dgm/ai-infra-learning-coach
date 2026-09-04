package coach.wiring

import coach.application.NullEvaluator
import coach.ports.EvaluatorPort

/**
 * Selected when the build excludes :ai-adapter. This source set is why
 * `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` compiles at all: with no adapter
 * on the classpath, the product still has a shipped evaluator and stays fully usable.
 */
internal fun provideEvaluator(): EvaluatorPort = NullEvaluator
