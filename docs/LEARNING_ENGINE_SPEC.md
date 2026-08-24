# Learning Engine Specification — AI Infra Learning Coach

**Aşama:** 2 — Öğrenme ve Mastery Modelini Tasarla  
**Tamamlanan adım:** 2A — Bilgi birimleri  
**Durum:** 2A TAMAMLANDI / AŞAMA 2 TAMAMLANDI  
**Tarih:** 2026-08-24  
**Granularity clarification:** 2026-08-25 — D-044

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
- Technical English ile Python/C/Linux gibi teknik alanlar aynı modelde nasıl birlikte yaşar?
- Geniş bir domain içindeki gerçek zayıflık hangi seviyede tutulur?

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

D-044 clarification:

> **Weakness ve remediation da mümkün olduğunca Skill / Learning Objective seviyesinde lokalize edilir. `Python zayıf`, `Linux zayıf` gibi Domain-level sonuçlar yalnız derived summary olabilir.**

---

# 3. Domain

## Tanım

Uzun vadeli büyük yetkinlik alanıdır.

Örnekler:

- Technical English
- Python
- C Programming
- Linux / Git / Shell
- Data Structures & Algorithms
- Modern C++
- Computer Architecture
- OS & Memory
- Concurrency
- Networking
- Distributed Systems
- Storage / Databases
- Cloud / Observability
- Performance Engineering
- GPU Architecture
- CUDA
- Triton
- LLM Inference
- AI Infrastructure

## Sorumluluğu

- curriculum'un en üst seviye organizasyonu,
- progress/analytics'te büyük alan görünümü,
- uzun vadeli kariyer yönünü anlaşılır biçimde bölme.

## Domain ne değildir?

- tek başına öğrenme kanıtı değildir,
- kullanıcı bir domain kartını bitirdi diye mastered sayılmaz,
- runtime prerequisite'in ana birimi değildir,
- tek başına weakness diagnosis atomu değildir.

## Domain mastery

Doğrudan bağımsız bir puan olarak üretilmez. Altındaki canonical skill'lerin kurallı özetinden **derived** olarak hesaplanır.

---

# 4. Module

## Tanım

Bir domain içindeki anlamlı öğrenme kümesidir.

Örnek:

- Domain: `Python`
- Module: `Programming Foundations`
- Module: `Control Flow`
- Module: `Functions & Modularity`

veya:

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

Örnek C:

`Basic Pointers`

Bu topic altında şu skill'ler ele alınabilir:
- address ile value farkını ayırt etme,
- pointer declaration kullanma,
- dereference uygulama,
- pointer üzerinden değeri değiştirme.

Örnek Python:

`Loops`

Bu topic altında ayrı canonical skill'ler bulunabilir:
- `for` ile iterable üzerinde doğru iteration,
- `while` termination condition kurma,
- `break` / `continue` davranışını uygulama,
- common infinite-loop bug'larını teşhis etme.

## Topic'in rolü

- lesson/anlatım bağlamı,
- örnekler,
- practice task'leri,
- assessment item havuzu,
- remediation materyali,
- kullanıcıya gösterilen anlaşılır çalışma başlığı.

## Topic ne değildir?

Topic bir "mastery atomu" değildir.

`Loops dersi tamamlandı` demek bütün loop skill'lerinin öğrenildiği anlamına gelmez.

## Topic progress vs mastery

Topic için iki kavram ayrılmalıdır:
- **coverage/progress:** içerik/aktivite açısından ne kadarı görüldü,
- **mastery status:** bağlı skill/objective kanıtlarına göre öğrenme durumu.

Coverage mastery yerine geçmez.

---

# 6. Skill

## Tanım

Kullanıcının tekrar kullanılabilir, ölçülebilir gerçek yeteneğidir.

Skill bir ders başlığı değil, **yapabilme/bilme kapasitesidir**.

İyi örnekler:

- `Python'da while döngüsü için doğru termination condition kurabilmek`
- `C'de pointer ile bir değişkenin değerini güvenli biçimde okuyup değiştirebilmek`
- `Linux'ta relative ve absolute path kullanarak filesystem içinde gezinebilmek`
- `Bir compiler error mesajında dosya/satır ve temel hata nedenini ayırt edebilmek`

Zayıf skill isimleri:

- `Python`
- `Loops`
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

Kavramsal olarak en az:
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

> Verilen bir Python `while` döngüsünde termination condition'ın hangi state değişimine bağlı olduğunu belirler ve infinite-loop riskini doğru açıklar.

### Kötü örnek

> Pointer'ları anlar.

Neden kötü: "anlar" doğrudan gözlemlenebilir ve ölçülebilir değildir.

