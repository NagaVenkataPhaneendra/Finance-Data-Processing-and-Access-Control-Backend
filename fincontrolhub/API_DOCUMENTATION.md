# Finance Dashboard API - Complete Implementation Guide

## Overview
A comprehensive Spring Boot application for Personal Finance Management with role-based access control, JWT authentication, and advanced financial analytics.

## Core Features Implemented

### 1. **User & Role Management** ✅
- **Roles**: VIEWER, ANALYST, ADMIN
- Role-based access control (RBAC) using @PreAuthorize
- User registration and authentication with JWT tokens
- Soft delete support for users (maintain audit trail)
- User status management (active/inactive)
- Pagination and search for user listings

### 2. **Financial Records Management** ✅
- Full CRUD operations for financial records
- Automatic user association via JWT
- Soft delete support (logical deletion)
- Timestamped entries (createdAt, updatedAt)
- Amount validation (must be > 0)
- Type validation (income/expense only)
- Category-based organization
- Date-based filtering
- Search functionality for records

### 3. **Dashboard Summary APIs** ✅
- Total income/expenses calculation
- Net balance computation
- Category-wise breakdown
- Monthly/weekly trend analysis (6 months)
- Recent transaction listings
- Comprehensive summary endpoint
- User-specific analytics

### 4. **Access Control** ✅
Implemented via Spring Security + JWT:

**ADMIN**: Full system access
- Manage all users
- View all records
- Create/update records
- Access dashboard for all users
- Enable/disable user accounts

**ANALYST**: Data management access
- View all records
- Create new records
- Update own records
- Access personal dashboard
- Search and filter records

**VIEWER**: Read-only access
- View personal dashboard
- View own records only
- Cannot create or modify records

### 5. **Validation & Error Handling** ✅

**Input Validation**:
- @NotBlank, @NotNull annotations
- @Positive for amounts
- @Pattern for type validation
- @Size constraints
- Custom validation in services

**Error Handling**:
- Global exception handler
- Proper HTTP status codes
- Descriptive error messages
- Validation error details
- Audit trail for failed operations

### 6. **Data Persistence** ✅
- MySQL 5.1.9 database
- JPA/Hibernate ORM
- Transaction management
- Soft delete implementation
- Timestamps (createdAt, updatedAt)
- Optimized queries with @Query annotations

### 7. **Optional Enhancements** ✅

**Pagination & Sorting**:
- Page-based pagination
- Configurable page size
- Sort order specification
- Total count and page info

**Search Support**:
- Search by username
- Search records by category/keywords
- Type and category filtering
- Date range filtering

**Soft Delete**:
- isDeleted flag on all entities
- Logical deletion (no actual removal)
- Audit trail preservation
- Recovery capability

**Authentication**:
- JWT token-based (JJWT library)
- 24-hour token expiration
- Secure password encoding (BCrypt)
- OncePerRequestFilter for token validation

**API Documentation**:
- Springdoc OpenAPI integration
- Auto-generated Swagger UI
- Method-level @Operation annotations
- Tag-based organization

---

## System Architecture

### Database Schema

**Users Table**:
```sql
- id (PK, Auto)
- username (UNIQUE, NOT NULL)
- password (NOT NULL, BCrypt encoded)
- role (ENUM: VIEWER, ANALYST, ADMIN)
- active (BOOLEAN, default true)
- isDeleted (BOOLEAN, default false)
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)
```

**FinancialRecords Table**:
```sql
- id (PK, Auto)
- user_id (FK to Users, NOT NULL)
- amount (DECIMAL(15,2), NOT NULL)
- type (VARCHAR: 'income' or 'expense')
- category (VARCHAR, NOT NULL)
- date (DATE, NOT NULL)
- notes (VARCHAR, optional)
- isDeleted (BOOLEAN, default false)
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)
- INDEX: (user_id, date) for fast queries
```

### Package Structure
```
com.finance/
├── config/          (Security, JWT, Filters)
├── controller/      (REST endpoints)
├── dto/            (Data transfer objects)
├── exception/      (Global exception handling)
├── model/          (JPA entities)
├── repository/     (Data access layer)
└── service/        (Business logic)
```

---

