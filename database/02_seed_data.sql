-- ===========================================================================
--  PharmaTrack - Pharmacy Inventory and Sales Management System
--  FILE   : 02_seed_data.sql   (sample data so the system is demo-ready)
--  OWNER  : RAMZI  -  Database Design & Data Access Layer
-- ===========================================================================
--  RUN THIS AFTER 01_schema.sql.
--  Re-running this file is safe - it clears the tables first.
-- ===========================================================================

USE pharmatrack;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE sale_items;
TRUNCATE TABLE sales;
TRUNCATE TABLE medicines;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;


-- ---------------------------------------------------------------------------
--  USERS
-- ---------------------------------------------------------------------------
--  !! IMPORTANT !!  These password_hash values are real BCrypt hashes.
--  You CANNOT read the password out of them - that is the point.
--
--     username : admin     password : admin123      role : ADMIN
--     username : cashier   password : cashier123    role : CASHIER
--
--  To add your own account, do NOT type a plain password into this file.
--  Run the helper instead (see docs/tasks/aliff-auth-security.md):
--      System.out.println(PasswordUtil.hash("yourPassword"));
--  then paste the 60-character result below.
-- ---------------------------------------------------------------------------
INSERT INTO users (username, password_hash, full_name, role) VALUES
('admin',   '$2a$10$RGUv.y5FrqxoBgSBXdtSyeFKMz8HGYZ57jMljKkWEcVCABa/v9i32', 'System Administrator', 'ADMIN'),
('cashier', '$2a$10$kfnlsUHTeas/NAP.cB57HOKf3YVLdKzt96N5a9BRHcHIPvR6c/0MW', 'Front Counter Staff',  'CASHIER');


-- ---------------------------------------------------------------------------
--  MEDICINES
--  A few rows are deliberately BELOW their reorder_level so the low-stock
--  report has something to show during the demo (see the last 3 rows).
--  One row expires soon, so the expiry logic is also demonstrable.
-- ---------------------------------------------------------------------------
INSERT INTO medicines (name, category, price, quantity_in_stock, reorder_level, expiry_date) VALUES
('Paracetamol 500mg (100 tabs)', 'Analgesic',       12.50, 240,  50, '2027-11-30'),
('Ibuprofen 400mg (60 tabs)',    'Analgesic',       18.90, 130,  40, '2027-08-15'),
('Amoxicillin 500mg (30 caps)',  'Antibiotic',      24.00,  85,  30, '2027-05-20'),
('Cetirizine 10mg (30 tabs)',    'Antihistamine',   14.20, 160,  40, '2028-01-10'),
('Loratadine 10mg (30 tabs)',    'Antihistamine',   15.80, 110,  30, '2027-12-01'),
('Omeprazole 20mg (30 caps)',    'Antacid',         28.50,  70,  25, '2027-09-05'),
('Metformin 500mg (100 tabs)',   'Antidiabetic',    22.00,  95,  30, '2028-03-18'),
('Vitamin C 1000mg (60 tabs)',   'Supplement',      35.00, 200,  50, '2028-06-30'),
('Cough Syrup 120ml',            'Respiratory',     16.75,  64,  20, '2027-07-22'),
('ORS Sachet (10 pcs)',          'Rehydration',      9.90, 180,  40, '2028-02-14'),
('Hand Sanitiser 500ml',         'Hygiene',         13.50,  12,  30, '2028-09-01'),  -- LOW STOCK
('Surgical Face Mask (50 pcs)',  'Hygiene',         19.90,   8,  25, '2029-01-01'),  -- LOW STOCK
('Insulin Pen Needles (100)',    'Diabetic Care',   45.00,   5,  15, '2027-04-30');  -- LOW STOCK + EXPIRING


-- ---------------------------------------------------------------------------
--  SAMPLE SALES
--  Two completed transactions so the Sales Report page is not empty on day 1.
--  Sale 1's total_amount matches its line items exactly.
--  Sale 2's is 0.20 too low ON PURPOSE, so that when Yasierul's recalculation
--  logic runs (queries E1/E2 in 03_sample_queries.sql) you can SEE it correct
--  a wrong total rather than just reproduce a right one.
-- ---------------------------------------------------------------------------
INSERT INTO sales (sale_id, sale_date, user_id, total_amount) VALUES
(1, '2026-09-15 10:24:00', 2,  58.10),
(2, '2026-09-16 15:41:00', 2, 104.30);

INSERT INTO sale_items (sale_id, medicine_id, quantity, unit_price) VALUES
-- Sale 1:  (2 x 12.50) + (1 x 18.90) + (1 x 14.20)  =  58.10   <- correct
(1,  1, 2, 12.50),
(1,  2, 1, 18.90),
(1,  4, 1, 14.20),
-- Sale 2:  (1 x 35.00) + (2 x 28.50) + (1 x 12.50)  =  104.50
-- but stored as 104.30 on purpose, so Yasierul's recalculation (query E1/E2)
-- visibly corrects it. Sale 1 is correct; sale 2 is the broken one.
(2,  8, 1, 35.00),
(2,  6, 2, 28.50),
(2,  1, 1, 12.50);


-- ---------------------------------------------------------------------------
--  Confirmation
-- ---------------------------------------------------------------------------
SELECT 'Seed data inserted.' AS status;
SELECT (SELECT COUNT(*) FROM users)      AS users,
       (SELECT COUNT(*) FROM medicines)  AS medicines,
       (SELECT COUNT(*) FROM sales)      AS sales,
       (SELECT COUNT(*) FROM sale_items) AS sale_items;
