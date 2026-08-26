from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import yaml


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "curriculum" / "decomposition" / "6e_gpu_ml_inference"
FDM = ROOT / "curriculum" / "decomposition" / "6c_foundations"
SDM = ROOT / "curriculum" / "decomposition" / "6d_systems"
TODAY = "2026-08-26"


def load(directory: Path, name: str):
    return yaml.safe_load((directory / name).read_text(encoding="utf-8"))


def dump(directory: Path, name: str, value: object) -> None:
    directory.mkdir(parents=True, exist_ok=True)
    (directory / name).write_text(
        yaml.safe_dump(value, allow_unicode=True, sort_keys=False, width=120),
        encoding="utf-8",
    )


fdm_skills = {x["skill_id_candidate"] for x in load(FDM, "skills.yaml")}
sdm_skills = {x["skill_id_candidate"] for x in load(SDM, "skills.yaml")}
prior_skills = fdm_skills | sdm_skills
prior_edges = [
    (x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"])
    for directory in (FDM, SDM)
    for x in load(directory, "prerequisite_edges.yaml")
]


domains = {
    "domain.gpu_architecture": ("GPU Architecture", "D14", "accelerator_core"),
    "domain.cuda": ("CUDA", "D15", "accelerator_core"),
    "domain.triton": ("Triton", "D16", "accelerator_core"),
    "domain.ml_transformer": ("ML + Transformer Foundations", "D17", "inference_supporting"),
    "domain.llm_inference": ("LLM Inference Internals", "D18", "inference_core"),
    "domain.inference_serving": ("Inference Serving Systems", "D19", "inference_serving_core"),
    "domain.inference_runtime_optimization": ("KV Cache / Batching / Scheduling / Quantization", "D20", "inference_optimization_core"),
    "domain.multi_gpu": ("Multi-GPU + NCCL + RDMA", "D21", "distributed_accelerator_core"),
    "domain.ai_gpu_infrastructure": ("AI Infrastructure / GPU Infrastructure", "D22", "target_integration"),
}


module_topics = {
    "module.gpu.execution_model": [
        "topic.gpu.execution_hierarchy", "topic.gpu.simt_divergence", "topic.gpu.latency_hiding_occupancy",
    ],
    "module.gpu.memory_system": [
        "topic.gpu.memory_spaces", "topic.gpu.coalescing_transactions", "topic.gpu.shared_memory_banks",
        "topic.gpu.cache_locality",
    ],
    "module.gpu.performance_model": [
        "topic.gpu.compute_memory_balance", "topic.gpu.overlap_async_execution", "topic.gpu.profiling_metrics",
    ],
    "module.cuda.programming_model": [
        "topic.cuda.kernel_launch_indexing", "topic.cuda.grid_stride_work", "topic.cuda.synchronization_ordering",
    ],
    "module.cuda.memory_management": [
        "topic.cuda.allocation_transfer", "topic.cuda.shared_memory_tiling", "topic.cuda.pinned_unified_memory",
    ],
    "module.cuda.correctness_performance": [
        "topic.cuda.error_race_bounds", "topic.cuda.streams_events", "topic.cuda.profiling_optimization",
    ],
    "module.triton.programming_model": [
        "topic.triton.program_instance_mapping", "topic.triton.masked_memory_ops",
    ],
    "module.triton.kernel_patterns": [
        "topic.triton.reduction_softmax", "topic.triton.matmul_tiling",
    ],
    "module.triton.optimization_debugging": [
        "topic.triton.autotuning_configs", "topic.triton.correctness_performance_validation",
    ],
    "module.ml.math_numerical": [
        "topic.ml.tensor_shape_broadcast", "topic.ml.linear_algebra_matmul", "topic.ml.probability_softmax",
        "topic.ml.numerical_precision_stability",
    ],
    "module.ml.model_inference_foundations": [
        "topic.ml.parameters_activations", "topic.ml.embedding_normalization",
    ],
    "module.ml.transformer_architecture": [
        "topic.ml.attention_qkv_masking", "topic.ml.transformer_block_causal_lm",
    ],
    "module.inference.execution": [
        "topic.inference.model_loading_layout", "topic.inference.prefill_decode", "topic.inference.autoregressive_generation",
    ],
    "module.inference.attention_generation": [
        "topic.inference.attention_execution", "topic.inference.kv_semantics", "topic.inference.sampling_decoding",
    ],
    "module.inference.operator_runtime": [
        "topic.inference.operator_fusion", "topic.inference.memory_footprint",
    ],
    "module.serving.architecture": [
        "topic.serving.engine_worker_topology", "topic.serving.request_lifecycle", "topic.serving.admission_backpressure",
    ],
    "module.serving.runtime": [
        "topic.serving.streaming_cancellation", "topic.serving.engine_operation",
    ],
    "module.serving.measurement_reliability": [
        "topic.serving.load_benchmark_slo", "topic.serving.failure_observability",
    ],
    "module.optimization.kv_memory": [
        "topic.optimization.kv_layout_capacity", "topic.optimization.paged_prefix_cache",
    ],
    "module.optimization.batching_scheduling": [
        "topic.optimization.continuous_batching", "topic.optimization.token_scheduler",
    ],
    "module.optimization.quantization": [
        "topic.optimization.quantization_formats", "topic.optimization.quantization_validation",
    ],
    "module.multi_gpu.interconnect_collectives": [
        "topic.multi_gpu.topology_interconnect", "topic.multi_gpu.collective_semantics", "topic.multi_gpu.nccl_operation",
    ],
    "module.multi_gpu.rdma_transport": [
        "topic.multi_gpu.rdma_data_path", "topic.multi_gpu.gpu_direct_transport",
    ],
    "module.multi_gpu.distributed_inference": [
        "topic.multi_gpu.parallelism_sharding", "topic.multi_gpu.collective_performance_failure",
    ],
    "module.ai_infra.resource_fleet": [
        "topic.ai_infra.gpu_resource_inventory", "topic.ai_infra.placement_partitioning", "topic.ai_infra.driver_runtime_compatibility",
    ],
    "module.ai_infra.capacity_reliability": [
        "topic.ai_infra.capacity_admission", "topic.ai_infra.gpu_observability", "topic.ai_infra.health_failure_containment",
    ],
    "module.ai_infra.delivery_operations": [
        "topic.ai_infra.model_artifact_rollout", "topic.ai_infra.workload_isolation_security", "topic.ai_infra.cost_efficiency",
    ],
}


module_domain = {}
for module_id in module_topics:
    if module_id.startswith("module.gpu."):
        module_domain[module_id] = "domain.gpu_architecture"
    elif module_id.startswith("module.cuda."):
        module_domain[module_id] = "domain.cuda"
    elif module_id.startswith("module.triton."):
        module_domain[module_id] = "domain.triton"
    elif module_id.startswith("module.ml."):
        module_domain[module_id] = "domain.ml_transformer"
    elif module_id.startswith("module.inference."):
        module_domain[module_id] = "domain.llm_inference"
    elif module_id.startswith("module.serving."):
        module_domain[module_id] = "domain.inference_serving"
    elif module_id.startswith("module.optimization."):
        module_domain[module_id] = "domain.inference_runtime_optimization"
    elif module_id.startswith("module.multi_gpu."):
        module_domain[module_id] = "domain.multi_gpu"
    elif module_id.startswith("module.ai_infra."):
        module_domain[module_id] = "domain.ai_gpu_infrastructure"
    else:
        raise ValueError(module_id)


# sid | topic | capability | kind | retention | hard prereqs | soft prereqs | professional tags | freshness | technologies
SKILL_ROWS = """
skill.gpu.thread_block_grid_model|topic.gpu.execution_hierarchy|GPU thread, block ve grid hiyerarşisini work decomposition ile eşleyebilmek.|systems_reasoning|complex|skill.concurrency.work_decomposition;skill.arch.simd_data_parallel_model||performance_reasoning;systems_design|evergreen|
skill.gpu.warp_execution_model|topic.gpu.execution_hierarchy|Warp tabanlı yürütmeyi lane davranışı ve instruction issue ile açıklayabilmek.|systems_reasoning|complex|skill.gpu.thread_block_grid_model;skill.arch.instruction_execution_model||performance_reasoning|evergreen|
skill.gpu.simt_divergence_reasoning|topic.gpu.simt_divergence|SIMT branch divergence etkisini control-flow deseni üzerinden gerekçelendirebilmek.|systems_reasoning|complex|skill.gpu.warp_execution_model;skill.arch.branch_prediction_cost||performance_reasoning;debugging|evergreen|
skill.gpu.warp_efficiency_measurement|topic.gpu.simt_divergence|Warp execution verimliliğini profiler metriği ve branch yapısıyla ilişkilendirebilmek.|measurement_reasoning|complex|skill.gpu.simt_divergence_reasoning;skill.performance.measurement_validity_reasoning||profiling_benchmarking|version_sensitive|technology.gpu_profiler
skill.gpu.occupancy_resource_model|topic.gpu.latency_hiding_occupancy|Register, shared memory ve resident warp kaynaklarıyla occupancy sınırını hesaplayıp yorumlayabilmek.|systems_reasoning|complex|skill.gpu.warp_execution_model;skill.arch.latency_throughput_distinction||performance_reasoning|evergreen|
skill.gpu.latency_hiding_reasoning|topic.gpu.latency_hiding_occupancy|GPU latency hiding davranışını ready warp ve memory latency ilişkisiyle açıklayabilmek.|systems_reasoning|complex|skill.gpu.occupancy_resource_model;skill.arch.instruction_level_parallelism||performance_reasoning|evergreen|
skill.gpu.memory_space_selection|topic.gpu.memory_spaces|Global, shared, local, constant ve register benzeri memory spaces arasında erişim ve lifetime gerekçesiyle seçim yapabilmek.|design_reasoning|complex|skill.gpu.thread_block_grid_model;skill.arch.cache_hierarchy_model||performance_reasoning;systems_design|evergreen|
skill.gpu.global_memory_latency_bandwidth|topic.gpu.memory_spaces|Global memory erişiminde latency ve bandwidth sınırını ayrı yorumlayabilmek.|systems_reasoning|complex|skill.gpu.memory_space_selection;skill.arch.latency_throughput_distinction||performance_reasoning|evergreen|
skill.gpu.coalesced_access_reasoning|topic.gpu.coalescing_transactions|Warp memory erişim deseninin transaction sayısı ve bandwidth kullanımına etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.gpu.warp_execution_model;skill.arch.locality_reasoning||performance_reasoning|evergreen|
skill.gpu.memory_transaction_measurement|topic.gpu.coalescing_transactions|Memory transaction ve throughput metrikleriyle coalescing hipotezini doğrulayabilmek.|measurement_production|complex|skill.gpu.coalesced_access_reasoning;skill.performance.measurement_validity_reasoning||profiling_benchmarking|version_sensitive|technology.gpu_profiler
skill.gpu.shared_memory_bank_conflict|topic.gpu.shared_memory_banks|Shared-memory bank conflict riskini index deseni üzerinden belirleyebilmek.|systems_reasoning|complex|skill.gpu.memory_space_selection;skill.gpu.warp_execution_model||performance_reasoning;debugging|evergreen|
skill.gpu.shared_memory_layout_transform|topic.gpu.shared_memory_banks|Bank conflict veya reuse amacıyla shared-memory layout dönüşümü tasarlayabilmek.|systems_production|complex|skill.gpu.shared_memory_bank_conflict;skill.arch.data_layout_transformation||performance_reasoning|evergreen|
skill.gpu.cache_locality_reasoning|topic.gpu.cache_locality|GPU cache/locality davranışını workload erişim örüntüsüyle ilişkilendirebilmek.|systems_reasoning|complex|skill.gpu.global_memory_latency_bandwidth;skill.arch.locality_reasoning||performance_reasoning|evergreen|
skill.gpu.cache_metric_interpretation|topic.gpu.cache_locality|GPU cache hit/miss ve throughput metriklerini performans hipotezine bağlayabilmek.|measurement_reasoning|complex|skill.gpu.cache_locality_reasoning;skill.performance.profile_interpretation||profiling_benchmarking|version_sensitive|technology.gpu_profiler
skill.gpu.compute_memory_balance|topic.gpu.compute_memory_balance|GPU kernelini compute-bound veya memory-bound olarak ölçümle sınıflandırabilmek.|measurement_reasoning|complex|skill.arch.roofline_bottleneck_reasoning;skill.gpu.global_memory_latency_bandwidth||performance_reasoning;profiling_benchmarking|evergreen|
skill.gpu.arithmetic_intensity_reasoning|topic.gpu.compute_memory_balance|Arithmetic intensity değişikliğinin roofline konumuna etkisini gerekçelendirebilmek.|systems_reasoning|complex|skill.gpu.compute_memory_balance||performance_reasoning|evergreen|
skill.gpu.copy_compute_overlap_model|topic.gpu.overlap_async_execution|Data transfer ve compute overlap için dependency ve concurrency sınırını açıklayabilmek.|systems_reasoning|complex|skill.concurrency.async_execution_model;skill.gpu.global_memory_latency_bandwidth||performance_reasoning;concurrency|evergreen|
skill.gpu.timeline_overlap_analysis|topic.gpu.overlap_async_execution|GPU timeline üzerinden serialization ve overlap fırsatlarını lokalize edebilmek.|tool_specific_reasoning|complex|skill.gpu.copy_compute_overlap_model;skill.performance.profile_interpretation||profiling_benchmarking;debugging|version_sensitive|technology.gpu_profiler
skill.gpu.profiler_metric_selection|topic.gpu.profiling_metrics|GPU bottleneck hipotezine uygun profiler metric setini seçebilmek.|tool_specific_reasoning|complex|skill.performance.measurement_goal_definition;skill.gpu.compute_memory_balance||profiling_benchmarking|version_sensitive|technology.gpu_profiler
skill.gpu.bottleneck_attribution|topic.gpu.profiling_metrics|Profiler kanıtıyla occupancy, memory, compute veya launch kaynaklı baskın darboğazı ayırabilmek.|measurement_reasoning|complex|skill.gpu.profiler_metric_selection;skill.performance.bottleneck_localization||profiling_benchmarking;debugging|version_sensitive|technology.gpu_profiler
skill.cuda.kernel_execution_mapping|topic.cuda.kernel_launch_indexing|CUDA kernel launch boyutlarını GPU execution hierarchy ile doğru eşleyebilmek.|tool_specific_production|complex|skill.gpu.thread_block_grid_model;skill.c.functions_basic||debugging|version_sensitive|technology.cuda_toolkit
skill.cuda.thread_index_calculation|topic.cuda.kernel_launch_indexing|Thread/block indekslerinden güvenli linear veya multidimensional veri indeksini üretebilmek.|tool_specific_production|complex|skill.cuda.kernel_execution_mapping;skill.programming.iteration_reasoning||debugging|version_sensitive|technology.cuda_toolkit
skill.cuda.grid_stride_loop|topic.cuda.grid_stride_work|Grid-stride loop ile problem boyutundan bağımsız kernel iteration kurabilmek.|tool_specific_production|complex|skill.cuda.thread_index_calculation;skill.programming.iteration_reasoning||performance_reasoning|version_sensitive|technology.cuda_toolkit
skill.cuda.launch_configuration_reasoning|topic.cuda.grid_stride_work|Launch configuration seçimini occupancy, work size ve mapping gerekçesiyle yapabilmek.|design_reasoning|complex|skill.cuda.kernel_execution_mapping;skill.gpu.occupancy_resource_model||performance_reasoning|evergreen|
skill.cuda.block_synchronization|topic.cuda.synchronization_ordering|Block içi synchronization barrier kullanımını data dependency ile doğru konumlandırabilmek.|tool_specific_production|complex|skill.cuda.kernel_execution_mapping;skill.concurrency.shared_mutable_state_risk||concurrency;reliability|version_sensitive|technology.cuda_toolkit
skill.cuda.memory_visibility_ordering|topic.cuda.synchronization_ordering|CUDA memory visibility ve ordering gereksinimini producer-consumer ilişkisi üzerinden gerekçelendirebilmek.|systems_reasoning|complex|skill.cuda.block_synchronization;skill.concurrency.memory_ordering_model||concurrency;operational_safety|evergreen|
skill.cuda.device_allocation_lifetime|topic.cuda.allocation_transfer|Device allocation, ownership ve release lifecycle'ını sızıntısız yönetebilmek.|tool_specific_production|complex|skill.c.dynamic_allocation_lifecycle;skill.gpu.memory_space_selection||reliability|version_sensitive|technology.cuda_toolkit
skill.cuda.host_device_transfer|topic.cuda.allocation_transfer|Host-device transfer yönünü, boyutunu ve synchronization etkisini doğru yönetebilmek.|tool_specific_production|complex|skill.cuda.device_allocation_lifetime;skill.gpu.global_memory_latency_bandwidth||performance_reasoning;reliability|version_sensitive|technology.cuda_toolkit
skill.cuda.shared_memory_tiling|topic.cuda.shared_memory_tiling|CUDA shared-memory tile ile global-memory reuse sağlayan kernel kurabilmek.|tool_specific_production|complex|skill.cuda.block_synchronization;skill.gpu.shared_memory_layout_transform||performance_reasoning|version_sensitive|technology.cuda_toolkit
skill.cuda.tile_boundary_handling|topic.cuda.shared_memory_tiling|Tile sınırlarında out-of-range erişimi engelleyip doğru sonuç koruyabilmek.|tool_specific_production|complex|skill.cuda.shared_memory_tiling;skill.programming.input_validation_reasoning||debugging;reliability|version_sensitive|technology.cuda_toolkit
skill.cuda.pinned_memory_usage|topic.cuda.pinned_unified_memory|Pinned host memory kullanımını transfer overlap ihtiyacıyla gerekçelendirebilmek.|tool_specific_production|complex|skill.cuda.host_device_transfer;skill.gpu.copy_compute_overlap_model||performance_reasoning|version_sensitive|technology.cuda_toolkit
skill.cuda.unified_memory_behavior|topic.cuda.pinned_unified_memory|Unified memory migration/page-fault davranışını workload erişimiyle ilişkilendirebilmek.|tool_specific_reasoning|complex|skill.os.page_fault_behavior;skill.cuda.device_allocation_lifetime||performance_reasoning;debugging|version_sensitive|technology.cuda_toolkit
skill.cuda.error_checking_diagnosis|topic.cuda.error_race_bounds|CUDA launch/runtime hatalarını explicit error checks ile kaynağa lokalize edebilmek.|tool_specific_production|complex|skill.cuda.kernel_execution_mapping;skill.engineering.explain_debug_fix_basic||debugging|version_sensitive|technology.cuda_toolkit
skill.cuda.race_bounds_diagnosis|topic.cuda.error_race_bounds|GPU memory race ve out-of-bounds hatalarını sanitizer/tool kanıtıyla lokalize edebilmek.|tool_specific_production|complex|skill.cuda.memory_visibility_ordering;skill.cuda.thread_index_calculation||debugging;operational_safety|version_sensitive|technology.cuda_toolkit
skill.cuda.stream_dependency_model|topic.cuda.streams_events|Stream ordering ve dependency davranışını host/device timeline üzerinde açıklayabilmek.|systems_reasoning|complex|skill.gpu.copy_compute_overlap_model;skill.cuda.host_device_transfer||concurrency;performance_reasoning|evergreen|
skill.cuda.event_timing_dependency|topic.cuda.streams_events|CUDA event ile timing ve dependency ölçümünü doğru scope'ta kurabilmek.|tool_specific_production|complex|skill.cuda.stream_dependency_model;skill.performance.measurement_validity_reasoning||profiling_benchmarking|version_sensitive|technology.cuda_toolkit
skill.cuda.kernel_profile_collection|topic.cuda.profiling_optimization|Temsil edici CUDA kernel profili ve timeline toplayabilmek.|tool_specific_production|complex|skill.gpu.profiler_metric_selection;skill.performance.measurement_environment_control||profiling_benchmarking|version_sensitive|technology.gpu_profiler;technology.cuda_toolkit
skill.cuda.optimization_validation|topic.cuda.profiling_optimization|CUDA optimizasyonunu correctness-preserving önce/sonra ölçümüyle doğrulayabilmek.|measurement_production|complex|skill.cuda.kernel_profile_collection;skill.performance.optimization_hypothesis_validation||profiling_benchmarking;testing|version_sensitive|technology.cuda_toolkit;technology.gpu_profiler
skill.triton.program_instance_mapping|topic.triton.program_instance_mapping|Triton program instance ve block pointer modelini GPU work decomposition ile eşleyebilmek.|tool_specific_reasoning|complex|skill.cuda.kernel_execution_mapping;skill.gpu.thread_block_grid_model||performance_reasoning|version_sensitive|technology.triton
skill.triton.program_id_indexing|topic.triton.program_instance_mapping|Program ID ve block offset kullanarak veri parçalarını doğru eşleyebilmek.|tool_specific_production|complex|skill.triton.program_instance_mapping;skill.cuda.thread_index_calculation||debugging|version_sensitive|technology.triton
skill.triton.masked_load_store|topic.triton.masked_memory_ops|Masked load/store ile sınır güvenli tensor erişimi kurabilmek.|tool_specific_production|complex|skill.triton.program_id_indexing;skill.programming.input_validation_reasoning||debugging;reliability|version_sensitive|technology.triton
skill.triton.vectorized_memory_layout|topic.triton.masked_memory_ops|Triton block/tensor layout seçimini coalescing ve locality gerekçesiyle yapabilmek.|tool_specific_production|complex|skill.triton.masked_load_store;skill.gpu.coalesced_access_reasoning||performance_reasoning|version_sensitive|technology.triton
skill.triton.reduction_kernel|topic.triton.reduction_softmax|Triton reduction kernelini numerik doğruluk ve paralel decomposition ile kurabilmek.|tool_specific_production|complex|skill.triton.masked_load_store;skill.gpu.latency_hiding_reasoning||performance_reasoning|version_sensitive|technology.triton
skill.triton.softmax_kernel|topic.triton.reduction_softmax|Stable softmax davranışını Triton kernelinde reduction ve normalization ile uygulayabilmek.|tool_specific_production|complex|skill.triton.reduction_kernel;skill.math.softmax_stability||performance_reasoning;reliability|version_sensitive|technology.triton
skill.triton.matmul_tiling|topic.triton.matmul_tiling|Triton matmul kernelini tile decomposition ve memory reuse ile kurabilmek.|tool_specific_production|complex|skill.triton.vectorized_memory_layout;skill.math.matrix_multiplication_reasoning||performance_reasoning|version_sensitive|technology.triton
skill.triton.matmul_performance_reasoning|topic.triton.matmul_tiling|Triton matmul tile/config seçimini compute-memory balance ile gerekçelendirebilmek.|measurement_reasoning|complex|skill.triton.matmul_tiling;skill.gpu.compute_memory_balance||profiling_benchmarking|version_sensitive|technology.triton;technology.gpu_profiler
skill.triton.autotune_search_space|topic.triton.autotuning_configs|Autotune config uzayını workload shape ve hardware constraints ile sınırlandırabilmek.|tool_specific_production|complex|skill.triton.matmul_performance_reasoning;skill.performance.benchmark_workload_design||profiling_benchmarking|fast_moving|technology.triton
skill.triton.autotune_result_validation|topic.triton.autotuning_configs|Autotune sonucunu overfit benchmark yerine temsil edici workload setiyle doğrulayabilmek.|measurement_production|complex|skill.triton.autotune_search_space;skill.performance.regression_detection||profiling_benchmarking;testing|fast_moving|technology.triton
skill.triton.correctness_reference_test|topic.triton.correctness_performance_validation|Triton kernel çıktısını güvenilir reference implementation ve toleransla doğrulayabilmek.|tool_specific_production|complex|skill.triton.masked_load_store;skill.programming.test_case_basic||testing;reproducibility|version_sensitive|technology.triton
skill.triton.profile_compare_cuda|topic.triton.correctness_performance_validation|Triton ve eşdeğer CUDA/reference kernelini aynı measurement contract altında karşılaştırabilmek.|measurement_production|complex|skill.triton.correctness_reference_test;skill.cuda.optimization_validation||profiling_benchmarking;technical_communication|version_sensitive|technology.triton;technology.cuda_toolkit
skill.math.tensor_shape_reasoning|topic.ml.tensor_shape_broadcast|Tensor rank, axis ve shape dönüşümünü operation semantics ile izleyebilmek.|shared_reasoning|complex|skill.dsa.sequence_traversal||systems_design|evergreen|
skill.math.broadcasting_reasoning|topic.ml.tensor_shape_broadcast|Broadcasting uyumluluğunu ve implicit expansion sonucunu shape üzerinden belirleyebilmek.|shared_reasoning|complex|skill.math.tensor_shape_reasoning||debugging|evergreen|
skill.math.matrix_multiplication_reasoning|topic.ml.linear_algebra_matmul|Matrix multiplication boyutlarını, reduction axis'ini ve çıktı shape'ini açıklayabilmek.|shared_reasoning|complex|skill.math.tensor_shape_reasoning;skill.programming.iteration_reasoning||performance_reasoning|evergreen|
skill.math.dot_product_similarity|topic.ml.linear_algebra_matmul|Dot product ve similarity hesabını vector boyutu ve reduction ile ilişkilendirebilmek.|shared_reasoning|standard|skill.math.matrix_multiplication_reasoning||systems_design|evergreen|
skill.math.probability_normalization|topic.ml.probability_softmax|Pozitif score'ları normalize edilmiş dağılıma dönüştürme mantığını açıklayabilmek.|shared_reasoning|complex|skill.math.tensor_shape_reasoning||reliability|evergreen|
skill.math.softmax_stability|topic.ml.probability_softmax|Softmax hesabında max-shift ile overflow/underflow riskini azaltma nedenini açıklayabilmek.|shared_reasoning|complex|skill.math.probability_normalization;skill.arch.number_representation_precision||reliability;performance_reasoning|evergreen|
skill.math.floating_point_error_reasoning|topic.ml.numerical_precision_stability|Floating-point rounding, accumulation ve precision kaybını tensor hesaplarında gerekçelendirebilmek.|shared_reasoning|complex|skill.arch.number_representation_precision||reliability|evergreen|
skill.math.mixed_precision_tradeoff|topic.ml.numerical_precision_stability|Düşük precision kullanımında hız, memory ve numerical error dengesini gerekçelendirebilmek.|design_reasoning|complex|skill.math.floating_point_error_reasoning;skill.performance.measurement_validity_reasoning||performance_reasoning;reliability|evergreen|
skill.ml.parameter_activation_distinction|topic.ml.parameters_activations|Model parameter, activation ve intermediate tensor rollerini inference execution içinde ayırt edebilmek.|systems_reasoning|standard|skill.math.tensor_shape_reasoning||systems_design|evergreen|
skill.ml.inference_forward_pass|topic.ml.parameters_activations|Training state güncellemesi olmadan forward inference veri akışını açıklayabilmek.|systems_reasoning|complex|skill.ml.parameter_activation_distinction;skill.math.matrix_multiplication_reasoning||systems_design|evergreen|
skill.ml.embedding_lookup_reasoning|topic.ml.embedding_normalization|Token/id embedding lookup'unu table shape ve gather davranışıyla açıklayabilmek.|systems_reasoning|complex|skill.math.tensor_shape_reasoning||performance_reasoning|evergreen|
skill.ml.normalization_layer_reasoning|topic.ml.embedding_normalization|Normalization katmanının axis ve numerical davranışını inference bağlamında açıklayabilmek.|systems_reasoning|complex|skill.math.floating_point_error_reasoning;skill.math.tensor_shape_reasoning||reliability|evergreen|
skill.ml.attention_qkv_shapes|topic.ml.attention_qkv_masking|Q/K/V projection ve head reshape boyutlarını tutarlı biçimde izleyebilmek.|systems_reasoning|complex|skill.math.matrix_multiplication_reasoning;skill.math.tensor_shape_reasoning||debugging;systems_design|evergreen|
skill.ml.attention_score_mask_softmax|topic.ml.attention_qkv_masking|Scaled dot-product attention score, mask ve softmax zincirini shape/numerical doğrulukla açıklayabilmek.|systems_reasoning|complex|skill.ml.attention_qkv_shapes;skill.math.softmax_stability;skill.math.dot_product_similarity||reliability;performance_reasoning|evergreen|
skill.ml.transformer_block_dataflow|topic.ml.transformer_block_causal_lm|Attention, residual, normalization ve MLP bileşenleri arasındaki transformer block dataflow'unu izleyebilmek.|systems_reasoning|complex|skill.ml.attention_score_mask_softmax;skill.ml.normalization_layer_reasoning||systems_design|evergreen|
skill.ml.causal_lm_logits_next_token|topic.ml.transformer_block_causal_lm|Causal LM hidden state'ten logits ve next-token distribution üretimini açıklayabilmek.|systems_reasoning|complex|skill.ml.transformer_block_dataflow;skill.math.probability_normalization||systems_design|evergreen|
skill.inference.weight_loading_layout|topic.inference.model_loading_layout|Model weight shard, dtype ve device placement'ını inference memory planına doğru yerleştirebilmek.|systems_production|complex|skill.ml.parameter_activation_distinction;skill.gpu.memory_space_selection||reliability;performance_reasoning|evergreen|
skill.inference.weight_memory_estimation|topic.inference.model_loading_layout|Parameter count ve dtype üzerinden model weight memory footprint'ünü hesaplayabilmek.|measurement_reasoning|complex|skill.inference.weight_loading_layout;skill.math.mixed_precision_tradeoff||performance_reasoning|evergreen|
skill.inference.prefill_decode_distinction|topic.inference.prefill_decode|Prefill ve decode fazlarını compute shape, parallelism ve latency profiliyle ayırt edebilmek.|systems_reasoning|complex|skill.ml.causal_lm_logits_next_token;skill.arch.latency_throughput_distinction||performance_reasoning|evergreen|
skill.inference.prefill_decode_profile|topic.inference.prefill_decode|Prefill/decode zaman ve kernel dağılımını ayrı ölçüp bottleneck çıkarabilmek.|measurement_production|complex|skill.inference.prefill_decode_distinction;skill.gpu.bottleneck_attribution||profiling_benchmarking|version_sensitive|technology.inference_runtime;technology.gpu_profiler
skill.inference.autoregressive_loop|topic.inference.autoregressive_generation|Autoregressive generation döngüsünde token state, stopping ve repeated model invocation akışını açıklayabilmek.|systems_reasoning|complex|skill.ml.causal_lm_logits_next_token;skill.programming.iteration_reasoning||systems_design|evergreen|
skill.inference.generation_stopping_state|topic.inference.autoregressive_generation|EOS, max-token ve request cancellation stopping koşullarını güvenilir state olarak yönetebilmek.|systems_production|complex|skill.inference.autoregressive_loop;skill.concurrency.cancellation_timeout_semantics||reliability|evergreen|
skill.inference.attention_operator_execution|topic.inference.attention_execution|Attention operatorunun matmul, mask, softmax ve value aggregation execution aşamalarını kernel/memory davranışıyla eşleyebilmek.|systems_reasoning|complex|skill.ml.attention_score_mask_softmax;skill.gpu.compute_memory_balance||performance_reasoning|evergreen|
skill.inference.attention_memory_bottleneck|topic.inference.attention_execution|Attention workload'unda sequence length ve memory traffic etkisini ölçümle gerekçelendirebilmek.|measurement_reasoning|complex|skill.inference.attention_operator_execution;skill.performance.bottleneck_localization||performance_reasoning;profiling_benchmarking|evergreen|
skill.inference.kv_cache_semantics|topic.inference.kv_semantics|Autoregressive decode sırasında K/V state reuse mantığını sequence/request sınırlarıyla açıklayabilmek.|systems_reasoning|complex|skill.inference.autoregressive_loop;skill.ml.attention_qkv_shapes||systems_design|evergreen|
skill.inference.kv_memory_growth|topic.inference.kv_semantics|KV memory growth'ünü layers, heads, sequence ve dtype üzerinden tahmin edebilmek.|measurement_reasoning|complex|skill.inference.kv_cache_semantics;skill.math.tensor_shape_reasoning;skill.math.mixed_precision_tradeoff||performance_reasoning|evergreen|
skill.inference.sampling_temperature|topic.inference.sampling_decoding|Temperature değişiminin logits distribution ve sampling davranışına etkisini açıklayabilmek.|systems_reasoning|standard|skill.ml.causal_lm_logits_next_token;skill.math.probability_normalization||systems_design|evergreen|
skill.inference.topk_topp_sampling|topic.inference.sampling_decoding|Top-k ve nucleus sampling aday kümesini doğru oluşturup örnekleyebilmek.|systems_production|complex|skill.inference.sampling_temperature;skill.dsa.sequence_traversal||reliability|evergreen|
skill.inference.operator_fusion_reasoning|topic.inference.operator_fusion|Operator fusion'ın launch overhead, intermediate memory ve locality etkisini gerekçelendirebilmek.|design_reasoning|complex|skill.gpu.compute_memory_balance;skill.performance.bottleneck_localization||performance_reasoning|evergreen|
skill.inference.fusion_correctness_validation|topic.inference.operator_fusion|Fused operator sonucunu reference path ile numerical tolerance altında doğrulayabilmek.|measurement_production|complex|skill.inference.operator_fusion_reasoning;skill.math.floating_point_error_reasoning||testing;profiling_benchmarking|evergreen|
skill.inference.runtime_memory_budget|topic.inference.memory_footprint|Weights, activations, KV ve workspace bileşenlerini ayrı memory budget olarak hesaplayabilmek.|measurement_reasoning|complex|skill.inference.weight_memory_estimation;skill.inference.kv_memory_growth||performance_reasoning;systems_design|evergreen|
skill.inference.oom_root_cause_diagnosis|topic.inference.memory_footprint|Inference OOM olayını weight, KV, batch veya workspace baskısına lokalize edebilmek.|professional_workflow|complex|skill.inference.runtime_memory_budget;skill.os.memory_usage_diagnosis||debugging;reliability|evergreen|
skill.serving.worker_engine_topology|topic.serving.engine_worker_topology|Frontend, scheduler, model worker ve device execution rollerini serving topology içinde ayırabilmek.|systems_reasoning|complex|skill.inference.autoregressive_loop;skill.distributed.partial_failure_model||systems_design|evergreen|
skill.serving.worker_resource_binding|topic.serving.engine_worker_topology|Serving worker'larını CPU/GPU resource ve process sınırlarıyla doğru bağlayabilmek.|systems_production|complex|skill.serving.worker_engine_topology;skill.os.process_address_space_model||reliability;performance_reasoning|evergreen|
skill.serving.request_state_lifecycle|topic.serving.request_lifecycle|Request queue, active generation, completion ve cancellation state'lerini izleyebilmek.|systems_reasoning|complex|skill.serving.worker_engine_topology;skill.inference.generation_stopping_state||reliability|evergreen|
skill.serving.streaming_response_semantics|topic.serving.request_lifecycle|Streaming token response ve client disconnect davranışını güvenilir biçimde yönetebilmek.|systems_production|complex|skill.serving.request_state_lifecycle;skill.network.rpc_call_semantics||reliability|evergreen|
skill.serving.admission_control|topic.serving.admission_backpressure|Memory/token capacity'ye göre yeni request admission kararını verebilmek.|design_reasoning|complex|skill.inference.runtime_memory_budget;skill.distributed.backpressure_reasoning||reliability;performance_reasoning|evergreen|
skill.serving.queue_backpressure|topic.serving.admission_backpressure|Queue büyümesi ve overload durumunda backpressure davranışını kurabilmek.|systems_production|complex|skill.serving.admission_control;skill.distributed.backpressure_reasoning||reliability|evergreen|
skill.serving.stream_cancellation_cleanup|topic.serving.streaming_cancellation|Client cancellation sonrası scheduler/KV/request kaynaklarını güvenli temizleyebilmek.|systems_production|complex|skill.serving.streaming_response_semantics;skill.concurrency.cancellation_timeout_semantics||reliability;debugging|evergreen|
skill.serving.timeout_deadline_propagation|topic.serving.streaming_cancellation|Request deadline ve timeout'u serving katmanları boyunca tutarlı taşımak.|systems_production|complex|skill.serving.request_state_lifecycle;skill.network.timeout_retry_policy||reliability|evergreen|
skill.serving.engine_configuration_reasoning|topic.serving.engine_operation|Serving engine concurrency, memory ve execution ayarlarını stable resource model üzerinden gerekçelendirebilmek.|design_reasoning|complex|skill.serving.admission_control;skill.inference.runtime_memory_budget||systems_design;performance_reasoning|evergreen|
skill.serving.vllm_runtime_operation|topic.serving.engine_operation|vLLM runtime'ını documented model/load/serve/observe akışıyla çalıştırıp davranışı doğrulayabilmek.|tool_specific_production|complex|skill.serving.engine_configuration_reasoning;skill.linux.process_inspection||build_tooling;observability|fast_moving|technology.vllm
skill.serving.sglang_runtime_operation|topic.serving.engine_operation|SGLang runtime'ını documented model/load/serve/observe akışıyla çalıştırıp davranışı doğrulayabilmek.|tool_specific_production|complex|skill.serving.engine_configuration_reasoning;skill.linux.process_inspection||build_tooling;observability|fast_moving|technology.sglang
skill.serving.tensorrt_llm_runtime_operation|topic.serving.engine_operation|TensorRT-LLM runtime'ını build/load/serve/observe akışıyla çalıştırıp davranışı doğrulayabilmek.|tool_specific_production|complex|skill.serving.engine_configuration_reasoning;skill.cuda.kernel_execution_mapping||build_tooling;observability|fast_moving|technology.tensorrt_llm
skill.serving.load_generation_methodology|topic.serving.load_benchmark_slo|Request mix, prompt/decode length ve concurrency dağılımını temsil eden load test tasarlayabilmek.|measurement_production|complex|skill.performance.benchmark_workload_design;skill.serving.request_state_lifecycle||profiling_benchmarking;reproducibility|evergreen|
skill.serving.latency_throughput_slo_analysis|topic.serving.load_benchmark_slo|TTFT, inter-token latency, end-to-end latency ve throughput metriklerini birlikte analiz edebilmek.|measurement_reasoning|complex|skill.serving.load_generation_methodology;skill.performance.tail_latency_reasoning||performance_reasoning;reliability|evergreen|
skill.serving.request_trace_correlation|topic.serving.failure_observability|Request ID üzerinden frontend, scheduler ve worker log/metric/trace sinyallerini korele edebilmek.|professional_workflow|complex|skill.observability.signal_correlation_diagnosis;skill.serving.request_state_lifecycle||observability;debugging|evergreen|
skill.serving.failure_domain_diagnosis|topic.serving.failure_observability|Serving arızasını request, scheduler, model worker, GPU veya network failure domain'ine lokalize edebilmek.|professional_workflow|complex|skill.serving.request_trace_correlation;skill.distributed.partial_failure_model||debugging;reliability|evergreen|
skill.optimization.kv_capacity_model|topic.optimization.kv_layout_capacity|KV cache capacity'yi block/page layout, dtype ve sequence workload üzerinden modelleyebilmek.|measurement_reasoning|complex|skill.inference.kv_memory_growth;skill.inference.runtime_memory_budget||performance_reasoning|evergreen|
skill.optimization.kv_fragmentation_reasoning|topic.optimization.kv_layout_capacity|KV allocation fragmentation ve unusable capacity etkisini workload değişimiyle açıklayabilmek.|systems_reasoning|complex|skill.optimization.kv_capacity_model;skill.os.allocator_behavior_model||performance_reasoning|evergreen|
skill.optimization.paged_kv_allocation|topic.optimization.paged_prefix_cache|Paged KV allocation ile variable-length request memory kullanımını yönetebilmek.|systems_production|complex|skill.optimization.kv_fragmentation_reasoning;skill.os.virtual_physical_translation||systems_design;performance_reasoning|evergreen|
skill.optimization.prefix_cache_reuse|topic.optimization.paged_prefix_cache|Prefix cache reuse için key/equivalence ve invalidation sınırını güvenilir kurabilmek.|systems_production|complex|skill.optimization.paged_kv_allocation;skill.storage.index_selection_reasoning||reliability;performance_reasoning|evergreen|
skill.optimization.continuous_batching_reasoning|topic.optimization.continuous_batching|Request'leri decode adımları arasında dinamik batch'e alma mantığını açıklayabilmek.|systems_reasoning|complex|skill.serving.request_state_lifecycle;skill.inference.prefill_decode_distinction||performance_reasoning;systems_design|evergreen|
skill.optimization.batch_composition_tradeoff|topic.optimization.continuous_batching|Batch composition kararını throughput, latency ve memory baskısı arasında gerekçelendirebilmek.|design_reasoning|complex|skill.optimization.continuous_batching_reasoning;skill.performance.tail_latency_reasoning||performance_reasoning|evergreen|
skill.optimization.token_budget_scheduler|topic.optimization.token_scheduler|Prefill/decode token budget kullanarak capacity-aware scheduling kararı verebilmek.|systems_production|complex|skill.optimization.continuous_batching_reasoning;skill.serving.admission_control||systems_design;performance_reasoning|evergreen|
skill.optimization.scheduler_fairness_preemption|topic.optimization.token_scheduler|Scheduler fairness, starvation ve preemption trade-off'larını request sınıfları üzerinde gerekçelendirebilmek.|design_reasoning|complex|skill.optimization.token_budget_scheduler;skill.concurrency.scaling_limit_reasoning||reliability;performance_reasoning|evergreen|
skill.optimization.quantization_format_reasoning|topic.optimization.quantization_formats|Weight/activation/KV quantization formatlarını memory, compute ve accuracy etkisiyle ayırabilmek.|design_reasoning|complex|skill.math.mixed_precision_tradeoff;skill.ml.parameter_activation_distinction||performance_reasoning;reliability|evergreen|
skill.optimization.quantized_memory_estimation|topic.optimization.quantization_formats|Quantized weights/KV için gerçek memory footprint ve packing overhead'ini hesaplayabilmek.|measurement_reasoning|complex|skill.optimization.quantization_format_reasoning;skill.inference.runtime_memory_budget||performance_reasoning|evergreen|
skill.optimization.quantization_quality_validation|topic.optimization.quantization_validation|Quantized model çıktısını representative quality/equivalence ölçümüyle doğrulayabilmek.|measurement_production|complex|skill.optimization.quantization_format_reasoning;skill.performance.measurement_validity_reasoning||testing;reliability|evergreen|
skill.optimization.quantization_performance_validation|topic.optimization.quantization_validation|Quantization speed/memory iddiasını aynı workload ve correctness gate altında benchmark edebilmek.|measurement_production|complex|skill.optimization.quantization_quality_validation;skill.performance.optimization_hypothesis_validation||profiling_benchmarking|evergreen|
skill.multi_gpu.topology_bandwidth_model|topic.multi_gpu.topology_interconnect|PCIe/NVLink-benzeri interconnect topology ve link bandwidth sınırlarını workload communication ile eşleyebilmek.|systems_reasoning|complex|skill.network.latency_bandwidth_budget;skill.gpu.global_memory_latency_bandwidth||performance_reasoning;systems_design|evergreen|
skill.multi_gpu.topology_discovery|topic.multi_gpu.topology_interconnect|GPU/NUMA/interconnect topology'yi araç çıktısıyla çıkarıp placement kararına çevirebilmek.|tool_specific_production|complex|skill.multi_gpu.topology_bandwidth_model;skill.os.cpu_time_accounting||profiling_benchmarking|version_sensitive|technology.gpu_runtime
skill.multi_gpu.collective_semantics|topic.multi_gpu.collective_semantics|All-reduce, all-gather ve reduce-scatter kolektiflerinin veri dönüşüm semantiğini ayırt edebilmek.|systems_reasoning|complex|skill.distributed.replication_models;skill.math.tensor_shape_reasoning||systems_design|evergreen|
skill.multi_gpu.collective_cost_reasoning|topic.multi_gpu.collective_semantics|Collective maliyetini message size, topology ve synchronization üzerinden gerekçelendirebilmek.|measurement_reasoning|complex|skill.multi_gpu.collective_semantics;skill.multi_gpu.topology_bandwidth_model||performance_reasoning|evergreen|
skill.multi_gpu.nccl_collective_operation|topic.multi_gpu.nccl_operation|NCCL collective operation'ını communicator/rank/buffer lifecycle ile doğru çalıştırabilmek.|tool_specific_production|complex|skill.multi_gpu.collective_semantics;skill.cuda.stream_dependency_model||reliability|version_sensitive|technology.nccl
skill.multi_gpu.nccl_debug_trace|topic.multi_gpu.nccl_operation|NCCL initialization/collective hang veya transport hatasını log/topology kanıtıyla lokalize edebilmek.|tool_specific_production|complex|skill.multi_gpu.nccl_collective_operation;skill.network.network_failure_diagnosis||debugging;observability|version_sensitive|technology.nccl
skill.multi_gpu.rdma_transport_model|topic.multi_gpu.rdma_data_path|RDMA queue-pair, registration ve one-sided/data-path kavramlarını distributed GPU transfer bağlamında açıklayabilmek.|systems_reasoning|complex|skill.network.layering_model;skill.os.memory_mapping_usage||systems_design|evergreen|
skill.multi_gpu.rdma_path_validation|topic.multi_gpu.rdma_data_path|RDMA path ve bandwidth davranışını host/network ölçümleriyle doğrulayabilmek.|tool_specific_production|complex|skill.multi_gpu.rdma_transport_model;skill.network.network_failure_diagnosis||profiling_benchmarking;debugging|version_sensitive|technology.rdma_stack
skill.multi_gpu.gpu_direct_data_path|topic.multi_gpu.gpu_direct_transport|GPU-direct transport ile host staging arasındaki data path farkını açıklayabilmek.|systems_reasoning|complex|skill.multi_gpu.rdma_transport_model;skill.cuda.host_device_transfer||performance_reasoning|evergreen|
skill.multi_gpu.gpu_direct_validation|topic.multi_gpu.gpu_direct_transport|GPU-direct path kullanımını topology, counters ve transfer benchmark ile doğrulayabilmek.|measurement_production|complex|skill.multi_gpu.gpu_direct_data_path;skill.multi_gpu.rdma_path_validation||profiling_benchmarking|version_sensitive|technology.rdma_stack;technology.gpu_runtime
skill.multi_gpu.tensor_parallel_sharding|topic.multi_gpu.parallelism_sharding|Tensor-parallel shard sınırlarını matmul dimension ve collective gereksinimiyle kurabilmek.|systems_reasoning|complex|skill.math.matrix_multiplication_reasoning;skill.multi_gpu.collective_semantics||systems_design;performance_reasoning|evergreen|
skill.multi_gpu.pipeline_parallel_partition|topic.multi_gpu.parallelism_sharding|Pipeline stage partition'ını layer workload ve inter-stage transfer gerekçesiyle yapabilmek.|design_reasoning|complex|skill.ml.transformer_block_dataflow;skill.multi_gpu.topology_bandwidth_model||systems_design;performance_reasoning|evergreen|
skill.multi_gpu.distributed_inference_placement|topic.multi_gpu.parallelism_sharding|Tensor/pipeline shard'ları device/topology üzerinde communication-aware yerleştirebilmek.|design_reasoning|complex|skill.multi_gpu.tensor_parallel_sharding;skill.multi_gpu.pipeline_parallel_partition||systems_design;performance_reasoning|evergreen|
skill.multi_gpu.collective_profile_analysis|topic.multi_gpu.collective_performance_failure|Collective timeline ve bandwidth ölçümünden communication bottleneck'i lokalize edebilmek.|measurement_reasoning|complex|skill.multi_gpu.collective_cost_reasoning;skill.performance.end_to_end_latency_attribution||profiling_benchmarking|version_sensitive|technology.nccl;technology.gpu_profiler
skill.multi_gpu.distributed_failure_isolation|topic.multi_gpu.collective_performance_failure|Multi-node inference failure'ını rank, transport, topology veya workload katmanına lokalize edebilmek.|professional_workflow|complex|skill.multi_gpu.nccl_debug_trace;skill.distributed.partial_failure_model||debugging;reliability|evergreen|
skill.ai_infra.gpu_capability_inventory|topic.ai_infra.gpu_resource_inventory|GPU model, memory, compute capability ve interconnect özelliklerini schedulable resource inventory'ye dönüştürebilmek.|systems_production|complex|skill.multi_gpu.topology_discovery;skill.platform.cloud_compute_resource_model||systems_design;reproducibility|version_sensitive|technology.gpu_runtime
skill.ai_infra.workload_resource_profile|topic.ai_infra.gpu_resource_inventory|Inference workload'un GPU memory, compute, network ve latency ihtiyacını resource profile olarak çıkarabilmek.|measurement_reasoning|complex|skill.inference.runtime_memory_budget;skill.serving.latency_throughput_slo_analysis||capacity_planning;performance_reasoning|evergreen|
skill.ai_infra.topology_aware_placement|topic.ai_infra.placement_partitioning|Distributed inference workload'unu GPU/interconnect topology ve failure-domain kısıtlarıyla yerleştirebilmek.|design_reasoning|complex|skill.ai_infra.gpu_capability_inventory;skill.multi_gpu.distributed_inference_placement||systems_design;reliability|evergreen|
skill.ai_infra.gpu_partitioning_isolation|topic.ai_infra.placement_partitioning|GPU partitioning/multi-tenancy seçimini isolation, utilization ve compatibility gerekçesiyle yapabilmek.|design_reasoning|complex|skill.ai_infra.workload_resource_profile;skill.platform.container_vs_vm_boundary||operational_safety;performance_reasoning|version_sensitive|technology.gpu_partitioning
skill.ai_infra.driver_toolkit_compatibility|topic.ai_infra.driver_runtime_compatibility|Driver, GPU runtime, CUDA toolkit ve serving runtime compatibility zincirini doğrulayabilmek.|tool_specific_production|complex|skill.ai_infra.gpu_capability_inventory;skill.platform.deployment_change_safety||reliability;build_tooling|fast_moving|technology.gpu_driver;technology.cuda_toolkit;technology.inference_runtime
skill.ai_infra.reproducible_runtime_environment|topic.ai_infra.driver_runtime_compatibility|GPU workload runtime environment'ını version-pinned ve tekrar üretilebilir paketleyebilmek.|professional_workflow|complex|skill.ai_infra.driver_toolkit_compatibility;skill.engineering.reproducible_run_notes||reproducibility;build_tooling|version_sensitive|technology.gpu_driver;technology.container_platform
skill.ai_infra.capacity_headroom_model|topic.ai_infra.capacity_admission|GPU fleet capacity, saturation ve headroom'u request/workload dağılımıyla modelleyebilmek.|measurement_reasoning|complex|skill.ai_infra.workload_resource_profile;skill.performance.capacity_headroom_reasoning||capacity_planning;reliability|evergreen|
skill.ai_infra.cluster_admission_policy|topic.ai_infra.capacity_admission|Yeni model/workload admission kararını capacity, SLO ve fragmentation riskine göre verebilmek.|design_reasoning|complex|skill.ai_infra.capacity_headroom_model;skill.serving.admission_control||reliability;systems_design|evergreen|
skill.ai_infra.gpu_telemetry_instrumentation|topic.ai_infra.gpu_observability|GPU utilization, memory, errors ve thermal/power sinyallerini anlamlı telemetry olarak toplamak.|tool_specific_production|complex|skill.observability.metric_instrumentation;skill.ai_infra.gpu_capability_inventory||observability;reliability|version_sensitive|technology.gpu_telemetry
skill.ai_infra.gpu_signal_correlation|topic.ai_infra.gpu_observability|GPU telemetry, serving metrics ve request trace sinyallerini tek performance/failure anlatısında birleştirebilmek.|professional_workflow|complex|skill.ai_infra.gpu_telemetry_instrumentation;skill.serving.request_trace_correlation||observability;debugging|evergreen|
skill.ai_infra.gpu_health_failure_containment|topic.ai_infra.health_failure_containment|GPU/device fault durumunda unhealthy resource'u workload'tan izole edip kontrollü recovery başlatabilmek.|professional_workflow|complex|skill.ai_infra.gpu_signal_correlation;skill.reliability.incident_response_workflow||reliability;operational_safety|evergreen|
skill.ai_infra.failure_domain_capacity_recovery|topic.ai_infra.health_failure_containment|Node/device kaybı sonrası remaining capacity ve placement'i SLO etkisiyle yeniden değerlendirebilmek.|design_reasoning|complex|skill.ai_infra.gpu_health_failure_containment;skill.distributed.partial_failure_model||reliability;capacity_planning|evergreen|
skill.ai_infra.model_artifact_versioning|topic.ai_infra.model_artifact_rollout|Model weights, tokenizer/config ve runtime compatibility metadata'sını immutable release artifact olarak versionlayabilmek.|professional_workflow|complex|skill.inference.weight_loading_layout;skill.platform.image_supply_chain_safety||reproducibility;operational_safety|evergreen|
skill.ai_infra.model_rollout_validation|topic.ai_infra.model_artifact_rollout|Model/runtime rollout'unu quality, latency, error ve rollback kriterleriyle doğrulayabilmek.|professional_workflow|complex|skill.ai_infra.model_artifact_versioning;skill.platform.deployment_change_safety;skill.performance.regression_detection||reliability;testing|evergreen|
skill.ai_infra.workload_isolation_boundary|topic.ai_infra.workload_isolation_security|Multi-tenant GPU workload'larında process/container/device erişim sınırını explicit kurabilmek.|design_reasoning|complex|skill.ai_infra.gpu_partitioning_isolation;skill.platform.secret_handling_safety||operational_safety;systems_design|evergreen|
skill.ai_infra.model_data_provenance_safety|topic.ai_infra.workload_isolation_security|Model artifact, input/output ve secret provenance risklerini deployment workflow'unda kontrol edebilmek.|professional_workflow|complex|skill.ai_infra.model_artifact_versioning;skill.platform.image_supply_chain_safety||operational_safety;reproducibility|evergreen|
skill.ai_infra.cost_per_token_reasoning|topic.ai_infra.cost_efficiency|GPU-hours, utilization ve served-token ölçümlerinden cost-per-token davranışını hesaplayıp yorumlayabilmek.|measurement_reasoning|complex|skill.ai_infra.capacity_headroom_model;skill.performance.cost_efficiency_reasoning||performance_reasoning;technical_communication|evergreen|
skill.ai_infra.efficiency_optimization_validation|topic.ai_infra.cost_efficiency|Infra efficiency değişikliğini SLO ve quality gate'leri korunarak önce/sonra ölçümüyle doğrulayabilmek.|measurement_production|complex|skill.ai_infra.cost_per_token_reasoning;skill.performance.optimization_hypothesis_validation||profiling_benchmarking;reliability|evergreen|
""".strip().splitlines()


EVIDENCE_BY_KIND = {
    "shared_reasoning": ["explanation", "artifact_analysis"],
    "design_reasoning": ["design_argument", "explanation"],
    "systems_reasoning": ["system_observation", "explanation"],
    "systems_production": ["authored_code", "observed_system_behavior"],
    "tool_specific_production": ["hands_on_system_task", "observed_result"],
    "tool_specific_reasoning": ["artifact_analysis", "explanation"],
    "measurement_production": ["measurement_artifact", "reproducible_benchmark"],
    "measurement_reasoning": ["artifact_analysis", "explanation"],
    "professional_workflow": ["hands_on_workflow", "artifact_analysis"],
}

SHARED_KINDS = {
    "shared_reasoning", "design_reasoning", "systems_reasoning", "systems_production",
    "measurement_production", "measurement_reasoning", "professional_workflow",
}

CRITICAL_SKILLS = {
    "skill.gpu.thread_block_grid_model", "skill.gpu.warp_execution_model", "skill.gpu.memory_space_selection",
    "skill.cuda.kernel_execution_mapping", "skill.cuda.thread_index_calculation", "skill.cuda.race_bounds_diagnosis",
    "skill.math.tensor_shape_reasoning", "skill.math.matrix_multiplication_reasoning", "skill.math.softmax_stability",
    "skill.ml.attention_score_mask_softmax", "skill.inference.prefill_decode_distinction",
    "skill.inference.kv_cache_semantics", "skill.inference.runtime_memory_budget",
    "skill.serving.request_state_lifecycle", "skill.serving.admission_control",
    "skill.optimization.continuous_batching_reasoning", "skill.optimization.token_budget_scheduler",
    "skill.multi_gpu.collective_semantics", "skill.multi_gpu.nccl_collective_operation",
    "skill.ai_infra.driver_toolkit_compatibility", "skill.ai_infra.gpu_health_failure_containment",
}


def split_ids(value: str) -> list[str]:
    return [x for x in value.split(";") if x]


def parse_skills() -> list[dict]:
    result: list[dict] = []
    declared_local = {line.split("|", 1)[0] for line in SKILL_ROWS}
    for line in SKILL_ROWS:
        sid, topic, statement, kind, retention, hard, soft, tags, freshness, technologies = line.split("|")
        domain = next(module_domain[mid] for mid, topics in module_topics.items() if topic in topics)
        hard_ids, soft_ids = split_ids(hard), split_ids(soft)
        missing = [x for x in hard_ids + soft_ids if x not in prior_skills and x not in declared_local]
        if missing:
            raise ValueError(f"{sid} references prerequisite(s) before declaration or outside prior registry: {missing}")
        evidence = EVIDENCE_BY_KIND[kind]
        tag_list = split_ids(tags)
        tech_list = split_ids(technologies)
        if freshness != "evergreen" and not tech_list:
            raise ValueError(f"{sid}: non-evergreen Skill needs technology dependency")
        depth = ["independent_application", "delayed_retention"]
        if "debugging" in tag_list:
            depth.append("debugging")
        if "profiling_benchmarking" in tag_list or kind.startswith("measurement"):
            depth.append("performance_measurement")
        if kind in {"shared_reasoning", "systems_reasoning", "design_reasoning", "measurement_reasoning"}:
            depth.append("transfer")
        if sid.startswith(("skill.serving.", "skill.multi_gpu.", "skill.ai_infra.")):
            depth.append("production_context")
        compared = sorted(x for x in hard_ids + soft_ids if x in prior_skills)
        row = {
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
                "remediation_boundary": "Failure is remediated at this exact capability, not by resetting the broad accelerator/inference Domain.",
                "reuse_boundary": "Prior Foundation/Systems capability is reused by canonical ID; context-only placement uses TopicSkillLink.",
            },
            "shared_vs_specific": {
                "classification": "shared" if kind in SHARED_KINDS and not kind.startswith("tool_specific") else "domain_or_tool_specific",
                "rationale": (
                    "Stable reasoning/engineering capability is reusable across accelerator and inference contexts."
                    if kind in SHARED_KINDS and not kind.startswith("tool_specific")
                    else "Tool/runtime behavior changes the observable production boundary and therefore remains version/freshness scoped."
                ),
            },
            "evidence_depth_expectations": sorted(set(depth)),
            "retention_profile": retention,
            "diagnostic_eligibility": "eligible",
            "critical_prerequisite_candidate": sid in CRITICAL_SKILLS,
            "remediation_tags": sorted(set(
                ["targeted_reteach", "fresh_variant"]
                + (["debug_localization"] if "debugging" in tag_list else [])
                + (["measurement_replay"] if "profiling_benchmarking" in tag_list or kind.startswith("measurement") else [])
                + (["failure_scenario_replay"] if "reliability" in tag_list else [])
            )),
            "professional_capability_tags": tag_list,
            "project_capability_tags": [],
            "source_refs": ["source.repo.pdm_v0", "source.repo.frdb_v0", "source.repo.gns_v0"],
            "provenance_ref": "source.repo.internal_6e_authoring",
            "freshness_class": freshness,
            "technology_dependency_refs": tech_list,
            "duplicate_resolution": {
                "status": "create_new",
                "compared_skill_ids": compared,
                "rationale": "Compared against accepted 6C + 6D registries; matching prior capability IDs are reused as prerequisites/placements rather than cloned.",
            },
            "granularity_review_codes": ["GRANULARITY_OK"] + (
                ["TOOL_SPECIFIC_SPLIT"] if kind.startswith("tool_specific") else ["SHARED_SKILL_REUSE"] if kind in SHARED_KINDS else []
            ),
            "seed_mapping_refs": [],
            "review_refs": [],
            "_hard": hard_ids,
            "_soft": soft_ids,
        }
        result.append(row)
    return result


