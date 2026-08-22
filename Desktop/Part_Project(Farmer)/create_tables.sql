-- =============================================
-- GENERAL FARMERS - Full Database Schema + Seed Data
-- Database: farmer_crop
-- =============================================

-- Drop tables if exist (reverse dependency order)
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS inventory_transactions CASCADE;
DROP TABLE IF EXISTS inventory_items CASCADE;
DROP TABLE IF EXISTS harvests CASCADE;
DROP TABLE IF EXISTS expenses CASCADE;

-- =============================================
-- 1. INVENTORY ITEMS
-- =============================================
CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    farm_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('SEED','FERTILIZER','PESTICIDE','TOOL','EQUIPMENT','PACKAGING','OTHER')),
    quantity DOUBLE PRECISION NOT NULL DEFAULT 0,
    unit VARCHAR(50) NOT NULL,
    minimum_stock DOUBLE PRECISION,
    location VARCHAR(255),
    description TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 2. INVENTORY TRANSACTIONS
-- =============================================
CREATE TABLE inventory_transactions (
    id BIGSERIAL PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL REFERENCES inventory_items(id),
    type VARCHAR(50) NOT NULL CHECK (type IN ('STOCK_IN','STOCK_OUT','ADJUSTMENT')),
    quantity DOUBLE PRECISION NOT NULL,
    reason TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 3. EXPENSES
-- =============================================
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    farm_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('SEEDS','FERTILIZER','PESTICIDE','LABOR','EQUIPMENT','TRANSPORTATION','IRRIGATION','OTHER')),
    amount NUMERIC(12,2) NOT NULL,
    description TEXT,
    expense_date DATE NOT NULL,
    receipt_url VARCHAR(500),
    created_by BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. HARVESTS
