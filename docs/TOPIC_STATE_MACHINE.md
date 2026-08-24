# Topic State Machine — AI Infra Learning Coach

**Adım:** 2B — Topic durumları  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge Topic'in kullanıcıya ve planner'a görünen çalışma durumunu tanımlar. `docs/LEARNING_ENGINE_SPEC.md` içindeki 2A kararları ve `docs/LEARNING_BEHAVIOR_RULES.md` içindeki bağlayıcı öğrenme davranışları bu state machine'in temelidir.

Ana ilke:

> **Topic state mastery'nin kendisi değildir. Topic state; prerequisite uygunluğu, coverage, canonical Skill mastery, retention ve remediation sinyallerinden türetilen açıklanabilir bir çalışma durumudur.**

---

# 1. Neden Topic state gerekiyor?

Canonical mastery Skill seviyesinde tutulsa da kullanıcı ve planner şu sorulara hızlı cevap vermek zorundadır:

- Bu Topic'e henüz girebilir miyim?
- Bu Topic üzerinde şu anda çalışıyor muyum?
- Gerekli becerileri yeterince kanıtladım mı?
- Daha önce öğrendiğim Topic'te unutma riski mi oluştu?
- Hedefli remediation gerekli mi?

Topic state bu sorular için **derived orchestration/UX state** sağlar.

Topic state:

- bağımsız bir mastery puanı değildir,
- task completion'dan doğrudan çıkmaz,
- Skill mastery'nin yerine geçmez,
- prerequisite graph'ın canonical kaynağı değildir.

---

# 2. Canonical Topic state'leri

V1 için altı ana state kullanılır:

1. `locked`
2. `available`
3. `learning`
4. `mastered`
5. `weakening`
6. `remediation_required`

UI'da daha kullanıcı dostu Türkçe etiketler kullanılabilir:

| Internal state | Önerilen UI anlamı |
|---|---|
| `locked` | Kilitli |
| `available` | Hazır |
| `learning` | Öğreniliyor |
| `mastered` | Öğrenildi |
| `weakening` | Tekrar Gerekebilir / Zayıflıyor |
| `remediation_required` | Pekiştirme Gerekli |

UI kelimeleri daha sonra 7E'de kesinleşebilir; internal state kimlikleri sabit tutulmalıdır.

---

# 3. Topic state'in beslendiği veriler

State kararı en az aşağıdaki girdilerden türetilebilir:

- Topic daha önce başlatıldı mı?
- Topic'in entry için gereken hard prerequisite Skill'leri uygun mu?
- Topic'in required Learning Objective coverage durumu nedir?
- Topic'e bağlı required/critical canonical Skill mastery durumları nedir?
- Aktif remediation flag'i var mı?
- Daha önce mastered olmuş Skill'lerde retention riski var mı?
- Curriculum update ile yeni required objective/skill eklendi mi?

Kesin mastery threshold, remediation threshold ve retention matematiği 2C–2F'de tanımlanacaktır. 2B yalnız state davranışını kilitler.

---

# 4. `locked`

## Tanım

Topic henüz başlanabilir değildir çünkü giriş için gerekli en az bir **hard prerequisite Skill** gerekli uygunluk koşulunu karşılamıyordur.

## Kritik kural

`locked` esas olarak **henüz başlanmamış Topic'in giriş state'idir**.

Bir Topic daha önce gerçekten başlatıldıysa veya mastered olduysa, eski prerequisite daha sonra zayıfladığı için Topic geriye dönüp `locked` yapılmaz.

Böyle durumlarda Skill-level dependency yeniden değerlendirilir ve gerekirse Topic `weakening` veya `remediation_required` olur.

## Planner davranışı

- Topic yeni öğrenme görevi olarak seçilemez.
- Eksik prerequisite Skill için remediation/review başka Topic veya task üzerinden planlanabilir.
- Bağımsız curriculum dalları çalışmaya devam eder.

## `locked → available`

Tüm gerekli hard entry prerequisite Skill'ler uygun hale geldiğinde Topic `available` olur.

Soft prerequisite eksikleri Topic'i otomatik `locked` yapmaz; soft davranış 3D'de detaylandırılır.

