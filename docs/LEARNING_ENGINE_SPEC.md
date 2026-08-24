# Learning Engine Specification — AI Infra Learning Coach

**Aşama:** 2 — Öğrenme ve Mastery Modelini Tasarla  
**Tamamlanan adım:** 2A — Bilgi birimleri  
**Durum:** 2A TAMAMLANDI / 2B AKTİF  
**Tarih:** 2026-08-24

Bu belge öğrenme motorunun bağlayıcı teknik/pedagojik spesifikasyonudur. Eski `docs/LEARNING_ENGINE.md` kavramsal taslak olarak kalabilir; uygulama kararlarında bu dosya daha güncel ve daha kesin kaynaktır.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

---

# 1. 2A'nın amacı

Öğrenme sisteminde kullanılan birimlerin birbirine karışmasını önlemek ve şu sorulara tek anlamlı cevap vermek:

- Curriculum hangi seviyelerde organize edilir?
- Gerçek beceri nerede temsil edilir?
- Mastery hangi seviyede ölçülür?
- Prerequisite hangi birimler arasında kurulur?
- Bir `Topic` ile `Skill` arasındaki fark nedir?
- Bir beceri birden fazla topic'te kullanılırsa mastery nasıl tekil tutulur?
- Learning Objective ne zaman gerçekten ölçülebilir kabul edilir?
- Technical English ile C/Linux gibi teknik alanlar aynı modelde nasıl birlikte yaşar?

---

# 2. Temel model

Resmî hiyerarşi:

`Domain → Module → Topic → Skill → Learning Objective`

Ancak bu yapı **tam anlamıyla katı bir ağaç değildir**.

İki katman vardır:

## 2.1 Curriculum organizasyon katmanı

`Domain → Module → Topic`

Bu katman içeriği okunabilir ve yönetilebilir paketlere ayırır.

## 2.2 Öğrenme/ölçüm katmanı

`Skill → Learning Objective`

Bu katman kullanıcının gerçekten ne bildiğini ve ne yapabildiğini temsil eder.

Kritik karar:

> **Mastery'nin gerçek kaynağı Topic tamamlanması değil, Skill ve onun Learning Objective'lerinden gelen kanıtlardır.**

---

# 3. Domain

## Tanım

Uzun vadeli büyük yetkinlik alanıdır.

Örnekler:

- Computer Fundamentals
- C Programming
- Linux
- Data Structures & Algorithms
- Modern C++
- OS & Memory
- Concurrency
- Networking
- Distributed Systems
- GPU Architecture
- CUDA
- Triton
- Technical English
- AI Infrastructure

## Sorumluluğu

- curriculum'un en üst seviye organizasyonu,
- progress/analytics'te büyük alan görünümü,
- uzun vadeli kariyer yönünü anlaşılır biçimde bölme.

## Domain ne değildir?

- tek başına öğrenme kanıtı değildir,
- kullanıcı bir domain kartını bitirdi diye mastered sayılmaz,
- runtime prerequisite'in ana birimi değildir.

## Domain mastery

Doğrudan bağımsız bir puan olarak üretilmez. Altındaki canonical skill'lerin ağırlıklı/kurallı özetinden **derived** olarak hesaplanır.

Kesin aggregation formülü 2E'de kararlaştırılacaktır.

---

# 4. Module

## Tanım

Bir domain içindeki anlamlı öğrenme kümesidir.

Örnek:

- Domain: `C Programming`
- Module: `C Foundations`
- Module: `Memory Foundations`
- Module: `Structured Data`

## Sorumluluğu

- çok büyük domain'i öğretilebilir paketlere ayırmak,
- curriculum authoring ve navigation'ı kolaylaştırmak,
- raporlama ve içerik sürümlemeyi düzenlemek.

## Module mastery

Bağımsız gerçek mastery kaydı değildir. İçerdiği/referans verdiği skill'lerden derived edilir.

## Prerequisite

