# Diagrams — report section 6

**Worth 20 marks.** The single largest item in the documentation rubric, and
the only one split across all four members.

| Diagram | § | Owner | Starting point |
|---------|---|-------|----------------|
| Use Case | 6.1 | **Amir** | [`use-case.md`](use-case.md) |
| Activity | 6.2 | **Amir** | [`activity-sale.md`](activity-sale.md) |
| Sequence | 6.3 | **Aliff** | [`sequence-login.md`](sequence-login.md) |
| Class | 6.4 | **Ramzi** | [`class-diagram.md`](class-diagram.md) |
| ERD | 6.5 | **Ramzi** | [`erd.md`](erd.md) |

---

## How to use these files

Each file contains the **content** of the diagram — the actors, classes,
relationships and flows, already matching the code as written. What it does not
contain is a finished picture.

Your job is to draw it properly and export a PNG for the report. Do not paste
the text from these files into the report; the marking band asks for diagrams,
and a code block is not a diagram.

---

## Tools

| Tool | Good for | Notes |
|------|----------|-------|
| **draw.io** (diagrams.net) | everything | Free, browser-based, no account. Has UML shape libraries built in. **Recommended.** |
| **MySQL Workbench** | the ERD only | Database → Reverse Engineer generates a real ERD from the live schema in ~30 seconds. By far the fastest route for 6.5, and it cannot drift from the code. |
| **Lucidchart** | everything | Free tier is limited to 3 documents |
| **StarUML** | strict UML | Desktop app, stricter notation |
| **NetBeans UML plugin** | class diagrams | Often broken on recent versions — not worth the time |

---

## What separates the bands

The Excellent band reads *"all diagrams are well-structured, detailed, and
accurately reflect the system architecture"*. In practice the marks go on:

1. **Correct notation.** Crow's feet on the ERD. Dashed arrows for returns on
   the sequence diagram. Hollow triangles for inheritance, dashed for interface
   realisation. Using the wrong arrow is the most common lost mark.
2. **Matching the code.** If the ERD says `quantity` and the table says
   `quantity_in_stock`, that is the "misses key relationships or details" band.
   **Draw from the code, not from the draft report** — the draft predates the
   implementation and several names changed.
3. **The failure paths.** A sequence diagram with only the happy path, or an
   activity diagram with no decision diamonds, looks like it was drawn from a
   description rather than from a working system.
4. **Consistency.** Same class names, same spelling, same case across all five
   diagrams and the code.

---

## Export checklist

- [ ] PNG at a readable resolution — if the reader has to zoom, it is too small
- [ ] Figure caption: *"Figure 6.1: Use Case Diagram for PharmaTrack"*
- [ ] Referenced in the body text at least once ("as shown in Figure 6.1…")
- [ ] Source file (`.drawio`) committed to this folder so it can be edited later
