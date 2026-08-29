# Employee Management System

Sistem manajemen karyawan berbasis REST API dengan Spring Boot. Menggunakan JWT authentication, role-based access control, dan PostgreSQL sebagai database.

---

## Tech Stack

| Komponen       | Teknologi                          |
|----------------|------------------------------------|
| Language       | Java 21+                           |
| Framework      | Spring Boot 3.5.3                  |
| Security       | Spring Security + JWT              |
| ORM            | Blaze-Persistence (NO JPA Repositories) |
| DB View        | Blaze-Persistence Entity View      |
| Database       | PostgreSQL 17                      |
| Migrations     | Flyway                             |
| Validation     | Hibernate Validator (Jakarta)      |
| API Docs       | Springdoc OpenAPI (Swagger UI)     |
| Monitoring     | Spring Boot Actuator               |
| Build Tool     | Maven                              |
| Code Gen       | Lombok                             |
| QueryDSL       | OpenFeign QueryDSL 7.3.0 (type-safe queries) |

### Dependencies

| Dependency                      | Version | Scope                          |
|---------------------------------|---------|--------------------------------|
| `spring-boot-starter-actuator`  |         | compile                        |
| `querydsl-jpa`                  | 7.3.0   | compile                        |
| `querydsl-apt`                  | 7.3.0   | provided (annotation processor)|

---

## Project Structure