skills = parse_skills()
skill_by_id = {x["skill_id_candidate"]: x for x in skills}
local_skills = set(skill_by_id)
known_skills = prior_skills | local_skills


EXTRA_OBJECTIVES = [
    ("skill.gpu.bottleneck_attribution", "attribute_and_verify", "Yeni bir GPU profile'ında baskın bottleneck'i kanıtla atfeder ve hedefli değişiklikle hipotezi doğrular."),
    ("skill.cuda.race_bounds_diagnosis", "localize_fix_verify", "Fresh CUDA bug'ında race veya bounds kaynağını lokalize eder, düzeltir ve sanitizer/run ile doğrular."),
    ("skill.cuda.optimization_validation", "preserve_correctness_under_optimization", "CUDA optimizasyonunda reference correctness'i koruyup performans etkisini tekrar üretilebilir ölçer."),
    ("skill.triton.correctness_reference_test", "cross_shape_validation", "Triton kernelini farklı shape ve boundary context'lerinde reference sonuçla doğrular."),
    ("skill.math.softmax_stability", "explain_overflow_prevention", "Naive ve stable softmax örneğinde overflow riskini gösterir ve max-shift davranışını açıklar."),
    ("skill.ml.attention_score_mask_softmax", "trace_attention_shapes", "Yeni attention örneğinde Q/K/V, score, mask ve output shape zincirini hatasız izler."),
    ("skill.inference.prefill_decode_profile", "separate_phase_bottlenecks", "Prefill ve decode profiler kanıtını ayırıp her faz için baskın bottleneck'i raporlar."),
    ("skill.inference.oom_root_cause_diagnosis", "classify_memory_pressure", "OOM olayını weights, KV, batch veya workspace baskısına kanıtla bağlar."),
    ("skill.serving.latency_throughput_slo_analysis", "analyze_tail_and_token_latency", "Load testte TTFT, token latency, tail latency ve throughput trade-off'unu birlikte yorumlar."),
    ("skill.serving.failure_domain_diagnosis", "isolate_and_explain_failure", "Fresh serving incident'ında failure domain'i lokalize eder ve evidence chain'i açıklar."),
    ("skill.optimization.scheduler_fairness_preemption", "detect_starvation_tradeoff", "Scheduler trace'inde starvation/fairness sorununu belirler ve latency/throughput etkisiyle çözüm önerir."),
    ("skill.optimization.quantization_performance_validation", "report_quality_performance_tradeoff", "Quantization değişikliğini quality, memory ve performance çıktılarıyla birlikte raporlar."),
    ("skill.multi_gpu.nccl_debug_trace", "isolate_rank_transport_issue", "NCCL failure/hang olayını rank, topology veya transport evidence'ına lokalize eder."),
    ("skill.multi_gpu.collective_profile_analysis", "measure_scaling_efficiency", "Collective profile'dan communication scaling efficiency ve bottleneck çıkarır."),
    ("skill.ai_infra.model_rollout_validation", "decide_rollout_or_rollback", "Model/runtime rollout verisinden SLO, quality ve error gate'lerine göre rollout/rollback kararı üretir."),
    ("skill.ai_infra.efficiency_optimization_validation", "verify_cost_without_slo_regression", "Cost/verim optimizasyonunu SLO ve quality regresyonu oluşturmadan doğrular."),
]


