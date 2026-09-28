# PharmaTrack User Manual

**Pharmacy Inventory and Sales Management System**
SWC4253 / SWC4593 Enterprise Software Development — Report section 11

> **Screenshots** are stored in `docs/screenshots/` and were taken from the
> running system with the sample data loaded. The full list is in
> [Appendix A](#appendix-a--screenshot-checklist).

This manual is written for a pharmacy assistant using PharmaTrack for the first
time. No technical knowledge is needed except for section 1, which is for
whoever installs the system.

---

## Contents

1. [System requirements and installation](#1-system-requirements-and-installation)
2. [Logging in](#2-logging-in)
3. [The dashboard](#3-the-dashboard)
4. [Managing medicines (Admin)](#4-managing-medicines-admin)
5. [Processing a sale](#5-processing-a-sale)
6. [Searching records](#6-searching-records)
7. [Reports](#7-reports)
8. [Logging out](#8-logging-out)
9. [Troubleshooting](#9-troubleshooting)

---

## 1. System requirements and installation

### 1.1 What you need

| Item | Minimum |
|------|---------|
| Operating system | Windows 10 / 11, or macOS 12 or later |
| Java | JDK 8 or 11 |
| Web server | Apache Tomcat **9.x** (not 10) |
| Database | MySQL Server 8.0 or later |
| Browser | A current version of Chrome, Edge, Firefox or Safari |

### 1.2 Setting up (one time only)

1. In MySQL Workbench, open and run `database/01_schema.sql`, then
   `database/02_seed_data.sql`. This creates the `pharmatrack` database and
   loads the sample medicines and user accounts.
2. Open `src/main/resources/db.properties` and set `db.password` to your MySQL
   root password.
3. Open the project in NetBeans, make sure Tomcat 9 is selected as the server,
   and click **Run**.
4. The browser opens at **`http://localhost:8080/pharmatrack`**.

Full step-by-step instructions are in [`SETUP-NETBEANS.md`](SETUP-NETBEANS.md).

![The PharmaTrack login page in the browser](screenshots/01-login-page.png)

*Once the system is running, this login page is the first thing you see.*

---

## 2. Logging in

1. Open the browser and go to `http://localhost:8080/pharmatrack`.
2. Type your **Username** and **Password**.
3. Click **Sign in**.

![Login form filled in](screenshots/02-login-filled.png)

*Enter your username and password, then click Sign in.*

### 2.1 Accounts and roles

There are two kinds of user. What you can see depends on your role, which is
shown as a badge next to your name in the top-right corner.

| Role | Can do | Sample account |
|------|--------|----------------|
| **Admin** | Everything: manage medicines, sell, search, view reports | `admin` / `admin123` |
| **Cashier** | Sell, search and view reports. Cannot add, edit or delete medicines | `cashier` / `cashier123` |

> Change the sample passwords before using the system in a real pharmacy.

A cashier does not see the **Medicines** menu or the **Add medicine** button.
If a cashier types the medicines address into the browser anyway, the system
refuses and returns them to the dashboard:

![Cashier refused from the medicines page](screenshots/23-cashier-access-denied.png)

*A cashier's menu has no Medicines link, and the page itself is refused.*

### 2.2 If login fails

If the username or password is wrong, the system shows
**"Invalid username or password"**. It deliberately does not say *which* one was
wrong, so that someone guessing cannot find out which usernames exist.

![Invalid login message](screenshots/03-login-error.png)

*A failed login shows this message. Check Caps Lock and try again.*

---

## 3. The dashboard

After signing in you land on the **Dashboard**, a quick overview of the pharmacy
today.

![Dashboard after login](screenshots/04-dashboard.png)

*The dashboard shows today's figures and shortcuts to the most common tasks.*

| Tile | What it means |
|------|---------------|
| **Medicines** | How many different medicines are in the system |
| **Sales today** | How many sales have been completed since midnight |
| **Revenue today** | The total money taken today, in RM |

Below the tiles are shortcut buttons: **Add medicine**, **New sale**,
**Search records** and **Reports**. The same pages are always available from
the navigation bar at the top of every screen.

---

## 4. Managing medicines (Admin)

> Only **Admin** users can open this section. A cashier who tries is sent back
> to the dashboard with the message *"Access denied: only an Admin can manage
> medicines."*

### 4.1 Viewing the medicine list

Click **Medicines** in the navigation bar.

![Medicine list](screenshots/05-medicine-list.png)

*The medicine list shows every item in stock, newest first.*

| Column | Meaning |
|--------|---------|
| **#** | The medicine's ID number |
| **Name / Category** | What the medicine is and its group (e.g. Analgesic) |
| **Price (RM)** | Selling price for one unit |
| **Stock** | How many units are on the shelf now |
| **Status** | **OK** (green) or **Low** (red) — *Low* means stock has reached or fallen below the reorder level, so it is time to order more |
| **Expiry** | The expiry date printed on the stock |

Use the search box at the top of the list to filter by name or category.

### 4.2 Adding a medicine

1. Click **+ Add medicine**.
2. Fill in the form. Fields marked **\*** are required.
3. Click **Add medicine** at the bottom of the form.

![Empty add medicine form](screenshots/06-medicine-add-empty.png)

*The empty Add medicine form.*

| Field | What to enter |
|-------|---------------|
| **Medicine name \*** | Full name including strength and pack size, e.g. *Paracetamol 500mg (100 tabs)* |
| **Category** | The group, e.g. *Analgesic*, *Antibiotic* |
| **Unit price (RM) \*** | Selling price, e.g. `12.50` |
| **Quantity in stock \*** | Units being put on the shelf |
| **Reorder level \*** | When stock falls to this number or below, the medicine is flagged **Low** |
| **Expiry date** | The date on the packaging |

![Add medicine form filled in](screenshots/07-medicine-add-filled.png)

*A completed form, ready to save.*

![Success message after saving](screenshots/08-medicine-saved.png)

*After saving, a green message confirms the medicine was added.*

If something is missing or invalid (for example a negative price), the form is
shown again with a red message explaining what to fix. Nothing you typed is lost.

### 4.3 Editing a medicine

Click **Edit** on the medicine's row, change the fields, and click **Save changes**.

![Edit form pre-filled](screenshots/09-medicine-edit.png)

*The edit form opens with the current details already filled in.*

### 4.4 Deleting a medicine

Click **Delete** on the row. The browser asks you to confirm; click **OK** to
delete, or **Cancel** to keep it.

![Delete confirmation dialog](screenshots/10-medicine-delete-confirm.png)

*Deleting always asks for confirmation first, because it cannot be undone.*
*(Take this screenshot yourself: browser pop-up dialogs cannot be captured
automatically.)*

> **A medicine that appears on a past receipt cannot be deleted**, because the
> old receipt must still show what was sold. If you no longer stock it, edit it
> and set **Quantity in stock** to `0` instead.

---

## 5. Processing a sale

This is the main counter task. Click **New Sale** in the navigation bar.

![Point of sale screen](screenshots/11-pos-empty.png)

*The sale screen: available medicines on the left, the basket on the right.*

1. **Find the medicine.** Type part of its name or category in the search box
   and click **Search**, or scroll the list.
2. **Enter the quantity** in the small box beside the medicine.
3. Click **Add**. The item appears in the **Basket** and the total updates.

   ![Adding an item to the basket](screenshots/12-pos-add-item.png)

   *The basket updates immediately with the line subtotal and running total.*

4. **Repeat** for each medicine the customer is buying. Adding the same medicine
   again increases its quantity rather than creating a second line.
5. To take an item out, click **Remove** on its line. To start again, click
   **Clear basket**.
6. **Check the total** at the bottom of the basket with the customer.

   ![Basket with several items and the total](screenshots/13-pos-basket.png)

   *Check the basket and total with the customer before completing the sale.*

7. Click **Complete sale**.
8. The **receipt** appears. Click **Print receipt** to print it for the customer,
   or **New sale** to serve the next customer.

![Receipt after a completed sale](screenshots/14-receipt.png)

*The receipt shows the receipt number, date, cashier, each line and the total.*

### What happens behind the scenes

* **Stock is deducted automatically** the moment the sale is completed. You do
  not need to update the medicine list by hand.
* **A sale is refused if there is not enough stock.** You cannot add more units
  than are on the shelf: the quantity box will not accept a number above the
  stock, and if the basket already holds some units the system shows a red
  message saying how many are in stock. If another cashier sells the last units while your
  basket is open, you are warned when you click **Complete sale** and asked to
  review the quantities.
* **All or nothing.** A sale is saved completely — receipt, lines and stock
  deduction together — or not at all. There is never a receipt for medicine that
  was not taken off the shelf.

![Not enough stock message](screenshots/15-pos-insufficient-stock.png)

*Here the basket already holds all 5 Insulin Pen Needles; adding one more is refused.*

---

## 6. Searching records

Click **Search** in the navigation bar. You can search **Medicines** or
**Sales** — choose which from the drop-down on the left.

### 6.1 Searching medicines

1. Leave the drop-down on **Medicines**.
2. Type part of a name or category — for example `para` or `antibiotic`.
   You do not need the full name, and capital letters do not matter.
3. Click **Search**.

![Medicine search results](screenshots/16-search-medicine.png)

*Results show how many matches were found, e.g. "2 results for 'para'".*

Your keyword stays in the box after searching, so you can adjust it and search
again. Leave the box empty and click **Search** to list every medicine. Click
**Clear** to reset the page.

### 6.2 Searching sales by date

1. Change the drop-down to **Sales**. Two date boxes appear.
2. Pick a **From** date and a **To** date. Both days are included in full.
3. Optionally type a **receipt number** (e.g. `12` or `#12`) or part of a
   **cashier's name** to narrow the list further.
4. Click **Search**.

![Sales search results](screenshots/17-search-sales.png)

*Sales results show the count, the total value and the average sale for the period.*

Click **View receipt** on any row to open that receipt again, for example to
reprint it. Leave both dates empty to see every sale.

If you give only one date, or the From date is after the To date, a red message
explains what to correct.

### 6.3 When nothing matches

If there are no matches the page says so in words — for example
**No results found for "xyz"** — with a suggestion to try a shorter keyword or a
wider date range.

![No results message](screenshots/18-search-no-results.png)

*A clear "no results" message instead of an empty table.*

---

## 7. Reports

Click **Reports** in the navigation bar. There are three reports, chosen with the
tabs at the top of the page. The Sales summary and Expiring soon reports have
a **Print** button; the valuation report can be printed from the browser menu.

### 7.1 Inventory valuation

How much money is tied up in stock, grouped by category.

![Inventory valuation report](screenshots/19-report-valuation.png)

*Stock value by category, with each category's share of the total.*

* **Stock value** = price × units in stock, added up for each category.
* **Share** shows what percentage of the pharmacy's stock value sits in each category.
* The bold **ALL CATEGORIES** row at the bottom is the total for the whole pharmacy.

### 7.2 Sales summary

Revenue per day, with the number of transactions and the average sale.

![Sales summary report](screenshots/20-report-sales.png)

*Daily sales with a grand-total row at the bottom.*

| Figure | Meaning |
|--------|---------|
| **Total revenue** | All money taken across every day shown |
| **Transactions** | Total number of sales |
| **Average sale** | Total revenue ÷ total transactions — how much a typical customer spends |
| **Best day** | The day with the highest revenue |

The **ALL DAYS** row at the bottom totals everything. Its average is worked out
from the overall totals, so a quiet day with one sale does not count as much as
a busy day with twenty.

**Receipt total check.** Every time this report opens, the system adds up the
lines of every receipt and compares the result with the total stored on the
receipt. If any disagree, a yellow panel lists them, for example
*"Receipt #2: stored RM 104.30, lines add up to RM 104.50"*. An **Admin** can
click **Recalculate totals** to correct them all at once; a cashier is asked to
fetch an Admin. (The sample data contains one wrong receipt on purpose, so you
can see this working.)

### 7.3 Expiring soon

Medicines that will expire within a chosen period, soonest first. Use the
drop-down to choose 30 days, 60 days, 90 days (the default), 6 months, 1 year
or 2 years.

![Expiring soon report](screenshots/21-report-expiry.png)

*Medicines close to expiry, with how many days are left and the value at risk.*

| Status badge | Meaning | What to do |
|--------------|---------|------------|
| **Expired** (red) | The expiry date has passed; *Days left* is negative | Remove from the shelf immediately — do not sell |
| **≤ 30 days** (amber) | Expires within a month | Sell first, or arrange a return to the supplier |
| **Watch** (green) | Expires later in the chosen period | Keep an eye on it |

The tiles show how many items are listed, how many have already expired, how
many expire within 30 days, and the **stock value at risk** in RM.

---

## 8. Logging out

Click **Log out** at the top-right of any page. You return to the login page.

![Logged out, back at the login page](screenshots/22-logout.png)

*After logging out you are returned to the login page.*

**Automatic logout:** if the system is left untouched for **30 minutes**, you are
logged out automatically and must sign in again. On a shared counter computer
this stops the next person from making sales under your name. Always log out
yourself when you leave the counter.

---

## 9. Troubleshooting

| Problem | What to do |
|---------|------------|
| "Invalid username or password" | Check Caps Lock and spelling. If you have forgotten your password, ask an Admin to reset it. |
| Sent back to the login page unexpectedly | You were inactive for 30 minutes. Sign in again; any unfinished basket must be re-entered. |
| Sent back to the dashboard with a "forbidden" message when opening **Medicines** | Your account is a Cashier. Ask an Admin to add or change medicines. |
| "Could not load all dashboard figures" or "Cannot reach the database" | MySQL is not running. Tell whoever looks after the computer. |
| The page will not load at all | Tomcat is not running. Start the system from NetBeans (section 1.2). |
| A sale is refused for stock | There is not enough on the shelf. Reduce the quantity, or check the medicine list. |
| A medicine cannot be deleted | It appears on a past receipt. Set its stock to 0 instead (section 4.4). |
| Yellow "receipt total(s) do not match" panel on the Sales summary | A stored receipt total is wrong. An Admin clicks **Recalculate totals** (section 7.2). |
| A search finds nothing | Try fewer letters (e.g. `amox` instead of `amoxicillin 500mg`), or a wider date range. |
| "Page not found" | The address was typed wrongly. Use the navigation bar instead. |

---

## Appendix A — Screenshot checklist

Save each image in `docs/screenshots/` using the exact file name below. Use the
seeded sample data, log in as **admin** unless stated, and crop to the browser
window.

| # | File name | What to capture |
|---|-----------|-----------------|
| 01 | `01-login-page.png` | Empty login page |
| 02 | `02-login-filled.png` | Login with `admin` typed in |
| 03 | `03-login-error.png` | Login after a wrong password |
| 04 | `04-dashboard.png` | Dashboard straight after login |
| 05 | `05-medicine-list.png` | Medicine list showing Low and OK badges |
| 06 | `06-medicine-add-empty.png` | Empty Add medicine form |
| 07 | `07-medicine-add-filled.png` | Add form filled in |
| 08 | `08-medicine-saved.png` | List with the green "saved" message |
| 09 | `09-medicine-edit.png` | Edit form pre-filled |
| 10 | `10-medicine-delete-confirm.png` | Browser delete confirmation dialog — **take this one yourself** |
| 11 | `11-pos-empty.png` | New Sale screen with an empty basket |
| 12 | `12-pos-add-item.png` | Basket after adding one item |
| 13 | `13-pos-basket.png` | Basket with 2–3 items and the total |
| 14 | `14-receipt.png` | Receipt after completing the sale |
| 15 | `15-pos-insufficient-stock.png` | Basket holding all 5 Insulin Pen Needles, then **Add** 1 more → red stock message |
| 16 | `16-search-medicine.png` | Search: Medicines, keyword `para` |
| 17 | `17-search-sales.png` | Search: Sales, a date range covering the seeded sales |
| 18 | `18-search-no-results.png` | Search: Medicines, keyword `xyz` |
| 19 | `19-report-valuation.png` | Reports → Inventory valuation |
| 20 | `20-report-sales.png` | Reports → Sales summary |
| 21 | `21-report-expiry.png` | Reports → Expiring soon, set to 2 years so the seeded items appear |
| 22 | `22-logout.png` | Login page after clicking Log out |
| 23 | `23-cashier-access-denied.png` | Logged in as **cashier**, medicines address typed in → refused |