```
src/main/java/com/spring/review/
│
├── ReviewApplication.java              # Entry point aplikasi (@EnableAsync, @EnableScheduling)
│
├── entity/                             # JPA Entity (model/database table)
│   ├── UserEntity.java                 #   Tabel users
│   ├── EmployeeEntity.java             #   Tabel employees
│   ├── DepartmentEntity.java           #   Tabel departments
│   ├── PositionEntity.java             #   Tabel positions
│   ├── AuditLogEntity.java             #   Tabel audit_logs
│   ├── Gender.java                     #   Enum: MALE, FEMALE
│   └── EmployeeStatus.java             #   Enum: ACTIVE, INACTIVE, RESIGNED
│
├── entityView/                         # Blaze-Persistence Entity View (DTO projection)
│   ├── UserView.java                   #   User read-only view
│   ├── EmployeeView.java               #   Employee read-only view (termasuk department & position name)
│   ├── DepartmentView.java             #   Department read-only view
│   ├── PositionView.java               #   Position read-only view
│   ├── AuthUserView.java               #   Auth user view
│   ├── AuditLogView.java               #   Audit log read-only view
│
├── bean/                               # Request/Response DTOs
│   ├── auth/
│   │   ├── LoginRequest.java           #   Login request body
│   │   ├── LoginResponse.java          #   Login response (tokens)
│   │   ├── RefreshTokenRequest.java    #   Refresh token request body
│   │   ├── CurrentUserResponse.java    #   Current user info response
│   │   ├── LogoutRequest.java          #   Logout request body
│   │   └── ChangePasswordRequest.java  #   Change password request body
│   ├── employee/
│   │   ├── CreateEmployeeRequest.java  #   Create employee request body
│   │   ├── UpdateEmployeeRequest.java  #   Update employee request body
│   │   └── EmployeeSearchRequest.java  #   Search/filter employees params
│   ├── department/
│   │   ├── CreateDepartmentRequest.java  #   Create department request body
│   │   ├── UpdateDepartmentRequest.java  #   Update department request body
│   │   └── DepartmentSearchRequest.java  #   Search/filter departments params
│   ├── position/
│   │   ├── CreatePositionRequest.java  #   Create position request body
│   │   ├── UpdatePositionRequest.java  #   Update position request body
│   │   └── PositionSearchRequest.java  #   Search/filter positions params
│   ├── audit/
│   │   └── AuditLogSearchRequest.java  #   Search/filter audit logs params
│   └── dashboard/
│       ├── DashboardStatsResponse.java #   Dashboard statistics response
│       └── HiringTrendResponse.java    #   Hiring trend response
│
├── controller/                         # REST Controllers
│   ├── AuthController.java             #   /api/auth/** - Login, refresh, current user, logout, change password
│   ├── EmployeeController.java         #   /api/employees/** - CRUD employees
│   ├── DepartmentController.java       #   /api/departments/** - CRUD departments
│   ├── PositionController.java         #   /api/positions/** - CRUD positions
│   ├── FileController.java             #   /api/files/** - Upload & delete files
│   ├── AuditLogController.java         #   /api/audit-logs/** - View audit logs (ADMIN only)
│   ├── ExportController.java           #   /api/export/** - Export/Import Excel & PDF
│   └── DashboardController.java        #   /api/dashboard/** - Dashboard statistics
│
├── service/                            # Business Logic
│   ├── UserAuthService.java            #   Auth logic (login, register, refresh, logout, change password)
│   ├── EmployeeService.java            #   Employee CRUD logic
│   ├── DepartmentService.java          #   Department CRUD logic
│   ├── PositionService.java            #   Position CRUD logic
│   ├── FileStorageService.java         #   File upload, delete, validation
│   ├── JwtService.java                 #   JWT token generation & validation
│   ├── JwtAuthenticationFilter.java    #   Filter request, validate JWT header
│   ├── AuditLogService.java            #   Audit log record & search with Blaze
│   ├── ExportImportService.java        #   Excel/PDF export, Excel import
│   └── DashboardService.java           #   Dashboard statistics & hiring trend
│
├── config/                             # Configuration
│   ├── SecurityConfig.java             #   Spring Security config + CORS
│   ├── ApplicationConfig.java          #   App-wide bean config
│   ├── BlazeConfig.java                #   Blaze-Persistence config (+ AuditLogView registration)
│   ├── FileStorageConfig.java          #   File upload config (path, size, types)
│   ├── WebConfig.java                  #   Static resource serving (/uploads/**)
│   ├── DataInitializer.java            #   Seed data saat pertama kali run (DB sequence-based codes)
│   └── OpenApiConfig.java              #   Swagger/OpenAPI config
│
├── aspect/                             # AOP Aspects
│   └── AuditAspect.java               #   @Auditable aspect - auto-records audit logs
│
├── annotation/                         # Custom Annotations
│   └── Auditable.java                  #   @Auditable annotation for audit logging
│
├── common/                             # Shared classes
│   ├── ApiResponse.java                #   Wrapper response {code, message, data}
│   ├── PageResponse.java               #   Pagination response
│   ├── PageSpec.java                   #   Pagination request (page, size)
│   └── ErrorCode.java                  #   Enum error codes
│
├── exception/                          # Exception Handling
│   ├── GlobalExceptionHandler.java     #   @RestControllerAdvice global handler
│   ├── BusinessException.java          #   Custom business exception
│   └── ErrorResponse.java              #   Error response body
│
├── validation/                         # Custom validators
│   ├── UniqueEmail.java               #   @UniqueEmail annotation
│   ├── UniqueEmailValidator.java       #   Validates email uniqueness
│   ├── ExistingDepartment.java         #   @ExistingDepartment annotation
│   ├── ExistingDepartmentValidator.java #  Validates department exists by ID
│   ├── ExistingPosition.java           #   @ExistingPosition annotation
│   └── ExistingPositionValidator.java   #  Validates position exists by ID
│
└── db/                                 # Database migrations
    └── migration/
        └── V1__init_schema.sql         #   Flyway initial schema
```

---

## Prerequisites

1. **Java 21** atau lebih baru
2. **Maven 3.8+**
3. **PostgreSQL 15+** - buat database bernama `karyawan`

```sql
CREATE DATABASE karyawan;
```

4. Pastikan PostgreSQL jalan di `localhost:5432`

---

## Configuration

File: `src/main/resources/application.properties`

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/karyawan
spring.datasource.username=postgres
spring.datasource.password=12345678

