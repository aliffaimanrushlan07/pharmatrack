# Yasierul — Business Logic, Reports & Documentation

> **Scope changed:** the point-of-sale screen moved to Amir, and the low-stock
> report and `InventoryService` were removed from the system. You keep the
> engine room — `SaleDAO`, `SalesCalculator`, search and reports — plus the
> documentation.
>
> **Do the search page first** — it is a whole 10-mark criterion and takes
> about an hour, because the DAO method it needs is already written.
>
> **Amir depends on you.** His point-of-sale screen calls your
> `SaleDAO.insertSale()` and `SalesCalculator`. Agree the method signatures
> with him on day one so you can both work in parallel.
>
> Two rubric criteria are yours outright: **Search & Calculation Features (10)**
> and, through the user manual, **User Manual (10)** in the documentation rubric.

---

## What you own

| File | Status | What to do |
|------|--------|------------|
| `controller/SearchServlet.java` | 🔴 **STUB** | TODO 1–4 · **do this first** |
| `webapp/search.jsp` | 🔴 **STUB** | Results table |
| `dao/SaleDAO.java` | 🔴 **STUB** | TODO 1–5 · includes the transaction |
| `service/SalesCalculator.java` | 🟡 Partial | TODO 1–4 |
| `controller/ReportServlet.java` | 🟡 Partial | TODO 1–3 · routing + stock report done |
| `webapp/report/stock.jsp` | ✅ Done | Inventory valuation — **your worked example** |
| `webapp/report/sales.jsp` | 🔴 **STUB** | Sales summary — copy `stock.jsp` |
| `docs/USER-MANUAL.md` | 🔴 **STUB** | Report section 11 |

**Report sections you own:** 5. System Scope, 8. Contribution Matrix (compile),
10. GitHub link, 11. User Manual, 12. References.

---

## Task 1 — Search page ⭐ DO THIS FIRST (1 hour)

**Files:** `controller/SearchServlet.java`, `webapp/search.jsp`

Highest marks-per-hour in the whole project. `MedicineDAO.search(keyword)` is
already written and tested, so your first working version is about fifteen lines.

- [ ] **TODO 1** — read `keyword`, call `medicineDAO.search(keyword)`, put the
      result in a request attribute called `results`, forward to `search.jsp`
- [ ] Render the results table in `search.jsp` — copy the markup from
      `medicine/list.jsp`
- [ ] **TODO 2–3** — a `type` parameter switching between medicines and sales;
      sales use `findByDateRange` (needs SaleDAO TODO 4)
- [ ] **TODO 4** — show `No results found for "xyz"` rather than an empty table

**Two details that lift this from Satisfactory to Excellent, two lines each:**

1. Keep the keyword in the search box after submitting. A box that clears itself
   feels broken.
2. Show a result count — *"3 results for 'para'"*.

---

## Task 2 — `SalesCalculator` (1 hour)

**File:** `src/main/java/my/edu/uptm/pharmatrack/service/SalesCalculator.java`

`calculateSubtotal()` is done as your worked example. The rest follow the same
shape.

- [ ] **TODO 1** — `applyDiscount()` · multiply first, round last
- [ ] **TODO 2** — `calculateTax()` · using `SST_RATE`
- [ ] **TODO 3** — `calculateChange()` · return negative when the payment is short,
      so the JSP can say "short by RM 3.50"
- [ ] **TODO 4** — `calculateAverageSale()` · ⚠️ guard against divide-by-zero on
      an empty database. That `ArithmeticException` is a classic way to crash a
      demo on the examiner's freshly-seeded machine.

**The BigDecimal trap:** `total.add(x)` on its own does nothing. BigDecimal is
immutable — you must write `total = total.add(x)`. This bug is silent; the
number is just quietly wrong.

---

## Task 3 — `SaleDAO`, including the transaction ⭐ (3–4 hours)

**File:** `src/main/java/my/edu/uptm/pharmatrack/dao/SaleDAO.java`

The hardest piece of code in the project, and the most valuable. The full
transaction pattern is written out in the class comment at the top of the file —
follow it closely.

- [ ] **TODO 1** — `findAll()` · easiest, start here
- [ ] **TODO 2** — `findById()` · two queries: header, then line items
- [ ] **TODO 3** — `insertSale()` · **the transaction**
- [ ] **TODO 4** — `findByDateRange()` · ⚠️ append `" 23:59:59"` to the end date,
      or MySQL reads it as midnight and silently drops the last day's sales
- [ ] **TODO 5** — `getDailySummary()` · feeds the report

**Why `insertSale` has to be a transaction:** one user action writes to three
tables — the receipt header, its lines, and the stock deduction. If the stock
update fails after the sale row is written, the pharmacy has a receipt for
medicine it never dispensed, permanently. `setAutoCommit(false)` … `commit()` …
`rollback()` in the catch block is what makes all three succeed or none.