-- =============================================
CREATE TABLE harvests (
    id BIGSERIAL PRIMARY KEY,
    farm_id BIGINT NOT NULL,
    field_id BIGINT,
    crop_id BIGINT,
    recorded_by BIGINT NOT NULL,
    harvest_date DATE NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(50) NOT NULL,
    quality VARCHAR(20) CHECK (quality IN ('LOW','MEDIUM','GOOD','PREMIUM')),
    estimated_value NUMERIC(12,2),
    actual_revenue NUMERIC(12,2),
    buyer_id BIGINT,
    notes TEXT,
    image_url VARCHAR(500),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 5. PRODUCTS (Marketplace)
-- =============================================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(12,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING','APPROVED','REJECTED','ACTIVE','HIDDEN','SOLD_OUT')),
    seller_id BIGINT NOT NULL,
    farm_id BIGINT,
    image_url VARCHAR(500),
    category VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 6. ORDERS
-- =============================================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED','CANCELLED')),
    total_amount NUMERIC(12,2) NOT NULL,
    shipping_address TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 7. ORDER ITEMS
-- =============================================
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    total_price NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 8. NOTIFICATIONS
-- =============================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('TASK_ASSIGNED','TASK_DUE','TASK_COMPLETED','TASK_APPROVED','TASK_REJECTED','LOW_INVENTORY','CROP_WARNING','WEATHER_WARNING','HARVEST_REMINDER','MARKETPLACE_ORDER','SYSTEM')),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- INDEXES
-- =============================================
CREATE INDEX idx_inventory_items_farm_id ON inventory_items(farm_id);
CREATE INDEX idx_expenses_farm_id ON expenses(farm_id);
CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);
CREATE INDEX idx_harvests_farm_id ON harvests(farm_id);
CREATE INDEX idx_harvests_harvest_date ON harvests(harvest_date);
CREATE INDEX idx_products_seller_id ON products(seller_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- =============================================
-- SEED DATA
-- =============================================

-- Inventory Items
INSERT INTO inventory_items (farm_id, name, category, quantity, unit, minimum_stock, location, description) VALUES
(1, 'NPK Fertilizer 50kg', 'FERTILIZER', 100, 'bags', 20, 'Warehouse A', 'High quality NPK 15-15-15 fertilizer'),
(1, 'Maize Seeds (Hybrid)', 'SEED', 50, 'kg', 10, 'Warehouse A', 'SR52 hybrid maize seeds'),
(1, 'Roundup Herbicide', 'PESTICIDE', 30, 'liters', 5, 'Chemical Store', 'Non-selective herbicide'),
(1, 'Shovel (Heavy Duty)', 'TOOL', 15, 'pieces', 3, 'Tool Shed', 'Steel shovel with wooden handle'),
(1, 'Drip Irrigation Kit', 'EQUIPMENT', 8, 'sets', 2, 'Equipment Room', 'Complete drip irrigation system'),
(1, 'Planting Bags', 'PACKAGING', 500, 'pieces', 100, 'Warehouse B', 'Biodegradable planting bags'),
(2, 'Urea Fertilizer', 'FERTILIZER', 45, 'bags', 15, 'Warehouse A', '46% nitrogen content urea'),
(2, 'Bean Seeds (Red)', 'SEED', 25, 'kg', 8, 'Warehouse A', 'Red kidney bean seeds'),
(2, 'Spray Pump (Manual)', 'TOOL', 10, 'pieces', 2, 'Tool Shed', '20L manual knapsack sprayer');

-- Inventory Transactions
INSERT INTO inventory_transactions (inventory_item_id, type, quantity, reason, created_by) VALUES
(1, 'STOCK_IN', 100, 'Initial stock purchase from supplier', 1),
(2, 'STOCK_IN', 50, 'Seeds purchased for planting season', 1),
(3, 'STOCK_IN', 30, 'Herbicide stock replenishment', 1),
(1, 'STOCK_OUT', 10, 'Used for field preparation - Farm 1', 1),
(2, 'STOCK_OUT', 5, 'Planting started on Field A', 1),
(4, 'STOCK_IN', 15, 'New tools purchased', 2),
(5, 'STOCK_IN', 8, 'Irrigation equipment for dry season', 1);

-- Expenses
INSERT INTO expenses (farm_id, category, amount, description, expense_date, created_by) VALUES
(1, 'SEEDS', 250.00, 'Maize seeds for planting season', '2026-08-01', 1),
(1, 'FERTILIZER', 1500.00, 'NPK fertilizer bulk purchase', '2026-08-05', 1),
(1, 'LABOR', 800.00, 'Field preparation labor - 10 workers x 2 days', '2026-08-10', 1),
(1, 'TRANSPORTATION', 150.00, 'Transportation of fertilizer to farm', '2026-08-06', 1),
(1, 'IRRIGATION', 350.00, 'Drip irrigation system installation', '2026-08-12', 1),
(1, 'PESTICIDE', 200.00, 'Roundup herbicide for weed control', '2026-08-15', 1),
(2, 'SEEDS', 180.00, 'Bean seeds purchase', '2026-08-02', 2),
(2, 'EQUIPMENT', 450.00, 'Spray pump and accessories', '2026-08-08', 2),
(2, 'LABOR', 600.00, 'Land clearing labor', '2026-08-10', 2),
(1, 'OTHER', 75.00, 'Farm fence repair materials', '2026-08-14', 1);

-- Harvests
INSERT INTO harvests (farm_id, field_id, crop_id, recorded_by, harvest_date, quantity, unit, quality, estimated_value, actual_revenue, buyer_id, notes) VALUES
(1, 1, 1, 1, '2026-08-16', 2500, 'kg', 'GOOD', 3750.00, 3500.00, 101, 'First harvest of the season from Field A'),
(1, 2, 2, 1, '2026-08-18', 1200, 'kg', 'PREMIUM', 2400.00, 2600.00, 102, 'Premium quality tomatoes from Field B'),
(1, 1, 1, 2, '2026-08-20', 1800, 'kg', 'MEDIUM', 2700.00, 2500.00, 101, 'Second harvest from Field A'),
(2, 3, 3, 2, '2026-08-17', 800, 'kg', 'GOOD', 1200.00, 1100.00, 103, 'Bean harvest from Farm 2');

-- Products (Marketplace)
INSERT INTO products (name, description, price, stock, status, seller_id, farm_id, category) VALUES
('Fresh Maize (per kg)', 'Freshly harvested hybrid maize from our farms', 2.50, 500, 'ACTIVE', 1, 1, 'Grains'),
('Organic Tomatoes (per kg)', 'Locally grown organic tomatoes - no pesticides', 4.00, 200, 'ACTIVE', 1, 1, 'Vegetables'),
('Red Kidney Beans (per kg)', 'Premium quality red kidney beans', 5.00, 150, 'ACTIVE', 2, 2, 'Grains'),
('NPK Fertilizer 50kg', 'Leftover stock - selling at discount', 35.00, 10, 'PENDING', 1, 1, 'Farm Inputs'),
('Maize Seeds (Hybrid)', 'Extra seeds from bulk purchase', 8.00, 25, 'ACTIVE', 2, 2, 'Farm Inputs'),
('Fresh Cassava (per kg)', 'High quality cassava roots', 1.80, 300, 'PENDING', 2, 2, 'Root Crops'),
('Spray Pump Manual', 'Used spray pump in good condition', 25.00, 2, 'ACTIVE', 1, 1, 'Equipment'),
('Drip Irrigation Kit', 'Complete kit - selling because upgraded', 120.00, 1, 'REJECTED', 2, 2, 'Equipment');

-- Orders
INSERT INTO orders (buyer_id, seller_id, status, total_amount, shipping_address, notes) VALUES
(101, 1, 'DELIVERED', 125.00, '123 Market Street, Kigali', 'Delivered on time - thanks!'),
(102, 1, 'SHIPPED', 80.00, '456 Business Road, Huye', 'Please deliver before Friday'),
(103, 2, 'CONFIRMED', 50.00, '789 Farm Lane, Musanze', 'Bulk order - 10kg beans'),
(104, 1, 'PENDING', 200.00, '321 Main Street, Gisenyi', 'Need by end of month'),
(101, 2, 'PROCESSING', 75.00, '123 Market Street, Kigali', 'Repeat order for restaurant');

-- Order Items
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, total_price) VALUES
(1, 1, 'Fresh Maize (per kg)', 50, 2.50, 125.00),
(2, 2, 'Organic Tomatoes (per kg)', 20, 4.00, 80.00),
(3, 3, 'Red Kidney Beans (per kg)', 10, 5.00, 50.00),
(4, 1, 'Fresh Maize (per kg)', 80, 2.50, 200.00),
(5, 3, 'Red Kidney Beans (per kg)', 15, 5.00, 75.00);