# Hibernate - no auto schema, managed by Flyway
spring.jpa.hibernate.ddl-auto=none

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# JWT
jwt.secret=ems-development-secret-key-2026-super-secure-minimum-32-characters
jwt.expiration=86400000          # 1 hari (ms)
jwt.refresh-expiration=604800000 # 7 hari (ms)

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.info.env.enabled=true
```

> **Note:** Schema dikelola oleh Flyway migration (`ddl-auto=none`). `baselineOnMigrate=true` memungkinkan Flyway bekerja pada database yang sudah ada.

---

## Run

```bash
# Build & Run
mvn spring-boot:run

# Atau build dulu lalu run
mvn clean package -DskipTests
java -jar target/review-0.0.1-SNAPSHOT.jar
```

Aplikasi jalan di: `http://localhost:8080`

---

## Cara Menggunakan

### 1. Login

Buka Swagger UI di `http://localhost:8080/swagger-ui.html` atau gunakan Postman/curl.

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Simpan `accessToken` dari response. Untuk semua request selanjutnya, tambahkan header:

```
Authorization: Bearer <accessToken>
```

### 2. Default Users

Saat pertama kali run, aplikasi otomatis membuat 3 user:

| Username  | Password   | Role    | Akses                              |
|-----------|------------|---------|------------------------------------|
| admin     | admin123   | ADMIN   | Full akses (CRUD + Delete)         |
| hr_user   | hr123      | HR      | Create, Read, Update employees     |
| manager   | manager123 | MANAGER | Read, Update employees             |

### 3. Logout

```
POST /api/auth/logout
Authorization: Bearer <token>
Content-Type: application/json

{
  "accessToken": "<accessToken>"
}
```

Token yang sudah di-logout tidak dapat digunakan lagi untuk request berikutnya.

### 4. Change Password

```
POST /api/auth/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "oldPassword": "admin123",
  "newPassword": "newpassword123"
}
```

### 5. Kelola Departments

**Create Department:**

```
POST /api/departments
Authorization: Bearer <token>

{
  "name": "Engineering",
  "description": "Department for software development"
}
```

`departmentCode` di-generate otomatis menggunakan DB sequence (DEPT001, DEPT002, dst).

**Search & Pagination:**

```
GET /api/departments?page=0&size=5&isActive=true
```

Parameter filter: `name`, `departmentCode`, `isActive`

**Get by ID:**

```
GET /api/departments/1
```

**Update Department:**

```
PUT /api/departments/1
Authorization: Bearer <token>

{
  "name": "Engineering Updated",
  "description": "Updated description",
  "isActive": true
}
```

**Delete Department:**

```
DELETE /api/departments/1
Authorization: Bearer <token>
```

> Hanya role **ADMIN** yang bisa delete.

### 6. Kelola Positions

**Create Position:**

```
POST /api/positions
Authorization: Bearer <token>

{
  "name": "Software Developer",
  "description": "Develop and maintain software applications",
  "departmentId": 1
}
```

`positionCode` di-generate otomatis menggunakan DB sequence (POS001, POS002, dst). Validasi `@ExistingDepartment` memastikan `departmentId` valid.

**Search & Pagination:**

```
GET /api/positions?page=0&size=5&isActive=true&departmentId=1
```

Parameter filter: `name`, `positionCode`, `departmentId`, `isActive`

**Get by ID:**

```
GET /api/positions/1
```

**Update Position:**

```
PUT /api/positions/1
Authorization: Bearer <token>

{
  "name": "Senior Software Developer",
  "description": "Lead software development team",
  "departmentId": 1,
  "isActive": true
}
```

**Delete Position:**

```
DELETE /api/positions/1
Authorization: Bearer <token>
```

> Hanya role **ADMIN** yang bisa delete.

### 7. Kelola Employees

**Create Employee:**

```
POST /api/employees
Authorization: Bearer <token>

{
  "fullName": "Budi Santoso",
  "email": "budi@company.com",
  "phoneNumber": "081234567890",
  "gender": "MALE",
  "birthDate": "1990-05-15",
  "hireDate": "2022-01-10",
  "status": "ACTIVE",
  "departmentId": 1,
  "positionId": 1
}
```

