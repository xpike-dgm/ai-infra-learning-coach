from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import yaml


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "curriculum" / "decomposition" / "6d_systems"
BASE = ROOT / "curriculum" / "decomposition" / "6c_foundations"


def dump(name: str, value: object) -> None:
    path = OUT / name
    path.write_text(
        yaml.safe_dump(value, allow_unicode=True, sort_keys=False, width=110),
        encoding="utf-8",
    )


def load_base(name: str):
    return yaml.safe_load((BASE / name).read_text(encoding="utf-8"))


base_skills = {x["skill_id_candidate"] for x in load_base("skills.yaml")}
base_edges = [
    (x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"])
    for x in load_base("prerequisite_edges.yaml")
]
base_topics = {x["logical_id_candidate"] for x in load_base("organization_entities.yaml") if x["entity_type"] == "topic"}


domains = {
    "domain.modern_cpp": ("Modern C++", "D06", "systems_core"),
    "domain.computer_architecture": ("Computer Architecture", "D07", "systems_core"),
    "domain.operating_systems_memory": ("Operating Systems + Memory", "D08", "systems_core"),
    "domain.concurrency": ("Concurrency / Parallel Programming", "D09", "systems_core"),
    "domain.networking": ("Networking", "D10", "systems_core"),
    "domain.distributed_storage": ("Distributed Systems + Storage / Databases", "D11", "distributed_platform_core"),
    "domain.containers_cloud_observability": ("Containers / Cloud / Observability", "D12", "distributed_platform_core"),
    "domain.performance_engineering": ("Performance Engineering & Profiling", "D13", "performance_core"),
}

module_topics = {
    "module.cpp.language_object_model": [
        "topic.cpp.toolchain_translation", "topic.cpp.types_values_initialization",
        "topic.cpp.object_lifetime_ownership",
    ],
    "module.cpp.abstraction_generics": [
        "topic.cpp.classes_polymorphism", "topic.cpp.templates_generic_programming",
        "topic.cpp.standard_library_containers_algorithms",
    ],
    "module.cpp.engineering_practice": [
        "topic.cpp.error_handling_contracts", "topic.cpp.build_dependency_management",
        "topic.cpp.testing_debugging_sanitizers",
    ],
    "module.arch.execution_model": [
        "topic.arch.isa_execution", "topic.arch.pipeline_branch_speculation", "topic.arch.data_parallel_simd",
    ],
    "module.arch.memory_system": [
        "topic.arch.memory_hierarchy_caches", "topic.arch.locality_access_patterns",
        "topic.arch.latency_throughput_model",
    ],
    "module.os.process_execution": [
        "topic.os.process_thread_model", "topic.os.syscall_userspace_kernel", "topic.os.scheduling_behavior",
    ],
    "module.os.memory_management": [
        "topic.os.virtual_memory_paging", "topic.os.memory_mapping_allocators",
    ],
    "module.os.io_and_observation": [
        "topic.os.file_descriptor_io_model", "topic.os.signals_process_lifecycle",
        "topic.os.kernel_observation_tracing",
    ],
    "module.concurrency.correctness": [
        "topic.concurrency.thread_task_model", "topic.concurrency.synchronization_primitives",
        "topic.concurrency.race_deadlock_diagnosis", "topic.concurrency.atomics_memory_ordering",
    ],
    "module.concurrency.parallel_engineering": [
        "topic.concurrency.work_decomposition_pools", "topic.concurrency.async_io_event_loops",
        "topic.concurrency.parallel_performance_scaling",
    ],
    "module.network.protocol_foundations": [
        "topic.network.layering_addressing", "topic.network.transport_tcp_udp",
        "topic.network.dns_naming_resolution",
    ],
    "module.network.application_protocols": [
        "topic.network.http_semantics", "topic.network.tls_secure_transport", "topic.network.serialization_rpc",
    ],
    "module.network.systems_networking": [
        "topic.network.socket_programming", "topic.network.io_multiplexing_nonblocking",
        "topic.network.latency_bandwidth_failure_reasoning",
    ],
    "module.distributed.core_model": [
        "topic.distributed.failure_time_model", "topic.distributed.replication_partitioning",
        "topic.distributed.consistency_models", "topic.distributed.consensus_leader_election",
    ],
    "module.storage.engine_foundations": [
        "topic.storage.durability_wal_recovery", "topic.storage.transactions_isolation",
        "topic.storage.index_storage_structures", "topic.storage.query_access_cost",
    ],
    "module.distributed.data_movement": [
        "topic.distributed.queues_streaming", "topic.distributed.idempotency_retry_delivery",
    ],
    "module.platform.containerization": [
        "topic.platform.namespaces_cgroups_isolation", "topic.platform.container_image_build",
        "topic.platform.container_runtime_networking_storage",
    ],
    "module.platform.orchestration_cloud": [
        "topic.platform.orchestration_workloads", "topic.platform.configuration_secrets_deployment",
        "topic.platform.cloud_resource_model",
    ],
    "module.platform.observability_reliability": [
        "topic.platform.logs_metrics_traces", "topic.platform.slo_reliability_incident",
    ],
    "module.performance.measurement": [
        "topic.performance.measurement_methodology", "topic.performance.benchmark_design",
        "topic.performance.statistics_variance_reasoning",
    ],
    "module.performance.analysis_optimization": [
        "topic.performance.cpu_profiling", "topic.performance.memory_profiling",
        "topic.performance.bottleneck_attribution", "topic.performance.capacity_cost_tradeoff",
    ],
}

module_domain = {
    "module.cpp.language_object_model": "domain.modern_cpp",
    "module.cpp.abstraction_generics": "domain.modern_cpp",
    "module.cpp.engineering_practice": "domain.modern_cpp",
    "module.arch.execution_model": "domain.computer_architecture",
    "module.arch.memory_system": "domain.computer_architecture",
    "module.os.process_execution": "domain.operating_systems_memory",
    "module.os.memory_management": "domain.operating_systems_memory",
    "module.os.io_and_observation": "domain.operating_systems_memory",
    "module.concurrency.correctness": "domain.concurrency",
    "module.concurrency.parallel_engineering": "domain.concurrency",
    "module.network.protocol_foundations": "domain.networking",
    "module.network.application_protocols": "domain.networking",
    "module.network.systems_networking": "domain.networking",
    "module.distributed.core_model": "domain.distributed_storage",
    "module.storage.engine_foundations": "domain.distributed_storage",
    "module.distributed.data_movement": "domain.distributed_storage",
    "module.platform.containerization": "domain.containers_cloud_observability",
    "module.platform.orchestration_cloud": "domain.containers_cloud_observability",
    "module.platform.observability_reliability": "domain.containers_cloud_observability",
    "module.performance.measurement": "domain.performance_engineering",
    "module.performance.analysis_optimization": "domain.performance_engineering",
}


# id | primary topic | capability statement | capability kind | retention | hard prereqs | soft prereqs | professional tags
SKILL_ROWS = """
skill.cpp.build_translation_model|topic.cpp.toolchain_translation|C++ preprocess, compile, link ve run aşamalarını araç çıktısıyla ayırt edebilmek.|tool_specific_production|standard|skill.c.compile_link_run_basic||build_tooling
skill.cpp.standard_version_toolchain_flags|topic.cpp.toolchain_translation|Language standard, warning ve optimization flag'lerinin derleme sonucuna etkisini kontrol edebilmek.|tool_specific_production|standard|skill.cpp.build_translation_model||build_tooling;reproducibility
skill.cpp.header_module_interface|topic.cpp.toolchain_translation|Interface ve implementation sınırını ODR-güvenli biçimde kurabilmek.|language_specific_production|complex|skill.cpp.build_translation_model;skill.c.header_translation_unit_interface||build_tooling;systems_design
skill.cpp.value_categories|topic.cpp.types_values_initialization|Expression value category davranışını örnek üzerinde ayırt edebilmek.|language_specific_reasoning|complex|skill.cpp.build_translation_model;skill.c.expression_evaluation||debugging
skill.cpp.initialization_semantics|topic.cpp.types_values_initialization|Initialization formunun hangi constructor veya conversion'ı seçtiğini izleyebilmek.|language_specific_production|complex|skill.cpp.value_categories||debugging
skill.cpp.const_correctness_qualifiers|topic.cpp.types_values_initialization|const ve compile-time qualifier'ları arayüz anlamına uygun kullanabilmek.|language_specific_production|standard|skill.cpp.initialization_semantics||systems_design;reliability
skill.cpp.reference_binding|topic.cpp.types_values_initialization|Reference binding kurallarını ve dangling reference riskini ayırt edebilmek.|language_specific_reasoning|complex|skill.cpp.value_categories;skill.memory.address_value_distinction||operational_safety;debugging
skill.cpp.object_lifetime_scope|topic.cpp.object_lifetime_ownership|C++ object lifetime, scope ve destruction sırasını izleyebilmek.|language_specific_reasoning|complex|skill.cpp.initialization_semantics;skill.memory.storage_lifetime_intuition||debugging;operational_safety
skill.cpp.raii_resource_ownership|topic.cpp.object_lifetime_ownership|RAII ile resource ownership ve deterministic release kurabilmek.|language_specific_production|complex|skill.cpp.object_lifetime_scope||reliability;systems_design
skill.cpp.smart_pointer_ownership|topic.cpp.object_lifetime_ownership|Unique ve shared ownership semantiğini uygun sahiplik modeline eşleyebilmek.|language_specific_production|complex|skill.cpp.raii_resource_ownership||operational_safety;systems_design
skill.cpp.move_semantics|topic.cpp.object_lifetime_ownership|Move ve copy davranışını moved-from state farkındalığıyla kullanabilmek.|language_specific_production|complex|skill.cpp.value_categories;skill.cpp.raii_resource_ownership||performance_reasoning;debugging
skill.cpp.special_member_rules|topic.cpp.object_lifetime_ownership|Special member function üretilme ve silinme kurallarını sınıf tasarımında uygulayabilmek.|language_specific_reasoning|complex|skill.cpp.move_semantics||systems_design
skill.cpp.class_design_invariants|topic.cpp.classes_polymorphism|Class invariant, encapsulation ve interface sınırını kurabilmek.|language_specific_production|complex|skill.cpp.const_correctness_qualifiers;skill.programming.function_decomposition||systems_design
skill.cpp.virtual_dispatch_polymorphism|topic.cpp.classes_polymorphism|Virtual dispatch, override ve base/derived lifetime kurallarını doğru kullanabilmek.|language_specific_production|complex|skill.cpp.class_design_invariants;skill.cpp.object_lifetime_scope||systems_design;debugging
skill.cpp.interface_composition_tradeoff|topic.cpp.classes_polymorphism|Inheritance ve composition arasında gerekçeli tasarım seçimi yapabilmek.|design_reasoning|complex|skill.cpp.virtual_dispatch_polymorphism||systems_design;technical_communication
skill.cpp.function_class_templates|topic.cpp.templates_generic_programming|Template ile tip-generik davranış yazıp instantiate edebilmek.|language_specific_production|complex|skill.cpp.class_design_invariants||systems_design
skill.cpp.template_constraints_overload|topic.cpp.templates_generic_programming|Overload resolution ve constraint ile doğru template seçimini sağlayabilmek.|language_specific_reasoning|complex|skill.cpp.function_class_templates||debugging
skill.cpp.template_diagnostic_reading|topic.cpp.templates_generic_programming|Template instantiation hata çıktısını gerçek kaynağa lokalize edebilmek.|tool_specific_reasoning|complex|skill.cpp.function_class_templates;skill.c.compiler_diagnostic_reading||debugging;build_tooling
skill.cpp.container_selection|topic.cpp.standard_library_containers_algorithms|Standard container'ı complexity ve access pattern gerekçesiyle seçebilmek.|language_specific_reasoning|complex|skill.cpp.function_class_templates;skill.dsa.asymptotic_bound_reasoning||performance_reasoning;systems_design
skill.cpp.iterator_range_algorithms|topic.cpp.standard_library_containers_algorithms|Iterator ve algorithm kullanımını invalidation kurallarıyla birlikte yapabilmek.|language_specific_production|complex|skill.cpp.container_selection;skill.dsa.sequence_traversal||debugging
skill.cpp.non_owning_view_borrowing|topic.cpp.standard_library_containers_algorithms|Non-owning view türlerini lifetime güvenliğiyle kullanabilmek.|language_specific_production|complex|skill.cpp.reference_binding;skill.cpp.iterator_range_algorithms||operational_safety
skill.cpp.exception_safety_guarantees|topic.cpp.error_handling_contracts|Exception safety garanti seviyelerini kod üzerinde uygulayabilmek.|language_specific_production|complex|skill.cpp.raii_resource_ownership;skill.programming.trace_execution_basic||reliability
skill.cpp.error_return_vs_exception|topic.cpp.error_handling_contracts|Error return değeri ile exception arasında arayüze uygun seçim yapabilmek.|design_reasoning|complex|skill.cpp.exception_safety_guarantees;skill.c.file_io_error_handling||reliability;systems_design
skill.cpp.contract_precondition_validation|topic.cpp.error_handling_contracts|Precondition, postcondition ve invariant kontrolünü açık biçimde kurabilmek.|language_specific_production|standard|skill.cpp.class_design_invariants;skill.programming.input_validation_reasoning||reliability;testing
skill.cpp.build_system_project_definition|topic.cpp.build_dependency_management|Build system ile target, dependency ve flag'leri tekrar üretilebilir tanımlayabilmek.|tool_specific_production|complex|skill.cpp.standard_version_toolchain_flags;skill.c.multi_file_build||build_tooling;reproducibility
skill.cpp.dependency_integration|topic.cpp.build_dependency_management|Third-party dependency'yi versiyonlanmış ve tekrar üretilebilir biçimde entegre edebilmek.|tool_specific_production|complex|skill.cpp.build_system_project_definition||build_tooling;reproducibility
skill.cpp.compilation_time_hygiene|topic.cpp.build_dependency_management|Build süresi ve include bağımlılık grafiğini ölçüp iyileştirebilmek.|measurement_production|complex|skill.cpp.build_system_project_definition;skill.cpp.header_module_interface||build_tooling;profiling_benchmarking
skill.cpp.unit_test_harness|topic.cpp.testing_debugging_sanitizers|C++ test harness ile bağımsız ve deterministic test yazabilmek.|tool_specific_production|complex|skill.cpp.build_system_project_definition;skill.programming.test_case_basic||testing
skill.cpp.debugger_inspection|topic.cpp.testing_debugging_sanitizers|Debugger ile C++ stack, object state ve control flow'unu inceleyebilmek.|tool_specific_production|complex|skill.cpp.build_translation_model;skill.c.debugger_trace_basic||debugging
skill.cpp.sanitizer_ub_diagnosis|topic.cpp.testing_debugging_sanitizers|Sanitizer bulgusunu C++ lifetime veya undefined-behavior kaynağına lokalize edebilmek.|tool_specific_production|complex|skill.cpp.object_lifetime_scope;skill.c.sanitizer_undefined_behavior||debugging;operational_safety
skill.arch.instruction_execution_model|topic.arch.isa_execution|Instruction fetch, decode ve execute döngüsünü register/memory rolüyle açıklayabilmek.|systems_reasoning|standard|skill.computing.program_execution_model||systems_design
skill.arch.isa_abstraction_boundary|topic.arch.isa_execution|ISA, microarchitecture ve compiler çıktısı arasındaki sınırı ayırt edebilmek.|systems_reasoning|complex|skill.arch.instruction_execution_model;skill.c.compile_link_run_basic||source_reading
skill.arch.assembly_reading_basic|topic.arch.isa_execution|Küçük fonksiyonun compiler-üretilmiş assembly çıktısını kaynak koda eşleyebilmek.|tool_specific_reasoning|complex|skill.arch.isa_abstraction_boundary;skill.programming.code_reading_basic||source_reading;debugging
skill.arch.number_representation_precision|topic.arch.isa_execution|Integer ve floating-point temsil sınırlarını sonuç üzerinde gerekçelendirebilmek.|systems_reasoning|complex|skill.c.bitwise_mask_reasoning||reliability
skill.arch.pipeline_hazard_reasoning|topic.arch.pipeline_branch_speculation|Pipeline stage, hazard ve stall etkisini örnek üzerinde açıklayabilmek.|systems_reasoning|complex|skill.arch.instruction_execution_model||performance_reasoning
skill.arch.branch_prediction_cost|topic.arch.pipeline_branch_speculation|Branch predictability'nin ölçülebilir performans etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.arch.pipeline_hazard_reasoning||performance_reasoning
skill.arch.instruction_level_parallelism|topic.arch.pipeline_branch_speculation|ILP, dependency chain ve throughput ilişkisini analiz edebilmek.|systems_reasoning|complex|skill.arch.pipeline_hazard_reasoning||performance_reasoning
skill.arch.simd_data_parallel_model|topic.arch.data_parallel_simd|SIMD lane, vector width ve data-parallel uygunluk sınırını açıklayabilmek.|systems_reasoning|complex|skill.arch.instruction_level_parallelism||performance_reasoning
skill.arch.vectorization_enablement|topic.arch.data_parallel_simd|Vectorization'ı engelleyen kod faktörlerini tespit edip düzeltebilmek.|language_specific_production|complex|skill.arch.simd_data_parallel_model;skill.cpp.iterator_range_algorithms||performance_reasoning;debugging
skill.arch.cache_hierarchy_model|topic.arch.memory_hierarchy_caches|Cache seviyeleri, line, hit/miss ve maliyet ilişkisini açıklayabilmek.|systems_reasoning|complex|skill.arch.instruction_execution_model;skill.memory.address_value_distinction||performance_reasoning
skill.arch.cache_miss_classification|topic.arch.memory_hierarchy_caches|Cache miss türlerini gözlemlenen davranışla ilişkilendirebilmek.|systems_reasoning|complex|skill.arch.cache_hierarchy_model||performance_reasoning;profiling_benchmarking
skill.arch.memory_alignment_layout|topic.arch.memory_hierarchy_caches|Data layout, alignment ve padding'in erişim maliyetine etkisini kurabilmek.|systems_reasoning|complex|skill.arch.cache_hierarchy_model;skill.c.struct_enum_model||performance_reasoning;systems_design
skill.arch.locality_reasoning|topic.arch.locality_access_patterns|Temporal ve spatial locality'yi erişim deseni üzerinden gerekçelendirebilmek.|systems_reasoning|complex|skill.arch.cache_hierarchy_model||performance_reasoning
skill.arch.data_layout_transformation|topic.arch.locality_access_patterns|Access pattern'a göre data layout dönüşümünü ölçülebilir gerekçeyle uygulayabilmek.|language_specific_production|complex|skill.arch.locality_reasoning;skill.arch.memory_alignment_layout||performance_reasoning
skill.arch.false_sharing_awareness|topic.arch.locality_access_patterns|Paylaşılan cache line kaynaklı performans kaybını tanıyabilmek.|systems_reasoning|complex|skill.arch.cache_hierarchy_model||performance_reasoning;concurrency
skill.arch.latency_throughput_distinction|topic.arch.latency_throughput_model|Latency ve throughput hedeflerini ayrı metrik olarak modelleyebilmek.|systems_reasoning|standard|skill.arch.instruction_execution_model||performance_reasoning
skill.arch.roofline_bottleneck_reasoning|topic.arch.latency_throughput_model|Compute-bound ve memory-bound sınırı ölçüm üzerinden ayırt edebilmek.|measurement_reasoning|complex|skill.arch.latency_throughput_distinction;skill.arch.cache_hierarchy_model||performance_reasoning;profiling_benchmarking
skill.arch.hardware_counter_interpretation|topic.arch.latency_throughput_model|Hardware performance counter çıktısını mimari davranışa bağlayabilmek.|tool_specific_reasoning|complex|skill.arch.cache_miss_classification||profiling_benchmarking
skill.os.process_address_space_model|topic.os.process_thread_model|Process, address space ve isolation sınırını açıklayabilmek.|systems_reasoning|complex|skill.linux.process_exit_stdout_stderr_basic;skill.memory.address_value_distinction||systems_design
skill.os.thread_vs_process_model|topic.os.process_thread_model|Thread ve process arasındaki paylaşım ile izolasyon farkını gerekçelendirebilmek.|systems_reasoning|complex|skill.os.process_address_space_model||systems_design;concurrency
skill.os.process_creation_lifecycle|topic.os.process_thread_model|Process oluşturma, exec, wait ve exit lifecycle'ını gözlemleyerek kurabilmek.|systems_production|complex|skill.os.process_address_space_model;skill.linux.process_inspection||reliability
skill.os.syscall_boundary_model|topic.os.syscall_userspace_kernel|User space ile kernel arasındaki syscall sınırını ve maliyetini açıklayabilmek.|systems_reasoning|complex|skill.os.process_address_space_model||systems_design;performance_reasoning
skill.os.syscall_error_semantics|topic.os.syscall_userspace_kernel|Syscall dönüş değeri ve hata semantiğini doğru ele alabilmek.|systems_production|complex|skill.os.syscall_boundary_model;skill.c.file_io_error_handling||reliability;debugging
skill.os.syscall_tracing|topic.os.syscall_userspace_kernel|Syscall tracing aracıyla programın kernel etkileşimini gözlemleyebilmek.|tool_specific_production|complex|skill.os.syscall_boundary_model;skill.linux.process_inspection||debugging;observability
skill.os.scheduler_model|topic.os.scheduling_behavior|Scheduler, run queue, preemption ve priority davranışını açıklayabilmek.|systems_reasoning|complex|skill.os.thread_vs_process_model||performance_reasoning
skill.os.context_switch_cost|topic.os.scheduling_behavior|Context switch ve CPU affinity etkisini ölçüm üzerinden gerekçelendirebilmek.|measurement_reasoning|complex|skill.os.scheduler_model;skill.arch.cache_hierarchy_model||performance_reasoning;profiling_benchmarking
skill.os.cpu_time_accounting|topic.os.scheduling_behavior|User, system ve bekleme süresi ayrımını gözlemle doğru yorumlayabilmek.|tool_specific_reasoning|standard|skill.os.scheduler_model;skill.linux.process_inspection||profiling_benchmarking;observability
skill.os.virtual_physical_translation|topic.os.virtual_memory_paging|Virtual address, page table ve translation davranışını açıklayabilmek.|systems_reasoning|complex|skill.os.process_address_space_model||systems_design
skill.os.page_fault_behavior|topic.os.virtual_memory_paging|Page fault türlerini ve maliyetini gözlemle ilişkilendirebilmek.|systems_reasoning|complex|skill.os.virtual_physical_translation||performance_reasoning;debugging
skill.os.tlb_page_size_effects|topic.os.virtual_memory_paging|TLB davranışı ve page size seçiminin erişim maliyetine etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.os.virtual_physical_translation;skill.arch.cache_hierarchy_model||performance_reasoning
skill.os.memory_mapping_usage|topic.os.memory_mapping_allocators|Memory-mapped bölgeyi doğru amaç ve lifetime ile kullanabilmek.|systems_production|complex|skill.os.virtual_physical_translation;skill.c.dynamic_allocation_lifecycle||reliability
skill.os.allocator_behavior_model|topic.os.memory_mapping_allocators|Allocator davranışını, fragmentation'ı ve arena/pool ayrımını açıklayabilmek.|systems_reasoning|complex|skill.c.dynamic_allocation_lifecycle;skill.os.virtual_physical_translation||performance_reasoning
skill.os.memory_usage_diagnosis|topic.os.memory_mapping_allocators|Process memory kullanımını ölçüp büyüme veya sızıntı kaynağını lokalize edebilmek.|tool_specific_production|complex|skill.os.allocator_behavior_model;skill.cpp.sanitizer_ub_diagnosis||debugging;profiling_benchmarking
skill.os.file_descriptor_model|topic.os.file_descriptor_io_model|File descriptor, open file description ve inheritance davranışını ayırt edebilmek.|systems_reasoning|complex|skill.linux.process_exit_stdout_stderr_basic;skill.os.process_address_space_model||systems_design
skill.os.blocking_io_semantics|topic.os.file_descriptor_io_model|Blocking I/O, partial read/write ve EOF semantiğini doğru kurabilmek.|systems_production|complex|skill.os.file_descriptor_model;skill.os.syscall_error_semantics||reliability;debugging
skill.os.buffering_flush_semantics|topic.os.file_descriptor_io_model|Userspace buffering, flush ve durability sınırını ayırt edebilmek.|systems_reasoning|complex|skill.os.blocking_io_semantics||reliability
skill.os.signal_handling_semantics|topic.os.signals_process_lifecycle|Signal delivery, handler kısıtları ve kesilen syscall davranışını doğru kurabilmek.|systems_production|complex|skill.os.syscall_error_semantics;skill.linux.process_signal_job_control||reliability;operational_safety
skill.os.graceful_shutdown_behavior|topic.os.signals_process_lifecycle|Graceful shutdown ve resource cleanup akışını gözlemlenebilir biçimde kurabilmek.|systems_production|complex|skill.os.signal_handling_semantics;skill.os.process_creation_lifecycle||reliability
skill.os.resource_limit_reasoning|topic.os.kernel_observation_tracing|Resource limit ve quota'nın process davranışına etkisini yorumlayabilmek.|systems_reasoning|standard|skill.os.process_creation_lifecycle||operational_safety;reliability
skill.os.kernel_event_observation|topic.os.kernel_observation_tracing|Kernel seviyesindeki event ve trace verisiyle sistem davranışını gözlemleyebilmek.|tool_specific_production|complex|skill.os.syscall_tracing;skill.os.scheduler_model||observability;profiling_benchmarking
skill.concurrency.thread_lifecycle_management|topic.concurrency.thread_task_model|Thread oluşturma, join/detach ve lifetime güvenliğini kurabilmek.|systems_production|complex|skill.os.thread_vs_process_model;skill.cpp.raii_resource_ownership||reliability;concurrency
skill.concurrency.shared_mutable_state_risk|topic.concurrency.thread_task_model|Paylaşılan mutable state'in neden yarış ürettiğini örnekle açıklayabilmek.|shared_reasoning|complex|skill.os.thread_vs_process_model;skill.programming.state_assignment_model||concurrency;debugging
skill.concurrency.task_vs_thread_abstraction|topic.concurrency.thread_task_model|Task tabanlı soyutlama ile açık thread yönetimini gerekçeyle ayırabilmek.|design_reasoning|complex|skill.concurrency.thread_lifecycle_management||systems_design;concurrency
skill.concurrency.mutex_critical_section|topic.concurrency.synchronization_primitives|Mutex ile kritik bölge sınırını doğru ve dar kurabilmek.|systems_production|complex|skill.concurrency.shared_mutable_state_risk;skill.cpp.raii_resource_ownership||concurrency;reliability
skill.concurrency.condition_variable_coordination|topic.concurrency.synchronization_primitives|Condition variable ile bekleme ve uyandırma koordinasyonunu güvenli kurabilmek.|systems_production|complex|skill.concurrency.mutex_critical_section||concurrency;reliability
skill.concurrency.lock_granularity_tradeoff|topic.concurrency.synchronization_primitives|Lock granularity ve contention dengesini ölçülebilir gerekçeyle seçebilmek.|design_reasoning|complex|skill.concurrency.mutex_critical_section;skill.arch.false_sharing_awareness||performance_reasoning;concurrency
skill.concurrency.data_race_diagnosis|topic.concurrency.race_deadlock_diagnosis|Data race'i tespit edip kaynağını lokalize edebilmek.|tool_specific_production|complex|skill.concurrency.mutex_critical_section;skill.cpp.sanitizer_ub_diagnosis||debugging;concurrency
skill.concurrency.deadlock_analysis|topic.concurrency.race_deadlock_diagnosis|Deadlock koşullarını analiz edip lock ordering ile önleyebilmek.|systems_reasoning|complex|skill.concurrency.mutex_critical_section||debugging;reliability
skill.concurrency.nondeterministic_reproduction|topic.concurrency.race_deadlock_diagnosis|Nondeterministik concurrency hatasını tekrar üretilebilir hale getirebilmek.|professional_workflow|complex|skill.concurrency.data_race_diagnosis;skill.engineering.reproducible_run_notes||debugging;reproducibility
skill.concurrency.atomic_operations|topic.concurrency.atomics_memory_ordering|Atomic okuma, yazma ve read-modify-write davranışını doğru kullanabilmek.|systems_production|complex|skill.concurrency.shared_mutable_state_risk;skill.cpp.const_correctness_qualifiers||concurrency;reliability
skill.concurrency.memory_ordering_model|topic.concurrency.atomics_memory_ordering|Memory ordering garantilerini ve reordering riskini gerekçelendirebilmek.|systems_reasoning|complex|skill.concurrency.atomic_operations;skill.arch.instruction_level_parallelism||concurrency;operational_safety
skill.concurrency.lockfree_tradeoff_reasoning|topic.concurrency.atomics_memory_ordering|Lock-free yaklaşımın doğruluk ve karmaşıklık maliyetini gerekçelendirebilmek.|design_reasoning|complex|skill.concurrency.memory_ordering_model||systems_design;performance_reasoning
skill.concurrency.work_decomposition|topic.concurrency.work_decomposition_pools|Problemi bağımsız çalışabilir iş birimlerine bölebilmek.|shared_reasoning|complex|skill.programming.function_decomposition;skill.concurrency.task_vs_thread_abstraction||concurrency;systems_design
skill.concurrency.thread_pool_usage|topic.concurrency.work_decomposition_pools|Thread pool ile iş kuyruğunu backpressure farkındalığıyla kullanabilmek.|systems_production|complex|skill.concurrency.work_decomposition;skill.concurrency.condition_variable_coordination||concurrency;reliability
skill.concurrency.parallel_result_aggregation|topic.concurrency.work_decomposition_pools|Parallel sonuçları deterministic biçimde birleştirebilmek.|systems_production|complex|skill.concurrency.thread_pool_usage;skill.concurrency.atomic_operations||concurrency;reliability
skill.concurrency.async_execution_model|topic.concurrency.async_io_event_loops|Async ve event-driven execution modelini blocking modelden ayırabilmek.|systems_reasoning|complex|skill.concurrency.task_vs_thread_abstraction;skill.python.async_task_coordination||concurrency;systems_design
skill.concurrency.event_loop_behavior|topic.concurrency.async_io_event_loops|Event loop üzerinde blocking iş ve starvation riskini tanıyabilmek.|systems_reasoning|complex|skill.concurrency.async_execution_model||reliability;performance_reasoning
skill.concurrency.cancellation_timeout_semantics|topic.concurrency.async_io_event_loops|Cancellation ve timeout davranışını kaynak sızdırmadan kurabilmek.|systems_production|complex|skill.concurrency.async_execution_model;skill.cpp.raii_resource_ownership||reliability
skill.concurrency.scaling_limit_reasoning|topic.concurrency.parallel_performance_scaling|Serial fraction ve contention nedeniyle oluşan scaling limitini gerekçelendirebilmek.|measurement_reasoning|complex|skill.concurrency.lock_granularity_tradeoff;skill.arch.latency_throughput_distinction||performance_reasoning
skill.concurrency.parallel_speedup_measurement|topic.concurrency.parallel_performance_scaling|Parallel speedup ve efficiency'yi tekrar üretilebilir ölçümle raporlayabilmek.|measurement_production|complex|skill.concurrency.scaling_limit_reasoning;skill.engineering.reproducible_run_notes||profiling_benchmarking;reproducibility
skill.network.layering_model|topic.network.layering_addressing|Network layering ve encapsulation davranışını açıklayabilmek.|systems_reasoning|standard||skill.os.file_descriptor_model|systems_design
skill.network.addressing_routing_basics|topic.network.layering_addressing|IP addressing, subnet ve routing kararını temel örnekte izleyebilmek.|systems_reasoning|complex|skill.network.layering_model||systems_design
skill.network.packet_capture_reading|topic.network.layering_addressing|Packet capture çıktısını protokol davranışına eşleyebilmek.|tool_specific_production|complex|skill.network.layering_model;skill.linux.process_inspection||debugging;observability
skill.network.tcp_connection_lifecycle|topic.network.transport_tcp_udp|TCP bağlantı kurulum, aktarım ve kapanış durumlarını izleyebilmek.|systems_reasoning|complex|skill.network.layering_model||systems_design;debugging
skill.network.tcp_reliability_flow_control|topic.network.transport_tcp_udp|Retransmission, flow control ve congestion davranışının etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.network.tcp_connection_lifecycle||performance_reasoning
skill.network.udp_datagram_tradeoff|topic.network.transport_tcp_udp|UDP datagram semantiğini ve uygunluk sınırını gerekçelendirebilmek.|systems_reasoning|standard|skill.network.layering_model||systems_design
skill.network.dns_resolution_behavior|topic.network.dns_naming_resolution|Name resolution zincirini ve cache/TTL etkisini gözlemleyebilmek.|systems_reasoning|standard|skill.network.addressing_routing_basics||debugging;reliability
skill.network.service_discovery_naming|topic.network.dns_naming_resolution|Service naming ve endpoint çözümlemesinin failure modlarını ayırt edebilmek.|systems_reasoning|complex|skill.network.dns_resolution_behavior||reliability;distributed_failure_reasoning
skill.network.http_request_response_model|topic.network.http_semantics|HTTP request/response semantiğini ve status davranışını doğru kullanabilmek.|systems_production|standard|skill.network.tcp_connection_lifecycle;skill.python.network_client_basic||systems_design
skill.network.http_connection_performance|topic.network.http_semantics|Keep-alive, multiplexing ve header maliyetinin performans etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.network.http_request_response_model;skill.network.tcp_reliability_flow_control||performance_reasoning
skill.network.http_api_error_contract|topic.network.http_semantics|HTTP API hata sözleşmesini client davranışıyla tutarlı kurabilmek.|design_reasoning|complex|skill.network.http_request_response_model;skill.programming.input_validation_reasoning||reliability;systems_design
skill.network.tls_handshake_trust_model|topic.network.tls_secure_transport|TLS handshake ve sertifika güven zincirini açıklayabilmek.|systems_reasoning|complex|skill.network.tcp_connection_lifecycle||operational_safety
skill.network.tls_failure_diagnosis|topic.network.tls_secure_transport|TLS ve sertifika hatasını doğru kaynağa lokalize edebilmek.|tool_specific_production|complex|skill.network.tls_handshake_trust_model;skill.network.packet_capture_reading||debugging;operational_safety
skill.network.serialization_format_tradeoff|topic.network.serialization_rpc|Serialization format seçimini schema, versiyon ve performans gerekçesiyle yapabilmek.|design_reasoning|complex|skill.python.bytes_text_boundary;skill.network.http_request_response_model||systems_design;performance_reasoning
skill.network.schema_evolution_compatibility|topic.network.serialization_rpc|Schema değişiminde geriye ve ileriye uyumluluk kurallarını uygulayabilmek.|design_reasoning|complex|skill.network.serialization_format_tradeoff||reliability;systems_design
skill.network.rpc_call_semantics|topic.network.serialization_rpc|RPC çağrı semantiğini ve failure görünürlüğünü local çağrıdan ayırabilmek.|systems_reasoning|complex|skill.network.serialization_format_tradeoff;skill.network.tcp_connection_lifecycle||distributed_failure_reasoning
skill.network.socket_client_server|topic.network.socket_programming|Socket ile client ve server veri akışını doğru kurabilmek.|systems_production|complex|skill.os.file_descriptor_model;skill.network.tcp_connection_lifecycle||systems_design
skill.network.socket_error_partial_io|topic.network.socket_programming|Socket hata kodları ve partial I/O davranışını güvenli ele alabilmek.|systems_production|complex|skill.network.socket_client_server;skill.os.blocking_io_semantics||reliability;debugging
skill.network.protocol_framing|topic.network.socket_programming|Message framing ve mesaj sınırı problemini doğru çözebilmek.|systems_production|complex|skill.network.socket_error_partial_io||reliability
skill.network.nonblocking_io_model|topic.network.io_multiplexing_nonblocking|Non-blocking socket ve readiness modelini doğru kurabilmek.|systems_production|complex|skill.network.socket_error_partial_io;skill.concurrency.async_execution_model||concurrency;performance_reasoning
skill.network.io_multiplexing_usage|topic.network.io_multiplexing_nonblocking|I/O multiplexing ile çok bağlantılı akışı ölçeklenebilir yönetebilmek.|systems_production|complex|skill.network.nonblocking_io_model||performance_reasoning;concurrency
skill.network.connection_resource_limits|topic.network.io_multiplexing_nonblocking|Bağlantı ve descriptor limitlerinin servis davranışına etkisini yönetebilmek.|systems_reasoning|complex|skill.network.io_multiplexing_usage;skill.os.resource_limit_reasoning||reliability;operational_safety
skill.network.latency_bandwidth_budget|topic.network.latency_bandwidth_failure_reasoning|Latency, bandwidth ve RTT bütçesini uçtan uca gerekçelendirebilmek.|measurement_reasoning|complex|skill.network.tcp_reliability_flow_control;skill.arch.latency_throughput_distinction||performance_reasoning
skill.network.timeout_retry_policy|topic.network.latency_bandwidth_failure_reasoning|Timeout, retry ve backoff politikasını failure modeline göre kurabilmek.|design_reasoning|complex|skill.network.latency_bandwidth_budget;skill.concurrency.cancellation_timeout_semantics||reliability;distributed_failure_reasoning
skill.network.network_failure_diagnosis|topic.network.latency_bandwidth_failure_reasoning|Network kaynaklı arızayı katman katman daraltarak lokalize edebilmek.|professional_workflow|complex|skill.network.packet_capture_reading;skill.network.timeout_retry_policy||debugging;distributed_failure_reasoning
skill.distributed.partial_failure_model|topic.distributed.failure_time_model|Partial failure ve güvenilmez ağ varsayımını sistem tasarımına yansıtabilmek.|systems_reasoning|complex|skill.network.timeout_retry_policy||distributed_failure_reasoning
skill.distributed.time_clock_ordering|topic.distributed.failure_time_model|Clock skew ve event ordering sınırlarını gerekçelendirebilmek.|systems_reasoning|complex|skill.distributed.partial_failure_model||distributed_failure_reasoning
skill.distributed.failure_detection_semantics|topic.distributed.failure_time_model|Failure detection ve timeout kaynaklı belirsizliği doğru yorumlayabilmek.|systems_reasoning|complex|skill.distributed.partial_failure_model;skill.network.timeout_retry_policy||distributed_failure_reasoning;reliability
skill.distributed.replication_models|topic.distributed.replication_partitioning|Replication topolojilerini durability ve availability gerekçesiyle ayırt edebilmek.|systems_reasoning|complex|skill.distributed.partial_failure_model||systems_design
skill.distributed.partitioning_strategies|topic.distributed.replication_partitioning|Partitioning stratejisini yük ve erişim desenine göre seçebilmek.|design_reasoning|complex|skill.distributed.replication_models;skill.dsa.hash_table_tradeoff||systems_design;performance_reasoning
skill.distributed.rebalancing_hotspot_reasoning|topic.distributed.replication_partitioning|Rebalancing ve hotspot etkisini ölçülebilir biçimde gerekçelendirebilmek.|systems_reasoning|complex|skill.distributed.partitioning_strategies||performance_reasoning
skill.distributed.consistency_model_distinction|topic.distributed.consistency_models|Güçlü, nihai ve ara consistency modellerini gözlemlenebilir davranışla ayırabilmek.|systems_reasoning|complex|skill.distributed.replication_models;skill.distributed.time_clock_ordering||systems_design
skill.distributed.availability_consistency_tradeoff|topic.distributed.consistency_models|Partition durumunda consistency ve availability dengesini gerekçelendirebilmek.|design_reasoning|complex|skill.distributed.consistency_model_distinction;skill.distributed.failure_detection_semantics||distributed_failure_reasoning;systems_design
skill.distributed.client_visible_anomaly_reasoning|topic.distributed.consistency_models|Consistency modelinin client tarafında ürettiği anomaliyi tanıyabilmek.|systems_reasoning|complex|skill.distributed.consistency_model_distinction||debugging;distributed_failure_reasoning
skill.distributed.quorum_reasoning|topic.distributed.consensus_leader_election|Quorum ve çoğunluk garantilerini doğru hesaplayabilmek.|systems_reasoning|complex|skill.distributed.replication_models||distributed_failure_reasoning
skill.distributed.consensus_protocol_model|topic.distributed.consensus_leader_election|Consensus protokolünün sağladığı ve sağlamadığı garantileri açıklayabilmek.|systems_reasoning|complex|skill.distributed.quorum_reasoning;skill.distributed.time_clock_ordering||systems_design
skill.distributed.leader_election_failover|topic.distributed.consensus_leader_election|Leader election ve failover davranışının split-brain riskini gerekçelendirebilmek.|systems_reasoning|complex|skill.distributed.consensus_protocol_model;skill.distributed.failure_detection_semantics||reliability;distributed_failure_reasoning
skill.storage.durability_write_path|topic.storage.durability_wal_recovery|Write path ve durability garantisini flush sınırıyla ilişkilendirebilmek.|systems_reasoning|complex|skill.os.buffering_flush_semantics||reliability
skill.storage.write_ahead_logging|topic.storage.durability_wal_recovery|Write-ahead logging ile crash recovery davranışını açıklayabilmek.|systems_reasoning|complex|skill.storage.durability_write_path||reliability
skill.storage.crash_recovery_reasoning|topic.storage.durability_wal_recovery|Crash noktası ve recovery sonrası state'i gerekçelendirebilmek.|systems_reasoning|complex|skill.storage.write_ahead_logging||reliability;debugging
skill.storage.transaction_atomicity|topic.storage.transactions_isolation|Transaction sınırını ve atomicity davranışını doğru kurabilmek.|systems_production|complex|skill.storage.write_ahead_logging||reliability
skill.storage.isolation_level_anomalies|topic.storage.transactions_isolation|Isolation seviyelerinin izin verdiği anomalileri örnekle ayırabilmek.|systems_reasoning|complex|skill.storage.transaction_atomicity;skill.concurrency.shared_mutable_state_risk||debugging;reliability
skill.storage.concurrency_control_mechanisms|topic.storage.transactions_isolation|Locking ve çok sürümlü concurrency control davranışını ayırt edebilmek.|systems_reasoning|complex|skill.storage.isolation_level_anomalies;skill.concurrency.lock_granularity_tradeoff||systems_design;performance_reasoning
skill.storage.index_structure_tradeoff|topic.storage.index_storage_structures|Ağaç ve log-yapılı index ailelerinin okuma/yazma dengesini gerekçelendirebilmek.|systems_reasoning|complex|skill.dsa.tree_traversal_basic;skill.storage.durability_write_path||performance_reasoning;systems_design
skill.storage.index_selection_reasoning|topic.storage.index_storage_structures|Sorgu erişim desenine göre index seçimini gerekçelendirebilmek.|design_reasoning|complex|skill.storage.index_structure_tradeoff;skill.dsa.asymptotic_bound_reasoning||performance_reasoning
skill.storage.data_layout_compaction|topic.storage.index_storage_structures|Disk üzerindeki layout ve compaction davranışının maliyetini yorumlayabilmek.|systems_reasoning|complex|skill.storage.index_structure_tradeoff;skill.arch.locality_reasoning||performance_reasoning
skill.storage.query_cost_reasoning|topic.storage.query_access_cost|Sorgu maliyetini erişim planı üzerinden tahmin edebilmek.|measurement_reasoning|complex|skill.storage.index_selection_reasoning||performance_reasoning
skill.storage.query_plan_inspection|topic.storage.query_access_cost|Execution plan çıktısını gerçek maliyet kaynağına eşleyebilmek.|tool_specific_production|complex|skill.storage.query_cost_reasoning||profiling_benchmarking;debugging
skill.distributed.queue_streaming_model|topic.distributed.queues_streaming|Queue ve stream semantiğini offset ve consumer davranışıyla ayırabilmek.|systems_reasoning|complex|skill.distributed.partitioning_strategies||systems_design
skill.distributed.backpressure_reasoning|topic.distributed.queues_streaming|Backpressure ve buffer büyümesinin sistem davranışına etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.distributed.queue_streaming_model;skill.concurrency.event_loop_behavior||reliability;performance_reasoning
skill.distributed.stream_processing_semantics|topic.distributed.queues_streaming|Streaming işlemede sıralama ve pencereleme sınırlarını açıklayabilmek.|systems_reasoning|complex|skill.distributed.queue_streaming_model;skill.distributed.time_clock_ordering||systems_design
skill.distributed.delivery_semantics|topic.distributed.idempotency_retry_delivery|Teslim garantisi seviyelerini gözlemlenebilir davranışla ayırt edebilmek.|systems_reasoning|complex|skill.distributed.queue_streaming_model;skill.distributed.partial_failure_model||distributed_failure_reasoning
skill.distributed.idempotent_operation_design|topic.distributed.idempotency_retry_delivery|Retry altında güvenli idempotent işlem tasarlayabilmek.|design_reasoning|complex|skill.distributed.delivery_semantics;skill.network.timeout_retry_policy||reliability;systems_design
skill.distributed.duplicate_reordering_handling|topic.distributed.idempotency_retry_delivery|Tekrarlı ve sırasız mesaj davranışını doğru ele alabilmek.|systems_production|complex|skill.distributed.idempotent_operation_design||reliability;debugging
skill.platform.namespace_isolation_model|topic.platform.namespaces_cgroups_isolation|Namespace tabanlı izolasyonun neyi ayırdığını açıklayabilmek.|systems_reasoning|complex|skill.os.process_address_space_model;skill.linux.file_permissions_ownership||systems_design
skill.platform.cgroup_resource_control|topic.platform.namespaces_cgroups_isolation|Kaynak limitlerinin process davranışına etkisini gözlemleyerek yönetebilmek.|systems_production|complex|skill.platform.namespace_isolation_model;skill.os.resource_limit_reasoning||reliability;operational_safety
skill.platform.container_vs_vm_boundary|topic.platform.namespaces_cgroups_isolation|Container ve VM izolasyon sınırını güvenlik ile performans gerekçesiyle ayırabilmek.|systems_reasoning|complex|skill.platform.namespace_isolation_model||operational_safety;systems_design
skill.platform.container_image_layers|topic.platform.container_image_build|Image layer, cache ve tekrar üretilebilirlik davranışını kontrol edebilmek.|tool_specific_production|complex|skill.platform.namespace_isolation_model;skill.engineering.reproducible_run_notes||reproducibility;build_tooling
skill.platform.image_build_optimization|topic.platform.container_image_build|Image boyutunu ve build süresini ölçülebilir biçimde iyileştirebilmek.|tool_specific_production|complex|skill.platform.container_image_layers||build_tooling;performance_reasoning
skill.platform.image_supply_chain_safety|topic.platform.container_image_build|Base image, sürüm sabitleme ve provenance riskini yönetebilmek.|professional_workflow|complex|skill.platform.container_image_layers||operational_safety;reproducibility
skill.platform.container_runtime_lifecycle|topic.platform.container_runtime_networking_storage|Container runtime lifecycle ve exit davranışını gözlemleyebilmek.|tool_specific_production|standard|skill.platform.container_image_layers;skill.os.process_creation_lifecycle||debugging;reliability
skill.platform.container_network_model|topic.platform.container_runtime_networking_storage|Container ağ izolasyonu, port ve servis erişimini doğru kurabilmek.|systems_production|complex|skill.platform.container_runtime_lifecycle;skill.network.addressing_routing_basics||systems_design;debugging
skill.platform.container_storage_persistence|topic.platform.container_runtime_networking_storage|Geçici ve kalıcı storage sınırını doğru kullanabilmek.|systems_production|complex|skill.platform.container_runtime_lifecycle;skill.storage.durability_write_path||reliability
skill.platform.orchestrated_workload_model|topic.platform.orchestration_workloads|Declarative workload ve desired-state modelini açıklayabilmek.|systems_reasoning|complex|skill.platform.container_runtime_lifecycle||systems_design
skill.platform.scheduling_placement_constraints|topic.platform.orchestration_workloads|Kaynak isteği ve yerleşim kısıtlarının etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.platform.orchestrated_workload_model;skill.platform.cgroup_resource_control||reliability;performance_reasoning
skill.platform.rollout_health_recovery|topic.platform.orchestration_workloads|Rollout, health check ve otomatik recovery davranışını doğru kurabilmek.|systems_production|complex|skill.platform.orchestrated_workload_model;skill.distributed.failure_detection_semantics||reliability
skill.platform.workload_failure_diagnosis|topic.platform.orchestration_workloads|Orkestre edilmiş workload arızasını event, log ve state üzerinden lokalize edebilmek.|professional_workflow|complex|skill.platform.rollout_health_recovery;skill.platform.container_runtime_lifecycle||debugging;observability
skill.platform.configuration_management|topic.platform.configuration_secrets_deployment|Ortama özgü configuration'ı image'dan ayırarak yönetebilmek.|professional_workflow|complex|skill.platform.orchestrated_workload_model;skill.linux.environment_path_resolution||reproducibility;reliability
skill.platform.secret_handling_safety|topic.platform.configuration_secrets_deployment|Secret'ı log, image ve repository sızıntısı olmadan yönetebilmek.|professional_workflow|complex|skill.platform.configuration_management||operational_safety
skill.platform.deployment_change_safety|topic.platform.configuration_secrets_deployment|Deployment değişikliğini geri alınabilir ve doğrulanabilir yapabilmek.|professional_workflow|complex|skill.platform.rollout_health_recovery;skill.git.branch_merge_model||reliability;reproducibility
skill.platform.cloud_compute_resource_model|topic.platform.cloud_resource_model|Cloud compute, ağ ve storage kaynak modelini iş yüküne eşleyebilmek.|systems_reasoning|complex|skill.platform.scheduling_placement_constraints||systems_design
skill.platform.cloud_identity_access_boundary|topic.platform.cloud_resource_model|Kimlik ve yetki sınırını en az ayrıcalık gerekçesiyle kurabilmek.|professional_workflow|complex|skill.platform.cloud_compute_resource_model;skill.linux.file_permissions_ownership||operational_safety
skill.platform.infrastructure_as_code_reproducibility|topic.platform.cloud_resource_model|Altyapı tanımını versiyonlanmış ve tekrar üretilebilir tutabilmek.|professional_workflow|complex|skill.platform.cloud_compute_resource_model;skill.platform.configuration_management||reproducibility;build_tooling
skill.observability.structured_logging|topic.platform.logs_metrics_traces|Yapılandırılmış ve korelasyon edilebilir log üretebilmek.|professional_workflow|complex|skill.platform.container_runtime_lifecycle;skill.engineering.reproducible_run_notes||observability;debugging
skill.observability.metric_instrumentation|topic.platform.logs_metrics_traces|Anlamlı metric tipi ve etiket seçimiyle instrumentation yapabilmek.|professional_workflow|complex|skill.observability.structured_logging||observability;performance_reasoning
skill.observability.distributed_tracing|topic.platform.logs_metrics_traces|Distributed trace ile istek yolunu ve gecikme dağılımını izleyebilmek.|tool_specific_production|complex|skill.observability.metric_instrumentation;skill.network.rpc_call_semantics||observability;profiling_benchmarking
skill.observability.signal_correlation_diagnosis|topic.platform.logs_metrics_traces|Log, metric ve trace sinyallerini tek arıza anlatısında birleştirebilmek.|professional_workflow|complex|skill.observability.distributed_tracing;skill.platform.workload_failure_diagnosis||debugging;distributed_failure_reasoning
skill.reliability.slo_definition|topic.platform.slo_reliability_incident|Kullanıcı etkisini yansıtan servis seviyesi göstergesi ve hedefi tanımlayabilmek.|design_reasoning|complex|skill.observability.metric_instrumentation||reliability;technical_communication
skill.reliability.alerting_signal_quality|topic.platform.slo_reliability_incident|Aksiyon alınabilir alarmı gürültüden ayıran uyarı kurabilmek.|professional_workflow|complex|skill.reliability.slo_definition||observability;reliability
skill.reliability.incident_response_workflow|topic.platform.slo_reliability_incident|Incident sırasında etki daraltma ve durum iletişimini yürütebilmek.|professional_workflow|complex|skill.reliability.alerting_signal_quality;skill.observability.signal_correlation_diagnosis||reliability;technical_communication
skill.reliability.postmortem_analysis|topic.platform.slo_reliability_incident|Suçlayıcı olmayan postmortem ile kök neden ve önleyici aksiyon üretebilmek.|professional_workflow|complex|skill.reliability.incident_response_workflow;skill.engineering.explain_debug_fix_basic||technical_communication;reliability
skill.performance.measurement_goal_definition|topic.performance.measurement_methodology|Performans sorusunu ölçülebilir metrik ve hedefe dönüştürebilmek.|measurement_reasoning|complex|skill.arch.latency_throughput_distinction||performance_reasoning;technical_communication
skill.performance.measurement_environment_control|topic.performance.measurement_methodology|Ölçüm ortamındaki gürültü kaynaklarını kontrol edebilmek.|measurement_production|complex|skill.performance.measurement_goal_definition;skill.engineering.reproducible_run_notes||reproducibility;profiling_benchmarking
skill.performance.measurement_validity_reasoning|topic.performance.measurement_methodology|Ölçümün gerçekten hedef davranışı ölçtüğünü doğrulayabilmek.|measurement_reasoning|complex|skill.performance.measurement_environment_control||profiling_benchmarking;reliability
skill.performance.benchmark_workload_design|topic.performance.benchmark_design|Gerçek kullanımı temsil eden benchmark iş yükü tasarlayabilmek.|measurement_production|complex|skill.performance.measurement_validity_reasoning;skill.python.benchmark_automation||profiling_benchmarking
skill.performance.microbenchmark_pitfalls|topic.performance.benchmark_design|Microbenchmark'ta ölü kod, warmup ve ölçek hatalarını önleyebilmek.|measurement_reasoning|complex|skill.performance.benchmark_workload_design;skill.arch.pipeline_hazard_reasoning||profiling_benchmarking;debugging
skill.performance.benchmark_reproducibility|topic.performance.benchmark_design|Benchmark sonucunu başkasının tekrar üretebileceği biçimde paketleyebilmek.|professional_workflow|complex|skill.performance.benchmark_workload_design;skill.engineering.reproducible_run_notes||reproducibility;technical_communication
skill.performance.variance_distribution_reasoning|topic.performance.statistics_variance_reasoning|Ölçüm dağılımını ve varyansı sonuç iddiasına doğru yansıtabilmek.|measurement_reasoning|complex|skill.performance.measurement_validity_reasoning||profiling_benchmarking
skill.performance.tail_latency_reasoning|topic.performance.statistics_variance_reasoning|Tail latency'yi ortalama davranıştan ayrı analiz edebilmek.|measurement_reasoning|complex|skill.performance.variance_distribution_reasoning;skill.network.latency_bandwidth_budget||performance_reasoning;reliability
skill.performance.regression_detection|topic.performance.statistics_variance_reasoning|Performans regresyonunu gürültüden ayırt edip raporlayabilmek.|measurement_production|complex|skill.performance.variance_distribution_reasoning;skill.performance.benchmark_reproducibility||profiling_benchmarking;testing
skill.performance.cpu_profile_collection|topic.performance.cpu_profiling|Temsil edici CPU profili toplayabilmek.|tool_specific_production|complex|skill.performance.measurement_environment_control;skill.python.profiling_measurement||profiling_benchmarking
skill.performance.profile_interpretation|topic.performance.cpu_profiling|Profil çıktısındaki sıcak yolu gerçek maliyet kaynağına eşleyebilmek.|tool_specific_reasoning|complex|skill.performance.cpu_profile_collection;skill.arch.hardware_counter_interpretation||profiling_benchmarking;debugging
skill.performance.sampling_instrumentation_tradeoff|topic.performance.cpu_profiling|Örnekleme ve instrumentation yaklaşımlarını gerekçeyle seçebilmek.|measurement_reasoning|complex|skill.performance.cpu_profile_collection||profiling_benchmarking
skill.performance.memory_profile_analysis|topic.performance.memory_profiling|Allocation ve memory büyüme profilini analiz edebilmek.|tool_specific_production|complex|skill.performance.profile_interpretation;skill.os.memory_usage_diagnosis||profiling_benchmarking;debugging
skill.performance.cache_behavior_measurement|topic.performance.memory_profiling|Cache davranışını ölçüp locality hipotezini doğrulayabilmek.|measurement_production|complex|skill.arch.cache_miss_classification;skill.performance.profile_interpretation||profiling_benchmarking;performance_reasoning
skill.performance.io_bound_analysis|topic.performance.memory_profiling|I/O sınırlı davranışı CPU sınırlı davranıştan ölçümle ayırabilmek.|measurement_reasoning|complex|skill.performance.profile_interpretation;skill.os.cpu_time_accounting||profiling_benchmarking
skill.performance.bottleneck_localization|topic.performance.bottleneck_attribution|Sistemdeki baskın darboğazı katmanlar arasında lokalize edebilmek.|measurement_reasoning|complex|skill.performance.profile_interpretation;skill.arch.roofline_bottleneck_reasoning||performance_reasoning;debugging
skill.performance.optimization_hypothesis_validation|topic.performance.bottleneck_attribution|Optimizasyon hipotezini önce/sonra ölçümüyle doğrulayabilmek.|measurement_production|complex|skill.performance.bottleneck_localization;skill.performance.regression_detection||profiling_benchmarking
skill.performance.end_to_end_latency_attribution|topic.performance.bottleneck_attribution|Uçtan uca gecikmeyi bileşenlere ölçülebilir biçimde dağıtabilmek.|measurement_reasoning|complex|skill.performance.bottleneck_localization;skill.observability.distributed_tracing||performance_reasoning;observability
skill.performance.capacity_headroom_reasoning|topic.performance.capacity_cost_tradeoff|Kapasite, doygunluk ve headroom ilişkisini gerekçelendirebilmek.|measurement_reasoning|complex|skill.performance.tail_latency_reasoning;skill.platform.scheduling_placement_constraints||performance_reasoning;reliability
skill.performance.cost_efficiency_reasoning|topic.performance.capacity_cost_tradeoff|Performans kazancını kaynak ve maliyet karşılığıyla değerlendirebilmek.|design_reasoning|complex|skill.performance.capacity_headroom_reasoning;skill.platform.cloud_compute_resource_model||performance_reasoning;technical_communication
skill.performance.performance_report_communication|topic.performance.capacity_cost_tradeoff|Ölçüm, yöntem ve sınırlarını içeren performans raporu yazabilmek.|professional_workflow|complex|skill.performance.optimization_hypothesis_validation;skill.performance.benchmark_reproducibility|skill.english.explain_simple_technical_process|technical_communication;reproducibility
""".strip().splitlines()


EVIDENCE_BY_KIND = {
    "shared_reasoning": ["explanation", "code_reading"],
    "professional_workflow": ["hands_on_workflow", "explanation"],
    "design_reasoning": ["design_argument", "explanation"],
    "systems_reasoning": ["system_observation", "explanation"],
    "systems_production": ["authored_code", "observed_system_behavior"],
    "language_specific_production": ["authored_code", "deterministic_test"],
    "language_specific_reasoning": ["code_reading", "explanation"],
    "tool_specific_production": ["hands_on_system_task", "observed_result"],
    "tool_specific_reasoning": ["artifact_analysis", "explanation"],
    "measurement_production": ["measurement_artifact", "reproducible_benchmark"],
    "measurement_reasoning": ["artifact_analysis", "explanation"],
}

SHARED_KINDS = {
    "shared_reasoning", "professional_workflow", "design_reasoning",
    "systems_reasoning", "systems_production", "measurement_reasoning", "measurement_production",
}

CRITICAL_SKILLS = {
    "skill.cpp.object_lifetime_scope",
    "skill.cpp.raii_resource_ownership",
    "skill.arch.cache_hierarchy_model",
    "skill.os.process_address_space_model",
    "skill.os.virtual_physical_translation",
    "skill.os.file_descriptor_model",
    "skill.concurrency.shared_mutable_state_risk",
    "skill.concurrency.mutex_critical_section",
    "skill.network.layering_model",
    "skill.network.tcp_connection_lifecycle",
    "skill.distributed.partial_failure_model",
    "skill.storage.durability_write_path",
    "skill.platform.namespace_isolation_model",
    "skill.performance.measurement_validity_reasoning",
}

# Practice-shaped capabilities survive tool churn even though their Domain is fast-moving.
EVERGREEN_PRACTICE = {
    "skill.reliability.incident_response_workflow",
    "skill.reliability.postmortem_analysis",
    "skill.platform.image_supply_chain_safety",
    "skill.platform.deployment_change_safety",
}

TECHNOLOGY_DEPENDENCIES = {
    "skill.platform.": ["technology.container_platform", "technology.orchestration_platform"],
    "skill.reliability.": ["technology.observability_stack", "technology.alerting_platform"],
    "skill.concurrency.": ["technology.concurrency_analysis_tooling"],
    "skill.observability.": ["technology.observability_stack"],
    "skill.cpp.": ["technology.cpp_toolchain"],
    "skill.storage.": ["technology.storage_engine"],
    "skill.performance.": ["technology.profiling_toolchain"],
    "skill.os.": ["technology.linux_kernel_interface"],
    "skill.network.": ["technology.network_tooling"],
    "skill.arch.": ["technology.cpu_platform"],
}


def technology_refs(sid: str, freshness: str) -> list[str]:
    if freshness == "evergreen":
        return []
    for prefix, refs in TECHNOLOGY_DEPENDENCIES.items():
        if sid.startswith(prefix):
            return refs
    return []


def parse_skills() -> list[dict]:
    result = []
    for line in SKILL_ROWS:
        sid, topic, statement, kind, retention, hard, soft, tags = line.split("|")
        domain = next(module_domain[m] for m, topics in module_topics.items() if topic in topics)
        evidence = EVIDENCE_BY_KIND[kind]
        tag_list = [x for x in tags.split(";") if x]
        if sid in EVERGREEN_PRACTICE:
            freshness = "evergreen"
        elif sid.startswith(("skill.platform.", "skill.observability.", "skill.reliability.")) and kind in {
            "tool_specific_production", "tool_specific_reasoning", "professional_workflow"
        }:
            freshness = "fast_moving"
        elif kind.startswith("tool_specific"):
            freshness = "version_sensitive"
        else:
            freshness = "evergreen"
        depth = ["independent_application", "delayed_retention"]
        if "debugging" in tag_list:
            depth.append("debugging")
        if kind in {"measurement_production", "measurement_reasoning"} or "profiling_benchmarking" in tag_list:
            depth.append("performance_measurement")
        if kind in {"systems_reasoning", "design_reasoning"}:
            depth.append("transfer")
        if sid.startswith(("skill.platform.", "skill.observability.", "skill.reliability.")):
            depth.append("production_context")
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
                "classification": "shared" if kind in SHARED_KINDS else "domain_specific",
                "rationale": (
                    "Stable systems capability survives tool/vendor change and is reused across Topics."
                    if kind in SHARED_KINDS else
                    "Language, toolchain or runtime procedure changes the capability core, so it stays specific."
                ),
            },
            "evidence_depth_expectations": sorted(set(depth)),
            "retention_profile": retention,
            "diagnostic_eligibility": "eligible",
            "critical_prerequisite_candidate": sid in CRITICAL_SKILLS,
            "remediation_tags": sorted(set(
                ["targeted_reteach", "fresh_variant"]
                + (["debug_localization"] if "debugging" in tag_list else [])
                + (["measurement_replay"] if "profiling_benchmarking" in tag_list else [])
                + (["failure_scenario_replay"] if "distributed_failure_reasoning" in tag_list else [])
            )),
            "professional_capability_tags": tag_list,
            "project_capability_tags": [],
            "source_refs": ["source.repo.pdm_v0", "source.repo.frdb_v0", "source.repo.gns_v0"],
            "provenance_ref": "source.repo.internal_6d_authoring",
            "freshness_class": freshness,
            "technology_dependency_refs": technology_refs(sid, freshness),
            "duplicate_resolution": {
                "status": "create_new",
                "compared_skill_ids": sorted(
                    x for x in [y for y in hard.split(";") if y] + [y for y in soft.split(";") if y]
                    if x in base_skills
                ),
                "rationale": (
                    "Compared against the accepted 6C registry and the full 6D candidate set; "
                    "prior canonical Skills are reused as prerequisites or TopicSkillLinks instead of being cloned."
                ),
            },
            "granularity_review_codes": ["GRANULARITY_OK"] + (
                ["SHARED_SKILL_REUSE"] if kind in SHARED_KINDS else
                ["TOOL_SPECIFIC_SPLIT"] if kind.startswith("tool_specific") else
                ["LANGUAGE_SPECIFIC_SPLIT"] if kind.startswith("language_specific") else []
            ),
            "seed_mapping_refs": [],
            "review_refs": [],
            "_hard": [x for x in hard.split(";") if x],
            "_soft": [x for x in soft.split(";") if x],
        })
    return result


