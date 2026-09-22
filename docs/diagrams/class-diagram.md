# Class Diagram — report section 6.4 · Owner: **Ramzi**

Draw the **four layers as separate groupings**. The layering is the point of
this diagram — it is the visual evidence for the *Java EE Architecture & Design*
criterion.

---

## Layer 1 — model (POJOs)

```
┌─────────────────────────────┐   ┌─────────────────────────────┐
│ User                        │   │ Medicine                    │
├─────────────────────────────┤   ├─────────────────────────────┤
│ - userId : int              │   │ - medicineId : int          │
│ - username : String         │   │ - name : String             │
│ - passwordHash : String     │   │ - category : String         │
│ - fullName : String         │   │ - price : BigDecimal        │
│ - role : String             │   │ - quantityInStock : int     │
│ - active : boolean          │   │ - reorderLevel : int        │
├─────────────────────────────┤   │ - expiryDate : Date         │
│ + isAdmin() : boolean       │   ├─────────────────────────────┤
│ + getters/setters           │   │ + isLowStock() : boolean    │
└─────────────────────────────┘   │ + getters/setters           │
                                  └─────────────────────────────┘

┌─────────────────────────────┐   ┌─────────────────────────────┐
│ SaleItem                    │   │ Sale                        │
├─────────────────────────────┤   ├─────────────────────────────┤
│ - saleItemId : int          │   │ - saleId : int              │
│ - saleId : int              │   │ - saleDate : Timestamp      │
│ - medicineId : int          │   │ - userId : int              │
│ - quantity : int            │   │ - totalAmount : BigDecimal  │
│ - unitPrice : BigDecimal    │   │ - items : List<SaleItem>    │
├─────────────────────────────┤   ├─────────────────────────────┤
│ + getSubtotal() : BigDecimal│◆──│ + calculateTotal()          │
└─────────────────────────────┘   │ + addItem(SaleItem)         │
                                  │ + getTotalUnits() : int     │
       ◆ = composition            └─────────────────────────────┘
       SaleItems cannot exist
       without their Sale
```

> **Four model classes.** `Supplier` was removed from scope.
> `Medicine.isLowStock()` compares `quantityInStock` against `reorderLevel` —
> it drives the Low/OK badge on the medicine list and is the one calculation
> that is already working.

## Layer 2 — dao ⭐ the important part

**Show `GenericDAO<T>` as an interface with dashed realisation arrows.**

> Suppliers and `InventoryService` are **out of scope** — do not draw them. This is
the visible evidence for the brief's *"implementing classes and interfaces"*
requirement. Do not leave it out.

```
          ┌──────────────────────────────────┐
          │      «interface»                 │
          │      GenericDAO<T>               │
          ├──────────────────────────────────┤
          │ + insert(T) : int                │
          │ + findAll() : List<T>            │
          │ + findById(int) : T              │
          │ + update(T) : boolean            │
          │ + delete(int) : boolean          │
          └────────────────△─────────────────┘
                           ╎  (dashed = realisation)
        ┌──────────────────┴──────────────────┐
        ╎                                     ╎
┌───────────────┐                     ┌───────────────┐
│ MedicineDAO   │  │ UserDAO       │
├───────────────┤  ├───────────────┤  ├───────────────┤
│ + search()    │                     │ + findBy      │
│ + deductStock │                     │   Username()  │
│   (Connection,│                     │ + update      │
│    int, int)  │                     │   Password()  │
└───────────────┘                     └───────────────┘

                   ┌───────────────┐
                   │ SaleDAO       │   ┌──────────────────┐
                   │ + findByDate  │   │ DBConnection     │
                   │   Range()     │   ├──────────────────┤
                   │ + getDaily    │   │ + getConnection()│
                   │   Summary()   │   │ + testConnection │
                   └───────────────┘   └──────────────────┘
                                        «utility» — all DAOs depend on it
```

> `SaleDAO` does not implement `GenericDAO` because a sale is written as a
> header plus its lines in one transaction, which does not fit the single-entity
> `insert(T)` signature. **Worth one sentence in the report** — knowing when a
> pattern does not apply reads better than forcing it.

---

## Layer 3 — service (business logic)

```
┌────────────────────────────────┐  ┌────────────────────────────────┐
│ «utility» SalesCalculator      │
├────────────────────────────────┤
│ + calculateSubtotal(List)      │
│ + applyDiscount(BigDecimal,..) │
│ + calculateTax(BigDecimal)     │
│ + calculateChange(..)          │
│ + calculateAverageSale(List)   │
└────────────────────────────────┘
```

---

## Layer 4 — controller + security

```
                    HttpServlet
                         △
                         │ (hollow triangle = inheritance)
   ┌──────────┬──────────┼──────────┬──────────┬──────────┐
LoginServlet  Logout  Medicine    Sale      Search/Report
   │                      │
   │ uses                 │ uses
   ▼                      ▼
 UserDAO              MedicineDAO

           ┌──────────────────────────────┐
           │      «interface» Filter      │
           └──────────────△───────────────┘
                     ╎         ╎
              AuthFilter    RoleFilter
              (who are you?) (are you allowed?)

           ┌──────────────────────────────┐
           │  «utility» PasswordUtil      │
           │  + hash(String) : String     │
           │  + verify(String,String)     │
           └──────────────────────────────┘
```

---

## Notation checklist

- [ ] `-` private, `+` public on every attribute and method
- [ ] Types on everything: `- price : BigDecimal`
- [ ] **Dashed** arrow with hollow triangle = interface realisation
- [ ] **Solid** arrow with hollow triangle = class inheritance
- [ ] Filled diamond ◆ = composition (Sale ◆── SaleItem)
- [ ] Dashed open arrow = dependency (Servlet ---> DAO)
- [ ] `«interface»` and `«utility»` stereotypes where they apply
