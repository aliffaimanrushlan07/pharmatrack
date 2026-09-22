# Report checklist — the 12 sections

The brief fixes the **order and the section names**. Do not reorder or rename
them; the documentation rubric is marked section by section against this list.

Deadline: **8 October 2026, 5:00 pm** — PDF, via Google Classroom.
File name format: `PROJECT GROUP <NAME1>_<NAME2>_<NAME3>`

---

| § | Section | Marks | Owner | Status |
|---|---------|-------|-------|--------|
| 1 | Project Title | ⎫ 10 | Group | 🟡 working title |
| 2 | Project Introduction | ⎭ | Group | 🔴 |
| 3 | Problem Statement | 10 | **Aliff** | 🟡 draft |
| 4 | Project Objectives | 10 | **Aliff** | 🟡 draft |
| 5 | System Scope | 10 | **Yasierul** | 🟡 draft |
| 6.1 | Use Case Diagram | ⎫ | **Amir** | 🔴 |
| 6.2 | Activity Diagram | ⎪ | **Amir** | 🔴 |
| 6.3 | Sequence Diagram | ⎬ **20** | **Aliff** | 🔴 |
| 6.4 | Class Diagram | ⎪ | **Ramzi** | 🔴 |
| 6.5 | ERD | ⎭ | **Ramzi** | 🔴 |
| 7 | Interface Design | 10 | **Amir** | 🔴 needs screenshots |
| 8 | Contribution Matrix | — | **Yasierul** compiles | 🟡 draft |
| 9 | Lessons Learned | 10 | **each member** | 🔴 |
| 10 | GitHub Repository Link | — | **Yasierul** | 🔴 |
| 11 | User Manual | 10 | **Yasierul** | 🔴 |
| 12 | References (APA) | — | **Yasierul** | 🟡 4 starters |
| — | Code Listing | 10 | everyone | ✅ ongoing |

---

## Section notes

### 1–2. Title & Introduction (10 marks, group)
Working title: *Pharmacy Inventory and Sales Management System*. Consider
locking in "PharmaTrack" as the product name — it is already used throughout the
code, the UI and the database, so the report should match.

Introduction: half a page to a page. Pharmacy business context, purpose of the
system, what the report covers. The Excellent band wants it "clear, concise and
highly relevant to the project objectives" — so write it **last**, once the
objectives are final.

### 3. Problem Statement (10, Aliff)
Each pain point tied to a consequence. Weak: *"manual tracking is inefficient"*.
Strong: *"manual tracking gives no visibility of reorder thresholds, so
shortages are only discovered when a customer asks for a medicine that is out of
stock"*.

### 4. Project Objectives (10, Aliff)
Five objectives, each starting with an infinitive, each visibly answering one of
the pain points in §3. The band checks alignment between the two sections.

### 5. System Scope (10, Yasierul)
Target users and system features. Update the draft to describe **what was
actually built**, not what was planned in week one.

### 6. Diagrams (20, split) ⭐

> Suppliers and the low-stock report are **out of scope** — do not diagram them.

The largest single item. Starting points for all five, already matching the
code: [`diagrams/`](diagrams/). Draw from the **code**, not from the draft report
— several column names changed during implementation.

### 7. Interface Design (10, Amir)
Minimum five pages required; we will have eight. Real data in every screenshot,
not `test test test`. Caption each one with what it does and which requirement
it satisfies.

### 8. Contribution Matrix (Yasierul compiles)
Must match the git history — the lecturer can see both. Each member verifies
their own row before submission.

### 9. Lessons Learned (10, individual)
150–250 words **each**, in your own voice. The band rewards "critical insights",
which means the thing that went wrong and what you understood afterwards — not
"I learned a lot about teamwork". Each member's brief in [`tasks/`](tasks/) ends
with suggested topics drawn from what you actually did.

### 10. GitHub Link (Yasierul)
Paste the URL. Confirm the lecturer can open it and that all four members have
visible commits.

### 11. User Manual (10, Yasierul)
The band explicitly asks for **visuals**. A text-only manual caps at Good.
Skeleton in [`USER-MANUAL.md`](USER-MANUAL.md).

### 12. References, APA (Yasierul)
Every source anyone actually used. *Ethical Practice* in the individual rubric
names proper citation directly.

### Code Listing (10, everyone)
Marked on comments and formatting. The existing files are the standard — keep it
up in your own code and this is 10 marks for no extra work.

---

## Two weeks before the deadline

- [ ] Every section has a named owner who has started it
- [ ] All five diagrams drawn and exported
- [ ] All screenshots taken from a working system with real data
- [ ] Four individual reflections written

## Submission week

- [ ] Table of contents updated (Word: Ctrl+A then F9)
- [ ] Figure numbers sequential, every figure referenced in the text
- [ ] Contribution matrix matches the git history
- [ ] GitHub URL correct and the repo is accessible
- [ ] Demo credentials removed from `login.jsp`
- [ ] Exported as **PDF**
- [ ] File named `PROJECT GROUP <NAMES>`
- [ ] Uploaded to Google Classroom before **5:00 pm, 8 October 2026**
- [ ] Demo rehearsed end to end, on the machine you will present from
