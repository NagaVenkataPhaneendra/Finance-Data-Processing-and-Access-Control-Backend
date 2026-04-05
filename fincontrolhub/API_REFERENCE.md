# API Reference Documentation

Complete reference guide for all Finance Dashboard API endpoints with detailed specifications, examples, and response formats.

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Authorization & Access Control](#authorization--access-control)
4. [Authentication Endpoints](#authentication-endpoints)
5. [User Management Endpoints](#user-management-endpoints)
6. [Financial Records Endpoints](#financial-records-endpoints)
7. [Dashboard Endpoints](#dashboard-endpoints)
8. [Error Handling](#error-handling)
9. [Response Formats](#response-formats)
10. [Rate Limiting & Best Practices](#rate-limiting--best-practices)

---

## Overview

### API Base URL

```
http://localhost:8080/api
```

### API Version

- **Current**: v1 (implicit in the endpoint structure)
- **Documentation**: `/v3/api-docs`
- **Swagger UI**: `/swagger-ui.html`

### Authentication Scheme

- **Type**: Bearer Token (JWT)
- **Header**: `Authorization: Bearer {token}`
- **Token Lifetime**: 24 hours
- **Encoding**: HS256 (HMAC with SHA-256)

### Content Type

```
Content-Type: application/json
Accept: application/json
```

---

## Authentication

### JWT Token Structure

Each JWT token contains:
- **Header**: Algorithm (HS256) and type (JWT)
- **Payload**: User ID, username, role, issue time, expiration
- **Signature**: HMAC-SHA256 signed secret

### Token Acquisition

1. Call `POST /api/auth/login` with credentials
2. Receive JWT token in response
3. Include token in Authorization header: `Authorization: Bearer YOUR_TOKEN`
4. Token valid for 24 hours
5. After expiration, re-login to get new token

### Security Best Practices

- ✅ Always use HTTPS in production
- ✅ Store token securely (not in localStorage for sensitive apps)
- ✅ Include token in Authorization header, not URL
- ✅ Never commit tokens to version control
- ✅ Change default JWT secret in production
- ✅ Use strong passwords (8+ characters, mix of types)

---

## Authorization & Access Control

### Role-Based Access Control (RBAC)

| Operation | VIEWER | ANALYST | ADMIN |
|-----------|--------|---------|-------|
| Login | ✅ | ✅ | ✅ |
| View own profile | ✅ | ✅ | ✅ |
| View own dashboard | ✅ | ✅ | ✅ |
| Create records | ❌ | ✅ | ✅ |
| Edit own records | ❌ | ✅ | ✅ |
| Delete own records | ❌ | ✅ | ✅ |
| View other users' records | ❌ | ❌ | ✅ |
| List all users | ❌ | ❌ | ✅ |
| Create users | ❌ | ❌ | ✅ |
| Modify users | ❌ | ❌ | ✅ |
| Delete users | ❌ | ❌ | ✅ |

### User Roles

**VIEWER**: Read-only access to personal data
- View personal dashboard
- View personal financial records
- Cannot create or modify any records

**ANALYST**: Data entry and analysis access
- All VIEWER permissions
- Create financial records
- Edit own records
- Delete own records
- Search and filter records

**ADMIN**: Full system access
- All ANALYST permissions
- Manage all users
- View all records
- Access all dashboards
- Enable/disable users
- Edit/delete any records

---

## Authentication Endpoints

### 1. Register User

Create a new user account.

**Request**:
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123",
  "role": "ANALYST"
}
```

**Parameters**:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | string | Yes | Unique username (3-50 chars) |
| password | string | Yes | Password (6+ chars) |
| role | string | Yes | One of: VIEWER, ANALYST, ADMIN |

**Response** (201 Created):
```json
{
  "message": "User registered successfully",
  "user": {
    "id": 2,
    "username": "john_doe",
    "role": "ANALYST",
    "active": true,
    "createdAt": "2026-04-04T14:30:00Z"
  }
}
```

**Possible Errors**:

| Status | Error | Reason |
|--------|-------|--------|
| 400 | Bad Request | Invalid input (missing fields, invalid role) |
| 409 | Conflict | Username already exists |
| 500 | Server Error | Database error |

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane_smith",
    "password": "JanePass456",
    "role": "VIEWER"
  }'
```

---

### 2. Login

Authenticate user and get JWT token.

**Request**:
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Parameters**:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | string | Yes | User's username |
| password | string | Yes | User's password |

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE2NDg1MzQ4MDAsImV4cCI6MTY0ODYyMTIwMH0.AbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOp",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN",
    "active": true,
    "createdAt": "2026-04-04T10:00:00Z"
  }
}
```

**Response Fields**:

| Field | Type | Description |
|-------|------|-------------|
| token | string | JWT token for authorization |
| tokenType | string | Always "Bearer" |
| expiresIn | number | Token lifetime in seconds (86400 = 24 hours) |
| user | object | Authenticated user details |

**Possible Errors**:

| Status | Error | Reason |
|--------|-------|--------|
| 400 | Bad Request | Missing credentials |
| 401 | Unauthorized | Invalid username or password |
| 500 | Server Error | Database error |

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## User Management Endpoints

### Prerequisites
- Requires ADMIN role
- All requests must include valid JWT token

---

### 1. Get All Users (Paginated)

List all users with pagination support.

**Request**:
```http
GET /api/users?page=0&size=10&sort=id,desc
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number (0-indexed) |
| size | integer | 20 | Items per page |
| sort | string | id,asc | Sort field and direction (e.g., username,asc) |

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "username": "admin",
      "role": "ADMIN",
      "active": true,
      "createdAt": "2026-04-04T10:00:00Z",
      "updatedAt": "2026-04-04T14:30:00Z"
    },
    {
      "id": 2,
      "username": "john_doe",
      "role": "ANALYST",
      "active": true,
      "createdAt": "2026-04-04T12:00:00Z",
      "updatedAt": "2026-04-04T12:00:00Z"
    }
  ],
  "page": 0,
  "pageSize": 10,
  "totalElements": 2,
  "totalPages": 1,
  "isFirst": true,
  "isLast": true,
  "hasNext": false,
  "hasPrevious": false
}
```

**Possible Errors**:

| Status | Error |
|--------|-------|
| 401 | Unauthorized (no token) |
| 403 | Forbidden (not ADMIN) |
| 500 | Server Error |

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 2. Get User by ID

Retrieve specific user details.

**Request**:
```http
GET /api/users/{id}
Authorization: Bearer {token}
```

**Path Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | User ID |

**Response** (200 OK):
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN",
  "active": true,
  "createdAt": "2026-04-04T10:00:00Z",
  "updatedAt": "2026-04-04T14:30:00Z"
}
```

**Possible Errors**:

| Status | Error | Reason |
|--------|-------|--------|
| 401 | Unauthorized | No token |
| 403 | Forbidden | Not ADMIN |
| 404 | Not Found | User doesn't exist |
| 500 | Server Error | Database error |

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. Search Users

Search users by username.

**Request**:
```http
GET /api/users/search?q=admin&page=0&size=10
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| q | string | Search query (username) |
| page | integer | Page number (0-indexed) |
| size | integer | Items per page |

**Response** (200 OK): Same pagination format as Get All Users

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/users/search?q=admin&page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 4. Update User Status

Enable or disable a user account.

**Request**:
```http
PUT /api/users/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "active": false
}
```

**Path Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | User ID |

**Request Body**:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| active | boolean | Yes | true to enable, false to disable |

**Response** (200 OK):
```json
{
  "message": "User status updated successfully",
  "user": {
    "id": 2,
    "username": "john_doe",
    "role": "ANALYST",
    "active": false,
    "updatedAt": "2026-04-04T15:00:00Z"
  }
}
```

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/users/2/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"active":false}'
```

---

### 5. Delete User (Soft Delete)

Mark a user as deleted (data retained).

**Request**:
```http
DELETE /api/users/{id}
Authorization: Bearer {token}
```

**Path Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | User ID |

**Response** (200 OK):
```json
{
  "message": "User deleted successfully"
}
```

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Financial Records Endpoints

### Prerequisites
- Requires valid JWT token
- VIEWER: Can only view own records
- ANALYST: Can create/edit/delete own records
- ADMIN: Full access to all records

---

### 1. Create Record

Create a new financial record.

**Request**:
```http
POST /api/records
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 150.50,
  "type": "expense",
  "category": "Food",
  "date": "2026-04-04",
  "notes": "Grocery shopping at Walmart"
}
```

**Request Body**:

| Field | Type | Required | Description | Constraints |
|-------|------|----------|-------------|-------------|
| amount | decimal | Yes | Transaction amount | > 0, max 15 digits total |
| type | string | Yes | Transaction type | "income" or "expense" |
| category | string | Yes | Category | 1-100 chars |
| date | string | Yes | Transaction date | YYYY-MM-DD format |
| notes | string | No | Additional notes | 0-500 chars |

**Response** (201 Created):
```json
{
  "id": 5,
  "amount": 150.50,
  "type": "expense",
  "category": "Food",
  "date": "2026-04-04",
  "notes": "Grocery shopping at Walmart",
  "username": "john_doe",
  "userId": 2,
  "createdAt": "2026-04-04T15:30:00Z",
  "updatedAt": "2026-04-04T15:30:00Z"
}
```

**Possible Errors**:

| Status | Error | Reason |
|--------|-------|--------|
| 400 | Bad Request | Invalid input (missing fields, negative amount) |
| 401 | Unauthorized | No token |
| 403 | Forbidden | Insufficient permissions (VIEWER cannot create) |
| 500 | Server Error | Database error |

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 150.50,
    "type": "expense",
    "category": "Food",
    "date": "2026-04-04",
    "notes": "Grocery shopping"
  }'
