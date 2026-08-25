from pathlib import Path


def replace_exact(path: str, old: str, new: str, expected: int = 1) -> bool:
    p = Path(path)
    text = p.read_text(encoding="utf-8")
    found = text.count(old)
    if found != expected:
        raise SystemExit(f"{path}: expected {expected} occurrence(s) of {old!r}, found {found}")
    updated = text.replace(old, new)
    if updated != text:
        p.write_text(updated, encoding="utf-8")
        return True
    return False


replacements = {
    "docs/MASTERY_FORMULA_V0.md": [
        ("Bu sayı **engineering heuristic**'tir ve 17C'de kalibre edilir.", "Bu sayı **engineering heuristic**'tir ve 18C'de kalibre edilir."),
        ("Threshold 17C pilotunda false-positive / false-negative sonuçlarına göre değişebilir.", "Threshold 18C pilotunda false-positive / false-negative sonuçlarına göre değişebilir."),
        ("# 23. Pilot calibration — 17C", "# 23. Pilot calibration — 18C"),
        ("ileride 13F'de kalibre edilmiş evaluator policy", "ileride 14F'de kalibre edilmiş evaluator policy"),
        ("13F'de gerçek benchmark ile LLM evaluator policy yeniden ele alınacaktır.", "14F'de gerçek benchmark ile LLM evaluator policy yeniden ele alınacaktır."),
        ("Kesin DB schema 8C, implementation 11A'da.", "Kesin DB schema 9C, implementation 12A'da."),
    ],
    "docs/RETENTION_FORGETTING_SPEC.md": [
        ("Kesin DB schema 8C'de. Behavior-level state:", "Kesin DB schema 9C'de. Behavior-level state:"),
        ("## 21. Pilot calibration — 17C", "## 21. Pilot calibration — 18C"),
    ],
    "docs/PRIORITY_POLICY_SPEC.md": [
        ("Exact eligible-deferral threshold 17B/17C pilotunda kalibre edilir.", "Exact eligible-deferral threshold 18B/18C pilotunda kalibre edilir."),
        ("Bunlar 3H simulation + 17B/17C pilot ile kalibre edilebilir.", "Bunlar 3H simulation + 18B/18C pilot ile kalibre edilebilir."),
        ("Track cadence/frequency 6C'de tanımlanır.", "Track cadence/frequency 7C'de tanımlanır."),
        ("Exact English cadence 6C'de tanımlanır.", "Exact English cadence 7C'de tanımlanır."),
        ("Exact DB/index implementasyonu 8C/11C'ye aittir.", "Exact DB/index implementasyonu 9C/12C'ye aittir."),
    ],
    "docs/PREREQUISITE_POLICY_SPEC.md": [
        ("- ileride 11B Prerequisite Engine'e", "- ileride 12B Prerequisite Engine'e"),
        ("Exact DB/index implementasyonu 8C/11B'ye aittir.", "Exact DB/index implementasyonu 9C/12B'ye aittir."),
    ],
    "docs/TASK_TAXONOMY_SPEC.md": [
        ("6A–6E aynı TaskCandidate contract'ını kullanabilir.", "7A–7E aynı TaskCandidate contract'ını kullanabilir."),
        ("Exact DB schema 8C'ye aittir. 3B davranış contract'ı:", "Exact DB schema 9C'ye aittir. 3B davranış contract'ı:"),
        ("Exact cap ve index stratejisi 8C/11C'de belirlenir.", "Exact cap ve index stratejisi 9C/12C'de belirlenir."),
    ],
    "docs/MASTERY_SIGNALS_SPEC.md": [
        ("Kesin database schema 8C'de tasarlanacaktır.", "Kesin database schema 9C'de tasarlanacaktır."),
        ("Kesin DB schema 8C'ye ait olmakla birlikte", "Kesin DB schema 9C'ye ait olmakla birlikte"),
        ("Bu bir database migration değildir; 8C için davranış sözleşmesidir.", "Bu bir database migration değildir; 9C için davranış sözleşmesidir."),
        ("- exact database schema → **8C**", "- exact database schema → **9C**"),
        ("- code evaluator/compiler mimarisi → **8E / Aşama 13**", "- code evaluator/compiler mimarisi → **9E / Aşama 14**"),
    ],
    "docs/AI_ASSISTANCE_EVIDENCE_SPEC.md": [
        ("AI evaluator'ın güvenilirliği, validator ve confidence politikası 4E / 13F / 8E'de ayrıca ele alınacaktır.", "AI evaluator'ın güvenilirliği, validator ve confidence politikası 4E / 14F / 9E'de ayrıca ele alınacaktır."),
        ("Kesin DB schema 8C'ye aittir.", "Kesin DB schema 9C'ye aittir."),
        ("Bu bir DB migration değildir; 8C için davranış sözleşmesidir.", "Bu bir DB migration değildir; 9C için davranış sözleşmesidir."),
        ("- task/question metadata schema → **4D / 8C**", "- task/question metadata schema → **4D / 9C**"),
        ("- provider/model seçimi → **8E**", "- provider/model seçimi → **9E**"),
        ("- AI Tutor UI ve gerçek prompt davranışları → **13A–13G**", "- AI Tutor UI ve gerçek prompt davranışları → **14A–14G**"),
        ("- code runner/compiler entegrasyonu → **8E / 13D**", "- code runner/compiler entegrasyonu → **9E / 14D**"),
    ],
    "docs/MISSED_DAY_RECOVERY_SPEC.md": [
        ("Exact query/candidate limitleri 8C/11C/17E performans aşamalarında kalibre edilir", "Exact query/candidate limitleri 9C/12C/18E performans aşamalarında kalibre edilir"),
        ("- kullanıcıya gösterilecek exact dönüş metinleri / reason codes → 3G / 7C,", "- kullanıcıya gösterilecek exact dönüş metinleri / reason codes → 3G / 8C,"),
        ("- DB/index/query implementation → 8C / 11C,", "- DB/index/query implementation → 9C / 12C,"),
        ("- notification cadence → 15E,", "- notification cadence → 16E,"),
        ("- exact performance/candidate scan limits → 17E,", "- exact performance/candidate scan limits → 18E,"),
        ("- retention interval calibration → 17C.", "- retention interval calibration → 18C."),
    ],
    "docs/PLANNER_EXPLAINABILITY_SPEC.md": [
        ("UI string'leri 7A–7G'de değişebilir", "UI string'leri 8A–8G'de değişebilir"),
        ("Exact retention/log compaction policy 8C/15B/17E'de kesinleşir.", "Exact retention/log compaction policy 9C/16B/18E'de kesinleşir."),
    ],
    "docs/TOPIC_STATE_MACHINE.md": [
        ("UI kelimeleri daha sonra 7E'de kesinleşebilir", "UI kelimeleri daha sonra 8E'de kesinleşebilir"),
        ("Kesin DB şeması 8C'de belirlenecektir.", "Kesin DB şeması 9C'de belirlenecektir."),
        ("Kesin event/data schema 8C'de belirlenir.", "Kesin event/data schema 9C'de belirlenir."),
        ("- kesin DB/state event schema → **8C**", "- kesin DB/state event schema → **9C**"),
    ],
    "docs/LEARNING_BEHAVIOR_RULES.md": [
        ("Kesin veri modeli Aşama 8C, planner algoritması Aşama 3'te tasarlanacaktır.", "Kesin veri modeli Aşama 9C, planner algoritması Aşama 3'te tasarlanacaktır."),
        ("Aşama 8E ve Aşama 17 kalibrasyon adımlarında", "Aşama 9E ve Aşama 18 kalibrasyon adımlarında"),
    ],
    "docs/DAILY_MICRO_ASSESSMENT_SPEC.md": [
        ("güven düzeyi ve validator sınırları 4E/13F'te kesinleşir.", "güven düzeyi ve validator sınırları 4E/14F'te kesinleşir."),
        ("Exact DB index/cache/query yapısı 8C/8F/11A–11C'de kesinleşir.", "Exact DB index/cache/query yapısı 9C/9F/12A–12C'de kesinleşir."),
    ],
    "docs/V1_SUCCESS_CRITERIA.md": [
        ("Bu profiller Aşama 3 ve Aşama 11'de daha ayrıntılı fixture/test datasına dönüştürülecektir.", "Bu profiller Aşama 3 ve Aşama 12'de daha ayrıntılı fixture/test datasına dönüştürülecektir."),
        ("Aşama 2F/12C'de seçilecek algoritmaya göre", "Aşama 2F/13C'de seçilecek algoritmaya göre"),
        ("- Aşama 6 — English,", "- Aşama 7 — English,"),
        ("- Aşama 17 — pilot kalibrasyonu", "- Aşama 18 — pilot kalibrasyonu"),
    ],
}

