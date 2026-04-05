# Finance Dashboard - Complete Project Documentation

## 📊 Project Overview

A production-ready **Personal Finance Management System** built with Spring Boot, featuring JWT authentication, role-based access control, comprehensive financial analytics, and a complete REST API with Swagger documentation.

### Key Highlights
- ✅ **Role-Based Access Control (RBAC)**: VIEWER, ANALYST, ADMIN
- ✅ **JWT Authentication**: Secure token-based auth  
- ✅ **Financial Records Management**: Complete CRUD with soft delete
- ✅ **Advanced Dashboard Analytics**: Income, expenses, trends, category analysis
- ✅ **Pagination & Search**: Efficient data retrieval
- ✅ **Comprehensive Validation**: Input validation with detailed error messages
- ✅ **API Documentation**: Auto-generated Swagger UI
- ✅ **Database**: MySQL with JPA/Hibernate
- ✅ **Production-Ready**: Comprehensive error handling and security

---

## 📑 Documentation Structure

This project includes comprehensive documentation covering:
1. **README.md** (this file) - Project overview and quick reference
2. **SETUP_GUIDE.md** - Detailed step-by-step setup instructions
3. **API_REFERENCE.md** - Complete API endpoint documentation
4. **ARCHITECTURE.md** - Design decisions and assumptions
5. **TRADEOFFS.md** - Design tradeoffs and rationale

---

## 🚀 Quick Start (30 seconds)

### Prerequisites
- **Java**: 1.8.0_331 or higher
- **Maven**: 3.x
- **MySQL**: 5.7+ running on localhost:3306

### 1️⃣ Setup Database
```bash
mysql -u root -proot < MYSQL_SETUP.sql
```

### 2️⃣ Build & Run
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### 3️⃣ Access Application
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Default Account**: `admin` / `admin123`

---

## 📚 Default Test Credentials

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | admin    | admin123  |

---

## 🔐 Role-Based Access Control

| Feature | VIEWER | ANALYST | ADMIN |
|---------|--------|---------|-------|
| View own dashboard | ✅ | ✅ | ✅ |
| Create records | ❌ | ✅ | ✅ |
| Edit own records | ❌ | ✅ | ✅ |
| Delete own records | ❌ | ✅ | ✅ |
| View all users | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

---

## 📡 Core API Endpoints (Quick Reference)

### Authentication
```
POST   /api/auth/register        Register new user
POST   /api/auth/login           Get JWT token
```

### Financial Records
```
POST   /api/records              Create record
GET    /api/records              List records (paginated)
GET    /api/records/{id}         Get single record
PUT    /api/records/{id}         Update record
DELETE /api/records/{id}         Delete record (soft delete)
GET    /api/records/filter       Filter by type
GET    /api/records/category     Filter by category
GET    /api/records/date-range   Date range filtering
GET    /api/records/search       Search records
```

### Dashboard
```
GET    /api/dashboard/summary    Complete dashboard summary
GET    /api/dashboard/income     Total income & monthly breakdown
GET    /api/dashboard/expenses   Total expenses & monthly breakdown
GET    /api/dashboard/balance    Net balance calculation
```

### User Management (Admin)
```
POST   /api/users                Create user
GET    /api/users                List users (paginated)
GET    /api/users/{id}           Get user details
PUT    /api/users/{id}/status    Update user status
DELETE /api/users/{id}           Delete user
GET    /api/users/search         Search users
```

---

## ✨ Features Implemented

### Core Requirements ✅
- ✅ User & Role Management (VIEWER, ANALYST, ADMIN)
- ✅ Financial Records Management (CRUD)
- ✅ Dashboard Summary APIs  
- ✅ Access Control Logic with @PreAuthorize
- ✅ Validation & Error Handling
- ✅ Data Persistence (MySQL + JPA/Hibernate)

### Optional Enhancements ✅
- ✅ Pagination & Sorting
- ✅ Search Support (username, category, keywords)
- ✅ Soft Delete (audit trail)
- ✅ JWT Authentication (24h expiration)
- ✅ API Documentation (Swagger UI)
- ✅ Global Exception Handler
- ✅ Timestamps on all entities
- ✅ Input Validation Annotations
- ✅ Category-wise breakdown
- ✅ Monthly trend analysis

---

## 🧪 Testing the API

