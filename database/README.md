# Database Setup — MySQL Workbench

**Module owner: Ramzi** (Database Design & Data Access Layer)
Everyone has to run this once before the app will start.

---

## What you need

- **MySQL Server 5.7 or 8.x** running on `localhost:3306`
- **MySQL Workbench** (the GUI you use to run the scripts)

If you installed MySQL Workbench but no server, the connection will fail with
`Communications link failure` — Workbench is only the client.

---

## Step by step

1. Open **MySQL Workbench** and click your local connection (usually
   `Local instance MySQL80`). Enter your root password.

2. **File → Open SQL Script…** → choose `database/01_schema.sql`.

3. Click the **⚡ lightning bolt** (Execute All). You should see
   `PharmaTrack schema created successfully.` in the output grid.

4. Repeat steps 2–3 for **`database/02_seed_data.sql`**.

5. In the **SCHEMAS** panel on the left, click the refresh icon. A
   `pharmatrack` schema should appear with 4 tables.

6. Open `src/main/resources/db.properties` in NetBeans and change
   `db.password` to **your own MySQL root password**. Leave everything else.

---

## Test accounts

| Username  | Password     | Role    |
|-----------|--------------|---------|
| `admin`   | `admin123`   | ADMIN   |
| `cashier` | `cashier123` | CASHIER |

These are stored as BCrypt hashes, not plain text. If you want to add your own
account, see `docs/tasks/aliff-auth-security.md` — there is a one-line helper
that prints a hash for you to paste in.

---

## The tables

```
medicines ──1:M──> sale_items <──M:1── sales ──M:1──> users
```

| Table        | What it holds                        | Rows after seeding |
|--------------|--------------------------------------|--------------------|
| `users`      | Login accounts + BCrypt hash + role  | 2                  |
| `medicines`  | The inventory                        | 13                 |
| `sales`      | Receipt header (one per transaction) | 2                  |
| `sale_items` | Receipt lines — junction table       | 6                  |

`sale_items` is a junction table resolving the many-to-many between sales and
medicines — that is what lets one receipt hold several different medicines.

Three medicines are seeded **below their `reorder_level`** so the Low/OK badge
on the medicine list has something to show during the demo.

---

## If something goes wrong

| Error | Fix |
|-------|-----|
| `Access denied for user 'root'@'localhost'` | Wrong password in `db.properties` |
| `Unknown database 'pharmatrack'` | You skipped `01_schema.sql` |
| `Communications link failure` | MySQL **Server** is not running (start it in Workbench or Services) |
| `Public Key Retrieval is not allowed` | Already handled — the JDBC URL in `db.properties` includes `allowPublicKeyRetrieval=true` |
| `The server time zone value ... is unrecognized` | Already handled — the URL includes `serverTimezone=Asia/Kuala_Lumpur` |

---

## Before you write a DAO query

Test it in Workbench first. `03_sample_queries.sql` has every query the app
needs, already written and runnable. If it works there, paste it into the DAO
and swap the literal values for `?` placeholders.