---

# 5. `available`

## Tanım

Topic'in gerekli giriş prerequisite'leri karşılanmıştır ve kullanıcı Topic'e başlayabilir; ancak Topic henüz gerçek çalışma sürecine girmemiştir.

Bu state "öğrenildi" anlamına gelmez.

## Planner davranışı

- Yeni konu aday havuzuna girebilir.
- Planner priority/capacity kurallarına göre bugün veya sonraki bir gün seçilebilir.
- Kullanıcıya `Başlanabilir` şeklinde gösterilebilir.

## Normal geçiş

`available → learning`

Kullanıcı Topic'e ait ilk gerçek teach/practice/assessment oturumuna başladığında.

## Diagnostic istisnası

Kullanıcı Topic'i zaten biliyorsa ve ileride 3E'de tanımlanacak güvenilir diagnostic/skip doğrulaması gerekli Skill'leri kanıtlarsa:

`available → mastered`

doğrudan geçiş mümkün olabilir.

Bu durumda coverage'ın zorunlu öğretim kısmı **validated diagnostic waiver** ile karşılanmış kabul edilir; yalnız bir kolay quiz ile doğrudan mastery verilmez.

---

# 6. `learning`

## Tanım

Topic üzerinde gerçek çalışma başlamıştır fakat Topic'in gerekli mastery/coverage çıkış koşulları henüz tamamlanmamıştır ve akut remediation gerektiren bir durum yoktur.

`learning` şu durumları kapsayabilir:

- konu anlatımı devam ediyor,
- gerekli Learning Objective'lerin bir kısmı henüz işlenmedi,
- objective'ler işlendi fakat yeterli evidence henüz toplanmadı,
- Skill'ler gelişiyor fakat mastery gate henüz geçilmedi.

## Planner davranışı

- Devam eden Topic olarak yüksek uygunluğa sahiptir.
- Teach/practice/assessment görevleri seçilebilir.
- Kullanıcının kaldığı yerden devam etmesi desteklenir.

## `learning → mastered`

Aşağıdaki iki genel koşul birlikte sağlandığında:

1. **Coverage gate:** Topic'in required Learning Objective'leri öğretim/uygulama açısından kapsanmış veya geçerli diagnostic ile waiver almış olmalı.
2. **Mastery gate:** Topic'in required/critical Skill'leri 2E'de tanımlanacak gerekli evidence/threshold koşullarını sağlamalı.

Sadece `lesson completed` veya `coverage = 100%` yeterli değildir.

## `learning → remediation_required`

Evidence, Topic içindeki gerekli Skill'lerden birinde hedefli müdahale gerektiren gerçek eksiklik bulunduğunu gösterdiğinde.

Tek bir küçük yanlışın remediation tetikleyip tetiklemeyeceği 2C–2E evidence kurallarıyla belirlenir; 2B bunu otomatik kabul etmez.

---

# 7. `mastered`

## Tanım

Topic'in required coverage koşulu karşılanmış ve bağlı required/critical Skill'ler gerekli mastery gate'lerini geçmiştir. Aktif remediation ihtiyacı veya doğrulanmış retention riski yoktur.

Kritik anlam:

> `mastered`, "bu konu artık asla sorulmayacak" demek değildir.

Canonical Skill'ler daha sonra başka Topic'lerde reinforce edilebilir ve retention amacıyla yeniden ölçülebilir.

## Planner davranışı

- Normalde yeniden temel öğretim görevi üretmez.
- Due retention/reinforcement varsa ilgili Skill için görev gelebilir.
- Daha ileri Topic'lerde bu Skill doğal olarak tekrar kullanılabilir.

## `mastered → weakening`

2F'de tanımlanacak retention modeli bir veya daha fazla gerekli Skill için anlamlı unutma riski doğruladığında.

Tek bir rastgele hata otomatik olarak bütün Topic'i düşürmez. Risk kararı evidence/confidence kurallarına bağlıdır.

## `mastered → remediation_required`

Güçlü ve yeterince doğrulanmış evidence, kritik required Skill'in yalnız hafif unutma değil hedefli onarım gerektirecek düzeyde bozulduğunu gösterirse mümkündür.