```

---

### 2. Get All Records (Paginated)

Retrieve all records for authenticated user (or all for ADMIN).

**Request**:
```http
GET /api/records?page=0&size=20&sort=date,desc
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number |
| size | integer | 20 | Items per page |
| sort | string | date,desc | Sort field and direction |

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": 5,
      "amount": 150.50,
      "type": "expense",
      "category": "Food",
      "date": "2026-04-04",
      "notes": "Grocery shopping",
      "username": "john_doe",
      "userId": 2,
      "createdAt": "2026-04-04T15:30:00Z",
      "updatedAt": "2026-04-04T15:30:00Z"
    }
  ],
  "page": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1,
  "isFirst": true,
  "isLast": true,
  "hasNext": false,
  "hasPrevious": false
}
```

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/records?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. Get Single Record

Retrieve specific record details.

**Request**:
```http
GET /api/records/{id}
Authorization: Bearer {token}
```

**Path Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | Record ID |

**Response** (200 OK):
```json
{
  "id": 5,
  "amount": 150.50,
  "type": "expense",
  "category": "Food",
  "date": "2026-04-04",
  "notes": "Grocery shopping",
  "username": "john_doe",
  "userId": 2,
  "createdAt": "2026-04-04T15:30:00Z",
  "updatedAt": "2026-04-04T15:30:00Z"
}
```

**Possible Errors**:

| Status | Error | Reason |
|--------|-------|--------|
| 401 | Unauthorized | No token |
| 403 | Forbidden | Not owner (VIEWER/ANALYST) or not ADMIN |
| 404 | Not Found | Record not found |

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/records/5 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 4. Update Record

Modify an existing record.

**Request**:
```http
PUT /api/records/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 175.75,
  "category": "Groceries",
  "notes": "Updated: Spent more"
}
```

**Path Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | Record ID |

**Request Body**: Same as Create Record (all fields optional except amount > 0)

**Response** (200 OK): Updated record object

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/records/5 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount": 175.75, "category": "Groceries"}'
```