changed = set()
for rel, reps in replacements.items():
    for old, new in reps:
        if replace_exact(rel, old, new):
            changed.add(rel)

# Historical research/provenance docs keep their source-era wording but are labeled.
historical_note = (
    "> **D-050 / D-044 hygiene note (2026-08-25):** Bu tarihsel araştırma/provenance belgesindeki "
    "ileride yapılacak aşamalara ait eski numaralar D-044 öncesi planı yansıtabilir. Güncel karşılık için "
    "`docs/STAGE_REINDEX_MAP.md` ve `docs/EXECUTION_INDEX.md` kullanılır. Tarihsel araştırma metni sessizce yeniden yazılmamıştır."
)
for rel in [
    "docs/2E_RESEARCH_VALIDATION.md",
    "docs/2F_RESEARCH_BRIEF.md",
    "docs/2F_RESEARCH_VALIDATION.md",
]:
    p = Path(rel)
    text = p.read_text(encoding="utf-8")
    if "D-050 / D-044 hygiene note" not in text:
        lines = text.splitlines()
        if not lines or not lines[0].startswith("#"):
            raise SystemExit(f"{rel}: cannot safely insert historical hygiene note")
        updated = "\n".join([lines[0], "", historical_note, ""] + lines[1:])
        p.write_text(updated, encoding="utf-8")
        changed.add(rel)

