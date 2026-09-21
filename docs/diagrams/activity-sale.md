# Activity Diagram — report section 6.2 · Owner: **Amir**

Diagram the **Process Sale** flow. It is the one with real decision points, and
it is the flow the live demo will follow.

---

## The flow

```
                        ● start
                        │
                        ▼
            ┌───────────────────────┐
            │  Cashier logs in      │
            └───────────┬───────────┘
                        ▼
            ┌───────────────────────┐
            │  Open New Sale screen │
            └───────────┬───────────┘
                        ▼
            ┌───────────────────────┐
     ┌─────>│  Search for medicine  │
     │      └───────────┬───────────┘
     │                  ▼
     │              ╱╲ stock
     │             ╱  ╲ available?
     │            ╱    ╲
     │       no ╱       ╲ yes
     │         ▼         ▼
     │  ┌────────────┐  ┌───────────────────────┐
     │  │ Show "out  │  │ Enter quantity        │
     │  │ of stock"  │  └───────────┬───────────┘
     │  └─────┬──────┘              ▼
     │        │          ┌───────────────────────┐
     └────────┘          │ Add line to basket    │
     │                   └───────────┬───────────┘
     │                               ▼
     │                   ┌───────────────────────┐
     │                   │ Recalculate total     │
     │                   └───────────┬───────────┘
     │                               ▼
     │                           ╱╲ add more
     └──────────────── yes ─────╱  ╲ items?
                                ╲  ╱
                                 ╲╱
                                  │ no
                                  ▼
                      ┌───────────────────────┐
                      │ Display grand total   │
                      └───────────┬───────────┘
                                  ▼
                              ╱╲ confirm
                             ╱  ╲ sale?
                        no ╱      ╲ yes
                          ▼        ▼
                 ┌──────────┐   ╔═══════════════════════════╗
                 │ Cancel   │   ║ TRANSACTION               ║
                 │ basket   │   ║  1. INSERT sales          ║
                 └────┬─────┘   ║  2. INSERT sale_items     ║
                      │         ║  3. UPDATE medicines      ║
                      │         ║     (deduct stock)        ║
                      │         ║  4. UPDATE total_amount   ║
                      │         ╚═════════╤═════════════════╝
                      │                   ▼
                      │               ╱╲ all steps
                      │              ╱  ╲ succeeded?
                      │         no ╱      ╲ yes
                      │           ▼        ▼
                      │   ┌────────────┐  ┌──────────────┐
                      │   │ ROLLBACK   │  │ COMMIT       │
                      │   │ show error │  │ show receipt │
                      │   └─────┬──────┘  └──────┬───────┘
                      │         │                │
                      └─────────┴────────────────┘
                                  ▼
                                  ◉ end
```

---

## Swimlanes (recommended)

Three vertical lanes — **Cashier | System | Database** — and put each activity
in the lane that performs it. It costs ten minutes and visibly demonstrates the
layered architecture, which is exactly what the Excellent band is looking for
when it asks for diagrams that "accurately reflect the system architecture".

---

## Checklist

- [ ] Filled circle ● for start, ringed circle ◉ for end
- [ ] Rounded rectangles for activities
- [ ] **Diamonds** for decisions, with **both** branches labelled (yes/no)
- [ ] The rollback branch included — a diagram with only the happy path reads as
      drawn from a description rather than from a working system
- [ ] Merge points where branches rejoin
- [ ] Consider swimlanes
