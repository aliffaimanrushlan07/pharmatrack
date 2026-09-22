-- ===========================================================================
--  PharmaTrack - FILE: 03_sample_queries.sql
--  OWNER : RAMZI (wrote them)  /  USED BY : everyone
-- ===========================================================================
--  These are the queries the Java DAO classes actually run, written out here
--  in plain SQL so you can TEST THEM IN MySQL WORKBENCH FIRST.
--
--  Golden rule for this project: if a query does not work in Workbench,
--  it will not work in Java either. Debug it here, then paste it into
--  the DAO with ? placeholders.
--
--  This file is also useful evidence for the report's Code Listing section.
-- ===========================================================================

USE pharmatrack;


-- ===========================================================================
--  SECTION A - AUTHENTICATION  (Aliff)
-- ===========================================================================

-- A1. Fetch a user by username for login. The password is verified in JAVA
--     with BCrypt.checkpw() - never compare passwords inside SQL.
SELECT user_id, username, password_hash, full_name, role, is_active
FROM   users
WHERE  username = 'admin' AND is_active = 1;


-- ===========================================================================
--  SECTION B - MEDICINE CRUD  (Amir - pattern already implemented in Java)
-- ===========================================================================

-- B1. RETRIEVE all, newest first.
SELECT m.*
FROM   medicines m
ORDER BY m.medicine_id DESC;

-- B2. RETRIEVE one (for the edit form).
SELECT * FROM medicines WHERE medicine_id = 1;

-- B3. CREATE.
INSERT INTO medicines (name, category, price, quantity_in_stock, reorder_level, expiry_date)
VALUES ('Test Medicine', 'Test', 9.99, 10, 5, '2028-01-01');

-- B4. UPDATE.
UPDATE medicines
SET    name = 'Test Medicine (edited)', price = 11.99
WHERE  medicine_id = LAST_INSERT_ID();

-- B5. DELETE.
DELETE FROM medicines WHERE name = 'Test Medicine (edited)';


-- ===========================================================================
--  SECTION C - SEARCH  (Yasierul)
-- ===========================================================================

-- C1. Search medicines by name OR category. In Java the '%keyword%' string
--     is built in the DAO and bound with setString(1, "%" + keyword + "%").
--     NEVER concatenate the keyword straight into the SQL string -
--     that is an SQL injection hole and the rubric penalises it.
SELECT m.*
FROM   medicines m
WHERE  m.name LIKE '%para%' OR m.category LIKE '%para%'
ORDER BY m.name;

-- C2. Search sales by date range.
SELECT sa.sale_id, sa.sale_date, u.full_name AS cashier, sa.total_amount
FROM   sales sa
JOIN   users u ON u.user_id = sa.user_id
WHERE  sa.sale_date BETWEEN '2026-09-01' AND '2026-09-30 23:59:59'
ORDER BY sa.sale_date DESC;


-- ===========================================================================
--  SECTION D - BUSINESS LOGIC / CALCULATION  (Yasierul)
-- ===========================================================================

-- D1. Recalculate one sale's total from its line items.
--     This is the calculation the rubric asks for. The Java service does the
--     same arithmetic in SalesCalculator so the logic lives in the app layer.
SELECT sale_id, SUM(subtotal) AS calculated_total
FROM   sale_items
WHERE  sale_id = 2
GROUP BY sale_id;

-- D2. Push that calculated total back into the sales row.
UPDATE sales sa
SET    sa.total_amount = (SELECT SUM(si.subtotal) FROM sale_items si WHERE si.sale_id = sa.sale_id)
WHERE  sa.sale_id = 2;

-- D3. Deduct stock after a sale (runs inside the same transaction as the insert).
UPDATE medicines
SET    quantity_in_stock = quantity_in_stock - 2
WHERE  medicine_id = 1 AND quantity_in_stock >= 2;

-- D4. Daily sales summary - total revenue and average basket size.
SELECT DATE(sale_date)    AS sale_day,
       COUNT(*)           AS transactions,
       SUM(total_amount)  AS revenue,
       ROUND(AVG(total_amount), 2) AS average_sale
FROM   sales
GROUP BY DATE(sale_date)
ORDER BY sale_day DESC;

-- D5. Best-selling medicines.
SELECT m.name,
       SUM(si.quantity) AS units_sold,
       SUM(si.subtotal) AS revenue
FROM   sale_items si
JOIN   medicines m ON m.medicine_id = si.medicine_id
GROUP BY m.medicine_id, m.name
ORDER BY units_sold DESC
LIMIT 10;


-- ===========================================================================
--  SECTION E - EXPIRY REPORT  (Yasierul)
-- ===========================================================================

-- E2. Medicines expiring within the next 90 days.
SELECT medicine_id, name, expiry_date,
       DATEDIFF(expiry_date, CURDATE()) AS days_remaining
FROM   medicines
WHERE  expiry_date IS NOT NULL
  AND  expiry_date <= DATE_ADD(CURDATE(), INTERVAL 90 DAY)
ORDER BY expiry_date ASC;