Runtime'da Module → Module hard prerequisite ana mekanizma değildir. Gerekirse authoring sırasında üst seviye yönlendirme olarak tutulabilir; gerçek kilitler Skill seviyesine normalize edilmelidir.

---

# 5. Topic

## Tanım

Kullanıcının çalıştığı öğretim bağlamı/paketidir. Bir topic bir veya daha fazla canonical skill'i öğretir, uygulatır ve ölçer.

Örnek:

`Basic Pointers`

Bu topic altında şu skill'ler ele alınabilir:

- address ile value farkını ayırt etme,
- pointer declaration kullanma,
- dereference uygulama,
- pointer üzerinden değeri değiştirme.

## Topic'in rolü

- lesson/anlatım bağlamı,
- örnekler,
- practice task'leri,
- assessment item havuzu,
- remediation materyali,
- kullanıcıya gösterilen anlaşılır çalışma başlığı.

## Topic ne değildir?

Topic bir "mastery atomu" değildir.

`Basic Pointers dersi tamamlandı` demek, pointer skill'lerinin öğrenildiği anlamına gelmez.

## Topic progress vs mastery

Topic için iki kavram ayrılmalıdır:

- **coverage/progress:** içerik/aktivite açısından ne kadarı görüldü,
- **mastery status:** bağlı skill/objective kanıtlarına göre öğrenme durumu.

Coverage mastery yerine geçmez.

Topic durum state machine'i 2B'de kesinleştirilecektir.

---

# 6. Skill

## Tanım

Kullanıcının tekrar kullanılabilir, ölçülebilir gerçek yeteneğidir.

Skill bir ders başlığı değil, **yapabilme/bilme kapasitesidir**.

İyi örnekler:

- `C'de pointer ile bir değişkenin değerini güvenli biçimde okuyup değiştirebilmek`
- `Linux'ta relative ve absolute path kullanarak filesystem içinde gezinebilmek`
- `Bir compiler error mesajında dosya/satır ve temel hata nedenini ayırt edebilmek`

Zayıf skill isimleri:

- `Pointers`
- `Linux`
- `Chapter 3`

Bunlar fazla geniş veya içerik başlığıdır.

## Skill canonical'dır

Aynı beceri farklı topic/module içinde tekrar kullanılabilir. Bu durumda yeni kopya skill yaratılmaz.

Örnek:

`dereference_pointer` skill'i:

- Basic Pointers topic'inde öğretilir,
- Functions with Pointers topic'inde tekrar kullanılır,
- Linked Lists topic'inde yeniden test edilir.

Kullanıcının üç farklı mastery kaydı olmaz. Tek canonical skill mastery state'i vardır ve farklı topic'lerden gelen evidence aynı kayda katkı sağlar.

## Topic ↔ Skill ilişkisi

İlişki many-to-many olabilir.

Önerilen ilişki nesnesi:

`TopicSkillLink`

Alanlar ileride veri modelinde kesinleştirilecek; kavramsal olarak en az:

- `topic_id`
- `skill_id`
- `role`: `teach | practice | assess | reinforce`
- `importance`: topic içindeki önem derecesi

Bir skill için opsiyonel `primary_teaching_topic_id` bulunabilir; fakat skill'in kimliği topic'e bağlı değildir.

---

# 7. Learning Objective

## Tanım

Bir skill altında, assessment tarafından doğrudan kanıtlanabilecek **atomik ve gözlemlenebilir öğrenme çıktısıdır**.

Learning Objective şu soruya cevap verir:

> "Kullanıcı neyi yapabiliyorsa bu öğrenme hedefini başarmış sayacağız?"

## Yazım standardı

Objective mümkün olduğunca:

`Koşul / bağlam + gözlemlenebilir eylem + beklenen başarı`

şeklinde yazılır.

### İyi örnek

> Verilen basit bir C kodunda pointer'ın tuttuğu adres ile dereference edilen değeri ayırt eder ve kendi cümlesiyle doğru açıklar.

### İyi örnek

