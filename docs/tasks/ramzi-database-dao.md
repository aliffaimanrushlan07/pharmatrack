# Ramzi — Database Design & Data Access Layer

> Your module is the foundation everyone else builds on. It is also the most
> front-loaded: most of your code is already written and working, so your real
> job is **reviewing, owning and being able to explain it** — plus the ERD and
> Class Diagram, which are worth 20 marks between them.

---

## What you own

| File | Status | Note |
|------|--------|------|
| `database/01_schema.sql` | ✅ Written | 5 tables, FKs, constraints, low-stock view |
| `database/02_seed_data.sql` | ✅ Written | Sample data + 2 hashed accounts |
| `database/03_sample_queries.sql` | ✅ Written | Every query the DAOs run |
| `database/README.md` | ✅ Written | Workbench setup guide |
| `dao/DBConnection.java` | ✅ Written | Single connection point — **nobody else edits this** |
| `dao/GenericDAO.java` | ✅ Written | The interface every DAO implements |
| `dao/MedicineDAO.java` | ✅ Written | The worked example for the whole team |
| `model/*.java` (5 files) | ✅ Written | One POJO per table |

**Report sections you own:** 6.4 Class Diagram, 6.5 ERD, Appendix A.

---

## Task 1 — Verify the database (30 min)

Before anyone else can start, the schema has to be provably correct on a clean
machine.

- [ ] Run `01_schema.sql` in MySQL Workbench on a **fresh** database. It drops
      and recreates, so this is safe to repeat.
- [ ] Run `02_seed_data.sql`. Check the confirmation output: 2 users,
      4 suppliers, 13 medicines, 2 sales, 6 sale items.
- [ ] Run `SELECT * FROM v_low_stock;` — you should get exactly 3 rows.
- [ ] Open `03_sample_queries.sql` and run every query in it. All of them
      should return results without error.
- [ ] Tell the group in the chat that the database is verified. **This unblocks
      everyone.**

---

## Task 2 — Own the DAO layer (1 hour)

The code is written; your job is to understand it well enough to defend it.
Read `MedicineDAO.java` line by line and make sure you can answer these, because
they are exactly what an examiner asks:

- [ ] **Why `PreparedStatement` and not string concatenation?**
      *SQL injection. `"... WHERE name = '" + input + "'"` lets a user type
      `'; DROP TABLE medicines; --` and mean it. A `?` placeholder sends the
      value separately from the SQL, so it can never be read as code.*

- [ ] **Why try-with-resources?**
      *It closes the connection even when the query throws. Without it, every
      failed query leaks a connection, and after a few dozen the app stops
      responding entirely — which looks like a random crash days later.*

- [ ] **Why does `deductStock` take a `Connection` parameter when nothing else does?**
      *So it can run inside the caller's transaction. If it opened its own
      connection it would be a separate transaction, and Yasierul's rollback
      would not undo it.*

- [ ] **Why `BigDecimal` for price and not `double`?**
      *Binary floating point cannot represent 0.10 exactly. `0.1 + 0.2` gives
      0.30000000000000004. In money that is a till that drifts.*

- [ ] **Why is `subtotal` a generated column in the database when Java also
      calculates it?**
      *Defence in depth — the database guarantees consistency even if a row is
      ever inserted outside the app, and the two agreeing is a useful check.*

---

## Task 3 — Entity-Relationship Diagram ⭐ (2 hours)

**Report section 6.5. Part of the 20-mark diagram criterion.**

Starting point with all tables, columns and relationships already listed:
[`../diagrams/erd.md`](../diagrams/erd.md)

- [ ] Draw it properly in **draw.io** (free, no account needed), **Lucidchart**,
      or **MySQL Workbench's own reverse-engineer feature**
      (Database → Reverse Engineer → point it at `pharmatrack` — it generates a
      real ERD from the live schema in about 30 seconds, which is by far the
      fastest route and guarantees it matches the code).
- [ ] Show **crow's-foot notation** for cardinality — the marking band asks for
      relationships to be "accurately reflected".
- [ ] Mark every PK and FK.
- [ ] Export as PNG and paste into the report.

The five relationships to show:

```
suppliers  1 ──────< M  medicines      (one supplier, many medicines)
users      1 ──────< M  sales          (one cashier, many sales)
sales      1 ──────< M  sale_items     (one receipt, many lines)
medicines  1 ──────< M  sale_items     (one medicine, many lines)

medicines M >─────< M  sales           resolved through sale_items
```

---

## Task 4 — Class Diagram ⭐ (2 hours)

**Report section 6.4. Also part of the 20-mark criterion.**

Starting point: [`../diagrams/class-diagram.md`](../diagrams/class-diagram.md)

This is the one place to show off the architecture, so do not just draw the
model classes:

- [ ] Show the four layers as separate groupings: **model**, **dao**,
      **service**, **controller**.
- [ ] Show `GenericDAO<T>` as an **interface**, with a dashed arrow
      (realisation) from `MedicineDAO`, `SupplierDAO` and `UserDAO`.
      *This is the visible evidence for the brief's "implementing classes and
      interfaces" requirement — do not leave it out.*
- [ ] Show composition between `Sale` and `SaleItem` (filled diamond — sale
      items cannot exist without their sale).
- [ ] Show dependency arrows from each servlet to the DAO it uses.

---

## Task 5 — Appendix A (30 min)

The report has a database table reference in Appendix A. Update it so it matches
the final schema exactly — column names, types, keys, and a note on each
constraint. It is currently based on an earlier draft and a few names have
changed (`quantity_in_stock`, `reorder_level`, `password_hash`).

---

## Optional, if you have time

These lift the *Database Integration* criterion from Good to Excellent:

- [ ] **A stored procedure** for the sale transaction, as an alternative to
      doing it in Java. Even writing it and explaining why you chose the Java
      route is worth a paragraph in the report.
- [ ] **A trigger** that blocks selling expired medicine.
- [ ] **JUnit tests** for `MedicineDAO` — the `junit` dependency is already in
      `pom.xml`, so this costs you nothing to set up.

---

## Your reflection (report section 9)

150–250 words, in your own voice. Things worth writing about:

- Why you chose five tables rather than the two the brief required as a minimum
- The reasoning behind `ON DELETE SET NULL` for suppliers but `ON DELETE RESTRICT` for sale items
- Storing `unit_price` on `sale_items` rather than joining to the current price
- Anything that genuinely surprised you — those are the sentences that read as real
