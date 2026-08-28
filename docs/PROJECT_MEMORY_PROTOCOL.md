# Project Memory Protocol — Zorunlu GitHub Beyin Tazeleme Döngüsü

**Durum:** BAĞLAYICI ÇALIŞMA PROTOKOLÜ  
**Tarih:** 2026-08-25  
**Son güçlendirme:** D-050

Bu belge AI Infra Learning Coach projesinde her numaralı adımın (`1A`, `2C`, `3A`, `12F` vb.) nasıl başlatılıp nasıl kapatılacağını ve repo içindeki kalıcı hafıza dosyalarının nasıl senkron tutulacağını tanımlar.

Ana kural:

> **Hiçbir numaralı proje adımı GitHub beyin tazelemesi yapılmadan başlatılmaz; hiçbir numaralı proje adımı gerekli GitHub hafıza dosyaları, MASTER_PLAN ilerleme kaydı ve repo-wide stale-reference kontrolü yapılmadan tamamlanmış sayılmaz.**

İkinci bağlayıcı kural:

> **GitHub durable source of truth'tur. Sohbet hafızası veya tek bir durum dosyası repo içindeki başka bir stale dosyanın varlığını mazur göstermez.**

**D-055 clarification:** Ana yönetici local çalışan agent olsa da bu protokol aynen bağlayıcıdır. Local manager takeover bootstrap'ı `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md` ile yapılır; manager implementation değişikliği PRE/POST kurallarını gevşetmez.

---

# 1. PRE-STEP — Adım başlamadan önce zorunlu beyin tazelemesi

Yeni bir numaralı adıma başlanmadan hemen önce ana yönetici GitHub'daki güncel durumu okumalıdır.

Minimum zorunlu kontrol seti:

1. `docs/HANDOFF_STATE.md` — mevcut proje konumu ve son kabul edilen durum.
2. `docs/EXECUTION_INDEX.md` — adım kimliği, sırası ve tamamlanma durumu.
3. `docs/STEP_STATUS.md` — aktif adımın hızlı doğrulaması.
4. `docs/DECISIONS.md` — ilgili bağlayıcı kararların kontrolü.
5. `docs/MASTER_PLAN.md` — ilgili ayrıntılı checklist'in canonical indeksle uyum kontrolü.
6. `PROJECT_CONTEXT.md` — kısa yaşayan proje snapshot'ının güncel olduğunun kontrolü.
7. Başlanacak adımla doğrudan ilgili en güncel spec/davranış dosyaları.

Gerekirse ayrıca:

- `docs/START_HERE.md`
- `docs/PROJECT_MASTER_CONTEXT.md`
- `README.md`
- `docs/V1_SCOPE.md`
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/NON_GOALS.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/AI_AGENT_WORKFLOW.md`
- `docs/PROGRESS_LOG.md`
- adımın alanına özel diğer spesifikasyonlar

okunur.

Amaç her defasında bütün repoyu körlemesine okumak değildir; önce canonical yaşayan durum dosyaları, sonra başlanacak adım için gerekli bağlayıcı bağlam tazelenir. Ancak büyük scope/reindex değişikliği veya dokümantasyon drift şüphesi varsa repo-wide audit yapılır.

## PRE-STEP kontrolünde doğrulanacaklar

- Gerçek aktif adım hangisi?
- Önceki adım gerçekten tamamlandı mı?
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROJECT_CONTEXT` ve `MASTER_PLAN` aynı execution state'i gösteriyor mu?
- Kullanıcı tarafından kabul edilmiş ve yeni adımı sınırlayan kararlar neler?
- Yeni adımın beklenen çıktısı nedir?
- Hangi konular bilinçli olarak sonraki adıma bırakılmıştır?
- Çelişen/stale bir doküman varsa canonical kaynak hangisidir?
- Araştırma AI / Kodlama AI / Test AI gerekip gerekmediği nedir?

Bu kontrol tamamlanmadan yeni adım için kalıcı tasarım kararı verilmez.

---

# 2. STEP EXECUTION — Adım sırasında

Adım uygulanırken:

- daha önce kilitlenmiş kararlar sessizce değiştirilmez,
- açık kalan konular adım kapsamı dışında ise uydurularak doldurulmaz,
- yeni kalıcı ürün/mimari kararları decision kaydına aday olarak işaretlenir,
- araştırma/kodlama/test gereksinimleri `docs/AI_AGENT_WORKFLOW.md` protokolüne göre ayrılır,
- kullanıcıyla adım sırasında netleşen önemli davranışlar sohbet içinde bırakılmaz; adım kapanırken ilgili spec'e taşınır,
- future-stage numarası veya dosya adı değiştiyse etkilenen cross-reference'lar POST-STEP'te repo-wide taranmak üzere işaretlenir.

