from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def replace_if_present(path: str, old: str, new: str) -> None:
    p = ROOT / path
    text = p.read_text(encoding="utf-8")
    if old in text:
        p.write_text(text.replace(old, new), encoding="utf-8")


# 6D summary/review counters after 6E consumed accelerator_forward_reuse.
replace_if_present("docs/SYSTEMS_DETAILED_MAP.md", "| Açık non-blocking review | 4 |", "| Açık non-blocking review | 3 |")
replace_if_present("docs/SYSTEMS_DETAILED_MAP.md", "0 blocking / 4 non-blocking", "0 blocking / 3 non-blocking")
replace_if_present("docs/STEP_STATUS.md", "0 blocking / 4 non-blocking review", "0 blocking / 3 non-blocking review")
replace_if_present(
    "docs/SYSTEMS_DETAILED_MAP.md",
    "- Systems/performance/concurrency/networking Skills'ini clone'lamaz; prerequisite veya TopicSkillLink olarak reuse eder,\n- stable GPU/inference concept'leri ile tool/version-specific capability'leri ayırır,\n- math/numerical hidden prerequisite'leri explicit candidate Skills/edges olarak yakalar,\n- aynı FRDB-v0 collection ve QA contract'ını uygular,\n- `review.6d.accelerator_forward_reuse` kaydını tüketir,\n- 6E başlamadan fresh PRE-STEP GitHub refresh yapar.\n\n**6D sonrası numaralı adım:** `6E — GPU / ML / Inference detailed map`.",
    "6E GIM-v0 / D-059 bu forward handoff'u tamamladı:\n- Systems/performance/concurrency/networking Skills clone'lanmadan canonical ID ile reuse edildi,\n- stable GPU/inference concepts ile tool/version-specific capabilities ayrıldı,\n- math/numerical hidden prerequisites explicit Skills/edges olarak yakalandı,\n- `review.6d.accelerator_forward_reuse` resolved edildi.\n\n**Güncel sonraki numaralı adım:** `6F — Professional engineering / project map`; `review.6d.professional_overlay_reconciliation` 6F'ye açık kalır.",
)

# Compact current-location footers.
replace_if_present(
    "docs/EXECUTION_INDEX.md",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6D`\n**Aktif:** **`6E — GPU / ML / Inference detailed map`**\n\n6D SDM-v0 / D-058 ile tamamlandı. 6E henüz yürütülmedi; 6E başlamadan fresh PRE-STEP GitHub refresh zorunludur.",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6E`\n**Aktif:** **`6F — Professional engineering / project map`**\n\n6E GIM-v0 / D-059 ile tamamlandı. 6F henüz yürütülmedi; 6F başlamadan fresh PRE-STEP GitHub refresh zorunludur.",
)
replace_if_present(
    "docs/MASTER_PLAN.md",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6D`\n**Aktif:** **`6E — GPU / ML / Inference detailed map`**\n\nBir sonraki yürütme: **6E başlamadan fresh PRE-STEP GitHub refresh → FRDB-v0 + FDM-v0 + SDM-v0 registry ile GPU/ML/Inference detailed map → POST-STEP D-050 sync + stale-reference audit.**",
    "**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6E`\n**Aktif:** **`6F — Professional engineering / project map`**\n\nBir sonraki yürütme: **6F başlamadan fresh PRE-STEP GitHub refresh → FRDB-v0 + FDM-v0 + SDM-v0 + GIM-v0 registry/attribution setleri ile Professional Engineering detailed map → POST-STEP D-050 sync + stale-reference audit.**",
)
replace_if_present("docs/MASTER_PLAN.md", "**Son senkron:** 2026-08-25", "**Son senkron:** 2026-08-26")

# Root bootstrap current state.
replace_if_present(
    "AGENTS.md",
    "- AŞAMA 6D: ✅ `SDM-v0 / D-058` tamamlandı.\n- **Aktif adım: 6E — GPU / ML / Inference detailed map.**\n- **6E henüz yürütülmedi.**\n- 6F–6H ve AŞAMA 7–20 bekliyor.\n\n**6E'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6E için ayrıca fresh PRE-STEP refresh yap.",
    "- AŞAMA 6D: ✅ `SDM-v0 / D-058` tamamlandı.\n- AŞAMA 6E: ✅ `GIM-v0 / D-059` tamamlandı.\n- **Aktif adım: 6F — Professional engineering / project map.**\n- **6F henüz yürütülmedi.**\n- 6G–6H ve AŞAMA 7–20 bekliyor.\n\n**6F'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6F için ayrıca fresh PRE-STEP refresh yap.",
)