## API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123",
  "role": "VIEWER"
}

Response: 200 OK
{
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "username": "john_doe",
    "role": "VIEWER",
    "active": true,
    "createdAt": "2026-04-04T17:30:00"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {...},
  "expiresIn": 86400,
  "tokenType": "Bearer"
}
```

### User Management Endpoints (ADMIN only)

#### Get All Users (Paginated)
```http
GET /api/users?page=0&size=10&sort=id,desc
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [{...}, {...}],
  "page": 0,
  "pageSize": 10,
  "totalElements": 25,
  "totalPages": 3,
  "isFirst": true,
  "isLast": false,
  "hasNext": true,
  "hasPrevious": false
}
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "username": "john_doe",
  "role": "VIEWER",
  "active": true,
  "createdAt": "2026-04-04T17:30:00"
}
```

#### Search Users
```http
GET /api/users/search?q=user_query&page=0&size=10
Authorization: Bearer {token}

Response: 200 OK (Paginated results)
```

#### Update User Status
```http
PUT /api/users/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "active": false
}

Response: 200 OK
```

#### Delete User (Soft Delete)
```http
DELETE /api/users/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "message": "User deleted successfully"
}
```

###Financial Records Endpoints

#### Create Record
```http
POST /api/records
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 100.50,
  "type": "expense",
  "category": "Food",
  "date": "2026-04-04",
  "notes": "Grocery shopping"
}

Response: 201 Created
{
  "id": 1,
  "amount": 100.50,
  "type": "expense",
  "category": "Food",
  "date": "2026-04-04",
  "notes": "Grocery shopping",
  "username": "john_doe",
  "createdAt": "2026-04-04T17:31:00"
}
```

#### Get User's Records (Paginated)
```http
GET /api/records?page=0&size=20&sort=date,desc
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [{}, {}...],
  "page": 0,
  "pageSize": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

#### Filter by Type
```http
GET /api/records/filter?type=expense&page=0&size=10
Authorization: Bearer {token}

Response: 200 OK (Filtered paginated results)
```

#### Filter by Category
```http
GET /api/records/category?category=Food&page=0&size=10
Authorization: Bearer {token}

Response: 200 OK (Filtered paginated results)
```

#### Filter by Date Range
```http
GET /api/records/date-range?start=2026-01-01&end=2026-04-04&page=0&size=10
Authorization: Bearer {token}

Response: 200 OK (Filtered paginated results)
```

#### Search Records
```http
GET /api/records/search?q=keyword&page=0&size=10
Authorization: Bearer {token}

Response: 200 OK (Search results paginated)
```

#### Get Single Record
```http
GET /api/records/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "amount": 100.50,
  "type": "expense",
  "category": "Food",
  "date": "2026-04-04",
  "notes": "Grocery shopping",
  "username": "john_doe",
  "createdAt": "2026-04-04T17:31:00",
  "updatedAt": "2026-04-04T17:32:00"
}
```

#### Update Record
```http
PUT /api/records/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 120.50,
  "category": "Groceries",
  "notes": "Updated grocery amount"
}

Response: 200 OK (Updated record)
```

#### Delete Record (Soft Delete)
```http
DELETE /api/records/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "message": "Record deleted successfully"
}
```

### Dashboard Endpoints

#### Personal Dashboard Summary
```http
GET /api/dashboard/summary
Authorization: Bearer {token}

Response: 200 OK
{
  "totalIncome": 5000.00,
  "totalExpenses": 3500.00,
  "netBalance": 1500.00,
  "categoryTotals": {
    "Food": 1200.00,
    "Transport": 800.00,
    "Entertainment": 500.00,
    ...
  },
  "expensesByCategory": {...},
  "incomeByCategory": {...},
  "last6Months": {
    "income": {"2026-01": 1000, "2026-02": 1200, ...},
    "expenses": {"2026-01": 800, "2026-02": 950, ...}
  },
  "recentTransactions": [{}, {}...]
}
```

#### Get Income Summary
```http
GET /api/dashboard/income
Authorization: Bearer {token}

Response: 200 OK
{
  "totalIncome": 5000.00,
  "monthly": {"2026-01": 1000, "2026-02": 1200, ...}
}
```