All four steps must share **one** `Connection`. That is exactly why
`MedicineDAO.deductStock()` takes a connection parameter instead of opening its
own — a second connection would be a second transaction, and your rollback would
not reach it.

**Demo this.** Try to sell 100 units of something with 5 in stock: the sale is
refused, and afterwards there is no orphan row in `sales` and the stock is
untouched. Examiners like seeing a rollback actually work.

---

## Task 4 — Support Amir's point-of-sale screen (ongoing)

Amir owns `SaleServlet`, `pos.jsp` and `receipt.jsp`. He calls into your code:

| He calls | You provide | Your TODO |
|---|---|---|
| `saleDAO.insertSale(sale)` | the transaction | TODO 3 |
| `SalesCalculator.calculateSubtotal(items)` | running total | done |
| `saleDAO.findById(id)` | receipt data | TODO 2 |

Agree these signatures **before either of you starts**, then work in parallel.
Do not let him block on you or you on him.

## Task 5 — Reports (1–2 hours)

**Files:** `controller/ReportServlet.java`, `webapp/report/sales.jsp`

**Half of this is already built for you.** The Reports page now has two tabs,
chosen by `?type=` on the same servlet:

| Tab | URL | State |
|-----|-----|-------|
| Inventory valuation | `/report?type=stock` (default) | ✅ Working |
| Sales summary | `/report?type=sales` | 🔴 Yours |

The **inventory valuation** report is done and reads only the `medicines`
table, so it works on a freshly seeded database before a single sale exists.
That matters for you: it means the Reports page already demonstrates a real
aggregate calculation, so your sales summary is the *enhancement* rather than
the thing the whole criterion rests on. Build the rest without panicking about
the deadline.

Read these three together before you start — they are the same page you are
about to write, one report earlier:

- `MedicineDAO.getStockValuation()` — the aggregate query and its DTO mapping
- `StockValuation.grandTotal()` — the total row, calculated in Java
- `webapp/report/stock.jsp` — tabs, summary tiles, table, `<tfoot>` total

- [ ] **TODO 1** — `getDailySummary()` rendered in `sales.jsp`
- [ ] **TODO 2** — grand total revenue and overall average — this row is the
      single clearest piece of evidence for the calculation criterion
- [ ] **TODO 3** — expiring-soon report. SQL is ready in
      `database/03_sample_queries.sql`, section E1

> **⚠️ The averaging trap, which the examiner may well probe.** The overall
> average sale is `total revenue ÷ total transactions`. It is **not** the mean
> of the daily averages — the days have different transaction counts, so
> averaging the averages over-weights the quiet days. `StockValuation`
> hits the same trap and solves it by weighting; read the comment on
> `grandTotal()` and do the equivalent.

> The low-stock report and reorder calculation were **removed from scope**.
> The Low/OK badge on the medicine list stays — it is computed in
> `Medicine.isLowStock()` and needs nothing from you.

**Two things to say in the viva about the valuation report**, since it is on
your page and you will be asked:

1. *Why group in SQL rather than in Java?* One round trip and one result set,
   instead of pulling every medicine across the wire to add them up.
2. *Why is the total row calculated in Java, then?* Because the weighted
   average is far easier to read — and to unit-test — as Java than as a nested
   SQL expression, and the JSP should never contain arithmetic.

## Task 6 — User Manual ⭐ (3–4 hours)

**File:** `docs/USER-MANUAL.md` → report section 11. **Worth 10 marks.**

The Excellent band asks for "comprehensive user manual with clear instructions
and **visuals**". Screenshots are not optional — a text-only manual caps out at
Good.

A skeleton with all eight sections is already in the file. For each step:
**one screenshot, one sentence of instruction.** Write it for a pharmacy
assistant who has never seen the system, not for someone who built it.

Do this **after** the features work, so the screenshots are of the real thing.

---

## Task 7 — Report sections 5, 8, 10, 12 (2 hours)

- [ ] **Section 5 — System Scope.** Target users and system features. A draft
      exists; update it to match what was actually built, not what was planned.
- [ ] **Section 8 — Contribution Matrix.** You compile it; each member verifies
      their own row before submission. It must match the git history — the
      lecturer can see both.
- [ ] **Section 10 — GitHub link.** Paste the repository URL. Confirm the
      lecturer can open it, and that all four members have visible commits.
- [ ] **Section 12 — References, APA format.** Four starters are in the draft.
      Add the sources each member actually used — BCrypt documentation, the
      Servlet specification, the MySQL manual, any tutorial that helped. The
      *Ethical Practice* criterion in the individual rubric explicitly mentions
      proper citation.

---

## Your reflection (report section 9)

150–250 words. Worth writing about:

- The transaction — why it was necessary, and what would have broken without it
- Why `BigDecimal` and not `double` for money
- Designing an interface Amir could build against before your code existed
- Coordinating four people's modules while also compiling the documentation
