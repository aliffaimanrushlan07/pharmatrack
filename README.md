# PharmaTrack

**Pharmacy Inventory and Sales Management System**
A Java EE web application built for SWC4253 / SWC4593 — Enterprise Software Development, Universiti Poly-Tech Malaysia.

> **New to this repo? Read [`docs/00-START-HERE.md`](docs/00-START-HERE.md) first.** It takes about 20 minutes to get running.

---

## The team and who owns what

Everything in this repository is tagged with its owner. Look for the
`MODULE OWNER:` banner at the top of any file, or find your name below.

| Member       | Module                                    | Your files                                                                                                        | Your brief |
|--------------|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------|------------|
| **Ramzi**    | Database Design & Data Access Layer       | `database/*.sql`, `dao/DBConnection.java`, `dao/GenericDAO.java`, `dao/MedicineDAO.java`, `model/*.java`            | [brief](docs/tasks/ramzi-database-dao.md) |
| **Amir**     | CRUD, Interface & Point of Sale           | `controller/MedicineServlet.java`, `controller/SaleServlet.java`, all `*.jsp`, `css/style.css`, `util/ValidationUtil.java` | [brief](docs/tasks/amir-crud-sale.md) |
| **Aliff**    | Authentication, Security & Architecture   | `security/*.java`, `controller/LoginServlet.java`, `controller/LogoutServlet.java`, `login.jsp`, `pom.xml`, `web.xml` | [brief](docs/tasks/aliff-auth-security.md) |
| **Yasierul** | Business Logic, Reports & Documentation   | `dao/SaleDAO.java`, `service/SalesCalculator.java`, `controller/{Search,Report}Servlet.java`, `search.jsp`, `report/sales.jsp`, `docs/USER-MANUAL.md` | [brief](docs/tasks/yasierul-logic-reporting.md) |

Full mapping including report sections: [`docs/MODULE-OWNERSHIP.md`](docs/MODULE-OWNERSHIP.md)

---

## Where things stand

| # | Rubric requirement                | Status         | Owner    |
|---|-----------------------------------|----------------|----------|
| 1 | Relational tables (≥2)            | ✅ Done — 4 tables incl. junction table | Ramzi |
| 2 | Forms / pages (≥5)                | 🟡 5 of 8 built | Amir |
| 3 | CRUD operations                   | ✅ Done — full Medicine CRUD | Amir |
| 4 | Search functionality              | 🔴 Not started | Yasierul |
| 5 | Login with encrypted passwords    | ✅ Done — BCrypt | Aliff |
| 6 | Business logic / calculation      | 🟡 Low-stock badge live; sale maths pending | Yasierul |
| 7 | Enterprise design (MVC + DAO)     | ✅ Structure in place | Aliff |
| 8 | Bug-free and efficient            | ⏳ Ongoing | Everyone |

✅ done 🟡 partial 🔴 not started

**What already works end to end:** login → dashboard → medicine list → add / edit / delete a medicine → search medicines → logout. The Low/OK badge on the medicine list is live.

**What is still stubbed:** point of sale, the search page, the sales summary report, and role-based access control. Each stub file has numbered `TODO`s naming its owner.

---

## Tech stack

| Layer     | Choice                                   |
|-----------|------------------------------------------|
| Language  | Java 8                                   |
| Web       | Servlets 4.0 + JSP + JSTL 1.2 (`javax.servlet`) |
| Build     | Maven (`pom.xml`)                        |
| Server    | Apache Tomcat 9.x *or* GlassFish 5–6     |
| Database  | MySQL 5.7 / 8.x via MySQL Workbench      |
| Security  | jBCrypt 0.4 password hashing             |
| IDE       | Apache NetBeans                          |

> **⚠️ `javax` not `jakarta`.** This project targets **Tomcat 9 / GlassFish 5–6**.
> On Tomcat 10+ or GlassFish 7 every `javax.servlet` import fails, because the
> namespace was renamed to `jakarta.servlet`. If your lab machine only has
> Tomcat 10, see the switching note in [`docs/SETUP-NETBEANS.md`](docs/SETUP-NETBEANS.md)
> — but agree it with the whole group first, since it changes every file.

---

## Quick start

```bash
git clone <this-repo-url>
cd PharmaTrack
```

