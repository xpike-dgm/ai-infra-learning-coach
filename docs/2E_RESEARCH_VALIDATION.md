# 2E Research Validation — Mastery Formula v0

> **D-050 / D-044 hygiene note (2026-08-25):** Bu tarihsel araştırma/provenance belgesindeki ileride yapılacak aşamalara ait eski numaralar D-044 öncesi planı yansıtabilir. Güncel karşılık için `docs/STAGE_REINDEX_MAP.md` ve `docs/EXECUTION_INDEX.md` kullanılır. Tarihsel araştırma metni sessizce yeniden yazılmamıştır.


**Adım:** 2E — Mastery formülü v0  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge, kullanıcı tarafından ayrı Research AI'a verilen bağımsız araştırma görevinin raporunu ana yönetici tarafından değerlendirme kaydıdır. Research AI raporu otomatik ürün kararı olarak kabul edilmemiş; 2A–2D bağlayıcı kararları ve seçili akademik kaynaklarla karşılaştırılmıştır.

## 1. Sonuç

İlk candidate `Beta-style weighted accumulator` final model olarak kabul edilmedi.

Final 2E yönü:

> **Mastery kararı, son dönemdeki geçerli ve bağımsız H0 direct evidence'ın bounded/recent skoru + Objective'e özgü hard gates + problem-family/testlet diversity + verification hysteresis üzerinden verilir. Assisted ve corroborating evidence öğrenme/teşhis için saklanır fakat bağımsız mastery puanını ikame etmez.**

Final model adı: **Gated Recent Evidence v0 (GRE-v0)**.

Bu isim özellikle seçildi; model gerçek PFA/R-PFA değildir çünkü V1 cold-start aşamasında popülasyon verisiyle fit edilmiş lojistik regresyon katsayıları yoktur.

## 2. Research AI raporundan kabul edilen ana noktalar

- Sınırsız tüm-geçmiş birikimi yeni kanıtın etkisini giderek küçültebilir; mastery state dinamik olduğu için bounded/recent evidence yaklaşımı daha güvenlidir.
- `H1/H2/H3 = 0.85/0.65/0.35` gibi exact assistance katsayıları için yeterli ampirik temel yoktur; sahte hassasiyet üretir.
- AI evaluator için sabit `0.80` güven katsayısı savunulamaz; evaluator güvenilirliği ayrı doğrulama/calibration problemi olmalıdır.
- Coding/production Objective'i gerçek kullanıcı tarafından üretilmiş bağımsız artifact gerektirir; recognition/code-reading bunu ikame edemez.
- Debugging Objective'i bağımsız diagnosis/fix evidence istemelidir.
- Aynı veya çok yakın problem familyaları bağımsız evidence gibi sayılmamalı; dependency/testlet grouping gerekir.
- Tek temiz negative evidence mastered Skill'i anında sıfırlamamalı; `verification_due` ve fresh recheck kullanılmalıdır.
- Difficulty cold-start'ta keyfi bir numeric multiplier olmamalı; item eligibility ve gate olarak kullanılmalıdır.
- `0.80` threshold ve bounded recent-window büyüklüğü calibration öncesi **engineering heuristic** olarak açıkça versionlanmalıdır.

## 3. Research AI raporunda düzeltilen / fazla güçlü bulunan iddialar

### 3.1 Kesirli Beta parametreleri

Research raporu kesirli pseudo-count kullanımını doğrudan geçersiz gibi çerçeveledi. Bu ifade fazla güçlüdür. Beta dağılımının shape parametreleri pozitif reel değerler olabilir ve fractional/power-likelihood benzeri yaklaşımlar matematiksel olarak mümkündür.

Bizim candidate modeldeki asıl problem:

- bu ağırlıkların veriyle kalibre edilmiş bir likelihood modelinden gelmemesi,
- `objective_score` değerinin gerçek posterior knowledge probability gibi yorumlanamaması,
- sınırsız history birikiminin yeni evidence etkisini sönümletmesi,
- assistance/provenance/evidence-role gibi farklı kavramların tek çarpanda karıştırılmasıdır.

Bu nedenle Beta-style çekirdek finalden çıkarılmıştır; gerekçe yalnız “fractional count yasaktır” değildir.

### 3.2 H1–H4 için evrensel sıfır iddiası

