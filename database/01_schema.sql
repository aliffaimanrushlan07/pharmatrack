-- ===========================================================================
--  PharmaTrack - Pharmacy Inventory and Sales Management System
--  FILE   : 01_schema.sql   (Data Definition Language - creates the database)
--  OWNER  : RAMZI  -  Database Design & Data Access Layer
--  COURSE : SWC4253 / SWC4593 Enterprise Software Development
-- ===========================================================================
--  HOW TO RUN (MySQL Workbench):
--    1. Open MySQL Workbench and connect to your local MySQL server.
--    2. File > Open SQL Script... > select this file.
--    3. Click the lightning bolt (Execute All) button.
--    4. Then run 02_seed_data.sql the same way.
--    5. Refresh the SCHEMAS panel on the left - you should see 'pharmatrack'.
-- ===========================================================================
--  RUBRIC NOTE: "Relational Tables - use at least two relational tables".
--  We use FOUR tables with proper PK/FK relationships, including a junction
--  table (sale_items) resolving the many-to-many between sales and medicines.
-- ===========================================================================

DROP DATABASE IF EXISTS pharmatrack;
CREATE DATABASE pharmatrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE pharmatrack;


-- ---------------------------------------------------------------------------
--  TABLE 1: users
--  Login accounts. Password is NEVER stored in plain text - the application
--  stores a BCrypt hash (see PasswordUtil.java - Aliff's module).
--  A BCrypt hash is always 60 characters, VARCHAR(255) leaves room to spare.
-- ---------------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'CASHIER',
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_users        PRIMARY KEY (user_id),
    CONSTRAINT uq_users_uname  UNIQUE (username),
    CONSTRAINT chk_users_role  CHECK (role IN ('ADMIN', 'CASHIER'))
) ENGINE=InnoDB;


-- ---------------------------------------------------------------------------
--  TABLE 2: medicines
--  The inventory itself. quantity_in_stock drives the low-stock alert;
--  reorder_level is the threshold each medicine is compared against.
-- ---------------------------------------------------------------------------
CREATE TABLE medicines (
    medicine_id       INT           NOT NULL AUTO_INCREMENT,
    name              VARCHAR(100)  NOT NULL,
    category          VARCHAR(50)            DEFAULT NULL,
    price             DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantity_in_stock INT           NOT NULL DEFAULT 0,
    reorder_level     INT           NOT NULL DEFAULT 10,   -- drives the Low/OK badge
    expiry_date       DATE                   DEFAULT NULL,

    CONSTRAINT pk_medicines PRIMARY KEY (medicine_id),
    CONSTRAINT chk_medicines_price CHECK (price >= 0),
    CONSTRAINT chk_medicines_qty   CHECK (quantity_in_stock >= 0)
) ENGINE=InnoDB;

-- Index on name because the Search module (Yasierul) queries it with LIKE.
CREATE INDEX idx_medicines_name ON medicines (name);


-- ---------------------------------------------------------------------------
--  TABLE 3: sales
--  One row per completed transaction (the "receipt header").
--  total_amount is CALCULATED by the application from the sale_items rows -
--  this is the "business logic / calculation feature" the rubric asks for.
-- ---------------------------------------------------------------------------
CREATE TABLE sales (
    sale_id      INT           NOT NULL AUTO_INCREMENT,
    sale_date    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id      INT           NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    CONSTRAINT pk_sales PRIMARY KEY (sale_id),
    CONSTRAINT fk_sales_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_sales_date ON sales (sale_date);


-- ---------------------------------------------------------------------------
--  TABLE 4: sale_items
--  Junction table resolving the many-to-many between sales and medicines.
--  unit_price is copied in at the time of sale on purpose: if the medicine
--  price changes next month, an old receipt must still show the old price.
--  subtotal is a STORED GENERATED COLUMN - MySQL keeps it correct for us.
-- ---------------------------------------------------------------------------
CREATE TABLE sale_items (
    sale_item_id INT           NOT NULL AUTO_INCREMENT,
    sale_id      INT           NOT NULL,
    medicine_id  INT           NOT NULL,
    quantity     INT           NOT NULL,
    unit_price   DECIMAL(10,2) NOT NULL,
    subtotal     DECIMAL(10,2) AS (quantity * unit_price) STORED,

    CONSTRAINT pk_sale_items PRIMARY KEY (sale_item_id),
    CONSTRAINT fk_sale_items_sale
        FOREIGN KEY (sale_id) REFERENCES sales (sale_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_sale_items_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines (medicine_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_sale_items_qty CHECK (quantity > 0)
) ENGINE=InnoDB;


-- ---------------------------------------------------------------------------
--  Confirmation
-- ---------------------------------------------------------------------------
SELECT 'PharmaTrack schema created successfully.' AS status;
SHOW TABLES;
