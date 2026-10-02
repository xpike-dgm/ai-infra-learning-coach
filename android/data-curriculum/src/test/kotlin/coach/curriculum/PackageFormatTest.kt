package coach.curriculum

import coach.model.AssessmentScope
import coach.model.BlueprintRole
import coach.model.MonthlyRole
import coach.model.SlotRole
import coach.model.ContentOrigin
import coach.model.IndependenceMode
import coach.model.LifecycleStatus
import coach.model.UseCeiling
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The authored package format is read strictly, or not at all (11D). */
class PackageFormatTest {

    private val itemRef = VersionedRef("item.python.loops.q1", 1)

    private val authored = """
        curriculum_package/1
        version=1
        source_refs=curriculum/decomposition/6c_foundations
        provenance=authored_11d

        [topic]
        logical_id=topic.python.control_flow
        version=1
        name=Control flow

        [skill]
        logical_id=skill.python.loops
        version=1
        canonical_name=Loop tracing
        capability_statement=Trace a bounded loop and state its output
        lifecycle_status=stable
        capability_kind=concept
        retention_profile=standard
        critical_prerequisite=false
        source_refs=curriculum/decomposition/6c_foundations
        provenance=authored_11d

        [objective]
        logical_id=objective.python.loops.trace
        version=1
        parent_skill=skill.python.loops@v1
        required=true
        criticality=standard
        acceptable_evidence_types=code_reading,explanation
        direct_evidence_types=code_reading
        required_direct_type=code_reading

        [topic_skill]
        topic=topic.python.control_flow@v1
        skill=skill.python.loops@v1

        [resource]
        logical_id=item.python.loops.q1
        version=1
        content_ref=item://item.python.loops.q1@v1
        evidence_type=code_reading
        allowed_tools_policy=documentation
        variant_family_id=family.python.loops.trace
        content_origin=human_authored

        [validation]
        resource=item.python.loops.q1@v1
        validated_at_instant=1788000000000
        status=validated
        validator=human_review
        origin=human_authored

        [item]
        ref=item.python.loops.q1@v1
        prompt=Bu döngü kaç kez çalışır?
        target_objectives=objective.python.loops.trace@v1
        target_skills=skill.python.loops@v1
        required_skills=
        evidence_type=code_reading
        expected_answer_or_rubric_ref=key://item.python.loops.q1@v1
        evaluator_required_status=verified
        evaluator_deterministic_required=true
        evaluator_policy_version=policy.deterministic.v1
        deterministic_verification=true
        allowed_tools=documentation
        prohibited_solution_sources=external_ai
        independence_mode=h0_required
        difficulty_class=core
        lifecycle_status=validated
        content_origin=human_authored
        declared_use_ceiling=standard_mastery_eligible
        scope_eligibility=daily_micro
        variant_family_id=family.python.loops.trace
        forbidden_not_yet_concepts=
    """.trimIndent()

    @Test
    fun `a well-formed package parses into pinned rows and one item`() {
        val parsed = PackageFormat.parse(authored)
        assertEquals(1, parsed.curriculum.version)
        assertEquals(listOf("skill.python.loops"), parsed.curriculum.skills.map { it.ref.logicalId })
        assertEquals(listOf("code_reading", "explanation"), parsed.curriculum.objectives.single().acceptableEvidenceTypes)
        assertEquals(VersionedRef("skill.python.loops", 1), parsed.curriculum.objectives.single().parentSkill)
        assertEquals(ContentOrigin.HUMAN_AUTHORED, parsed.curriculum.resources.single().contentOrigin)
        assertEquals(LifecycleStatus.VALIDATED, parsed.curriculum.validationRecords.single().status)
        assertTrue(parsed.curriculum.unresolvedReferences().isEmpty())

        val item = assertNotNull(parsed.items[itemRef])
        assertEquals(setOf(AssessmentScope.DAILY_MICRO), item.scopeEligibility)
        assertEquals(IndependenceMode.H0_REQUIRED, item.independenceMode)
        assertEquals(UseCeiling.STANDARD_MASTERY_ELIGIBLE, item.declaredUseCeiling)
        assertTrue(item.deterministicVerification)
        assertEquals(listOf("external_ai"), item.allowedTools.prohibitedSolutionSources)
        assertEquals("Bu döngü kaç kez çalışır?", parsed.documents[itemRef])
    }