---

### 5. Delete Record (Soft Delete)

Mark record as deleted.

**Request**:
```http
DELETE /api/records/{id}
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "message": "Record deleted successfully"
}
```

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/records/5 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 6. Filter Records by Type

Get records filtered by type (income/expense).

**Request**:
```http
GET /api/records/filter?type=expense&page=0&size=10
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| type | string | Yes | "income" or "expense" |
| page | integer | No | Page number |
| size | integer | No | Items per page |

**Response** (200 OK): Paginated filtered records

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/records/filter?type=expense&page=0" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 7. Filter Records by Category

Get records for specific category.

**Request**:
```http
GET /api/records/category?category=Food&page=0&size=10
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| category | string | Yes | Category name |
| page | integer | No | Page number |
| size | integer | No | Items per page |

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/records/category?category=Food" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 8. Filter Records by Date Range

Get records within date range.

**Request**:
```http
GET /api/records/date-range?start=2026-03-01&end=2026-04-04&page=0&size=10
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Required | Description | Format |
|-----------|------|----------|-------------|--------|
| start | string | Yes | Start date | YYYY-MM-DD |
| end | string | Yes | End date | YYYY-MM-DD |
| page | integer | No | Page number | |
| size | integer | No | Items per page | |

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/records/date-range?start=2026-03-01&end=2026-04-04" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 9. Search Records

Search records by keywords in notes or category.

**Request**:
```http
GET /api/records/search?q=walmart&page=0&size=10
Authorization: Bearer {token}
```

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| q | string | Yes | Search keyword |
| page | integer | No | Page number |
| size | integer | No | Items per page |

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/records/search?q=walmart" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Dashboard Endpoints

### Prerequisites
- Requires valid JWT token
- All users can access their own dashboard
- ADMIN can access any user's dashboard

---

### 1. Dashboard Summary

Get complete dashboard summary with analytics.

**Request**:
```http
GET /api/dashboard/summary
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 3500.00,
  "netBalance": 1500.00,
  "categoryTotals": {
    "Food": 1200.00,
    "Transport": 800.00,
    "Entertainment": 500.00,
    "Utilities": 1000.00
  },
  "expensesByCategory": {
    "Food": 1200.00,
    "Transport": 800.00,
    "Entertainment": 500.00,
    "Utilities": 1000.00
  },
  "incomeByCategory": {
    "Salary": 5000.00
  },
  "last6Months": {
    "income": {
      "2025-10": 5000.00,
      "2025-11": 5000.00,
      "2025-12": 5000.00,
      "2026-01": 5000.00,
      "2026-02": 5000.00,
      "2026-03": 5000.00
    },
    "expenses": {
      "2025-10": 3200.00,
      "2025-11": 3300.00,
      "2025-12": 3400.00,
      "2026-01": 3500.00,
      "2026-02": 3400.00,
      "2026-03": 3500.00
    }
  },
  "recentTransactions": [
    {
      "id": 5,
      "amount": 150.50,
      "type": "expense",
      "category": "Food",
      "date": "2026-04-04",
      "notes": "Grocery shopping",
      "createdAt": "2026-04-04T15:30:00Z"
    }
  ]
}
```

**Response Fields**:

| Field | Type | Description |
|-------|------|-------------|
| totalIncome | decimal | Sum of all income |
| totalExpenses | decimal | Sum of all expenses |
| netBalance | decimal | Total income - expenses |
| categoryTotals | object | Total by category |
| expensesByCategory | object | Expenses breakdown by category |
| incomeByCategory | object | Income breakdown by category |
| last6Months | object | Monthly trends (6 months) |
| recentTransactions | array | Last 5 transactions |

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 2. Income Summary

Get income total and monthly breakdown.

**Request**:
```http
GET /api/dashboard/income
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "totalIncome": 5000.00,
  "monthly": {
    "2025-10": 5000.00,
    "2025-11": 5000.00,
    "2025-12": 5000.00,
    "2026-01": 5000.00,
    "2026-02": 5000.00,
    "2026-03": 5000.00
  }
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/dashboard/income \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. Expense Summary

