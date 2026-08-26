# Query Index

Sorular `queries/` altında, cevaplar ve görsel çıktılar `outputs/` altında saklanır.

Çalışma biçimi: [[vault/wiki/workflows/Knowledge Base Workflow|Knowledge Base Workflow]].

```dataview
TABLE asked_at, status, output
FROM "queries"
SORT asked_at DESC
```
