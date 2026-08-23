# AI Agent Workflow — Araştırma / Kodlama / Test İş Bölümü

Bu belge, AI Infra Learning Coach projesinde birden fazla yapay zekâ aracının nasıl birlikte kullanılacağını tanımlar. Amaç aynı işi üç AI'a yaptırmak değil; uzman rolleri ayırarak araştırma, implementasyon ve doğrulamayı birbirinden bağımsız hale getirmektir.

## 1. Roller

### 1.1 Ana Yönetici / Ürün ve Mimari Koordinatörü

Ana yönetici proje bağlamını, `MASTER_PLAN.md`, `DECISIONS.md`, `HANDOFF_STATE.md` ve ilgili teknik spesifikasyonları esas alır.

Sorumlulukları:
- sıradaki işi seçmek,
- işi doğru AI rolüne vermek,
- araştırma sonuçlarını ürün kararına çevirmek,
- kodlama AI'ına uygulanabilir görev/spec hazırlamak,
- test AI'ının bulgularını değerlendirmek,
- başarısız testte işi tekrar kodlama aşamasına döndürmek,
- yalnız kabul kriterleri sağlandığında işi tamamlanmış saymak,
- GitHub proje hafızasını güncel tutmak.

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
→ Yönetici sonucu kontrol eder ve ilgili master plan maddesini tamamlar.

**7. GitHub hafızası güncellenir**
→ `MASTER_PLAN.md` checkbox + completion note
→ gerekirse `DECISIONS.md`
→ `PROGRESS_LOG.md`
→ önemli durum değiştiyse `HANDOFF_STATE.md`

## 3. Hangi İş Hangi AI'a Gider?

### Araştırma AI'a öncelikli gönder
- 'Hangisini seçmeliyiz?'
- güncel dış bilgi gereken sorular
- kaynak doğrulama
- algoritma/literatür karşılaştırması
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

Buna karşılık mastery algoritması gibi kritik bir konuda:
Yönetici → Araştırma AI → Spec → Kodlama AI → Test AI → gerekirse iterasyon gerekir.

Amaç süreç yaratmak değil, hata riskine göre doğru doğrulama seviyesini kullanmaktır.

## 5. Bağımsızlık Kuralı

Aynı AI mümkünse hem ana implementasyonu hem nihai QA'yı yapmamalıdır.

Sebep: Kendi varsayımlarını ve hatalarını tekrar etme riski.

Kodlama AI 'çalışıyor' dedi diye görev tamamlanmış kabul edilmez. Test AI veya yöneticinin bağımsız kabul kontrolü gerekir.

## 6. Kaynak Gerçeği ve Karar Gerçeği Ayrımı

Araştırma AI'ın raporu `araştırma girdisi`dir.

Kalıcı ürün kararı ancak ana yönetici tarafından değerlendirildikten ve `DECISIONS.md` içine işlendiğinde kabul edilmiş sayılır.

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

Bu, agent'ın projeyi yeniden yorumlamasını azaltır.

## 8. GitHub Branch / PR Stratejisi — Kod Başladığında

Kodlama aşamasına geçildiğinde önerilen akış:

- `main`: kabul edilmiş stabil durum
- feature/fix branch: kodlama AI'ın çalışma alanı
- test/QA sonucu PASS olmadan main'e merge edilmez
- önemli feature'larda PR açıklamasında acceptance criteria tutulur

Kişisel proje olduğu için gereksiz ağır süreç kurulmaz; ancak geri dönüş ve bağımsız test için branch/PR yaklaşımı tercih edilir.

## 9. Tamamlanma Tanımı

Bir geliştirme işi ancak aşağıdakiler sağlandığında tamamlanmış sayılır:

1. İstenen davranış implement edildi.
2. Acceptance criteria karşılandı.
3. Kritik testler geçti.
4. Bilinen kritik bug yok.
5. Gerekli dokümantasyon güncellendi.
6. Master plan completion note yazıldı.

Kodun yazılmış olması tek başına 'tamamlandı' değildir.

## 10. Projedeki Rol Dağılımının Özeti

**Ana Yönetici:** Ne yapılacağını ve neyin kabul edileceğini belirler.

**Araştırma AI:** Doğru bilgi ve seçenekleri getirir.

**Kodlama AI:** Onaylanmış tasarımı uygular.

**Test AI:** Uygulamanın gerçekten istenen şeyi yaptığını bağımsız doğrular.

Bu iş bölümü projenin ilerleyen tüm aşamalarında kullanılabilir ve ihtiyaç halinde yeni uzman AI rolleri eklenebilir.