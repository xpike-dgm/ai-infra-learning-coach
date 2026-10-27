package coach.presentation

import coach.model.AllowedToolsPolicy

/**
 * 17D (`A11X-v0`): an assessment's allowed tools in words. A tool id from the package never reaches the screen; a tool
 * the app has no word for is said as such, not shown as its id.
 */
object ToolWords {
    val labels: Map<String, String> = mapOf(
        "terminal" to "terminal",
        "documentation" to "belgeler",
        AllowedToolsPolicy.EXTERNAL_AI to "dış yapay zekâ",
    )
    const val UNNAMED = "adı verilmemiş bir araç"

    fun of(ids: List<String>): String = ids.map { labels[it] ?: UNNAMED }.distinct().joinToString(", ")
}
