from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import re
import yaml


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "curriculum" / "decomposition" / "6c_foundations"
FBB = ROOT / "docs" / "V1_FOUNDATION_BACKBONE.md"


def dump(name: str, value: object) -> None:
    path = OUT / name
    path.write_text(
        yaml.safe_dump(value, allow_unicode=True, sort_keys=False, width=110),
        encoding="utf-8",
    )


domains = {
    "domain.technical_english": ("Technical English", "D01", "parallel_track"),
    "domain.python": ("Python", "D02", "common_foundation"),
    "domain.c": ("C", "D03", "common_foundation"),
    "domain.linux_git_shell": ("Linux + Git + Shell", "D04", "common_foundation"),
    "domain.data_structures_algorithms": ("Data Structures & Algorithms Foundations", "D05", "supporting_domain"),
}

module_topics = {
    "module.english.language_foundations": [
        "topic.english.core_terms", "topic.english.grammar_functions", "topic.english.instructions_errors"
    ],
    "module.english.technical_comprehension": [
        "topic.english.documentation_reading", "topic.english.technical_writing", "topic.english.spoken_communication"
    ],
    "module.python.programming_core": [
        "topic.python.execution", "topic.python.values_types", "topic.python.expressions_io",
        "topic.python.conditionals", "topic.python.iteration"
    ],
    "module.python.data_functions": [
        "topic.python.strings_bytes", "topic.python.collections", "topic.python.functions_scope",
        "topic.python.iterators_generators"
    ],
    "module.python.software_engineering": [
        "topic.python.errors", "topic.python.modules_files", "topic.python.objects_typing",
        "topic.python.testing_environments", "topic.python.packaging_cli"
    ],
    "module.python.systems_data": [
        "topic.python.os_network_concurrency", "topic.python.data_tensor", "topic.python.performance_automation"
    ],
    "module.c.language_toolchain": [
        "topic.c.toolchain", "topic.c.types_expressions", "topic.c.io_control", "topic.c.functions_scope"
    ],
    "module.c.memory_data": [
        "topic.c.pointers_arrays", "topic.c.strings_structs", "topic.c.dynamic_memory"
    ],
    "module.c.engineering": [
        "topic.c.modular_build", "topic.c.debugging_safety", "topic.c.files_bits"
    ],
    "module.linux.working_environment": [
        "topic.linux.filesystem_permissions", "topic.linux.processes_io", "topic.linux.environment_tools"
    ],
    "module.shell.command_automation": [
        "topic.shell.command_language", "topic.shell.pipelines_scripts"
    ],
    "module.git.version_control": [
        "topic.git.state_commits", "topic.git.branches_history", "topic.git.remotes_recovery"
    ],
    "module.dsa.analysis_sequences": [
        "topic.dsa.complexity_correctness", "topic.dsa.sequences_search", "topic.dsa.linear_structures"
    ],
    "module.dsa.recursive_structures": [
        "topic.dsa.recursion_sorting", "topic.dsa.trees_graphs"
    ],
}

module_domain = {
    "module.english.language_foundations": "domain.technical_english",
    "module.english.technical_comprehension": "domain.technical_english",
    "module.python.programming_core": "domain.python",
    "module.python.data_functions": "domain.python",
    "module.python.software_engineering": "domain.python",
    "module.python.systems_data": "domain.python",
    "module.c.language_toolchain": "domain.c",
    "module.c.memory_data": "domain.c",
    "module.c.engineering": "domain.c",
    "module.linux.working_environment": "domain.linux_git_shell",
    "module.shell.command_automation": "domain.linux_git_shell",
    "module.git.version_control": "domain.linux_git_shell",
    "module.dsa.analysis_sequences": "domain.data_structures_algorithms",
    "module.dsa.recursive_structures": "domain.data_structures_algorithms",
}


