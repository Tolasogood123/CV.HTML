
# General Farmers — Developer 2 Task Breakdown
## Ownership: Inventory/Expenses/Harvest, Marketplace/Orders, Notifications/Weather, Reports/Dashboards, Production Readiness

Branch prefix: `feature/dev2-*`

---

## Phase 0 — Shared Foundation (build together, support Dev1's setup)

- [ ] Review/agree on base package structure and shared conventions with Dev1
- [ ] Agree on `.env.example` variables needed for weather API and file storage
- [ ] Confirm global API response format and exception handler conventions before building on top of them
- [ ] Docker config skeleton (can start early, refined in Phase 8)

---

## Phase 1 — Inventory Module

**Branch:** `feature/inventory`

- [ ] `InventoryItem` entity (farmId, name, category, quantity, unit, minimumStock, location, description)
- [ ] Category enum: `SEED`, `FERTILIZER`, `PESTICIDE`, `TOOL`, `EQUIPMENT`, `PACKAGING`, `OTHER`
- [ ] `InventoryTransaction` entity (type, quantity, reason, createdBy)
  - Type enum: `STOCK_IN`, `STOCK_OUT`, `ADJUSTMENT`
- [ ] Auto-detect `quantity <= minimumStock` → generate `LOW_INVENTORY` notification
- [ ] `@Transactional` on stock-out operations

---

## Phase 2 — Expense & Harvest Modules

**Branch:** `feature/finance`

- [ ] `Expense`, `ExpenseCategory` entities
  - Categories: `SEEDS`, `FERTILIZER`, `PESTICIDE`, `LABOR`, `EQUIPMENT`, `TRANSPORTATION`, `IRRIGATION`, `OTHER`
- [ ] `GET/POST/PUT/DELETE /api/v1/expenses` with filter by farm, category, date range, amount
- [ ] Business rule: an expense must belong to a farm the current user has permission to access

**Branch:** `feature/harvest`

- [ ] `Harvest` entity (farmId, fieldId, cropId, recordedBy, harvestDate, quantity, unit, quality, estimatedValue, actualRevenue, buyerId, notes, imageUrl)
- [ ] Quality enum: `LOW`, `MEDIUM`, `GOOD`, `PREMIUM`
- [ ] `GET/POST/PUT/DELETE /api/v1/harvests`
- [ ] Business rule: a harvest must reference an existing crop/field/farm relationship
- [ ] `@Transactional` on record harvest flow

---

## Phase 3 — Marketplace Module

**Branch:** `feature/marketplace`

- [ ] `Product`, `ProductCategory` entities
  - Status enum: `PENDING`, `APPROVED`, `REJECTED`, `ACTIVE`, `HIDDEN`, `SOLD_OUT`
- [ ] `Order`, `OrderItem` entities
  - Status enum: `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`
- [ ] Endpoints:
  - `GET/POST/PUT/DELETE /api/v1/products`
  - `POST /api/v1/orders`, `GET /api/v1/orders`, `GET /api/v1/orders/{id}`
  - `PATCH /api/v1/orders/{id}/status`
- [ ] Admin approve/reject product endpoints
- [ ] Business rules:
  - A seller can only modify their own products
  - Only authorized admins can approve/reject marketplace products
- [ ] `@Transactional` on create order (multi-row: order + order items + product stock)

---

## Phase 4 — Notifications & Weather

**Branch:** `feature/notifications`

- [ ] `Notification` entity (userId, type, title, message, referenceType, referenceId, isRead)
- [ ] Type enum: `TASK_ASSIGNED`, `TASK_DUE`, `TASK_COMPLETED`, `TASK_APPROVED`, `TASK_REJECTED`, `LOW_INVENTORY`, `CROP_WARNING`, `WEATHER_WARNING`, `HARVEST_REMINDER`, `MARKETPLACE_ORDER`, `SYSTEM`
- [ ] `GET /api/v1/notifications`, `PATCH /api/v1/notifications/{id}/read`, `PATCH /api/v1/notifications/read-all`, `DELETE /api/v1/notifications/{id}`
- [ ] Notification triggers hooked into: task assignment/approval (coordinate with Dev1), low stock, marketplace orders

**Weather:**

- [ ] `WeatherService` / `WeatherServiceImpl` abstraction (lat/lng in → temperature, humidity, wind, rainProbability, condition, forecast out)
- [ ] Farming recommendations based on weather conditions
- [ ] `WEATHER_API_KEY` via environment variable only, never committed

---

## Phase 5 — File Storage (shared with Dev1)

**Branch:** `feature/dev2-file-storage` (or joint with Dev1)

