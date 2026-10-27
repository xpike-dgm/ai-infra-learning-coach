# 17D — Accessibility: research synthesis

**Step:** 17D — Accessibility (`A11X-v0` / `D-137`)  
**Question:** can someone who uses a screen reader, large text or a switch reach every screen and every control, and hear what each one is and what state it is in?

## 1. Internal contracts (binding)

- **`VDSX-v0` §8, §12:** every interactive target is at least 48 x 48 dp; state is said in text, never by colour alone; the layout holds at 200% text.
- **`UXIA-v0`:** every surface has one title; the four destinations are the shell.
- **`TRUX-v0` (8C) and 17C (`USBX-v0`):** the focused-flow exit is one action away and stays in view; the position is a fact said in words.
- **17A (`VPLX-v0`) and 17B (`MOTX-v0`):** titles and section headers are the type scale's headings; motion follows the system's reduced-motion setting.

## 2. External facts used

- **WCAG 2.2:** 4.1.2 Name, Role, Value (every control has a name and a role, and its state is exposed); 2.5.3 Label in Name (a control's spoken name contains its visible words, so a voice-control user can say what they see); 3.3.1 Error Identification (an error is identified and described in text); 4.1.3 Status Messages (a status is announced without moving focus); 1.4.4 Resize Text (200%); 2.4.6 Headings and Labels.
- **Android:** an accessibility service such as TalkBack reads the `AccessibilityNodeInfo` tree; `uiautomator dump` writes the same tree (text, content description, class, clickable, checkable, bounds), so it is a faithful source for names, classes, sizes, clickability and order — but not for headings, state descriptions, live regions, roles or errors, and not for what TalkBack actually says.
- **Compose:** `Modifier.semantics { heading() }`, `stateDescription`, `liveRegion = LiveRegionMode.Polite`, `selectableGroup()`, `Role.RadioButton`; `OutlinedTextField(supportingText)` puts an error inside its field rather than as loose text below it (`isError` would also paint it in the error colour); an `Icon` with `contentDescription = null` is decorative and is skipped.
- **The dump's bounds** are clipped to the window, not to a scrolling container, so a node cut by a scroll edge can look too small; the audit matches such a node by place to a reading where it is whole. The dump carries no heading, state description, live region, role or error, so those are checked in the code.

## 3. What the emulator showed

- At 100% and 200% text, every screen, read whole, passes R1–R8 after the fixes below.
- At 200% text the run's orientation choices and its start button are below the fold; they are reachable by scrolling (the first audit stopped there because it did not scroll).
- The three daily-length fields were all named "dk" and their three buttons "Kaydet": a screen reader could not tell them apart.
- Today's row to Progress was named "İlerleme", like the tab; R7 caught two controls with one spoken name.
- Today drew "Bu plan neden böyle?" twice (beside the task and in the supporting rows); R7 over the whole screen caught it.
- `OutlinedTextField(isError = true)` draws the field in Material's error colour, which `VDSX-v0` keeps for system faults (17A's check caught it); the error stays the field's supporting text.
- TalkBack is not installed on the emulator image (no Play services), and an APK from outside the Play store is an untrusted download, so spoken output was not heard.

## 4. Not decided here

A visible focus ring, a theme preference and an in-app reduced-motion preference (not chosen for 17D; raised at 17E's PRE); the day summary's real changes, two IA edges and AI usage/cost visibility (17E, `D-136`); TalkBack heard on the device (19).