# Local manager handoff: every volatile current-step copy must advance together.
replace_if_present("docs/LOCAL_MANAGER_HANDOFF.md", "Current execution state'in hâlâ `6A–6D completed / 6E active-not-executed` olduğunu doğrula.", "Current execution state'in `6A–6E completed / 6F active-not-executed` olduğunu doğrula.")
replace_if_present("docs/LOCAL_MANAGER_HANDOFF.md", "Ancak bundan sonra, 6E için **ayrı bir fresh PRE-STEP GitHub refresh** yap.", "Ancak bundan sonra, 6F için **ayrı bir fresh PRE-STEP GitHub refresh** yap.")
replace_if_present(
    "docs/LOCAL_MANAGER_HANDOFF.md",
    "**Son tamamlanan numaralı adım:** `6D — Systems detailed map`\n**Final:** `SDM-v0 — Systems Detailed Map` / D-058\n**Canonical:** `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/`\n\n**Aktif adım:** `6E — GPU / ML / Inference detailed map`\n**Durum:** **HENÜZ YÜRÜTÜLMEDİ**\n\nKullanıcı onaylı numbered work 6D'yi tamamladı. Bu handoff belgesi **6E'yi başlatmaz veya ilerletmez**.\n\nKullanıcı 6E'yi devam ettirmek/onaylamak istediğinde:",
    "**Son tamamlanan numaralı adım:** `6E — GPU / ML / Inference detailed map`\n**Final:** `GIM-v0 — GPU / ML / Inference Detailed Map` / D-059\n**Canonical:** `docs/GPU_ML_INFERENCE_DETAILED_MAP.md` + `curriculum/decomposition/6e_gpu_ml_inference/`\n\n**Aktif adım:** `6F — Professional engineering / project map`\n**Durum:** **HENÜZ YÜRÜTÜLMEDİ**\n\nKullanıcı onaylı numbered work 6E'yi tamamladı. Bu handoff belgesi **6F'yi başlatmaz veya ilerletmez**.\n\nKullanıcı 6F'yi devam ettirmek/onaylamak istediğinde:",
)
replace_if_present("docs/LOCAL_MANAGER_HANDOFF.md", "fresh 6E PRE-STEP GitHub refresh\n→ 6E execution", "fresh 6F PRE-STEP GitHub refresh\n→ 6F execution")
replace_if_present(
    "docs/LOCAL_MANAGER_HANDOFF.md",
    "Bu takeover sırasında 6E'yi yürütme. Önce bana ürün hedefini, tamamlanan modelleri, değiştirilemez invariants'ı, current exact step'i, tamamlanan FDM-v0 ile SDM-v0'ı ve sıradaki 6E scope'unu özetleyip devralmaya hazır olduğunu söyle.",
    "Bu takeover sırasında 6F'yi yürütme. Önce bana ürün hedefini, tamamlanan modelleri, değiştirilemez invariants'ı, current exact step'i, tamamlanan FDM-v0, SDM-v0 ve GIM-v0'ı ve sıradaki 6F scope'unu özetleyip devralmaya hazır olduğunu söyle.",
)
replace_if_present(
    "docs/LOCAL_MANAGER_HANDOFF.md",
    "AŞAMA 6D ✅ SDM-v0 / D-058\nAŞAMA 6E 🟡 ACTIVE — NOT EXECUTED\nAŞAMA 6F–6H ⬜",
    "AŞAMA 6D ✅ SDM-v0 / D-058\nAŞAMA 6E ✅ GIM-v0 / D-059\nAŞAMA 6F 🟡 ACTIVE — NOT EXECUTED\nAŞAMA 6G–6H ⬜",
)
# Remaining route-progress summary marker discovered by repo-wide stale audit.
replace_if_present(
    "docs/LOCAL_MANAGER_HANDOFF.md",
    "- **6E 🟡 ACTIVE / NOT EXECUTED — GPU / ML / Inference detailed map**\n- 6F–6H waiting",
    "- **6E ✅ GIM-v0 / D-059 — GPU / ML / Inference detailed map**\n- **6F 🟡 ACTIVE / NOT EXECUTED — Professional engineering / project map**\n- 6G–6H waiting",
)

# START_HERE read order includes all completed decomposition summaries.
p = ROOT / "docs/START_HERE.md"
text = p.read_text(encoding="utf-8")
needle = "11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`"
if needle in text:
    text = text.replace(
        needle,
        "11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/SYSTEMS_DETAILED_MAP.md`\n11h. `curriculum/decomposition/6d_systems/manifest.yaml`\n11i. `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`\n11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`\n11k. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    )
p.write_text(text, encoding="utf-8")

print("EXTRA_POST_SYNC=PASS")