# id | topic | capability statement | kind | retention | hard prerequisites | soft prerequisites | professional tags
SKILL_ROWS = """
skill.computing.program_execution_model|topic.python.execution|Source, translator veya runtime ve çalışan process ilişkisini açıklayabilmek.|shared_reasoning|standard|||technical_communication
skill.computing.source_runtime_artifact_distinction|topic.python.execution|Source, script, object, executable ve runtime output artifact'larını ayırt edebilmek.|shared_reasoning|standard|skill.computing.program_execution_model||build_tooling
skill.programming.state_assignment_model|topic.python.values_types|State ve assignment değişimini adım adım izleyebilmek.|shared_reasoning|standard|||debugging
skill.programming.expression_boolean_reasoning|topic.python.expressions_io|Expression ve boolean sonuçlarını gerekçeli değerlendirebilmek.|shared_reasoning|standard|skill.programming.state_assignment_model||debugging
skill.programming.branching_reasoning|topic.python.conditionals|Koşula göre yürütülecek branch'i belirleyebilmek.|shared_reasoning|standard|skill.programming.expression_boolean_reasoning||debugging
skill.programming.iteration_reasoning|topic.python.iteration|Iteration state, invariant ve termination davranışını izleyebilmek.|shared_reasoning|complex|skill.programming.state_assignment_model|skill.programming.branching_reasoning|debugging
skill.programming.function_decomposition|topic.python.functions_scope|Problemi input, output ve sorumlulukları belirli fonksiyonlara bölebilmek.|shared_reasoning|standard|skill.programming.state_assignment_model||systems_design
skill.programming.trace_execution_basic|topic.python.errors|Kısa programın execution trace'ini üretip state geçişlerini ilişkilendirebilmek.|shared_reasoning|complex|skill.programming.state_assignment_model||debugging
skill.programming.debug_localization_basic|topic.python.errors|Symptom, fault location ve likely cause'u ayırarak basit bug'ı lokalize edebilmek.|shared_reasoning|complex|skill.programming.trace_execution_basic||debugging
skill.programming.test_case_basic|topic.python.testing_environments|Input, expected result ve edge case içeren bağımsız test case tasarlayabilmek.|shared_reasoning|standard|skill.programming.function_decomposition||testing
skill.programming.input_validation_reasoning|topic.python.expressions_io|Girdinin kabul koşullarını ve invalid-input davranışını tanımlayabilmek.|shared_reasoning|standard|skill.programming.branching_reasoning||testing;reliability
skill.programming.code_reading_basic|topic.python.errors|Küçük bir programın control/data flow'unu çalıştırmadan izleyebilmek.|shared_reasoning|complex|skill.programming.trace_execution_basic||debugging;source_reading
skill.engineering.reproducible_run_notes|topic.python.packaging_cli|Environment, command, input ve sonucu yeniden üretilebilir biçimde kaydedebilmek.|professional_workflow|standard|||reproducibility;technical_communication
skill.engineering.explain_debug_fix_basic|topic.python.errors|Bir bug için symptom, cause, fix ve verification zincirini açıklayabilmek.|professional_workflow|complex|skill.programming.debug_localization_basic|skill.programming.test_case_basic|debugging;technical_communication
skill.english.recognize_core_technical_labels|topic.english.core_terms|Temel file, run, error, value, input ve output etiketlerini bağlamda tanıyabilmek.|language_recognition|factual|||technical_communication
skill.english.technical_noun_phrase_recognition|topic.english.grammar_functions|Basit article, plural ve modifier içeren teknik noun phrase'i çözümleyebilmek.|language_reasoning|standard|skill.english.recognize_core_technical_labels||technical_communication
skill.english.be_and_simple_present_comprehension|topic.english.grammar_functions|Be ve simple-present ile kurulan kısa teknik ifadelerin işlevini anlayabilmek.|language_reasoning|standard|skill.english.recognize_core_technical_labels||technical_communication
skill.english.imperative_instruction_comprehension|topic.english.instructions_errors|Kısa imperative teknik yönergede eylem ve nesneyi belirleyebilmek.|language_reasoning|standard|skill.english.recognize_core_technical_labels||technical_communication
skill.english.follow_bilingual_technical_instruction|topic.english.instructions_errors|Öğretilmiş grammar ve vocabulary ile kısa bilingual teknik yönergeyi izleyebilmek.|language_application|standard|skill.english.imperative_instruction_comprehension||technical_communication
skill.english.read_simple_terminal_error_fragments|topic.english.instructions_errors|Kısa terminal veya compiler error fragment'ında ana sinyali bulabilmek.|language_application|standard|skill.english.recognize_core_technical_labels||debugging;technical_communication
skill.english.preposition_function_word_comprehension|topic.english.grammar_functions|Teknik yönergelerde temel preposition ve function-word rollerini ayırt edebilmek.|language_reasoning|standard|skill.english.be_and_simple_present_comprehension||technical_communication
skill.english.negation_question_comprehension|topic.english.grammar_functions|Basit teknik olumsuzluk ve soru yapılarının anlamını çözebilmek.|language_reasoning|standard|skill.english.be_and_simple_present_comprehension||technical_communication
skill.english.documentation_navigation|topic.english.documentation_reading|Başlık, code block, option ve example işaretlerini kullanarak kısa dokümanda bilgi bulabilmek.|language_application|standard|skill.english.recognize_core_technical_labels||source_reading
skill.english.read_definition_and_constraint|topic.english.documentation_reading|Kısa teknik tanımda kavram, koşul ve kısıtı ayırt edebilmek.|language_application|complex|skill.english.technical_noun_phrase_recognition;skill.english.be_and_simple_present_comprehension||source_reading
skill.english.read_procedure_sequence|topic.english.documentation_reading|Kısa teknik prosedürde adım sırasını ve conditional uyarıyı izleyebilmek.|language_application|complex|skill.english.imperative_instruction_comprehension;skill.english.preposition_function_word_comprehension||source_reading
skill.english.write_command_result_note|topic.english.technical_writing|Öğretilmiş kalıplarla command, input ve result içeren kısa teknik not yazabilmek.|language_production|standard|skill.english.be_and_simple_present_comprehension|skill.engineering.reproducible_run_notes|technical_communication;reproducibility
skill.english.write_basic_bug_description|topic.english.technical_writing|Öğretilmiş dil ile expected, actual ve error bilgisini içeren kısa bug açıklaması yazabilmek.|language_production|complex|skill.english.negation_question_comprehension|skill.engineering.explain_debug_fix_basic|technical_communication;debugging
skill.english.explain_simple_technical_process|topic.english.spoken_communication|Öğretilmiş kalıplarla kısa bir teknik süreci sözlü veya yazılı sıralı açıklayabilmek.|language_production|complex|skill.english.read_procedure_sequence||technical_communication
skill.english.ask_clarifying_technical_question|topic.english.spoken_communication|Eksik requirement veya belirsiz hata için basit açıklayıcı soru kurabilmek.|language_production|standard|skill.english.negation_question_comprehension||technical_communication
skill.python.run_repl_script|topic.python.execution|Python REPL ve script çalıştırıp output ile error sonucunu ayırt edebilmek.|language_specific_production|standard|skill.computing.program_execution_model||build_tooling
skill.python.value_type_behavior|topic.python.values_types|Temel Python value türlerini ve runtime type davranışını ayırt edebilmek.|language_specific_reasoning|standard|skill.python.run_repl_script||debugging
skill.python.assignment_binding|topic.python.values_types|Python assignment ve name binding ile state değişimi kurabilmek.|language_specific_production|standard|skill.programming.state_assignment_model;skill.python.value_type_behavior||debugging
skill.python.expression_evaluation|topic.python.expressions_io|Python operator precedence ve expression evaluation sonucunu izleyebilmek.|language_specific_reasoning|standard|skill.python.value_type_behavior;skill.programming.expression_boolean_reasoning||debugging
skill.python.input_output_basic|topic.python.expressions_io|Basit stdin/input ve stdout/print akışı kurabilmek.|language_specific_production|standard|skill.python.assignment_binding||reproducibility
skill.python.conditionals|topic.python.conditionals|If, elif ve else ile doğru branch davranışı üretebilmek.|language_specific_production|standard|skill.python.expression_evaluation;skill.programming.branching_reasoning||debugging
skill.python.for_iteration|topic.python.iteration|Iterable üzerinde for iteration kurup sonucu doğrulayabilmek.|language_specific_production|standard|skill.python.assignment_binding;skill.programming.iteration_reasoning||debugging
skill.python.while_termination|topic.python.iteration|While state update ve termination condition kurup non-termination'ı düzeltebilmek.|language_specific_production|complex|skill.python.conditionals;skill.programming.iteration_reasoning||debugging
skill.python.loop_control_flow|topic.python.iteration|Break, continue ve loop-else davranışını uygun durumda kullanabilmek.|language_specific_production|complex|skill.python.for_iteration;skill.python.while_termination||debugging
skill.python.comprehension_transformation|topic.python.iteration|Comprehension ile açık iteration arasındaki dönüşümü okuyup uygun olanı yazabilmek.|language_specific_production|complex|skill.python.for_iteration;skill.python.conditionals||source_reading
skill.python.string_text_operations|topic.python.strings_bytes|Unicode string üzerinde slicing, searching, formatting ve normalization-aware temel işlemler yapabilmek.|language_specific_production|standard|skill.python.expression_evaluation||technical_communication
skill.python.bytes_text_boundary|topic.python.strings_bytes|Bytes ile text farkını ve encode/decode sınırını güvenli örnekte uygulayabilmek.|language_specific_production|complex|skill.python.string_text_operations||reliability
skill.python.list_operations|topic.python.collections|Mutable ordered list üzerinde oluşturma, güncelleme, traversal ve copy davranışını kullanabilmek.|language_specific_production|standard|skill.python.for_iteration||debugging
skill.python.tuple_immutable_sequence|topic.python.collections|Tuple ve immutable sequence davranışını uygun veri modelinde kullanabilmek.|language_specific_production|standard|skill.python.for_iteration||systems_design
skill.python.set_operations|topic.python.collections|Set membership ve temel set işlemlerini uniqueness problemi için kullanabilmek.|language_specific_production|standard|skill.python.for_iteration||performance_reasoning
skill.python.mapping_collections_basic|topic.python.collections|Dictionary key/value lookup, update ve iteration davranışını kullanabilmek.|language_specific_production|standard|skill.python.for_iteration;skill.python.value_type_behavior||debugging
skill.python.functions_parameters_return|topic.python.functions_scope|Function, parameter, argument ve return ile küçük davranışları ayırabilmek.|language_specific_production|standard|skill.python.assignment_binding;skill.programming.function_decomposition||systems_design
skill.python.scope_name_resolution|topic.python.functions_scope|Local, enclosing, global ve built-in name resolution sınırlarını izleyebilmek.|language_specific_reasoning|complex|skill.python.functions_parameters_return;skill.python.assignment_binding||debugging
skill.python.argument_binding|topic.python.functions_scope|Positional, keyword, default ve variadic argument binding davranışını güvenli kullanabilmek.|language_specific_production|complex|skill.python.functions_parameters_return||debugging
skill.python.first_class_callable|topic.python.functions_scope|Callable değerleri callback veya composition bağlamında kullanabilmek.|language_specific_production|complex|skill.python.functions_parameters_return;skill.python.scope_name_resolution||systems_design
skill.python.iterator_protocol|topic.python.iterators_generators|Iterable, iterator ve StopIteration modelini açıklayıp custom iteration'ı okuyabilmek.|language_specific_reasoning|complex|skill.python.for_iteration;skill.python.functions_parameters_return||source_reading
skill.python.generator_functions|topic.python.iterators_generators|Yield tabanlı generator ile lazy sequence behavior'ı üretebilmek.|language_specific_production|complex|skill.python.iterator_protocol;skill.python.scope_name_resolution||performance_reasoning
skill.python.exceptions_read_basic|topic.python.errors|Exception türü, traceback frame ve fault location bilgisini okuyabilmek.|language_specific_reasoning|standard|skill.python.run_repl_script||debugging
skill.python.exception_handling|topic.python.errors|Dar exception handling, propagation ve cleanup davranışını doğru kurabilmek.|language_specific_production|complex|skill.python.exceptions_read_basic;skill.python.functions_parameters_return||reliability
skill.python.debugging_tools|topic.python.errors|Breakpoint, inspection ve minimal reproduction ile Python bug'ını lokalize edebilmek.|tool_specific_production|complex|skill.programming.debug_localization_basic;skill.python.exceptions_read_basic||debugging
skill.python.modules_imports_basic|topic.python.modules_files|Module namespace, import resolution ve entry-point guard davranışını kullanabilmek.|language_specific_production|standard|skill.python.run_repl_script;skill.python.scope_name_resolution||build_tooling
skill.python.path_handling|topic.python.modules_files|Relative, absolute ve platform-aware path işlemlerini güvenli kurabilmek.|language_specific_production|standard|skill.python.string_text_operations||reproducibility
skill.python.files_paths_basic|topic.python.modules_files|Text file open, read, write ve deterministic close akışını kurabilmek.|language_specific_production|standard|skill.python.path_handling;skill.python.exception_handling||reliability
skill.python.class_object_model|topic.python.objects_typing|Class, instance, attribute ve method binding modelini küçük örnekte kullanabilmek.|language_specific_production|complex|skill.python.functions_parameters_return;skill.python.scope_name_resolution||systems_design
skill.python.data_model_protocols|topic.python.objects_typing|Temel dunder protocol behavior'ını magic ezberi yerine object model üzerinden açıklayabilmek.|language_specific_reasoning|complex|skill.python.class_object_model;skill.python.iterator_protocol||source_reading
skill.python.type_hints_static_checking|topic.python.objects_typing|Type annotation yazıp static checker sonucunu runtime behavior'dan ayırabilmek.|tool_specific_production|complex|skill.python.functions_parameters_return;skill.python.class_object_model||testing;build_tooling
skill.python.unit_testing|topic.python.testing_environments|Bağımsız unit test, assertion, fixture ve failure message üretebilmek.|tool_specific_production|complex|skill.programming.test_case_basic;skill.python.functions_parameters_return||testing
skill.python.test_doubles_boundaries|topic.python.testing_environments|External dependency sınırında fake veya mock kullanımını davranış doğrulamasından ayırabilmek.|tool_specific_production|complex|skill.python.unit_testing;skill.python.class_object_model||testing
skill.python.virtual_environment_dependency|topic.python.testing_environments|Virtual environment ve declared dependency ile yeniden üretilebilir Python environment kurabilmek.|tool_specific_production|standard|skill.python.run_repl_script|skill.engineering.reproducible_run_notes|build_tooling;reproducibility
skill.python.package_layout_metadata|topic.python.packaging_cli|Importable package layout ve temel project metadata'sını kurabilmek.|tool_specific_production|complex|skill.python.modules_imports_basic;skill.python.virtual_environment_dependency||build_tooling
skill.python.cli_argument_parsing|topic.python.packaging_cli|CLI arguments, validation, exit status ve help behavior'ı kurabilmek.|language_specific_production|complex|skill.python.functions_parameters_return;skill.programming.input_validation_reasoning||technical_communication
skill.python.os_subprocess_interaction|topic.python.os_network_concurrency|Environment, filesystem ve subprocess çağrılarını argument-safe biçimde kullanabilmek.|language_specific_production|complex|skill.python.exception_handling;skill.python.path_handling|skill.linux.process_exit_stdout_stderr_basic|reliability;operational_safety
skill.python.network_client_basic|topic.python.os_network_concurrency|Timeout ve error handling içeren basit network client akışını kurabilmek.|language_specific_production|complex|skill.python.exception_handling;skill.python.bytes_text_boundary||reliability
skill.python.async_task_coordination|topic.python.os_network_concurrency|Async task, await, cancellation ve timeout davranışını küçük I/O örneğinde kurabilmek.|language_specific_production|complex|skill.python.exception_handling;skill.python.first_class_callable||concurrency;reliability
skill.python.thread_process_choice|topic.python.os_network_concurrency|Thread, process ve async seçeneklerini workload ve isolation ihtiyacına göre ayırt edebilmek.|language_specific_reasoning|complex|skill.python.async_task_coordination|skill.python.os_subprocess_interaction|performance_reasoning
skill.python.data_table_transformation|topic.python.data_tensor|Tabular veriyi schema, missing value ve reproducibility guard'larıyla dönüştürebilmek.|tool_specific_production|complex|skill.python.mapping_collections_basic;skill.python.files_paths_basic||reproducibility
skill.python.numpy_array_shape_dtype|topic.python.data_tensor|Array shape, dtype, indexing ve broadcasting behavior'ını izleyebilmek.|tool_specific_production|complex|skill.python.list_operations;skill.dsa.complexity_growth_intuition||performance_reasoning
skill.python.tensor_device_dtype|topic.python.data_tensor|Tensor shape, device ve dtype dönüşümlerini explicit ve doğrulanabilir kurabilmek.|tool_specific_production|complex|skill.python.numpy_array_shape_dtype||performance_reasoning
skill.python.profiling_measurement|topic.python.performance_automation|Timing ve profiler artifact'ı ile Python bottleneck candidate'ını ölçebilmek.|tool_specific_production|complex|skill.engineering.reproducible_run_notes;skill.python.unit_testing||profiling_benchmarking
skill.python.benchmark_automation|topic.python.performance_automation|Warmup, repeated runs ve structured result içeren küçük benchmark automation'ı yazabilmek.|language_specific_production|complex|skill.python.profiling_measurement;skill.python.cli_argument_parsing||profiling_benchmarking;reproducibility
skill.c.compile_link_run_basic|topic.c.toolchain|C translation, compile, link ve run aşamalarını araç çıktısıyla ayırt edebilmek.|tool_specific_production|standard|skill.linux.terminal_filesystem_navigation;skill.computing.program_execution_model||build_tooling
skill.c.declaration_type_model|topic.c.types_expressions|C declaration, scalar type, qualifier ve conversion davranışını okuyup yazabilmek.|language_specific_production|complex|skill.c.compile_link_run_basic;skill.programming.state_assignment_model||debugging
skill.c.expression_evaluation|topic.c.types_expressions|C operator, precedence, integer conversion ve side-effect sınırını güvenli değerlendirebilmek.|language_specific_reasoning|complex|skill.c.declaration_type_model;skill.programming.expression_boolean_reasoning||debugging;operational_safety
skill.c.standard_io_basic|topic.c.io_control|Basit formatted input/output akışını return-value ve bounds farkındalığıyla kurabilmek.|language_specific_production|standard|skill.c.declaration_type_model||reliability
skill.c.conditionals|topic.c.io_control|C if/switch branch davranışını doğru condition ile üretebilmek.|language_specific_production|standard|skill.c.expression_evaluation;skill.programming.branching_reasoning||debugging
skill.c.for_iteration|topic.c.io_control|C for loop state, condition ve update davranışını doğru kurabilmek.|language_specific_production|standard|skill.c.expression_evaluation;skill.programming.iteration_reasoning||debugging
skill.c.while_iteration|topic.c.io_control|C while/do-while termination davranışını kurup non-termination'ı düzeltebilmek.|language_specific_production|complex|skill.c.conditionals;skill.programming.iteration_reasoning||debugging
skill.c.functions_basic|topic.c.functions_scope|C function declaration, prototype, call, parameter ve return davranışını kullanabilmek.|language_specific_production|standard|skill.c.declaration_type_model;skill.programming.function_decomposition||systems_design
skill.c.scope_linkage_storage_duration|topic.c.functions_scope|Block/file scope, linkage ve storage duration kavramlarını örnekte ayırt edebilmek.|language_specific_reasoning|complex|skill.c.functions_basic;skill.memory.storage_lifetime_intuition||debugging
skill.memory.address_value_distinction|topic.c.pointers_arrays|Stored value ile memory address kavramını örnekte ayırt edebilmek.|shared_reasoning|complex|skill.c.declaration_type_model||debugging
skill.c.pointer_formation|topic.c.pointers_arrays|Pointer declaration ve address-of ile type-compatible pointer oluşturabilmek.|language_specific_production|complex|skill.memory.address_value_distinction;skill.c.declaration_type_model||operational_safety
skill.c.pointer_dereference|topic.c.pointers_arrays|Geçerli pointer üzerinden value okuyup kontrollü güncelleme yapabilmek.|language_specific_production|complex|skill.c.pointer_formation||operational_safety;debugging
skill.c.array_bounds_indexing|topic.c.pointers_arrays|Array indexing, extent ve bounds ilişkisini güvenli biçimde uygulayabilmek.|language_specific_production|complex|skill.c.pointer_dereference;skill.programming.iteration_reasoning||operational_safety
skill.c.array_pointer_distinction|topic.c.pointers_arrays|Array object, pointer value ve decay davranışını uygun context'te ayırt edebilmek.|language_specific_reasoning|complex|skill.c.array_bounds_indexing;skill.c.pointer_formation||debugging
skill.c.string_null_termination|topic.c.strings_structs|C string buffer, terminator ve length/capacity sınırını güvenli yönetebilmek.|language_specific_production|complex|skill.c.array_bounds_indexing||operational_safety
skill.c.struct_enum_model|topic.c.strings_structs|Struct ve enum ile ilişkili veriyi açık layout/meaning altında modelleyebilmek.|language_specific_production|standard|skill.c.declaration_type_model||systems_design
skill.c.dynamic_allocation_lifecycle|topic.c.dynamic_memory|Allocate, ownership, use ve free lifecycle'ını hata yollarıyla birlikte kurabilmek.|language_specific_production|complex|skill.c.pointer_dereference;skill.memory.storage_lifetime_intuition||operational_safety;reliability
skill.c.allocation_size_overflow_safety|topic.c.dynamic_memory|Allocation size hesabında overflow ve bounds riskini kontrol edebilmek.|language_specific_reasoning|complex|skill.c.dynamic_allocation_lifecycle;skill.c.expression_evaluation||operational_safety
skill.memory.storage_lifetime_intuition|topic.c.dynamic_memory|Automatic, static ve dynamic object lifetime sınırlarını başlangıç seviyesinde ayırt edebilmek.|shared_reasoning|complex|skill.c.functions_basic||debugging
skill.c.header_translation_unit_interface|topic.c.modular_build|Header declaration, definition ve translation-unit sınırını doğru kurabilmek.|language_specific_production|complex|skill.c.functions_basic;skill.c.compile_link_run_basic||build_tooling
skill.c.multi_file_build|topic.c.modular_build|Birden çok translation unit'i deterministic flags ile compile/link edebilmek.|tool_specific_production|complex|skill.c.header_translation_unit_interface;skill.c.compile_link_run_basic||build_tooling;reproducibility
skill.c.compiler_diagnostic_reading|topic.c.debugging_safety|Compiler diagnostic'te location, category ve likely source'u belirleyebilmek.|tool_specific_reasoning|standard|skill.c.compile_link_run_basic;skill.programming.debug_localization_basic||debugging
skill.c.debugger_trace_basic|topic.c.debugging_safety|Debugger ile stack, variable ve control-flow state'ini izleyebilmek.|tool_specific_production|complex|skill.c.multi_file_build;skill.programming.trace_execution_basic||debugging
skill.c.sanitizer_undefined_behavior|topic.c.debugging_safety|Sanitizer bulgusunu undefined behavior kaynağına lokalize edip doğrulayabilmek.|tool_specific_production|complex|skill.c.pointer_dereference;skill.c.compiler_diagnostic_reading||debugging;operational_safety
skill.c.file_io_error_handling|topic.c.files_bits|C file I/O lifecycle ve error return handling davranışını kurabilmek.|language_specific_production|complex|skill.c.standard_io_basic;skill.c.pointer_dereference||reliability
skill.c.bitwise_mask_reasoning|topic.c.files_bits|Unsigned bit mask, shift ve flag behavior'ını örnek üzerinde izleyebilmek.|language_specific_reasoning|complex|skill.c.expression_evaluation||systems_design
skill.linux.terminal_filesystem_navigation|topic.linux.filesystem_permissions|Relative/absolute path ve working directory ile güvenli terminal navigation yapabilmek.|tool_specific_production|standard|||reproducibility
skill.linux.file_permissions_ownership|topic.linux.filesystem_permissions|Read/write/execute permission ve ownership etkisini temel örnekte yorumlayabilmek.|tool_specific_reasoning|complex|skill.linux.terminal_filesystem_navigation||operational_safety
skill.linux.links_file_identity|topic.linux.filesystem_permissions|Symbolic link, hard link ve pathname/file identity farkını ayırt edebilmek.|systems_reasoning|complex|skill.linux.terminal_filesystem_navigation||reliability
skill.linux.process_exit_stdout_stderr_basic|topic.linux.processes_io|Process, exit status, stdout ve stderr davranışını ayırt edebilmek.|systems_reasoning|standard|skill.computing.program_execution_model||debugging
skill.linux.process_signal_job_control|topic.linux.processes_io|Process ID, signal ve foreground/background job davranışını güvenli yönetebilmek.|tool_specific_production|complex|skill.linux.process_exit_stdout_stderr_basic||operational_safety
skill.linux.process_inspection|topic.linux.processes_io|Process tree, resource snapshot ve command line bilgisini araçlarla okuyabilmek.|tool_specific_production|complex|skill.linux.process_exit_stdout_stderr_basic||debugging
skill.linux.environment_path_resolution|topic.linux.environment_tools|Environment variable ve PATH command resolution davranışını izleyebilmek.|systems_reasoning|standard|skill.linux.terminal_filesystem_navigation||build_tooling
skill.linux.tool_discovery_help|topic.linux.environment_tools|Man/help/version output'undan command kullanımını ve capability sınırını bulabilmek.|tool_specific_production|standard|skill.linux.environment_path_resolution||source_reading
skill.shell.command_options_redirection_basic|topic.shell.command_language|Command, option, argument ve quoting yapısını doğru kurabilmek.|tool_specific_production|standard|skill.linux.terminal_filesystem_navigation||operational_safety
skill.shell.expansion_quoting|topic.shell.command_language|Variable, glob ve command substitution expansion'ını quoting ile güvenli kontrol edebilmek.|tool_specific_production|complex|skill.shell.command_options_redirection_basic||operational_safety
skill.shell.pipeline_redirection|topic.shell.pipelines_scripts|Pipeline ve stdin/stdout/stderr redirection akışını kurup exit behavior'ı yorumlayabilmek.|tool_specific_production|complex|skill.shell.command_options_redirection_basic;skill.linux.process_exit_stdout_stderr_basic||reproducibility
skill.shell.script_control_flow|topic.shell.pipelines_scripts|Shell script'te condition, loop, function ve fail-fast davranışını kurabilmek.|tool_specific_production|complex|skill.shell.expansion_quoting;skill.shell.pipeline_redirection||automation;reliability
skill.git.repository_status_diff|topic.git.state_commits|Working tree ve index değişikliklerini status/diff ile okuyabilmek.|tool_specific_production|standard|skill.linux.terminal_filesystem_navigation||open_source_workflow
skill.git.stage_commit_history_basic|topic.git.state_commits|Seçili değişikliği stage ve commit edip resulting history'yi doğrulayabilmek.|tool_specific_production|standard|skill.git.repository_status_diff||open_source_workflow
skill.git.branch_merge_model|topic.git.branches_history|Branch/ref ve merge ancestry modelini basit repository üzerinde uygulayabilmek.|tool_specific_production|complex|skill.git.stage_commit_history_basic||open_source_workflow
skill.git.history_inspection|topic.git.branches_history|Log, show ve blame benzeri history views ile değişikliğin kaynağını izleyebilmek.|tool_specific_production|complex|skill.git.stage_commit_history_basic||source_reading;open_source_workflow
skill.git.remote_sync|topic.git.remotes_recovery|Fetch, pull/rebase seçimi ve push davranışını local/remote ref modeliyle kullanabilmek.|tool_specific_production|complex|skill.git.branch_merge_model||open_source_workflow
skill.git.conflict_resolution|topic.git.remotes_recovery|Merge/rebase conflict'ini intent'i koruyarak çözmek ve sonucu test etmek.|tool_specific_production|complex|skill.git.remote_sync;skill.programming.test_case_basic||open_source_workflow;testing
skill.git.recovery_safe_undo|topic.git.remotes_recovery|Restore, revert ve reset seçeneklerini history/publication etkisine göre güvenli seçebilmek.|tool_specific_reasoning|complex|skill.git.history_inspection||operational_safety;open_source_workflow
skill.dsa.complexity_growth_intuition|topic.dsa.complexity_correctness|Sabit, logarithmic ve linear benzeri growth farklarını input büyümesi üzerinden ayırt edebilmek.|algorithmic_reasoning|standard|skill.programming.expression_boolean_reasoning||performance_reasoning
skill.dsa.asymptotic_bound_reasoning|topic.dsa.complexity_correctness|Basit algoritma için dominant work ve asymptotic bound gerekçesi kurabilmek.|algorithmic_reasoning|complex|skill.dsa.complexity_growth_intuition;skill.programming.iteration_reasoning||performance_reasoning
skill.dsa.algorithm_correctness_invariant|topic.dsa.complexity_correctness|Precondition, invariant ve postcondition ile küçük algoritmanın doğruluğunu açıklayabilmek.|algorithmic_reasoning|complex|skill.programming.iteration_reasoning;skill.programming.test_case_basic||testing
skill.dsa.sequence_traversal|topic.dsa.sequences_search|Sequence üzerinde indeksli veya iterator tabanlı traversal kurabilmek.|algorithmic_production|standard|skill.programming.iteration_reasoning;skill.python.list_operations||debugging
skill.dsa.linear_search|topic.dsa.sequences_search|Linear search'ü implement edip termination ve result contract'ını açıklayabilmek.|algorithmic_production|complex|skill.dsa.sequence_traversal;skill.dsa.algorithm_correctness_invariant||testing
skill.dsa.binary_search|topic.dsa.sequences_search|Sorted-input invariant altında binary search boundary update'lerini doğru kurabilmek.|algorithmic_production|complex|skill.dsa.linear_search;skill.dsa.asymptotic_bound_reasoning||debugging
skill.dsa.stack_queue_behavior|topic.dsa.linear_structures|Stack ve queue semantics'ini problem behavior'ına göre seçip uygulayabilmek.|algorithmic_production|standard|skill.python.list_operations||systems_design
skill.dsa.linked_structure_reasoning|topic.dsa.linear_structures|Node/link traversal ve update invariants'ını pointer veya reference modeliyle açıklayabilmek.|algorithmic_reasoning|complex|skill.c.pointer_dereference;skill.dsa.sequence_traversal||systems_design
skill.dsa.hash_table_tradeoff|topic.dsa.linear_structures|Hash lookup, collision ve capacity trade-off'larını temel kullanımda açıklayabilmek.|algorithmic_reasoning|complex|skill.python.mapping_collections_basic;skill.dsa.complexity_growth_intuition||performance_reasoning
skill.dsa.recursive_problem_decomposition|topic.dsa.recursion_sorting|Base case ve recursive reduction ile küçük problemi parçalayabilmek.|algorithmic_production|complex|skill.programming.function_decomposition;skill.programming.branching_reasoning||debugging
skill.dsa.sorting_comparison_reasoning|topic.dsa.recursion_sorting|Comparison sorting behavior'ını correctness ve growth açısından karşılaştırabilmek.|algorithmic_reasoning|complex|skill.dsa.asymptotic_bound_reasoning;skill.dsa.algorithm_correctness_invariant||performance_reasoning
skill.dsa.tree_traversal_basic|topic.dsa.trees_graphs|Tree node/edge modelinde depth-first ve breadth-first traversal'ı uygulayabilmek.|algorithmic_production|complex|skill.dsa.stack_queue_behavior;skill.dsa.recursive_problem_decomposition||systems_design
skill.dsa.graph_traversal_basic|topic.dsa.trees_graphs|Graph adjacency modelinde visited-state ile BFS/DFS traversal kurabilmek.|algorithmic_production|complex|skill.dsa.tree_traversal_basic;skill.python.set_operations||systems_design
""".strip().splitlines()