---

# 3. Dosya rolleri — benzer görünen dosyalar neden var?

Aynı bilgiyi amaçsızca çoğaltmak yasaktır. Yaşayan dosyaların rolleri ayrıdır:

## `PROJECT_CONTEXT.md` — kısa yaşayan proje snapshot'ı
- Kök dizindeki kısa ve hızlı hafıza dosyasıdır.
- Güncel ana yönü, önemli kararları ve **mevcut execution state'i** özetler.
- **Her numaralı adım sonunda kontrol edilir ve execution state değiştiyse mutlaka güncellenir.**

## `docs/START_HERE.md` — yeni sohbet / yeni agent bootstrap
- Yeni oturumun nereden başlayacağını ve ne okuyacağını söyler.
- Güncel aktif adımı ve canonical okuma sırasını taşır.
- **Her numaralı adım sonunda kontrol edilir; aktif adım değiştiyse güncellenir.**

## `docs/HANDOFF_STATE.md` — ayrıntılı current handoff
- Son kabul edilen modelleri, aktif adımın kapsamını ve doğrudan okunacak dosyaları ayrıntılı verir.
- **Her numaralı adım sonunda güncellenir.**

## `docs/STEP_STATUS.md` — kısa execution tablosu
- En hızlı durum kontrolüdür.
- **Her numaralı adım sonunda güncellenir.**

## `docs/EXECUTION_INDEX.md` — canonical adım kimlikleri ve sıra
- Adım kodlarının source of truth'udur.
- **Her numaralı adım sonunda checkbox/aktif iş açısından kontrol edilir ve gerekiyorsa güncellenir.**

## `docs/MASTER_PLAN.md` — ayrıntılı geliştirme checklist'i
- Aşamaların detaylarını ve completion notlarını taşır.
- **Her numaralı adım sonunda canonical state ile senkron kontrolü zorunludur.**

## `docs/PROGRESS_LOG.md` — kronolojik tarihçe
- Ne zaman ne yapıldığını ve nedenini append-only mantıkla kaydeder.
- **Her numaralı adım sonunda yeni kayıt eklenir.**

## `docs/DECISIONS.md` — kalıcı karar günlüğü
- Her adımda kontrol edilir.
- Yalnız yeni kalıcı karar oluştuysa yeni decision eklenir; sırf step ilerledi diye gereksiz decision üretilmez.

## `docs/PROJECT_MASTER_CONTEXT.md` — uzun ve nispeten stabil proje bağlamı
- Ürün amacı, felsefe, ana mimari/öğrenme yaklaşımı gibi uzun ömürlü bağlamı taşır.
- **Volatile aktif adımı tekrar etmez.** Current execution için `STEP_STATUS` / `HANDOFF_STATE` kullanılır.
- Yalnız büyük ürün/scope/felsefe değişikliklerinde güncellenir.

## `README.md` — insan için repo giriş sayfası
- Genel amacı ve canonical dokümanlara navigasyonu verir.
- Volatile aktif step'i kopyalamaz; current state için `STEP_STATUS`/`HANDOFF_STATE`e yönlendirir.
- Yalnız repo giriş anlatımı değiştiğinde güncellenir.

## Stable spec / research dosyaları
- Tamamlanmış kararların davranış kaydıdır.
- Sırf aktif step değişti diye yeniden yazılmaz.
- Ancak stage reindex, dosya rename, superseded contract veya yanlış future-reference oluşursa cross-reference düzeltilir.

---

# 4. POST-STEP — Her numaralı adım sonunda zorunlu GitHub senkronu

Bir numaralı adım ancak çıktı kabul edilebilir hale geldikten sonra kapatılır.

## 4.1 Her adımda zorunlu ALWAYS-CHECK / gerektiğinde ALWAYS-SYNC seti

Aşağıdakiler **istisnasız her numaralı adım sonunda kontrol edilir**:

1. adımın ana spec/çıktı dosyası,
2. `docs/EXECUTION_INDEX.md`,
3. `docs/STEP_STATUS.md`,
4. `docs/HANDOFF_STATE.md`,
5. `docs/PROGRESS_LOG.md`,
6. `docs/MASTER_PLAN.md`,
7. `PROJECT_CONTEXT.md`,
8. `docs/START_HERE.md`,
9. `docs/DECISIONS.md`.

