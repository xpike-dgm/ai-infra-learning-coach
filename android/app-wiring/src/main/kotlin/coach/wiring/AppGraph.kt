package coach.wiring

import coach.curriculum.FileContentSource
import coach.model.StudyTimestamp
import coach.persistence.SqlitePersistence
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.EvaluatorPort
import coach.ports.PersistencePort
import java.time.Instant
import java.time.ZoneId

/**
 * The composition root. It is the only place that knows every implementation, and it holds no
 * domain logic, no policy and no state (MSBX-v0 §composition_root).
 *
 * There is no DI framework by decision: one object graph, one user, and a core that must stay
 * free of framework annotations. Wiring is plain constructor calls.
 */
class AppGraph(
    val clock: ClockPort = SystemClock(),
    val persistence: PersistencePort = SqlitePersistence(),
    val content: ContentPort = FileContentSource(),
    val evaluator: EvaluatorPort = provideEvaluator(),
)

/**
 * The only place in the product that reads the system clock. Core receives time as an input.
 * java.time is native from minSdk 26, which is why no desugaring dependency appears in the
 * code that computes retention and review timing (AMTS-v0 §8.1).
 */
class SystemClock(private val zone: ZoneId = ZoneId.systemDefault()) : ClockPort {
    override fun now(): StudyTimestamp {
        val instant = Instant.now()
        val offset = zone.rules.getOffset(instant)
        return StudyTimestamp(
            instantEpochMillis = instant.toEpochMilli(),
            studyDay = instant.atZone(zone).toLocalDate().toString(),
            utcOffsetSeconds = offset.totalSeconds,
        )
    }
}
