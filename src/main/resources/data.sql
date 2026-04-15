-- ============================================================
-- Fix column types if they were incorrectly created as bytea
-- by Hibernate 6. Safe to run on startup (casts existing data).
-- Use standard SQL functions instead of PostgreSQL-specific lower(bytea)

-- Update user role check constraint to include new roles
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users 
ADD CONSTRAINT users_role_check 
CHECK (role IN ('admin', 'staff', 'owner', 'manager', 'accountant', 'employee', 'superadmin'));

-- products table
-- products table
-- ALTER TABLE products
--     ALTER COLUMN name    TYPE varchar(255) USING convert_from(name, 'UTF8'),
--     ALTER COLUMN code    TYPE varchar(50)  USING convert_from(code, 'UTF8'),
--     ALTER COLUMN barcode TYPE varchar(50)  USING convert_from(barcode, 'UTF8');

-- -- customers table
-- ALTER TABLE customers
--     ALTER COLUMN name TYPE varchar(255) USING convert_from(name, 'UTF8');

-- -- invoices table
-- ALTER TABLE invoices
--     ALTER COLUMN invoice_number TYPE varchar(50)  USING convert_from(invoice_number, 'UTF8'),
--     ALTER COLUMN customer_name  TYPE varchar(255) USING convert_from(customer_name, 'UTF8');

-- -- users table  
-- ALTER TABLE users
--     ALTER COLUMN name          TYPE varchar(255) USING convert_from(name, 'UTF8'),
--     ALTER COLUMN email         TYPE varchar(255) USING convert_from(email, 'UTF8'),
--     ALTER COLUMN password_hash TYPE varchar(255) USING convert_from(password_hash, 'UTF8');

-- -- Commissions table - Convert string UUIDs to actual UUID type
-- ALTER TABLE commissions
--     ALTER COLUMN contractor_id TYPE uuid USING contractor_id::uuid,
--     ALTER COLUMN invoice_id TYPE uuid USING invoice_id::uuid;

-- -- PriceMapping table - Convert string UUIDs to actual UUID type
-- ALTER TABLE price_mapping
--     ALTER COLUMN customer_id TYPE uuid USING customer_id::uuid,
--     ALTER COLUMN product_id TYPE uuid USING product_id::uuid;

-- ============================================================

-- Default admin user (password: admin123)
INSERT INTO users (id, name, email, password_hash, phone, role, is_active, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    'Administrator',
    'admin@shivahardwares.com',
    '$2a$12$ZsWJ45vrOTUjDB7cEHtoyO9E2h58nFFu2TS6XNaE1di60viaOeIh2',
    '9999999999',
    'admin',
    true,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- Walk-in customer (required - cannot be deleted)
INSERT INTO customers (id, name, phone, email, address, type, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440002',
    'Walk-in Customer',
    NULL,
    NULL,
    NULL,
    'customer',
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- ===========================================================
