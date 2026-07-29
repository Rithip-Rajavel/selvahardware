-- ============================================================
-- Fix column types if they were incorrectly created as bytea
-- by Hibernate 6. Safe to run on startup (casts existing data).
-- Use standard SQL functions instead of PostgreSQL-specific lower(bytea)

-- Update user role check constraint to include new roles
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users
ADD CONSTRAINT users_role_check
CHECK (role IN ('admin', 'staff', 'owner', 'manager', 'accountant', 'employee', 'superadmin'));

-- Add new columns to customers table for city, isActive, lastPaymentDate, lastInvoiceDate
ALTER TABLE customers ADD COLUMN IF NOT EXISTS city VARCHAR(100);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS last_payment_date TIMESTAMP;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS last_invoice_date TIMESTAMP;

-- Add nextPayDate column to payments table
ALTER TABLE payments ADD COLUMN IF NOT EXISTS next_pay_date TIMESTAMP;

-- Add nextPayDate column to invoices table
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS next_pay_date TIMESTAMP;

-- Create invoice_audit_logs table for complete audit trail
CREATE TABLE IF NOT EXISTS invoice_audit_logs (
    id VARCHAR(50) PRIMARY KEY,
    invoice_id VARCHAR(50) NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    customer_id VARCHAR(50),
    customer_name VARCHAR(255),
    action_type VARCHAR(50) NOT NULL,
    action_description TEXT,
    amount DECIMAL(10, 2),
    balance_before DECIMAL(10, 2),
    balance_after DECIMAL(10, 2),
    amount_paid_before DECIMAL(10, 2),
    amount_paid_after DECIMAL(10, 2),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(50),
    return_id VARCHAR(50),
    notes TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL
);

-- Create invoice_returns table for return/written bill functionality
CREATE TABLE IF NOT EXISTS invoice_returns (
    id VARCHAR(50) PRIMARY KEY,
    return_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_id VARCHAR(50) NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    total_return_amount DECIMAL(10, 2) NOT NULL,
    total_return_quantity DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    refund_amount DECIMAL(10, 2),
    refund_method VARCHAR(50),
    reason TEXT,
    processed_by VARCHAR(100),
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create invoice_return_items table
CREATE TABLE IF NOT EXISTS invoice_return_items (
    id VARCHAR(50) PRIMARY KEY,
    return_id VARCHAR(50) NOT NULL,
    invoice_item_id VARCHAR(50),
    product_id VARCHAR(50) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_code VARCHAR(50),
    quantity DECIMAL(10, 2) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    gst_percentage DECIMAL(5, 2),
    gst_amount DECIMAL(10, 2),
    discount DECIMAL(10, 2) DEFAULT 0,
    subtotal DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    reason TEXT,
    original_invoice_quantity DECIMAL(10, 2)
);

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