def parse_skills() -> list[dict]:
    evidence_by_kind = {
        "shared_reasoning": ["explanation", "code_reading"],
        "professional_workflow": ["hands_on_workflow", "explanation"],
        "language_recognition": ["recognition", "structured_response"],
        "language_reasoning": ["reading_comprehension", "structured_response"],
        "language_application": ["reading_comprehension", "hands_on_task"],
        "language_production": ["written_or_spoken_production"],
        "language_specific_production": ["authored_code", "deterministic_test"],
        "language_specific_reasoning": ["code_reading", "explanation"],
        "tool_specific_production": ["hands_on_system_task", "observed_result"],
        "tool_specific_reasoning": ["artifact_analysis", "explanation"],
        "systems_reasoning": ["system_observation", "explanation"],
        "algorithmic_reasoning": ["explanation", "trace"],
        "algorithmic_production": ["authored_code", "deterministic_test"],
    }
    result = []
    for line in SKILL_ROWS:
        sid, topic, statement, kind, retention, hard, soft, tags = line.split("|")
        domain = next(module_domain[m] for m, topics in module_topics.items() if topic in topics)
        evidence = evidence_by_kind[kind]
        result.append({
            "skill_id_candidate": sid,
            "canonical_name": sid.split(".")[-1].replace("_", " ").title(),
            "capability_statement": statement,
            "capability_kind": kind,
            "lifecycle_status": "draft",
            "authoring_status": "internally_qa_passed",
            "primary_teaching_topic_id": topic,
            "linked_topic_ids": [],
            "shared_placement_domain_ids": [domain],
            "independent_evidence_path": {
                "observable": True,
                "direct_evidence_types": evidence,
                "example_outcome": statement,
            },
            "boundaries": {
                "prerequisite_boundary": "Declared Skill prerequisites define the interpretable source boundary.",
                "remediation_boundary": "Failure is remediated at this capability, not by resetting its Domain.",
                "reuse_boundary": "Reuse requires semantic identity; context-only differences use TopicSkillLink.",
            },
            "shared_vs_specific": {
                "classification": "shared" if kind in {"shared_reasoning", "professional_workflow"} else "domain_specific",
                "rationale": "Capability identity follows behavior and evidence, not curriculum placement.",
            },
            "evidence_depth_expectations": ["independent_application", "delayed_retention"] + (["debugging"] if "debugging" in tags else []),
            "retention_profile": retention,
            "diagnostic_eligibility": "eligible",
            "critical_prerequisite_candidate": sid in {
                "skill.programming.state_assignment_model", "skill.programming.expression_boolean_reasoning",
                "skill.programming.iteration_reasoning", "skill.c.declaration_type_model",
                "skill.memory.address_value_distinction", "skill.c.pointer_dereference"
            },
            "remediation_tags": ["targeted_reteach", "fresh_variant"] + (["debug_localization"] if "debugging" in tags else []),
            "professional_capability_tags": [x for x in tags.split(";") if x],
            "project_capability_tags": [],
            "source_refs": ["source.repo.fbb_v0", "source.repo.gns_v0", "source.repo.frdb_v0"],
            "provenance_ref": "source.repo.internal_6c_authoring",
            "freshness_class": "version_sensitive" if kind.startswith("tool_specific") else "evergreen",
            "technology_dependency_refs": [],
            "duplicate_resolution": {
                "status": "reuse_existing" if sid.startswith(("skill.programming.", "skill.computing.", "skill.engineering.", "skill.memory.")) else "no_duplicate_found",
                "compared_skill_ids": [],
                "rationale": "Compared against FBB-v0 seeds and the complete 6C package registry.",
            },
            "granularity_review_codes": ["GRANULARITY_OK"] + (["SHARED_SKILL_REUSE"] if kind in {"shared_reasoning", "professional_workflow"} else []),
            "seed_mapping_refs": [],
            "review_refs": [],
            "_hard": [x for x in hard.split(";") if x],
            "_soft": [x for x in soft.split(";") if x],
        })
    return result