skills = parse_skills()
skill_by_id = {x["skill_id_candidate"]: x for x in skills}


EXTRA_OBJECTIVES = [
    ("skill.cpp.move_semantics", "detect_invalid_moved_from_use",
     "Moved-from bir nesnenin hatalı kullanımını tespit edip davranışı bozmadan düzeltir."),
    ("skill.cpp.sanitizer_ub_diagnosis", "localize_and_verify_fix",
     "Sanitizer bulgusunu kaynağa lokalize eder, düzeltir ve temiz çalıştırmayla doğrular."),
    ("skill.arch.roofline_bottleneck_reasoning", "classify_bound_from_measurement",
     "Yeni bir ölçüm setinde compute-bound / memory-bound sınıflandırmasını gerekçesiyle üretir."),
    ("skill.os.memory_usage_diagnosis", "localize_growth_source",
     "Artan memory kullanımının kaynağını ölçüm kanıtıyla belirli bir allocation yoluna bağlar."),
    ("skill.concurrency.data_race_diagnosis", "localize_and_verify_fix",
     "Data race'i lokalize eder, düzeltir ve tekrarlı çalıştırmayla doğrular."),
    ("skill.concurrency.deadlock_analysis", "propose_and_verify_lock_order",
     "Deadlock senaryosunda güvenli lock sırası önerir ve önerinin kilitlenmeyi kaldırdığını gösterir."),
    ("skill.network.network_failure_diagnosis", "narrow_layer_and_verify",
     "Arızayı katman katman daraltır, hipotezini gözlemle doğrular ve sonucu raporlar."),
    ("skill.storage.crash_recovery_reasoning", "predict_recovered_state",
     "Verilen crash noktası için recovery sonrası state'i tahmin eder ve gerekçelendirir."),
    ("skill.distributed.idempotent_operation_design", "verify_under_retry",
     "Tasarladığı işlemin tekrarlı çağrı altında aynı sonucu ürettiğini gösterir."),
    ("skill.platform.workload_failure_diagnosis", "localize_from_events",
     "Workload arızasını event ve state kanıtıyla tek bir kök nedene bağlar."),
    ("skill.observability.signal_correlation_diagnosis", "build_failure_narrative",
     "Log, metric ve trace kanıtını tek tutarlı arıza anlatısına dönüştürür."),
    ("skill.reliability.postmortem_analysis", "author_postmortem",
     "Gerçek bir arıza için kök neden, etki ve önleyici aksiyon içeren postmortem yazar."),
    ("skill.performance.regression_detection", "separate_signal_from_noise",
     "Yeni ölçüm setinde regresyon iddiasını varyans kanıtıyla kabul veya reddeder."),
    ("skill.performance.bottleneck_localization", "attribute_and_verify",
     "Baskın darboğazı ölçümle atfeder ve hedefli değişikliğin etkisini doğrular."),
    ("skill.performance.optimization_hypothesis_validation", "report_before_after",
     "Önce/sonra ölçümünü yöntem ve belirsizlik sınırlarıyla birlikte raporlar."),
]