    @Test
    fun `anything the format does not define refuses the whole package`() {
        val broken = mapOf(
            "not the format" to authored.replace("curriculum_package/1", "curriculum_package/2"),
            "unknown section" to authored + "\n\n[mystery]\nkey=value",
            "unknown key" to authored.replace("difficulty_class=core", "difficulty_class=core\nsecret_weight=3"),
            "repeated key" to authored.replace("criticality=standard", "criticality=standard\ncriticality=critical"),
            "unpinned reference" to authored.replace("parent_skill=skill.python.loops@v1", "parent_skill=skill.python.loops"),
            "unknown enum" to authored.replace("status=validated", "status=gold_plated"),
            "unknown scope" to authored.replace("scope_eligibility=daily_micro", "scope_eligibility=hourly"),
            "not a key=value line" to authored.replace("difficulty_class=core", "difficulty_class core"),
        )
        broken.forEach { (name, text) ->
            assertFailsWith<PackageFormat.ParseFailure>(name) { PackageFormat.parse(text) }
        }
    }

    @Test
    fun `a refusal names the line and the rule, never an accident inside the parser`() {
        val unpinned = authored.replace("parent_skill=skill.python.loops@v1", "parent_skill=skill.python.loops")
        val failure = assertFailsWith<PackageFormat.ParseFailure> { PackageFormat.parse(unpinned) }
        assertTrue(
            failure.reasons.any { "not a pinned reference" in it },
            "the refusal did not name the rule: ${failure.reasons}",
        )
    }

    @Test
    fun `a missing required field refuses rather than defaulting`() {
        val withoutAnswerKey = authored.replace("expected_answer_or_rubric_ref=key://item.python.loops.q1@v1\n", "")
        assertFailsWith<PackageFormat.ParseFailure> { PackageFormat.parse(withoutAnswerKey) }
    }