def build_objective(skill: dict, action: str, statement: str, extra: bool = False) -> dict:
    evidence = skill["independent_evidence_path"]["direct_evidence_types"]
    return {
        "objective_id_candidate": "objective." + skill["skill_id_candidate"].removeprefix("skill.") + "." + action,
        "owner_skill_id": skill["skill_id_candidate"],
        "objective_statement": statement,
        "observable_action": action,
        "success_criteria": "Hedef davranış bağımsız, prerequisite-valid, observable ve verified evidence ile gösterilir.",
        "lifecycle_status": "draft",
        "authoring_status": "internally_qa_passed",
        "requirement_role": "required",
        "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
        "evidence_profile": {
            "acceptable_evidence_types": evidence,
            "direct_evidence_types": evidence,
            "required_direct_type": evidence[0],
            "requires_user_authored_artifact": any(x in evidence for x in (
                "authored_code", "measurement_artifact", "hands_on_system_task", "hands_on_workflow"
            )),
            "requires_transfer": True,
            "evaluator_requirement": "verified",
            "policy_defaults_ref": "GRE-v0",
        },
        "task_environment_expectation": "Fresh prerequisite-valid accelerator/inference task, artifact, run or measurement.",
        "allowed_tools_policy_ref": "H0 independent evidence; declared compilers/debuggers/profilers/runtimes allowed when tool use is the target behavior.",
        "diagnostic_eligibility": skill["diagnostic_eligibility"],
        "retention_requirement": {
            "delayed_revalidation_required": True,
            "context_diversity_requirement": "different_hardware_workload_or_shape" if extra else "fresh_context",
        },
        "remediation_tags": skill["remediation_tags"],
        "source_refs": ["source.repo.frdb_v0", "source.repo.gns_v0"],
        "provenance_ref": "source.repo.internal_6e_authoring",
        "freshness_class": skill["freshness_class"],
        "granularity_review_codes": ["GRANULARITY_OK"],
        "review_refs": [],
    }


