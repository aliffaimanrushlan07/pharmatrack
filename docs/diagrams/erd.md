# ERD — report section 6.5 · Owner: **Ramzi**

Content for the diagram. Draw it properly in draw.io, or reverse-engineer it
from the live database in MySQL Workbench (**Database → Reverse Engineer**) —
that route takes about 30 seconds and guarantees it matches the code.

---

## Entities and attributes

**Exactly as implemented in `database/01_schema.sql`.** Use these names — the
draft report has older ones.

### users
| Attribute | Type | Key |
|-----------|------|-----|
| user_id | INT AUTO_INCREMENT | **PK** |
| username | VARCHAR(50) | UNIQUE |
| password_hash | VARCHAR(255) | |
| full_name | VARCHAR(100) | |
| role | VARCHAR(20) | CHECK IN ('ADMIN','CASHIER') |
| is_active | TINYINT(1) | |
| created_at | DATETIME | |

### suppliers
| Attribute | Type | Key |
|-----------|------|-----|
| supplier_id | INT AUTO_INCREMENT | **PK** |
| name | VARCHAR(100) | |
| contact_person | VARCHAR(100) | |
| phone | VARCHAR(30) | |
| email | VARCHAR(100) | |
| address | VARCHAR(255) | |

### medicines
| Attribute | Type | Key |
|-----------|------|-----|
| medicine_id | INT AUTO_INCREMENT | **PK** |
| name | VARCHAR(100) | indexed |
| category | VARCHAR(50) | |
| price | DECIMAL(10,2) | |
| quantity_in_stock | INT | |
| reorder_level | INT | |
| expiry_date | DATE | nullable |
| supplier_id | INT | **FK** → suppliers |

### sales
| Attribute | Type | Key |
|-----------|------|-----|
| sale_id | INT AUTO_INCREMENT | **PK** |
| sale_date | DATETIME | indexed |
| user_id | INT | **FK** → users |
| total_amount | DECIMAL(10,2) | |

### sale_items
| Attribute | Type | Key |
|-----------|------|-----|
| sale_item_id | INT AUTO_INCREMENT | **PK** |
| sale_id | INT | **FK** → sales |
| medicine_id | INT | **FK** → medicines |
| quantity | INT | |
| unit_price | DECIMAL(10,2) | |
| subtotal | DECIMAL(10,2) | GENERATED = quantity × unit_price |

---

## Relationships (crow's-foot notation)

```
suppliers  ──1────────<  medicines      one supplier supplies many medicines
users      ──1────────<  sales          one cashier records many sales
sales      ──1────────<  sale_items     one receipt has many lines
medicines  ──1────────<  sale_items     one medicine appears on many lines

medicines  >──────────<  sales          many-to-many, resolved via sale_items
```

Referential actions to label on the diagram:

| Relationship | ON DELETE | Why |
|--------------|-----------|-----|
| medicines → suppliers | SET NULL | Deleting a supplier should not delete its medicines |
| sales → users | RESTRICT | A cashier with sales history cannot be deleted |
| sale_items → sales | CASCADE | Deleting a receipt removes its lines |
| sale_items → medicines | RESTRICT | A medicine on a past receipt cannot be deleted |

---

## Worth saying in the text

- **Why five tables when the brief required two.** `sale_items` is a junction
  table resolving the many-to-many between sales and medicines — the textbook
  case, and the reason a sale can contain several different medicines.
- **Why `unit_price` is duplicated onto `sale_items`.** It is copied at the
  moment of sale and frozen. If the shop raises a price next month, last month's
  receipt must still show last month's price. Joining to the current price would
  silently rewrite history.
- **Third normal form.** Apart from that deliberate, justified denormalisation,
  the schema is in 3NF. Saying so — and naming the one exception and why — reads
  much better than claiming perfect normalisation.
