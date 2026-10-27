package coach.presentation

import coach.model.VersionedRef

/**
 * 17A (`VPLX-v0 / D-133`): Turkish display names for the published Topics and Skills.
 *
 * The curriculum's own names are identifiers turned into English title case ("Expressions Io", "Standard Io Basic").
 * They stay the canonical names in the store; these are **presentation copy**, keyed by logical id, so a new version of
 * a Topic or Skill keeps its name. A logical id that is not here falls back to the authored name — never to a guessed
 * translation — and a JVM test over the eight shipped packages requires every published Topic and Skill to be here.
 *
 * A name is a label, not a claim: none of them says how far the learner is, and an English or technical term is kept
 * where the lessons themselves use it.
 */
object CurriculumNames {

    /** The second segment of a Topic's logical id (`topic.<area>.<name>`), the curriculum's own grouping for Learn. */
    val areas: List<Pair<String, String>> = listOf(
        "python" to "Python",
        "c" to "C",
        "linux" to "Linux",
        "shell" to "Kabuk (shell)",
        "git" to "Git",
        "english" to "Teknik İngilizce",
    )

    const val OTHER_AREA = "Diğer"

    val topics: Map<String, String> = mapOf(
        "topic.python.execution" to "Programın çalışması",
        "topic.python.values_types" to "Değerler ve türler",
        "topic.python.expressions_io" to "İfadeler, girdi ve çıktı",
        "topic.python.conditionals" to "Koşullar",
        "topic.python.iteration" to "Döngüler",
        "topic.python.functions_scope" to "Fonksiyonlar ve kapsam",
        "topic.python.errors" to "Hatalar",
        "topic.python.testing_environments" to "Test ve çalışma ortamı",
        "topic.python.collections" to "Koleksiyonlar",
        "topic.python.modules_files" to "Modüller ve dosyalar",
        "topic.python.strings_bytes" to "Metinler ve baytlar",
        "topic.c.toolchain" to "Derleme araçları",
        "topic.c.types_expressions" to "Türler ve ifadeler",
        "topic.c.io_control" to "Girdi/çıktı ve akış kontrolü",
        "topic.c.functions_scope" to "C'de fonksiyonlar ve kapsam",
        "topic.c.pointers_arrays" to "İşaretçiler ve diziler",
        "topic.c.dynamic_memory" to "Dinamik bellek",
        "topic.linux.filesystem_permissions" to "Dosya sistemi ve izinler",
        "topic.linux.processes_io" to "Süreçler ve girdi/çıktı",
        "topic.shell.command_language" to "Komut dili",
        "topic.shell.pipelines_scripts" to "Boru hatları ve betikler",
        "topic.git.state_commits" to "Değişiklikler ve commit'ler",
        "topic.english.core_terms" to "Temel terimler",
        "topic.english.grammar_functions" to "Temel dil bilgisi yapıları",
        "topic.english.instructions_errors" to "Yönergeler ve hata mesajları",
        "topic.english.documentation_reading" to "Doküman okuma",
        "topic.english.technical_writing" to "Teknik yazma",
    )

