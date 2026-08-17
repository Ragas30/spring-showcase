# Employee Management System

Sistem manajemen karyawan berbasis REST API dengan Spring Boot. Menggunakan JWT authentication, role-based access control, dan PostgreSQL sebagai database.

---

## Tech Stack

| Komponen       | Teknologi                          |
|----------------|------------------------------------|
| Language       | Java 21+                           |
| Framework      | Spring Boot 3.5.3                  |
| Security       | Spring Security + JWT              |
| ORM            | Spring Data JPA + Hibernate        |
| DB View        | Blaze-Persistence Entity View      |
| Database       | PostgreSQL 17                      |
| Validation     | Hibernate Validator (Jakarta)      |
| API Docs       | Springdoc OpenAPI (Swagger UI)     |
| Build Tool     | Maven                              |
| Code Gen       | Lombok                             |

---

## Project Structure

```
src/main/java/com/spring/review/
│
├── ReviewApplication.java              # Entry point aplikasi
│
├── entity/                             # JPA Entity (model/database table)
│   ├── UserEntity.java                 #   Tabel users
│   ├── EmployeeEntity.java             #   Tabel employees
│   ├── DepartmentEntity.java           #   Tabel departments
│   ├── PositionEntity.java             #   Tabel positions
│   ├── Gender.java                     #   Enum: MALE, FEMALE
│   └── EmployeeStatus.java             #   Enum: ACTIVE, INACTIVE, RESIGNED
│
├── entityView/                         # Blaze-Persistence Entity View (DTO projection)
│   ├── UserView.java                   #   User read-only view
│   ├── EmployeeView.java               #   Employee read-only view (termasuk department & position name)
│   ├── DepartmentView.java             #   Department read-only view
│   ├── PositionView.java               #   Position read-only view
│   └── AuthUserView.java               #   Auth user view
│
├── bean/                               # Request/Response DTOs
│   ├── auth/
│   │   ├── LoginRequest.java           #   Login request body
│   │   ├── LoginResponse.java          #   Login response (tokens)
│   │   ├── RefreshTokenRequest.java    #   Refresh token request body
│   │   └── CurrentUserResponse.java    #   Current user info response
│   ├── employee/
│   │   ├── CreateEmployeeRequest.java  #   Create employee request body
│   │   ├── UpdateEmployeeRequest.java  #   Update employee request body
│   │   └── EmployeeSearchRequest.java  #   Search/filter employees params
│   ├── department/
│   │   ├── CreateDepartmentRequest.java  #   Create department request body
│   │   ├── UpdateDepartmentRequest.java  #   Update department request body
│   │   └── DepartmentSearchRequest.java  #   Search/filter departments params
│   └── position/
│       ├── CreatePositionRequest.java  #   Create position request body
│       ├── UpdatePositionRequest.java  #   Update position request body
│       └── PositionSearchRequest.java  #   Search/filter positions params
│
├── controller/                         # REST Controllers
│   ├── AuthController.java             #   /api/auth/** - Login, refresh, current user
│   ├── EmployeeController.java         #   /api/employees/** - CRUD employees
│   ├── DepartmentController.java       #   /api/departments/** - CRUD departments
│   ├── PositionController.java         #   /api/positions/** - CRUD positions
│   └── FileController.java             #   /api/files/** - Upload & delete files
│
├── service/                            # Business Logic
│   ├── UserAuthService.java            #   Auth logic (login, register, refresh)
│   ├── EmployeeService.java            #   Employee CRUD logic
│   ├── DepartmentService.java          #   Department CRUD logic
│   ├── PositionService.java            #   Position CRUD logic
│   ├── FileStorageService.java         #   File upload, delete, validation
│   ├── JwtService.java                 #   JWT token generation & validation
│   └── JwtAuthenticationFilter.java    #   Filter request, validate JWT header
│
├── config/                             # Configuration
│   ├── SecurityConfig.java             #   Spring Security config + CORS
│   ├── ApplicationConfig.java          #   App-wide bean config
│   ├── BlazeConfig.java                #   Blaze-Persistence config
│   ├── FileStorageConfig.java          #   File upload config (path, size, types)
│   ├── WebConfig.java                  #   Static resource serving (/uploads/**)
│   ├── DataInitializer.java            #   Seed data saat pertama kali run
│   └── OpenApiConfig.java              #   Swagger/OpenAPI config
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
└── validation/                         # Custom validators (empty, reserved for future)
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

# Hibernate - auto create/update schema
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=ems-development-secret-key-2026-super-secure-minimum-32-characters
jwt.expiration=86400000          # 1 hari (ms)
jwt.refresh-expiration=604800000 # 7 hari (ms)
```

