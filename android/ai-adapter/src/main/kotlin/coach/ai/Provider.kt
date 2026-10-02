package coach.ai

import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

/**
 * The provider seam (14G, `PRVX-v0 / D-111`). Everything provider-specific lives in this module and in configuration;
 * core never sees a model name, an endpoint or a key (`AIAX-v0` §8).
 */

/** What the learner's key looks like to the adapter: present or not. Where it is kept is the platform's concern. */
fun interface ApiKeySource {
    /** The key, or `null` when the learner has not entered one — then nothing leaves the device. */
    fun current(): String?
}

/**
 * The router's task classes (`AIAX-v0` §8). Each class names its model in configuration, so a model can be swapped
 * for one class without touching another — and without touching core.
 */
enum class TaskClass(val id: String) {
    TUTOR_HELP("tutor_help"),
    OPEN_RESPONSE_EVALUATION("open_response_evaluation"),
    CODE_EVALUATION("code_evaluation"),
}

/**
 * Configuration values recorded with the build (`AIAX-v0` §8.2) — not canonical claims. The model ids and the request
 * shape were confirmed against the provider's current reference on [referenceCheckedOn]; re-verify before changing
 * them. [budgetMillis] is end-to-end across every attempt (`AIAX-v0` §7.1) and [maxAttempts] bounds the retries
 * inside it: product defaults, not scientific values, tunable here.
 */
data class ProviderConfig(
    val provider: String = "openai",
    /** How the settings screen names the provider to the learner; core never names one (`AIAX-v0` §8). */
    val displayName: String = "OpenAI",
    val endpoint: String = "https://api.openai.com/v1/responses",
    val models: Map<TaskClass, String> = TaskClass.entries.associateWith { "gpt-6-astra" },
    val budgetMillis: Long = 60_000,
    val maxAttempts: Int = 2,
    val reference: String = "developers.openai.com/api/reference/resources/responses/methods/create; developers.openai.com/api/docs/guides/structured-outputs; developers.openai.com/api/docs/models",
    val referenceCheckedOn: String = "2026-10-02",
) {
    init {
        require(endpoint.startsWith("https://")) { "the key only ever travels over TLS" }
        require(TaskClass.entries.all { !models[it].isNullOrBlank() }) { "every task class names a model" }
        require(budgetMillis > 0 && maxAttempts >= 1) { "a budget and at least one attempt" }
    }

    fun model(taskClass: TaskClass): String = models.getValue(taskClass)
}

data class HttpResponse(val status: Int, val body: String)

/** One HTTP POST. A timeout is thrown as [SocketTimeoutException]; any other failure to talk as [IOException]. */
fun interface HttpTransport {
    fun post(url: String, headers: Map<String, String>, body: String, timeoutMillis: Long): HttpResponse
}

/**
 * The platform's own HTTP client (`HttpURLConnection`, on Android and the JVM alike) — no library is added (10A: no
 * HTTP client until a call site exists; 14G is that call site). Nothing here logs: not the URL, not the headers, not
 * the body, so the key never reaches a log (`AIAX-v0` §10.3).
 */
class UrlConnectionTransport : HttpTransport {
    override fun post(url: String, headers: Map<String, String>, body: String, timeoutMillis: Long): HttpResponse {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            val timeout = timeoutMillis.coerceIn(1, Int.MAX_VALUE.toLong()).toInt()
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.useCaches = false
            headers.forEach { (name, value) -> connection.setRequestProperty(name, value) }
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            return HttpResponse(status, text)
        } finally {
            connection.disconnect()
        }
    }
}

/** What one provider call produced, before any meaning is read into it. */
sealed interface ProviderOutcome {
    /** A completed reply whose only output was one JSON object; [model] is the model that answered. */
    data class Delivered(val json: JsonValue.Obj, val model: String) : ProviderOutcome

    /** No usable answer (`AIAX-v0` §6). [keyRejected] tells the connection check the key itself was refused. */
    data class NotDelivered(val reason: coach.model.PendingReason, val keyRejected: Boolean = false) : ProviderOutcome
}