def build_objective(sid: str, action: str, statement: str, extra: bool) -> dict:
    skill = skill_by_id[sid]
    evidence = skill["independent_evidence_path"]["direct_evidence_types"]
    return {
        "objective_id_candidate": "objective." + sid.removeprefix("skill.") + "." + action,
        "owner_skill_id": sid,
        "objective_statement": statement,
        "observable_action": action,
        "success_criteria": (
            "Hedef davranış bağımsız, prerequisite-valid ve tekrar doğrulanabilir kanıtla gösterilir."
        ),
        "lifecycle_status": "draft",
        "authoring_status": "internally_qa_passed",
        "requirement_role": "required",
        "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
        "evidence_profile": {
            "acceptable_evidence_types": evidence,
            "direct_evidence_types": evidence,
            "required_direct_type": evidence[0],
            "requires_user_authored_artifact": any(
                x in evidence for x in ("authored_code", "measurement_artifact", "hands_on_system_task", "hands_on_workflow")
            ),
            "requires_transfer": True,
            "evaluator_requirement": "verified",
            "policy_defaults_ref": "GRE-v0",
        },
        "task_environment_expectation": "Fresh, prerequisite-valid systems task, artifact or observed run.",
        "allowed_tools_policy_ref": "H0 independent evidence",
        "diagnostic_eligibility": skill["diagnostic_eligibility"],
        "retention_requirement": {
            "delayed_revalidation_required": True,
            "context_diversity_requirement": "different_system_or_workload_context" if extra else "fresh_context",
        },
        "remediation_tags": skill["remediation_tags"],
        "source_refs": ["source.repo.frdb_v0", "source.repo.gns_v0"],
        "provenance_ref": "source.repo.internal_6d_authoring",
        "freshness_class": skill["freshness_class"],
        "granularity_review_codes": ["GRANULARITY_OK"],
        "review_refs": [],
    }


