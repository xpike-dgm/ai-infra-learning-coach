# 7C Research — Daily Technical English Cadence & Task Mix

**Adım:** 7C — Günlük English bileşeni  
**Rol:** Research input — ürün kararı değildir  
**Tarih:** 2026-08-27

## Araştırma sorusu

AI Infra Learning Coach'ta Technical English paralel hattı, kullanıcının ortak günlük çalışma kapasitesini sabit yüzde/dakika ile bölmeden nasıl düzenli çalıştırılmalı; retrieval, spacing, production, feedback ve retention görevleri hangi tasarım ilkeleriyle seçilmelidir?

## Kaynaklar

### R1 — Cepeda et al. (2006), distributed practice meta-analysis
- Source: *Psychological Bulletin*, “Distributed practice in verbal recall tasks: A review and quantitative synthesis”.
- URL: https://pubmed.ncbi.nlm.nih.gov/16719566/
- 184 makaledeki 317 deneyden 839 distributed-practice assessment'ı sentezler.
- Ana sonuç: spaced/distributed presentation genel olarak retention için avantaj sağlar; uygun inter-study interval retention interval ile birlikte değişir.

**7C çıkarımı:** English practice'i tek yoğun blokta toplamak yerine zaman içine dağıtılmış tekrar/retrieval fırsatları üretmek makuldür; fakat “her X günde tam Y dakika” gibi evrensel bir interval çıkarılamaz.

### R2 — Classroom distributed-practice meta-analysis (2025)
- Source: “The Distributed Practice Effect on Classroom Learning: A Meta-Analytic Review of Applied Research”.
- URL: https://pubmed.ncbi.nlm.nih.gov/40564553/
- 31 effect size / N > 3000; distributed practice lehine orta büyüklükte etki raporlar (d ≈ 0.54).

**7C çıkarımı:** planner, tekrarları tek güne yığmak yerine farklı günlere yaymayı desteklemeli; bunun exact interval'i RVR-v0/pilot tarafından belirlenmeli.

### R3 — Rogers, Nakata & Chiu (2025), L2 vocabulary spacing replication
- Source: *Studies in Second Language Acquisition*, “Optimizing distributed practice online”.
- URL: https://www.cambridge.org/core/journals/studies-in-second-language-acquisition/article/optimizing-distributed-practice-online/C833408A4C3BAD939CA39EA734423BB7
- Cepeda et al. paradigmasının L2 vocabulary bağlamındaki conceptual replication'ında spaced koşullar massed koşuldan daha iyi performans gösterdi.

**7C çıkarımı:** Technical English'te de tekrar/retrieval'in oturumlar arasında dağıtılması için destek vardır; yalnız vocabulary sonucu bütün grammar/production alanlarına doğrudan genellenemez.

### R4 — Retrieval-practice transfer meta-analysis
- Source: “Transfer of test-enhanced learning: Meta-analytic review and synthesis”.
- URL: https://pubmed.ncbi.nlm.nih.gov/29733621/
- 122 deney / 192 transfer effect size; testing/retrieval practice'in re-exposure kontrolüne karşı transfer avantajı raporlanır (ortalama d ≈ 0.40), fakat transfer moderator'lara bağlıdır.

**7C çıkarımı:** daily English yalnız passive rereading olmamalı; uygun Skill'lerde free recall, application, production veya fresh-context retrieval bulunmalı. Aynı kolay recognition formatını tekrar etmek transfer kanıtı değildir.

### R5 — Interim retrieval / grain-size evidence
- Source: “A grain of truth in the grain size effect: Retrieval practice is more effective when interspersed during learning”.
- URL: https://pubmed.ncbi.nlm.nih.gov/39556402/
- Beş deney ve mevcut çalışmaların meta-analizi, öğrenme boyunca araya yerleştirilen retrieval'ın yalnız sona bırakılan büyük testlere göre küçük-orta avantaj gösterebildiğini raporlar (g ≈ 0.25).

