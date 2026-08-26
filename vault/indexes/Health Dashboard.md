# Health Dashboard

Bu sayfa LLM health-check sonuçlarının giriş noktasıdır.

Kontrol kapsamı: [[vault/wiki/workflows/Knowledge Base Workflow|Knowledge Base Workflow]].

```dataview
TABLE check_type, severity, checked_at, status
FROM "health-checks"
SORT checked_at DESC
```

## Beklenen kontroller

- Yetim kaynak ve kavram notları
- Kırık wikilink ve eksik backlink
- Çelişen iddialar
- Kaynaksız iddialar
- Güncelliğini yitirmiş kaynaklar