1. **Database** — open MySQL Workbench, run `database/01_schema.sql` then `database/02_seed_data.sql`.
2. **Password** — edit `src/main/resources/db.properties`, set `db.password` to your MySQL root password.
3. **NetBeans** — File → Open Project → select this folder. It is a Maven project; NetBeans downloads the libraries by itself.
4. **Run** — press F6. Browser opens at `http://localhost:8080/pharmatrack`.
5. **Log in** — `admin` / `admin123`

Step-by-step with screenshots and troubleshooting: [`docs/SETUP-NETBEANS.md`](docs/SETUP-NETBEANS.md)

---

## Project layout

```
PharmaTrack/
├── database/                    ← SQL scripts  (Ramzi)
│   ├── 01_schema.sql                 4 tables, keys, constraints
│   ├── 02_seed_data.sql              sample data + test accounts
│   └── 03_sample_queries.sql         every query the DAOs run, testable in Workbench
│
├── docs/                        ← everything the group needs to read
│   ├── 00-START-HERE.md              read this first
│   ├── SETUP-NETBEANS.md             install + run + troubleshooting
│   ├── MODULE-OWNERSHIP.md           who owns which file and which report section
│   ├── REPORT-CHECKLIST.md           the 12 report sections, owner, status
│   ├── USER-MANUAL.md                skeleton  (Yasierul)
│   ├── tasks/                        one brief per member
│   └── diagrams/                     starting points for all 5 required diagrams
│
└── src/main/
    ├── java/my/edu/uptm/pharmatrack/
    │   ├── model/       POJOs, one per table          (Ramzi)
    │   ├── dao/         database access, one per table (Ramzi + Yasierul)
    │   ├── service/     business logic & calculations  (Yasierul)
    │   ├── security/    hashing, auth filter, role filter (Aliff)
    │   ├── controller/  servlets — the C in MVC        (all)
    │   └── util/        validation helpers             (Amir)
    ├── resources/
    │   └── db.properties     ← EDIT YOUR PASSWORD HERE
    └── webapp/               JSP views — the V in MVC  (Amir)
```

---

## The architecture, in one picture

```
 Browser
    │  HTTP request
    ▼
 ┌──────────────┐   not logged in → login.jsp
 │  AuthFilter  │  (Aliff)
 └──────┬───────┘
        │ logged in
        ▼
 ┌──────────────┐   reads parameters, no SQL, no HTML
 │  Servlet     │  (Controller)
 └──────┬───────┘
        │
        ├──────────────► ┌──────────────┐  totals, discounts, stock rules
        │                │   Service    │  (Yasierul)
        │                └──────────────┘
        ▼
 ┌──────────────┐   PreparedStatement only, one place per table
 │     DAO      │  (Ramzi)
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │    MySQL     │
 └──────────────┘
        │
        ▼  request attributes
 ┌──────────────┐   display only, no Java, everything through <c:out>
 │     JSP      │  (View — Amir)
 └──────────────┘
```

**The rule this diagram encodes:** no layer skips a layer. A JSP never talks to a DAO; a DAO never builds HTML. That separation is what the rubric's *Java EE Architecture & Design* criterion is marking, and it is the single easiest way to lose or gain those 10 marks.

---

## Learning from the worked examples

Four files are finished specifically so the rest can be copied from them. When you are stuck, open the matching example side by side:

| If you are writing…      | Copy the shape of…                       |
|--------------------------|------------------------------------------|
| a DAO                    | `dao/MedicineDAO.java`                   |
| a servlet                | `controller/MedicineServlet.java`        |
| a list page              | `webapp/medicine/list.jsp`               |
| a form page              | `webapp/medicine/form.jsp`               |

### Scope note

Supplier management and the automatic low-stock report were **removed from
scope** by group decision. `medicines.reorder_level` is kept solely to drive
the Low/OK badge on the medicine list.

---

## Working together

Branch and commit conventions, pull request flow, and what to do when git
conflicts: [`CONTRIBUTING.md`](CONTRIBUTING.md)

Short version:

```bash
git checkout -b feat/point-of-sale        # branch per feature, never push to main
git commit -m "feat(sale): add basket handling to SaleServlet"
git push -u origin feat/point-of-sale     # then open a Pull Request
```

**Commit regularly under your own name.** The report needs an Individual
Contribution Matrix and the lecturer can see the commit history — four
members with visible, steady commits is evidence; one member with 200 commits
and three with two is also evidence, of something else.

---

## Deadline

**8 October 2026, 5:00 pm** — submitted as PDF through Google Classroom, plus
this repository link in section 10 of the report.

Suggested internal milestones are in [`docs/00-START-HERE.md`](docs/00-START-HERE.md).