**7C çıkarımı:** yeni English öğrenimi olan bir oturumda, yalnız uzun açıklama → final quiz yerine küçük study/retrieval döngüleri tercih edilebilir. Bu bir fixed item-count kuralına dönüştürülmemelidir.

### R6 — L2 written feedback evidence
- Source: Scherer, Graham & Busse (2024), *Learning and Instruction*, “How effective is feedback for L1, L2, and FL learners’ writing? A meta-analysis”.
- URL: https://www.sciencedirect.com/science/article/pii/S0959475224000884
- 200 karşılaştırmada feedback'in surface/deep writing outcomes üzerinde koşula göre pozitif etkileri raporlanır; etki learner/context/feedback type'a göre değişir.

**7C çıkarımı:** production görevinde feedback kullanılabilir; fakat feedback türü target'a göre seçilmeli ve AI/feedback sonrası düzeltilmiş cevap bağımsız H0 evidence gibi sayılmamalıdır.

## Karşı-kanıt / belirsizlik

- Spacing literatüründe optimum interval tek sabit değildir; retention horizon ve materyal özellikleriyle etkileşir.
- L2 vocabulary çalışmalarının tamamı aynı yönde değildir. Örneğin 2023 *System* çalışması belirli koşullarda massed practice lehine sonuç raporlamıştır: https://www.sciencedirect.com/science/article/pii/S0346251X23000714
- Vocabulary findings doğrudan writing, documentation navigation veya professional interaction'a birebir taşınamaz.
- “Her gün kesin X dakika English” veya “günün %Y'si English” için bu kaynaklardan geçerli tek bir bilimsel sabit çıkarılamaz.

## Manager'a öneri

1. **Common capacity pool:** English ayrı günlük dakika kotası değil, mevcut planner'ın ortak hard budget'ında `technical_english` track olarak kalmalı.
2. **Daily opportunity, not daily obligation:** aktif çalışma gününde eligible/open English need varsa en az bir English TaskCandidate üretilmeli; seçilip seçilmeyeceği PBR-v0 priority + capacity ile belirlenmeli.
3. **No debt/failure:** English o gün seçilmez veya kullanıcı çalışmazsa failure/mastery loss/debt yazılmamalı.
4. **Balance pressure:** eligible olduğu halde tekrar tekrar capacity/priority nedeniyle seçilmeyen English need, mevcut PBR starvation/track-balance mekanizmasıyla öne taşınmalı; fixed “N gün” threshold uydurulmamalı.
5. **State-driven task mix:** new learning, remediation, verification, retention, practice ve production oranları sabit yüzde değil current Skill/Objective state'inden türemeli.
6. **Distributed retrieval:** retention/retrieval due sinyalleri RVR-v0 üzerinden zaman içine yayılmalı; 7C yeni spacing formula icat etmemeli.
7. **Interspersed check:** yeni öğretim varsa uygun yerlerde kısa retrieval/application checkpoint'leri tercih edilmeli; tek büyük final quiz zorunlu pattern olmamalı.
8. **Evidence diversity:** mümkün olduğunda aynı family/aynı prompt tekrarı yerine farklı item/context ve receptive→productive/transfer progression kullanılmalı.
9. **Feedback safety:** corrective feedback practice/remediation için kullanılabilir; assisted revision bağımsız mastery evidence değildir.
10. **No technical integration creep:** Technical task içindeki bilingual scaffolding ve cross-track integration 7D ownership'inde kalmalı.

## Güven seviyesi

- **Yüksek:** distributed practice ve retrieval practice'in genel öğrenme/retention yararı; fixed daily percentage gerekmemesi.
- **Orta-yüksek:** L2 vocabulary için spacing/retrieval; Technical English task selection'a yön verici olarak kullanılabilir.
- **Orta:** writing feedback'in exact günlük mix'e çevrilmesi; target ve learner profile'a duyarlıdır.
- **Düşük / kilitlenmemeli:** evrensel günlük dakika, task sayısı, gün-aralığı veya kategori yüzdesi.
