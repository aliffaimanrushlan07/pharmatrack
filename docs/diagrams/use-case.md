# Use Case Diagram — report section 6.1 · Owner: **Amir**

---

## Actors

| Actor | Who | Can do |
|-------|-----|--------|
| **Cashier** | Front-counter staff | Sell, search, view stock |
| **Admin** | Pharmacist / manager | Everything the Cashier can, plus manage records |

**Draw the generalisation arrow** from Admin to Cashier (solid line, hollow
triangle pointing at Cashier). It says "an Admin is also a Cashier" in one
stroke, instead of duplicating six use cases. This single arrow is what
separates a thought-through diagram from a list of bubbles.

---

## Use cases

### Cashier (and therefore also Admin)
- Log in
- Log out
- Search Medicine
- View Medicine List
- Process Sale
- View Receipt
- View Low-Stock Report

### Admin only
- Add Medicine
- Update Medicine
- Delete Medicine
- Manage Suppliers
- View Sales Report

---

## Layout

```
                ┌──────────────── PharmaTrack ────────────────┐
                │                                             │
                │      (  Log in  )                           │
                │      (  Log out )                           │
                │                                             │
   Cashier ─────┼──────( Search Medicine )                    │
      △         │      ( View Medicine List )                 │
      │         │      ( Process Sale ) ·····<<include>>····> ( Update Stock )
      │         │      ( View Receipt )                       │
      │         │      ( View Low-Stock Report )              │
      │         │                                             │
      │         │      ( Add Medicine )                       │
    Admin ──────┼──────( Update Medicine )                    │
                │      ( Delete Medicine )                    │
                │      ( Manage Suppliers )                   │
                │      ( View Sales Report )                  │
                │                                             │
                └─────────────────────────────────────────────┘
```

---

## Relationships to show

| Type | Between | Meaning |
|------|---------|---------|
| **Generalisation** | Admin ──▷ Cashier | Admin inherits every Cashier use case |
| **«include»** | Process Sale ···> Update Stock | Always happens as part of it |
| **«include»** | Process Sale ···> Calculate Total | Always happens as part of it |
| **«extend»** | Process Sale <··· Apply Discount | Optional, only sometimes |

`«include»` is a mandatory sub-behaviour; `«extend»` is optional. Getting the
arrow direction right matters — `«include»` points **from** the base case to the
included one, `«extend»` points **towards** the base case. This is the most
commonly reversed thing on a use case diagram.

---

## Checklist

- [ ] Actors are stick figures **outside** the system boundary box
- [ ] Boundary box drawn and labelled "PharmaTrack"
- [ ] Use cases are ovals with verb-phrase names ("Process Sale", not "Sale")
- [ ] Generalisation arrow from Admin to Cashier
- [ ] At least one `«include»`, shown as a dashed arrow with the stereotype
- [ ] Every use case actually exists in the code — do not diagram features
      nobody is building