# Discoverability: map is reference-only, not another current-state source.
p = Path("README.md")
text = p.read_text(encoding="utf-8")
if "`docs/STAGE_REINDEX_MAP.md`" not in text:
    marker = "- `docs/PROJECT_MEMORY_PROTOCOL.md` — zorunlu PRE/POST GitHub hafıza senkronu ve dosya rol matrisi\n"
    if text.count(marker) != 1:
        raise SystemExit("README.md: stage map insertion marker missing/duplicate")
    text = text.replace(
        marker,
        marker + "- `docs/STAGE_REINDEX_MAP.md` — D-044 öncesi future-stage referanslarının current karşılık haritası\n",
    )
    p.write_text(text, encoding="utf-8")
    changed.add("README.md")

p = Path("docs/START_HERE.md")
text = p.read_text(encoding="utf-8")
if "`docs/STAGE_REINDEX_MAP.md`" not in text:
    marker = "## 6. Yeni sohbet/agent okuma sırası\n"
    if text.count(marker) != 1:
        raise SystemExit("START_HERE.md: stage map note marker missing/duplicate")
    note = (
        "## 6. Yeni sohbet/agent okuma sırası\n"
        "> D-044 öncesi bir stable/historical belgede eski future-stage numarası görülürse, current execution'ı değiştirmeden önce "
        "`docs/STAGE_REINDEX_MAP.md` ile karşılığı doğrulanır.\n\n"
    )
    p.write_text(text.replace(marker, note), encoding="utf-8")
    changed.add("docs/START_HERE.md")

p = Path("docs/PROJECT_MEMORY_PROTOCOL.md")
text = p.read_text(encoding="utf-8")
if "Canonical legacy→current future-stage çeviri kaydı" not in text:
    anchor = "Bu tarama yapılmadan reindex tamamlanmış sayılmaz.\n"
    if text.count(anchor) != 1:
        raise SystemExit("PROJECT_MEMORY_PROTOCOL.md: reindex anchor missing/duplicate")
    extra = (
        anchor
        + "\nCanonical legacy→current future-stage çeviri kaydı: `docs/STAGE_REINDEX_MAP.md`. "
          "Bu map current execution source of truth değildir; current adımlar için daima "
          "`EXECUTION_INDEX.md` / `MASTER_PLAN.md` kullanılır.\n"
    )
    p.write_text(text.replace(anchor, extra), encoding="utf-8")
    changed.add("docs/PROJECT_MEMORY_PROTOCOL.md")

# Record cleanup without advancing 5B.
p = Path("docs/PROGRESS_LOG.md")
text = p.read_text(encoding="utf-8")
tag = "### 2026-08-25 — D-050 repository-wide documentation hygiene tamamlandı"
if tag not in text:
    block_lines = [
        "",
        "---",
        "",
        tag,
        "- Kullanıcının onayıyla 5B başlatılmadan önce repo-wide dokümantasyon cleanup devam ettirildi.",
        "- D-044 future-stage reindex'i için `docs/STAGE_REINDEX_MAP.md` oluşturuldu.",
        "- D-044 öncesinde tamamlanmış stable spec'lerdeki **future-stage pointer** drift'leri yalnız referans düzeyinde düzeltildi; GRE/RVR/PBR/PRG/DMA ve diğer davranış contract'ları değiştirilmedi.",
        "- Historical Research AI/provenance belgeleri sessizce yeniden yazılmadı; legacy future-stage numaraları için explicit D-050/D-044 hygiene note eklendi.",
        "- `README`, `START_HERE` ve `PROJECT_MEMORY_PROTOCOL` stage-reindex map'e navigasyon verecek şekilde güncellendi.",
        "- Önceki cleanup'ta stale `PROJECT_CONTEXT` 5B'ye senkronlandı, `docs/TODO.md` silindi, `LEARNING_ENGINE.md` historical/superseded pointer'a, `ENGLISH_TRACK.md` non-canonical seed notes'a çevrildi.",
        "- Bu hygiene turu **5B execution değildir** ve execution state'i ilerletmez.",
        "- Canonical durum cleanup sonunda hâlâ: **5A tamamlandı; 5B aktif ve henüz yürütülmedi**.",
        "",
    ]
    p.write_text(text.rstrip() + "\n" + "\n".join(block_lines), encoding="utf-8")
    changed.add("docs/PROGRESS_LOG.md")