skills = parse_skills()
skill_by_id = {x["skill_id_candidate"]: x for x in skills}

skill_targets = {
    "skill.python.values_variables_expressions": ["skill.python.value_type_behavior", "skill.python.assignment_binding", "skill.python.expression_evaluation"],
    "skill.python.sequence_collections_basic": ["skill.python.list_operations", "skill.python.tuple_immutable_sequence", "skill.python.set_operations"],
    "skill.python.files_paths_basic": ["skill.python.path_handling", "skill.python.files_paths_basic"],
    "skill.c.declarations_types_expressions": ["skill.c.declaration_type_model", "skill.c.expression_evaluation"],
    "skill.c.conditionals_loops_basic": ["skill.c.conditionals", "skill.c.for_iteration", "skill.c.while_iteration"],
    "skill.c.pointer_declaration_dereference_basic": ["skill.c.pointer_formation", "skill.c.pointer_dereference"],
    "skill.shell.command_options_redirection_basic": ["skill.shell.command_options_redirection_basic", "skill.shell.pipeline_redirection"],
    "skill.dsa.sequence_traversal_linear_search": ["skill.dsa.sequence_traversal", "skill.dsa.linear_search"],
}


fbb_text = FBB.read_text(encoding="utf-8")
fbb_skills = sorted(set(re.findall(r"skill\.[a-z0-9_.]+", fbb_text)))
fbb_objectives = sorted(set(re.findall(r"objective\.[a-z0-9_.]+", fbb_text)))
for sid in fbb_skills:
    if sid not in skill_targets:
        skill_targets[sid] = [sid]