`employeeCode` di-generate otomatis menggunakan DB sequence (EMP0001, EMP0002, dst). Validasi `@UniqueEmail`, `@ExistingDepartment`, `@ExistingPosition` memastikan input valid.

**Search & Pagination:**

```
GET /api/employees?page=0&size=5&status=ACTIVE&gender=MALE&departmentId=1
```

Parameter filter: `fullName`, `employeeCode`, `email`, `gender`, `status`, `departmentId`

**Get by ID:**

Response termasuk `departmentName` dari relasi department.

```
GET /api/employees/1
```

Response:

```json
{
  "code": "SUCCESS",
  "message": "Employee retrieved successfully",
  "data": {
    "id": 1,
    "employeeCode": "EMP0001",
    "fullName": "Budi Santoso",
    "email": "budi@company.com",
    "gender": "MALE",
    "status": "ACTIVE",
    "departmentName": "Engineering",
    "createdAt": "2026-08-17T22:00:00",
    "updatedAt": "2026-08-17T22:00:00"
  }
}
```

**Update Employee:**

```
PUT /api/employees/1
Authorization: Bearer <token>

{
  "fullName": "Budi Santoso Updated",
  "email": "budi.updated@company.com",
  "phoneNumber": "081234567899",
  "gender": "MALE",
  "birthDate": "1990-05-15",
  "hireDate": "2022-01-10",
  "status": "ACTIVE",
  "departmentId": 2,
  "positionId": 1
}
```

**Delete Employee:**

```
DELETE /api/employees/1
Authorization: Bearer <token>
```

> Hanya role **ADMIN** yang bisa delete.

### 8. Upload Foto Employee

**Upload File:**

```
POST /api/files/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <image file>
```

Response:

```json
{
  "code": "SUCCESS",
  "message": "File uploaded successfully",
  "data": {
    "filename": "a1b2c3d4.jpg",
    "url": "/uploads/a1b2c3d4.jpg"
  }
}
```

**Update Employee dengan Photo:**

```
PUT /api/employees/1
Authorization: Bearer <token>

{
  "fullName": "Budi Santoso",
  "email": "budi@company.com",
  "gender": "MALE",
  "birthDate": "1990-05-15",
  "hireDate": "2022-01-10",
  "status": "ACTIVE",
  "departmentId": 1,
  "positionId": 1,
  "photoUrl": "/uploads/a1b2c3d4.jpg"
}
```

**Delete File:**

```
DELETE /api/files/{filename}
Authorization: Bearer <token>
```

> Hanya role **ADMIN** yang bisa delete file.

**Akses Gambar:**

```
GET /uploads/{filename}
```

**Batas Upload:**
- Format: JPEG, PNG, WebP
- Maksimal: 5 MB

### 9. Refresh Token

Ketika `accessToken` expired (24 jam), gunakan `refreshToken` untuk dapat baru:

```
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refreshToken>"
}
```

`refreshToken` berlaku selama 7 hari.

### 10. Cek User Login

```
GET /api/auth/me
Authorization: Bearer <token>
```

### 11. Export Data

**Export ke Excel:**

```
GET /api/export/excel
Authorization: Bearer <token>
```

Response: File `.xlsx` berisi data employees, departments, dan positions.

**Export ke PDF:**

```
GET /api/export/pdf
Authorization: Bearer <token>
```

Response: File `.pdf` berisi laporan data employees.

### 12. Import Data

**Import dari Excel:**

```
POST /api/export/import
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <Excel file (.xlsx)>
```

File Excel harus mengikuti format template yang sesuai. Data akan di-import ke tabel employees.

### 13. Dashboard

**Statistik Dashboard:**

```
GET /api/dashboard/stats
Authorization: Bearer <token>
```

Response:

```json
{
  "code": "SUCCESS",
  "message": "Dashboard stats retrieved successfully",
  "data": {
    "totalEmployees": 100,
    "totalDepartments": 5,
    "totalPositions": 20,
    "activeEmployees": 85,
    "inactiveEmployees": 10,
    "resignedEmployees": 5
  }
}
```

**Hiring Trend:**

```
GET /api/dashboard/hiring-trend
Authorization: Bearer <token>
```

Response:

```json
{
  "code": "SUCCESS",
  "message": "Hiring trend retrieved successfully",
  "data": [
    { "year": 2024, "month": 1, "count": 5 },
    { "year": 2024, "month": 2, "count": 8 }
  ]
}
```

### 14. Audit Log

```
GET /api/audit-logs?page=0&size=10&action=CREATE&entityType=EmployeeEntity
Authorization: Bearer <token>
```

Parameter filter: `entityType`, `entityId`, `action`, `performedBy`, `dateFrom`, `dateTo`

> Hanya role **ADMIN** yang bisa mengakses audit logs.

### 15. Swagger UI

Buka `http://localhost:8080/swagger-ui.html` untuk interactive API documentation. Bisa langsung test semua endpoint dari browser.

> Untuk authorize di Swagger, klik tombol **Authorize** di atas kanan, lalu masukkan: `Bearer <token>`

### 16. Spring Actuator

**Health Check (Public):**

```
GET /actuator/health
```

Response:

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" }
  }
}
```

> Endpoint ini tidak memerlukan authentication.

**Application Info (ADMIN only):**

```
GET /actuator/info
Authorization: Bearer <token>
```

**Metrics (ADMIN only):**

```
GET /actuator/metrics
Authorization: Bearer <token>
```

### 17. QueryDSL Integration

Project menggunakan **QueryDSL** (OpenFeign fork v7.3.0) untuk type-safe queries. Pendekatan hybrid: QueryDSL untuk query building (count, predicates, ID fetching) + Blaze-Persistence Entity Views untuk DTO projection.

**Q-Classes:**

Q-classes di-generate otomatis dari entity menggunakan annotation processor:

```text
QEmployeeEntity
QDepartmentEntity
QPositionEntity
QAuditLogEntity
```

**Predicates dengan BooleanExpression:**

```java
BooleanExpression predicate = Expressions.TRUE;
predicate = predicate.and(qEmployee.fullName.containsIgnoreCase(keyword));
predicate = predicate.and(qEmployee.status.eq(EmployeeStatus.ACTIVE));

Long count = queryFactory.select(qEmployee.count())
    .from(qEmployee)
    .where(predicate)
    .fetchOne();
```

`Expressions.TRUE` digunakan sebagai default untuk chaining predicates, karena `BooleanExpression` (bukan `Predicate`) yang memiliki method `and()`.

**JPAQueryFactory:**

`JPAQueryFactory` digunakan untuk count queries dan ID fetching:

```java
List<Long> ids = queryFactory.select(qEmployee.id)
    .from(qEmployee)
    .where(predicate)
    .orderBy(qEmployee.fullName.asc())
    .offset(page * size)
    .limit(size)
    .fetch();