> Bir `int` değişkenini pointer üzerinden değiştiren kısa C kodunu, çözüm kopyalamadan yazabilir.

### Kötü örnek

> Pointer'ları anlar.

Neden kötü: "anlar" doğrudan gözlemlenebilir ve ölçülebilir değildir.

## Objective kuralları

Her objective:

1. tek bir ana beceriye bağlı olmalı,
2. ölçülebilir bir fiil içermeli,
3. en az bir assessment/evidence türüyle doğrulanabilmeli,
4. gereksiz şekilde iki-üç farklı yeteneği tek cümlede birleştirmemeli,
5. sadece içerik tüketimini tarif etmemeli (`videoyu izle`, `dokümanı oku` objective değildir),
6. mümkünse transfer/uygulama bağlamına izin vermelidir.

## Objective mastery

Evidence en küçük seviyede Learning Objective'e bağlanabilir. Objective durumu, Skill mastery hesabının temel girdilerinden biridir.

2E'de objective → skill aggregation ve threshold kuralları kesinleştirilecektir.

---

# 8. Mastery nerede tutulacak?

## 8.1 Canonical mastery seviyeleri

Gerçek mastery state'inin iki temel seviyesi vardır:

1. **Learning Objective evidence/state** — en atomik ölçüm katmanı.
2. **Skill Mastery State** — kullanıcının tekrar kullanılabilir beceri durumu; ana planner/prerequisite karar katmanı.

## 8.2 Derived mastery seviyeleri

Aşağıdakiler ayrı bağımsız gerçeklik değil, skill verilerinden türetilen görünümlerdir:

- Topic mastery
- Module mastery
- Domain mastery

Bu kararın amacı aynı öğrenme durumunun beş farklı yerde birbirinden kopuk puanlar üretmesini engellemektir.

## 8.3 Neden Skill ana seviyedir?

Çünkü planner şu tür bir karar vermelidir:

> "Kullanıcı `Basic Pointers` videosunu bitirdi mi?" değil,
>
> "Kullanıcı pointer dereference becerisini yeterli kanıtla gösterebiliyor mu?"

Prerequisite ve remediation kararları mümkün olduğunca canonical skill mastery üzerinden alınacaktır.

---

# 9. Prerequisite modeli

## 9.1 Ana runtime prerequisite

Ana prerequisite edge:

`Skill → Skill`

Örnek:

`understand_memory_address` → `dereference_pointer` → `dynamic_memory_basic`

Bu yapı gerçek öğrenme bağımlılığını temsil eder.

## 9.2 Learning Objective prerequisite

Çok ince taneli özel durumlarda objective düzeyinde dependency gerekebilir; ancak V1'de varsayılan değildir. Gereksiz graph karmaşıklığı yaratmamak için önce Skill-level edge kullanılacaktır.

## 9.3 Topic prerequisite

Topic-level prerequisite authoring kolaylığı için bulunabilir fakat **tek başına gerçek mastery kilidi sayılmaz**.

Örneğin "Dynamic Memory topic'i Basic Pointers topic'inden sonra" ifadesi authoring kuralı olabilir; runtime'daki gerçek kilit Dynamic Memory'nin entry skill'lerinin ihtiyaç duyduğu pointer skill mastery üzerinden çalışır.

## 9.4 Module/Domain prerequisite

Runtime hard lock olarak kullanılmaz. Çok kaba olduğu için bağımsız dalları gereksiz kilitleyebilir.

## 9.5 Hard ve soft prerequisite

Edge'in `hard` veya `soft` olması 3D ile birlikte detaylandırılacaktır. 2A seviyesinde karar:

- prerequisite'in canonical hedefi Skill'dir,
- bir alandaki eksik skill tüm domain'i otomatik kilitlemez.

---

# 10. Cross-domain skill ve prerequisite

Teknik alanlar birbirinden tamamen yalıtılmış değildir.

Örnek:

- C'de compiler error okuma,
- Linux terminal kullanım becerisi,
- Technical English error vocabulary

