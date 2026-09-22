# Aliff — Authentication, Security & Architecture

> Your module is small in lines of code and large in marks. **Security (10)** is
> a criterion of its own, and **Java EE Architecture & Design (10)** is largely
> a question of whether the structure you own holds up.
>
> Most of your code is already written. Your remaining work is `RoleFilter`,
> the Sequence Diagram, and being the person who can explain the architecture
> when the examiner asks.

---

## What you own

| File | Status | What to do |
|------|--------|------------|
| `security/PasswordUtil.java` | ✅ Done | BCrypt hashing — read and understand it |
| `security/AuthFilter.java` | ✅ Done | Blocks unauthenticated requests |
| `controller/LoginServlet.java` | ✅ Done | Your worked example |
| `controller/LogoutServlet.java` | ✅ Done | Session invalidation |
| `webapp/login.jsp` | ✅ Done | ⚠️ remove the demo credentials before submitting |
| `webapp/WEB-INF/web.xml` | ✅ Done | Session config, error pages |
| `pom.xml` | ✅ Done | You own this — nobody adds a dependency without asking you |
| `security/RoleFilter.java` | 🔴 **STUB** | TODO 1–2 |

**Report sections you own:** 3. Problem Statement, 4. Project Objectives,
6.3 Sequence Diagram.

---

## Task 1 — `RoleFilter` ⭐ START HERE (1 hour)

**File:** `src/main/java/my/edu/uptm/pharmatrack/security/RoleFilter.java`

Right now the filter passes everything through, which means it does nothing.
Leaving it that way on submission day costs marks in the Security band.

**The distinction to get right, and to name in the report:**

- `AuthFilter` does **authentication** — *who are you?*
- `RoleFilter` does **authorisation** — *are you allowed to be here?*

These are constantly confused, and using them correctly reads well.

- [ ] **TODO 1** — implement the check:

```java
HttpServletRequest  req = (HttpServletRequest)  request;
HttpServletResponse res = (HttpServletResponse) response;

HttpSession session = req.getSession(false);
User user = (session == null) ? null
          : (User) session.getAttribute(AuthFilter.SESSION_USER);

if (user != null && user.isAdmin()) {
    chain.doFilter(request, response);
} else {
    res.sendRedirect(req.getContextPath() + "/dashboard.jsp?error=forbidden");
}
```

  Note: redirect to the **dashboard**, not the login page. They *are* logged in
  — they are just not permitted, and a login prompt would be confusing.

- [ ] **TODO 2** — widen `urlPatterns` to cover the admin-only URLs.
      **Tell Amir first** — he owns the medicine screens and needs to know they
      are about to become admin-only.

**Demo this in the presentation.** Log in as `cashier`, then type the admin URL
straight into the address bar. It refuses. That is 30 seconds that proves your
security is real and not just hidden menu links — which is exactly the
difference between the Good and Excellent bands.

---

## Task 2 — Be able to defend the security design (1 hour)

These are the questions that come up. Have the answers ready, and put them in
your report section:

- [ ] **"Is the password encrypted?"**
      *No — it is hashed, which is stronger for this purpose. Encryption is
      reversible: anyone with the key recovers every password. A hash is one-way,
      so even we cannot read them back. We use BCrypt with a per-password random
      salt and a cost factor of 10.*

- [ ] **"Why not MD5 or SHA-256?"**
      *Both are designed to be fast, which is the opposite of what you want here
      — a modern GPU tries billions of MD5 guesses per second against a stolen
      database. BCrypt is deliberately slow, and the cost factor can be raised
      as hardware gets faster.*

- [ ] **"What is the salt for?"**
      *Two users who both choose `password123` get completely different stored
      hashes, so a precomputed rainbow table is useless and cracking one
      password tells the attacker nothing about the other.*

- [ ] **"Why does login give the same error for a wrong username and a wrong
      password?"**
      *Username enumeration. "No such user" would let an attacker discover which
      accounts exist before they start guessing passwords.*

- [ ] **"What is session fixation and how do you stop it?"**
      *An attacker plants a known session ID before you log in, then reuses it
      afterwards. `LoginServlet` invalidates the old session and creates a fresh
      one on successful login, so the planted ID is dead.*

- [ ] **"How do you prevent SQL injection?"**
      *Every query in the project uses `PreparedStatement` with `?` placeholders.
      No SQL string anywhere is built by concatenating user input.*

