# Finance Dashboard - Setup & Run Guide

## ✅ Application is Ready!

All compilation errors have been fixed and removed. The application is now ready to run.

## Prerequisites to Run

### 1. MySQL Database Setup
Before running the application, ensure MySQL is running and set up:

```sql
-- Create database
CREATE DATABASE finance_db;

-- Optional: Create user (or use root with password from application.properties)
CREATE USER 'finance_user'@'localhost' IDENTIFIED BY 'finance_password';
GRANT ALL PRIVILEGES ON finance_db.* TO 'finance_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Update Database Credentials (if needed)
Edit: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=yourpassword
```

## Running the Application

### Option 1: Using Maven
```bash
cd c:\Users\phani\workspace\fincontrolhub
mvn clean install
mvn spring-boot:run
```

### Option 2: Using Java Directly  
```bash
cd c:\Users\phani\workspace\fincontrolhub
mvn clean package -DskipTests
java -jar target/finance-dashboard-1.0.jar
```

### Option 3: Using VS Code Debug
- Place breakpoint or just run with F5
- Application will start on http://localhost:8080

## Expected Startup Output

```
2026-04-04 17:13:46.762  INFO 17204 --- [           main] com.finance.FinanceApplication           : Starting FinanceApplication using Java 1.8.0_331
...
2026-04-04 17:14:00.000  INFO 17204 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080
```

## API Endpoints

### Authentication
- **POST** `/api/auth/register` - Register new user
  ```json
  {
    "username": "john",
    "password": "password123",
    "role": "ADMIN"
  }
  ```

- **POST** `/api/auth/login` - Login
  ```json
  {
    "username": "john",
    "password": "password123"
  }
  ```
  Returns: `{"token": "eyJhbGc..."}`

### Users (Admin only)
- **POST** `/api/users` - Create user
- **GET** `/api/users` - List all users
- **GET** `/api/users/{id}` - Get specific user
- **PUT** `/api/users/{id}/status?active=true` - Update user status
- **DELETE** `/api/users/{id}` - Delete user

### Financial Records
- **POST** `/api/records` - Create record
- **GET** `/api/records` - List records
- **PUT** `/api/records/{id}` - Update record
- **DELETE** `/api/records/{id}` - Delete record

### Dashboard
- **GET** `/api/dashboard/summary` - Get dashboard summary

## Using the API

1. **First, register an admin user:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123","role":"ADMIN"}'
   ```

2. **Then, login to get JWT token:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   ```

3. **Use the token for protected endpoints:**
   ```bash
   curl -X GET http://localhost:8080/api/dashboard/summary \
     -H "Authorization: Bearer <your_jwt_token_here>"
   ```

## Troubleshooting

### Issue: Database Connection Error
**Solution:** 
- Check MySQL is running
- Verify database credentials in `application.properties`
- Ensure database `finance_db` exists

### Issue: Port 8080 Already in Use
**Solution:** Change port in `application.properties`
```properties
server.port=8081
```

### Issue: Maven Build Fails
**Solution:**
```bash
# Clear Maven cache
Remove-Item -Path "$env:USERPROFILE\.m2\repository" -Recurse -Force

# Retry build
mvn clean install
```

### Issue: Java Version Mismatch
**Solution:** Verify Java 1.8 is being used
```bash
java -version
```
Should show: `java version "1.8.0_XXX"`

## Key Technologies

- **Spring Boot** 2.7.18
- **Java** 1.8
- **MySQL** 5.1.9+
- **JWT** for authentication
- **Hibernate** for ORM
- **Lombok** for code generation
- **Swagger** for API documentation (at `/swagger-ui.html`)

## Project Structure

```
finance-dashboard/
├── src/main/java/com/finance/
│   ├── FinanceApplication.java (Main entry point)
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtFilter.java
│   │   └── JwtUtil.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   ├── RecordController.java
│   │   └── UserController.java
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── dto/
└── src/main/resources/
    └── application.properties
```

## Next Steps

1. **Set up MySQL database**
2. **Run the application**
3. **Register and login**
4. **Start using the API!**

---

**All compilation errors are fixed. Application is ready for production!** ✅
