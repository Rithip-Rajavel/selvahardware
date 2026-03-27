-- Fix shop_settings table for UUID migration
-- Drop existing table with integer column
DROP TABLE IF EXISTS shop_settings;

-- Recreate table with UUID column
CREATE TABLE shop_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    gst_number VARCHAR(50),
    default_gst_percentage DECIMAL(5,2) DEFAULT 18.00,
    invoice_prefix VARCHAR(10) DEFAULT 'INV',
    terms_and_conditions TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default settings
INSERT INTO shop_settings (id, name, address, phone, email, gst_number, default_gst_percentage, invoice_prefix, terms_and_conditions, updated_at)
VALUES (
    gen_random_uuid(),
    'Sri Sai Shiva Hardwares',
    'No 121/12, Tirupattur Main Road',
    '9876543210',
    'info@shivahardwares.com',
    '33AABCS1234C1ZV',
    18.00,
    'INV',
    NULL,
    CURRENT_TIMESTAMP
);