Durum/karar değişikliğinden etkilenen dosya aynı POST-STEP içinde güncellenir. Özellikle `PROJECT_CONTEXT.md`, `START_HERE.md`, `HANDOFF_STATE.md` ve `STEP_STATUS.md` eski aktif adımda bırakılamaz.

## 4.2 Etki varsa güncellenecek dosyalar

Aşağıdakiler her adımda okunmak zorunda değildir fakat değişiklik bunları etkiliyorsa aynı POST-STEP içinde güncellenir:

- `README.md`,
- `docs/PROJECT_MASTER_CONTEXT.md`,
- `docs/PRODUCT_REQUIREMENTS.md`,
- `docs/PRODUCT_VISION.md`,
- `docs/PROFESSIONAL_READINESS_TARGET.md`,
- `docs/V1_SCOPE.md`,
- `docs/V1_SUCCESS_CRITERIA.md`,
- `docs/NON_GOALS.md`,
- curriculum/domain/English belgeleri,
- tamamlanmış stable spec'lerdeki future-stage veya renamed-file cross-reference'ları,
- ilgili research/provenance belgeleri.

## 4.3 Repo-wide stale-reference kontrolü

Her adım kapanışında en azından değişen state/karar isimleri için repo-wide arama yapılır. Aşağıdakiler özellikle aranır:

- eski `Aktif adım` / `henüz yürütülmedi` iddiaları,
- superseded decision/model adları,
- silinen/rename edilen dosya yolları,
- eski stage numaraları,
- geri çekilmiş kararların canonical gibi kullanımı,
- aynı kavram için birbiriyle çelişen yaşayan özetler.

Stale referans bulunduysa:
- yaşayan/current dosyada ise aynı POST-STEP'te düzeltilir,
- historical log/spec içinde geçmiş zamanı anlatıyorsa tarihsel bağlam korunur,
- belirsizse `historical` veya `non-canonical` etiketi eklenir; sessizce anlam değiştirilmez.

## 4.4 Doğrulama araçlarının iki ayrı sınıfı — 2026-08-29 açıklaması

Repo'daki `tools/` script'leri iki farklı ömre sahiptir ve karıştırılmamalıdır.

### `tools/validate_*.py` — standing regression suite
Kabul edilmiş bir modelin kalıcı davranış sözleşmesini doğrular. **Her zaman PASS vermelidir.** Buradaki bir FAIL ya gerçek bir regresyondur ya da kendisi stale kalmış bir living gate'tir ve aynı POST-STEP içinde düzeltilir.

Örnek: 8C POST audit'i sırasında `validate_english_entry_diagnostic.py` içindeki `E7A-15`, 7B'nin resolved ettiği `review.6c.english.cefr_alignment` review'ının hâlâ `open` olduğunu iddia ederken bulundu ve assertion 7B ownership-handoff'una daraltıldı.

### `tools/audit_*_post_step_stale.py` — one-time step-closure gate
Yalnız kendi adımının kapanış anındaki state'i doğrular ve tasarımı gereği `<step> complete AND <next step> active-not-executed` iddiasını sabitler.

Bir sonraki numaralı adım tamamlandığı anda bu iddia doğal olarak geçersizleşir ve script FAIL vermeye başlar. **Bu bir regresyon değildir ve düzeltilmemelidir**; script'in amacı zaten o anın kanıtını dondurmaktır. Kalıcı kanıt, script'in ürettiği `stale_reference_audit.yaml` raporudur.

Bağlayıcı sonuç:

- standing regression sweep yalnız `validate_*.py` script'lerini içerir,
- `audit_*_post_step_stale.py` yalnız kendi adımının POST-STEP'inde çalıştırılır,
- eski bir closure audit'inin FAIL vermesi current state hakkında hiçbir şey söylemez ve current state iddiası olarak kullanılamaz.

**Somut tehlike:** bu script'ler çalıştıklarında kendi `stale_reference_audit.yaml` raporlarını yeniden yazar. Eski bir closure audit'ini merakla çalıştırmak, o adımın dondurulmuş PASS kanıtını FAIL ile ezer. 8D POST'unda bu bir kez yaşandı ve dört rapor (`7E`, `8A`, `8B`, `8C`) `git checkout --` ile geri alındı. Eski closure audit'leri çalıştırılmaz; kanıt için raporları okunur.