objectives = [
    build_objective(
        x["skill_id_candidate"], "demonstrate_capability",
        "Yeni bir bağlamda " + x["capability_statement"].removesuffix("."),
        False,
    )
    for x in skills
]
objectives += [build_objective(sid, action, statement, True) for sid, action, statement in EXTRA_OBJECTIVES]
objectives.sort(key=lambda x: x["objective_id_candidate"])
objective_ids = {x["objective_id_candidate"] for x in objectives}


organization = []
for did, (name, route, role) in domains.items():
    organization.append({
        "entity_type": "domain", "logical_id_candidate": did, "display_name": name,
        "semantic_statement": f"{name} systems capability organization domain.",
        "parent_organization_id": None, "primary_domain_id": did, "organization_role": role,
        "teaching_intent_tags": ["teach", "practice", "assess"], "linked_route_family_ids": [route],
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
        "source_refs": ["source.repo.pdm_v0"], "provenance_ref": "source.repo.internal_6d_authoring",
        "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"],
        "review_refs": [], "notes": None,
    })
for mid, topics in module_topics.items():
    did = module_domain[mid]
    route = domains[did][1]
    organization.append({
        "entity_type": "module", "logical_id_candidate": mid,
        "display_name": mid.split(".", 1)[1].replace(".", " ").replace("_", " ").title(),
        "semantic_statement": "Related systems Topics için stable organization cluster.",
        "parent_organization_id": did, "primary_domain_id": did, "organization_role": "authoring_cluster",
        "teaching_intent_tags": ["teach", "practice"], "linked_route_family_ids": [route],
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
        "source_refs": ["source.repo.pdm_v0", "source.repo.gns_v0"],
        "provenance_ref": "source.repo.internal_6d_authoring", "freshness_class": "evergreen",
        "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
    })
    for topic in topics:
        organization.append({
            "entity_type": "topic", "logical_id_candidate": topic,
            "display_name": topic.split(".", 1)[1].replace(".", " ").replace("_", " ").title(),
            "semantic_statement": "Learner-facing teaching, practice and evidence context.",
            "parent_organization_id": mid, "primary_domain_id": did, "organization_role": "teaching_context",
            "teaching_intent_tags": ["teach", "practice", "assess", "remediate"],
            "linked_route_family_ids": [route], "lifecycle_status": "draft",
            "authoring_status": "internally_qa_passed", "source_refs": ["source.repo.pdm_v0", "source.repo.fdm_v0"],
            "provenance_ref": "source.repo.internal_6d_authoring", "freshness_class": "evergreen",
            "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
        })

