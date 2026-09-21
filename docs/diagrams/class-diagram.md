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
│ + isAdmin() : boolean       │   │ - supplierId : int          │
│ + getters/setters           │   ├─────────────────────────────┤
└─────────────────────────────┘   │ + isLowStock() : boolean    │
                                  │ + getters/setters           │
┌─────────────────────────────┐   └─────────────────────────────┘
│ Supplier                    │
├─────────────────────────────┤   ┌─────────────────────────────┐
│ - supplierId : int          │   │ Sale                        │
│ - name : String             │   ├─────────────────────────────┤
│ - contactPerson : String    │   │ - saleId : int              │
│ - phone, email, address     │   │ - saleDate : Timestamp      │
└─────────────────────────────┘   │ - userId : int              │
                                  │ - totalAmount : BigDecimal  │
┌─────────────────────────────┐   │ - items : List<SaleItem>    │
│ SaleItem                    │   ├─────────────────────────────┤
├─────────────────────────────┤◆──│ + calculateTotal()          │
│ - saleItemId : int          │   │ + addItem(SaleItem)         │
│ - saleId : int              │   │ + getTotalUnits() : int     │
│ - medicineId : int          │   └─────────────────────────────┘
│ - quantity : int            │
│ - unitPrice : BigDecimal    │      ◆ = composition
├─────────────────────────────┤      SaleItems cannot exist
│ + getSubtotal() : BigDecimal│      without their Sale
└─────────────────────────────┘
```

---

## Layer 2 — dao ⭐ the important part

**Show `GenericDAO<T>` as an interface with dashed realisation arrows.** This is
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
        ┌──────────────────┼──────────────────┐
        ╎                  ╎                  ╎
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ MedicineDAO   │  │ SupplierDAO   │  │ UserDAO       │
├───────────────┤  ├───────────────┤  ├───────────────┤
│ + search()    │  │ + search()    │  │ + findBy      │
│ + findLow     │  └───────────────┘  │   Username()  │
│   Stock()     │                     │ + update      │
│ + deductStock │  ┌───────────────┐  │   Password()  │
│   (Connection,│  │ SaleDAO       │  └───────────────┘
│    int, int)  │  ├───────────────┤
└───────────────┘  │ + insertSale()│   ┌──────────────────┐
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
│ «utility» SalesCalculator      │  │ «utility» InventoryService     │
├────────────────────────────────┤  ├────────────────────────────────┤
│ + calculateSubtotal(List)      │  │ + needsReorder(Medicine)       │
│ + applyDiscount(BigDecimal,..) │  │ + suggestReorderQuantity(..)   │
│ + calculateTax(BigDecimal)     │  │ + isExpiringSoon(Medicine,int) │
│ + calculateChange(..)          │  │ + countLowStock(List)          │
│ + calculateAverageSale(List)   │  └────────────────────────────────┘
└────────────────────────────────┘
```

---

## Layer 4 — controller + security

```
                    HttpServlet
                         △
                         │ (hollow triangle = inheritance)
   ┌──────────┬──────────┼──────────┬──────────┬──────────┐
LoginServlet  Logout  Medicine   Supplier    Sale      Search/Report
   │                      │
   │ uses                 │ uses
   ▼                      ▼
 UserDAO              MedicineDAO, SupplierDAO

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