> **Note:** `ddl-auto=update` otomatis membuat/mengupdate schema. Untuk production, gunakan `validate` atau `none`.

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

### 3. Kelola Departments

**Create Department:**

```
POST /api/departments
Authorization: Bearer <token>

{
  "name": "Engineering",
  "description": "Department for software development"
}
```

`departmentCode` di-generate otomatis (DEPT001, DEPT002, dst).

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

### 4. Kelola Positions

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

`positionCode` di-generate otomatis (POS001, POS002, dst).

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

### 5. Kelola Employees

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
  "departmentId": 1
}
```

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
  "departmentId": 2
}
```

**Delete Employee:**

```
DELETE /api/employees/1
Authorization: Bearer <token>
```

> Hanya role **ADMIN** yang bisa delete.

### 6. Upload Foto Employee

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

### 7. Refresh Token

Ketika `accessToken` expired (24 jam), gunakan `refreshToken` untuk dapat baru:

```
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refreshToken>"
}
```

`refreshToken` berlaku selama 7 hari.

### 8. Cek User Login

```
GET /api/auth/me
Authorization: Bearer <token>
```

### 9. Swagger UI

Buka `http://localhost:8080/swagger-ui.html` untuk interactive API documentation. Bisa langsung test semua endpoint dari browser.

> Untuk authorize di Swagger, klik tombol **Authorize** di atas kanan, lalu masukkan: `Bearer <token>`

---

## Role & Access Control

| Endpoint                | Method | ADMIN | HR  | MANAGER |
|-------------------------|--------|:-----:|:---:|:-------:|
| `/api/auth/login`       | POST   |  O    |  O  |    O    |
| `/api/auth/me`          | GET    |  O    |  O  |    O    |
| `/api/departments`      | GET    |  O    |  O  |    O    |
| `/api/departments/{id}` | GET    |  O    |  O  |    O    |
| `/api/departments`      | POST   |  O    |  O  |    -    |
| `/api/departments/{id}` | PUT    |  O    |  O  |    O    |
| `/api/departments/{id}` | DELETE |  O    |  -  |    -    |
| `/api/positions`        | GET    |  O    |  O  |    O    |
| `/api/positions/{id}`   | GET    |  O    |  O  |    O    |
| `/api/positions`        | POST   |  O    |  O  |    -    |
| `/api/positions/{id}`   | PUT    |  O    |  O  |    O    |
| `/api/positions/{id}`   | DELETE |  O    |  -  |    -    |
| `/api/employees`        | GET    |  O    |  O  |    O    |
| `/api/employees/{id}`   | GET    |  O    |  O  |    O    |
| `/api/employees`        | POST   |  O    |  O  |    -    |
| `/api/employees/{id}`   | PUT    |  O    |  O  |    O    |
| `/api/employees/{id}`   | DELETE |  O    |  -  |    -    |
| `/api/files/upload`     | POST   |  O    |  O  |    -    |
| `/api/files/{filename}` | DELETE |  O    |  -  |    -    |

---

## Database Schema

```
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

## Changelog

| Tanggal       | Versi | Perubahan                                            |
|---------------|-------|------------------------------------------------------|
| 2026-08-17    | 1.0   | Initial: Auth (JWT), Employee CRUD, Search           |
| 2026-08-17    | 1.1   | Department module: CRUD, relasi ke Employee, seed    |
| 2026-08-18    | 1.2   | File Upload: foto profil employee, local storage     |
| 2026-08-18    | 1.3   | Position module: CRUD, relasi ke Department & Employee|

---

## Roadmap

| # | Modul                  | Status | Keterangan                                    |
|---|------------------------|:------:|-----------------------------------------------|
| 1 | Auth (JWT)             |   O    | Login, refresh token, role-based access       |
| 2 | Employee CRUD          |   O    | Create, read, update, delete, search, paginate|
| 3 | Department CRUD        |   O    | Relasi ManyToOne ke Employee, seed data       |
| 4 | File Upload            |   O    | Upload foto employee, local storage           |
| 5 | Position               |   O    | Relasi ManyToOne ke Department & Employee     |
| 6 | Audit Log              |   -    | Track siapa yang akses/ubah data              |
| 7 | Export/Import Excel    |   -    | Export data ke Excel                          |
| 8 | Export/Import PDF      |   -    | Export laporan ke PDF                         |
| 9 | Notification           |   -    | Email notification saat event tertentu        |
|10 | Dashboard/Reporting    |   -    | Statistik & laporan karyawan                  |