Get expense total and monthly breakdown.

**Request**:
```http
GET /api/dashboard/expenses
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "totalExpenses": 3500.00,
  "monthly": {
    "2025-10": 3200.00,
    "2025-11": 3300.00,
    "2025-12": 3400.00,
    "2026-01": 3500.00,
    "2026-02": 3400.00,
    "2026-03": 3500.00
  }
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/dashboard/expenses \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 4. Balance Summary

Get net balance calculation.

**Request**:
```http
GET /api/dashboard/balance
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "netBalance": 1500.00,
  "totalIncome": 5000.00,
  "totalExpenses": 3500.00
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/dashboard/balance \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Error Handling

### Standard Error Response Format

All errors follow this format:

```json
{
  "timestamp": "2026-04-04T15:45:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Descriptive error message",
  "fields": {
    "fieldName": "Validation error for this field"
  },
  "path": "/api/endpoint"
}
```

### HTTP Status Codes

| Status | Meaning | Common Causes |
|--------|---------|---------------|
| 200 | OK | Request successful |
| 201 | Created | Resource successfully created |
| 400 | Bad Request | Invalid input, missing required fields |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate resource (e.g., username exists) |
| 500 | Server Error | Internal server error |

### Common Error Examples

**400 Bad Request - Invalid Input**:
```json
{
  "timestamp": "2026-04-04T15:45:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fields": {
    "amount": "Amount must be greater than 0",
    "type": "Type must be 'income' or 'expense'"
  },
  "path": "/api/records"
}
```

