# 16D — Settings: research synthesis

**Step:** 16D — Ayarlar (`STGX-v0` / `D-126`; the new step 16F: `D-125`)  
**Question:** where do the learner's own settings live, how does today's time become today's plan, and how does a backup go out and come back without a target, a debt or a silent loss?

## 1. Internal contracts (binding)

- **`UXIA-v0`** (`ux/8a_information_architecture/ia.yaml`): Profile is "How should this personal app operate for me?". It owns `capacity_preferences`, `reminders`, `theme_accessibility_preferences` and `backup_export_restore`.
- **`THUX-v0`** (`ux/8b_today_home/home.yaml`) on capacity:
  - the hard budget is shown as time context;
  - `today_override_action: change_today_capacity` triggers a replan;
  - persistent preferences live in Profile;
  - a smaller day is never a failure;
  - a larger one promises no fixed extra tasks.
- **D-033 / `docs/ADAPTIVE_PLANNER_SPEC.md`:**
  - §2 resolves the day's minutes in this order: today's override, today's profile, the scheduled default, then the normal profile;
  - §3 defines the short, normal and intensive profiles with v0 presets of 30, 60 and 90 minutes, each editable;
  - §4 makes the budget a hard ceiling;
  - §16 makes `today_capacity_changed` a replan event.
- **`SRR-v0` (D-038):** absence is not debt, and yesterday's plan is never replayed.
- **`LFPS-v0` §11** (`docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`):
  - export is user-initiated and complete, preferences included;
  - restore is atomic, verified before it replaces anything, and never a silent merge;
  - replacing a profile is explicit.
- **`APHX-v0` (10E):** `Backup` is the mechanism (verified export; staged, verified, atomic restore). The Profile controls were left to 16D.
- **`DDM-v0`:** user truth is append-only. D-104 set the precedent for a declared extension table.

## 2. External source

- **Android Storage Access Framework.** https://developer.android.com/training/data-storage/shared/documents-files, read 2026-10-25.
  - `ACTION_CREATE_DOCUMENT` and `ACTION_OPEN_DOCUMENT` open the system picker. The user chooses the location or the document, and the app only suggests a name and a type.
  - No storage permission is needed.
  - The app gets access only to the document the user chose, as a content URI it reads and writes through the content resolver.
  - Creating a document never overwrites an existing name: the system adds a number instead.
  - Used for:
    - the backup goes only where the learner puts it, and comes back only from the file the learner picks;
    - the app needs no storage permission, and an export can never silently replace another file.

## 3. Synthesis

- **Preferences belong in the store.** The export is the database (10E), and `LFPS-v0` exports preferences. Platform settings would leave them behind on a restore, so they are an append-only table there.
- **Today's plan is asked of the planner, never composed by the app.** The app only says when: at open, and on a new study day. The planner resolves the capacity, keeps an existing plan, and replans for a changed budget with D-033's own event.
- **A restore keeps what it replaces.** The user's decision makes a replaced profile something kept, not something gone. The copy is verified like any backup, and without it nothing is replaced.
- **Splitting the step.** Every app call since 11 had been sent here. Settings plus today's plan is one coherent outcome, because Today shows a real plan. Running that plan end to end is a different one, so it becomes 16F.