#### Get Expense Summary
```http
GET /api/dashboard/expenses
Authorization: Bearer {token}

Response: 200 OK
{
  "totalExpenses": 3500.00,
  "monthly": {"2026-01": 800, "2026-02": 950, ...}
}
```

#### Get Net Balance
```http
GET /api/dashboard/balance
Authorization: Bearer {token}

Response: 200 OK
{
  "netBalance": 1500.00,
  "totalIncome": 5000.00,
  "totalExpenses": 3500.00
}
```

---

## Running the Application

### Prerequisites
- Java 1.8.0_331 or higher
- Maven 3.x
- MySQL 5.1.9
- Git

### Setup Steps

#### 1. Database Setup
```sql
CREATE DATABASE finance_db;
USE finance_db;
```

Or run the SQL script:
```bash
mysql -u root -proot < MYSQL_SETUP.sql
```

#### 2. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC
```

#### 3. Build Project
```bash
cd c:\Users\phani\workspace\fincontrolhub
mvn clean install
```

#### 4. Run Application
```bash
mvn spring-boot:run
```

Or via Java:
```bash
java -cp target/classes:target/lib/* com.finance.FinanceApplication
```

Application starts on: `http://localhost:8080`

### Access Points

- **API Docs**: http://localhost:8080/swagger-ui.html
- **API JSON**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

---

## Default Test Account

**Username**: admin
**Password**: admin123
**Role**: ADMIN

---

## Role-Based Access Control Matrix

|Operation|VIEWER|ANALYST|ADMIN|
|---------|------|-------|-----|
|View own dashboard|✓|✓|✓|
|Create records|✗|✓|✓|
|Edit own records|✗|✓|✓|
|Delete own records|✗|✓|✓|
|View all users|✗|✗|✓|
|Manage users|✗|✗|✓|
|Enable/disable users|✗|✗|✓|
|View all records|✗|✗|✓|
|Search records|✓|✓|✓|
|Export data|Partial|✓|✓|

---

## Error Response Format

All errors follow this standardized format:

```json
{
  "timestamp": "2026-04-04T17:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed or operation failed",
  "fields": {
    "fieldName": "error message for this field"
  },
  "path": "/api/endpoint"
}
```

### Common HTTP Status Codes

- `200 OK`: Success
- `201 Created`: Resource created
- `400 Bad Request`: Invalid input
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

---

## Security Features

### JWT Authentication
- Token-based stateless auth
- 24-hour expiration
- Refresh support via re-login
- Secure token validation

### Password Security
- BCrypt hashing algorithm
- Minimum 6 characters
- Cannot reuse usernames

### SQL Injection Prevention
- Parameterized queries (@Query with @Param)
- JPA prevents direct SQL injection

### CORS Configuration
- Configured for `http://localhost:3000` (frontend)
- All methods enabled for dev

---

## Database Indexes for Performance

```sql
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_active ON user(active);
CREATE INDEX idx_record_user ON financial_record(user_id);
CREATE INDEX idx_record_date ON financial_record(date);
CREATE INDEX idx_record_user_date ON financial_record(user_id, date);
CREATE INDEX idx_record_type ON financial_record(type);
CREATE INDEX idx_record_category ON financial_record(category);
```

---

## Troubleshooting

### Issue: "Access denied for user 'root'@'localhost'"
**Solution**: Verify MySQL credentials in application.properties match your MySQL setup

### Issue: "Unknown column 'XXX' in 'field list'"
**Solution**: Run `mvn clean compile` to regenerate classes

### Issue: "Cannot resolve symbol" in IDE
**Solution**: 
- Run `mvn clean install`
- Rebuild project (IDE menu)
- Clear IDE cache

### Issue: JWT Token Expired
**Solution**: Re-login to get a new token

---

## Version Information

- Spring Boot: 2.7.18
- Spring Framework: 5.3.31
- Spring Data JPA: 2.7.18
- Hibernate: 5.6.15
- MySQL Connector: 5.1.9
- JJWT: 0.9.1
- Lombok: 1.18.30
- Springdoc OpenAPI: 1.7.0

---

## License
MIT License - Free to use and modify

---

## Contact & Support
For issues or questions, contact the development team.