objective_owner_overrides = {
    "objective.python.values_variables_expressions.write_state_change": "skill.python.assignment_binding",
    "objective.python.sequence_collections_basic.select_and_use_sequence": "skill.python.list_operations",
    "objective.c.declarations_types_expressions.write_basic_declarations": "skill.c.declaration_type_model",
    "objective.c.conditionals_loops_basic.write_control_flow": "skill.c.conditionals",
    "objective.c.pointer_declaration_dereference_basic.read_pointed_value": "skill.c.pointer_dereference",
    "objective.c.pointer_declaration_dereference_basic.modify_value_via_pointer": "skill.c.pointer_dereference",
    "objective.dsa.sequence_traversal_linear_search.implement_search": "skill.dsa.linear_search",
    "objective.dsa.sequence_traversal_linear_search.explain_termination_and_result": "skill.dsa.sequence_traversal",
}


def old_objective_owner(oid: str) -> str:
    body = oid.removeprefix("objective.")
    return "skill." + body.rsplit(".", 1)[0]


objectives = []
objective_result_by_seed = {}
covered_skills = set()
for old_oid in fbb_objectives:
    old_owner = old_objective_owner(old_oid)
    owner = objective_owner_overrides.get(old_oid, skill_targets[old_owner][0])
    action = old_oid.rsplit(".", 1)[1]
    new_oid = "objective." + owner.removeprefix("skill.") + "." + action
    objective_result_by_seed[old_oid] = new_oid
    covered_skills.add(owner)
    objectives.append({
        "objective_id_candidate": new_oid,
        "owner_skill_id": owner,
        "objective_statement": f"Yeni bir bağlamda {skill_by_id[owner]['capability_statement'].removesuffix('.')}",
        "observable_action": action,
        "success_criteria": "Target behavior bağımsız olarak gözlenir; sonuç doğru ve tekrar doğrulanabilirdir.",
        "lifecycle_status": "draft",
        "authoring_status": "internally_qa_passed",
        "requirement_role": "required",
        "criticality": "standard",
        "evidence_profile": {
            "acceptable_evidence_types": skill_by_id[owner]["independent_evidence_path"]["direct_evidence_types"],
            "direct_evidence_types": skill_by_id[owner]["independent_evidence_path"]["direct_evidence_types"],
            "required_direct_type": skill_by_id[owner]["independent_evidence_path"]["direct_evidence_types"][0],
            "requires_user_authored_artifact": "authored_code" in skill_by_id[owner]["independent_evidence_path"]["direct_evidence_types"],
            "requires_transfer": True,
            "evaluator_requirement": "verified",
            "policy_defaults_ref": "GRE-v0",
        },
        "task_environment_expectation": "Fresh, prerequisite-valid task or artifact.",
        "allowed_tools_policy_ref": "H0 independent evidence",
        "diagnostic_eligibility": skill_by_id[owner]["diagnostic_eligibility"],
        "retention_requirement": {"delayed_revalidation_required": True, "context_diversity_requirement": "fresh_context"},
        "remediation_tags": skill_by_id[owner]["remediation_tags"],
        "source_refs": ["source.repo.fbb_v0", "source.repo.gns_v0"],
        "provenance_ref": "source.repo.internal_6c_authoring",
        "freshness_class": skill_by_id[owner]["freshness_class"],
        "granularity_review_codes": ["GRANULARITY_OK"],
        "review_refs": [],
    })