## Objective kuralları

Her objective:

1. tek bir ana beceriye bağlı olmalı,
2. ölçülebilir bir fiil içermeli,
3. en az bir assessment/evidence türüyle doğrulanabilmeli,
4. gereksiz şekilde farklı yetenekleri tek cümlede birleştirmemeli,
5. sadece içerik tüketimini tarif etmemeli,
6. mümkünse transfer/uygulama bağlamına izin vermelidir.

## Objective mastery

Evidence en küçük seviyede Learning Objective'e bağlanabilir. Objective durumu, Skill mastery hesabının temel girdilerinden biridir.

---

# 8. Mastery ve weakness nerede tutulacak?

## 8.1 Canonical mastery seviyeleri

1. **Learning Objective evidence/state** — en atomik ölçüm katmanı.
2. **Skill Mastery State** — ana planner/prerequisite/remediation karar katmanı.

## 8.2 Derived seviyeler

Aşağıdakiler ayrı bağımsız gerçeklik değil, skill verilerinden türetilen görünümlerdir:
- Topic mastery / weakness summary
- Module mastery / weakness summary
- Domain mastery / weakness summary

## 8.3 D-044 weakness localization kuralı

Bir Domain'de bir alt skill zayıfsa bütün Domain otomatik zayıf/remediation_required sayılmaz.

Örnek:

```text
Python overall: learning
  Variables: strong
  Conditionals: mastered
  Loops: remediation_required
    for_iteration: weak
    while_termination: weak
    break_continue: stable
  Functions: learning
```

Planner/remediation şu tür karar vermelidir:

> "Python'ı baştan tekrar et" değil,
>
> "while termination + loop debugging için hedefli remediation üret."

---

# 9. Prerequisite modeli

## 9.1 Ana runtime prerequisite

Ana prerequisite edge:

`Skill → Skill`

Örnek:

`understand_memory_address` → `dereference_pointer` → `dynamic_memory_basic`

veya:

`evaluate_boolean_condition` → `while_termination_condition` → `debug_infinite_loop`

## 9.2 Learning Objective prerequisite

Çok ince taneli özel durumlarda objective düzeyinde dependency gerekebilir; ancak varsayılan ana mekanizma Skill-level edge'dir.

## 9.3 Topic prerequisite

Topic-level prerequisite authoring kolaylığı için bulunabilir fakat **tek başına gerçek mastery kilidi sayılmaz**.

## 9.4 Module/Domain prerequisite

Runtime hard lock olarak kullanılmaz. Çok kaba olduğu için bağımsız dalları gereksiz kilitleyebilir.

## 9.5 Hard ve soft prerequisite

Canonical detay PRG-v0 / D-036'dadır.

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

> **Technical English zayıflığı, gerçek teknik prerequisite olmadığı sürece teknik ilerlemeyi gereksiz yere hard-lock etmemelidir.**

English parallel track ayrı skill'ler olarak ilerler; teknik görevlerle ilişkilendirilebilir fakat varsayılan global kapı değildir.

---

# 11. Task, assessment ve evidence bağlantısı

Bir `LearningTask` veya `AssessmentItem`, sadece Topic'e değil hedeflediği Learning Objective/Skill'lere bağlanmalıdır.

Örnek:

`Debug this pointer code`:
- Topic: Basic Pointers
- Skill: dereference_pointer
- Objective: invalid dereference hatasını bulup nedenini açıklama
- Evidence type: debugging

Bir görev birden fazla objective'i test edebilir; fakat hangi evidence'ın hangi hedefe katkı verdiği açık olmalıdır.

Detaylı evidence semantics D-025/D-031/D-040 ve ilgili assessment spec'lerindedir.

---

# 12. Örnek teknik model

## Domain
`C Programming`

## Module
`Memory Foundations`

## Topic
`Basic Pointers`

### Skill C-MEM-PTR-01
`Address ile value farkını ayırt edebilmek`

Objectives:
- verilen kodda value ile address'i doğru işaretleyebilir,
- `&` operatörünün ürettiği şeyin adres olduğunu açıklayabilir.

### Skill C-MEM-PTR-02
`Bir pointer'ı declare, assign ve dereference edebilmek`

Objectives:
- uygun pointer declaration yazabilir,
- pointer'a geçerli address atayabilir,
- `*ptr` ile değeri okuyabilir,
- pointer üzerinden değeri değiştirebilir.

### Skill C-MEM-PTR-03
`Basit invalid pointer kullanımını teşhis edebilmek`

Objectives:
- invalid dereference riskini tespit edebilir,
- nedenini açıklayabilir,
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

### Skill ENG-COMP-01
`Basit compiler error mesajında temel yapıyı ayırt edebilmek`