## Curriculum değişikliği

Yeni curriculum versiyonu Topic'e yeni **required** Learning Objective veya Skill eklerse önceki `mastered` state otomatik olarak sahte biçimde korunmamalıdır.

State yeniden değerlendirilir:

- yeni requirement henüz öğretilmediyse çoğunlukla `learning`,
- mevcut Skill'de kanıtlanmış ciddi eksiklik varsa `remediation_required`.

Bu değişiklik reason code ile açıklanmalıdır.

---

# 8. `weakening`

## Tanım

Topic daha önce yeterli mastery ile tamamlanmıştır; ancak bağlı required Skill'lerden en az birinde **retention riski / zayıflama** tespit edilmiştir. Henüz tam remediation gerektirecek kadar güçlü eksiklik doğrulanmamış olabilir.

Bu state öğrenmenin silindiği anlamına gelmez.

## Planner davranışı

- Kısa retrieval/review/reinforcement görevi öncelik kazanabilir.
- Bütün Topic sıfırdan yeniden okutulmaz.
- Mümkünse eski Skill sonraki gerçek görev içinde doğal olarak yeniden kullandırılır.

## `weakening → mastered`

Yeni retention/retrieval evidence Skill'in yeterli düzeyde korunduğunu doğrularsa Topic yeniden `mastered` olur.

## `weakening → remediation_required`

Tekrarlanan veya daha güçlü başarısız evidence gerçek eksikliği doğrularsa.

## Future prerequisite etkisi

Topic state tek başına ileri Topic'i kilitlemez. Canonical Skill mastery/prerequisite gate yeniden hesaplanır.

Örneğin `Basic Pointers` Topic'i `weakening` olabilir ama yalnız `dereference_pointer` Skill'i hard prerequisite eşiğinin altına düştüyse ona bağlı yeni Topic'ler bekletilir.

---

# 9. `remediation_required`

## Tanım

Bir veya daha fazla required/critical Skill için evidence hedefli müdahale gerektiğini göstermektedir.

Bu state:

- ceza değildir,
- bütün Topic'in başarısız olduğu anlamına gelmez,
- geçmiş coverage'ı sıfırlamaz,
- bütün curriculum'u durdurmaz.

## Planner davranışı

Planner mümkün olduğunca eksik Skill/Objective'e özel remediation üretir:

- daha sade/alternatif anlatım,
- worked example,
- micro-drill,
- debugging,
- prerequisite geri dönüşü,
- AI Tutor açıklaması,
- farklı varyasyonla yeniden doğrulama.

Remediation günlük kapasitenin üstüne kontrolsüz eklenmez; 3A–3F kurallarıyla mevcut kapasite içine yerleştirilir.

## Bağımlı Topic'ler

Eksik Skill başka Topic'ler için hard prerequisite ise yalnız o bağımlı ilerleme bekleyebilir. Bağımsız Linux/English veya başka branch görevleri devam edebilir.

## `remediation_required → learning`

Akut remediation gereksinimi kalkmış fakat Topic'in gerekli mastery çıkış gate'i henüz tamamlanmamışsa.

## `remediation_required → mastered`

Özellikle daha önce mastered olan Topic'te, remediation sonrası required Skill'ler tekrar yeterli ve retention sağlıklı hale geldiyse doğrudan `mastered` olabilir.

Remediation sadece "bir görevi tamamladım" diye kapanmaz; yeni evidence gerekir.

---

# 10. Deterministik state çözümleme önceliği

Topic state aynı girdiler için tekrar hesaplandığında aynı sonucu vermelidir.

Önerilen temel çözüm sırası:

## 10.1 Topic hiç başlanmadıysa

1. Hard entry prerequisite eksik → `locked`
2. Hard entry prerequisite uygun → `available`

## 10.2 Topic daha önce başlandıysa

1. Doğrulanmış aktif remediation ihtiyacı → `remediation_required`
2. Daha önce mastered + doğrulanmış retention riski → `weakening`
3. Required coverage/waiver + required mastery gate tamam → `mastered`
4. Diğer durumlar → `learning`

Bu sıra önemli bir invariant üretir:

> **Başlanmış bir Topic prerequisite regression nedeniyle geriye dönük `locked` olmaz.**

Prerequisite regression Skill-level planner/gating davranışını etkiler.

---

# 11. State transition tablosu

| From | Event / condition | To |
|---|---|---|
| `locked` | hard entry prerequisite'ler uygun | `available` |
| `available` | ilk gerçek çalışma başlar | `learning` |
| `available` | güvenilir diagnostic tüm gerekli gate'leri doğrular | `mastered` |
| `learning` | coverage + mastery gate sağlanır | `mastered` |
| `learning` | hedefli müdahale gerektiren eksiklik doğrulanır | `remediation_required` |
| `mastered` | retention riski doğrulanır | `weakening` |
| `mastered` | ciddi/tekrarlanan kritik eksiklik doğrulanır | `remediation_required` |
| `mastered` | yeni required curriculum hedefi eklenir ve henüz karşılanmaz | `learning` veya duruma göre `remediation_required` |
| `weakening` | retention yeniden doğrulanır | `mastered` |
| `weakening` | gerçek eksiklik doğrulanır | `remediation_required` |
| `remediation_required` | akut eksik düzelir fakat mastery gate tamam değildir | `learning` |
| `remediation_required` | gerekli mastery yeniden doğrulanır | `mastered` |

State transition hiçbir zaman sadece takvimde gün geçtiği için yapılmaz.

---

# 12. Coverage ayrı tutulacak

Topic state ile coverage tek alan değildir.

Önerilen ayrı kavramlar:

- `coverage_started`
- `required_objectives_covered`
- `coverage_complete`
- `diagnostic_coverage_waiver`

Kesin DB şeması 8C'de belirlenecektir.

Örnek:

- Coverage %100 / mastery düşük → `learning` veya `remediation_required`
- Coverage düşük / diagnostic mastery güçlü → uygun validated skip ile `mastered` mümkün
- Coverage %20 → normalde `learning`

Bu ayrım "dersi bitirdi = öğrendi" hatasını yapısal olarak engeller.

---

# 13. Planner için state anlamı

| State | Planner'ın ana yorumu |
|---|---|
| `locked` | Topic'i yeni çalışma olarak seçme; prerequisite'i düzelt |
| `available` | Yeni Topic adayı |
| `learning` | Devam eden öğrenme; teach/practice/assessment üret |
| `mastered` | Normal öğretim yok; gerektiğinde reinforce/retention |
| `weakening` | Kısa review/retrieval önceliği ver |
| `remediation_required` | Hedefli remediation'a yüksek öncelik ver; bağımlı Skill path'ini gerektiğinde beklet |

Kesin priority score Aşama 3'te tasarlanacaktır.

---

# 14. State reason/history zorunluluğu

State değişiklikleri açıklanabilir olmalıdır.

Her transition ileride en az şu tür bilgileri taşıyabilmelidir:

- önceki state,
- yeni state,
- timestamp,
- reason code,
- ilgili Skill/Objective,
- transition'ı tetikleyen evidence/assessment referansı,
- gerekiyorsa curriculum version.

Örnek reason code'lar:

- `hard_prerequisites_satisfied`
- `topic_started`
- `diagnostic_mastery_confirmed`
- `mastery_gate_satisfied`
- `retention_risk_confirmed`
- `retention_recovered`
- `remediation_triggered`
- `remediation_recovered`
- `curriculum_requirement_changed`

Kesin event/data schema 8C'de belirlenir.

---

# 15. Örnek — Basic Pointers

Başlangıç:

`understand_memory_address` hard prerequisite henüz yeterli değil.

`Basic Pointers = locked`

Memory address Skill yeterli hale gelir:

`locked → available`

İlk pointer dersi başlar:

`available → learning`

Kullanıcı address/value iyi, declaration iyi; dereference debugging'de tekrar eden sorun gösterir:

`learning → remediation_required`

Hedefli debugging + micro-drill sonrası akut sorun azalır fakat gerekli tüm evidence henüz yoktur:

`remediation_required → learning`

Coding + debugging + explanation/diğer gerekli evidence gate'leri tamamlanır:

`learning → mastered`