**401 Unauthorized**:
```json
{
  "timestamp": "2026-04-04T15:45:30Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/dashboard/summary"
}
```

**403 Forbidden**:
```json
{
  "timestamp": "2026-04-04T15:45:30Z",
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource",
  "path": "/api/users"
}
```

**404 Not Found**:
```json
{
  "timestamp": "2026-04-04T15:45:30Z",
  "status": 404,
  "error": "Not Found",
  "message": "Record with ID 999 not found",
  "path": "/api/records/999"
}
```

---

## Response Formats

### Pagination Response

All list endpoints return paginated responses:

```json
{
  "content": [ /* array of items */ ],
  "page": 0,
  "pageSize": 20,
  "totalElements": 150,
  "totalPages": 8,
  "isFirst": true,
  "isLast": false,
  "hasNext": true,
  "hasPrevious": false
}
```

### Pagination Parameters

| Parameter | Type | Default | Range |
|-----------|------|---------|-------|
| page | integer | 0 | 0 to (totalPages-1) |
| size | integer | 20 | 1 to 100 |
| sort | string | varies | See individual endpoints |

---

## Rate Limiting & Best Practices

### Rate Limiting (Current)

- No formal rate limiting implemented
- Monitor your request patterns
- Consider implementing client-side throttling

### Best Practices

1. **Authentication**:
   - Store token securely
   - Refresh before expiration
   - Use HTTPS only in production

2. **Pagination**:
   - Always use pagination for list endpoints
   - Don't request all records at once
   - Use appropriate page size (10-50 recommended)

3. **Error Handling**:
   - Check HTTP status codes
   - Handle error responses gracefully
   - Implement retry logic for 5xx errors

4. **Performance**:
   - Filter data server-side when possible
   - Use search instead of retrieving all records
   - Cache responses client-side when appropriate

5. **Security**:
   - Never commit tokens to version control
   - Use environment variables for sensitive data
   - Validate input on client and server
   - Use HTTPS in production

---

**Last Updated**: April 4, 2026
