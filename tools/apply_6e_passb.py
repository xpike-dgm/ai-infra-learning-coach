from __future__ import annotations

from pathlib import Path


PATH = Path("tools/generate_gpu_ml_inference_package.py")

# FRDB-v0 §19.2: these source capabilities improve scaffold/analogy/fluency,
# but target teaching/evidence remains interpretable without them.
SOFT_OVERRIDES = {
    ("skill.gpu.simt_divergence_reasoning", "skill.arch.branch_prediction_cost"),
    ("skill.gpu.latency_hiding_reasoning", "skill.arch.instruction_level_parallelism"),
    ("skill.gpu.shared_memory_layout_transform", "skill.arch.data_layout_transformation"),
    ("skill.cuda.thread_index_calculation", "skill.programming.iteration_reasoning"),
    ("skill.cuda.device_allocation_lifetime", "skill.c.dynamic_allocation_lifecycle"),
    ("skill.cuda.pinned_memory_usage", "skill.gpu.copy_compute_overlap_model"),
    ("skill.triton.program_id_indexing", "skill.cuda.thread_index_calculation"),
    ("skill.triton.reduction_kernel", "skill.gpu.latency_hiding_reasoning"),
    ("skill.math.tensor_shape_reasoning", "skill.dsa.sequence_traversal"),
    ("skill.math.matrix_multiplication_reasoning", "skill.programming.iteration_reasoning"),
    ("skill.math.probability_normalization", "skill.math.tensor_shape_reasoning"),
    ("skill.math.mixed_precision_tradeoff", "skill.performance.measurement_validity_reasoning"),
    ("skill.ml.normalization_layer_reasoning", "skill.math.floating_point_error_reasoning"),
    ("skill.inference.prefill_decode_distinction", "skill.arch.latency_throughput_distinction"),
    ("skill.inference.autoregressive_loop", "skill.programming.iteration_reasoning"),
    ("skill.inference.generation_stopping_state", "skill.concurrency.cancellation_timeout_semantics"),
    ("skill.inference.attention_operator_execution", "skill.gpu.compute_memory_balance"),
    ("skill.inference.topk_topp_sampling", "skill.dsa.sequence_traversal"),
    ("skill.serving.worker_engine_topology", "skill.distributed.partial_failure_model"),
    ("skill.serving.worker_resource_binding", "skill.os.process_address_space_model"),
    ("skill.serving.vllm_runtime_operation", "skill.linux.process_inspection"),
    ("skill.serving.sglang_runtime_operation", "skill.linux.process_inspection"),
    ("skill.serving.tensorrt_llm_runtime_operation", "skill.cuda.kernel_execution_mapping"),
    ("skill.optimization.kv_fragmentation_reasoning", "skill.os.allocator_behavior_model"),
    ("skill.optimization.prefix_cache_reuse", "skill.storage.index_selection_reasoning"),
    ("skill.optimization.scheduler_fairness_preemption", "skill.concurrency.scaling_limit_reasoning"),
    ("skill.multi_gpu.rdma_transport_model", "skill.os.memory_mapping_usage"),
    ("skill.multi_gpu.pipeline_parallel_partition", "skill.multi_gpu.topology_bandwidth_model"),
    ("skill.ai_infra.gpu_capability_inventory", "skill.platform.cloud_compute_resource_model"),
    ("skill.ai_infra.gpu_partitioning_isolation", "skill.platform.container_vs_vm_boundary"),
    ("skill.ai_infra.model_artifact_versioning", "skill.platform.image_supply_chain_safety"),
    ("skill.ai_infra.workload_isolation_boundary", "skill.platform.secret_handling_safety"),
}

# These were discovered as contextually adjacent but not semantic prerequisites at all.
REMOVE_OVERRIDES = {
    ("skill.multi_gpu.topology_discovery", "skill.os.cpu_time_accounting"),
    ("skill.multi_gpu.collective_semantics", "skill.distributed.replication_models"),
}


def split_ids(value: str) -> list[str]:
    return [x for x in value.split(";") if x]


text = PATH.read_text(encoding="utf-8")
prefix, rest = text.split('SKILL_ROWS = """', 1)
block, suffix = rest.split('""".strip().splitlines()', 1)
lines = [line for line in block.strip().splitlines() if line.strip()]

moved: set[tuple[str, str]] = set()
removed: set[tuple[str, str]] = set()
new_lines: list[str] = []

for line in lines:
    fields = line.split("|")
    if len(fields) != 10:
        raise RuntimeError(f"Unexpected SKILL_ROWS field count for: {line}")
    target = fields[0]
    hard = split_ids(fields[5])
    soft = split_ids(fields[6])

    for pair in sorted(SOFT_OVERRIDES):
        pair_target, source = pair
        if pair_target != target:
            continue
        if source in hard:
            hard.remove(source)
            if source not in soft:
                soft.append(source)
            moved.add(pair)
        elif source in soft:
            moved.add(pair)
        else:
            raise RuntimeError(f"Pass B soft override source missing: {source} -> {target}")

    for pair in sorted(REMOVE_OVERRIDES):
        pair_target, source = pair
        if pair_target != target:
            continue
        if source in hard:
            hard.remove(source)
            removed.add(pair)
        elif source in soft:
            soft.remove(source)
            removed.add(pair)
        else:
            raise RuntimeError(f"Pass B remove override source missing: {source} -> {target}")

    fields[5] = ";".join(hard)
    fields[6] = ";".join(soft)
    new_lines.append("|".join(fields))

if moved != SOFT_OVERRIDES:
    raise RuntimeError(f"Soft override mismatch: missing={sorted(SOFT_OVERRIDES - moved)}")
if removed != REMOVE_OVERRIDES:
    raise RuntimeError(f"Remove override mismatch: missing={sorted(REMOVE_OVERRIDES - removed)}")

PATH.write_text(
    prefix + 'SKILL_ROWS = """\n' + "\n".join(new_lines) + '\n""".strip().splitlines()' + suffix,
    encoding="utf-8",
)

print(f"PASS_B_REVIEW=PASS soft_downgrades={len(moved)} removed_non_dependencies={len(removed)}")
