# Start here

Everyone reads this once, on day one. About 20 minutes including setup.

---

## 1. What we are building

A **Pharmacy Inventory and Sales Management System** — a Java EE web app where
pharmacy staff log in, manage medicine and supplier records, ring up sales, and
see which stock needs reordering.

The assignment is worth **40% of the course**, split into two 20% halves:

- **System Development & Demonstration** — the working app, presented live
- **System Documentation** — the report (12 sections, in a fixed order)

This repository is the first half. The report is the second half and lives in
the shared Word document — but the two are linked: several report sections are
just screenshots and explanations of what is in here.

---

## 2. Find your name

| You are       | You own                                  | Read this next                                    |
|---------------|------------------------------------------|---------------------------------------------------|
| **Ramzi**     | Database & DAO layer                     | [`tasks/ramzi-database-dao.md`](tasks/ramzi-database-dao.md) |
| **Amir**      | CRUD screens & interface                 | [`tasks/amir-crud-ui.md`](tasks/amir-crud-ui.md)   |
| **Aliff**     | Login, security & architecture           | [`tasks/aliff-auth-security.md`](tasks/aliff-auth-security.md) |
| **Yasierul**  | Search, calculations, reports & docs     | [`tasks/yasierul-search-reporting.md`](tasks/yasierul-search-reporting.md) |

Your brief tells you exactly which files are yours, in what order to do them,
and what "done" looks like for each.

---

## 3. Get it running (do this before reading anything else)

Full instructions with troubleshooting: [`SETUP-NETBEANS.md`](SETUP-NETBEANS.md)

```
1. Install:  Apache NetBeans, JDK 8+, Apache Tomcat 9, MySQL Server + Workbench
2. Clone this repository
3. MySQL Workbench → run database/01_schema.sql, then 02_seed_data.sql
4. Edit src/main/resources/db.properties → set db.password to YOUR MySQL password
5. NetBeans → File → Open Project → pick the PharmaTrack folder
6. Press F6
7. Log in as admin / admin123
```

**You are not finished until you have logged in and seen the medicine list with
13 rows in it.** Do not start writing code before that works — debugging your own
code on top of a broken setup wastes an entire evening.

---

## 4. How to work on your part

Four files are already finished as **worked examples**. The stub files you have
been given are the same shape with the details removed. The intended workflow is
literally: open the example, open your stub, copy the structure, change the
entity.

| Writing…   | Open this beside it              |
|------------|----------------------------------|
| a DAO      | `dao/MedicineDAO.java`           |
| a servlet  | `controller/MedicineServlet.java`|
| a list page| `webapp/medicine/list.jsp`       |
| a form page| `webapp/medicine/form.jsp`       |

Every stub has numbered TODOs — `TODO 1`, `TODO 2` — in the order you should do
them. In NetBeans, **Window → Action Items** lists every TODO in the project.

**Finding your own work quickly:** search the project (Ctrl+Shift+F) for
`MODULE OWNER: YOUR NAME`.

---

## 5. Git, briefly

```bash
git checkout main
git pull                                   # always start from the latest
git checkout -b feat/supplier-crud         # your own branch

# ... work, then ...
git add .
git commit -m "feat(supplier): implement SupplierDAO.findAll and mapRow"
git push -u origin feat/supplier-crud      # then open a Pull Request on GitHub
```

Three rules:

1. **Never commit directly to `main`.** Branch, push, open a PR, get one other
   member to click Approve.
2. **Commit under your own name.** The lecturer reads the commit history, and
   the Individual Contribution Matrix in the report has to match it.
3. **Commit often.** Four commits of one hour each beat one commit of four
   hours — both for reviewing and for proving you did the work.

Full conventions: [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

---

## 6. The report

The report has **12 sections in a fixed order** set by the brief. Owners and
status: [`REPORT-CHECKLIST.md`](REPORT-CHECKLIST.md).

Two things to start early, because they always take longer than expected:

- **The five diagrams** (section 6) — worth **20 marks**, the single largest
  item in the documentation rubric. Starting points for all five, already
  matching the code: [`diagrams/`](diagrams/)
- **Your individual reflection** (section 9) — 150–250 words, written by you
  about your own module. Write it while you still remember what went wrong;
  reconstructing it the night before reads like it was reconstructed the
  night before.

---

## 7. Suggested milestones

Deadline is **8 October 2026, 5:00 pm**.

| By          | Everyone should have…                                                |
|-------------|----------------------------------------------------------------------|
| **Week 1**  | The app running locally. First commit pushed. Own brief read.        |
| **Week 2**  | Core module working — Amir: supplier CRUD. Yasierul: search page. Aliff: RoleFilter. Ramzi: DAOs reviewed + ERD drawn. |
| **Week 3**  | All 8 rubric items green. Diagrams finished. Screenshots taken.      |
| **Week 4**  | Report assembled, user manual written, reflections in, demo rehearsed. |
| **Buffer**  | Leave the last 3 days clear. Something always breaks.                |

---

## 8. If you get stuck

1. Read the error message properly — it usually names the file and line.
2. Check [`SETUP-NETBEANS.md`](SETUP-NETBEANS.md) — the common errors are listed with fixes.
3. Compare your file against the matching worked example.
4. If it is a SQL problem, run the query in MySQL Workbench first. Use
   `database/03_sample_queries.sql`.
5. Ask in the group chat with the **actual error text**, not "it doesn't work".