aynı günlük görevde etkileşebilir.

Bu nedenle cross-domain Skill → Skill edge desteklenir.

Ancak önemli ürün kuralı:

> **Technical English zayıflığı, gerçek teknik prerequisite olmadığı sürece C/Linux ilerlemesini gereksiz yere hard-lock etmemelidir.**

English parallel track ayrı skill'ler olarak ilerler; teknik görevlerle ilişkilendirilebilir fakat varsayılan olarak kariyer yolunu bloke eden global kapı değildir.

---

# 11. Task, assessment ve evidence bağlantısı

Bir `LearningTask` veya `AssessmentItem`, sadece Topic'e değil hedeflediği Learning Objective/Skill'lere bağlanmalıdır.

Örnek:

`Debug this pointer code` görevi:

- Topic: Basic Pointers
- Skill: dereference_pointer
- Objective: invalid dereference hatasını bulup nedenini açıklama
- Evidence type: debugging

Böylece task tamamlandı bilgisi ile öğrenme kanıtı birbirinden ayrılır.

## Çoklu hedefli görev

Bir görev birden fazla objective'i test edebilir; fakat hangi evidence'ın hangi hedefe katkı verdiği açık olmalıdır.

Detaylı scoring/weighting 2C–2E ve Aşama 4'te kesinleştirilecektir.

---

# 12. Örnek teknik model

## Domain
`C Programming`

## Module
`Memory Foundations`

## Topic
`Basic Pointers`

## Canonical Skills

### Skill C-MEM-PTR-01
`Address ile value farkını ayırt edebilmek`

Learning Objectives:

- verilen kodda bir değişkenin value'su ile address'ini doğru işaretleyebilir,
- `&` operatörünün ürettiği şeyin adres olduğunu kendi cümlesiyle açıklayabilir.

### Skill C-MEM-PTR-02
`Bir pointer'ı declare, assign ve dereference edebilmek`

Learning Objectives:

- uygun pointer declaration yazabilir,
- pointer'a geçerli address atayabilir,
- `*ptr` ile değeri okuyabilir,
- pointer üzerinden değişken değerini değiştirebilir.

### Skill C-MEM-PTR-03
`Basit invalid pointer kullanımını teşhis edebilmek`

Learning Objectives:

- verilen kısa örnekte geçersiz dereference riskini tespit edebilir,
- hatanın nedenini kısa biçimde açıklayabilir,
- uygun düzeltmeyi uygulayabilir.

Topic tamamlandı bilgisi bu üç skill'i otomatik mastered yapmaz.

---

# 13. Örnek Technical English modeli

## Domain
`Technical English`

## Module
`Compiler & Terminal English`

## Topic
`Reading Basic Compiler Errors`

## Skills

### Skill ENG-COMP-01
`Basit compiler error mesajında temel yapıyı ayırt edebilmek`

Learning Objectives:

- mesajdan filename ve line number bilgisini bulabilir,
- `error`, `warning`, `expected`, `undeclared` gibi temel kelimeleri bağlam içinde ayırt edebilir,
- çok basit bir hata mesajının ana anlamını Türkçe veya basit İngilizce ile açıklayabilir.

Bu skill daha sonra C görevlerinde `reinforce` olarak tekrar kullanılabilir; yeni bir kopyası oluşturulmaz.

---

# 14. Kimlik ve içerik sürümleme ilkesi

Her canonical birimin insan tarafından okunabilir isminden bağımsız stabil bir ID'si olmalıdır.

Örnek:

- `domain.c`
- `module.c.memory_foundations`
- `topic.c.basic_pointers`
- `skill.c.pointer_dereference`
- `objective.c.pointer_dereference.modify_int_via_pointer`

İsim/metin daha sonra değişse bile ID mümkün olduğunca değişmez.

Sebep:

- mastery history bozulmaması,
- curriculum content güncellendiğinde eski kullanıcı verisinin bağını koruması,
- migration ve analytics'in güvenilir kalması.