Koedinger & Aleven'ın assistance-dilemma literatürü yardımın öğrenmeye etkisinin bağlama göre değiştiğini ve ne zaman/ne kadar yardım verilmesi gerektiğinin tek sabit parametreyle çözülemediğini gösterir. Instructional Factors Analysis da farklı instructional intervention türlerinin ayrı kategoriler olarak modellenmesinin yararlı olabildiğini gösterir.

Bu yüzden final karar:

- H1–H4 **öğrenme açısından değersiz değildir**,
- fakat **bağımsız mastery skoruna positive H0 evidence ile aynı kanaldan girmez**,
- remediation, hint dependence, task selection ve fresh recheck ihtiyacını besler.

### 3.3 `M=5` ve `0.80`

Research AI'ın önerdiği `M=5` ve `0.80` değerleri literatürden çıkan evrensel optimumlar değildir.

Finalde ikisi de versioned cold-start heuristics olarak tutulur:

- `recent_window_max_groups_v0 = 5`
- `objective_mastery_threshold_v0 = 0.80`

17C pilot calibration'da false-positive/false-negative mastery ve future-transfer/retention sonuçlarıyla yeniden değerlendirilecektir.

### 3.4 Modeli “Rolling-PFA” diye adlandırmama

PFA ve Recent-PFA popülasyon verisinden fit edilen lojistik prediction modelleridir. V1'de böyle fit edilmiş katsayılarımız olmadığı için final heuristic motor “PFA” diye adlandırılmayacaktır.

R-PFA'dan alınan fikir yalnızca **recent performance'ın tüm geçmişe göre daha anlamlı olabileceği** yönündeki tasarım ilhamıdır.

## 4. Dış kaynak doğrulama özeti

- Koedinger & Aleven (2007), *Exploring the Assistance Dilemma in Experiments with Cognitive Tutors*, Educational Psychology Review, DOI `10.1007/s10648-007-9049-0`: assistance giving/withholding için tek evrensel optimum parametrenin henüz açık bir araştırma problemi olduğunu vurgular.
- Chi, Koedinger, Gordon, Jordan & VanLehn (2011), *Instructional Factors Analysis*: farklı instructional intervention türlerini ayrı kategoriler olarak modellemenin bazı prediction görevlerinde yararlı olduğunu gösterir.
- Pavlik, Cen & Koedinger (2009), *Performance Factors Analysis — A New Alternative to Knowledge Tracing*, DOI `10.3233/978-1-60750-028-5-531`: success ve failure history'yi ayrı modelleyen PFA yaklaşımını tanımlar.
- Galyardt & Goldin (2014/2015), *Recent-Performance Factors Analysis*: recent performance'ın recency-weighted temsilinin PFA/AFM üzerinde prediction iyileştirmesi gösterebildiğini raporlar; ancak bu çalışma geniş öğrenci datasında fit edilmiş prediction modelidir ve bizim cold-start heuristic'imizin doğrudan parametre kaynağı değildir.
- McCracken et al. (2001), DOI `10.1145/572133.572137` ve Lister/BRACElet çizgisi, programlama performansının gerçek writing/production görevleriyle ayrıca ölçülmesinin önemini destekler.
- LLM automated grading çalışmaları model, soru ve rubric'e göre agreement'ın ciddi değişebildiğini gösterir; bu nedenle sabit bir evaluator reliability coefficient finalden çıkarılmıştır.

## 5. 2E için bağlayıcı araştırma ayrımı

### Research-supported direction

- assisted performance ile independent performance'ı ayırmak,
- gerçek production Objective için production artifact istemek,
- same-family/dependent items'ı bağımsız saymamak,
- tek hatada instant mastery reset yapmamak,
- cold-start'ta kalibre edilmemiş difficulty multiplier kullanmamak,
- LLM evaluator'ı doğrulanmamış high-stakes hakem olarak görmemek.

### Engineering heuristic — calibration gerekli

- recent window maksimum `5` independent evidence group,
- operational score threshold `0.80`,
- standard Objective default minimum group/family sayıları,
- critical Objective default minimum group/family sayıları.

Bu değerler UI'da bilimsel kesinlik veya “% öğrenildi” olarak gösterilmez.

## 6. 2F sınırı

Research raporundaki half-life regression, spaced repetition interval ve time-based decay önerileri 2E'ye gömülmedi. Bunlar **2F — Unutma modeli** kapsamındadır ve ayrı PRE-STEP + Research AI turuyla ele alınacaktır.