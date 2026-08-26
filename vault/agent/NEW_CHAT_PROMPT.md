# New Chat Prompt

Aşağıdaki metni yeni bir AI sohbetinin ilk mesajı olarak kullan:

```text
Bu repo kalıcı external memory kullanır. Çalışmaya başlamadan önce root AGENTS.md talimatlarını uygula. Ardından vault/agent/SESSION_START.md, vault/agent/CURRENT_CONTEXT.md ve ilgili canonical kaynakları oku. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı çapraz doğrula. Numaralı adımı ancak fresh PRE-STEP ve benim açık onayımla yürüt; bitirince PROJECT_MEMORY_PROTOCOL POST-STEP ve stale-reference audit uygula. Sohbette netleşen kalıcı kararları veya açık loop'ları uygun durable notlara yaz.
```

Bu prompt, repo içindeki kanonik kaynakların yerine geçmez; yeni sohbeti doğru bootstrap'a yönlendirir.
