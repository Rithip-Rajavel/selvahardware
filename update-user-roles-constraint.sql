-- ============================================================
-- Update user role check constraint to include new roles
-- ============================================================

-- First, drop the existing check constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Then add the new check constraint with all allowed roles
ALTER TABLE users 
ADD CONSTRAINT users_role_check 
CHECK (role IN ('admin', 'staff', 'owner', 'manager', 'accountant', 'employee', 'superadmin'));

-- ============================================================
-- Verify the constraint was added correctly
-- ============================================================

-- You can verify the constraint with:
-- SELECT conname, consrc FROM pg_constraint WHERE conrelid = 'users'::regclass AND contype = 'c';
