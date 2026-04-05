# Architecture & Design Document

Comprehensive documentation of the Finance Dashboard system architecture, design decisions, and assumptions.

---

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Technical Stack](#technical-stack)
3. [Design Patterns](#design-patterns)
4. [Data Model](#data-model)
5. [Security Architecture](#security-architecture)
6. [Access Control Design](#access-control-design)
7. [Key Assumptions](#key-assumptions)
8. [API Design Principles](#api-design-principles)
9. [Performance Considerations](#performance-considerations)
10. [Scalability](#scalability)

---

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Applications                     │
│              (Web, Mobile, Desktop Clients)                  │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    HTTP/HTTPS REST API
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                   Spring Boot Application                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Security Layer                           │   │
│  │  ├─ JWT Authentication Filter                        │   │
│  │  ├─ Spring Security Configuration                    │   │
│  │  └─ CORS Configuration                               │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           REST Controllers (API Layer)                │   │
│  │  ├─ AuthController                                    │   │
│  │  ├─ UserController                                    │   │
│  │  ├─ RecordController                                  │   │
│  │  └─ DashboardController                               │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │        Service Layer (Business Logic)                 │   │
│  │  ├─ AuthService                                       │   │
│  │  ├─ UserService                                       │   │
│  │  ├─ RecordService                                     │   │
│  │  └─ DashboardService                                  │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │    Repository Layer (Data Access Layer)               │   │
│  │  ├─ UserRepository                                    │   │
│  │  └─ FinancialRecordRepository                         │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Exception Handling & Logging                  │   │
│  │  ├─ Global Exception Handler                          │   │
│  │  └─ Structured Logging                                │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    JDBC / Hibernate ORM
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                    MySQL Database                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Tables:                                              │   │
│  │  ├─ user (authentication & authorization)            │   │
│  │  └─ financial_record (transaction data)              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Layered Architecture

The application follows a **three-tier layered architecture**:

1. **Presentation Layer (Controller)**
   - REST endpoints
   - Request validation
   - Response formatting
   - HTTP status code handling

2. **Business Logic Layer (Service)**
   - Core business rules
   - Authorization checks
   - Data transformation
   - Error handling

3. **Data Access Layer (Repository)**
   - Database operations
   - Query execution
   - Entity mapping
   - Transaction management

### Advantages of This Architecture

✅ **Separation of Concerns**: Each layer has distinct responsibility  
✅ **Testability**: Easy to test each layer independently  
✅ **Maintainability**: Clear code organization and structure  
✅ **Reusability**: Services can be used by multiple controllers  
✅ **Scalability**: Can add caching or new data sources easily  

---

## Technical Stack

### Core Framework

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 1.8.0_331+ | Programming language |
| Spring Boot | 2.7.18 | Application framework |
| Spring Framework | 5.3.31 | Core framework |
| Spring Security | 5.7.x | Authentication & authorization |
| Spring Data JPA | 2.7.18 | Data access abstraction |

### Database & ORM

| Component | Version | Purpose |
|-----------|---------|---------|
| MySQL | 5.7+ or 8.0+ | Relational database |
| MySQL Connector | 5.1.9 | JDBC driver |
| Hibernate | 5.6.15 | ORM framework |

### Authentication & Security

| Component | Version | Purpose |
|-----------|---------|---------|
| JJWT | 0.9.1 | JWT token creation/validation |
| Spring Security | 5.7.x | Security configuration & filters |

### Utilities & Libraries

| Component | Version | Purpose |
|-----------|---------|---------|
| Lombok | 1.18.30 | Code generation (@Data, @Getter, @Setter) |
| Springdoc OpenAPI | 1.6.15 | Swagger/OpenAPI documentation |
| Jackson | included | JSON serialization/deserialization |

### Build & Dependency Management

| Component | Version | Purpose |
|-----------|---------|---------|
| Maven | 3.6.x+ | Build tool and dependency management |
| Spring Boot Maven Plugin | 2.7.18 | Build executable JAR |

---

## Design Patterns

### 1. MVC (Model-View-Controller) Pattern

**Implementation**:
- **Model**: JPA entities (@Entity classes)
- **View**: JSON responses (via Jackson serialization)
- **Controller**: REST endpoints (@RestController)

**Benefits**:
- Clear separation between data, logic, and presentation
- Standardized web application structure

### 2. Repository Pattern

**Implementation**:
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

**Benefits**:
- Abstracts database operations
- Easy to switch implementations (different databases)
- Testable with mock repositories

### 3. Service Layer Pattern

**Implementation**:
```java
@Service
public class UserService {
    private UserRepository userRepository;
    
    public User createUser(UserRequest request) {
        // Business logic here
    }
}
```

**Benefits**:
- Centralizes business logic
- Reusable across multiple controllers
- Easier to test

### 4. DTO (Data Transfer Object) Pattern

**Implementation**:
```java
@Data
public class UserRequest {
    private String username;
    private String password;
    private String role;
}
```

**Benefits**:
- Separates API contract from internal entity
- Flexible validation and transformation
- API versioning support

### 5. Dependency Injection Pattern

**Implementation**: Spring's @Autowired annotation

```java
@RestController
public class UserController {
    @Autowired
    private UserService userService;
}
```

**Benefits**:
- Loose coupling between components
- Easy testing with mock dependencies
- Configuration flexibility

### 6. Strategy Pattern (for Authorization)

**Implementation**: Spring Security's @PreAuthorize annotation

```java
@PreAuthorize("hasRole('ADMIN') or @recordService.isOwner(#id)")
public ResponseEntity<?> updateRecord(@PathVariable Long id) {
    // Authorization logic handled by Spring Security
}
```

**Benefits**:
- Flexible authorization rules
- Reusable across endpoints
- Centralized permission logic

### 7. Builder Pattern (Implicit)

**Implementation**: Lombok @Builder annotation

```java
@Data
@Builder
public class FinancialRecord {
    private Long id;
    private User user;
    private BigDecimal amount;
    // ...
}
```

**Benefits**:
- Clean object construction
- Optional parameters
- Readable code

---

## Data Model

### User Entity

**Purpose**: Store user account information and authentication data

**Schema**:
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Fields**:

| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login identifier |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| active | BOOLEAN | DEFAULT true | Account enabled/disabled |
| role | VARCHAR(20) | NOT NULL | VIEWER, ANALYST, or ADMIN |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation time |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last modification time |

**Indexes**:
```sql
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_active ON user(active);
```

### FinancialRecord Entity

**Purpose**: Store financial transactions

**Schema**:
```sql
CREATE TABLE financial_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    date DATE NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date)
);
```

**Fields**:

| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| user_id | BIGINT | FK, NOT NULL | Reference to user |
| amount | DECIMAL(15,2) | NOT NULL | Transaction amount |
| type | VARCHAR(50) | NOT NULL | "income" or "expense" |
| category | VARCHAR(100) | NOT NULL | Categorization |
| date | DATE | NOT NULL | Transaction date |
| notes | VARCHAR(500) | Nullable | Optional description |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last modification time |

**Indexes**:
```sql
CREATE INDEX idx_record_user ON financial_record(user_id);
CREATE INDEX idx_record_date ON financial_record(date);
CREATE INDEX idx_record_user_date ON financial_record(user_id, date);
CREATE INDEX idx_record_type ON financial_record(type);
CREATE INDEX idx_record_category ON financial_record(category);
```

### Entity Relationships

```
User (1) ──────── (Many) FinancialRecord
  ├─ One user can have multiple records
  ├─ Cascade delete: Deleting user deletes records
  └─ User ID is required foreign key
```

---

## Security Architecture

### Authentication Flow

```
┌─────────────────┐
│ Client Request  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│ AuthController.login()          │
│ - Validate credentials          │
│ - Verify username/password      │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│ UserService                     │
│ - Fetch user from database      │
│ - Compare password (BCrypt)     │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│ JwtUtil.generateToken()         │
│ - Create JWT with user info     │
│ - Sign with secret key          │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│ Return JWT Token to Client      │
│ - Token expires in 24 hours     │
└─────────────────────────────────┘
```

### Authorization Flow

```
┌──────────────────────┐
│ Incoming Request     │
│ Header: Authorization│
└────────┬─────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ JwtFilter.doFilterInternal()     │
│ - Extract token from header      │
│ - Validate token signature       │
│ - Verify token not expired       │
└────────┬─────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ Token Valid?                     │
└────────┬─────────────────────────┘
         │
    ┌────┴────┐
    │          │
   YES        NO
    │          │
    ▼          ▼
┌─────────┐  ┌──────────┐
│ Continue│  │ Return   │
│ Request │  │ 401 Error│
└────┬────┘  └──────────┘
     │
     ▼
┌────────────────────────────┐
│ @PreAuthorize annotation   │
│ - Check user role          │
│ - Verify permissions       │
└────────┬───────────────────┘
     │
 ┌───┴────┐
 │         │
YES       NO
 │         │
 ▼         ▼
┌──────┐ ┌──────────┐
│Allow │ │Return 403│
└──────┘ │Forbidden │
         └──────────┘
```

### Password Security

- **Algorithm**: BCrypt (Spring Security default)
- **Hash Function**: Adaptive hash function
- **Salt**: Automatically included in hash
- **Cost Factor**: 10 rounds (default)

**Password Validation**:
```java
// During registration
String hashedPassword = passwordEncoder.encode(plainPassword);

// During login
boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
```

### JWT Token Security

- **Algorithm**: HS256 (HMAC with SHA-256)
- **Payload Contains**:
  - `sub`: Username
  - `id`: User ID
  - `role`: User role
  - `iat`: Issue time
  - `exp`: Expiration time

- **Token Format**: `header.payload.signature`

**Example JWT**:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJhZG1pbiIsImlkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE2NDg1MzQ4MDAsImV4cCI6MTY0ODYyMTIwMH0.
AbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOp
```

---

## Access Control Design

### Role Definition

**VIEWER** (Read-Only User):
- Can view personal dashboard
- Can view own financial records
- Cannot create, edit, or delete records
- Typical Use Case: Family member with view-only access

**ANALYST** (Data Entry User):
- Can view personal dashboard
- Can create own financial records
- Can edit own records
- Can delete own records
- Can search and filter records
- Cannot manage users
- Typical Use Case: Primary user managing personal finances

**ADMIN** (System Administrator):
- Full system access
- Can manage all users
- Can view all records (any user)
- Can create/edit/delete any records
- Can enable/disable user accounts
- Typical Use Case: System administrator

### Authorization Mechanism

**Spring Security Configuration**:
```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // Global security configuration
}
```

**Method-Level Authorization**:
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getAllUsers() { }

@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
public ResponseEntity<?> createRecord() { }

@PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
public ResponseEntity<?> getDashboard() { }
```

**Custom Authorization Logic**:
```java
@PreAuthorize("hasRole('ADMIN') or @recordService.isOwner(#id, authentication)")
public ResponseEntity<?> updateRecord(@PathVariable Long id) { }
```

---

## Key Assumptions

### Business Assumptions

1. **Single Tenant System**
   - Each user account is independent
   - No data sharing between users (except admin view)
   - No multi-organization support
   - **Implication**: Authentication alone determines data visibility

2. **Financial Record Ownership**
   - Records belong exclusively to the user who created them
   - Only creator can edit/delete own records
   - Admin can override for all records
   - **Implication**: user_id is immutable after creation

3. **Single Currency**
   - System assumes single currency per instance
   - No currency conversion support
   - All amounts in same currency
   - **Implication**: No currency field needed in records

4. **Soft Delete Only**
   - Records are logically deleted (isDeleted flag)
   - Data never physically removed
   - Enables audit trail
   - **Implication**: Queries must check isDeleted = false

5. **Role Hierarchy**
   - ADMIN > ANALYST > VIEWER (permission hierarchy)
   - Users have exactly one role
   - Role changes require re-login
   - **Implication**: Efficient permission checks

### Technical Assumptions

1. **Stateless API**
   - All information needed in JWT token
   - No server-side session storage
   - Horizontal scalability enabled
   - **Implication**: Token contains essential user info

2. **Synchronous Processing**
   - All operations complete immediately
   - No background job processing
   - No message queues
   - **Implication**: Dashboard calculations done on-demand

3. **In-Memory Security**
   - JWT token valid until expiration
   - No token revocation list
   - Logout only client-side
   - **Implication**: Compromised tokens remain valid until expiration

4. **Database Consistency**
   - Strong consistency model
   - ACID transactions
   - No distributed transactions
   - **Implication**: Single MySQL database

5. **Default UTC Timezone**
   - All timestamps in UTC
   - Client handles timezone conversion
   - Dates stored as DATE type (no time component)
   - **Implication**: Time portions ignored for record dates

### Performance Assumptions

1. **Typical User Dataset**
   - Thousands of records per user
   - Hundreds of active users
   - Not optimized for millions of users
   - **Implication**: Current indexing strategy sufficient

2. **Dashboard Recalculation**
   - Calculated on-demand (not cached)
   - Last 6 months data retrieved fresh
   - No pre-aggregated summaries
   - **Implication**: Dashboard may be slow with large datasets

3. **Real-Time Updates**
   - No real-time notifications
   - No live dashboard updates
   - Client must poll for changes
   - **Implication**: Eventual consistency acceptable

---

## API Design Principles

### 1. RESTful Principles

**Resource-Oriented URLs**:
```
/api/records      (Collection)
/api/records/{id} (Resource)
```

**Standard HTTP Methods**:
- `GET`: Retrieve data
- `POST`: Create new resource
- `PUT`: Update entire resource
- `DELETE`: Remove resource

### 2. Request/Response Consistency

**Request Format**:
```json
{
  "field": "value"
}
```

**Response Format**:
```json
{
  "field": "value",
  "timestamp": "ISO-8601",
  "status": "HTTP-status"
}
```

### 3. Error Standardization

All errors return consistent structure:
```json
{
  "timestamp": "ISO-8601",
  "status": "HTTP-status",
  "error": "Error Type",
  "message": "Human-readable message",
  "fields": { "field": "error details" },
  "path": "/api/endpoint"
}
```

### 4. Pagination Standard

All list endpoints support:
- `page`: 0-indexed page number
- `size`: Items per page
- `sort`: Field,direction

### 5. Filtering Strategy

- **Type-based**: `/records/filter?type=expense`
- **Category-based**: `/records/category?category=Food`
- **Date range**: `/records/date-range?start=YYYY-MM-DD&end=YYYY-MM-DD`
- **Search**: `/records/search?q=keyword`

### 6. Versioning Strategy

- Version implicit in API structure (/api)
- Breaking changes would require /api/v2
- Currently no versioning needed

---

## Performance Considerations

### Database Optimization

**Indexes Created**:
```sql
-- User lookups
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_active ON user(active);

-- Record queries
CREATE INDEX idx_record_user ON financial_record(user_id);
CREATE INDEX idx_record_date ON financial_record(date);
CREATE INDEX idx_record_user_date ON financial_record(user_id, date);
CREATE INDEX idx_record_type ON financial_record(type);
CREATE INDEX idx_record_category ON financial_record(category);
```

**Query Optimization**:
- `@Query` annotations for complex queries
- Join Fetch for N+1 prevention
- Pagination for large result sets

### Caching Opportunities (Future)

1. **Token Caching**: Cache user roles/permissions during token lifetime
2. **Dashboard Caching**: Cache 6-month statistics for dashboard
3. **User Cache**: Cache user details for quick lookups

### Connection Pooling

**Default Hikari Connection Pool**:
- Pool Size: 10
- Max Lifetime: 30 minutes
- Idle Timeout: 10 minutes

---

## Scalability

### Current Limitations

1. **Single Database Instance**
   - No database replication
   - Single point of failure
   - Cannot distribute load

2. **Stateful JWT Processing**
   - Must verify token on every request
   - Cannot use distributed caching without changes

3. **Synchronous Processing**
   - Long-running operations block requests
   - No request queuing

### Future Scalability Enhancements

1. **Database**
   - Read replicas for reporting
   - Sharding by user ID
   - Connection pooling optimization

2. **Caching Layer**
   - Redis for token validation
   - Dashboard calculation caching
   - User data caching

3. **API Gateway**
   - Rate limiting
   - Request routing
   - Load balancing

4. **Asynchronous Processing**
   - Background jobs for aggregations
   - Message queues for notifications
   - Event sourcing for audit trail

5. **Monitoring & Observability**
   - Distributed tracing
   - Metrics collection
   - Health checks and alerting

---

## Design Decisions Rationale

### Why JWT Over Sessions?

**Chosen**: JWT  
**Reason**: Stateless, scalable, suitable for REST API

**Alternative**: Server-side sessions  
**Why Not**: Requires server state, harder to scale horizontally

### Why Soft Delete?

**Chosen**: Soft delete (isDeleted flag)  
**Reason**: Audit trail, data recovery, reporting history

**Alternative**: Physical delete  
**Why Not**: Permanent data loss, cannot reconstruct history

### Why Single Role Per User?

**Chosen**: One role per user  
**Reason**: Simpler authorization logic, faster permission checks

**Alternative**: Multiple roles per user  
**Why Not**: Added complexity, slower authorization

### Why Synchronous Dashboard?

**Chosen**: On-demand calculation  
**Reason**: Always current data, simpler architecture

**Alternative**: Pre-aggregated, asynchronous  
**Why Not**: Potential staleness, added complexity

---

## Deployment Architecture

### Development Environment
```
Local Machine
├─ Spring Boot Application (Port 8080)
├─ MySQL Database (Port 3306)
└─ Client Testing Tools (Postman, cURL)
```

### Production Considerations

1. **Environment Variables**
   - Database credentials
   - JWT secret key
   - CORS allowed origins
   - Debug logging level

2. **Security Hardening**
   - HTTPS/TLS for all traffic
   - Firewall rules
   - Database access restrictions
   - API key management

3. **Monitoring**
   - Application logs
   - Database query logs
   - Error tracking
   - Performance metrics

4. **Backup & Recovery**
   - Database backups
   - Recovery procedures
   - Disaster recovery plan

---

**Last Updated**: April 4, 2026
