# Sequence Diagrams — report section 6.3 · Owner: **Aliff**

Draw **two**. The login flow alone is thin; the Process Sale one is where all
four layers and a transaction show up.

---

## (a) Login / authentication

Participants, left to right:

`Actor:User` · `login.jsp` · `LoginServlet` · `UserDAO` · `DBConnection` · `MySQL` · `PasswordUtil` · `HttpSession`

```
User    login.jsp   LoginServlet   UserDAO   DBConnection   MySQL   PasswordUtil   Session
 │          │            │            │           │           │          │            │
 │─ open ──>│            │            │           │           │          │            │
 │<─ form ──│            │            │           │           │          │            │
 │                       │            │           │           │          │            │
 │─ POST username+password ──────────>│           │           │          │            │
 │                       │            │           │           │          │            │
 │                       │─ findByUsername(u) ───>│           │          │            │
 │                       │            │─ getConnection() ────>│          │            │
 │                       │            │<╌ Connection ╌╌╌╌╌╌╌╌╌│          │            │
 │                       │            │─ SELECT … WHERE username = ? ───>│            │
 │                       │            │<╌ ResultSet ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌│            │
 │                       │<╌ User ╌╌╌╌│           │           │          │            │
 │                       │            │           │           │          │            │
 │                       │─ verify(plain, storedHash) ────────────────-─>│            │
 │                       │<╌ boolean ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌│            │
 │                       │            │           │           │          │            │
 ╔═══ alt ═══════════════╪════════════╪═══════════╪═══════════╪══════════╪════════════╗
 ║ [password correct]    │            │           │           │          │            ║
 ║                       │─ invalidate() ─────────────────────────────────────────---->║
 ║                       │─ setAttribute("loggedInUser", user) ──────────────────────->║
 │<─ 302 redirect /dashboard.jsp ─────│           │           │          │            ║
 ╠═══════════════════════╪════════════╪═══════════╪═══════════╪══════════╪════════════╣
 ║ [password wrong]      │            │           │           │          │            ║
 ║                       │─ setAttribute("errorMessage") ─────────────────────────────║
 │<─ forward to login.jsp with error ─│           │           │          │            ║
 ╚═══════════════════════╪════════════╪═══════════╪═══════════╪══════════╪════════════╝
```

**Note the `invalidate()` before `setAttribute`.** That is the session-fixation
defence — call it out in the report text, it is a real security decision and
most student projects do not have it.

---

## (b) Process Sale — the better diagram

Participants: `Actor:Cashier` · `pos.jsp` · `SaleServlet` · `SalesCalculator` · `SaleDAO` · `MedicineDAO` · `MySQL`

```
Cashier   pos.jsp   SaleServlet   SalesCalculator   SaleDAO   MedicineDAO   MySQL
   │         │           │              │              │           │          │
   │─ add item ─────────>│              │              │           │          │
   │         │           │─ calculateSubtotal(items) ─>│           │          │
   │         │           │<╌ BigDecimal ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌│           │          │
   │<─ basket + running total ─────────│              │           │          │
   │         │           │              │              │           │          │
   │─ complete sale ────>│              │              │           │          │
   │         │           │─ insertSale(sale) ─────────────────────>│          │
   │         │           │              │              │           │          │
   ╔═══ loop ═══════════════════════════════════════════════════════════════════╗
   ║         │           │              │   setAutoCommit(false) ──────────────>║
   ║         │           │              │   INSERT INTO sales ─────────────────>║
   ║         │           │              │   INSERT INTO sale_items ────────────>║
   ║         │           │              │─ deductStock(conn,id,qty) ─>│         ║
   ║         │           │              │              │   UPDATE medicines ──>║
   ╚═══════════════════════════════════════════════════════════════════════════╝
   │         │           │              │              │           │          │
   ╔═══ alt ════════════════════════════════════════════════════════════════════╗
   ║ [all succeeded]     │              │   commit() ──────────────────────────>║
   │<─ redirect to receipt.jsp ────────│              │           │          ║
   ╠════════════════════════════════════════════════════════════════════════════╣
   ║ [any step failed]   │              │   rollback() ────────────────────────>║
   │<─ "insufficient stock" error ─────│              │           │          ║
   ╚════════════════════════════════════════════════════════════════════════════╝
```

---

## Checklist

- [ ] Lifelines as vertical dashed lines below each participant box
- [ ] **Activation bars** — the thin rectangles showing when each object is active
- [ ] **Solid** arrows for calls, **dashed** arrows for returns. Mixing these up
      is the most common lost mark on a sequence diagram.
- [ ] `alt` fragment for the success/failure branch — a diagram with only the
      happy path lands in the "misses key details" band
- [ ] `loop` fragment around the per-item work
- [ ] Message labels are real method names from the code, not prose