topic_ids = {x["logical_id_candidate"] for x in organization if x["entity_type"] == "topic"}


topic_links = []
for skill in skills:
    topic_links.append({
        "topic_id": skill["primary_teaching_topic_id"], "skill_id": skill["skill_id_candidate"],
        "role": "teach", "importance": "core", "is_primary_teaching_context": True,
        "objective_scope_ids": [
            x["objective_id_candidate"] for x in objectives
            if x["owner_skill_id"] == skill["skill_id_candidate"]
        ],
        "authoring_rationale": "Primary evidence-bearing teaching context.",
        "source_refs": ["source.repo.internal_6d_authoring"], "lifecycle_status": "draft", "review_refs": [],
    })

# Prior-package canonical Skills reused in 6D Topics instead of being cloned.
REUSE_LINKS = {
    "skill.c.pointer_dereference": ["topic.cpp.object_lifetime_ownership", "topic.os.virtual_memory_paging"],
    "skill.memory.address_value_distinction": ["topic.arch.memory_hierarchy_caches", "topic.os.virtual_memory_paging"],
    "skill.memory.storage_lifetime_intuition": ["topic.cpp.object_lifetime_ownership"],
    "skill.c.struct_enum_model": ["topic.arch.locality_access_patterns"],
    "skill.c.file_io_error_handling": ["topic.os.file_descriptor_io_model"],
    "skill.programming.state_assignment_model": ["topic.concurrency.thread_task_model"],
    "skill.programming.debug_localization_basic": [
        "topic.concurrency.race_deadlock_diagnosis",
        "topic.network.latency_bandwidth_failure_reasoning",
        "topic.performance.bottleneck_attribution",
    ],
    "skill.programming.code_reading_basic": [
        "topic.cpp.standard_library_containers_algorithms", "topic.arch.isa_execution",
    ],
    "skill.programming.test_case_basic": ["topic.cpp.testing_debugging_sanitizers"],
    "skill.engineering.reproducible_run_notes": [
        "topic.performance.benchmark_design", "topic.platform.container_image_build",
    ],
    "skill.engineering.explain_debug_fix_basic": ["topic.platform.slo_reliability_incident"],
    "skill.linux.process_inspection": [
        "topic.os.scheduling_behavior", "topic.platform.container_runtime_networking_storage",
    ],
    "skill.linux.terminal_filesystem_navigation": ["topic.platform.container_image_build"],
    "skill.linux.environment_path_resolution": ["topic.platform.configuration_secrets_deployment"],
    "skill.git.branch_merge_model": ["topic.platform.configuration_secrets_deployment"],
    "skill.dsa.asymptotic_bound_reasoning": [
        "topic.storage.index_storage_structures", "topic.cpp.standard_library_containers_algorithms",
    ],
    "skill.dsa.hash_table_tradeoff": ["topic.distributed.replication_partitioning"],
    "skill.dsa.tree_traversal_basic": ["topic.storage.index_storage_structures"],
    "skill.python.profiling_measurement": ["topic.performance.cpu_profiling"],
    "skill.python.benchmark_automation": ["topic.performance.benchmark_design"],
    "skill.python.async_task_coordination": ["topic.concurrency.async_io_event_loops"],
    "skill.python.network_client_basic": ["topic.network.http_semantics"],
    "skill.english.read_definition_and_constraint": ["topic.network.transport_tcp_udp"],
    "skill.english.write_basic_bug_description": ["topic.platform.slo_reliability_incident"],
}
for sid, topics in REUSE_LINKS.items():
    for topic in topics:
        topic_links.append({
            "topic_id": topic, "skill_id": sid, "role": "reinforce", "importance": "supporting",
            "is_primary_teaching_context": False, "objective_scope_ids": [],
            "authoring_rationale": "Accepted 6C canonical Skill is reused in a Systems context without cloning learner state.",
            "source_refs": ["source.repo.fdm_v0"], "lifecycle_status": "draft", "review_refs": [],
        })


