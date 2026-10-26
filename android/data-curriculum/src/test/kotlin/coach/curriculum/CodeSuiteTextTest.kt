package coach.curriculum

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * 16G (`HLPX-v0`): the suite text the learner copies is served only for exactly the item version and the suite version
 * the package declares — a suite for another version would run tests the course does not judge by.
 */
class CodeSuiteTextTest {

    private val assets = File("../app-wiring/src/main/assets")
    private val first = File(assets, "curriculum_package.txt").readText()
    private val python = File(assets, "curriculum_package_v2.txt").readText()

    private fun source(suites: (String) -> String?) =
        FileContentSource(later = { listOf(python) }, codeSuites = suites, codeRunner = { "runner" }, source = { first })

    private val plain = source { null }
    private val item = PackageFormat.parse(python).curriculum.resources.map { it.ref }.first { plain.codeTestsFor(it) != null }
    private val name = "${item.logicalId}-v${item.version}.json"
    private val shipped = File(assets, "code_suites/$name").readText()

    @Test
    fun `the shipped suite is served for its own item version and suite version only`() {
        assertEquals(shipped, source { if (it == name) shipped else null }.codeSuiteText(item))
        assertNull(plain.codeSuiteText(item), "nothing ships: nothing is served, and the item is not served either")
        val suite = assertNotNull(plain.codeTestsFor(item)).ref
        val otherItem = shipped.replace("\"${item}\"", "\"${item.logicalId}@v${item.version + 1}\"")
        val otherSuite = shipped.replace("\"${suite}\"", "\"${suite.logicalId}@v${suite.version + 1}\"")
        val otherFormat = shipped.replace("code_test_suite/1", "code_test_suite/2")
        listOf(otherItem, otherSuite, otherFormat).forEach { text ->
            check(text != shipped)
            assertNull(source { if (it == name) text else null }.codeSuiteText(item))
        }
        assertEquals("runner", source { null }.codeRunnerText())
        assertNull(FileContentSource(source = { first }).codeRunnerText())
    }
}
