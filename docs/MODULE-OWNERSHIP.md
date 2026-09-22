# Module ownership

Every file in this repository has exactly one owner. This page is the master
list. The same information is repeated in each file's header comment
(`MODULE OWNER:`) and enforced for reviews in [`.github/CODEOWNERS`](../.github/CODEOWNERS).

**The rule:** you may *read* anything. You may only *change* your own files. If
you need a change in someone else's file, message them or open an issue —
do not edit it yourself. That is how merge conflicts and "who broke it?"
arguments start.

**Shared files** (`pom.xml`, `web.xml`, `db.properties`, `style.css`) still have
one named owner, and a change to them is announced in the group chat before it
is pushed.

> **Scope change (agreed by the group):** supplier management and the automatic
> low-stock report were removed. Amir took the point-of-sale screen in place of
> that module; Yasierul keeps `SaleDAO`, `SalesCalculator`, search and reports. `medicines.reorder_level` survives only to drive the Low/OK badge.

---

## By file

### `database/` — **RAMZI**

| File | Status |
|------|--------|
| `01_schema.sql` | ✅ Done |
| `02_seed_data.sql` | ✅ Done |
| `03_sample_queries.sql` | ✅ Done |
| `README.md` | ✅ Done |

### `model/` — **RAMZI**

| File | Status |
|------|--------|
| `User.java` | ✅ Done |
| `Medicine.java` | ✅ Done |
| `Sale.java` | ✅ Done |
| `SaleItem.java` | ✅ Done |

### `dao/`

| File | Owner | Status |
|------|-------|--------|
| `DBConnection.java` | Ramzi | ✅ Done — **nobody else edits this** |
| `GenericDAO.java` | Ramzi | ✅ Done — the interface |
| `MedicineDAO.java` | Ramzi + Amir | ✅ Done — **worked example** |
| `UserDAO.java` | Aliff | ✅ Done |
| `SaleDAO.java` | **Yasierul** | 🔴 Stub — TODO 1–5 |

### `security/` — **ALIFF**

| File | Status |
|------|--------|
| `PasswordUtil.java` | ✅ Done — BCrypt |
| `AuthFilter.java` | ✅ Done |
| `RoleFilter.java` | 🔴 Stub — TODO 1–2 |

### `service/` — **YASIERUL**

| File | Status |
|------|--------|
| `SalesCalculator.java` | 🟡 Partial — TODO 1–4 |

### `controller/`

| File | Owner | Status |
|------|-------|--------|
| `LoginServlet.java` | Aliff | ✅ Done |
| `LogoutServlet.java` | Aliff | ✅ Done |
| `MedicineServlet.java` | Amir | ✅ Done — **worked example** |
| `SaleServlet.java` | **Amir** | 🔴 Stub — TODO 1–5 |
| `SearchServlet.java` | **Yasierul** | 🔴 Stub — TODO 1–4 |
| `ReportServlet.java` | **Yasierul** | 🔴 Stub — TODO 1–3 |

### `util/` — **AMIR**

| File | Status |
|------|--------|
| `ValidationUtil.java` | 🟡 Partial — TODO 1–3 |

### `webapp/` — views

| File | Owner | Status |
|------|-------|--------|
| `index.jsp` | Aliff | ✅ Done |
| `login.jsp` | Aliff | ✅ Done — ⚠️ remove demo credentials before submitting |
| `dashboard.jsp` | Amir | 🟡 Partial |
| `includes/header.jspf` | Amir | ✅ Done |
| `includes/footer.jspf` | Amir | ✅ Done |
| `css/style.css` | Amir | ✅ Done |
| `medicine/list.jsp` | Amir | ✅ Done — **worked example** |
| `medicine/form.jsp` | Amir | ✅ Done — **worked example** |
| `sale/pos.jsp` | **Amir** | 🔴 Stub |
| `sale/receipt.jsp` | **Amir** | 🔴 Stub |
| `search.jsp` | **Yasierul** | 🔴 Stub |
| `report/sales.jsp` | **Yasierul** | 🔴 Stub |
| `WEB-INF/web.xml` | Aliff | ✅ Done |
| `WEB-INF/error/*.jsp` | Aliff | ✅ Done |
| `META-INF/context.xml` | Aliff | ✅ Done |

### Build & config

| File | Owner | Note |
|------|-------|------|
| `pom.xml` | **Aliff** | No new dependency without asking him |
| `db.properties` | Ramzi | Everyone edits their own password locally, nobody commits it |
| `.gitignore` | Aliff | |

---

## By rubric criterion — System Development (70 marks)

| Criterion | Marks | Primary owner | Status |
|-----------|-------|---------------|--------|
| Database Integration | 10 | Ramzi | ✅ |
| Java EE Architecture & Design | 10 | Aliff | ✅ |
| CRUD Functionality | 10 | Amir | ✅ |
| Search & Calculation Features | 10 | Yasierul | 🟡 badge live, rest pending |
| Security (Login & Password) | 10 | Aliff | ✅ |
| Efficiency & Bug-Free Operation | 10 | Everyone | ⏳ |
| Interface Design (JSP/JSF) | 10 | Amir | 🟡 |

---

## By report section — Documentation (100 marks)

| § | Section | Marks | Owner |
|---|---------|-------|-------|
| 1 | Project Title | — | Group |
| 2 | Project Introduction | 10 (with §1) | Group |
| 3 | Problem Statement | 10 | **Aliff** |
| 4 | Project Objectives | 10 | **Aliff** |
| 5 | System Scope | 10 | **Yasierul** |
| 6.1 | Use Case Diagram | ⎫ | **Amir** |
| 6.2 | Activity Diagram | ⎪ | **Amir** |
| 6.3 | Sequence Diagram | ⎬ 20 | **Aliff** |
| 6.4 | Class Diagram | ⎪ | **Ramzi** |
| 6.5 | ERD | ⎭ | **Ramzi** |
| 7 | Interface Design | 10 | **Amir** |
| 8 | Contribution Matrix | — | **Yasierul** compiles, all verify |
| 9 | Lessons Learned | 10 | **Each member individually** |
| 10 | GitHub Link | — | **Yasierul** |
| 11 | User Manual | 10 | **Yasierul** |
| 12 | References (APA) | — | **Yasierul** |
| — | Code Listing | 10 | Everyone — comment your own code |

> **Section 6 is worth 20 marks** — the largest single item in the documentation
> rubric, and it is split across all four members. Starting points for all five
> diagrams, already matching the code, are in [`diagrams/`](diagrams/).

> **Code Listing is worth 10 marks** and is judged on comments and formatting.
> The existing files are heavily commented as the standard to match — keep that
> up in your own code and this is 10 marks for no extra work.
