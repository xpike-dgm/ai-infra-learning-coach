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
- AŞAMA 6A: ✅ `GNS-v0 / D-054` tamamlandı.
- **Aktif adım: 6B — Full-route decomposition blueprint.**
- **6B henüz yürütülmedi.**
- 6C–6H ve AŞAMA 7–20 bekliyor.

**6B'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover okumasını tamamla, sonra 6B için ayrıca fresh PRE-STEP refresh yap.

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