- [ ] `FileStorageService` abstraction — local storage (dev) + cloud storage (prod)
- [ ] File validation: type, size, extension; block executable files
- [ ] Wire into: receipts (expenses), harvest images, marketplace product images (your modules)

---

## Phase 6 — Reports & Dashboards

**Branch:** `feature/reports`

- [ ] Reporting service covering: Farm, Crops, Production, Workers, Finance, Marketplace stats
- [ ] Endpoints:
  - `GET /api/v1/reports/dashboard`
  - `GET /api/v1/reports/farms`
  - `GET /api/v1/reports/crops`
  - `GET /api/v1/reports/production`
  - `GET /api/v1/reports/workers`
  - `GET /api/v1/reports/finance`
  - `GET /api/v1/reports/marketplace`
- [ ] Support query params: `from`, `to`, `farmId`, `cropId`, `province`
- [ ] Use DB aggregation queries — do not load all records into Java memory
- [ ] `GET /api/v1/dashboard/farmer` — own the weather, inventory, notifications, upcomingActivities sections (merge with Dev1's farms/fields/crops/tasks sections)
- [ ] `GET /api/v1/dashboard/admin` — own revenue, expenses, production, marketplace, geographic stats, critical alerts sections (merge with Dev1's user/farm/worker sections)

---

## Phase 6.5 — Audit Log Wiring (Dev1 builds the core module)

- [ ] Dev1 owns the `AuditLog` entity, `AuditLogService`, and `GET /api/v1/audit-logs` endpoint — do not duplicate it
- [ ] Call Dev1's `AuditLogService.log(action, module, description)` helper from your services for: `APPROVE_PRODUCT`, `REJECT_PRODUCT`
- [ ] Confirm audit entries also make sense for: low-stock adjustments and order status changes (optional/nice-to-have, confirm scope with Dev1)

## Phase 6.5 — Database Indexing (your tables)

- [ ] `expenses.farm_id`, `expenses.expense_date` — filtered/date-range expense queries
- [ ] `harvests.farm_id`, `harvests.harvest_date` — production reports
- [ ] `products.seller_id`, `products.status` — marketplace listing + seller dashboard
- [ ] `orders.buyer_id`, `orders.seller_id`, `orders.status` — order lookups both directions
- [ ] `notifications.is_read` — shared with Dev1's `notifications.user_id`; needed for unread-count queries

## Phase 6.5 — Security Checklist (your modules)

- [ ] File upload validation on receipts, harvest images, product images: type, size, extension; block executable files
- [ ] Rate limiting consideration on order creation (prevent spam/abuse)
- [ ] Confirm `WEATHER_API_KEY` and file storage credentials are only read from environment variables, never hardcoded or logged
- [ ] Confirm no internal stack traces leak through report/dashboard error responses

## Phase 6.5 — GitHub Workflow (agree with Dev1, apply to every branch)

- [ ] Same flow as Dev1: pull latest `develop` → implement → test → commit → push branch → open PR → code review → merge
- [ ] Never push unfinished work directly to `main`
- [ ] PR checklist: tests pass, Swagger updated, no secrets committed, DTOs used (no entity leakage)

---

## Phase 7 — Cross-Cutting: DTOs, Validation, Response Format

(Applies to all modules above — coordinate conventions with Dev1 early in Phase 0)

- [ ] Request/Response DTOs for every entity you own (never expose entities directly)
- [ ] Bean Validation annotations (`@NotBlank`, `@Positive`, `@PositiveOrZero`, etc.) — e.g. expense amount > 0, harvest quantity not negative
- [ ] Consistent success/error response format

---

## Phase 8 — Production Readiness (shared with Dev1)

- [ ] Unit + repository + controller tests for all modules above
- [ ] Integration test chain: Create Expense → Create Product → Create Order (append to Dev1's chain)
- [ ] Soft delete (`deletedAt`/`deleted`) for `Product` (Dev1 covers User/Farm/Crop)
- [ ] Swagger documentation for `/api/v1/inventory/**`, `/api/v1/expenses/**`, `/api/v1/harvests/**`, `/api/v1/products/**`, `/api/v1/orders/**`, `/api/v1/reports/**`
- [ ] Docker configuration finalization
- [ ] README contributions for your modules

---

## Suggested Order

1. Phase 0 (with Dev1) + confirm config/CORS/secrets conventions Dev1 sets up
2. Inventory → Expenses → Harvest
3. Marketplace (Products → Orders)
4. Notifications → Weather
5. Sync with Dev1 on File Storage
6. Reports → merge Dashboards with Dev1
7. Wire Audit Log calls (`APPROVE_PRODUCT`, `REJECT_PRODUCT`) into Dev1's AuditLogService
8. DB indexing pass on your tables
9. Tests + Docker + Swagger polish