### Using cURL - Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Using cURL - Create Record
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "type": "expense",
    "category": "Food",
    "date": "2026-04-04",
    "notes": "Lunch"
  }'
```

### Using Swagger UI
1. Open: http://localhost:8080/swagger-ui.html
2. Click "Authorize" button
3. Paste JWT token from login response
4. Test all endpoints interactively

---

## 📊 Database Schema

### Users Table
```
- id (PK, Auto-increment)
- username (UNIQUE, NOT NULL)
- password (BCrypt encoded)
- role (ENUM: VIEWER, ANALYST, ADMIN)
- active (BOOLEAN, default: true)
- isDeleted (BOOLEAN, default: false)
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)
```

### FinancialRecords Table
```
- id (PK, Auto-increment)
- user_id (FK to Users)
- amount (DECIMAL 15,2)
- type (VARCHAR: 'income' or 'expense')
- category (VARCHAR)
- date (DATE)
- notes (VARCHAR, optional)
- isDeleted (BOOLEAN, default: false)
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)
- INDEX: (user_id, date)
```

---

## 📦 Project Structure

```
fincontrolhub/
├── src/main/java/com/finance/
│   ├── FinanceApplication.java          (Main entry point)
│   ├── config/                          (Security & JWT configuration)
│   │   ├── SecurityConfig.java
│   │   ├── JwtFilter.java
│   │   └── JwtUtil.java
│   ├── controller/                      (REST endpoints)
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   ├── RecordController.java
│   │   └── UserController.java
│   ├── service/                         (Business logic)
│   ├── repository/                      (Data access layer)
│   ├── model/                           (JPA entities)
│   ├── dto/                             (Data transfer objects)
│   └── exception/                       (Global exception handling)
├── src/main/resources/
│   └── application.properties
├── pom.xml                              (Maven configuration)
├── MYSQL_SETUP.sql                      (Database initialization)
├── README.md                            (Overview & quick reference)
├── SETUP_GUIDE.md                       (Step-by-step setup)
├── API_REFERENCE.md                     (Complete API documentation)
├── ARCHITECTURE.md                      (Design & assumptions)
└── TRADEOFFS.md                         (Design decisions)
```

---

## 📈 Version Information

| Component | Version |
|-----------|---------|
| Java | 1.8.0_331 |
| Spring Boot | 2.7.18 |
| Spring Framework | 5.3.31 |
| Spring Security | 5.7.x |
| Hibernate | 5.6.15 |
| MySQL Connector | 5.1.9 |
| JJWT | 0.9.1 |
| Lombok | 1.18.30 |
| Springdoc OpenAPI | 1.6.15 |

---

## 🐛 Troubleshooting

### "Access denied for user 'root'@'localhost'"
- Verify MySQL is running: `mysql -u root -p -e "SHOW DATABASES;"`
- Check credentials in `src/main/resources/application.properties`
- Ensure database `finance_db` exists

### "Cannot resolve symbol" errors
- Run: `mvn clean install`
- Rebuild project in IDE: `Ctrl+Shift+B` (VS Code)

### Port 8080 already in use
- Change port in `application.properties`: `server.port=8081`
- Or kill the process: `netstat -ano | findstr :8080`

### JWT Token Expired
- Re-login to get a new token
- Token expires after 24 hours by default

---

## 📝 Additional Documentation

For more detailed information, refer to:
- **SETUP_GUIDE.md** - Complete setup instructions with troubleshooting
- **API_REFERENCE.md** - Detailed API endpoint specifications and examples
- **ARCHITECTURE.md** - System design, assumptions, and technical decisions
- **TRADEOFFS.md** - Design tradeoffs and their rationale
- **API_DOCUMENTATION.md** - Comprehensive API implementation details

---

## ✅ Project Status

**Status**: PRODUCTION READY ✅

All core requirements implemented with:
- ✅ Professional error handling
- ✅ Comprehensive validation
- ✅ Security best practices
- ✅ Optional enhancements
- ✅ Complete API documentation

---

## 🔗 Quick Links

- GitHub Repo: (Add your repo URL)
- Issue Tracker: (Add your tracker URL)
- API Docs: http://localhost:8080/swagger-ui.html (when running)

---

**Last Updated**: April 4, 2026  
**Maintained By**: Finance Dashboard Team
