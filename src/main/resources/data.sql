-- Default admin user (password: admin123)
INSERT INTO users (id, name, email, password_hash, phone, role, is_active, created_at, updated_at)
VALUES (
    'admin-1',
    'Administrator',
    'admin@shivahardwares.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    '9999999999',
    'admin',
    true,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- Walk-in customer (required - cannot be deleted)
INSERT INTO customers (id, name, phone, email, address, type, created_at, updated_at)
VALUES (
    'walk-in',
    'Walk-in Customer',
    NULL,
    NULL,
    NULL,
    'customer',
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- Default shop settings
INSERT INTO shop_settings (id, name, address, phone, email, gst_number, default_gst_percentage, invoice_prefix, updated_at)
VALUES (
    1,
    'Sri Sai Shiva Hardwares',
    'No 121/12, Tirupattur Main Road',
    '9876543210',
    'info@shivahardwares.com',
    '33AABCS1234C1ZV',
    18.00,
    'INV',
    NOW()
) ON CONFLICT (id) DO NOTHING;
