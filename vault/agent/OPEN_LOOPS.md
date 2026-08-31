---
type: open-loops
status: active
last_reviewed: 2026-08-29
---

# Open Loops

Bu sayfa karar yerine geçmez; henüz çözülmemiş veya sonraki aşamaya bırakılmış işleri görünür tutar.

## Project delivery

- [x] 6D Systems detailed map: SDM-v0 / D-058 ile tamamlandı.
- [x] 6E GPU/ML/Inference detailed map: GIM-v0 / D-059 tamamlandı; `review.6d.accelerator_forward_reuse` resolved.
- [x] 6F Professional Engineering detailed map: PEM-v0 / D-060 tamamlandı; 6D + 6E professional-overlay reconciliation resolved.
- [x] 6G weakness/remediation operationalization: WLRM-v0 / D-061 tamamlandı; `review.6g.external_behavior_coverage` 6H'ye devredildi.
- [x] 6H independent external Research QA: S6ERQA-v0 / D-062 ile tamamlandı; 10/10 6H review resolved.
- [x] 7A English entry diagnostic: EED-v0 / D-063 ile tamamlandı.
- [x] 7B CEFR + technical progression alignment: TECP-v0 / D-064 ile tamamlandı; `review.6c.english.cefr_alignment` resolved.
- [x] 7C Daily English component: DECP-v0 / D-065 ile tamamlandı; common-capacity daily candidate + no quota/streak/debt + PBR balance/starvation + state-driven task mix.
- [x] 7D Technical integration: TEIP-v0 / D-066 ile tamamlandı
- [x] 7E English mastery: TEPM-v0 / D-067 ile tamamlandı; 8 derived profile state + qualified A1/A2/B1 Technical English profile + B2+ per-capability evidence.
- [x] 8A Bilgi mimarisi: UXIA-v0 / D-068 ile tamamlandı; Today/Learn/Progress/Profile semantic IA + shared detail/focused flows.
- [x] 8B Ana ekran: THUX-v0 / D-069 ile tamamlandı; action-first Today hierarchy + current PlannedTask queue + capacity/reason/empty/degraded semantics.
- [x] 8C Günlük çalışma akışı: TRUX-v0 / D-070 ile tamamlandı; execution-surface sınırı + emergent ungraded session + shared focused-flow frame + non-punitive assistance + asked-not-inferred provenance.
- [x] 8D Sınav UX: ASUX-v0 / D-071 ile tamamlandı; tek session interior + atomic boundary submission + non-punitive assistance + semantic result + first-class `not_reliably_measured`.
- [x] 8E Skill/progress/weakness UX: SPWX-v0 / D-072 ile tamamlandı; tek 8-state Skill vokabüleri + kilitli Topic etiketleri + inventory-only counting + non-punitive weakness sunumu.
- [x] 8F Tasarım sistemi: VDSX-v0 / D-073 ile tamamlandı; expression layer + 6 tone + eksiksiz state tone eşlemesi + WCAG çapaları + Türkçe casing koruması.
- [x] 8G Wireframe/prototip: WFPX-v0 / D-074 ile tamamlandı; 3 window class + 6 surface geometry + 52 ölçülen kontrast çifti + bağlayıcı olmayan prototip. **AŞAMA 8 kapandı.**
- [x] 9A Mobil teknoloji seçimi: AMTS-v0 / D-075 ile tamamlandı; Android native + Kotlin/Compose + dynamic colour kapalı + saf Kotlin core + Türkçe casing koruması.
- [x] 9B Veri saklama / local-first: LFPS-v0 / D-076 ile tamamlandı; evidence = truth / state = projection, append-only, SQLite, kalıcı exposure, atomik migration/restore.
- [x] 9C Domain veri modeli: DDM-v0 / D-077 ile tamamlandı; 11 curriculum + 12 truth + 8 projection entity, yapısal pinning, dört eksen, üç-değerli zaman, watermark'lı projeksiyon.
- [x] 9D Servis sınırları: MSBX-v0 / D-078 ile tamamlandı; 10 modül, içe-doğru dependency kuralı, 4 port, port olarak saat, ürünle sevk edilen null evaluator.
- [x] 9E AI entegrasyon mimarisi: AIAX-v0 / D-079 ile tamamlandı; AI port arkasında yardımcı, schema-constrained evaluator, `provisional` uncalibrated LLM, refusal != yanlış cevap, uçtan uca timeout bütçesi, konfigürasyondaki model adı, APK'da key yok, asgari-içerik gizlilik sınırı.
- [x] 9F Test stratejisi: TVSX-v0 / D-081 ile tamamlandı; her invariant'ın adı konmuş sahibi, 6 katman, invariant-coverage gate, negatif doğrulama, dolu fixture migration'ları, canlı provider çağrısız AI doğrulaması, adapter'sız build ile null-evaluator doğrulaması. **AŞAMA 9 kapandı.**
- [ ] 10A Proje kurulumu **AKTİF**: somut library/version seçimi, build ve modül yapılandırması, DI wiring, `TVSX-v0` katmanlarını çalıştıran CI job'ları, `D-080` gereği API key gitignore kuralı.
- [ ] `TVSX-v0` invariant register'ı ileri aşamalar invariant kabul ettikçe büyümek zorundadır; sahipsiz bir invariant release'i bloklar.
- [ ] `AIAX-v0` model varsayılanlarının güncelliği 10A/14'te yeniden doğrulanacak; somut model kimlikleri konfigürasyon değeridir, kanonik değildir.
- [ ] Prompt metni ve rubric ifadesi (14), AI Tutor konuşma UX'i (14), evaluator kalibrasyonu (18) ve somut SDK çağrı noktaları (10A/14) hâlâ açık.
- [ ] 10A'ya devredilen bounded verification list (AMTS-v0 §9): güncel Compose/Material 3 adaptive navigation API'leri, dynamic colour'ı kapatma mekanizması, `minSdk` politikasını karşılayan güncel API seviyesi (gerçek cihaza karşı), screen-reader semantics API'leri, reduced-motion tespiti ve seçilen `minSdk`de compatibility library gerekip gerekmediği. Araştırma AI konusudur; seçimi değiştiremez.
- [ ] Hedef Android cihaz repoda kayıtlı değil; `minSdk` doğrulaması için 10A'da gerekli.
- [x] Standing regression sweep kapsamı: elle tutulan liste yerine `tools/validate_*.py` glob'u; 9E POST'unda 6H'den beri FAIL veren beş package validator bulundu ve kapatıldı (dördü stale gate, biri gerçek 6E seed_mappings veri regresyonu).

## Knowledge-base operations

- [ ] Yeni external kaynakların ilk ingest işlemi.
- [ ] İlk `health-checks/` raporu: broken links, orphan notes, source coverage ve stale snapshot audit.
- [ ] İhtiyaç ortaya çıktığında vault üzerinde CLI/search aracı tasarlamak.

## Links

- Current execution: [[vault/wiki/sources/Execution State Source]]
- Delivery map: [[vault/wiki/projects/AI Infra Learning Coach Delivery]]
- Session method: [[vault/agent/SESSION_START]]