# Verification invariants.
if Path("docs/TODO.md").exists():
    raise SystemExit("docs/TODO.md unexpectedly exists")

living = [
    "PROJECT_CONTEXT.md",
    "docs/START_HERE.md",
    "docs/HANDOFF_STATE.md",
    "docs/STEP_STATUS.md",
    "docs/EXECUTION_INDEX.md",
    "docs/MASTER_PLAN.md",
]
for rel in living:
    if "5B" not in Path(rel).read_text(encoding="utf-8"):
        raise SystemExit(f"{rel}: current 5B reference missing")

context = Path("PROJECT_CONTEXT.md").read_text(encoding="utf-8")
status = Path("docs/STEP_STATUS.md").read_text(encoding="utf-8")
if "AKTİF, HENÜZ YÜRÜTÜLMEDİ" not in context:
    raise SystemExit("PROJECT_CONTEXT.md: 5B active/not-executed invariant missing")
if "5B henüz yürütülmedi" not in status:
    raise SystemExit("STEP_STATUS.md: 5B not-executed invariant missing")

decisions = Path("docs/DECISIONS.md").read_text(encoding="utf-8")
if "D-043" not in decisions or "GERİ ÇEKİLDİ" not in decisions:
    raise SystemExit("D-043 withdrawn status missing")
if "D-050" not in decisions:
    raise SystemExit("D-050 decision missing")

for rel in ["README.md", "docs/START_HERE.md", "docs/PROJECT_MEMORY_PROTOCOL.md"]:
    if "STAGE_REINDEX_MAP.md" not in Path(rel).read_text(encoding="utf-8"):
        raise SystemExit(f"{rel}: stage reindex map navigation missing")

forbidden = {
    "docs/PLANNER_SIMULATION_SUITE.md": ["11F gerçek Planner", "17B planner pilot", "17E gerçek cihaz/performance"],
    "docs/MASTERY_FORMULA_V0.md": ["Pilot calibration — 17C", "13F'de gerçek benchmark", "DB schema 8C, implementation 11A"],
    "docs/RETENTION_FORGETTING_SPEC.md": ["Pilot calibration — 17C", "DB schema 8C'de"],
    "docs/PRIORITY_POLICY_SPEC.md": ["17B/17C pilot", "Track cadence/frequency 6C", "English cadence 6C", "8C/11C'ye"],
    "docs/PREREQUISITE_POLICY_SPEC.md": ["ileride 11B Prerequisite", "8C/11B'ye"],
    "docs/TASK_TAXONOMY_SPEC.md": ["6A–6E aynı TaskCandidate", "DB schema 8C'ye", "8C/11C'de"],
    "docs/MASTERY_SIGNALS_SPEC.md": ["database schema 8C", "DB schema 8C", "8E / Aşama 13"],
    "docs/AI_ASSISTANCE_EVIDENCE_SPEC.md": ["4E / 13F / 8E", "DB schema 8C", "4D / 8C", "13A–13G", "8E / 13D"],
    "docs/MISSED_DAY_RECOVERY_SPEC.md": ["8C/11C/17E", "3G / 7C,", "8C / 11C,", "notification cadence → 15E", "limits → 17E", "calibration → 17C"],
    "docs/PLANNER_EXPLAINABILITY_SPEC.md": ["UI string'leri 7A–7G", "8C/15B/17E"],
    "docs/TOPIC_STATE_MACHINE.md": ["sonra 7E'de", "DB şeması 8C", "event/data schema 8C", "schema → **8C**"],
    "docs/LEARNING_BEHAVIOR_RULES.md": ["Aşama 8C, planner", "Aşama 8E ve Aşama 17"],
    "docs/DAILY_MICRO_ASSESSMENT_SPEC.md": ["4E/13F'te", "8C/8F/11A–11C"],
    "docs/V1_SUCCESS_CRITERIA.md": ["Aşama 3 ve Aşama 11'de", "2F/12C'de", "Aşama 6 — English", "Aşama 17 — pilot kalibrasyonu"],
}
for rel, needles in forbidden.items():
    current = Path(rel).read_text(encoding="utf-8")
    for needle in needles:
        if needle in current:
            raise SystemExit(f"{rel}: stale pointer remains: {needle}")

print("D-050 current-state and known stale-reference checks: PASS")
print("Changed files:")
for rel in sorted(changed):
    print(" -", rel)