for skill in skills:
    sid = skill["skill_id_candidate"]
    if sid in covered_skills:
        continue
    oid = "objective." + sid.removeprefix("skill.") + ".demonstrate_capability"
    objectives.append({
        "objective_id_candidate": oid,
        "owner_skill_id": sid,
        "objective_statement": f"Yeni bir bağlamda {skill['capability_statement'].removesuffix('.')}",
        "observable_action": "demonstrate_capability",
        "success_criteria": "Target behavior bağımsız, prerequisite-valid ve doğrulanabilir evidence ile gösterilir.",
        "lifecycle_status": "draft",
        "authoring_status": "internally_qa_passed",
        "requirement_role": "required",
        "criticality": "standard",
        "evidence_profile": {
            "acceptable_evidence_types": skill["independent_evidence_path"]["direct_evidence_types"],
            "direct_evidence_types": skill["independent_evidence_path"]["direct_evidence_types"],
            "required_direct_type": skill["independent_evidence_path"]["direct_evidence_types"][0],
            "requires_user_authored_artifact": "authored_code" in skill["independent_evidence_path"]["direct_evidence_types"],
            "requires_transfer": True,
            "evaluator_requirement": "verified",
            "policy_defaults_ref": "GRE-v0",
        },
        "task_environment_expectation": "Fresh, prerequisite-valid task or artifact.",
        "allowed_tools_policy_ref": "H0 independent evidence",
        "diagnostic_eligibility": skill["diagnostic_eligibility"],
        "retention_requirement": {"delayed_revalidation_required": True, "context_diversity_requirement": "fresh_context"},
        "remediation_tags": skill["remediation_tags"],
        "source_refs": ["source.repo.gns_v0", "source.repo.frdb_v0"],
        "provenance_ref": "source.repo.internal_6c_authoring",
        "freshness_class": skill["freshness_class"],
        "granularity_review_codes": ["GRANULARITY_OK"],
        "review_refs": [],
    })

objectives.sort(key=lambda x: x["objective_id_candidate"])
objective_ids = {x["objective_id_candidate"] for x in objectives}

organization = []
for did, (name, route, role) in domains.items():
    organization.append({
        "entity_type": "domain", "logical_id_candidate": did, "display_name": name,
        "semantic_statement": f"{name} foundations capability organization domain.",
        "parent_organization_id": None, "primary_domain_id": did, "organization_role": role,
        "teaching_intent_tags": ["teach", "practice", "assess"], "linked_route_family_ids": [route],
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
        "source_refs": ["source.repo.pdm_v0"], "provenance_ref": "source.repo.internal_6c_authoring",
        "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"],
        "review_refs": [], "notes": None,
    })
for mid, topics in module_topics.items():
    did = module_domain[mid]
    route = domains[did][1]
    organization.append({
        "entity_type": "module", "logical_id_candidate": mid,
        "display_name": mid.split(".")[-1].replace("_", " ").title(),
        "semantic_statement": "Related foundation Topics için stable organization cluster.",
        "parent_organization_id": did, "primary_domain_id": did, "organization_role": "authoring_cluster",
        "teaching_intent_tags": ["teach", "practice"], "linked_route_family_ids": [route],
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
        "source_refs": ["source.repo.pdm_v0", "source.repo.gns_v0"],
        "provenance_ref": "source.repo.internal_6c_authoring", "freshness_class": "evergreen",
        "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
    })
    for topic in topics:
        organization.append({
            "entity_type": "topic", "logical_id_candidate": topic,
            "display_name": topic.split(".")[-1].replace("_", " ").title(),
            "semantic_statement": "Learner-facing teaching, practice and evidence context.",
            "parent_organization_id": mid, "primary_domain_id": did, "organization_role": "teaching_context",
            "teaching_intent_tags": ["teach", "practice", "assess", "remediate"],
            "linked_route_family_ids": [route], "lifecycle_status": "draft",
            "authoring_status": "internally_qa_passed", "source_refs": ["source.repo.pdm_v0", "source.repo.fbb_v0"],
            "provenance_ref": "source.repo.internal_6c_authoring", "freshness_class": "evergreen",
            "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
        })