İki ay sonra retention sistemi dereference kullanımında gerçek risk doğrular:

`mastered → weakening`

Farklı varyasyon ve gerçek coding kullanımında beceri yeniden doğrulanır:

`weakening → mastered`

Eğer farklı kontrollerde tekrar başarısız olursa:

`weakening → remediation_required`

Bu sırada Linux gibi bağımsız branch çalışmaya devam edebilir.

---

# 16. Değişmez kurallar / invariants

1. Topic completion tek başına `mastered` üretmez.
2. `locked` Topic yeni öğrenme görevi olarak planlanamaz.
3. Başlanmış/mastered Topic, prerequisite sonradan zayıfladı diye geriye dönük `locked` yapılmaz.
4. Topic state Skill mastery'nin yerine geçmez; prerequisite kararının canonical kaynağı Skill'dir.
5. Tek bir küçük yanlış tüm Topic'i otomatik `remediation_required` veya sıfır mastery yapmaz; evidence kuralları 2C–2E tarafından belirlenir.
6. `remediation_required` geçmiş coverage'ı sıfırlamaz.
7. `weakening` bütün Topic'in unutulduğu anlamına gelmez; hedefli Skill retention riskidir.
8. Bağımsız branch'ler tek bir Topic/Skill problemi nedeniyle durdurulmaz.
9. State sadece zaman geçti diye değişmez; zaman yalnız retention modelinin evidence/risk hesabını tetikleyebilir.
10. Aynı canonical girdiler aynı Topic state sonucunu üretmelidir.
11. State transition açıklanabilir reason/history kaydı bırakmalıdır.
12. Kullanıcı henüz öğretilmemiş prerequisite yüzünden başarısız sayılmaz.

---

# 17. 2B'de bilinçli olarak kilitlenmeyenler

Aşağıdakiler sonraki adımlara aittir:

- hangi evidence'ın ne kadar güçlü olduğu → **2C**
- hint/AI yardımının evidence'e etkisi → **2D**
- mastery/remediation threshold değerleri → **2E**
- retention risk formülü ve spaced repetition interval'leri → **2F**
- planner priority puanları ve günlük kapasite → **Aşama 3**
- assessment composition → **Aşama 4**
- kesin DB/state event schema → **8C**

Bu değerler 2B state machine içine keyfi biçimde gömülmemiştir.

---

# 18. 2B kabul kriterleri

2B tamamlanmıştır çünkü:

- altı canonical Topic state tek anlamlı tanımlandı,
- coverage ile mastery ayrıldı,
- Topic state'in derived olduğu kesinleştirildi,
- hard prerequisite'in `locked` davranışı tanımlandı,
- başlamış Topic'in prerequisite regression nedeniyle geriye dönük locked olmaması kararlaştırıldı,
- learning/mastered/weakening/remediation geçişleri tanımlandı,
- retention ve remediation sonrası dönüş yolları tanımlandı,
- planner'ın her state'i nasıl yorumlayacağı belirlendi,
- state history/reason gereksinimi kondu,
- `LEARNING_BEHAVIOR_RULES.md` ile çelişmeyen invariant'lar yazıldı,
- henüz 2C–2F'ye ait sayısal eşikler uydurulmadı.

> **Tamamlanma notu — 2026-08-24:** Topic state machine `locked → available → learning → mastered` ana ilerleme yolu ve `mastered ↔ weakening`, `learning/weakening/mastered → remediation_required → learning/mastered` onarım yollarıyla tanımlandı. Topic state Skill mastery yerine geçen bağımsız bir puan değil, prerequisite/coverage/mastery/retention/remediation girdilerinden türetilen açıklanabilir planner/UX durumudur.

---

# 19. Sıradaki adım

## 2C — Mastery sinyalleri

Bir sonraki adımda teori, coding, debugging, açıklama/Feynman, transfer, retention, proje ve süre gibi evidence türlerinin:

- neyi kanıtladığı,
- güvenilirlik düzeyi,
- hangi Learning Objective/Skill'lere bağlandığı,
- tek başına neyi kanıtlayamayacağı,
- yanlış pozitif mastery'yi nasıl engelleyeceği

kesinleştirilecektir.
