# AI Agent Workflow — Araştırma / Kodlama / Test İş Bölümü

Bu belge, AI Infra Learning Coach projesinde birden fazla yapay zekâ aracının nasıl birlikte kullanılacağını tanımlar. Amaç aynı işi üç AI'a yaptırmak değil; uzman rolleri ayırarak araştırma, implementasyon ve doğrulamayı birbirinden bağımsız hale getirmektir.

## 0. Zorunlu proje hafızası döngüsü

Bağlayıcı kaynak: `docs/PROJECT_MEMORY_PROTOCOL.md`.

Her numaralı proje adımı (`1A`, `2E`, `3A`, `12F` vb.) şu döngüyle yürütülür:

**PRE-STEP GitHub refresh → yaşayan state dosyalarının tutarlılık kontrolü → adımı yürüt → gerekirse Research/Coding/QA → sonucu değerlendir → POST-STEP living-memory sync → repo-wide stale-reference scan → sonraki adımı aktif yap**

PRE-STEP sırasında minimum olarak `HANDOFF_STATE.md`, `EXECUTION_INDEX.md`, `STEP_STATUS.md`, `DECISIONS.md`, `MASTER_PLAN.md`, root `PROJECT_CONTEXT.md` ve o adımla ilgili en güncel spec/davranış dosyaları kontrol edilir. Aynı sohbet içinde bir sonraki numaralı adıma geçiliyor olsa bile bu refresh yeniden yapılır.

D-050 sonrası POST-STEP'te `PROJECT_MEMORY_PROTOCOL.md` içindeki **ALWAYS-CHECK** seti bağlayıcıdır. Özellikle `PROJECT_CONTEXT`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` kontrol edilmeden adım kapatılamaz. Stage/file/model adı değiştiyse repo-wide stale-reference araması aynı turda yapılır.

---

## 1. Roller

### 1.1 Ana Yönetici / Ürün ve Mimari Koordinatörü

**D-055 local-manager clarification:** Ana yönetici cloud/chat manager olmak zorunda değildir; local çalışan agent bu rolü devralabilir. Terminal/tool erişimi authority contract'ını değiştirmez. Local manager da `AGENTS.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/PROJECT_MEMORY_PROTOCOL.md` ve bütün canonical specs'e bağlıdır. Manager transition tek başına numbered step execution değildir.

Ana yönetici proje bağlamını, `PROJECT_MEMORY_PROTOCOL.md`, `MASTER_PLAN.md`, `DECISIONS.md`, `HANDOFF_STATE.md` ve ilgili teknik spesifikasyonları esas alır.

Sorumlulukları:
- her numaralı adım öncesi zorunlu GitHub beyin tazelemesini yapmak,
- `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROJECT_CONTEXT` ve `MASTER_PLAN` ilerleme tutarlılığını kontrol etmek,
- sıradaki işi seçmek,
- işi doğru AI rolüne vermek,
- araştırma sonuçlarını ürün kararına çevirmek,
- kodlama AI'ına uygulanabilir görev/spec hazırlamak,
- test AI'ının bulgularını değerlendirmek,
- başarısız testte işi tekrar kodlama aşamasına döndürmek,
- yalnız kabul kriterleri sağlandığında işi tamamlanmış saymak,
- adım kapanışında D-050 living-memory setini ve MASTER_PLAN checklist'ini senkronize etmek,
- repo-wide stale step/file/decision pointer'larını taramak,
- GitHub proje hafızasını durable source of truth olarak güncel tutmak.

Ana yönetici mümkün olduğunca doğrudan büyük kod blokları üretmek yerine görevleri koordine eder; ancak küçük doğrulama, mimari değerlendirme ve dokümantasyon yapabilir.

### 1.2 Araştırma AI

Görevi dış bilgi gerektiren konularda kanıt toplamak ve seçenekleri karşılaştırmaktır.

Kullanım alanları:
- teknoloji seçimi,
- öğrenme bilimi / mastery model araştırması,
- spaced repetition yöntemleri,
- adaptive learning yaklaşımları,
- mobil framework karşılaştırması,
- SQLite/local persistence seçenekleri,
- AI API/provider seçenekleri,
- curriculum doğrulaması,
- teknik kariyer ve iş piyasası araştırması,
- güncel kütüphane/dokümantasyon doğrulaması.

