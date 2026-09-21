# How we work on this project

Four people, one codebase, four weeks. These conventions exist so that nobody
overwrites anybody, and so the commit history is clean evidence for the
Individual Contribution Matrix.

---

## The three rules

1. **Never commit directly to `main`.** Branch, push, open a Pull Request.
2. **Only edit files you own.** See [`docs/MODULE-OWNERSHIP.md`](docs/MODULE-OWNERSHIP.md).
   Need a change elsewhere? Message the owner or open an issue.
3. **Commit under your own name, often.** The lecturer reads the history.

---

## Set your git identity (once, per machine)

```bash
git config --global user.name  "Your Full Name"
git config --global user.email "your.student.email@example.com"
```

Use the **same email as your GitHub account**, otherwise your commits will not
be attributed to you on GitHub and your contribution will look empty.

---

## Everyday workflow

```bash
git checkout main
git pull                                  # always start from the latest

git checkout -b feat/supplier-crud        # your branch

# ... work ...

git add .
git commit -m "feat(supplier): implement SupplierDAO.findAll and mapRow"
git push -u origin feat/supplier-crud
```

Then on GitHub: **Compare & pull request** → describe what you did → ask one
other member to review → merge once approved.

---

## Branch names

```
feat/<what>      a new feature       feat/supplier-crud
fix/<what>       a bug fix           fix/login-redirect-loop
docs/<what>      documentation       docs/user-manual
style/<what>     CSS / layout only   style/dashboard-cards
```

One branch per task. Small branches merge easily; a branch that lives for two
weeks becomes a merge conflict.

---

## Commit messages

```
<type>(<scope>): <what changed, in the imperative>
```

| Type    | For                                   |
|---------|---------------------------------------|
| `feat`  | new functionality                     |
| `fix`   | a bug fix                             |
| `docs`  | documentation only                    |
| `style` | formatting, CSS — no logic change     |
| `refactor` | restructuring with no behaviour change |
| `test`  | tests                                 |

Scope is your module: `medicine`, `supplier`, `sale`, `search`, `auth`, `db`, `ui`, `report`.

Good:

```
feat(supplier): implement SupplierDAO CRUD methods
fix(sale): roll back transaction when stock is insufficient
docs(manual): add screenshots for the login section
style(ui): align action buttons in the medicine table
```

Not good:

```
update            ← what?
fixed bug         ← which one?
asdfgh            ← please no
final FINAL v2    ← that is what git is for
```

---

## Pull requests

Every PR needs **one approval** from another member. Reviewing takes five
minutes and catches things like a forgotten `PreparedStatement` before they
reach `main`.

Before you open one:

- [ ] The project still builds (**Clean and Build** in NetBeans)
- [ ] You can log in and use the feature you changed
- [ ] No `System.out.println` debugging left behind
- [ ] No real password committed in `db.properties`
- [ ] New code is commented to the same standard as the existing files
      (Code Listing is worth 10 marks)

---

## When git conflicts

It will happen, and it is not a disaster.

```bash
git checkout main
git pull
git checkout your-branch
git merge main               # conflicts appear here
```

Open each conflicted file. Git marks the clash:

```
<<<<<<< HEAD
your version
=======
their version
>>>>>>> main
```

Keep what should be kept, delete the `<<<<<<<`, `=======` and `>>>>>>>` lines,
then:

```bash
git add .
git commit -m "merge: resolve conflict with main"
git push
```

If you are unsure which version is right, **ask the other person** rather than
guessing. Silently deleting someone's work is how a feature disappears two days
before the deadline.

---

## Never commit these

Already handled by `.gitignore`, but worth knowing:

- `target/` — build output
- `nbproject/private/` — your local NetBeans settings
- Your real MySQL password in `db.properties`
- `.DS_Store`, `Thumbs.db`

To keep a local password without committing it:

```bash
git update-index --skip-worktree src/main/resources/db.properties
```

---

## Before submission

- [ ] All 8 rubric items green in the README table
- [ ] Demo credentials removed from `login.jsp`
- [ ] `db.properties` has the placeholder password
- [ ] All four members have visible commits in the history
- [ ] Repository is public, or the lecturer has been added as a collaborator
- [ ] The repository URL is in section 10 of the report
- [ ] `main` builds and runs from a fresh clone — **test this on someone else's laptop**
