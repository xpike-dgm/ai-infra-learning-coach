# Product Requirements — AI Infra Learning Coach

Bu belge ürün gereksinimlerinin kilitlendiği ana kayıttır. `docs/EXECUTION_INDEX.md` içindeki yürütme planıyla birlikte okunur.

## Durum

- **1A — Ana ürün amacı:** TAMAMLANDI
- **Güncel ürün hedefi:** professional-readiness odaklı 4+ yıllık esnek curriculum horizon'ı
- **Bağlayıcı ayrıntı:** `docs/PROFESSIONAL_READINESS_TARGET.md`
- **Granular curriculum charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

---

# 1A — Ana Ürün Amacı — TAMAMLANDI

## Tek cümlelik ürün amacı

**AI Infra Learning Coach; sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten, her gün ne öğrenmesi ve ne uygulaması gerektiğini mevcut bilgi durumuna göre belirleyen, yalnızca gerçekten öğrenildiği ölçümlerle kanıtlanan becerileri ilerleme kabul eden ve sınav, tekrar, hata analizi ve performans sonuçlarına göre gelecekteki çalışma planını otomatik yeniden düzenleyen kişisel adaptif mobil öğrenme koçudur.**

## Kullanıcının günlük aldığı temel değer

Kullanıcı uygulamayı açtığında uzun bir kurs listesi, aylara bölünmüş statik yol haritası veya “bugün ne çalışsam?” kararıyla karşılaşmamalıdır. Uygulama o gün için ayrılabilecek süreyi, daha önceki görev ve sınav performansını, unutulma riski taşıyan konuları, prerequisite durumlarını, teknik İngilizce gelişimini ve mevcut mastery seviyelerini dikkate alarak uygulanabilir günlük çalışma planı sunmalıdır.

> **“Uygulamayı açıyorum; bugün ne yapmam gerektiğini düşünüp planlamıyorum. Sistem bana doğru sıradaki işi veriyor, beni çalıştırıyor, gerçekten öğrenip öğrenmediğimi ölçüyor ve yarını buna göre değiştiriyor.”**

Ürün yalnızca plan yapan uygulama değildir. Öğrenme oturumunu yürütür, ölçer, eksikliği tespit eder, gerektiğinde konuyu farklı biçimde yeniden çalıştırır ve yeterlilik kanıtlanmadan bağımlı konulara geçiş vermez.

## Ürünün çözmek istediği ana problem

Uzun teknik kariyer yollarında sorun yalnızca “hangi konuları öğrenmeliyim?” değildir. Asıl sorunlar:

- bugün tam olarak ne çalışılacağının belirsiz olması,
- uzun roadmap'lerin uygulanabilir günlük görevlere dönüşmemesi,
- bir konuyu okumak/izlemek ile gerçekten öğrenmenin karıştırılması,
- temel eksikken ileri konuya geçilmesi,
- öğrenilen bilginin zaman içinde unutulması,
- sınav veya hataların sonraki çalışma planını değiştirmemesi,
- kişinin güçlü ve zayıf alanlarına rağmen herkes için aynı sabit programın uygulanması,
- geniş bir başlığın içinde **tam olarak hangi alt becerinin zayıf olduğunun görülememesi**,
- teknik eğitim ile İngilizcenin birbirinden kopuk yürütülmesi,
- AI araçlarıyla bir görevi tamamlamanın o görevi gerçekten anlamakla karıştırılması,
- birkaç gün ara verildiğinde programın bozulması veya görev borcu oluşması,
- yıllar süren ileri uzmanlaşma yolunda teorinin gerçek engineering yetkinliğine dönüşmemesi.

Ürün bu sorunları tek sistem içinde çözmeyi hedefler.

## Klasik kurs uygulamasından farkı

Klasik sistem:

`Ders 1 tamamlandı → Ders 2 açıldı → Ders 3 açıldı`

AI Infra Learning Coach:

`Çalış → ölç → mastery güncelle → retention kontrol et → prerequisite kontrol et → gerekiyorsa remediation uygula → uygun sıradaki görevi seç`

Bir videoyu izlemek, metni okumak, görev kartını işaretlemek veya uygulamada geçirilen süre tek başına ilerleme sağlamaz.

## Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Kanıtlanmış öğrenme hedefe göre şunların uygun kombinasyonuna dayanabilir:
- teori soruları,
- uygulamalı coding görevleri,
- debugging,
- kod/konu açıklayabilme,
- transfer soruları,
- gecikmeli retention testleri,
- gerçekçi project/capstone çıktıları,
- profiling/benchmarking ve production-context evidence.

Tek bir kolay quiz veya “öğrendim” demek kritik beceriyi mastered yapmaz.

## Adaptif davranış

Kullanıcı pointer konusunda zorlanıyorsa pointer'a bağlı yeni konular ertelenebilir; bağımsız Linux görevleri devam edebilir. Kullanıcı daha önce öğrendiği bilgiyi gecikmeli testte doğrulayamıyorsa ilgili Skill yeniden plana girebilir. Kullanıcı hızlı ilerliyorsa doğrulama sonrası gereksiz içeriği atlayabilmelidir.

Curriculum bir takvim değil, prerequisite ilişkileri bulunan bilgi haritasıdır. Takvim günlük capacity'yi; mastery + knowledge graph sıradaki doğru işi belirler.