Araştırma AI'dan beklenen çıktı:
1. araştırma sorusu,
2. kaynaklar,
3. bulgular,
4. alternatifler,
5. trade-off'lar,
6. riskler,
7. öneri,
8. güven seviyesi / belirsizlikler.

Araştırma sonucu otomatik olarak ürün kararı değildir. Ana yönetici sonucu proje bağlamıyla değerlendirir.

### 1.3 Kodlama AI

Görevi onaylanmış spesifikasyonu çalışan koda dönüştürmektir.

Kodlama AI'a görev verilmeden önce mümkün olduğunca şunlar sağlanır:
- görev amacı,
- mevcut repo/branch,
- ilgili dokümanlar,
- değiştirilecek kapsam,
- veri modeli veya API sözleşmesi,
- UI davranışı,
- acceptance criteria,
- test beklentileri,
- kapsam dışı alanlar.

Kodlama AI'ın beklenen çıktısı:
- yapılan değişikliklerin özeti,
- değişen dosyalar,
- alınan teknik kararlar,
- eklenen testler,
- çalıştırılan komutlar,
- bilinen sınırlamalar,
- test AI'a devredilecek notlar.

Kodlama AI kendi implementasyonunu nihai olarak onaylamaz.

### 1.4 Test / QA AI

Kodlama AI'dan bağımsız doğrulama rolüdür.

Görevleri:
- acceptance criteria'yı tek tek test etmek,
- happy path ve edge case testleri,
- regression kontrolü,
- state/persistence testleri,
- adaptive planner davranış testleri,
- mastery hesaplama testleri,
- UI/UX davranış kontrolü,
- yanlış/eksik implementasyon aramak,
- mümkünse otomatik test eklemek veya önerisini vermek.

Test AI sonucu şu sınıflardan biri olmalıdır:
- PASS — kabul kriterleri sağlandı.
- PASS WITH NOTES — çalışıyor fakat kritik olmayan notlar var.
- FAIL — kabul kriterlerinden biri veya daha fazlası sağlanmadı.
- BLOCKED — test için eksik ortam/veri/bağımlılık var.

FAIL durumunda iş tamamlanmış sayılmaz ve kodlama AI'a geri döner.

## 2. Standart İş Akışı

Her önemli özellik/karar için varsayılan akış:

**0. PRE-STEP GitHub beyin tazelemesi**
→ Aktif adım, önceki kararlar, ilgili spec'ler, `PROJECT_CONTEXT`, `EXECUTION_INDEX` ve `MASTER_PLAN` kapsamı doğrulanır.

**1. Yönetici problemi tanımlar**
→ Ne çözülüyor, neden gerekli, başarı kriteri nedir?

**2. Gerekirse Araştırma AI**
→ Kanıt, seçenek, trade-off ve öneri üretir.

**3. Yönetici karar/spec oluşturur**
→ Araştırma sonucu proje ihtiyaçlarıyla birleştirilir ve uygulanabilir görev haline getirilir.

**4. Kodlama AI implement eder**
→ Kod + kendi testleri + değişiklik raporu.

**5. Test AI bağımsız test eder**
→ Acceptance criteria + edge case + regression.

**6A. FAIL ise**
→ Bulgular kodlama AI'a gönderilir → düzeltme → tekrar QA.

**6B. PASS ise**
→ Yönetici sonucu kontrol eder.

**7. POST-STEP GitHub hafızası senkronize edilir**
→ ana spec/çıktı
→ `EXECUTION_INDEX.md`
→ `STEP_STATUS.md`
→ `HANDOFF_STATE.md`
→ `PROGRESS_LOG.md`
→ `MASTER_PLAN.md`
→ `PROJECT_CONTEXT.md`
→ `START_HERE.md`
→ `DECISIONS.md` kontrolü / gerekiyorsa yeni decision
→ etkilenen `README`, `PROJECT_MASTER_CONTEXT`, product/V1/curriculum/stable specs
→ repo-wide stale-reference scan

Bu POST-STEP senkronizasyonu yapılmadan numaralı adım tamamlanmış sayılmaz.

## 3. Hangi İş Hangi AI'a Gider?

### Araştırma AI'a öncelikli gönder
- `Hangisini seçmeliyiz?`
- güncel dış bilgi gereken sorular
- kaynak doğrulama
- algoritma/literatür karşılaştırması
- mastery / learning-science model araştırması
- spaced repetition
- curriculum veya kariyer araştırması
- framework/library güncelliği

