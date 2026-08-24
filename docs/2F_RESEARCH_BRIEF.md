# 2F Research Brief — Forgetting / Retention / Spaced Repetition

**Adım:** 2F — Unutma modeli  
**Durum:** RESEARCH AI RAPORU BEKLENİYOR  
**Tarih:** 2026-08-24

Bu belge 2F için ayrı Research AI'a verilecek araştırma görevini kalıcılaştırır. Research sonucu otomatik ürün kararı değildir; ana yönetici raporu mevcut 2A–2E bağlayıcı kararlarla karşılaştırıp final `RETENTION_FORGETTING_SPEC.md` üretir.

## Bağlayıcı mevcut durum

- Canonical mastery seviyesi Skill; evidence Learning Objective'e bağlanabilir.
- Final mastery modeli `GRE-v0 — Gated Recent Evidence`.
- Mastery score'a yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence group girer.
- Assisted H1–H4 evidence formative/remediation/recheck sinyalidir.
- Tek clean post-mastery hata mastery'yi anında silmez; `verification_due` gerekir.
- Topic state `mastered → weakening → mastered/remediation_required` yolunu destekler.
- State yalnız takvimde gün geçti diye değişmez; retention riskinin açıklanabilir girdileri gerekir.
- Natural reuse uygun koşullarda retention/reinforcement evidence olabilir.
- Retention/remediation günlük kapasite içinde planlanır; backlog dump yoktur.
- D-028 gereği model local/deterministic, bounded/incremental ve performans dostu uygulanabilmelidir.

## Research kapsamı

Research AI aşağıdakileri karşılaştırmalı ve kaynaklı biçimde incelemelidir:

1. spacing effect, retrieval practice, lag effect, desirable difficulty ve forgetting curve literatürü,
2. SM-2 / SuperMemo ailesi,
3. FSRS ve modern spaced-repetition scheduler yaklaşımları,
4. Half-Life Regression (HLR),
5. ACT-R / memory activation temelli modeller,
6. DASH / öğrenci forgetting modelleri ve varsa knowledge-tracing forgetting extensions,
7. Leitner gibi basit heuristic scheduler'lar,
8. tek kullanıcı + cold-start + az veri senaryosunda uygulanabilirlik,
9. flashcard recall ile coding/debugging/transfer gibi karmaşık beceriler arasındaki fark,
10. mastery state ile retention state'in ayrı mı tutulması gerektiği,
11. ilk review zamanı ve interval growth/failure davranışının nasıl seçilmesi gerektiği,
12. başarılı delayed retrieval, partial result ve failed delayed retrieval etkisi,
13. tek delayed failure sonrası verification/hysteresis,
14. natural reuse'un planned review yerine ne zaman sayılabileceği,
15. same-family item'ın retention için tekrar kullanılmasının correlation/memorization riski,
16. critical prerequisite Skill'lerde daha muhafazakâr retention policy gerekip gerekmediği,
17. overdue/missed review davranışı ve missed-day backlog oluşturmama,
18. explicit time decay ile sadece due-review/risk state kullanmanın trade-off'u,
19. forgetting riskinin prerequisite-ready kararını ne zaman etkilemesi gerektiği,
20. false-positive retention ve false-negative forgetting riskleri,
21. hangi parametrelerin research-supported, hangilerinin engineering heuristic ve pilot-calibration gerektirdiği.

## Research AI çıktı standardı

Rapor en az şunları içermelidir:

- Executive Summary
- Research methodology / source hierarchy
- Model comparison table
- Learning-science findings
- Complex-skill vs flashcard distinction
- Recommended V1 retention architecture
- Initial review / interval policy options
- Success/failure transition rules
- Natural reuse policy
- Mastery + retention integration
- Prerequisite implications
- False-positive / false-negative risk table
- Candidate parameter table with `research-supported | engineering heuristic | needs calibration | insufficient evidence`
- Pilot calibration plan
- Sources with DOI/URL when available
- Confidence / uncertainty per major recommendation

## Kritik uyarılar

- Başka bir uygulamanın interval'ini evrensel doğru diye kopyalama.
- FSRS/SM-2/HLR/ACT-R gibi modelleri isim benzerliğiyle değil gerçek varsayımlarıyla karşılaştır.
- Flashcard recall sonuçlarını coding/debugging gibi production Skill'lerine doğrudan genelleme.
- Exact retention interval veya decay katsayısı için kanıt yoksa sayı uydurma.
- `time passed = knowledge lost` gibi deterministik varsayım yapma.
- Research raporu ürün yöneticisi kararı gibi yazılmamalı; kanıt ve belirsizliği ayırmalı.

## Kapanış

2F bu Research AI raporu gelmeden tamamlanmaz. Rapor sonrası ana yönetici ayrıca kritik iddiaları doğrular, final retention spec'i oluşturur ve D-024 POST-STEP GitHub senkronizasyonunu yapar.