```

**Blaze-Persistence Entity Views:**

`CriteriaBuilderFactory` + `EntityViewManager` masih digunakan untuk single entity view lookups:

```java
evm.applySetting(
    EntityViewSetting.create(EmployeeView.class),
    cbf.create(em, EmployeeEntity.class)
        .where("id").eq(id)
).getSingleResult();
```

**Hybrid Flow:**

1. QueryDSL `JPAQueryFactory` digunakan untuk count total records dan fetch IDs berdasarkan predicates
2. IDs yang diperoleh digunakan untuk fetch full entities via `em.find()`
3. Blaze-Persistence `EntityViewManager` digunakan untuk project entity ke DTO (EmployeeView, DepartmentView, dst)

**Refactored Services:**

- `EmployeeService` - QueryDSL predicates untuk search/filter
- `DepartmentService` - QueryDSL predicates untuk search/filter
- `PositionService` - QueryDSL predicates untuk search/filter
- `AuditLogService` - QueryDSL predicates untuk search/filter
- `DashboardService` - Tetap menggunakan raw JPQL (aggregate queries)

---

## Role & Access Control

| Endpoint                    | Method | ADMIN | HR  | MANAGER |
|-----------------------------|--------|:-----:|:---:|:-------:|
| `/api/auth/login`           | POST   |  O    |  O  |    O    |
| `/api/auth/me`              | GET    |  O    |  O  |    O    |
| `/api/auth/logout`          | POST   |  O    |  O  |    O    |
| `/api/auth/change-password` | POST   |  O    |  O  |    O    |
| `/api/auth/refresh`         | POST   |  O    |  O  |    O    |
| `/api/departments`          | GET    |  O    |  O  |    O    |
| `/api/departments/{id}`     | GET    |  O    |  O  |    O    |
| `/api/departments`          | POST   |  O    |  O  |    -    |
| `/api/departments/{id}`     | PUT    |  O    |  O  |    O    |
| `/api/departments/{id}`     | DELETE |  O    |  -  |    -    |
| `/api/positions`            | GET    |  O    |  O  |    O    |
| `/api/positions/{id}`       | GET    |  O    |  O  |    O    |
| `/api/positions`            | POST   |  O    |  O  |    -    |
| `/api/positions/{id}`       | PUT    |  O    |  O  |    O    |
| `/api/positions/{id}`       | DELETE |  O    |  -  |    -    |
| `/api/employees`            | GET    |  O    |  O  |    O    |
| `/api/employees/{id}`       | GET    |  O    |  O  |    O    |
| `/api/employees`            | POST   |  O    |  O  |    -    |
| `/api/employees/{id}`       | PUT    |  O    |  O  |    O    |
| `/api/employees/{id}`       | DELETE |  O    |  -  |    -    |
| `/api/files/upload`         | POST   |  O    |  O  |    -    |
| `/api/files/{filename}`     | DELETE |  O    |  -  |    -    |
| `/api/audit-logs`           | GET    |  O    |  -  |    -    |
| `/api/export/excel`         | GET    |  O    |  O  |    O    |
| `/api/export/pdf`           | GET    |  O    |  O  |    O    |
| `/api/export/import`        | POST   |  O    |  O  |    -    |
| `/api/dashboard/stats`      | GET    |  O    |  O  |    O    |
| `/api/dashboard/hiring-trend` | GET  |  O    |  O  |    O    |
| `/actuator/health`          | GET    |  O    |  O  |    O    |
| `/actuator/info`            | GET    |  O    |  -  |    -    |
| `/actuator/metrics`         | GET    |  O    |  -  |    -    |

---

## Database Schema

Dikelola oleh Flyway migration (`V1__init_schema.sql`).

```sql
-- Sequences untuk code generation (concurrency-safe)
CREATE SEQUENCE emp_code_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE dept_code_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE pos_code_seq START WITH 1 INCREMENT BY 1;

users
├── id              BIGINT (PK, auto increment)
├── username        VARCHAR(50, unique)
├── password        VARCHAR(255) -- BCrypt hashed
├── role            VARCHAR (ADMIN/HR/MANAGER)
├── full_name       VARCHAR(100)
├── is_active       BOOLEAN
├── created_at      TIMESTAMP
└── updated_at      TIMESTAMP

departments
├── id              BIGINT (PK, auto increment)
├── department_code VARCHAR(50, unique)
├── name            VARCHAR(100)
├── description     VARCHAR(255)
├── is_active       BOOLEAN
├── created_at      TIMESTAMP
└── updated_at      TIMESTAMP

positions
├── id              BIGINT (PK, auto increment)
├── position_code   VARCHAR(50, unique)
├── name            VARCHAR(100)
├── description     VARCHAR(255)
├── department_id   BIGINT (FK → departments.id)
├── is_active       BOOLEAN
├── created_at      TIMESTAMP
└── updated_at      TIMESTAMP