### Kodlama AI'a öncelikli gönder
- ekran geliştirme
- veri modeli implementasyonu
- local DB
- planner/mastery kodu
- test altyapısı
- refactor
- bug fix
- APK/build işleri

### Test AI'a öncelikli gönder
- yeni feature doğrulaması
- bug fix sonrası regression
- planner simülasyonları
- hesaplama/threshold testleri
- persistence ve restore testleri
- release öncesi QA

## 4. Araştırma Gerektirmeyen İşlerde Gereksiz Tur Yok

Her görev üç AI'dan geçmek zorunda değildir.

Örneğin çok net bir UI metin düzeltmesi:
Yönetici → Kodlama AI → Test AI yeterlidir.

Buna karşılık mastery formülü veya forgetting modeli gibi kritik bir konuda:
Yönetici → Araştırma AI → Spec → gerektiğinde simülasyon/QA → karar gerekir.

Amaç süreç yaratmak değil, hata riskine göre doğru doğrulama seviyesini kullanmaktır.

## 5. Bağımsızlık Kuralı

Aynı AI mümkünse hem ana implementasyonu hem nihai QA'yı yapmamalıdır.

Kodlama AI `çalışıyor` dedi diye görev tamamlanmış kabul edilmez. Test AI veya yöneticinin bağımsız kabul kontrolü gerekir.

## 6. Kaynak Gerçeği ve Karar Gerçeği Ayrımı

Araştırma AI'ın raporu `araştırma girdisi`dir.

Kalıcı ürün kararı ancak ana yönetici tarafından değerlendirildikten ve gerekiyorsa `DECISIONS.md` içine işlendiğinde kabul edilmiş sayılır.

Benzer şekilde kodlama AI'ın teknik tercihi, daha önce kabul edilmiş mimari karara aykırıysa otomatik kabul edilmez.

## 7. Görev Paketleme Standardı

Kodlama veya test AI'a gönderilen önemli görevlerde mümkünse şu şablon kullanılır:

- Bağlam
- Amaç
- İlgili repo/dosyalar
- Mevcut davranış
- İstenen davranış
- Acceptance criteria
- Edge case'ler
- Değiştirilmemesi gerekenler
- Test komutları / beklenen doğrulama
- Çıktı raporu formatı

## 8. GitHub Branch / PR Stratejisi — Kod Başladığında

- `main`: kabul edilmiş stabil durum
- feature/fix branch: kodlama AI'ın çalışma alanı
- test/QA sonucu PASS olmadan main'e merge edilmez
- önemli feature'larda PR açıklamasında acceptance criteria tutulur

Kişisel proje olduğu için gereksiz ağır süreç kurulmaz; ancak geri dönüş ve bağımsız test için branch/PR yaklaşımı tercih edilir.

## 9. Tamamlanma Tanımı

Bir geliştirme işi ancak aşağıdakiler sağlandığında tamamlanmış sayılır:

1. PRE-STEP GitHub beyin tazelemesi yapılmıştır.
2. Yaşayan state dosyaları başlangıçta tutarlıdır veya stale kayıt düzeltilmiştir.
3. İstenen davranış implement edilmiş veya adımın beklenen spec/çıktısı üretilmiştir.
4. Acceptance criteria karşılanmıştır.
5. Gerekli kritik testler geçmiştir.
6. Bilinen kritik bug/açık engel yoktur.
7. Gerekli dokümantasyon güncellenmiştir.
8. D-050 ALWAYS-CHECK POST seti uygulanmıştır.
9. `MASTER_PLAN` checkbox/completion note günceldir.
10. Yeni aktif adım doğru kaydedilmiştir.
11. Repo-wide stale-reference scan yapılmıştır.

Kodun veya dokümanın üretilmiş olması tek başına `tamamlandı` değildir.

## 10. Projedeki Rol Dağılımının Özeti

**Ana Yönetici:** Ne yapılacağını ve neyin kabul edileceğini belirler; her adımın GitHub ve MASTER_PLAN hafıza bütünlüğünden sorumludur.

**Araştırma AI:** Doğru bilgi ve seçenekleri getirir.

**Kodlama AI:** Onaylanmış tasarımı uygular.

**Test AI:** Uygulamanın gerçekten istenen şeyi yaptığını bağımsız doğrular.

Dosya rollerinin ve mandatory living-memory setinin tek canonical tanımı `docs/PROJECT_MEMORY_PROTOCOL.md` içindedir.