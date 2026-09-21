# PharmaTrack User Manual

> **STATUS: SKELETON — Yasierul to complete (report section 11, 10 marks).**
>
> Every step below needs **one screenshot and one sentence**. The Excellent band
> asks for "comprehensive user manual with clear instructions and visuals", so
> the screenshots are not optional — a text-only manual caps out at Good.
>
> Write this **after** the features work, so the screenshots show the real thing.
> Write it for a pharmacy assistant who has never seen the system, not for
> someone who built it.

---

## 1. System requirements and installation

**TODO:** minimum requirements and how to get the system running.

- Requirements: Windows 10 / macOS, Java 8+, MySQL 8, a modern browser
- Setup steps: condensed from [`SETUP-NETBEANS.md`](SETUP-NETBEANS.md)
- Where to find the system once it is running: `http://localhost:8080/pharmatrack`

*[Screenshot: the login page in a browser]*

---

## 2. Logging in

**TODO**

1. Open the browser and go to the system address
2. Enter your username and password
3. Click **Sign in**

*[Screenshot: login page with fields filled]*
*[Screenshot: the dashboard after a successful login]*

**If your password is wrong:** the system shows *"Invalid username or password"*.
It deliberately does not say which one was wrong, for security reasons.

**Roles:** Admin can manage records; Cashier can sell and search.

---

## 3. The dashboard

**TODO** — explain each tile and what the low-stock number means.

*[Screenshot: dashboard with the four tiles]*

---

## 4. Managing medicines

### 4.1 Viewing the list
*[Screenshot: medicine list]* — explain the columns and the **Low** badge.

### 4.2 Adding a medicine
*[Screenshot: empty add form]*
*[Screenshot: form filled in]*
*[Screenshot: success message on the list]*

Explain each field, especially **reorder level** — the figure at or below which
the medicine is flagged as low stock.

### 4.3 Editing
*[Screenshot: edit form pre-filled]*

### 4.4 Deleting
*[Screenshot: the confirmation dialog]*

Note: a medicine that appears on a past receipt cannot be deleted. Set its stock
to 0 instead.

---

## 5. Managing suppliers

**TODO** — same structure as section 4.

---

## 6. Processing a sale

**TODO** — the most important section for the demo.

1. Open **New Sale**
2. Search for the medicine
3. Enter the quantity and click **Add**
4. Repeat for each item
5. Check the total
6. Click **Complete sale**
7. The receipt appears

*[Screenshot: each step]*

Explain that stock is deducted automatically, and that a sale is refused if
there is not enough stock.

---

## 7. Searching records

**TODO**

- Searching medicines by name or category
- Searching sales by date range
- What happens when nothing matches

*[Screenshot: search page with results]*

---

## 8. Reports

**TODO**

- **Low-stock report** — what the shortfall column means and how to act on it
- **Sales summary** — daily revenue, transaction count, average sale

*[Screenshot: each report]*

---

## 9. Logging out

**TODO** — click **Log out** in the top right. Explain that the system also logs
you out automatically after 30 minutes of inactivity, and why that matters on a
shared counter terminal.

---

## 10. Troubleshooting

**TODO** — a short table of what a non-technical user might hit:

| Problem | What to do |
|---------|------------|
| "Invalid username or password" | Check caps lock; ask an admin to reset |
| Page will not load | Check the system is running |
| "Cannot reach the database" | MySQL is not running — tell IT |
| Logged out unexpectedly | 30-minute inactivity timeout; sign in again |