# FRDB-v0 section 19 Pass B result: these declared sources improve fluency, transfer or depth,
# but the target stays fairly teachable and its evidence stays interpretable without them.
# They are therefore authored as soft edges and never hard-block the target branch.
SOFT_OVERRIDES = {
    ("skill.memory.address_value_distinction", "skill.arch.cache_hierarchy_model"),
    ("skill.c.compile_link_run_basic", "skill.arch.isa_abstraction_boundary"),
    ("skill.c.bitwise_mask_reasoning", "skill.arch.number_representation_precision"),
    ("skill.c.struct_enum_model", "skill.arch.memory_alignment_layout"),
    ("skill.cpp.iterator_range_algorithms", "skill.arch.vectorization_enablement"),
    ("skill.dsa.sequence_traversal", "skill.cpp.iterator_range_algorithms"),
    ("skill.programming.trace_execution_basic", "skill.cpp.exception_safety_guarantees"),
    ("skill.c.file_io_error_handling", "skill.cpp.error_return_vs_exception"),
    ("skill.arch.cache_hierarchy_model", "skill.os.context_switch_cost"),
    ("skill.arch.cache_hierarchy_model", "skill.os.tlb_page_size_effects"),
    ("skill.c.dynamic_allocation_lifecycle", "skill.os.memory_mapping_usage"),
    ("skill.cpp.sanitizer_ub_diagnosis", "skill.os.memory_usage_diagnosis"),
    ("skill.cpp.raii_resource_ownership", "skill.concurrency.thread_lifecycle_management"),
    ("skill.cpp.raii_resource_ownership", "skill.concurrency.mutex_critical_section"),
    ("skill.cpp.raii_resource_ownership", "skill.concurrency.cancellation_timeout_semantics"),
    ("skill.cpp.const_correctness_qualifiers", "skill.concurrency.atomic_operations"),
    ("skill.cpp.sanitizer_ub_diagnosis", "skill.concurrency.data_race_diagnosis"),
    ("skill.arch.false_sharing_awareness", "skill.concurrency.lock_granularity_tradeoff"),
    ("skill.arch.instruction_level_parallelism", "skill.concurrency.memory_ordering_model"),
    ("skill.arch.latency_throughput_distinction", "skill.concurrency.scaling_limit_reasoning"),
    ("skill.python.async_task_coordination", "skill.concurrency.async_execution_model"),
    ("skill.linux.process_inspection", "skill.network.packet_capture_reading"),
    ("skill.python.network_client_basic", "skill.network.http_request_response_model"),
    ("skill.programming.input_validation_reasoning", "skill.network.http_api_error_contract"),
    ("skill.python.bytes_text_boundary", "skill.network.serialization_format_tradeoff"),
    ("skill.arch.latency_throughput_distinction", "skill.network.latency_bandwidth_budget"),
    ("skill.dsa.hash_table_tradeoff", "skill.distributed.partitioning_strategies"),
    ("skill.concurrency.event_loop_behavior", "skill.distributed.backpressure_reasoning"),
    ("skill.concurrency.shared_mutable_state_risk", "skill.storage.isolation_level_anomalies"),
    ("skill.concurrency.lock_granularity_tradeoff", "skill.storage.concurrency_control_mechanisms"),
    ("skill.dsa.tree_traversal_basic", "skill.storage.index_structure_tradeoff"),
    ("skill.storage.durability_write_path", "skill.storage.index_structure_tradeoff"),
    ("skill.arch.locality_reasoning", "skill.storage.data_layout_compaction"),
    ("skill.engineering.reproducible_run_notes", "skill.platform.container_image_layers"),
    ("skill.storage.durability_write_path", "skill.platform.container_storage_persistence"),
    ("skill.distributed.failure_detection_semantics", "skill.platform.rollout_health_recovery"),
    ("skill.linux.environment_path_resolution", "skill.platform.configuration_management"),
    ("skill.git.branch_merge_model", "skill.platform.deployment_change_safety"),
    ("skill.linux.file_permissions_ownership", "skill.platform.cloud_identity_access_boundary"),
    ("skill.network.rpc_call_semantics", "skill.observability.distributed_tracing"),
    ("skill.platform.workload_failure_diagnosis", "skill.observability.signal_correlation_diagnosis"),
    ("skill.engineering.explain_debug_fix_basic", "skill.reliability.postmortem_analysis"),
    ("skill.python.benchmark_automation", "skill.performance.benchmark_workload_design"),
    ("skill.arch.pipeline_hazard_reasoning", "skill.performance.microbenchmark_pitfalls"),
    ("skill.network.latency_bandwidth_budget", "skill.performance.tail_latency_reasoning"),
    ("skill.python.profiling_measurement", "skill.performance.cpu_profile_collection"),
    ("skill.arch.hardware_counter_interpretation", "skill.performance.profile_interpretation"),
    ("skill.os.cpu_time_accounting", "skill.performance.io_bound_analysis"),
    ("skill.performance.regression_detection", "skill.performance.optimization_hypothesis_validation"),
    ("skill.observability.distributed_tracing", "skill.performance.end_to_end_latency_attribution"),
    ("skill.platform.scheduling_placement_constraints", "skill.performance.capacity_headroom_reasoning"),
    ("skill.platform.cloud_compute_resource_model", "skill.performance.cost_efficiency_reasoning"),
}

