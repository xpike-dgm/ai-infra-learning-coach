"""18G pilot report (PLTX-v0 / D-141): read a backup exported from the app and write the pilot's numbers.

Usage:
    python -X utf8 tools/pilot_report.py <backup.coachdb> [--out report.md]

The backup is opened read-only and nothing in it is changed. The report says only what the stored rows say:

- the window: the study days a plan was made on, and the days with an answer recorded;
- SC-049: of the planned days, how many had no structural correction reported (prerequisite impossible, time totally
  wrong, wrong branch); a personal preference is listed but never counted, as SC-049 says;
- SC-048: every rule violation the learner reported, with its note. No report is not proof that no rule was broken;
- planner observation (for 18B): the estimated minutes beside the interval between a planned task's first and last
  answer (it leaves out reading before the first answer, which is not stored, and may include time in the background;
  a one-answer task has no interval, and a task with no answer is not listed), and needs the planner kept passing over.

A trace the app's own codec would not decode — an unknown format or section, a missing field, a malformed escape, an id
outside its closed list — is named as unreadable, never guessed at (`PlanTraceCodec`).
"""
from __future__ import annotations

import argparse
import sqlite3
import sys
from collections import defaultdict
from pathlib import Path

KNOWN_TRACE_FORMATS = ("planner_trace/1", "planner_trace/2", "planner_trace/3")
STRUCTURAL = ("prerequisite_impossible", "time_wrong", "wrong_branch")
KIND_LABELS = {
    "prerequisite_impossible": "Ön koşul yüzünden imkânsız",
    "time_wrong": "Süre tamamen yanlış",
    "wrong_branch": "Yanlış dala götürüyor",
    "personal_preference": "Kişisel tercih",
    "rule_violation": "Kural ihlali",
}
SC049_TARGET = 0.90  # V1_SUCCESS_CRITERIA SC-049: a starting target, to be recalibrated with pilot data.
MIN_WINDOW_DAYS = 14  # SC-049: at least 14 days of real use.


# `PlanTraceCodec` (core-model): the fields each section requires, per format. A field only a later format has is
# refused in an earlier one, as the codec refuses it.
_COMMON = {
    "plan": {"generation_kind", "study_day", "curriculum_version", "truth_watermark", "skills_not_on_route"},
    "policy": {"key", "value"},
    "capacity": {"source", "hard", "planning", "reserve_relaxed", "below_minimum_block"},
    "plan_reasons": {"codes"},
    "invariant": {"name", "holds"},
    "selected": {"position", "candidate_id", "need_key", "purpose", "activity_kind", "title", "primary_skill", "track",
                 "estimated", "planned", "split"},
    "need": {"need_key", "trigger", "target_skills", "source_state_refs", "band", "scope", "criticality", "severity",
             "urgency", "starvation", "continuation", "decision_value", "track_balance", "duration_fit", "tie_break",
             "priority_reasons", "selected_candidate", "disposition", "final_reasons"},
    "candidate": {"candidate_id", "need_key", "validation", "eligibility", "cost", "disposition", "reasons"},
}
_V2_ONLY = {
    "replan": {"trigger", "previous_plan", "previous_study_day", "preserved", "invalidated", "preserved_minutes",
               "remainder_hard"},
    "reentry": {"previous_plan", "last_planned_study_day", "returned_study_day", "absence_study_days",
                "stale_planned_tasks", "paused_checkpoint_needs", "high_stakes_pauses_not_resumed",
                "open_needs_by_trigger", "due_skills_by_retention", "p0_p1_needs", "resolved_capacity"},
}
# The closed id lists of the values this report reads (core-model `PlannerFacts.kt`; the validator compares them).
TASK_PURPOSES = ("teach", "practice", "assess", "remediate", "retain", "diagnose", "reinforce")
NEED_TRIGGERS = ("new_learning", "continue_learning", "weakness_detected", "remediation_required", "retention_review_due",
                 "verification_due", "diagnostic_opportunity", "reinforcement_opportunity", "parallel_track_due",
                 "integration_opportunity", "transfer_opportunity")
NEED_DISPOSITIONS = ("selected", "partially_served", "eligible_not_selected", "blocked", "resolved_before_selection",
                     "no_valid_candidate")