objectives = [
    build_objective(x, "demonstrate_capability", "Yeni bir bağlamda " + x["capability_statement"].removesuffix("."))
    for x in skills
]
for sid, action, statement in EXTRA_OBJECTIVES:
    objectives.append(build_objective(skill_by_id[sid], action, statement, True))
objectives.sort(key=lambda x: x["objective_id_candidate"])
objective_ids = {x["objective_id_candidate"] for x in objectives}


organization = []
for did, (display, route, role) in domains.items():
    organization.append({
        "entity_type": "domain", "logical_id_candidate": did, "display_name": display,
        "semantic_statement": f"{display} capability organization domain for the D14-D22 accelerator/inference route.",
        "parent_organization_id": None, "primary_domain_id": did, "organization_role": role,
        "teaching_intent_tags": ["teach", "practice", "assess", "transfer"], "linked_route_family_ids": [route],
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
        "source_refs": ["source.repo.pdm_v0"], "provenance_ref": "source.repo.internal_6e_authoring",
        "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
    })
for mid, topics in module_topics.items():
    did = module_domain[mid]
    route = domains[did][1]
    organization.append({
        "entity_type": "module", "logical_id_candidate": mid,
        "display_name": mid.split(".", 1)[1].replace(".", " ").replace("_", " ").title(),
        "semantic_statement": "Related accelerator/inference Topics için stable organization cluster.",
        "parent_organization_id": did, "primary_domain_id": did, "organization_role": "authoring_cluster",
        "teaching_intent_tags": ["teach", "practice", "assess"], "linked_route_family_ids": [route],
        "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
        "source_refs": ["source.repo.pdm_v0", "source.repo.gns_v0"], "provenance_ref": "source.repo.internal_6e_authoring",
        "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
    })
    for topic in topics:
        organization.append({
            "entity_type": "topic", "logical_id_candidate": topic,
            "display_name": topic.split(".", 1)[1].replace(".", " ").replace("_", " ").title(),
            "semantic_statement": "Learner-facing teaching, practice, debugging, measurement and evidence context.",
            "parent_organization_id": mid, "primary_domain_id": did, "organization_role": "teaching_context",
            "teaching_intent_tags": ["teach", "practice", "assess", "reinforce", "transfer"],
            "linked_route_family_ids": [route], "lifecycle_status": "draft", "authoring_status": "internally_qa_passed",
            "source_refs": ["source.repo.pdm_v0", "source.repo.frdb_v0"], "provenance_ref": "source.repo.internal_6e_authoring",
            "freshness_class": "evergreen", "granularity_review_codes": ["GRANULARITY_OK"], "review_refs": [], "notes": None,
        })