REASON_KIND_BY_TAG = {
    "tool_specific_production": "tool_environment_dependency",
    "tool_specific_reasoning": "tool_environment_dependency",
    "language_specific_production": "language_dependency",
    "language_specific_reasoning": "language_dependency",
    "measurement_production": "performance_reasoning_dependency",
    "measurement_reasoning": "performance_reasoning_dependency",
    "professional_workflow": "professional_workflow_dependency",
}

prerequisites = []
applied_soft_overrides = set()
for skill in skills:
    target = skill["skill_id_candidate"]
    for declared_kind, sources in (("hard", skill.pop("_hard")), ("soft", skill.pop("_soft"))):
        for source in sources:
            kind = declared_kind
            downgraded = False
            if declared_kind == "hard" and (source, target) in SOFT_OVERRIDES:
                kind, downgraded = "soft", True
                applied_soft_overrides.add((source, target))
            if kind == "hard":
                reason = REASON_KIND_BY_TAG.get(skill["capability_kind"], "evidence_interpretability")
                if "operational_safety" in skill["professional_capability_tags"]:
                    reason = "safety_dependency"
            else:
                reason = "conceptual_dependency"
            prerequisites.append({
                "prerequisite_skill_id": source, "target_skill_id": target, "edge_kind": kind,
                "reason_kind": reason,
                "strictness_profile_ref": "default_prg_v0",
                "authoring_rationale": (
                    "Source yokken target ne adil öğretilebilir ne de target evidence güvenilir yorumlanabilir."
                    if kind == "hard" else
                    "Pass B sonucu: source derinlik veya akıcılık katar; target adil öğretilebilir ve "
                    "evidence yorumlanabilir kaldığı için hard-block edilmez."
                    if downgraded else
                    "Source akıcılığı artırır fakat target çalışmasını hard-block etmez."
                ),
                "contamination_risk_if_missing": "high" if kind == "hard" else "low",
                "hard_soft_test_result": "downgraded_to_soft_in_pass_b" if downgraded else "declared_as_authored",
                "task_specific_instead_of_graph_edge": False,
                "cross_package_ref": source in base_skills,
                "source_refs": ["source.repo.prg_v0", "source.repo.fdm_v0"],
                "provenance_ref": "source.repo.internal_6d_authoring", "lifecycle_status": "draft",
                "review_status": "reviewed", "review_refs": [],
            })

requirements = []
for skill in skills:
    requirements.append({
        "scope_kind": "domain", "scope_id": skill["shared_placement_domain_ids"][0],
        "capability_kind": "skill", "capability_id": skill["skill_id_candidate"],
        "requirement_role": "required",
        "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
        "rationale": "6D systems route coverage; runtime readiness stays Skill-level and branch-local.",
        "source_refs": ["source.repo.pdm_v0", "source.repo.kgc_v0"], "review_refs": [],
    })
for skill in skills:
    if "distributed_failure_reasoning" in skill["professional_capability_tags"] or \
            "performance_reasoning" in skill["professional_capability_tags"]:
        requirements.append({
            "scope_kind": "professional_route", "scope_id": "scope.professional_route.ai_infrastructure",
            "capability_kind": "skill", "capability_id": skill["skill_id_candidate"],
            "requirement_role": "required", "criticality": "standard",
            "rationale": "AI infrastructure route requires distributed failure and performance reasoning capability.",
            "source_refs": ["source.repo.pdm_v0"], "review_refs": [],
        })

professional = []
for skill in skills:
    for tag in skill["professional_capability_tags"]:
        professional.append({
            "capability_id": skill["skill_id_candidate"], "professional_family_id": "professional." + tag,
            "attribution_role": "supporting", "context_requirements": ["fresh_independent_context"],
            "evidence_expectation_ref": "GRE-v0",
            "rationale": "Systems capability supports this professional family without granting family-level mastery.",
            "source_refs": ["source.repo.pdm_v0"], "review_refs": [],
        })

PROJECT_SKILLS = [
    "skill.cpp.build_system_project_definition",
    "skill.cpp.unit_test_harness",
    "skill.os.signal_handling_semantics",
    "skill.concurrency.thread_pool_usage",
    "skill.network.socket_client_server",
    "skill.network.protocol_framing",
    "skill.storage.durability_write_path",
    "skill.distributed.idempotent_operation_design",
    "skill.platform.container_image_layers",
    "skill.observability.structured_logging",
    "skill.reliability.slo_definition",
    "skill.performance.benchmark_reproducibility",
]
project_attributions = []
for sid in PROJECT_SKILLS:
    oid = next(x["objective_id_candidate"] for x in objectives if x["owner_skill_id"] == sid)
    project_attributions.append({
        "project_or_capstone_id": "project.systems.observable_networked_service", "skill_id": sid,
        "objective_id": oid, "role": "supporting", "structurally_essential": True,
        "separately_observable": True, "expected_evidence_type": "component_artifact",
        "rubric_component_ref": "rubric.systems.observable_networked_service." + sid.split(".")[-1],
        "source_refs": ["source.repo.frdb_v0"], "review_refs": [],
    })