-- Notifications
INSERT INTO notifications (user_id, type, title, message, reference_type, reference_id, is_read) VALUES
(1, 'LOW_INVENTORY', 'Low Stock Alert', 'NPK Fertilizer stock is running low (10 bags remaining). Minimum stock: 20 bags.', 'INVENTORY_ITEM', 1, FALSE),
(1, 'MARKETPLACE_ORDER', 'New Order Received', 'You have a new order #4 for 80kg Fresh Maize (Total: 200.00).', 'ORDER', 4, FALSE),
(1, 'HARVEST_REMINDER', 'Harvest Reminder', 'Field A maize crop is due for harvest in 3 days.', 'FIELD', 1, FALSE),
(1, 'WEATHER_WARNING', 'Weather Alert', 'Heavy rain expected in the next 24 hours. Secure harvested crops.', NULL, NULL, FALSE),
(2, 'MARKETPLACE_ORDER', 'Order Confirmed', 'Order #3 has been confirmed by the buyer.', 'ORDER', 3, TRUE),
(2, 'TASK_COMPLETED', 'Task Completed', 'Field preparation for Farm 2 has been marked as completed.', 'TASK', 1, TRUE),
(1, 'TASK_ASSIGNED', 'New Task Assigned', 'You have been assigned: Irrigation system check for Field B.', 'TASK', 5, FALSE),
(1, 'SYSTEM', 'Welcome', 'Welcome to General Farmers! Start managing your farm operations.', NULL, NULL, TRUE);

SELECT 'All tables created and seed data inserted successfully!' AS result;