- [ ] **"How do you prevent XSS?"**
      *All output in the JSPs goes through JSTL's `<c:out>`, which HTML-escapes.
      A medicine saved as `<script>alert(1)</script>` is displayed as text, not
      executed. Worth demonstrating live — save one and show it.*

---

## Task 3 — Sequence Diagram ⭐ (2 hours)

**Report section 6.3. Part of the 20-mark diagram criterion.**

Starting point: [`../diagrams/sequence-login.md`](../diagrams/sequence-login.md)

Draw **two** sequence diagrams — the brief's examples show one, but the login
flow alone is thin, and the second one is where the architecture shows:

**(a) Login / authentication**

```
Actor      login.jsp    LoginServlet    UserDAO    DBConnection    MySQL    PasswordUtil
  │            │              │            │            │            │           │
  │─ credentials ─────────────>│            │            │            │           │
  │            │              │─ findByUsername ────────>│            │           │
  │            │              │            │─ getConnection ─────────>│           │
  │            │              │            │─ SELECT ... WHERE username = ? ─────>│
  │            │              │            │<─ ResultSet ─────────────────────────│
  │            │              │<─ User ────│            │            │           │
  │            │              │─ verify(plain, hash) ───────────────────────────>│
  │            │              │<─ true ─────────────────────────────────────────│
  │            │              │─ session.setAttribute(user)                       │
  │<─ redirect to dashboard ──│                                                   │
```

**(b) Process Sale** — the better diagram, because it shows all four layers and
a transaction: `Cashier → SaleServlet → SalesCalculator → SaleDAO → MedicineDAO → MySQL`

- [ ] Use proper UML: solid arrows for calls, **dashed** arrows for returns
- [ ] Draw **activation bars** (the thin rectangles on each lifeline)
- [ ] Show the **alt fragment** for the failure path — wrong password in (a),
      insufficient stock in (b). A sequence diagram with only the happy path
      lands in the "misses key details" band.

---

## Task 4 — Report sections 3 and 4 (1–2 hours)

**Section 3 — Problem Statement.** A draft exists in the group's Word document.
The Excellent band asks for "clear, well-justified problem statement with strong
relevance". What lifts it: each pain point tied to a **consequence**, and where
possible a citation.

> Weak: *"Manual stock tracking is inefficient."*
> Strong: *"Manual, spreadsheet-based stock tracking gives no visibility of
> reorder thresholds, so shortages are only discovered when a customer asks for
> a medicine that is out of stock — a direct loss of sale and of confidence."*

**Section 4 — Project Objectives.** Five objectives, drafted already. Make sure
each one maps to a rubric requirement and starts with an infinitive ("To design…",
"To implement…"). The marking band checks they are "aligned with solving the
problem statement", so each objective should visibly answer one of your pain
points.

---

## Task 5 — Before submission ⚠️

- [ ] **Remove the demo credentials block from `login.jsp`.** Search for
      `demo-box`. Leaving test passwords printed on the login page of the
      submitted version is an easy mark to lose under Security.
- [ ] Confirm `db.properties` has the placeholder password, not anyone's real one
- [ ] Confirm no stack traces reach the browser — the error pages in
      `WEB-INF/error/` handle that
- [ ] Check no `System.out.println` debugging is left in the servlets

---

## Task 6 — Architecture is yours to explain

You own `pom.xml` and `web.xml`, so you are the person who answers structural
questions. Two you should have ready:

- **"Why annotations instead of declaring servlets in web.xml?"**
  *`@WebServlet` keeps each component's configuration next to its code. `web.xml`
  is kept for genuinely application-wide settings only — session timeout, error
  pages. Declaring a servlet in both is a common source of deployment errors.*

- **"Why `DriverManager` and not a connection pool?"**
  *A pool is what production would use. For a coursework system with a handful of
  concurrent users it adds setup complexity on four different laptops for no
  visible benefit.* — **Put this in the report's limitations section.** Naming a
  known trade-off reads as engineering judgement; not mentioning it reads as not
  having noticed.

---

## Your reflection (report section 9)

150–250 words. Worth writing about:

- Learning the difference between encryption and hashing, and why it matters
- Why a Filter beats a login check inside every servlet
- Authentication vs authorisation — and which one you nearly forgot
- The trade-off between security and demo convenience (those test credentials)