    @Test
    fun `an item may declare its minutes and blueprint roles, and nothing is defaulted when it does not`() {
        val undeclared = assertNotNull(PackageFormat.parse(authored).items[itemRef])
        assertNull(undeclared.expectedActiveMinutes)
        assertTrue(undeclared.blueprintRoles.isEmpty())

        val weekly = authored.replace("forbidden_not_yet_concepts=",
            "forbidden_not_yet_concepts=\nexpected_active_minutes=8\nblueprint_roles=retention_due,weakness_or_verification")
            .replace("scope_eligibility=daily_micro", "scope_eligibility=daily_micro,weekly_blueprint")
        val item = assertNotNull(PackageFormat.parse(weekly).items[itemRef])
        assertEquals(8, item.expectedActiveMinutes)
        assertEquals(setOf(BlueprintRole.RETENTION_DUE, BlueprintRole.WEAKNESS_OR_VERIFICATION), item.blueprintRoles)
        assertEquals(listOf(itemRef), FileContentSource { weekly }.assessmentItemsFor(VersionedRef("skill.python.loops", 1)).map { it.ref })
        assertTrue(FileContentSource { weekly }.assessmentItemsFor(VersionedRef("skill.python.loops", 2)).isEmpty())

        listOf("expected_active_minutes=0", "expected_active_minutes=soon", "blueprint_roles=final_exam").forEach { bad ->
            val text = authored.replace("forbidden_not_yet_concepts=", "forbidden_not_yet_concepts=\n$bad")
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(text) }
        }
    }

    @Test
    fun `a monthly role is declared on an item the monthly scope can use, never on one it cannot`() {
        val both = authored.replace("forbidden_not_yet_concepts=",
            "forbidden_not_yet_concepts=\nexpected_active_minutes=30\nblueprint_roles=retention_due,delayed_retention_sampling")
            .replace("scope_eligibility=daily_micro", "scope_eligibility=weekly_blueprint,monthly_capability")
        val item = assertNotNull(PackageFormat.parse(both).items[itemRef])
        assertEquals(setOf<SlotRole>(BlueprintRole.RETENTION_DUE, MonthlyRole.DELAYED_RETENTION_SAMPLING), item.blueprintRoles)

        // A role of a scope the item is not eligible for is a contradiction in the package, refused.
        val weeklyOnly = both.replace("scope_eligibility=weekly_blueprint,monthly_capability", "scope_eligibility=weekly_blueprint")
        val failure = assertFailsWith<PackageFormat.ParseFailure> { PackageFormat.parse(weeklyOnly) }
        assertTrue(failure.reasons.any { "needs scope 'monthly_capability'" in it }, failure.reasons.toString())
    }

    @Test
    fun `a misconception label is read strictly and pinned to its Objective`() {
        val label = """

            [misconception]
            logical_id=misconception.python.loops.off_by_one
            version=1
            objective=objective.python.loops.trace@v1
            name=bir eksik ya da bir fazla tur
            open_question=Döngünün son turunu sayarken bir tur kaçırmış olabilir misin?
        """.trimIndent()
        val parsed = PackageFormat.parse(authored + "\n" + label).curriculum.misconceptions.single()
        assertEquals(VersionedRef("misconception.python.loops.off_by_one", 1), parsed.ref)
        assertEquals(VersionedRef("objective.python.loops.trace", 1), parsed.objective)
        for (bad in listOf(
            label.replace("misconception.python.loops.off_by_one", "off_by_one"),
            label.replace("?", "."),
            label.replace("objective.python.loops.trace@v1", "objective.python.loops.trace"),
            label + "\nseverity=high",
        )) {
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(authored + "\n" + bad) }
        }
    }

    @Test
    fun `comments and blank lines are ignored`() {
        val commented = authored.replace("[topic]", "# the first topic\n[topic]")
        assertEquals(1, PackageFormat.parse(commented).curriculum.topics.size)
    }

    @Test
    fun `with no package shipping, every lookup is null rather than a crash`() {
        val source = FileContentSource()
        assertNull(source.curriculumPackage())
        assertNull(source.assessmentItem(itemRef))
        assertNull(source.resource(itemRef))
        assertNull(source.failure)
    }

    @Test
    fun `a package that does not parse serves nothing at all, and says why`() {
        val source = FileContentSource { authored.replace("status=validated", "status=gold_plated") }
        assertNull(source.curriculumPackage())
        assertNull(source.assessmentItem(itemRef))
        assertTrue(assertNotNull(source.failure).message!!.contains("gold_plated"))
    }

    @Test
    fun `a written explanation is read strictly and served by its Objective version`() {
        val explanations = """

            [explanation]
            logical_id=explanation.python.loops.trace
            version=1
            objective=objective.python.loops.trace@v1
            form=canonical
            text=Bir döngü koşul doğru olduğu sürece tekrar eder.\nHer tur sayacı bir artırır.

            [explanation]
            logical_id=explanation.python.loops.trace_worked
            version=1
            objective=objective.python.loops.trace@v1
            form=worked_example
            level=H2
            text=for i in range(3) üç tur döner: 0, 1, 2.
        """.trimIndent()
        val source = FileContentSource { authored + "\n" + explanations }
        val served = source.explanationsFor(VersionedRef("objective.python.loops.trace", 1))
        assertEquals(listOf("explanation.python.loops.trace", "explanation.python.loops.trace_worked"), served.map { it.ref.logicalId })
        assertEquals("Bir döngü koşul doğru olduğu sürece tekrar eder.\nHer tur sayacı bir artırır.", served.first().text)
        assertTrue(source.explanationsFor(VersionedRef("objective.python.loops.trace", 2)).isEmpty())
        for (bad in listOf(
            explanations.replace("form=worked_example", "form=story"),
            explanations.replace("level=H2", "level=H5"),
            explanations.replace("form=canonical", "form=canonical\nlevel=H1"),
            explanations.replace("explanation.python.loops.trace_worked", "trace_worked"),
            explanations + "\nreviewer=me",
        )) {
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(authored + "\n" + bad) }
        }
    }

    @Test
    fun `code tests are read strictly, attached to their suite and served only for the item version they test`() {
        val tests = """

            [code_test_suite]
            logical_id=codetest.python.loops.count
            version=1
            item=item.python.loops.q1@v1
            build_objective=objective.python.loops.trace@v1

            [code_test]
            suite=codetest.python.loops.count@v1
            id=counts_three
            objective=objective.python.loops.explain@v1
            misconception=misconception.python.loops.off_by_one

            [code_test]
            suite=codetest.python.loops.count@v1
            id=counts_zero
            objective=objective.python.loops.explain@v1
        """.trimIndent()
        val source = FileContentSource { authored + "\n" + tests }
        val suite = assertNotNull(source.codeTestsFor(VersionedRef("item.python.loops.q1", 1)))
        assertEquals(listOf("counts_three", "counts_zero"), suite.tests.map { it.id })
        assertEquals(VersionedRef("objective.python.loops.trace", 1), suite.buildObjective)
        assertEquals("misconception.python.loops.off_by_one", suite.tests.first().misconceptionOnFailure)
        assertNull(source.codeTestsFor(VersionedRef("item.python.loops.q1", 2)), "another item version's tests do not test it")
        assertNull(FileContentSource { authored }.codeTestsFor(VersionedRef("item.python.loops.q1", 1)), "no tests written is the truthful answer")
        val again = "\n\n[code_test_suite]\nlogical_id=codetest.python.loops.again\nversion=1\nitem=item.python.loops.q1@v1\n\n" +
            "[code_test]\nsuite=codetest.python.loops.again@v1\nid=again\nobjective=objective.python.loops.explain@v1"
        for (bad in listOf(
            tests.replace("suite=codetest.python.loops.count@v1\nid=counts_zero", "suite=codetest.python.loops.other@v1\nid=counts_zero"),
            tests.replace("id=counts_zero", "id=counts_three"),
            tests.replace("id=counts_zero", "id=Counts Zero"),
            tests.replace("logical_id=codetest.python.loops.count", "logical_id=tests.python.loops.count"),
            tests.replace("item=item.python.loops.q1@v1", "item=item.python.loops.q1"),
            tests + "\nexpected_stdout=3",
            tests.substringBefore("\n\n[code_test]\n"),
            tests + again,
        )) {
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(authored + "\n" + bad) }
        }
    }

    @Test
    fun `a written comprehension check is read strictly and served for the item version it follows`() {
        val checks = """

            [comprehension_check]
            logical_id=comprehension.python.loops.count_line
            version=1
            item=item.python.loops.q1@v1
            objective=objective.python.loops.trace@v1
            kind=line_purpose
            evidence_type=explanation
            prompt=i += 1 satırı neden gerekli?\nSilinirse ne olur?
            choice_a=Döngü sonsuz olur
            choice_b=Çıktı değişmez
            answer=a
        """.trimIndent()
        val source = FileContentSource { authored + "\n" + checks }
        val served = source.comprehensionChecksFor(VersionedRef("item.python.loops.q1", 1)).single()
        assertEquals("i += 1 satırı neden gerekli?\nSilinirse ne olur?", served.prompt)
        assertEquals(mapOf("a" to "Döngü sonsuz olur", "b" to "Çıktı değişmez"), served.choices)
        assertTrue(source.comprehensionChecksFor(VersionedRef("item.python.loops.q1", 2)).isEmpty(), "another item version's checks do not follow it")
        assertTrue(FileContentSource { authored }.comprehensionChecksFor(VersionedRef("item.python.loops.q1", 1)).isEmpty())
        for (bad in listOf(
            checks.replace("kind=line_purpose", "kind=apply_again"),
            checks.replace("answer=a", "answer=c"),
            checks.replace("choice_b=Çıktı değişmez\n", ""),
            checks.replace("logical_id=comprehension.python.loops.count_line", "logical_id=quiz.python.loops.count_line"),
            checks.replace("evidence_type=explanation\n", ""),
            checks + "\nweight=2",
        )) {
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(authored + "\n" + bad) }
        }
    }

    @Test
    fun `answer keys and rubrics are read strictly and served for the item version they belong to`() {
        val written = """

            [answer_key]
            logical_id=answerkey.python.loops.count_word
            version=1
            item=item.python.loops.q1@v1
            objective=objective.python.loops.trace@v1
            case_sensitive=false

            [accepted_answer]
            key=answerkey.python.loops.count_word@v1
            text=three

            [accepted_answer]
            key=answerkey.python.loops.count_word@v1
            text=3

            [rubric]
            logical_id=rubric.python.loops.explain_count
            version=1
            item=item.python.loops.q1@v1

            [rubric_criterion]
            rubric=rubric.python.loops.explain_count@v1
            id=names_condition
            objective=objective.python.loops.explain@v1
            statement=Döngü koşulu yanlış olunca biter.
        """.trimIndent()
        val source = FileContentSource { authored + "\n" + written }
        val key = assertNotNull(source.answerKeyFor(VersionedRef("item.python.loops.q1", 1)))
        assertEquals(listOf("three", "3"), key.answers)
        assertTrue(key.accepts("Three"))
        val rubric = assertNotNull(source.rubricFor(VersionedRef("item.python.loops.q1", 1)))
        assertEquals(listOf("names_condition"), rubric.criteria.map { it.id })
        assertNull(source.answerKeyFor(VersionedRef("item.python.loops.q1", 2)))
        assertNull(source.rubricFor(VersionedRef("item.python.loops.q1", 2)))
        assertNull(FileContentSource { authored }.rubricFor(VersionedRef("item.python.loops.q1", 1)))
        for (bad in listOf(
            written.replace("key=answerkey.python.loops.count_word@v1\ntext=3", "key=answerkey.python.loops.other@v1\ntext=3"),
            written.replace("case_sensitive=false", "case_sensitive=maybe"),
            written.replace("rubric=rubric.python.loops.explain_count@v1", "rubric=rubric.python.loops.other@v1"),
            written.replace("id=names_condition", "id=Names Condition"),
            written.replace("logical_id=rubric.python.loops.explain_count", "logical_id=scheme.python.loops.explain_count"),
            written + "\nweight=2",
            written.substringBefore("\n\n[rubric_criterion]"),
            // A criterion of an undeclared rubric is refused even when every declared rubric is complete.
            written + "\n\n[rubric_criterion]\nrubric=rubric.python.loops.other@v1\nid=stray\nobjective=objective.python.loops.explain@v1\nstatement=x",
        )) {
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(authored + "\n" + bad) }
        }
    }

    // ------------------------------------------------------------------------------------ 15A

    private val task = """
        [task]
        logical_id=task.python.loops.practice
        version=1
        title=Döngü izleme alıştırması
        primary_skill=skill.python.loops@v1
        target_objectives=objective.python.loops.trace@v1
        purpose=practice
        activity_kind=code_reading_trace
        serves=continue_learning
        cost_minutes=10
        required_skills=
        explanations=
        items=item.python.loops.q1@v1
        lifecycle_status=validated
        content_origin=ai_generated
    """.trimIndent()

    @Test
    fun `only a whole line is a comment, so code and prose may contain a hash`() {
        val hashed = authored.replace("prompt=Bu döngü kaç kez çalışır?", "prompt=x = 1  # sayaç\\nprint(x)  # yazdır")
        val parsed = PackageFormat.parse(hashed.replace("[topic]", "# yorum satırı\n[topic]"))
        assertEquals("x = 1  # sayaç\nprint(x)  # yazdır", parsed.documents[itemRef], "a prompt keeps its hashes and its line breaks")
    }

    @Test
    fun `an authored task is read strictly and served only for the needs it declares about its own Skill`() {
        val parsed = PackageFormat.parse(authored + "\n\n" + task)
        val read = parsed.tasks.single()
        assertEquals(coach.model.TaskPurpose.PRACTICE, read.purpose)
        assertEquals(setOf(coach.model.NeedTrigger.CONTINUE_LEARNING), read.serves)
        assertEquals(listOf(itemRef), read.items)

        val source = FileContentSource { authored + "\n\n" + task }
        val skill = VersionedRef("skill.python.loops", 1)
        fun need(trigger: coach.model.NeedTrigger, about: VersionedRef = skill) =
            coach.model.LearningNeed("${trigger.id}:$about", trigger, listOf(about), coach.model.Criticality.REQUIRED)
        val served = source.taskCandidates(need(coach.model.NeedTrigger.CONTINUE_LEARNING)).single()
        assertEquals("authored:task.python.loops.practice@v1:continue_learning:$skill", served.id)
        assertEquals(10, served.costMinutes)
        assertEquals("Döngü izleme alıştırması", served.title)
        assertTrue(source.taskCandidates(need(coach.model.NeedTrigger.NEW_LEARNING)).isEmpty(), "a practice task is not a lesson")
        assertTrue(source.taskCandidates(need(coach.model.NeedTrigger.CONTINUE_LEARNING, VersionedRef("skill.python.loops", 2))).isEmpty(),
            "another version of the Skill is another Skill")
        assertTrue(FileContentSource { authored }.taskCandidates(need(coach.model.NeedTrigger.CONTINUE_LEARNING)).isEmpty(),
            "with no task authored, no candidate is invented")
    }

    @Test
    fun `a task that presents what the package lacks, or claims more trust than its items, refuses the package`() {
        for (bad in listOf(
            task.replace("items=item.python.loops.q1@v1", "items=item.python.loops.q9@v1"),
            // A candidate task claims no trust, so only the missing item itself can refuse it.
            task.replace("items=item.python.loops.q1@v1", "items=item.python.loops.q9@v1").replace("lifecycle_status=validated", "lifecycle_status=candidate"),
            task.replace("explanations=", "explanations=explanation.python.loops.canonical@v1"),
            task.replace("target_objectives=objective.python.loops.trace@v1", "target_objectives=objective.python.other.trace@v1"),
            task.replace("primary_skill=skill.python.loops@v1", "primary_skill=skill.python.other@v1"),
            task.replace("serves=continue_learning", "serves=continue_learning,verification_due"),
            task.replace("serves=continue_learning", "serves=finish_lesson"),
            // An unknown trigger beside a known one is still refused, never silently dropped.
            task.replace("serves=continue_learning", "serves=continue_learning,finish_lesson"),
            task.replace("purpose=practice", "purpose=diagnose"),
            task.replace("cost_minutes=10", "cost_minutes=0"),
            task.replace("activity_kind=code_reading_trace", "activity_kind=quiz"),
            task + "\npriority=1",
            task + "\n\n" + task,
        )) {
            assertFailsWith<PackageFormat.ParseFailure>(bad) { PackageFormat.parse(authored + "\n\n" + bad) }
        }
        // The item is validated by this package, so the task may say so; once it is only a candidate, it may not.
        val candidateItem = authored.replace("status=validated", "status=candidate")
        assertFailsWith<PackageFormat.ParseFailure> { PackageFormat.parse(candidateItem + "\n\n" + task) }
        assertEquals(1, PackageFormat.parse(candidateItem + "\n\n" + task.replace("lifecycle_status=validated", "lifecycle_status=candidate")).tasks.size)
    }

    // ------------------------------------------------------------------------------------ 15B

    private val second = """
        curriculum_package/1
        version=2
        source_refs=curriculum/content/15b
        provenance=authored_15b

        [skill]
        logical_id=skill.python.functions
        version=1
        canonical_name=Functions
        capability_statement=Write a small function
        lifecycle_status=published
        capability_kind=language_specific_production
        retention_profile=standard
        source_refs=src
        provenance=authored_15b

        [objective]
        logical_id=objective.python.functions.write
        version=1
        parent_skill=skill.python.functions@v1
        required=true
        criticality=standard
        acceptable_evidence_types=authored_code
        direct_evidence_types=authored_code
        required_direct_type=authored_code

        [prerequisite_edge]
        prerequisite=skill.python.loops@v1
        target=skill.python.functions@v1
        edge_version=1
        edge_kind=hard
        reason_kind=evidence_interpretability
        strictness_profile=default_prg_v0
        lifecycle_status=published
        provenance=authored_15b

        [explanation]
        logical_id=explanation.python.functions.write.canonical
        version=1
        objective=objective.python.functions.write@v1
        form=canonical
        text=Bir fonksiyon def ile tanımlanır.

        [task]
        logical_id=task.python.functions.teach
        version=1
        title=Fonksiyon yazmak
        primary_skill=skill.python.functions@v1
        target_objectives=objective.python.functions.write@v1
        purpose=teach
        activity_kind=content_explanation
        serves=new_learning
        cost_minutes=12
        explanations=explanation.python.functions.write.canonical@v1
        lifecycle_status=validated
        content_origin=ai_generated
    """.trimIndent()

    @Test
    fun `later packages are served with the first as one course, and published oldest first`() {
        val source = FileContentSource(later = { listOf(second) }, source = { authored })
        assertEquals(listOf(1, 2), source.curriculumPackages().map { it.version })
        assertEquals(1, assertNotNull(source.curriculumPackage()).version)
        assertNotNull(source.assessmentItem(itemRef), "the first package's items are still served")
        val functions = VersionedRef("skill.python.functions", 1)
        val need = coach.model.LearningNeed("new_learning:$functions", coach.model.NeedTrigger.NEW_LEARNING, listOf(functions),
            coach.model.Criticality.REQUIRED)
        assertEquals("authored:task.python.functions.teach@v1:new_learning:$functions", source.taskCandidates(need).single().id)
        assertEquals(1, source.explanationsFor(VersionedRef("objective.python.functions.write", 1)).size)
        // The second package's edge points at a Skill only the first publishes: that is resolved at publishing, not here.
        assertEquals(listOf("prerequisite_edge -> skill skill.python.loops@v1"), source.curriculumPackages()[1].unresolvedReferences())
    }

    @Test
    fun `packages that do not rise in version, or author something twice, are not served at all`() {
        for (later in listOf(
            second.replace("version=2\n", "version=1\n"),
            authored.replace("version=1\nsource_refs", "version=2\nsource_refs"),
        )) {
            val source = FileContentSource(later = { listOf(later) }, source = { authored })
            assertTrue(source.curriculumPackages().isEmpty(), later.take(40))
            assertNull(source.assessmentItem(itemRef), "nothing is half served")
            assertNotNull(source.failure)
        }
    }

    /** A third package that authors one thing a second time — and nothing else that the sequence check would object to. */
    private fun third(sections: List<String>) = "curriculum_package/1\nversion=3\nsource_refs=curriculum/content/test\nprovenance=test\n\n" +
        sections.joinToString("\n\n")

    private fun sectionsOf(text: String) = text.split("\n\n").filter { it.startsWith("[") }

    @Test
    fun `each kind of thing authored in two packages is named, and the course is not served`() {
        val v2 = sectionsOf(second)
        // The explanation again, under a package that otherwise repeats only the second one's graph (which publishing refuses).
        val explanationTwice = third(v2.filterNot { it.startsWith("[task]") })
        // The task again; its explanation renamed so only the task is repeated.
        val taskTwice = third(v2.map { it.replace("explanation.python.functions.write.canonical", "explanation.python.functions.write.canonical_again") })
        // A test suite and an answer key again, each over a renamed item, taken from the real second package.
        val shipped = sectionsOf(java.io.File("../app-wiring/src/main/assets/curriculum_package_v2.txt").readText())
        fun over(item: String, judge: String) = third(shipped.filter { s -> item in s && (s.startsWith("[resource]") || s.startsWith("[validation]") ||
            s.startsWith("[item]")) || judge in s && !s.startsWith("[item]") && !s.startsWith("[task]") }.map { it.replace(item, "${item}_again") })
        val suiteTwice = over("item.python.set_operations.demonstrate_capability.f02", "codetest.python.set_operations.demonstrate_capability.f02")
        val keyTwice = over("item.python.value_type_behavior.demonstrate_capability.f02", "answerkey.python.value_type_behavior.demonstrate_capability.f02")

        for ((later, named) in listOf(
            explanationTwice to "explanation explanation.python.functions.write.canonical@v1",
            taskTwice to "task task.python.functions.teach@v1",
            suiteTwice to "code test suite codetest.python.set_operations.demonstrate_capability.f02@v1",
            keyTwice to "answer key answerkey.python.value_type_behavior.demonstrate_capability.f02@v1",
        )) {
            val first = if (later === suiteTwice || later === keyTwice) java.io.File("../app-wiring/src/main/assets/curriculum_package.txt").readText() else authored
            val middle = if (later === suiteTwice || later === keyTwice) java.io.File("../app-wiring/src/main/assets/curriculum_package_v2.txt").readText() else second
            val source = FileContentSource(later = { listOf(middle, later) }, source = { first })
            assertTrue(source.curriculumPackages().isEmpty(), named)
            val failure = assertNotNull(source.failure as? PackageFormat.ParseFailure, "$named: ${source.failure}")
            assertEquals(listOf("$named is authored in more than one package"), failure.reasons)
        }
    }

    @Test
    fun `an authored package is served as pinned documents and items`() {
        val source = FileContentSource { authored }
        assertEquals("Bu döngü kaç kez çalışır?", assertNotNull(source.resource(itemRef)).body)
        assertEquals(itemRef, assertNotNull(source.assessmentItem(itemRef)).ref)
        // A different version of the same logical id is not served in place of the pinned one.
        assertNull(source.assessmentItem(VersionedRef(itemRef.logicalId, 2)))
        assertNull(source.resource(VersionedRef(itemRef.logicalId, 2)))
    }
}