organization.sort(key=lambda x: (x["entity_type"], x["logical_id_candidate"]))


def owner_objectives(sid: str) -> list[str]:
    return sorted(x["objective_id_candidate"] for x in objectives if x["owner_skill_id"] == sid)


topic_skill_links = []
for skill in skills:
    topic_skill_links.append({
        "topic_id": skill["primary_teaching_topic_id"], "skill_id": skill["skill_id_candidate"],
        "role": "teach", "importance": "core", "is_primary_teaching_context": True,
        "objective_scope_ids": owner_objectives(skill["skill_id_candidate"]),
        "authoring_rationale": "Primary teaching/evidence context for this canonical accelerator/inference capability.",
        "source_refs": ["source.repo.frdb_v0"], "lifecycle_status": "draft", "review_refs": [],
    })

# Every prior-package prerequisite receives an explicit reuse placement in the first local Topic that consumes it.
reuse_link_keys = set()
for skill in skills:
    for source in skill["_hard"] + skill["_soft"]:
        if source not in prior_skills:
            continue
        key = (skill["primary_teaching_topic_id"], source)
        if key in reuse_link_keys:
            continue
        reuse_link_keys.add(key)
        topic_skill_links.append({
            "topic_id": skill["primary_teaching_topic_id"], "skill_id": source,
            "role": "reinforce", "importance": "supporting", "is_primary_teaching_context": False,
            "objective_scope_ids": [],
            "authoring_rationale": "Accepted 6C/6D capability is reused in accelerator/inference context without cloning learner state.",
            "source_refs": ["source.repo.sdm_v0" if source in sdm_skills else "source.repo.fdm_v0"],
            "lifecycle_status": "draft", "review_refs": [],
        })