Kesin veri şeması Aşama 8C'de tasarlanacaktır.

---

# 15. Authoring kalite kuralları

Yeni curriculum içeriği eklenirken şu kontroller zorunludur:

1. Her Topic en az bir canonical Skill'e bağlanmalı.
2. Her Skill en az bir ölçülebilir Learning Objective içermeli.
3. Her Objective en az bir uygun evidence/assessment yolu ile doğrulanabilir olmalı.
4. `oku`, `izle`, `tamamla` tek başına Learning Objective olamaz.
5. Aynı yetenek farklı topic'te tekrar kullanılıyorsa duplicate Skill yaratılmamalı.
6. Topic completion mastery üretmemeli.
7. Prerequisite mümkün olduğunca gerçek Skill bağımlılığını ifade etmeli.
8. Domain/Module seviyesinde kaba hard-lock kullanılmamalı.
9. Cross-domain bağlantılar desteklenmeli.
10. English skill'leri teknik programı yalnız gerçekten gerekli olduğunda hard-lock edebilmeli.

---

# 16. 2A'da bilinçli olarak kararlaştırılmayanlar

Aşağıdakiler sonraki sabit adımlara aittir ve 2A'da uydurulmamıştır:

- Topic state'lerinin tam state machine'i → **2B**
- Hangi evidence türünün mastery'ye nasıl katkı verdiği → **2C**
- Hint/AI yardım seviyeleri ve etkisi → **2D**
- Mastery ağırlıkları, threshold ve confidence → **2E**
- Retention / decay / spaced repetition → **2F**
- Planner priority ve prerequisite runtime davranış detayları → **Aşama 3**
- Soru/scoring rubrics → **Aşama 4**
- Gerçek curriculum dataset → **Aşama 5 / 14**
- Database schema → **8C**

---

# 17. 2A kabul kriterleri

2A aşağıdaki koşullar sağlandığı için tamamlanmıştır:

- `Domain → Module → Topic → Skill → Learning Objective` katmanlarının her birinin tek anlamı tanımlandı.
- Curriculum organizasyonu ile gerçek öğrenme/ölçüm katmanı ayrıldı.
- Topic ile Skill arasındaki fark netleştirildi.
- Skill'in canonical ve topic'lerden bağımsız tekrar kullanılabilir olması kararlaştırıldı.
- Topic ↔ Skill many-to-many ilişki ihtiyacı tanımlandı.
- Mastery'nin canonical olarak Objective evidence + Skill state üzerinde tutulması kararlaştırıldı.
- Topic/Module/Domain mastery'nin derived olması kararlaştırıldı.
- Runtime prerequisite'in ana biriminin Skill → Skill olması kararlaştırıldı.
- Cross-domain skill/prerequisite desteklendi.
- Technical English'in aynı modele oturması ancak global teknik hard-lock olmaması ilkesi korundu.
- Learning Objective için ölçülebilir yazım standardı oluşturuldu.
- Task/assessment'in objective/skill'e bağlanması zorunlu hale getirildi.
- Duplicate skill ve topic-completion kaynaklı sahte mastery önleyici authoring kuralları yazıldı.

> **Tamamlanma notu — 2026-08-24:** 2A ile öğrenme motorunun yapısal omurgası kilitlendi. Domain/Module/Topic curriculum organizasyon katmanı; Skill/Learning Objective ise gerçek öğrenme ve ölçüm katmanı olarak ayrıldı. Mastery'nin ana karar seviyesi canonical Skill, en atomik evidence seviyesi Learning Objective olarak belirlendi. Topic/Module/Domain mastery değerleri derived olacak. Runtime prerequisite ana olarak Skill → Skill çalışacak. Bu karar 2B topic state machine ve 2C–2E mastery tasarımının temelidir.

---

# 18. Sıradaki adım

## 2B — Topic durumları

Bir sonraki adımda `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required` gibi durumların **kesin anlamı, geçiş koşulları ve mastery/retention ile ilişkisi** state machine olarak tasarlanacaktır.