topic_links = []
for skill in skills:
    topic_links.append({
        "topic_id": skill["primary_teaching_topic_id"], "skill_id": skill["skill_id_candidate"],
        "role": "teach", "importance": "core", "is_primary_teaching_context": True,
        "objective_scope_ids": [x["objective_id_candidate"] for x in objectives if x["owner_skill_id"] == skill["skill_id_candidate"]],
        "authoring_rationale": "Primary evidence-bearing teaching context.",
        "source_refs": ["source.repo.internal_6c_authoring"], "lifecycle_status": "draft", "review_refs": [],
    })

extra_links = {
    "skill.programming.state_assignment_model": ["topic.c.types_expressions"],
    "skill.programming.expression_boolean_reasoning": ["topic.c.io_control", "topic.dsa.complexity_correctness"],
    "skill.programming.branching_reasoning": ["topic.c.io_control"],
    "skill.programming.iteration_reasoning": ["topic.c.io_control", "topic.dsa.sequences_search"],
    "skill.programming.function_decomposition": ["topic.c.functions_scope", "topic.dsa.recursion_sorting"],
    "skill.programming.test_case_basic": ["topic.c.debugging_safety", "topic.dsa.complexity_correctness"],
    "skill.engineering.reproducible_run_notes": ["topic.c.toolchain", "topic.shell.pipelines_scripts"],
    "skill.engineering.explain_debug_fix_basic": ["topic.c.debugging_safety"],
}
for sid, topics in extra_links.items():
    for topic in topics:
        topic_links.append({
            "topic_id": topic, "skill_id": sid, "role": "reinforce", "importance": "supporting",
            "is_primary_teaching_context": False, "objective_scope_ids": [],
            "authoring_rationale": "Shared canonical Skill is reused without cloning learner state.",
            "source_refs": ["source.repo.gqa_v0"], "lifecycle_status": "draft", "review_refs": [],
        })

prerequisites = []
for skill in skills:
    target = skill["skill_id_candidate"]
    for kind, sources in (("hard", skill.pop("_hard")), ("soft", skill.pop("_soft"))):
        for source in sources:
            prerequisites.append({
                "prerequisite_skill_id": source, "target_skill_id": target, "edge_kind": kind,
                "reason_kind": "evidence_interpretability" if kind == "hard" else "conceptual_dependency",
                "strictness_profile_ref": "default_prg_v0",
                "authoring_rationale": "Source readiness is required for fair target teaching/evidence." if kind == "hard" else "Source improves fluency but does not hard-block target work.",
                "contamination_risk_if_missing": "high" if kind == "hard" else "low",
                "task_specific_instead_of_graph_edge": False,
                "source_refs": ["source.repo.prg_v0", "source.repo.gqa_v0"],
                "provenance_ref": "source.repo.internal_6c_authoring", "lifecycle_status": "draft",
                "review_status": "reviewed", "review_refs": [],
            })

requirements = []
for skill in skills:
    sid = skill["skill_id_candidate"]
    requirements.append({
        "scope_kind": "domain", "scope_id": skill["shared_placement_domain_ids"][0],
        "capability_kind": "skill", "capability_id": sid, "requirement_role": "required",
        "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
        "rationale": "6C foundation route coverage; runtime readiness remains Skill-level and branch-local.",
        "source_refs": ["source.repo.pdm_v0", "source.repo.kgc_v0"], "review_refs": [],
    })

professional = []
for skill in skills:
    for tag in skill["professional_capability_tags"]:
        professional.append({
            "capability_id": skill["skill_id_candidate"], "professional_family_id": "professional." + tag,
            "attribution_role": "supporting", "context_requirements": ["fresh_independent_context"],
            "evidence_expectation_ref": "GRE-v0", "rationale": "Foundation capability supports this professional family.",
            "source_refs": ["source.repo.pdm_v0"], "review_refs": [],
        })

project_skill_ids = [
    "skill.python.cli_argument_parsing", "skill.python.files_paths_basic", "skill.python.unit_testing",
    "skill.git.stage_commit_history_basic", "skill.shell.script_control_flow", "skill.engineering.reproducible_run_notes",
    "skill.engineering.explain_debug_fix_basic", "skill.c.multi_file_build", "skill.c.sanitizer_undefined_behavior",
    "skill.dsa.linear_search",
]
project_attributions = []
for sid in project_skill_ids:
    oid = next(x["objective_id_candidate"] for x in objectives if x["owner_skill_id"] == sid)
    project_attributions.append({
        "project_or_capstone_id": "project.foundation.reproducible_cli_tool", "skill_id": sid,
        "objective_id": oid, "role": "supporting", "structurally_essential": True,
        "separately_observable": True, "expected_evidence_type": "component_artifact",
        "rubric_component_ref": "rubric.foundation.reproducible_cli_tool." + sid.split(".")[-1],
        "source_refs": ["source.repo.frdb_v0"], "review_refs": [],
    })

seed_mappings = []
for old_sid in fbb_skills:
    targets = skill_targets[old_sid]
    disposition = "ratify_as_is" if targets == [old_sid] else "split_required"
    seed_mappings.append({
        "seed_id": old_sid, "seed_kind": "skill", "disposition": disposition,
        "result_entity_refs": targets, "evidence_compatibility": "not_applicable_not_published",
        "rationale": {
            "semantic_boundary": "Retained when evidence/remediation boundary is coherent; split when independent learner states are required.",
            "prerequisite_effect": "Edges are reattached to the narrow target capability.",
            "evidence_effect": "No learner evidence migration because FBB seed was not published.",
            "remediation_effect": "Split targets support narrower remediation.",
            "reuse_effect": "Shared capabilities remain single canonical identities.",
        },
        "mapping_class": "unchanged" if disposition == "ratify_as_is" else "split_into",
        "source_refs": ["source.repo.fbb_v0", "source.repo.gns_v0"], "review_refs": [],
    })
for old_oid in fbb_objectives:
    new_oid = objective_result_by_seed[old_oid]
    seed_mappings.append({
        "seed_id": old_oid, "seed_kind": "objective",
        "disposition": "ratify_as_is" if new_oid == old_oid else "normalize_logical_id",
        "result_entity_refs": [new_oid], "evidence_compatibility": "not_applicable_not_published",
        "rationale": {
            "semantic_boundary": "Objective remains one observable target under its ratified owner Skill.",
            "prerequisite_effect": "Inherited from the ratified owner Skill.",
            "evidence_effect": "No learner evidence migration because FBB seed was not published.",
            "remediation_effect": "Objective remediation follows the ratified owner Skill.",
            "reuse_effect": "Objective identity is not cloned across Topic placements.",
        },
        "mapping_class": "unchanged" if new_oid == old_oid else "seed_identity_normalization",
        "source_refs": ["source.repo.fbb_v0", "source.repo.gns_v0"], "review_refs": [],
    })

reviews = [
    {
        "review_id": "review.6c.english.cefr_alignment", "subject_refs": ["domain.technical_english"],
        "review_codes": ["CONTEXT_ONLY_NO_SPLIT"], "severity": "non_blocking",
        "question": "6C capability identities AŞAMA 7'de hangi CEFR/technical progression metadata'sına bağlanmalı?",
        "decision_inputs_required": ["7A diagnostic design", "7B CEFR alignment"],
        "resolution_owner_step": "7B", "status": "open", "resolution": None,
        "source_refs": ["source.repo.english_rules"], "created_at": "2026-08-26", "resolved_at": None,
    },
    {
        "review_id": "review.6c.external_coverage", "subject_refs": ["domain.python", "domain.c", "domain.linux_git_shell", "domain.data_structures_algorithms"],
        "review_codes": ["NEEDS_GRANULARITY_REVIEW"], "severity": "non_blocking",
        "question": "Bağımsız external sources current coverage veya hidden prerequisite boşluğu gösteriyor mu?",
        "decision_inputs_required": ["independent Research AI", "authoritative current sources"],
        "resolution_owner_step": "6H", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": "2026-08-26", "resolved_at": None,
    },
]