topic_skill_links.sort(key=lambda x: (x["topic_id"], x["skill_id"], x["role"]))


reason_by_source = {
    "skill.performance.": "performance_reasoning_dependency",
    "skill.gpu.": "performance_reasoning_dependency",
    "skill.cuda.": "tool_environment_dependency",
    "skill.triton.": "tool_environment_dependency",
    "skill.math.": "conceptual_dependency",
    "skill.network.": "conceptual_dependency",
    "skill.multi_gpu.": "conceptual_dependency",
    "skill.concurrency.": "conceptual_dependency",
    "skill.ai_infra.": "professional_workflow_dependency",
}


def reason_kind(source: str, target: str) -> str:
    for prefix, reason in reason_by_source.items():
        if source.startswith(prefix):
            return reason
    if source.startswith(("skill.platform.", "skill.reliability.", "skill.observability.")):
        return "professional_workflow_dependency"
    if source.startswith("skill.arch."):
        return "performance_reasoning_dependency"
    if source.startswith(("skill.os.", "skill.distributed.", "skill.storage.")):
        return "conceptual_dependency"
    return "conceptual_dependency"


prerequisite_edges = []
for skill in skills:
    target = skill["skill_id_candidate"]
    for kind, sources in (("hard", skill["_hard"]), ("soft", skill["_soft"])):
        for source in sources:
            prerequisite_edges.append({
                "prerequisite_skill_id": source, "target_skill_id": target, "edge_kind": kind,
                "reason_kind": reason_kind(source, target), "strictness_profile_ref": "default_prg_v0",
                "authoring_rationale": (
                    "Source capability is required to teach/measure the target without prerequisite contamination."
                    if kind == "hard" else "Source capability improves scaffold or fluency but is not required for fair target evidence."
                ),
                "contamination_risk_if_missing": "high" if kind == "hard" else "low",
                "task_specific_instead_of_graph_edge": False,
                "source_refs": ["source.repo.prg_v0", "source.repo.frdb_v0"],
                "provenance_ref": "source.repo.internal_6e_authoring", "lifecycle_status": "draft",
                "review_status": "reviewed", "review_refs": [], "cross_package_ref": source in prior_skills,
                "hard_soft_test_result": "hard_required_for_interpretable_target" if kind == "hard" else "soft_scaffold_only",
            })