Objectives:
- filename ve line number bilgisini bulabilir,
- `error`, `warning`, `expected`, `undeclared` gibi temel kelimeleri bağlam içinde ayırt edebilir,
- hata mesajının ana anlamını Türkçe veya basit İngilizce ile açıklayabilir.

Bu skill C görevlerinde `reinforce` olarak tekrar kullanılabilir; duplicate yaratılmaz.

---

# 14. Kimlik ve içerik sürümleme ilkesi

Her canonical birimin insan tarafından okunabilir isminden bağımsız stabil bir ID'si olmalıdır.

Örnek:
- `domain.python`
- `module.python.control_flow`
- `topic.python.loops`
- `skill.python.while_termination`
- `objective.python.while_termination.detect_infinite_loop`

ve:
- `domain.c`
- `module.c.memory_foundations`
- `topic.c.basic_pointers`
- `skill.c.pointer_dereference`

İsim/metin daha sonra değişse bile ID mümkün olduğunca değişmez.

Sebep:
- mastery history bozulmaması,
- curriculum update'lerinde eski kullanıcı verisinin bağını korumak,
- migration/analytics'in güvenilir kalması.

Kesin veri şeması **AŞAMA 9C**'de tasarlanacaktır.

---

# 15. Authoring kalite kuralları

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
11. **Granularity, hedefli weakness diagnosis/remediation sağlayacak kadar ince; anlamsız keyword-level atom patlaması yaratmayacak kadar anlamlı olmalı.**
12. Geniş Domain/Topic adları canonical Skill yerine kullanılmamalı.

---

# 16. 2A'da bilinçli olarak kararlaştırılmayanlar / sonradan bağlanan aşamalar

2A yapısal modeli tanımlar. Ayrıntılar:

- Topic state machine → **2B**
- mastery evidence taxonomy → **2C**
- Hint/AI assistance → **2D**
- mastery formula → **2E / GRE-v0**
- retention → **2F / RVR-v0**
- planner runtime → **AŞAMA 3**
- assessment policies → **AŞAMA 4**
- graph/schema backbone → **AŞAMA 5**
- full-route granular capability decomposition → **AŞAMA 6 / D-044**
- database schema → **AŞAMA 9C**
- first production curriculum content → **AŞAMA 15**
- full professional curriculum production → **AŞAMA 20**

D-044, 2A'yı yeniden açmaz; 2A'nın hiyerarşisini gerçek profesyonel rotanın tamamına uygulayacak ayrı authoring/planning aşaması ekler.

---

# 17. 2A kabul kriterleri

2A aşağıdaki koşullar sağlandığı için tamamlanmıştır:

- `Domain → Module → Topic → Skill → Learning Objective` katmanlarının tek anlamı tanımlandı.
- Curriculum organizasyonu ile gerçek öğrenme/ölçüm katmanı ayrıldı.
- Topic ile Skill farkı netleştirildi.
- Skill'in canonical ve topic'lerden bağımsız tekrar kullanılabilir olması kararlaştırıldı.
- Topic ↔ Skill many-to-many ilişki ihtiyacı tanımlandı.
- Mastery'nin canonical olarak Objective evidence + Skill state üzerinde tutulması kararlaştırıldı.
- Topic/Module/Domain mastery'nin derived olması kararlaştırıldı.
- Runtime prerequisite ana birimi Skill → Skill olarak belirlendi.
- Cross-domain skill/prerequisite desteklendi.
- Technical English'in aynı modele oturması ancak global teknik hard-lock olmaması korundu.
- Learning Objective için ölçülebilir yazım standardı oluşturuldu.
- Task/assessment'in objective/skill'e bağlanması zorunlu hale getirildi.
- Duplicate skill ve topic-completion kaynaklı sahte mastery önleyici authoring kuralları yazıldı.

> **Tamamlanma notu — 2026-08-24:** 2A ile öğrenme motorunun yapısal omurgası kilitlendi.

> **D-044 clarification — 2026-08-25:** Bu omurga, kullanıcı zayıflığını gerçek alt beceride görebilecek granularity'de authoring yapılmasını gerektirir. Ayrıntılı full-route decomposition AŞAMA 6'da yapılacaktır.

---

# 18. Güncel proje ilişkisi

AŞAMA 2 tamamlanmıştır; current active adım AŞAMA 4 içindeki **4B — Haftalık sınav**dır.

Bu dosyanın yapısal modeli AŞAMA 5 graph schema, AŞAMA 6 granular capability map, AŞAMA 12 runtime mastery/planner implementation ve AŞAMA 15/20 content production tarafından tüketilecektir.
