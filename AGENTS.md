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
- 8D: ✅ `ASUX-v0 / D-071` tamamlandı — üç scope için tek assessment session interior, atomic evidence boundary submission, frozen submitted boundary, skip != incorrect, disclosed independence/tools, non-punitive in-session assistance, beş koşullu slot recomposition, contested item dispute, provisional/invalid güvenliği, semantic result ve first-class `not_reliably_measured`; 107/107 QA PASS.
- 8E: ✅ `SPWX-v0 / D-072` tamamlandı — TEPM-v0'dan genelleştirilen tek 8-state Skill vokabüleri, qualifier olarak `at_risk`, sıralanan fakat çökertilmeyen multi-axis truth, kilitlenmiş 8 Skill + 6 Topic etiketi, inventory-only counting, hypothesis != deficiency, `remediation_task_completed != remediation_closed`, streak olmayan learning history, gradebook olmayan longitudinal assessment_report; 128/128 QA PASS.
- 8F: ✅ `VDSX-v0 / D-073` tamamlandı — expression layer (`visual_severity <= canonical_severity`), 6 tone, 46 surface + 8 Skill + 6 Topic + 4 qualifier state eksiksiz tone eşlemesi, yalnız gerçek arızaya izinli `system_fault`, attention grubunun tone yükseltmemesi, WCAG çapalı kontrast/48dp/%200 metin, Türkçe casing koruması, ikna edici olmayan motion, competence progress-bar yasağı; 121/121 QA PASS.
- 8G: ✅ `WFPX-v0 / D-074` tamamlandı — 3 window class, 6 surface region geometry'si sahibi spec'lere karşı doğrulanmış, focused-flow exit 48dp sabit, 52 kontrast çifti hesaplanarak ölçülmüş, `attention` menekşe ve kırmızı yalnız `system_fault`, prototip bağlayıcı değil; 222/222 QA PASS.
- **AŞAMA 8 TAMAMLANDI** — UXIA-v0 → THUX-v0 → TRUX-v0 → ASUX-v0 → SPWX-v0 → VDSX-v0 → WFPX-v0.
- 9A: ✅ `AMTS-v0 / D-075` tamamlandı — Android native (V1'de cross-platform UI katmanı yok), Kotlin + Jetpack Compose, Material 3 yalnız substrate ve dynamic colour kapalı, domain core saf Kotlin ve Android/UI/network/AI bağımsız, window class'lar WFPX-v0 ile birebir, default-locale case transform yasak, `minSdk` politika; 6 maddelik verification list 10A'ya devredildi; 100/100 QA PASS.
- 9B: ✅ `LFPS-v0 / D-076` tamamlandı — kanıt source of truth ve öğrenci state'i yeniden hesaplanabilir projeksiyon, append-only truth kayıtları, SQLite, core-owned persistence interface'leri, ayrı versiyonlanan curriculum/user state, kalıcı exposure kayıtları, tek-eylem-tek-transaction, forward-only migration, atomik doğrulanmış restore, sessiz reset yasak; 100/100 QA PASS.
- 9C: ✅ `DDM-v0 / D-077` tamamlandı — schema mimariyi uygular; üç store bölgesi, `(logical_id, version)` composite kimlik ve yapısal pinning, UPDATE yolu olmayan append-only truth tabloları, dört ayrı evidence ekseni, append edilen disposition, instant + study day + offset üçlüsü, watermark'lı projection provenance, indekslenmiş exposure, library-neutral schema; 114/114 QA PASS.
- 9D: ✅ `MSBX-v0 / D-078` tamamlandı — sınırlar garantileri yapısal yapar; 10 modül, içe-doğru dependency kuralı, `core-*` asla `data-*`/`ai-*`/`app-*`'e bağımlı olamaz, 4 port, port olarak saat, core'da rastgelelik yok, ürünle sevk edilen null evaluator, engine başına tek state ailesi, `core-application`da transaction, core'da presentation projection; 93/93 QA PASS.
- 9E: ✅ `AIAX-v0 / D-079` tamamlandı — AI port arkasında yardımcıdır ve otorite değildir (AI önerir, deterministic engine'ler karar verir); `LEARNING_BEHAVIOR_RULES` §17 model seçimi ve §18 güvenlik/proxy/backend kararları kapatıldı; schema-constrained evaluator çıktısı ve serbest metin ayrıştırma yasağı, `provisional` uncalibrated LLM, 7 sonuçlu taksonomi ve **refusal yanlış cevap değildir**, her yanıtsızlık `evaluation_pending`, uçtan uca timeout bütçesi, konfigürasyondaki model adı + provider-independent adapter, deterministik iş için AI çağrısı yasağı, APK'da key yok ve V1'de proxy yok, asgari-içerik gizlilik sınırı, untrusted generated item, her evidence satırında evaluator_ref; 86/86 QA PASS.
- 9F: ✅ `TVSX-v0 / D-081` tamamlandı — üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir; kabul edilmiş her invariant'ın adı konmuş sahibi vardır ve sahipsiz invariant release'i bloklar; 6 doğrulama katmanı ve en küçüğü cihaz katmanı; coverage yüzdesi gate değil, gate invariant coverage; negatif doğrulama zorunlu; append-only schema seviyesinde, migration dolu fixture'lara karşı; hiçbir check canlı AI provider çağırmaz; null-evaluator yolu `ai-adapter` olmadan build alınarak doğrulanır; determinizm enjekte saatle egzersiz edilir ve flaky check düşmüş check'tir; 11 koşullu release gate `validate_*.py` glob'unun tamamını içerir; 288/288 QA PASS.
- **AŞAMA 9 TAMAMLANDI** — AMTS-v0 → LFPS-v0 → DDM-v0 → MSBX-v0 → AIAX-v0 → TVSX-v0.
- `D-080`: ürün kişisel kullanım içindir; store dağıtımı ve halka açık paylaşım yoktur. Store QA, dağıtım imzalama, güvenlik amaçlı obfuscation/pinning ve cihaz matrisi kapsam dışıdır; kanıt ve AI yetki kuralları değişmez.
- 10A: ✅ `MPSX-v0 / D-082` tamamlandı — repodaki ilk çalıştırılabilir çıktı; çalıştırılmamış hiçbir şey iddia edilmez ve build iki yanlış pin'i (Gradle 9.5, `kotlin.android`) anında yakaladı; on modül `android/` altında ve bağımlılıklar `boundaries.yaml`a eşit; `verifyModuleBoundaries` yasak kenarı/cycle'ı hesaplayıp build'i düşürüyor; `-PwithAiAdapter=false` adaptörsüz build geçiyor (V1 kriteri 8) ve APK üretildi; hedef cihaz **Poco M6 Pro / API 36** kaydedildi; `AMTS-v0` §9'un altı maddesi kapandı; DI/ORM/HTTP client yok; saat tek yerde; dynamic colour yok; key repoya giremiyor; 127/127 QA PASS.
- 10B: ✅ `NSHX-v0 / D-083` tamamlandı — navigasyon kuralları `core-presentation`da saf fonksiyonlar, UI toolkit'inde değil; dört destination kabul edilmiş sırada ve enum sırası kanonik; yasak top-level id yok; kanonik entity başına tek surface objesi olduğu için çelişkili Skill detail temsil edilemez; contextual edge kümesi kapalı ve `ia.yaml` ile karşılaştırılıyor; focused flow shell'i askıya alıyor ve `showsShell`/`requiresSafeExit` türetiliyor; dönüş kuralı deterministik; window class'lar core'da hesaplanıyor ve yalnız çizimi değiştiriyor; 104/104 QA PASS.
- 10C: ✅ `DSIX-v0 / D-084` tamamlandı — tasarım sistemi kanonik state'in iddia etmediği severity'yi ekleyemez; token'lar `core-presentation`da düz veri ve kontrast iki temada hex'ten yeniden hesaplanıyor (kayıtlı minimumlar token'lardan yeniden türetildi); palet revize edilmedi; `LearningTone` beş değerli ve fault değeri yok, yani learning state'e fault tonu vermek yazılamaz; Material `error` rolü yalnız `system_fault`; attention grubu tonu yükseltmez; 48dp modifier, %200 metin, metin olarak verilen state, locale-naive casing yok; dynamic colour scan'i kendi yorumunu yakaladı ve gate gevşetilmedi; 146/146 QA PASS.
- **Aktif adım: 10D — Local database.**
- **10D henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
- AŞAMA 10E–20 bekliyor.

**10D'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 10D için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.

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