## Granular weakness localization — D-044

Ürün `Python zayıf`, `Linux zayıf` gibi yalnız geniş Domain seviyesi sonuçlarla yetinmemelidir.

Canonical curriculum/diagnosis yapısı:

`Domain → Module → Topic → Skill → Learning Objective`

Gerçek weakness, mastery, prerequisite ve remediation mümkün olduğunca Skill/Objective seviyesinde tutulur.

Örnek:

```text
Python overall: learning
  Conditionals: mastered
  Loops: remediation_required
    for iteration: weak
    while termination: weak
    break/continue: stable
```

Böylece sistem tüm Python'ı yeniden öğretmek yerine yalnız eksik alt capability'ye hedefli reteach/practice/retest uygulayabilir.

Bu kapsamı tasarlayan ayrı planlama aşaması: **AŞAMA 6 — Granular Capability Map**.

## Uzun vadeli kariyer hedefiyle ilişki

Ürün genel amaçlı “her şeyi öğreten” eğitim uygulaması değildir. Uzun vadeli hedef **AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek teknik derinlik** oluşturmaktır.

Güncel ana yön:

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

Rota **4+ yıl veya daha uzun** sürebilir. Bu süre hedef değil, esnek planlama ufkudur:

```text
elapsed_time != readiness
professional_readiness = verified capability
```

Bir konunun ne zaman geçileceğini takvim değil yeterlilik belirler.

## Professional readiness çıkış hedefi

Kullanıcı final seviyede mümkün olduğunca bağımsız biçimde:
- Python/C/C++/Linux systems geliştirme,
- debugging ve profiling,
- concurrency/networking/distributed reasoning,
- GPU/CUDA/Triton geliştirme,
- LLM inference serving ve optimizasyon,
- latency/throughput/memory/cost benchmark analizi,
- multi-GPU / AI Infrastructure temel tasarım ve operasyon,
- teknik dokümantasyon, design decision ve troubleshooting iletişimi

gibi alanlarda gerçek artifact ve transfer evidence üretmelidir.

Bu hedef senior title, iş teklifi veya belirli maaş garantisi değildir. Gerçek ekip/production deneyimi ayrıca oluşur.

## İngilizcenin ürün içindeki rolü

Başlangıç İngilizce seviyesi A0 kabul edilebilir. İngilizce teknik eğitimin başlaması için önce tamamlanması gereken ayrı ön koşul değildir. Teknik eğitimle paralel ilerler; zaman içinde compiler/terminal mesajlarından docs, GitHub iletişimi, design docs/papers ve teknik mülakata kadar bağlanır.

## AI'nın ürün içindeki rolü

AI Tutor:
- açıklama yapar,
- farklı anlatım uygular,
- ipucu verir,
- yanlışın kök nedenini analiz eder,
- açık uçlu cevap ve kodu değerlendirir,
- AI ile üretilmiş kodun gerçekten anlaşılıp anlaşılmadığını kontrol eder.

AI kullanımı yasak değildir; ancak dış AI yardımıyla görev tamamlanmışsa mastery için Objective'e uygun independent comprehension/transfer/production doğrulaması gerekir.

## Kullanıcıya gösterilecek ilerleme anlayışı

4+ yıllık rota takvimsel başarı metriği değildir. UI'da `Gün 47 / 1460+` veya “kariyerin %12'si tamamlandı” ana başarı metriği değildir.

Kullanıcıya anlamlı olan:
- hangi becerileri mastered ettiği,
- hangi **alt beceride** zorlandığı,
- hangi bilgilerin yeniden doğrulama zamanı geldiği,
- bugün neden belirli görevleri yaptığı,
- hangi prerequisite'in bir sonraki işi tuttuğu,
- professional-readiness için hangi capability katmanlarının henüz kanıtlanmadığıdır.

## V1 release ile tam curriculum ayrımı

- **V1 release:** learning engine, planner, assessment, retention/remediation ve ilk production-quality curriculum paketi.
- **Tam professional curriculum:** aynı ürün motoru üzerinde yıllar boyunca kapsamı genişleyen systems → distributed → GPU/CUDA → inference → AI Infrastructure rotası.

D-044 sonrası curriculum üretim ayrımı:

```text
AŞAMA 5 = graph/schema backbone
AŞAMA 6 = full granular capability map
AŞAMA 15 = first 8–12 week production content
AŞAMA 20 = full professional content expansion + OSS + career + capstones
```

## Ürünün kişiliği

Uygulama kullanıcıyı suçlayan, streak kaybıyla baskılayan veya gereksiz gamification ile yöneten ürün olmayacaktır. Birkaç gün ara verilirse görev borcu yığmak yerine current state yeniden hesaplanacaktır.

Ürün deneyimi:
- sakin,
- profesyonel,
- net,
- modern,
- gereksiz kalabalıktan uzak,
- karar yükünü azaltan,
- öğrenme kalitesini geçirilen süreden daha önemli gören bir sistemdir.

## 1A kabul kontrolü

1A'nın ana ürün ilkeleri korunmaktadır. D-041/D-042/D-044 1A'yı iptal etmez; uzun vadeli çıkış hedefini, route kapsamını ve weakness granularity'sini netleştirir.
