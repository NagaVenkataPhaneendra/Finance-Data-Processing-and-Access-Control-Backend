# Setup & Installation Guide

A complete step-by-step guide to set up and run the Finance Dashboard application.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Project Configuration](#project-configuration)
4. [Build & Compilation](#build--compilation)
5. [Running the Application](#running-the-application)
6. [Verification](#verification)
7. [Troubleshooting](#troubleshooting)
8. [Next Steps](#next-steps)

---

## Prerequisites

Before starting, ensure you have the following installed on your system:

### Required Software

| Software | Version | Download |
|----------|---------|----------|
| Java (JDK) | 1.8.0_331+ | https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html |
| Maven | 3.6.x+ | https://maven.apache.org/download.cgi |
| MySQL | 5.7+ or 8.0+ | https://dev.mysql.com/downloads/mysql/ |
| Git (optional) | Latest | https://git-scm.com/downloads |

### System Requirements

- **RAM**: Minimum 2GB (4GB recommended)
- **Disk Space**: 1GB free space
- **Network**: Internet connection for Maven dependencies

### Verify Installations

```bash
# Check Java version
java -version
# Expected: java version "1.8.0_XXX"

# Check Maven version
mvn -version
# Expected: Apache Maven 3.6.x or higher

# Check MySQL version
mysql --version
# Expected: mysql Ver 8.0.x or 5.7.x
```

---

## Database Setup

### Option 1: Using SQL Script (Recommended)

The easiest way to set up the database is using the provided SQL script.

#### Step 1: Verify MySQL is Running

**Windows**:
```bash
# Open Command Prompt and check if MySQL service is running
sc query MySQL80
# Should return: SERVICE_NAME: MySQL80
#               STATE: 4 RUNNING
```

**macOS/Linux**:
```bash
# Check MySQL service
sudo systemctl status mysql
# Should show: active (running)
```

#### Step 2: Run Setup Script

```bash
# Navigate to project directory
cd c:\Users\phani\workspace\fincontrolhub

# Run the MySQL setup script
mysql -u root -p < MYSQL_SETUP.sql
# When prompted, enter your MySQL root password (default: root)
```

This script will:
- Create database: `finance_db`
- Create `user` table with proper schema
- Create `financial_record` table with proper schema
- Create default admin user (username: admin, password: admin123)
- Create necessary indexes for performance

**Verify**:
```bash
mysql -u root -p
# Enter password: root
mysql> USE finance_db;
mysql> SHOW TABLES;
# Should display: user, financial_record
mysql> SELECT COUNT(*) FROM user;
# Should show: 1 (admin user)
mysql> QUIT;
```

### Option 2: Manual Database Setup

If the script doesn't work, create the database manually:

#### Step 1: Connect to MySQL

```bash
mysql -u root -p
# Enter your MySQL root password
```

#### Step 2: Create Database and Tables

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS finance_db;

-- Use the database
USE finance_db;

-- Create User table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create FinancialRecord table
CREATE TABLE IF NOT EXISTS financial_record (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert default admin user
INSERT INTO user (username, password, active, role) 
VALUES ('admin', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy3SAMM', true, 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

-- Verify
SHOW TABLES;
SELECT * FROM user;
```

#### Step 3: Exit MySQL

```sql
EXIT;
```

---

## Project Configuration

### Verify and Update application.properties

The application configuration is located at:
```
src/main/resources/application.properties
```

#### Current Configuration:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.use_sql_comments=false

# Application Configuration
application.jwt.secret=your-secret-key-here-make-it-long-and-secure
application.jwt.expiration=86400000

# Springdoc OpenAPI / Swagger Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# Logging
logging.level.root=INFO
logging.level.com.finance=DEBUG
```

### Customize Configuration (if needed)

If your MySQL setup differs from defaults:

```bash
# Edit the file in your IDE or text editor
# Update these properties:

# If MySQL is on different host/port:
spring.datasource.url=jdbc:mysql://your-host:3306/finance_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8

# If MySQL credentials are different:
spring.datasource.username=your-username
spring.datasource.password=your-password

# If port 8080 is in use:
server.port=8081

# Change JWT secret (important for production):
application.jwt.secret=your-very-long-and-secure-secret-key-here
```

---

## Build & Compilation

### Clean Build

```bash
# Navigate to project directory
cd c:\Users\phani\workspace\fincontrolhub

# Clean and build the project
mvn clean install
```

**Expected Output**:
```
...
[INFO] --- maven-compiler-plugin:3.x.x:compile (default-compile) @ finance-dashboard ---
[INFO] Compiling 15 source files to target/classes
...
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXXs
[INFO] Finished at: 2026-04-04T14:xx:xxZ
```

### Skip Tests (Faster Build)

```bash
mvn clean install -DskipTests
```

This skips unit tests and speeds up build time.

### Rebuild Without Clean

```bash
# Faster rebuild if dependencies haven't changed
mvn install
```

### Troubleshoot Build Failures

If build fails with compilation errors:

```bash
# Clear Maven cache
mvn clean

# Update Maven plugins
mvn help:update-plugins

# Rebuild with verbose output
mvn clean install -X
```

Common Build Issues:

| Error | Solution |
|-------|----------|
| `Cannot find symbol` | Run `mvn clean install` - clears compiled classes |
| `Dependency not found` | Check internet, try `mvn dependency:purge-local-repository` |
| `Out of memory` | Increase Maven heap: `set MAVEN_OPTS=-Xmx1024m` |
| `Port already in use` | Change `server.port` in application.properties |

---

## Running the Application

### Option 1: Using Maven (Recommended for Development)

```bash
cd c:\Users\phani\workspace\fincontrolhub
mvn spring-boot:run
```

**Expected Output**:
```
2026-04-04 14:15:30.123  INFO 12345 --- [main] com.finance.FinanceApplication : Starting FinanceApplication
...
2026-04-04 14:15:40.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080
2026-04-04 14:15:40.500  INFO 12345 --- [main] com.finance.FinanceApplication : Started FinanceApplication in 10.345s
```

Application is ready when you see: `Tomcat started on port(s): 8080`

### Option 2: Using Java Directly (For Production)

```bash
# First, build the JAR
cd c:\Users\phani\workspace\fincontrolhub
mvn clean package -DskipTests

# Then run it
java -jar target/finance-dashboard-1.0.jar
```

### Option 3: Using IDE (VS Code / IntelliJ)

#### VS Code:

1. Install Extension: **Extension Pack for Java**
2. Open project in VS Code
3. Click **Run** > **Start Debugging** (F5)
4. Select **Java** as environment
5. Application starts with debugging enabled

#### IntelliJ IDEA:

1. Open project in IntelliJ
2. Right-click `FinanceApplication.java`
3. Select **Run 'FinanceApplication.main()'**
4. Or press **Shift+F10**

### Stopping the Application

- **Maven**: Press `Ctrl+C` in terminal
- **Java JAR**: Press `Ctrl+C` in terminal
- **IDE**: Click Stop button in Run panel

---

## Verification

### Step 1: Verify Server is Running

```bash
# Check if port 8080 is listening
netstat -ano | findstr :8080

# Or use curl
curl http://localhost:8080/swagger-ui.html
# Should return HTML page (not error)
```

### Step 2: Test API with cURL

#### Login Endpoint:

```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

**Expected Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN",
    "active": true
  },
  "expiresIn": 86400
}
```

#### Dashboard Endpoint (with Token):

```bash
# Replace YOUR_TOKEN_HERE with the token from login response
curl -X GET http://localhost:8080/api/dashboard/summary ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response**:
```json
{
  "totalIncome": 0,
  "totalExpenses": 0,
  "netBalance": 0,
  "categoryTotals": {},
  "expensesByCategory": {},
  "incomeByCategory": {},
  "last6Months": {
    "income": {},
    "expenses": {}
  },
  "recentTransactions": []
}
```

### Step 3: Access Swagger UI

Open browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

You should see:
- API documentation with all endpoints
- Try-it-out feature for testing
- Authorize button for JWT token

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: "Connection refused" or "Cannot connect to database"

**Symptoms**:
```
org.springframework.beans.factory.BeanCreationException: Error creating bean
Caused by: java.sql.SQLException: Access denied for user 'root'@'localhost'
```

**Solutions**:
1. Verify MySQL is running:
   ```bash
   mysql -u root -p
   ```
2. Check credentials in `application.properties`
3. Verify database exists:
   ```bash
   mysql -u root -p -e "SHOW DATABASES;"
   ```
4. If issues persist, restart MySQL:
   ```bash
   # Windows
   net stop MySQL80
   net start MySQL80
   ```

#### Issue 2: "Port 8080 already in use"

**Symptoms**:
```
ERROR in app: Failed to start netty. The socket address BindException: Address already in use: bind
```

**Solutions**:
1. Find what's using the port:
   ```bash
   netstat -ano | findstr :8080
   ```
2. Kill the process:
   ```bash
   taskkill /PID <PID> /F
   ```
3. Or use a different port:
   - Edit `application.properties`
   - Change `server.port=8081`

#### Issue 3: "Cannot resolve symbol" errors in IDE

**Solutions**:
1. Run Maven clean install:
   ```bash
   mvn clean install
   ```
2. Refresh IDE project
3. Clear IDE cache and restart

#### Issue 4: "SQL syntax error" during startup

**Symptoms**:
```
org.hibernate.tool.schema.spi.CommandAcceptanceException: Error executing DDL
```

**Solutions**:
1. Drop and recreate database:
   ```bash
   mysql -u root -p -e "DROP DATABASE finance_db;"
   mysql -u root -proot < MYSQL_SETUP.sql
   ```
2. Change Hibernate DDL setting in `application.properties`:
   ```properties
   spring.jpa.hibernate.ddl-auto=create-drop
   ```
3. Restart application

#### Issue 5: "Java version not supported"

**Symptoms**:
```
The Java version is not supported by this project
```

**Solutions**:
1. Check installed Java version:
   ```bash
   java -version
   ```
2. Install Java 1.8.x:
   - Download from Oracle website
   - Update `JAVA_HOME` environment variable
   - Restart terminal/IDE

#### Issue 6: OutOfMemory Exception

**Symptoms**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Solutions**:
1. Increase JVM heap size:
   ```bash
   # For Maven
   set MAVEN_OPTS=-Xmx1024m -Xms512m
   mvn spring-boot:run
   ```
2. For direct Java execution:
   ```bash
   java -Xmx1024m -jar target/finance-dashboard-1.0.jar
   ```

#### Issue 7: JWT Token Errors

**Symptoms**:
```
401 Unauthorized - Invalid or expired token
```

**Solutions**:
1. Re-login to get a fresh token:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   ```
2. Token expires after 24 hours by default
3. Update JWT secret in production:
   - Edit `application.properties`
   - Change `application.jwt.secret` to a secure value

### Getting Help

If you encounter issues not listed above:

1. Check logs in console output
2. Enable debug logging in `application.properties`:
   ```properties
   logging.level.com.finance=DEBUG
   ```
3. Check database tables exist:
   ```bash
   mysql -u root -p -e "USE finance_db; SHOW TABLES;"
   ```
4. Verify all prerequisites are installed and accessible

---

## Next Steps

After successful setup:

1. **Register Additional Users**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"analyst","password":"password123","role":"ANALYST"}'
   ```

2. **Create Financial Records**:
   - Login to get JWT token
   - Use POST `/api/records` to create records
   - Access Swagger UI for interactive testing

3. **Explore Dashboard**:
   - Access `/api/dashboard/summary` for analytics
   - View income/expense breakdowns
   - Check category-wise totals

4. **Test All Endpoints**:
   - Use Swagger UI at http://localhost:8080/swagger-ui.html
   - Test different user roles (VIEWER, ANALYST, ADMIN)
   - Verify access control works

5. **Review Documentation**:
   - See **API_REFERENCE.md** for complete endpoint details
   - See **ARCHITECTURE.md** for design decisions
   - See **TRADEOFFS.md** for design rationale

---

## Additional Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Spring Security**: https://spring.io/projects/spring-security
- **JWT Guide**: https://jwt.io/
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **Maven Guide**: https://maven.apache.org/guides/

---

**Last Updated**: April 4, 2026