prerequisite_edges.sort(key=lambda x: (x["target_skill_id"], x["prerequisite_skill_id"], x["edge_kind"]))


capability_requirements = []
for skill in skills:
    sid = skill["skill_id_candidate"]
    did = skill["shared_placement_domain_ids"][0]
    capability_requirements.extend([
        {
            "scope_kind": "domain", "scope_id": did, "capability_kind": "skill", "capability_id": sid,
            "requirement_role": "required", "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
            "rationale": "Required capability inside its accelerator/inference authoring Domain.",
            "source_refs": ["source.repo.pdm_v0", "source.repo.frdb_v0"], "review_refs": [],
        },
        {
            "scope_kind": "professional_route", "scope_id": "scope.professional_route.ai_infrastructure",
            "capability_kind": "skill", "capability_id": sid,
            "requirement_role": "required" if did != "domain.triton" else "supporting",
            "criticality": "critical" if skill["critical_prerequisite_candidate"] else "standard",
            "rationale": "Contributes to verified AI/GPU Infrastructure engineering capability; route role is scope-relative.",
            "source_refs": ["source.repo.professional_readiness", "source.repo.pdm_v0"], "review_refs": [],
        },
    ])


professional_attributions = []
for skill in skills:
    sid = skill["skill_id_candidate"]
    tags = skill["professional_capability_tags"] or ["systems_design"]
    for tag in tags:
        family = tag.replace("profiling_benchmarking", "profiling_benchmarking").replace("operational_safety", "operational_security_safety")
        professional_attributions.append({
            "capability_id": sid, "professional_family_id": "professional." + family,
            "attribution_role": "supporting", "context_requirements": ["fresh_accelerator_or_inference_context"],
            "evidence_expectation_ref": "evidence.h0.objective_specific",
            "rationale": "Capability contributes an observable component to professional accelerator/inference engineering.",
            "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0"], "review_refs": [],
        })


PROJECT_COMPONENTS = [
    "skill.cuda.race_bounds_diagnosis", "skill.cuda.optimization_validation",
    "skill.triton.correctness_reference_test", "skill.inference.prefill_decode_profile",
    "skill.inference.oom_root_cause_diagnosis", "skill.serving.admission_control",
    "skill.serving.latency_throughput_slo_analysis", "skill.optimization.continuous_batching_reasoning",
    "skill.optimization.scheduler_fairness_preemption", "skill.optimization.quantization_performance_validation",
    "skill.multi_gpu.nccl_debug_trace", "skill.multi_gpu.collective_profile_analysis",
    "skill.ai_infra.topology_aware_placement", "skill.ai_infra.driver_toolkit_compatibility",
    "skill.ai_infra.model_rollout_validation", "skill.ai_infra.efficiency_optimization_validation",
]
project_capstone_attributions = []
for sid in PROJECT_COMPONENTS:
    oid = owner_objectives(sid)[-1]
    project_capstone_attributions.append({
        "project_or_capstone_id": "project.gpu_inference.integrated_serving_stack",
        "skill_id": sid, "objective_id": oid, "role": "supporting", "structurally_essential": True,
        "separately_observable": True,
        "expected_evidence_type": skill_by_id[sid]["independent_evidence_path"]["direct_evidence_types"][0],
        "rubric_component_ref": "rubric.gpu_inference." + sid.removeprefix("skill.").replace(".", "_"),
        "source_refs": ["source.repo.professional_readiness", "source.repo.frdb_v0"], "review_refs": [],
    })


reused_prior = sorted(
    {x["prerequisite_skill_id"] for x in prerequisite_edges if x["cross_package_ref"]}
    | {x["skill_id"] for x in topic_skill_links if not x["is_primary_teaching_context"] and x["skill_id"] in prior_skills}
)
seed_mappings = []
for sid in reused_prior:
    origin = "6d_systems" if sid in sdm_skills else "6c_foundations"
    seed_mappings.append({
        "seed_id": sid, "seed_kind": "skill", "disposition": "ratify_as_is",
        "result_entity_refs": [sid], "evidence_compatibility": "not_applicable_not_published",
        "rationale": {
            "semantic_boundary": "Existing accepted capability is semantically identical and reused without clone.",
            "prerequisite_effect": "May serve as an explicit prerequisite or supporting placement in 6E.",
            "evidence_effect": "6E does not copy or synthesize prior learner evidence.",
            "remediation_effect": "Weakness remains attached to the original canonical Skill.",
            "reuse_effect": "Accelerator/inference context adds placement/edges, not a duplicate learner state.",
        },
        "mapping_class": "reused_from_prior_package", "origin_package": origin,
        "source_refs": ["source.repo.sdm_v0" if origin == "6d_systems" else "source.repo.fdm_v0", "source.repo.frdb_v0"],
        "review_refs": [],
    })


review_queue = [
    {
        "review_id": "review.6e.external_coverage", "subject_refs": sorted(domains),
        "review_codes": ["NEEDS_GRANULARITY_REVIEW"], "severity": "non_blocking",
        "question": "Independent current sources D14-D22 coverage, missing accelerator/inference capabilities or hidden prerequisites gösteriyor mu?",
        "decision_inputs_required": ["independent Research AI", "authoritative current GPU/ML/inference sources"],
        "resolution_owner_step": "6H", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": TODAY, "resolved_at": None,
    },
    {
        "review_id": "review.6e.tool_runtime_freshness", "subject_refs": ["domain.cuda", "domain.triton", "domain.inference_serving", "domain.multi_gpu", "domain.ai_gpu_infrastructure"],
        "review_codes": ["TOOL_SPECIFIC_SPLIT"], "severity": "non_blocking",
        "question": "CUDA/Triton/vLLM/SGLang/TensorRT-LLM/NCCL/RDMA/runtime capability'leri için exact freshness review triggers current vendor behavior ile uyumlu mu?",
        "decision_inputs_required": ["current vendor documentation", "6H source freshness audit"],
        "resolution_owner_step": "6H", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": TODAY, "resolved_at": None,
    },
    {
        "review_id": "review.6e.math_numerical_coverage", "subject_refs": ["domain.ml_transformer", "domain.llm_inference", "domain.inference_runtime_optimization"],
        "review_codes": ["HIDDEN_PREREQUISITE_RISK"], "severity": "non_blocking",
        "question": "Explicit math/numerical Skill set inference capability'leri için yeterli mi, yoksa 6H dış doğrulamasında ek prerequisite gerekiyor mu?",
        "decision_inputs_required": ["independent Research AI", "transformer/inference authoritative references"],
        "resolution_owner_step": "6H", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": TODAY, "resolved_at": None,
    },
    {
        "review_id": "review.6e.professional_overlay_reconciliation", "subject_refs": ["domain.inference_serving", "domain.multi_gpu", "domain.ai_gpu_infrastructure"],
        "review_codes": ["SHARED_SKILL_REUSE"], "severity": "non_blocking",
        "question": "6E professional tags ve project attributions 6F D23/professional registry ile hangi canonical Skills altında reconcile edilmeli?",
        "decision_inputs_required": ["6F professional overlay registry"],
        "resolution_owner_step": "6F", "status": "open", "resolution": None,
        "source_refs": ["source.repo.frdb_v0"], "created_at": TODAY, "resolved_at": None,
    },
]


