# Amir — CRUD, Interface & Point of Sale

> You own the biggest visible surface of the system. Two rubric criteria are
> yours outright — **CRUD Functionality (10)** and **Interface Design (10)** —
> and the examiner spends most of the demo looking at your screens.
>
> **Scope changed:** supplier management was removed from the system. In its
> place you now own the **point-of-sale screen**, which is the single best
> thing to lead the live demo with. Medicine CRUD is already finished and
> working — use it as your worked example.

---

## What you own

| File | Status | What to do |
|------|--------|------------|
| `controller/MedicineServlet.java` | ✅ Done | Read it — it is your template |
| `webapp/medicine/list.jsp` | ✅ Done | Your template for list pages |
| `webapp/medicine/form.jsp` | ✅ Done | Your template for form pages |
| `webapp/css/style.css` | ✅ Done | Tweak freely, it is yours |
| `webapp/includes/*.jspf` | ✅ Done | Shared header/footer |
| `controller/SaleServlet.java` | 🔴 **STUB** | TODO 1–5 · the point-of-sale screen |
| `webapp/sale/pos.jsp` | 🔴 **STUB** | The till screen |
| `webapp/sale/receipt.jsp` | 🔴 **STUB** | Printable receipt |
| `util/ValidationUtil.java` | 🟡 Partial | TODO 1–3 |
| `webapp/dashboard.jsp` | 🟡 Partial | Move it behind a servlet |

**Report sections you own:** 6.1 Use Case Diagram, 6.2 Activity Diagram,
7. Interface Design.

---

## Task 1 — Point of sale ⭐ START HERE (3–4 hours)

**Files:** `controller/SaleServlet.java`, `webapp/sale/pos.jsp`, `webapp/sale/receipt.jsp`

The most interactive screen in the system, and the one to open your demo with.

**Design decision, already made and worth explaining in the report:** the
basket lives in the **HTTP session** as a `Sale` object until the cashier
presses *Complete sale*. Nothing touches the database until the sale is final,
so an abandoned basket leaves no orphan rows and no wrongly-deducted stock.

- [ ] **TODO 1** — `?action=add` · get the `Sale` from the session (create if
      absent), add a `SaleItem`, put it back
- [ ] **TODO 2** — `?action=remove` and `?action=clear`
- [ ] **TODO 3** — running total via `SalesCalculator.calculateSubtotal()`
- [ ] **TODO 4** — `?action=complete` · calls `saleDAO.insertSale()`
- [ ] **TODO 5** — receipt page after a successful sale

Layout that works:

```
+-------------------------+-------------------------+
|  Search / pick medicine |  Basket                 |
|  name, price, stock     |  lines + subtotals      |
|  [ qty ]  [ Add ]       |  ---------------------  |
|                         |  TOTAL     RM 57.90     |
|                         |  [ Complete sale ]      |
+-------------------------+-------------------------+
```

**Validate stock twice** — when adding to the basket *and* at completion.
Checking only at add time leaves a gap where another cashier sells the last
packet while this basket is still open.

**You depend on Yasierul** for `SaleDAO.insertSale()` (his TODO 3) and
`SalesCalculator` (his TODOs 1–4). Agree an interface early and work in
parallel — do not wait for him to finish before starting the JSPs.

## Task 2 — `ValidationUtil` (30 min)

**File:** `src/main/java/my/edu/uptm/pharmatrack/util/ValidationUtil.java`

- [ ] **TODO 1** — `isValidEmail()`. A simple regex is fine:
      `^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$`. Do not attempt a fully RFC-compliant
      email regex; it is several hundred characters and still wrong.
- [ ] **TODO 2** — `isValidPhone()`. Accept the formats people actually type:
      `03-3342 2222`, `0333422222`, `+603 3342 2222`. Strip spaces and dashes
      first, then check length.
- [ ] **TODO 3** — `escapeHtml()`. Replace `& < > " '` with entities.

---

## Task 3 — Use Case Diagram ⭐ (1–2 hours)

**Report section 6.1. Part of the 20-mark diagram criterion.**

Starting point: [`../diagrams/use-case.md`](../diagrams/use-case.md) — actors and
use cases already listed to match the code.

- [ ] Two actors: **Admin** and **Cashier**, drawn as stick figures outside the
      system boundary box
- [ ] Show the **generalisation** arrow: Admin inherits everything Cashier can
      do, plus the management use cases. This single arrow is what separates a
      thought-through diagram from a list of bubbles.
- [ ] Use `<<include>>` where a use case always involves another (e.g.
      *Process Sale* `<<include>>` *Update Stock*)
- [ ] Draw the system boundary and label it "PharmaTrack"

---

## Task 4 — Activity Diagram ⭐ (1–2 hours)

**Report section 6.2. Same 20-mark criterion.**

Starting point: [`../diagrams/activity-sale.md`](../diagrams/activity-sale.md)

Diagram the **Process Sale** flow — it is the one with real decision points:

```
start → search medicine → [in stock?] ─no→ show error ──┐
                              │yes                       │
                       add to basket ←───────────────────┘
                              ↓
                      [add more?] ─yes→ back to search
                              │no
                       calculate total
                              ↓
                       [confirm?] ─no→ cancel → end
                              │yes
                  save sale + deduct stock  (one transaction)
                              ↓
                      print receipt → end
```

- [ ] Filled circle for start, ringed circle for end
- [ ] **Diamonds** for every decision, with both branches labelled
- [ ] Consider swimlanes (Cashier / System / Database) — cheap to add, and it
      visibly demonstrates the layered architecture

---

## Task 5 — Interface Design section (1 hour)

**Report section 7.** The brief requires **at least five forms/pages**. Take a
clean screenshot of each, with realistic data — not `test test test`:

1. Login page
2. Dashboard
3. Medicine list (showing the search box and a Low badge)
4. Medicine add/edit form
5. Point of sale — the basket with a running total
6. Receipt
7. Search page *(Yasierul's)*
8. Sales summary report *(Yasierul's)*

For each: a caption saying what it does, which servlet serves it, and which
rubric requirement it satisfies.

---

## Optional polish (cheap marks in the Interface criterion)

- [ ] Sortable table columns
- [ ] Pagination once the list passes ~20 rows
- [ ] A "N items" count above each table
- [ ] Move `dashboard.jsp` behind a proper `DashboardServlet` — it currently has
      a scriptlet in it, which is the one place the project breaks its own MVC
      rule. Fixing it is a genuine architecture point for the report.

---

## Your reflection (report section 9)

150–250 words. Worth writing about:

- Why one servlet per entity with `?action=` routing, rather than five servlets
- The POST-redirect-GET problem and how you found it
- Keeping the basket in the session rather than writing to the database as you go
- Coordinating with Yasierul across the SaleServlet / SaleDAO boundary
- What you would design differently with more time