REPLAN_TRIGGERS = ("today_capacity_changed", "session_remaining_time_changed", "user_requested_extra_time",
                   "user_stopped_session", "task_finished_early", "task_overran_estimate", "task_completed",
                   "new_evidence_recorded", "retention_state_changed", "new_remediation_created",
                   "new_verification_due_created", "prerequisite_state_changed", "prerequisite_mastery_changed",
                   "prerequisite_retention_state_changed", "prerequisite_verification_due_created",
                   "prerequisite_verification_resolved", "prerequisite_remediation_opened",
                   "prerequisite_remediation_resolved", "diagnostic_waiver_granted", "task_prerequisite_metadata_invalid",
                   "curriculum_prerequisite_edge_changed", "diagnostic_requested")
_UNRESERVED = set("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~@:/")


def _unescape(encoded: str) -> str | None:
    """The codec's own inverse: `%XX` escapes and unreserved characters (and a list's comma) only, or None."""
    out = bytearray()
    i = 0
    while i < len(encoded):
        ch = encoded[i]
        if ch == "%":
            pair = encoded[i + 1:i + 3]
            if len(pair) != 2 or any(c not in "0123456789abcdefABCDEF" for c in pair):
                return None
            out.append(int(pair, 16))
            i += 3
        elif ch in _UNRESERVED or ch == ",":
            out.append(ord(ch))
            i += 1
        else:
            return None
    try:
        return out.decode("utf-8")
    except UnicodeDecodeError:
        return None


def parse_trace(text: str | None) -> dict | None:
    """The trace as sections of decoded fields, or None — never a guess — when it is not what the codec would decode:
    an unknown format or section, a missing required field, a later format's field, a malformed escape, or an id this
    report reads that is not in its closed list."""
    if not text:
        return None
    lines = text.split("\n")
    if lines[0] not in KNOWN_TRACE_FORMATS:
        return None
    version = KNOWN_TRACE_FORMATS.index(lines[0]) + 1
    required = {k: set(v) for k, v in _COMMON.items()}
    if version >= 2:
        required["selected"].add("preserved")
        required.update({k: set(v) for k, v in _V2_ONLY.items()})
    if version >= 3:
        required["candidate"].add("related_skills")
    later_only = {"selected": {"preserved"} if version < 2 else set(), "candidate": {"related_skills"} if version < 3 else set()}
    sections: dict[str, list[dict[str, str]]] = defaultdict(list)
    for raw in lines[1:]:
        parts = raw.split("\t")
        name = parts[0]
        if name not in required:
            return None
        fields = {}
        for field in parts[1:]:
            at = field.find("=")
            if at <= 0:
                return None
            value = _unescape(field[at + 1:])
            if value is None:
                return None
            fields[field[:at]] = value
        if not required[name] <= fields.keys() or later_only.get(name, set()) & fields.keys():
            return None
        if name in ("replan", "reentry") and sections[name]:
            return None
        sections[name].append(fields)
    if len(sections["plan"]) == 0 or not sections["capacity"] or not sections["plan_reasons"]:
        return None
    for s in sections["selected"]:
        if s["purpose"] not in TASK_PURPOSES or not s["position"].isdigit() or not s["estimated"].isdigit():
            return None
    for n in sections["need"]:
        if n["trigger"] not in NEED_TRIGGERS or n["disposition"] not in NEED_DISPOSITIONS:
            return None
    if any(r["trigger"] not in REPLAN_TRIGGERS for r in sections["replan"]):
        return None
    return sections


def table_exists(db: sqlite3.Connection, name: str) -> bool:
    return db.execute("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?", (name,)).fetchone() is not None


def columns(db: sqlite3.Connection, table: str) -> set[str]:
    return {row[1] for row in db.execute(f"PRAGMA table_info({table})")}


def pct(part: int, whole: int) -> str:
    return f"%{100 * part / whole:.0f}" if whole else "—"


