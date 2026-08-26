---
type: agent-bootstrap
status: active
authority: navigation-only
canonical_execution_state: "[[vault/wiki/sources/Execution State Source]]"
---

# Session Start — Persistent External Memory Bootstrap

Bu not, yeni bir sohbetin önceki sohbetlerin bağlamını güvenilir biçimde yeniden kurması için giriş kapısıdır. Modelin kendi konuşma hafızasının yerine geçmez; repo içindeki durable kaynaklara yönlendirir.

## Non-negotiable boundary

- Bu not **navigation-only**'dir; ürün davranışı, kalıcı karar veya execution state için source of truth değildir.
- Numaralı proje adımlarında bağlayıcı kural yalnızca [[docs/PROJECT_MEMORY_PROTOCOL|Project Memory Protocol]]'dür.
- Çelişkide şu sıra geçerlidir: current execution için [[vault/wiki/sources/Execution State Source|living-memory seti]], davranış için ilgili canonical spec, kalıcı karar için [[docs/DECISIONS|DECISIONS.md]].

## Her yeni sohbetin zorunlu bootstrap'ı

1. [[AGENTS|AGENTS.md]]'yi oku ve oradaki tüm takeover kurallarına uy.
2. [[docs/LOCAL_MANAGER_HANDOFF|Local Manager Handoff]], [[docs/START_HERE|Start Here]] ve [[docs/PROJECT_MEMORY_PROTOCOL|Project Memory Protocol]]'ü oku.
3. Current state'i birlikte doğrula: [[docs/EXECUTION_INDEX|Execution Index]], [[docs/STEP_STATUS|Step Status]], [[docs/HANDOFF_STATE|Handoff State]], [[PROJECT_CONTEXT|Project Context]] ve [[docs/MASTER_PLAN|Master Plan]].
4. Karar soruluyorsa [[docs/DECISIONS|Decisions]]'i; davranış soruluyorsa ilgili canonical spec'i aç.
5. İlgili konuya göre [[vault/wiki/mocs/AI Infra Learning Coach Map|ürün haritası]] veya [[vault/wiki/mocs/Repository Document Map|repo belge haritası]] üzerinden derinleş.

`AGENTS.md` ilk takeover için tüm Markdown corpus'unun okunmasını zaten zorunlu tutar. Bu sayfa o kapsamı azaltmaz; yalnızca doğru okuma sırasını kalıcılaştırır.

## Sohbet türüne göre davranış

| Kullanıcı isteği | Minimum işlem |
| --- | --- |
| Soru / özet | İlgili canonical source'u oku, kaynaklı cevap ver; state değiştirme. |
| İnceleme / diagnosis | Kanıt topla, nedenini açıkla; kullanıcı istemedikçe düzeltme yapma. |
| Numaralı adımı başlatma | Önce PROJECT_MEMORY_PROTOCOL PRE-STEP uygula; kullanıcı onayı yoksa adımı yürütme. |
| Uygulama / değişiklik | Scope'u belirle, ilgili spec'i oku, değiştir, doğrula; numaralı adım ise tam POST-STEP uygula. |
| Araştırma / kaynak ingest | `vault/inbox/` → `vault/raw/` → source note → concept/wiki bağlantıları akışını kullan. |

## Kısa durum kartı

[[vault/agent/CURRENT_CONTEXT|Current Context]] hızlı yön bulmak içindir. Oradaki her volatile bilgi başlamadan önce living-memory setiyle yeniden doğrulanır.

## Sohbet sonunda hafızaya yazma

Sohbet içinde önemli bilgi bırakma:

- Yeni kalıcı karar → [[docs/DECISIONS|DECISIONS.md]] (yalnız gerçekten kalıcıysa).
- Numaralı adım ilerlemesi → PROJECT_MEMORY_PROTOCOL'un tam POST-STEP seti.
- Kaynak/bulgu → `vault/wiki/sources/` ve ilgili `vault/wiki/concepts/` notu.
- Açık soru, sonraki aksiyon veya belirsizlik → [[vault/agent/OPEN_LOOPS|Open Loops]].
- Oturumun devredilecek özeti → `vault/agent/session-logs/` altında [[vault/templates/Session Handoff|Session Handoff]] ile tarihli kayıt.

Yeni kaydı yazmadan önce aynı role sahip mevcut notu kontrol et; duplicate source of truth oluşturma.

## Kullanıcının yeni sohbete yapıştıracağı kısa prompt

[[vault/agent/NEW_CHAT_PROMPT|New Chat Prompt]] kullanılabilir.

## Health check

Yeni veya devralınan sohbetin ilk büyük işinden önce [[vault/agent/checklists/MEMORY_HEALTH_CHECKLIST|Memory Health Checklist]] uygula.

Hızlı mekanik kontrol: `python tools/validate_external_memory.py`. Bu yalnız dosya/link bütünlüğünü doğrular; semantic current-state cross-check'i yine zorunludur.