    val skills: Map<String, String> = mapOf(
        "skill.computing.program_execution_model" to "Program çalışma modeli",
        "skill.computing.source_runtime_artifact_distinction" to "Kaynak kod, çalıştırılabilir dosya ve çıktı ayrımı",
        "skill.programming.state_assignment_model" to "Durum ve atama modeli",
        "skill.programming.expression_boolean_reasoning" to "İfadeler ve mantıksal sonuçlar",
        "skill.programming.branching_reasoning" to "Hangi kolun çalışacağını bulma",
        "skill.programming.iteration_reasoning" to "Döngüde durum ve sonlanma",
        "skill.programming.function_decomposition" to "Problemi fonksiyonlara bölme",
        "skill.programming.trace_execution_basic" to "Programın çalışma izini çıkarma",
        "skill.programming.debug_localization_basic" to "Hatanın yerini bulma",
        "skill.programming.test_case_basic" to "Test durumu tasarlama",
        "skill.programming.code_reading_basic" to "Kod okuma",
        "skill.programming.input_validation_reasoning" to "Girdiyi doğrulama",
        "skill.python.run_repl_script" to "Python'u çalıştırma: REPL ve script",
        "skill.python.value_type_behavior" to "Değer türlerinin davranışı",
        "skill.python.assignment_binding" to "Atama ve isim bağlama",
        "skill.python.expression_evaluation" to "İfadelerin hesaplanması",
        "skill.python.input_output_basic" to "Temel girdi ve çıktı",
        "skill.python.conditionals" to "Koşullar: if, elif, else",
        "skill.python.for_iteration" to "for döngüsü",
        "skill.python.while_termination" to "while döngüsü ve sonlanması",
        "skill.python.functions_parameters_return" to "Fonksiyon, parametre ve dönüş değeri",
        "skill.python.list_operations" to "Liste işlemleri",
        "skill.python.tuple_immutable_sequence" to "Tuple ve değiştirilemeyen diziler",
        "skill.python.set_operations" to "Küme (set) işlemleri",
        "skill.python.mapping_collections_basic" to "Sözlükler (dict)",
        "skill.python.exceptions_read_basic" to "Hata raporunu (traceback) okuma",
        "skill.python.scope_name_resolution" to "Kapsam ve isim çözümleme",
        "skill.python.modules_imports_basic" to "Modüller ve import",
        "skill.python.exception_handling" to "Hataları yakalama (try/except)",
        "skill.python.string_text_operations" to "Metin (string) işlemleri",
        "skill.python.path_handling" to "Dosya yollarıyla çalışma",
        "skill.python.files_paths_basic" to "Dosya okuma ve yazma",
        "skill.linux.terminal_filesystem_navigation" to "Terminalde dizinler arasında gezinme",
        "skill.c.compile_link_run_basic" to "Derleme, bağlama ve çalıştırma",
        "skill.c.declaration_type_model" to "C'de bildirimler ve türler",
        "skill.c.expression_evaluation" to "C'de ifadelerin hesaplanması",
        "skill.c.standard_io_basic" to "C'de standart girdi ve çıktı",
        "skill.c.conditionals" to "C'de koşullar: if ve switch",
        "skill.c.for_iteration" to "C'de for döngüsü",
        "skill.c.while_iteration" to "C'de while ve do-while",
        "skill.c.functions_basic" to "C'de fonksiyonlar",
        "skill.memory.address_value_distinction" to "Adres ve değer ayrımı",
        "skill.c.pointer_formation" to "İşaretçi oluşturma",
        "skill.c.pointer_dereference" to "İşaretçinin gösterdiği değere erişme",
        "skill.memory.storage_lifetime_intuition" to "Bellekteki nesnelerin ömrü",
        "skill.linux.process_exit_stdout_stderr_basic" to "Süreç, çıkış durumu, stdout ve stderr",
        "skill.shell.command_options_redirection_basic" to "Komut, seçenek, argüman ve çıktıyı dosyaya yazma",
        "skill.shell.pipeline_redirection" to "Boru hattı (pipe) ve yönlendirme",
        "skill.git.repository_status_diff" to "Deponun durumu ve farkları (status, diff)",
        "skill.git.stage_commit_history_basic" to "Hazırlama, commit ve geçmiş",
        "skill.english.recognize_core_technical_labels" to "Temel teknik etiketleri tanıma",
        "skill.english.technical_noun_phrase_recognition" to "Teknik isim öbeklerini çözme",
        "skill.english.be_and_simple_present_comprehension" to "is/are ve geniş zamanı anlama",
        "skill.english.imperative_instruction_comprehension" to "Emir kipindeki yönergeleri anlama",
        "skill.english.preposition_function_word_comprehension" to "Edatları ve küçük ilişki kelimelerini anlama",
        "skill.english.negation_question_comprehension" to "Olumsuz ve soru cümlelerini anlama",
        "skill.english.follow_bilingual_technical_instruction" to "İki dilli teknik bir yönergeyi izleme",
        "skill.english.read_simple_terminal_error_fragments" to "Kısa hata mesajlarını okuma",
        "skill.english.documentation_navigation" to "Dokümanda bilgi bulma",
        "skill.english.write_command_result_note" to "Komut ve sonuç notu yazma",
    )

    fun topic(ref: VersionedRef, authored: String?): String = topics[ref.logicalId] ?: authored ?: ref.logicalId

    fun skill(ref: VersionedRef, authored: String?): String = skills[ref.logicalId] ?: authored ?: ref.logicalId

    /** The Topic's area id, or null when its logical id does not follow `topic.<area>.<name>`. */
    fun areaOf(topicLogicalId: String): String? =
        topicLogicalId.split('.').takeIf { it.size >= 3 && it[0] == "topic" }?.get(1)

    fun areaLabel(area: String?): String = areas.firstOrNull { it.first == area }?.second ?: OTHER_AREA
}