def report(path: Path) -> str:
    # The path is a percent-escaped file URI, so a "?", "#" or "%" in it is part of the name, not of the URI.
    db = sqlite3.connect(path.resolve().as_uri() + "?mode=ro", uri=True)
    out: list[str] = []
    w = out.append

    version = db.execute("SELECT schema_version FROM schema_metadata").fetchone()[0]
    w("# Pilot raporu")
    w("")
    w(f"Yedek: `{path.name}` · şema v{version}")
    w("")

    # ------------------------------------------------------------------ plans
    plans = db.execute(
        "SELECT p.id, p.occurred_on_study_day, t.trace FROM plan_version p "
        "LEFT JOIN planner_decision_trace t ON t.plan_version_id = p.id ORDER BY p.sequence"
    ).fetchall()
    plans_by_day: dict[str, list[tuple[int, dict | None]]] = defaultdict(list)
    unreadable = 0
    traces: dict[int, dict | None] = {}
    for plan_id, day, trace_text in plans:
        trace = parse_trace(trace_text)
        if trace is None:
            unreadable += 1
        traces[plan_id] = trace
        plans_by_day[day].append((plan_id, trace))
    plan_days = sorted(plans_by_day)

    attempt_cols = columns(db, "attempt")
    has_planned_link = "planned_task_id" in attempt_cols
    answered_days = sorted({row[0] for row in db.execute("SELECT DISTINCT occurred_on_study_day FROM attempt")})

    w("## Pencere")
    w("")
    if plan_days:
        w(f"- Plan yapılan çalışma günleri: **{len(plan_days)}** ({plan_days[0]} – {plan_days[-1]})")
    else:
        w("- Plan yapılan çalışma günü yok.")
    w(f"- Cevap kaydedilen günler: **{len(answered_days)}**")
    w(f"- Plan sürümleri: {len(plans)} (izi okunamayan: {unreadable})")
    if len(plan_days) < MIN_WINDOW_DAYS:
        w(f"- SC-049 en az {MIN_WINDOW_DAYS} günlük gerçek kullanım ister; pencere henüz {len(plan_days)} gün.")
    w("")

    # ------------------------------------------------------------------ journal
    if not table_exists(db, "pilot_journal_entry"):
        w("## Pilot günlüğü")
        w("")
        w("Bu yedekte pilot günlüğü yok (şema v14'ten önce alınmış). SC-048/049 bu yedekten okunamaz.")
        w("")
        entries = []
    else:
        entries = db.execute(
            "SELECT entry_kind, note, plan_version_id, recorded_on_study_day FROM pilot_journal_entry ORDER BY sequence"
        ).fetchall()

    if table_exists(db, "pilot_journal_entry"):
        # A report that names a plan is about that plan's day, even when it was saved after the study day rolled over.
        plan_day = {plan_id: day for plan_id, day, _ in plans}
        entries = [(kind, note, plan_id, plan_day.get(plan_id, day)) for kind, note, plan_id, day in entries]
        structural_days = sorted({day for kind, _, _, day in entries if kind in STRUCTURAL})
        preference = [e for e in entries if e[0] == "personal_preference"]
        violations = [e for e in entries if e[0] == "rule_violation"]
        counted_days = sorted(set(plan_days) | set(structural_days))
        clean = len(counted_days) - len(structural_days)

        w("## SC-049 — Yapısal düzeltme gerektirmeyen günler")
        w("")
        w(f"- Sayılan günler: {len(counted_days)} (plan yapılan günler ve yapısal bildirim yapılan günler)")
        w(f"- Yapısal düzeltme bildirilen günler: **{len(structural_days)}**")
        w(f"- Düzeltme gerektirmeyen günler: **{clean} / {len(counted_days)} ({pct(clean, len(counted_days))})**"
          f" — başlangıç hedefi %{SC049_TARGET * 100:.0f} veya fazlası (pilot verisiyle yeniden kalibre edilebilir)")
        for kind in STRUCTURAL:
            n = sum(1 for e in entries if e[0] == kind)
            w(f"  - {KIND_LABELS[kind]}: {n}")
        w(f"- Kişisel tercih bildirimleri (sayılmaz): {len(preference)}")
        w("")

        w("## SC-048 — Bildirilen kural ihlalleri")
        w("")
        if violations:
            w(f"**{len(violations)} bildirim.** Her biri incelenmeli (ön koşulun atlanması, ilerleme kaybı, kritik bir becerinin yanlışlıkla öğrenilmiş sayılması).")
            w("")
            for _, note, plan_id, day in violations:
                w(f"- {day} · plan {plan_id if plan_id is not None else '—'} · {note or '(not yok)'}")
        else:
            w("Öğrenci hiçbir kural ihlali bildirmedi. Bu, ihlal olmadığının kanıtı değildir; yalnız bildirilmediğini söyler.")
        w("")

        w("## Bütün bildirimler")
        w("")
        if entries:
            w("| Gün | Tür | Plan | Not |")
            w("|---|---|---|---|")
            for kind, note, plan_id, day in entries:
                safe = (note or "").replace("|", "\\|")
                w(f"| {day} | {KIND_LABELS.get(kind, kind)} | {plan_id if plan_id is not None else '—'} | {safe} |")
        else:
            w("Bildirim yok.")
        w("")

    # ------------------------------------------------------------------ planner observation
    w("## Planlayıcı gözlemi (18B girdisi)")
    w("")

    # Estimated against observed, per planned task with answers.
    rows = []
    if has_planned_link:
        tasks = db.execute(
            "SELECT pt.id, pt.plan_version_id, pt.position, pt.skill_logical_id, "
            "MIN(a.occurred_at_instant), MAX(a.occurred_at_instant), COUNT(a.id), MIN(a.occurred_on_study_day) "
            "FROM planned_task pt JOIN attempt a ON a.planned_task_id = pt.id GROUP BY pt.id ORDER BY pt.id"
        ).fetchall()
        for task_id, plan_id, position, skill, first, last, answers, day in tasks:
            trace = traces.get(plan_id)
            selected = None
            if trace is not None:
                selected = next((s for s in trace.get("selected", []) if s.get("position") == str(position)), None)
            estimated = int(selected["estimated"]) if selected and selected.get("estimated", "").isdigit() else None
            span = (last - first) / 60000 if answers > 1 else None
            rows.append((day, skill, selected.get("title") if selected else None, estimated, span, answers))

    w("### Tahmini süre ve gözlenen aralık")
    w("")
    if not has_planned_link:
        w("Bu yedekte cevaplar planlı göreve bağlı değil (şema v11'den önce).")
    elif not rows:
        w("Henüz planlı bir göreve verilmiş cevap yok.")
    else:
        w("Gözlenen aralık ilk ve son cevap arasıdır: ilk cevaptan önceki okuma süresini içermez (saklanmaz), uygulamanın arka "
          "planda kaldığı süreyi içerebilir; tek cevaplı görevde aralık yoktur (—). Cevabı olmayan görevler (yalnız anlatım) "
          "burada yer almaz.")
        w("")
        w("| Gün | Beceri | Görev | Tahmin (dk) | İlk–son cevap (dk) | Cevap |")
        w("|---|---|---|---|---|---|")
        for day, skill, title, estimated, span, answers in rows:
            w(f"| {day} | {skill} | {(title or '—').replace('|', '/')} | {estimated if estimated is not None else '—'} | {f'{span:.1f}' if span is not None else '—'} | {answers} |")
    w("")

    # Needs passed over: on each planned day, the day's last plan's needs that were eligible but not selected.
    passed: dict[str, list[str]] = defaultdict(list)
    triggers: dict[str, str] = {}
    replans: dict[str, int] = defaultdict(int)
    for day in plan_days:
        versions = plans_by_day[day]
        for _, trace in versions:
            if trace is not None:
                for r in trace.get("replan", []):
                    replans[r.get("trigger", "?")] += 1
        last = versions[-1][1]
        if last is None:
            continue
        selected_now = {n["need_key"] for n in last.get("need", []) if n.get("disposition") in ("selected", "partially_served")}
        for need in last.get("need", []):
            key = need.get("need_key", "?")
            triggers[key] = need.get("trigger", "?")
            if need.get("disposition") == "eligible_not_selected" and key not in selected_now:
                passed[key].append(day)

    w("### Seçilmeden geçilen ihtiyaçlar")
    w("")
    if not passed:
        w("Uygun olup seçilmeyen ihtiyaç yok.")
    else:
        def longest_run(days: list[str]) -> int:
            index = {d: i for i, d in enumerate(plan_days)}
            best = run = 0
            previous = None
            for d in days:
                run = run + 1 if previous is not None and index[d] == index[previous] + 1 else 1
                best = max(best, run)
                previous = d
            return best

        ranked = sorted(passed.items(), key=lambda kv: (-longest_run(kv[1]), -len(kv[1]), kv[0]))
        w("Bir ihtiyaç, günün son planında uygun olduğu hâlde seçilmediyse o gün 'geçildi' sayılır. Ardışık gün, plan yapılan günlerin sırasıdır.")
        w("")
        w("| İhtiyaç | Tetikleyici | Geçildiği gün | En uzun ardışık |")
        w("|---|---|---|---|")
        for key, days in ranked[:15]:
            w(f"| {key} | {triggers.get(key, '?')} | {len(days)} | {longest_run(days)} |")
    w("")

    w("### Yeniden planlar")
    w("")
    if replans:
        for trigger, n in sorted(replans.items(), key=lambda kv: (-kv[1], kv[0])):
            w(f"- {trigger}: {n}")
    else:
        w("Yeniden plan yok.")
    w("")
    db.close()
    return "\n".join(out)


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Pilot report from an exported backup (18G).")
    parser.add_argument("backup", type=Path)
    parser.add_argument("--out", type=Path)
    args = parser.parse_args(argv)
    if not args.backup.is_file():
        print(f"yedek bulunamadı: {args.backup}", file=sys.stderr)
        return 2
    text = report(args.backup)
    if args.out:
        args.out.write_text(text + "\n", encoding="utf-8")
    else:
        print(text)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