---

# 5. Stage reindex / plan değişikliği için özel kural

Bir plan değişikliği future stage numaralarını etkilediğinde yalnız `MASTER_PLAN` ve `EXECUTION_INDEX` değiştirmek yeterli değildir.

Aynı senkron turunda:

1. repo-wide eski stage referansı aranır,
2. yaşayan/current belgeler yeni stage numarasına taşınır,
3. stable spec'lerdeki future-reference'lar düzeltilir,
4. historical completion metni geçmişi anlatıyorsa korunur,
5. `START_HERE`, `PROJECT_CONTEXT`, `HANDOFF_STATE`, `STEP_STATUS` ve README navigasyonu yeniden kontrol edilir.

Bu tarama yapılmadan reindex tamamlanmış sayılmaz.

Canonical legacy→current future-stage çeviri kaydı: `docs/STAGE_REINDEX_MAP.md`. Bu map current execution source of truth değildir; current adımlar için daima `EXECUTION_INDEX.md` / `MASTER_PLAN.md` kullanılır.

---

# 6. Gereksiz / duplicate doküman politikası

- Aynı role sahip iki yaşayan source of truth tutulmaz.
- Eski taslak yeni canonical spec tarafından tamamen supersede edilmiş ve benzersiz provenance değeri taşımıyorsa silinebilir.
- Faydalı seed/araştırma notu taşıyan eski dosya silinmek yerine açıkça `NON-CANONICAL / SEED / HISTORICAL` etiketlenebilir.
- Bir dosya silinmeden önce repo içi referansları kontrol edilir ve canonical replacement belirtilir.
- Silme işlemi geçmiş karar/spec davranışını değiştiremez.

---

# 7. Adım kapanış doğrulaması

Adım `✅ Tamamlandı` yapılmadan önce ana yönetici şu soruların hepsine cevap verebilmelidir:

- Ana çıktı/spec GitHub'da mevcut mu?
- Yeni kararlar gerekiyorsa `DECISIONS.md` içine işlendi mi?
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROJECT_CONTEXT` ve `MASTER_PLAN` aynı ilerleme durumunu gösteriyor mu?
- `START_HERE` yeni sohbeti doğru aktif adıma götürüyor mu?
- `PROGRESS_LOG` bu adımı kaydediyor mu?
- Repo-wide stale-reference taraması yapıldı mı?
- Eski stage/file/decision pointer'ı yaşayan belgelerde kaldı mı?
- Yeni sohbet yalnız GitHub'ı okuyarak doğru yerden devam edebilir mi?
- Açık kalan konular doğru sonraki adımlara bırakılmış mı?

Bunlardan kritik olan biri hayır ise adım kapanmış sayılmaz.

---

# 8. Yeni sohbet / aynı sohbet fark etmez

Bu protokol yalnız sohbet değiştiğinde uygulanmaz.

Aynı ChatGPT konuşması içinde art arda `2C → 2D → 2E` geçilecek olsa bile **her yeni numaralı adımın başında GitHub PRE-STEP beyin tazelemesi yeniden yapılır.**

Sebep:

- GitHub projenin durable source of truth kaynağıdır,
- önceki adım sonunda birden fazla dosya değişmiş olabilir,
- sohbet hafızası ile repo arasında drift oluşması engellenir,
- başka agent veya kullanıcı repo üzerinde değişiklik yaptıysa yeni adım stale bilgiyle başlamaz.

---

# 9. Kullanıcıya tekrar sordurmama ilkesi

GitHub beyin tazelemesinin amaçlarından biri kullanıcının daha önce verdiği cevapları yeniden sormamaktır.

Repo içinde yeterince açık kabul edilmiş karar varsa ana yönetici onu yeniden onaya sunmaz. Yalnız:

- gerçek bir çelişki,
- önemli yeni trade-off,
- kapsam değişikliği,
- geri dönüşü zor kullanıcı tercihi

oluşursa yeni karar gündeme getirilir.

---

# 10. Canonical protokol özeti

`PRE-STEP GitHub refresh → yaşayan state dosyaları tutarlılık kontrolü → adımı yürüt → gerekirse Research/Coding/QA → sonucu değerlendir → ALWAYS-CHECK POST seti → etkilenen stable docs → repo-wide stale-reference scan → sonraki adımı aktif yap`

Bu döngü tüm proje boyunca zorunludur.