reused_base_skills = sorted(
    set(REUSE_LINKS)
    | {x["prerequisite_skill_id"] for x in prerequisites if x["prerequisite_skill_id"] in base_skills}
)
seed_mappings = [{
    "seed_id": sid, "seed_kind": "skill", "disposition": "ratify_as_is",
    "result_entity_refs": [sid], "evidence_compatibility": "not_applicable_not_published",
    "mapping_source_package": "decomposition.6c_foundations",
    "rationale": {
        "semantic_boundary": "Accepted 6C capability identity is unchanged by its Systems reuse.",
        "prerequisite_effect": "6D declares cross-package prerequisite edges against the existing canonical ID.",
        "evidence_effect": "No evidence migration; the 6C package is not learner-published.",
        "remediation_effect": "Remediation stays on the original capability, not on a Systems clone.",
        "reuse_effect": "Reuse is expressed with TopicSkillLink and prerequisite edges instead of a new Skill.",
    },
    "mapping_class": "reused_from_prior_package",
    "source_refs": ["source.repo.fdm_v0", "source.repo.gns_v0"], "review_refs": [],
} for sid in reused_base_skills]

reviews = [
    {
        "review_id": "review.6d.external_coverage",
        "subject_refs": sorted(domains),
        "review_codes": ["NEEDS_GRANULARITY_REVIEW"], "severity": "non_blocking",
        "question": "Bağımsız external kaynaklar D06–D13 için coverage eksiği veya hidden prerequisite gösteriyor mu?",
        "decision_inputs_required": ["independent Research AI", "authoritative current sources"],
        "resolution_owner_step": "6H", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": "2026-08-26", "resolved_at": None,
    },
    {
        "review_id": "review.6d.platform_tool_freshness",
        "subject_refs": ["domain.containers_cloud_observability"],
        "review_codes": ["TOOL_SPECIFIC_SPLIT"], "severity": "non_blocking",
        "question": "Fast-moving container/orchestration/cloud capability'leri için review trigger policy nasıl versiyonlanmalı?",
        "decision_inputs_required": ["current vendor documentation", "freshness policy calibration"],
        "resolution_owner_step": "6H", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": "2026-08-26", "resolved_at": None,
    },
    {
        "review_id": "review.6d.professional_overlay_reconciliation",
        "subject_refs": ["domain.containers_cloud_observability", "domain.performance_engineering"],
        "review_codes": ["SHARED_SKILL_REUSE"], "severity": "non_blocking",
        "question": "Observability, reliability ve performance-report capability'leri 6F professional registry ile nasıl reconcile edilecek?",
        "decision_inputs_required": ["6F professional overlay registry"],
        "resolution_owner_step": "6F", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": "2026-08-26", "resolved_at": None,
    },
    {
        "review_id": "review.6d.accelerator_forward_reuse",
        "subject_refs": ["domain.performance_engineering", "domain.concurrency", "domain.networking"],
        "review_codes": ["SHARED_SKILL_REUSE"], "severity": "non_blocking",
        "question": "6E GPU/inference package'ı hangi Systems Skills'i clone'lamadan reuse edecek?",
        "decision_inputs_required": ["6E candidate registry"],
        "resolution_owner_step": "6E", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": "2026-08-26", "resolved_at": None,
    },
]

sources = [
    ("source.repo.pdm_v0", "Curriculum Domain Map", "docs/CURRICULUM_DOMAIN_MAP.md", ["route_coverage"]),
    ("source.repo.kgc_v0", "Curriculum Knowledge Graph Contract", "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md", ["graph_contract"]),
    ("source.repo.gns_v0", "Granularity and Naming Standard", "docs/GRANULARITY_NAMING_STANDARD.md", ["granularity", "logical_identity"]),
    ("source.repo.frdb_v0", "Full-Route Decomposition Blueprint", "docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md", ["package_contract"]),
    ("source.repo.fdm_v0", "Foundations Detailed Map", "docs/FOUNDATIONS_DETAILED_MAP.md", ["prior_package_registry"]),
    ("source.repo.gqa_v0", "Foundation Graph Architecture QA", "docs/GRAPH_ARCHITECTURE_QA.md", ["graph_qa"]),
    ("source.repo.prg_v0", "Prerequisite Readiness Gate", "docs/PREREQUISITE_POLICY_SPEC.md", ["prerequisite_semantics"]),
    ("source.repo.les_v0", "Learning Engine Spec", "docs/LEARNING_ENGINE_SPEC.md", ["capability_semantics"]),
    ("source.repo.internal_6d_authoring", "6D internal canonical-contract synthesis", "docs/SYSTEMS_DETAILED_MAP.md", ["decomposition_authoring"]),
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
    known = set(skill_ids) | base_skills
    check("route_family_partition", {x[1] for x in domains.values()} == {f"D{n:02d}" for n in range(6, 14)}, "D06-D13 exactly")
    check("unique_skill_ids", len(skill_ids) == len(set(skill_ids)), f"{len(skill_ids)} Skills")
    check("no_prior_package_collision", not (set(skill_ids) & base_skills), "no 6C Skill ID reused as a new 6D Skill")
    check("unique_objective_ids", len(objectives) == len(objective_ids), f"{len(objectives)} Objectives")
    check("objective_owner_refs", all(x["owner_skill_id"] in skill_by_id for x in objectives), "all owners resolve")
    check("skill_has_objective", set(skill_ids) <= {x["owner_skill_id"] for x in objectives}, "every Skill has Objective")
    check("topic_refs", all(x["primary_teaching_topic_id"] in topic_ids for x in skills), "all primary Topics resolve")
    check("link_refs", all(x["topic_id"] in topic_ids and x["skill_id"] in known for x in topic_links), "all TopicSkillLinks resolve")
    pairs = {(x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"]) for x in prerequisites}
    check("prerequisite_refs", all(a in known and b in known for a, b, _ in pairs), f"{len(pairs)} edges resolve")
    check("no_self_edges", all(a != b for a, b, _ in pairs), "no self edges")
    check("no_hard_soft_conflict", not any((a, b, "hard") in pairs and (a, b, "soft") in pairs for a, b, _ in pairs), "no pair conflict")
    check("target_is_local", all(b in skill_by_id for _, b, _ in pairs), "6D declares edges only into its own Skills")

    combined = set(base_edges) | pairs
    graph = defaultdict(list)
    indegree = {x: 0 for x in known}
    for a, b, kind in combined:
        if kind == "hard":
            graph[a].append(b)
            indegree[b] += 1
    queue = deque(x for x, degree in indegree.items() if degree == 0)
    visited = 0
    while queue:
        node = queue.popleft()
        visited += 1
        for nxt in graph[node]:
            indegree[nxt] -= 1
            if indegree[nxt] == 0:
                queue.append(nxt)
    check("combined_hard_graph_dag", visited == len(known), f"visited {visited}/{len(known)} across 6C+6D")
    check("english_global_gate", not any(
        a.startswith("skill.english.") and not b.startswith("skill.english.") and k == "hard"
        for a, b, k in combined
    ), "no English hard edge into technical Skills")
    check("cross_package_reuse_declared", all(
        row["seed_id"] in base_skills for row in seed_mappings
    ), f"{len(seed_mappings)} prior-package reuse rows")
    check("reuse_without_clone", all(
        row["result_entity_refs"] == [row["seed_id"]] for row in seed_mappings
    ), "reused Skills keep their canonical identity")
    check("requirement_refs", all(x["capability_id"] in skill_by_id for x in requirements), "requirement refs resolve")
    check("project_attribution_observable", all(
        x["structurally_essential"] and x["separately_observable"] for x in project_attributions
    ), f"{len(project_attributions)} project components separately observable")
    check("soft_override_set_applied", applied_soft_overrides == SOFT_OVERRIDES,
          f"{len(applied_soft_overrides)}/{len(SOFT_OVERRIDES)} Pass B downgrades matched a declared edge")
    check("soft_edges_present", any(k == "soft" for _, _, k in pairs),
          f"{len([x for x in prerequisites if x['edge_kind'] == 'soft'])} soft edges")
    check("blocking_reviews", not any(x["severity"] == "blocking" and x["status"] == "open" for x in reviews), "0 open blocking")
    check("source_paths", all((ROOT / x["ref"]).is_file() for x in source_rows), "all source paths resolve")
    return checks


checks = validate()

cross_package_edges = len([x for x in prerequisites if x["cross_package_ref"]])
manifest = {
    "package_id": "decomposition.6d_systems", "package_version": 1, "stage_step": "6D",
    "status": "authoring_complete_internal_qa", "blueprint_version": "FRDB-v0",
    "graph_contract_version": "KGC-v0", "granularity_standard_version": "GNS-v0",
    "base_graph_refs": ["FDM-v0", "FBB-v0", "GQA-v0"],
    "route_family_ids": [f"D{n:02d}" for n in range(6, 14)],
    "included_collections": [
        "sources", "organization_entities", "skills", "objectives", "topic_skill_links", "prerequisite_edges",
        "capability_requirements", "professional_attributions", "project_capstone_attributions", "seed_mappings",
        "review_queue", "qa_report",
    ],
    "source_catalog_refs": [x["source_id"] for x in source_rows],
    "coverage_declarations": [
        {"route_family_id": route, "status": "internally_mapped", "external_validation": "pending_6H"}
        for route in (f"D{n:02d}" for n in range(6, 14))
    ],
    "prior_package_refs": [{
        "package_id": "decomposition.6c_foundations",
        "reused_skill_count": len(reused_base_skills),
        "cross_package_prerequisite_edges": cross_package_edges,
        "target_ref_status": "resolved_prior_package",
        "blocking_for_current_package_publish": False,
    }],
    "known_exclusions": [
        "GPU, CUDA, Triton, ML/Transformer ve inference capability'leri 6E'ye aittir.",
        "D23 open-source/large-project/capstone decomposition'u 6F'e aittir.",
        "Learner weakness/remediation runtime mapping 6G'ye aittir.",
        "External coverage/current-industry validation 6H'ye aittir.",
        "Production lesson/task/resource body'leri AŞAMA 15/20'ye aittir.",
        "Physical database schema 9C'ye aittir.",
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
dump("capability_requirements.yaml", sorted(requirements, key=lambda x: (x["scope_kind"], x["capability_id"])))
dump("professional_attributions.yaml", sorted(professional, key=lambda x: (x["capability_id"], x["professional_family_id"])))
dump("project_capstone_attributions.yaml", project_attributions)
dump("seed_mappings.yaml", seed_mappings)
dump("review_queue.yaml", reviews)
dump("qa_report.yaml", {
    "package_id": "decomposition.6d_systems", "qa_contract": "FRDB-v0 section 27",
    "result": "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "checked_at": "2026-08-26",
    "counts": {
        "domains": len(domains), "modules": len(module_topics),
        "topics": sum(len(x) for x in module_topics.values()), "skills": len(skills),
        "objectives": len(objectives), "topic_skill_links": len(topic_links),
        "prerequisite_edges": len(prerequisites),
        "hard_prerequisite_edges": len([x for x in prerequisites if x["edge_kind"] == "hard"]),
        "soft_prerequisite_edges": len([x for x in prerequisites if x["edge_kind"] == "soft"]),
        "pass_b_soft_downgrades": len(SOFT_OVERRIDES),
        "cross_package_prerequisite_edges": cross_package_edges,
        "reused_prior_package_skills": len(reused_base_skills),
        "capability_requirements": len(requirements), "professional_attributions": len(professional),
        "project_attributions": len(project_attributions),
        "open_non_blocking_reviews": len(reviews), "open_blocking_reviews": 0,
    },
    "checks": checks,
    "external_research_qa": {"status": "pending", "owner_step": "6H", "required_before_external_validation": True},
})

print(f"Generated {OUT}")
print(f"domains={len(domains)} modules={len(module_topics)} topics={sum(len(x) for x in module_topics.values())}")
print(f"skills={len(skills)} objectives={len(objectives)} topic_skill_links={len(topic_links)}")
print(f"prerequisites={len(prerequisites)} cross_package={cross_package_edges} reused_6c_skills={len(reused_base_skills)}")
print("qa=PASS_WITH_OPEN_NON_BLOCKING_REVIEWS")