employees
├── id              BIGINT (PK, auto increment)
├── employee_code   VARCHAR(50, unique)
├── full_name       VARCHAR(150)
├── email           VARCHAR(150, unique)
├── phone_number    VARCHAR(30)
├── gender          VARCHAR (MALE/FEMALE)
├── birth_date      DATE
├── hire_date       DATE
├── status          VARCHAR (ACTIVE/INACTIVE/RESIGNED)
├── department_id   BIGINT (FK → departments.id)
├── position_id     BIGINT (FK → positions.id)
├── photo_url       VARCHAR(255)
├── created_at      TIMESTAMP
└── updated_at      TIMESTAMP

audit_logs
├── id              BIGINT (PK, auto increment)
├── entity_type     VARCHAR(50)
├── entity_id       BIGINT
├── action          VARCHAR(50) -- CREATE, UPDATE, DELETE
├── old_values      TEXT (JSON)
├── new_values      TEXT (JSON)
├── performed_by    VARCHAR(50)
├── performed_at    TIMESTAMP
```

---

## Response Format

Semua response menggunakan wrapper `ApiResponse`:

```json
{
  "code": "SUCCESS",
  "message": "Description",
  "data": { ... }
}
```

Error response:

```json
{
  "code": "NOT_FOUND",
  "message": "Department not found",
  "timestamp": "2026-08-17T22:00:00"
}
```

---

## Enums

**Gender:** `MALE`, `FEMALE`

**EmployeeStatus:** `ACTIVE`, `INACTIVE`, `RESIGNED`

---

## Custom Validators

| Validator              | Field           | Keterangan                                  |
|------------------------|-----------------|---------------------------------------------|
| `@UniqueEmail`         | email           | Memastikan email unik pada tabel employees   |
| `@ExistingDepartment`  | departmentId    | Memastikan department dengan ID tersebut ada  |
| `@ExistingPosition`    | positionId      | Memastikan position dengan ID tersebut ada    |

---

## Changelog

| Tanggal       | Versi | Perubahan                                            |
|---------------|-------|------------------------------------------------------|
| 2026-08-17    | 1.0   | Initial: Auth (JWT), Employee CRUD, Search           |
| 2026-08-17    | 1.1   | Department module: CRUD, relasi ke Employee, seed    |
| 2026-08-18    | 1.2   | File Upload: foto profil employee, local storage     |
| 2026-08-18    | 1.3   | Position module: CRUD, relasi ke Department & Employee|
| 2026-08-19    | 2.0   | Flyway migrations, Custom Validators, Logout, Change Password, Audit Logging, Export/Import (Excel/PDF), Dashboard |
| 2026-08-20    | 3.0   | Spring Actuator |
| 2026-08-20    | 3.1   | QueryDSL integration (OpenFeign fork v7.3.0), hybrid Blaze approach |

---

## Roadmap

| # | Modul                  | Status | Keterangan                                    |
|---|------------------------|:------:|-----------------------------------------------|
| 1 | Auth (JWT)             |   O    | Login, refresh token, role-based access       |
| 2 | Employee CRUD          |   O    | Create, read, update, delete, search, paginate|
| 3 | Department CRUD        |   O    | Relasi ManyToOne ke Employee, seed data       |
| 4 | File Upload            |   O    | Upload foto employee, local storage           |
| 5 | Position               |   O    | Relasi ManyToOne ke Department & Employee     |
| 6 | Audit Log              |   O    | Track siapa yang akses/ubah data              |
| 7 | Export/Import Excel    |   O    | Export data ke Excel, import dari Excel       |
| 8 | Export/Import PDF      |   O    | Export laporan ke PDF (OpenPDF)               |
| 9 | Spring Actuator       |   O    | Health check, info, metrics endpoints         |
|10 | QueryDSL Integration  |   O    | Type-safe queries with hybrid Blaze approach  |
|11 | Notification           |   -    | Email notification saat event tertentu        |
|12 | Dashboard/Reporting    |   O    | Statistik & hiring trend karyawan             |