sources = [
    {"source_id": "source.repo.pdm_v0", "source_kind": "canonical_spec", "title": "Professional Domain Backbone", "ref": "docs/CURRICULUM_DOMAIN_MAP.md", "version_or_date": "D-049", "authority_class": "accepted_project_contract", "supports": ["route_scope", "domain_roles"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.kgc_v0", "source_kind": "canonical_spec", "title": "Curriculum Knowledge Graph Contract", "ref": "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md", "version_or_date": "D-051", "authority_class": "accepted_project_contract", "supports": ["identity", "relations"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.gns_v0", "source_kind": "canonical_spec", "title": "Granularity & Naming Standard", "ref": "docs/GRANULARITY_NAMING_STANDARD.md", "version_or_date": "D-054", "authority_class": "accepted_project_contract", "supports": ["granularity", "logical_identity"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.frdb_v0", "source_kind": "canonical_spec", "title": "Full-Route Decomposition Blueprint", "ref": "docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md", "version_or_date": "D-056", "authority_class": "accepted_project_contract", "supports": ["package_contract", "reuse", "qa"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.fdm_v0", "source_kind": "canonical_spec", "title": "Foundations Detailed Map", "ref": "docs/FOUNDATIONS_DETAILED_MAP.md", "version_or_date": "D-057", "authority_class": "accepted_project_contract", "supports": ["prior_skill_registry"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.sdm_v0", "source_kind": "canonical_spec", "title": "Systems Detailed Map", "ref": "docs/SYSTEMS_DETAILED_MAP.md", "version_or_date": "D-058", "authority_class": "accepted_project_contract", "supports": ["prior_skill_registry", "performance_networking_concurrency_reuse"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.prg_v0", "source_kind": "canonical_spec", "title": "Prerequisite Readiness Gate", "ref": "docs/PREREQUISITE_POLICY_SPEC.md", "version_or_date": "D-036", "authority_class": "accepted_project_contract", "supports": ["hard_soft_prerequisites", "contamination_guard"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.professional_readiness", "source_kind": "canonical_spec", "title": "Professional Readiness Target", "ref": "docs/PROFESSIONAL_READINESS_TARGET.md", "version_or_date": "D-041", "authority_class": "accepted_project_contract", "supports": ["professional_evidence", "target_depth"], "freshness_class": "evergreen", "checked_at": TODAY, "notes": None},
    {"source_id": "source.repo.internal_6e_authoring", "source_kind": "internal_design_analysis", "title": "6E deterministic accelerator/inference authoring", "ref": "tools/generate_gpu_ml_inference_package.py", "version_or_date": TODAY, "authority_class": "internal_authoring_not_external_validation", "supports": ["candidate_generation", "cross_package_reuse"], "freshness_class": "version_sensitive", "checked_at": TODAY, "notes": "External coverage/current-industry validation remains mandatory in 6H."},
]
source_ids = [x["source_id"] for x in sources]


def combined_dag_stats(edges: list[tuple[str, str, str]]) -> tuple[int, int]:
    nodes = set(prior_skills) | local_skills
    graph = defaultdict(list)
    indegree = {x: 0 for x in nodes}
    for a, b, kind in edges:
        if kind != "hard":
            continue
        graph[a].append(b)
        indegree[b] += 1
    q = deque(x for x, d in indegree.items() if d == 0)
    visited = 0
    while q:
        node = q.popleft()
        visited += 1
        for target in graph[node]:
            indegree[target] -= 1
            if indegree[target] == 0:
                q.append(target)
    return visited, len(nodes)


combined_edges = prior_edges + [(x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"]) for x in prerequisite_edges]
visited, combined_nodes = combined_dag_stats(combined_edges)
if visited != combined_nodes:
    raise ValueError(f"Combined 6C+6D+6E hard graph is cyclic: {visited}/{combined_nodes}")


# Resolve the explicit 6D -> 6E forward-reuse handoff without changing any 6D Skill/Objective/edge semantics.
sdm_reviews = load(SDM, "review_queue.yaml")
forward = next(x for x in sdm_reviews if x["review_id"] == "review.6d.accelerator_forward_reuse")
forward["status"] = "resolved"
forward["resolution"] = f"6E reuses {len([x for x in reused_prior if x in sdm_skills])} accepted 6D Skills by canonical ID across prerequisites/TopicSkillLinks; no 6D Skill clone is created. See decomposition.6e_gpu_ml_inference seed_mappings.yaml."
forward["resolved_at"] = TODAY
forward["resolution_refs"] = ["decomposition.6e_gpu_ml_inference", "D-059"]
dump(SDM, "review_queue.yaml", sdm_reviews)

sdm_manifest = load(SDM, "manifest.yaml")
sdm_manifest["unresolved_review_count"] = len([x for x in sdm_reviews if x["status"] == "open"])
dump(SDM, "manifest.yaml", sdm_manifest)
sdm_qa = load(SDM, "qa_report.yaml")
sdm_qa["counts"]["open_non_blocking_reviews"] = len([x for x in sdm_reviews if x["status"] == "open" and x["severity"] == "non_blocking"])
sdm_qa["resolved_forward_reviews"] = ["review.6d.accelerator_forward_reuse"]
dump(SDM, "qa_report.yaml", sdm_qa)


manifest = {
    "package_id": "decomposition.6e_gpu_ml_inference", "package_version": 1, "stage_step": "6E",
    "status": "authoring_complete_internal_qa", "blueprint_version": "FRDB-v0",
    "graph_contract_version": "KGC-v0", "granularity_standard_version": "GNS-v0",
    "base_graph_refs": ["FDM-v0", "SDM-v0", "GQA-v0"],
    "route_family_ids": [f"D{n:02d}" for n in range(14, 23)],
    "included_collections": ["sources", "organization_entities", "skills", "objectives", "topic_skill_links", "prerequisite_edges", "capability_requirements", "professional_attributions", "project_capstone_attributions", "seed_mappings", "review_queue", "qa_report"],
    "source_catalog_refs": source_ids,
    "coverage_declarations": [{"route_family_id": f"D{n:02d}", "status": "internally_mapped", "external_validation": "pending_6H"} for n in range(14, 23)],
    "prior_package_refs": [
        {"package_id": "decomposition.6c_foundations", "reused_skill_count": len([x for x in reused_prior if x in fdm_skills]), "target_ref_status": "resolved_prior_package", "blocking_for_current_package_publish": False},
        {"package_id": "decomposition.6d_systems", "reused_skill_count": len([x for x in reused_prior if x in sdm_skills]), "target_ref_status": "resolved_prior_package", "blocking_for_current_package_publish": False},
    ],
    "known_exclusions": [
        "D23 open-source/large-project/capstone decomposition belongs to 6F.",
        "Learner weakness/remediation runtime mapping belongs to 6G.",
        "External coverage/current-industry/source-quality validation belongs to independent Research QA in 6H.",
        "Production lesson/task/resource bodies belong to AŞAMA 15/20.",
        "Physical database schema belongs to 9C.",
        "English progression/cadence remains AŞAMA 7 and is not a global technical prerequisite.",
    ],
    "unresolved_review_count": len([x for x in review_queue if x["status"] == "open"]),
    "blocking_review_count": len([x for x in review_queue if x["status"] == "open" and x["severity"] == "blocking"]),
    "generated_at": TODAY, "authored_by": "main_manager_gpt_5_6_sol",
    "review_refs": [x["review_id"] for x in review_queue], "content_hash": None,
}


domain_count = len(domains)
module_count = len(module_topics)
topic_count = sum(len(x) for x in module_topics.values())
hard_count = len([x for x in prerequisite_edges if x["edge_kind"] == "hard"])
soft_count = len(prerequisite_edges) - hard_count
qa_report = {
    "package_id": "decomposition.6e_gpu_ml_inference", "qa_contract": "FRDB-v0 section 27",
    "result": "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "checked_at": TODAY,
    "counts": {
        "domains": domain_count, "modules": module_count, "topics": topic_count,
        "skills": len(skills), "objectives": len(objectives), "topic_skill_links": len(topic_skill_links),
        "prerequisite_edges": len(prerequisite_edges), "hard_prerequisite_edges": hard_count,
        "soft_prerequisite_edges": soft_count,
        "cross_package_prerequisite_edges": len([x for x in prerequisite_edges if x["cross_package_ref"]]),
        "reused_prior_package_skills": len(reused_prior),
        "reused_6c_skills": len([x for x in reused_prior if x in fdm_skills]),
        "reused_6d_skills": len([x for x in reused_prior if x in sdm_skills]),
        "capability_requirements": len(capability_requirements),
        "professional_attributions": len(professional_attributions),
        "project_attributions": len(project_capstone_attributions),
        "open_non_blocking_reviews": len([x for x in review_queue if x["status"] == "open" and x["severity"] == "non_blocking"]),
        "open_blocking_reviews": len([x for x in review_queue if x["status"] == "open" and x["severity"] == "blocking"]),
    },
    "checks": [
        {"check": "route_family_partition", "result": "PASS", "details": "D14-D22 exactly"},
        {"check": "prior_registry_reuse", "result": "PASS", "details": f"{len(reused_prior)} prior Skills reused without clone"},
        {"check": "math_numerical_prerequisites_explicit", "result": "PASS", "details": "tensor shape, matmul, probability/softmax and floating-point Skills are explicit"},
        {"check": "stable_vs_tool_specific", "result": "PASS", "details": "tool/runtime Skills carry freshness + technology dependencies; stable concepts remain generic"},
        {"check": "triton_foundation_guard", "result": "PASS", "details": "Triton core depends on accepted GPU + CUDA foundation Skills"},
        {"check": "combined_hard_graph_dag", "result": "PASS", "details": f"visited {visited}/{combined_nodes} across 6C+6D+6E"},
        {"check": "english_global_gate", "result": "PASS", "details": "no English hard edge into technical Skills"},
        {"check": "forward_reuse_review", "result": "PASS", "details": "review.6d.accelerator_forward_reuse resolved by explicit 6E reuse registry"},
        {"check": "project_attribution_observable", "result": "PASS", "details": f"{len(project_capstone_attributions)} separately observable integration components"},
        {"check": "blocking_reviews", "result": "PASS", "details": "0 open blocking reviews"},
    ],
    "external_research_qa": {"status": "pending", "owner_step": "6H", "required_before_external_validation": True},
}


# Strip author-only helper fields before export.
for row in skills:
    row.pop("_hard", None)
    row.pop("_soft", None)

for name, value in [
    ("manifest.yaml", manifest), ("sources.yaml", sources), ("organization_entities.yaml", organization),
    ("skills.yaml", sorted(skills, key=lambda x: x["skill_id_candidate"])),
    ("objectives.yaml", objectives), ("topic_skill_links.yaml", topic_skill_links),
    ("prerequisite_edges.yaml", prerequisite_edges), ("capability_requirements.yaml", capability_requirements),
    ("professional_attributions.yaml", professional_attributions),
    ("project_capstone_attributions.yaml", project_capstone_attributions),
    ("seed_mappings.yaml", seed_mappings), ("review_queue.yaml", review_queue), ("qa_report.yaml", qa_report),
]:
    dump(OUT, name, value)

print("GPU_ML_INFERENCE_PACKAGE_GENERATED")
print(f"domains={domain_count} modules={module_count} topics={topic_count}")
print(f"skills={len(skills)} objectives={len(objectives)} links={len(topic_skill_links)}")
print(f"prerequisites={len(prerequisite_edges)} hard={hard_count} soft={soft_count}")
print(f"reused_prior={len(reused_prior)} reused_6c={len([x for x in reused_prior if x in fdm_skills])} reused_6d={len([x for x in reused_prior if x in sdm_skills])}")
print(f"combined_hard_dag_nodes={visited}/{combined_nodes}")
