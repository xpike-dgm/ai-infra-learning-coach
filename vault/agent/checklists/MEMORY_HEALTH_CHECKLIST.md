---
type: checklist
cadence: "new session; before large work; after material changes"
---

# Memory Health Checklist

## Bootstrap integrity

- [ ] Root `AGENTS.md` güncel session bootstrap'a işaret ediyor.
- [ ] [[vault/agent/SESSION_START|Session Start]] ve [[vault/agent/CURRENT_CONTEXT|Current Context]] mevcut.
- [ ] CURRENT_CONTEXT, living-memory kaynaklarıyla çelişmiyor.
- [ ] Canonical / historical / navigation-only ayrımı açık.

## Current-state integrity

- [ ] EXECUTION_INDEX, STEP_STATUS, HANDOFF_STATE, PROJECT_CONTEXT ve MASTER_PLAN aynı aktif adımı gösteriyor.
- [ ] DECISIONS'taki kalıcı kararlar özetlerde yanlış temsil edilmiyor.
- [ ] Açık işler [[vault/agent/OPEN_LOOPS|Open Loops]] içinde görünür.

## Knowledge-graph integrity

- [ ] Yeni source note en az bir raw/origin linki ve bir concept linki taşıyor.
- [ ] Yeni concept note en az bir source backlink'i taşıyor.
- [ ] Kırık wikilink, yetim aktif not veya duplicate source of truth yok.
- [ ] Historical not canonical gibi etiketlenmemiş.

## Handoff integrity

- [ ] Oturumda alınan kalıcı karar doğru canonical dosyaya yazıldı.
- [ ] Numaralı iş varsa PROJECT_MEMORY_PROTOCOL POST-STEP eksiksiz uygulandı.
- [ ] Yeni sohbetin kullanıcıya tekrar soru sormadan devam edebilmesi için açık bağlam durable notlarda var.

## Automated check

`python tools/validate_external_memory.py` komutu required memory files ve vault wikilink hedeflerini doğrular. Bu kontrol semantic canonical-state cross-check'in yerine geçmez.
