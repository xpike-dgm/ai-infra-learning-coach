# Project Memory Protocol — Zorunlu GitHub Beyin Tazeleme Döngüsü

**Durum:** BAĞLAYICI ÇALIŞMA PROTOKOLÜ  
**Tarih:** 2026-08-24

Bu belge AI Infra Learning Coach projesinde her numaralı adımın (`1A`, `2C`, `3A`, `11F` vb.) nasıl başlatılıp nasıl kapatılacağını tanımlar.

Ana kural:

> **Hiçbir numaralı proje adımı GitHub beyin tazelemesi yapılmadan başlatılmaz; hiçbir numaralı proje adımı gerekli GitHub hafıza dosyaları güncellenmeden tamamlanmış sayılmaz.**

---

# 1. PRE-STEP — Adım başlamadan önce zorunlu beyin tazelemesi

Yeni bir numaralı adıma başlanmadan hemen önce ana yönetici GitHub'daki güncel durumu okumalıdır.

Minimum zorunlu kontrol seti:

1. `docs/HANDOFF_STATE.md` — mevcut proje konumu ve son kabul edilen durum.
2. `docs/EXECUTION_INDEX.md` — adım kimliği, sırası ve tamamlanma durumu.
3. `docs/STEP_STATUS.md` — aktif adımın hızlı doğrulaması.
4. `docs/DECISIONS.md` — ilgili bağlayıcı kararların kontrolü.
5. Başlanacak adımla doğrudan ilgili en güncel spec/davranış dosyaları.

Gerekirse ayrıca:

- `docs/START_HERE.md`
- `docs/PROJECT_MASTER_CONTEXT.md`
- `PROJECT_CONTEXT.md`
- `docs/V1_SCOPE.md`
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/NON_GOALS.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/AI_AGENT_WORKFLOW.md`
- `docs/PROGRESS_LOG.md`
- adımın alanına özel diğer spesifikasyonlar

okunur.

Amaç her defasında bütün repoyu körlemesine okumak değil; **önce canonical güncel durum dosyalarını, sonra başlayacak adım için gerekli bağlayıcı bağlamı** tazelemektir.

## PRE-STEP kontrolünde doğrulanacaklar

- Gerçek aktif adım hangisi?
- Önceki adım gerçekten tamamlandı mı?
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
- kullanıcıyla adım sırasında netleşen önemli davranışlar sohbet içinde bırakılmaz; adım kapanırken ilgili spec'e taşınır.

---

# 3. POST-STEP — Adım bittikten sonra zorunlu GitHub güncellemesi

Bir numaralı adım ancak çıktı kabul edilebilir hale geldikten sonra kapatılır.

Adım sonunda en az şu dosyalar **gerekiyorsa** güncellenir:

- adımın ana spec/çıktı dosyası,
- `docs/EXECUTION_INDEX.md` — checkbox/durum ve completion note,
- `docs/STEP_STATUS.md` — son tamamlanan ve yeni aktif adım,
- `docs/HANDOFF_STATE.md` — güncel proje konumu ve yeni bağlayıcı bilgiler,
- `docs/PROGRESS_LOG.md` — tarihli çalışma/tamamlanma kaydı,
- `docs/DECISIONS.md` — yeni kalıcı karar oluştuysa,
- `docs/START_HERE.md` — başlangıç/handoff davranışı veya güncel yönü etkileyen değişiklik varsa,
- `docs/PROJECT_MASTER_CONTEXT.md` — yalnız büyük ürün amacı/felsefesi değiştiyse,
- `docs/MASTER_PLAN.md` — ilgili ayrıntılı checklist/completion notu güncel tutulması gerekiyorsa.

Her dosya her adımda zorunlu olarak değiştirilmez; **gerçekten etkilenen canonical dosyalar güncellenir.** Ancak `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` ve `EXECUTION_INDEX` adım kapanışında durum değişikliğini yansıtacak şekilde kontrol edilmeden adım tamamlanmış sayılmaz.

---

# 4. Adım kapanış doğrulaması

Adım `✅ Tamamlandı` yapılmadan önce ana yönetici şu soruların hepsine cevap verebilmelidir:

- Ana çıktı/spec GitHub'da mevcut mu?
- Yeni kararlar karar günlüğüne işlendi mi?
- Aktif adım bir sonrakine taşındı mı?
- Yeni sohbet yalnız GitHub'ı okuyarak doğru yerden devam edebilir mi?
- Önceki sohbet bilinmese bile adımın sonucu ve gerekçesi anlaşılabiliyor mu?
- Açık kalan konular doğru sonraki adımlara bırakılmış mı?

Bunlardan kritik olan biri hayır ise adım kapanmış sayılmaz.

---

# 5. Yeni sohbet / aynı sohbet fark etmez

Bu protokol yalnız sohbet değiştiğinde uygulanmaz.

Aynı ChatGPT konuşması içinde art arda `2C → 2D → 2E` geçilecek olsa bile **her yeni numaralı adımın başında GitHub PRE-STEP beyin tazelemesi yeniden yapılır.**

Sebep:

- GitHub projenin durable source of truth kaynağıdır,
- önceki adım sonunda birden fazla dosya değişmiş olabilir,
- sohbet hafızası ile repo arasında drift oluşması engellenir,
- başka agent veya kullanıcı repo üzerinde değişiklik yaptıysa yeni adım stale bilgiyle başlamaz.

---

# 6. Kullanıcıya tekrar sordurmama ilkesi

GitHub beyin tazelemesinin amaçlarından biri kullanıcının daha önce verdiği cevapları yeniden sormamaktır.

Repo içinde yeterince açık kabul edilmiş karar varsa ana yönetici onu yeniden onaya sunmaz. Yalnız:

- gerçek bir çelişki,
- önemli yeni trade-off,
- kapsam değişikliği,
- geri dönüşü zor kullanıcı tercihi

oluşursa yeni karar gündeme getirilir.

---

# 7. Canonical protokol özeti

`PRE-STEP GitHub refresh → adımı yürüt → gerekirse research/coding/QA → sonucu değerlendir → POST-STEP GitHub sync → sonraki adımı aktif yap`

Bu döngü tüm proje boyunca zorunludur.