sources = [
    ("source.repo.pdm_v0", "Curriculum Domain Map", "docs/CURRICULUM_DOMAIN_MAP.md", ["route_coverage"]),
    ("source.repo.kgc_v0", "Curriculum Knowledge Graph Contract", "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md", ["graph_contract"]),
    ("source.repo.fbb_v0", "V1 Foundation Backbone", "docs/V1_FOUNDATION_BACKBONE.md", ["foundation_seed"]),
    ("source.repo.gqa_v0", "Foundation Graph Architecture QA", "docs/GRAPH_ARCHITECTURE_QA.md", ["graph_qa"]),
    ("source.repo.gns_v0", "Granularity and Naming Standard", "docs/GRANULARITY_NAMING_STANDARD.md", ["granularity", "logical_identity"]),
    ("source.repo.frdb_v0", "Full-Route Decomposition Blueprint", "docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md", ["package_contract"]),
    ("source.repo.prg_v0", "Prerequisite Readiness Gate", "docs/PREREQUISITE_POLICY_SPEC.md", ["prerequisite_semantics"]),
    ("source.repo.english_rules", "English Foundation Rules", "docs/ENGLISH_FOUNDATION_RULES.md", ["language_safety"]),
    ("source.repo.internal_6c_authoring", "6C internal canonical-contract synthesis", "docs/FOUNDATIONS_DETAILED_MAP.md", ["decomposition_authoring"]),
]
source_rows = [{
    "source_id": sid, "source_kind": "internal_design_analysis" if sid.endswith("authoring") else "canonical_spec",
    "title": title, "ref": ref, "version_or_date": "2026-08-26", "authority_class": "accepted_project_contract",
    "supports": supports, "freshness_class": "evergreen", "checked_at": "2026-08-26", "notes": None,
} for sid, title, ref, supports in sources]


def validate() -> list[dict]:
    checks = []
    def check(name: str, condition: bool, details: str) -> None:
        checks.append({"check": name, "result": "PASS" if condition else "FAIL", "details": details})
        if not condition:
            raise ValueError(f"{name}: {details}")

    skill_ids = [x["skill_id_candidate"] for x in skills]
    topic_ids = {x["logical_id_candidate"] for x in organization if x["entity_type"] == "topic"}
    check("route_family_partition", set(x[1] for x in domains.values()) == {"D01", "D02", "D03", "D04", "D05"}, "D01-D05 exactly")
    check("unique_skill_ids", len(skill_ids) == len(set(skill_ids)), f"{len(skill_ids)} Skills")
    check("unique_objective_ids", len(objectives) == len(objective_ids), f"{len(objectives)} Objectives")
    check("objective_owner_refs", all(x["owner_skill_id"] in skill_by_id for x in objectives), "all owners resolve")
    check("skill_has_objective", set(skill_ids) <= {x["owner_skill_id"] for x in objectives}, "every Skill has Objective")
    check("topic_refs", all(x["primary_teaching_topic_id"] in topic_ids for x in skills), "all primary Topics resolve")
    pairs = {(x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"]) for x in prerequisites}
    check("prerequisite_refs", all(a in skill_by_id and b in skill_by_id for a, b, _ in pairs), f"{len(pairs)} edges resolve")
    check("no_self_edges", all(a != b for a, b, _ in pairs), "no self edges")
    check("no_hard_soft_conflict", not any((a, b, "hard") in pairs and (a, b, "soft") in pairs for a, b, _ in pairs), "no pair conflict")
    graph = defaultdict(list); indegree = {x: 0 for x in skill_ids}
    for a, b, kind in pairs:
        if kind == "hard": graph[a].append(b); indegree[b] += 1
    queue = deque(x for x, degree in indegree.items() if degree == 0); visited = 0
    while queue:
        node = queue.popleft(); visited += 1
        for nxt in graph[node]:
            indegree[nxt] -= 1
            if indegree[nxt] == 0: queue.append(nxt)
    check("hard_graph_dag", visited == len(skill_ids), f"visited {visited}/{len(skill_ids)}")
    check("fbb_skill_mapping", len([x for x in seed_mappings if x["seed_kind"] == "skill"]) == 41, "41/41")
    check("fbb_objective_mapping", len([x for x in seed_mappings if x["seed_kind"] == "objective"]) == 47, "47/47")
    all_entity_refs = set(skill_ids) | objective_ids
    check("seed_result_refs", all(ref in all_entity_refs for row in seed_mappings for ref in row["result_entity_refs"]), "all mapping refs resolve")
    check("english_global_gate", not any(a.startswith("skill.english.") and not b.startswith("skill.english.") and k == "hard" for a, b, k in pairs), "no English hard edge into technical Skills")
    check("blocking_reviews", not any(x["severity"] == "blocking" and x["status"] == "open" for x in reviews), "0 open blocking")
    return checks


checks = validate()
manifest = {
    "package_id": "decomposition.6c_foundations", "package_version": 1, "stage_step": "6C",
    "status": "authoring_complete_internal_qa", "blueprint_version": "FRDB-v0",
    "graph_contract_version": "KGC-v0", "granularity_standard_version": "GNS-v0",
    "base_graph_refs": ["FBB-v0", "GQA-v0"], "route_family_ids": ["D01", "D02", "D03", "D04", "D05"],
    "included_collections": [
        "sources", "organization_entities", "skills", "objectives", "topic_skill_links", "prerequisite_edges",
        "capability_requirements", "professional_attributions", "project_capstone_attributions", "seed_mappings",
        "review_queue", "qa_report"
    ],
    "source_catalog_refs": [x["source_id"] for x in source_rows],
    "coverage_declarations": [
        {"route_family_id": route, "status": "internally_mapped", "external_validation": "pending_6H"}
        for route in ["D01", "D02", "D03", "D04", "D05"]
    ],
    "known_exclusions": [
        "CEFR progression/cadence is owned by AŞAMA 7.",
        "Production lesson/task/resource bodies are owned by AŞAMA 15/20.",
        "External coverage/current-industry validation is owned by 6H.",
        "Physical database schema is owned by 9C."
    ],
    "unresolved_review_count": len([x for x in reviews if x["status"] == "open"]),
    "blocking_review_count": len([x for x in reviews if x["severity"] == "blocking" and x["status"] == "open"]),
    "generated_at": "2026-08-26", "authored_by": "local_main_manager",
    "review_refs": [x["review_id"] for x in reviews], "content_hash": None,
}

OUT.mkdir(parents=True, exist_ok=True)
dump("manifest.yaml", manifest)
dump("sources.yaml", source_rows)
dump("organization_entities.yaml", organization)
dump("skills.yaml", sorted(skills, key=lambda x: x["skill_id_candidate"]))
dump("objectives.yaml", objectives)
dump("topic_skill_links.yaml", sorted(topic_links, key=lambda x: (x["topic_id"], x["skill_id"], x["role"])))
dump("prerequisite_edges.yaml", sorted(prerequisites, key=lambda x: (x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"])))
dump("capability_requirements.yaml", sorted(requirements, key=lambda x: x["capability_id"]))
dump("professional_attributions.yaml", sorted(professional, key=lambda x: (x["capability_id"], x["professional_family_id"])))
dump("project_capstone_attributions.yaml", project_attributions)
dump("seed_mappings.yaml", sorted(seed_mappings, key=lambda x: (x["seed_kind"], x["seed_id"])))
dump("review_queue.yaml", reviews)
dump("qa_report.yaml", {
    "package_id": "decomposition.6c_foundations", "qa_contract": "FRDB-v0 section 27",
    "result": "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "checked_at": "2026-08-26",
    "counts": {
        "domains": len(domains), "modules": len(module_topics),
        "topics": sum(len(x) for x in module_topics.values()), "skills": len(skills),
        "objectives": len(objectives), "topic_skill_links": len(topic_links),
        "prerequisite_edges": len(prerequisites), "seed_skill_mappings": len(fbb_skills),
        "seed_objective_mappings": len(fbb_objectives), "open_non_blocking_reviews": len(reviews),
        "open_blocking_reviews": 0,
    },
    "checks": checks,
    "external_research_qa": {"status": "pending", "owner_step": "6H", "required_before_external_validation": True},
})

print(f"Generated {OUT}")
print(f"domains={len(domains)} modules={len(module_topics)} topics={sum(len(x) for x in module_topics.values())}")
print(f"skills={len(skills)} objectives={len(objectives)} prerequisites={len(prerequisites)}")
print(f"fbb_skill_mappings={len(fbb_skills)} fbb_objective_mappings={len(fbb_objectives)}")
print("qa=PASS_WITH_OPEN_NON_BLOCKING_REVIEWS")
