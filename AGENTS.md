# AGENTS.md — Local Manager Bootstrap

Bu repo artık yerel çalışan ana yönetici/koordinatör agent tarafından devralınabilir. Bu dosya **bootstrap talimatıdır**; ürün/spec gerçeğinin yerine geçmez.

## İlk zorunlu hareket

Herhangi bir numaralı adımı yürütmeden, karar vermeden, kod yazmadan veya dosya değiştirmeden önce:

1. `docs/LOCAL_MANAGER_HANDOFF.md` dosyasını baştan sona oku.
2. `docs/START_HERE.md` ve `docs/PROJECT_MEMORY_PROTOCOL.md` dosyalarını oku.
3. Root `PROJECT_CONTEXT.md`, `docs/HANDOFF_STATE.md`, `docs/EXECUTION_INDEX.md`, `docs/STEP_STATUS.md`, `docs/DECISIONS.md`, `docs/MASTER_PLAN.md` dosyalarını fresh oku.
4. Repo içindeki **tüm Markdown dosyalarının envanterini çıkar ve tamamını oku**. Handoff özeti canonical spec'lerin yerine geçmez.
5. Çelişki varsa current execution için `EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN`; davranış için ilgili canonical spec; kalıcı karar için `DECISIONS.md` esas alınır.

Önerilen local shell audit:

```bash
git pull --ff-only
find . -type f -name '*.md' -not -path './.git/*' -print | sort
```

İlk takeover sırasında yalnız dosya adlarını görmek yetmez; içerikleri de okunmalıdır.

## Değiştirilemez çalışma protokolü

`docs/PROJECT_MEMORY_PROTOCOL.md` bağlayıcıdır.

```text
PRE-STEP GitHub refresh
→ gerekiyorsa Research/Coding/QA
→ spec/implementation
→ bağımsız değerlendirme
→ POST-STEP living-memory sync
→ repo-wide stale-reference audit
→ sonraki adım
```

Hiçbir numaralı adım PRE olmadan başlamaz ve POST olmadan tamamlanmış sayılmaz.

## Güncel execution state

- AŞAMA 1–5: tamamlandı.
- AŞAMA 6: ✅ tamamlandı — `GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / S6ERQA-v0`.
- 6H final external Research QA: ✅ `S6ERQA-v0 / D-062`; 549 Skill / 608 Objective / 950 prerequisite edge; 549/549 hard DAG; 10/10 6H review resolved.
- 7A: ✅ `EED-v0 / D-063` tamamlandı — D01 15 Skill / 15 Objective / 16 English hard edge / 15 diagnostic task family.
- 7B: ✅ `TECP-v0 / D-064` tamamlandı — 15 Skill = 5 A1 + 5 A2 + 5 B1; 4 bounded B2+ extension; CEFR review resolved.
- 7C: ✅ `DECP-v0 / D-065` tamamlandı — common-capacity daily candidate opportunity; no fixed minute/percentage/streak/debt; PBR balance/starvation + state-driven task mix.
- 7D: ✅ `TEIP-v0 / D-066` tamamlandı — 4 construct-aware integration mode; component attribution; bidirectional contamination + reversible scaffold guards.
- 7E: ✅ `TEPM-v0 / D-067` tamamlandı — 8 derived Skill presentation state; qualified A1/A2/B1 Technical English profile; B2+ per-capability evidence; no general-English/official CEFR/numeric aggregate overclaim.
- **AŞAMA 7 tamamen tamamlandı.**
- 8A: ✅ `UXIA-v0 / D-068` tamamlandı — Today/Learn/Progress/Profile semantic information architecture; shared detail/focused-flow ownership; 49/49 QA PASS.
- 8B: ✅ `THUX-v0 / D-069` tamamlandı — action-first Today/Home hierarchy, current PlannedTask queue, hard-capacity/reason/empty/degraded semantics; 90/90 QA PASS.
- 8C: ✅ `TRUX-v0 / D-070` tamamlandı — Task Runner execution-surface sınırı, emergent ungraded working session, shared focused-flow frame, deterministic entry/resume revalidation, non-punitive assistance escalation, asked-not-inferred provenance, in-flight replan koruması, `evaluation_pending` truthfulness; 123/123 QA PASS.
- **Aktif adım: 8D — Sınav UX.**
- **8D henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 8E–20 bekliyor.

**8D'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 8D için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.

## Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Takvim kapasite/horizon bilgisidir; readiness/mastery gerçeği değildir.

## En kritik yasaklar

- `Day X / total days`, kariyer yüzdesi veya streak'i gerçek mastery/readiness metriği yapma.
- Task/lesson completion'ı mastery sayma.
- LLM'ye mastery/prerequisite/planner source-of-truth yetkisi verme.
- Tek kolay quiz ile kritik Skill'i mastered yapma.
- English'i bütün technical route için global hard gate yapma.
- Broad `Python weak` sonucu ile tüm domain'i resetleme; Skill/Objective seviyesinde lokalize et.
- Domain/Module/Topic placement'ı runtime hard prerequisite yerine kullanma.
- Aynı semantic Skill'i farklı Topic'lerde clone'lama.
- Fixed bilimsel görünüşlü threshold/weight/cooldown/ratio uydurma; accepted spec veya calibration yoksa semantik/deterministik politika kullan.
- AI-generated assessment içeriğini kendi kendine trusted sayma.
- D-043'ü geri getirme; geri çekilmiş/noncanonical karardır.
- Tamamlanmış canonical spec'leri sessizce değiştirme.

## Ana kaynak

Eksiksiz transfer ve mevcut tasarımın geniş özeti: `docs/LOCAL_MANAGER_HANDOFF.md`.

## Yeni sohbetler için kalıcı external-memory bootstrap

Bu repo, sohbet hafızasına değil durable repo hafızasına dayanır. Yeni veya devralınan her sohbet, herhangi bir anlamlı repo işi, karar, inceleme ya da değişiklikten önce `vault/agent/SESSION_START.md` dosyasını da okumalı ve oradaki source hierarchy'yi izlemelidir.

- `vault/agent/CURRENT_CONTEXT.md` yalnız hızlı briefing'dir; current execution için source of truth değildir.
- Current execution iddiası daima `EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN` ile yeniden doğrulanır.
- Sohbette netleşen kalıcı kararlar, açık loop'lar ve araştırma bulguları sohbet içinde bırakılmaz; `SESSION_START.md`de tanımlanan doğru durable hedefe yazılır.
- Bu katman `PROJECT_MEMORY_PROTOCOL.md`yi tamamlar; hiçbir numaralı adımın PRE/POST yükümlülüğünü azaltmaz.
