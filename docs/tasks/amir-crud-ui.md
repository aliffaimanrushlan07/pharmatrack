# Amir — CRUD & Interface Module

> You own the biggest visible surface of the system. Two rubric criteria are
> yours outright — **CRUD Functionality (10)** and **Interface Design (10)** —
> and the examiner spends most of the demo looking at your screens.
>
> The good news: the Medicine module is finished and working as your worked
> example. Suppliers is the same job with fewer fields.

---

## What you own

| File | Status | What to do |
|------|--------|------------|
| `controller/MedicineServlet.java` | ✅ Done | Read it — it is your template |
| `webapp/medicine/list.jsp` | ✅ Done | Your template for list pages |
| `webapp/medicine/form.jsp` | ✅ Done | Your template for form pages |
| `webapp/css/style.css` | ✅ Done | Tweak freely, it is yours |
| `webapp/includes/*.jspf` | ✅ Done | Shared header/footer |
| `dao/SupplierDAO.java` | 🔴 **STUB** | TODO 1–6 |
| `controller/SupplierServlet.java` | 🔴 **STUB** | TODO 1–6 |
| `webapp/supplier/list.jsp` | 🔴 **STUB** | Copy medicine/list.jsp |
| `webapp/supplier/form.jsp` | 🔴 **STUB** | Copy medicine/form.jsp |
| `util/ValidationUtil.java` | 🟡 Partial | TODO 1–3 |
| `webapp/dashboard.jsp` | 🟡 Partial | Move it behind a servlet |

**Report sections you own:** 6.1 Use Case Diagram, 6.2 Activity Diagram,
7. Interface Design.

---

## Task 1 — `SupplierDAO` ⭐ START HERE (1–2 hours)

**File:** `src/main/java/my/edu/uptm/pharmatrack/dao/SupplierDAO.java`

All six SQL statements are already written and tested at the top of the file.
You are writing the Java around them. Open `MedicineDAO.java` in a split window
(right-click the tab → *New Document Tab Group*) and work through in this order:

- [ ] **TODO 1 — `mapRow()`** · do this first, everything depends on it.
      Seven lines: `new Supplier()`, then a setter per column.
      Once it is done, `findAll()` works immediately — the rest of that method
      is already written for you.
- [ ] **TODO 2 — `findById()`** · `ps.setInt(1, id)`, then `rs.next() ? mapRow(rs) : null`.
- [ ] **TODO 3 — `insert()`** · needs `Statement.RETURN_GENERATED_KEYS`.
- [ ] **TODO 4 — `update()`** · ⚠️ five SET params are 1–5, the WHERE param is 6.
      Off-by-one here is the most common bug in this kind of code.
- [ ] **TODO 5 — `delete()`** · straightforward.
- [ ] **TODO 6 — `search()`** · wrap the keyword: `"%" + keyword.trim() + "%"`,
      then `setString`. Never put the `%` into the SQL string itself.

**Test as you go.** After TODO 1 and 2, run the app and open the *Add medicine*
form — the supplier dropdown should fill up. That is your DAO working, before
you have written a single supplier page.

---

## Task 2 — `SupplierServlet` (1–2 hours)

**File:** `src/main/java/my/edu/uptm/pharmatrack/controller/SupplierServlet.java`

A direct translation of `MedicineServlet`, minus the date and price handling.

- [ ] **TODO 1** — `doGet` switch on `?action=`: `new` / `edit` / `delete` / default `list`
- [ ] **TODO 2** — `listSuppliers()` → forward to `/supplier/list.jsp`
- [ ] **TODO 3** — `showForm()` → forward to `/supplier/form.jsp`
- [ ] **TODO 4** — `doPost` → `saveSupplier()`, insert when `supplierId == 0`, else update
- [ ] **TODO 5** — `deleteSupplier()`
- [ ] **TODO 6** — server-side validation using `ValidationUtil`

**Two things not to skip:**

1. **POST-redirect-GET.** After a successful save, `sendRedirect` — do not
   `forward`. Otherwise F5 on the result page re-submits the form and creates a
   duplicate record. An examiner will press F5.
2. **Validate on the server, not just in HTML.** The `required` attribute on an
   input is a convenience for honest users; anyone can bypass it. `MedicineServlet`
   has a `validate()` method showing the pattern.

---

## Task 3 — Supplier JSP pages (1–2 hours)

- [ ] `webapp/supplier/list.jsp` — copy `medicine/list.jsp`, change the columns to
      `# / Name / Contact person / Phone / Email / Medicines / Actions`
- [ ] `webapp/supplier/form.jsp` — copy `medicine/form.jsp`, fields:
      name (required), contact person, phone, email, address
- [ ] Delete the `todo-banner` div from both once they work

Keep the three habits from the examples: **no Java in the JSP**, **everything
through `<c:out>`**, and **a proper empty state** instead of a bare empty table.

---

## Task 4 — `ValidationUtil` (30 min)

**File:** `src/main/java/my/edu/uptm/pharmatrack/util/ValidationUtil.java`

- [ ] **TODO 1** — `isValidEmail()`. A simple regex is fine:
      `^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$`. Do not attempt a fully RFC-compliant
      email regex; it is several hundred characters and still wrong.
- [ ] **TODO 2** — `isValidPhone()`. Accept the formats people actually type:
      `03-3342 2222`, `0333422222`, `+603 3342 2222`. Strip spaces and dashes
      first, then check length.
- [ ] **TODO 3** — `escapeHtml()`. Replace `& < > " '` with entities.

---

## Task 5 — Use Case Diagram ⭐ (1–2 hours)

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

## Task 6 — Activity Diagram ⭐ (1–2 hours)

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

## Task 7 — Interface Design section (1 hour)

**Report section 7.** The brief requires **at least five forms/pages**. Take a
clean screenshot of each, with realistic data — not `test test test`:

1. Login page
2. Dashboard
3. Medicine list (showing the search box and a Low badge)
4. Medicine add/edit form
5. Supplier list
6. Point of sale *(Yasierul's — coordinate)*
7. Search page *(Yasierul's)*
8. Low-stock report *(Yasierul's)*

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
- Why server-side validation matters when the form already has `required`
- What you would design differently with more time
