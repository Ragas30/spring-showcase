# EMS Development Roadmap

## Step-by-Step Implementation Guide

---

# Project Goal

Membangun Employee Management System menggunakan:

* Spring Boot 3.5
* PostgreSQL
* Spring Security
* JWT
* Hibernate
* Blaze Persistence
* Entity View
* Bean Validation
* Global Exception Handler

Tanpa:

* Repository
* DAO

Menggunakan struktur:

```text
com.spring.review

├── bean
├── common
├── config
├── controller
├── entity
├── entityView
├── exception
├── service
├── validation
```

---

# PHASE 1 — PROJECT FOUNDATION

Tujuan:

Menyiapkan fondasi aplikasi.

---

## STEP 1

Buat project Spring Boot.

Dependencies:

```text
Spring Web
Spring Security
Validation
Spring Data JPA
PostgreSQL Driver
Lombok
Blaze Persistence
Swagger
JWT
```

Status:

```text
✓ DONE
```

---

## STEP 2

Konfigurasi database.

application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/karyawan
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Status:

```text
✓ DONE
```

---

## STEP 3

Buat database PostgreSQL.

```sql
CREATE DATABASE karyawan;
```

Status:

```text
✓ DONE
```

---

## STEP 4

Buat struktur package.

```text
bean
common
config
controller
entity
entityView
exception
service
validation
```

Status:

```text
✓ DONE
```

---

## STEP 5

Buat common class.

```text
common

├── ApiResponse
├── PageResponse
└── PageSpec
```

Status:

```text
✓ DONE
```

---

## STEP 6

Buat exception handling.

```text
exception

├── BusinessException
├── ErrorResponse
└── GlobalExceptionHandler
```

Status:

```text
✓ DONE
```

---

## STEP 7

Buat SecurityConfig sementara.

Tujuan:

Mematikan login bawaan Spring Security.

Status:

```text
✓ DONE
```

---

## STEP 8

Buat endpoint test.

```text
GET /test
```

Pastikan:

```json
{
  "code": "TEST_ERROR",
  "message": "...",
  "timestamp": "..."
}
```

Status:

```text
✓ DONE
```

---

# PHASE 2 — AUTHENTICATION

Tujuan:

Membuat login berbasis JWT.

---

## STEP 9

Buat UserEntity.

Lokasi:

```text
entity/UserEntity.java
```

Field:

```text
id
username
password
role
createdAt
updatedAt
```

Expected Result:

```text
users table otomatis dibuat
```

Status:

```text
⬜ TODO
```

---

## STEP 10

Buat UserView.

Lokasi:

```text
entityView/UserView.java
```

Field:

```text
id
username
role
```

Status:

```text
⬜ TODO
```

---

## STEP 11

Buat bean auth.

```text
bean/auth

├── LoginRequest
├── LoginResponse
```

Status:

```text
⬜ TODO
```

---

## STEP 12

Buat PasswordEncoder Bean.

Lokasi:

```text
config/CommonBeansConfig.java
```

Bean:

```java
PasswordEncoder
```

Status:

```text
⬜ TODO
```

---

## STEP 13

Buat JwtService.

Fitur:

```text
Generate Token
Validate Token
Extract Username
```

Status:

```text
⬜ TODO
```

---

## STEP 14

Buat UserAuthService.

Fitur:

```text
Login
Load User
Validate User
```

Status:

```text
⬜ TODO
```

---

## STEP 15

Buat AuthController.

Endpoint:

```text
POST /api/auth/login
```

Status:

```text
⬜ TODO
```

---

## STEP 16

Implement JWT Security.

Tujuan:

```text
Endpoint login public
Endpoint lain wajib JWT
```

Status:

```text
⬜ TODO
```

---

# PHASE 3 — EMPLOYEE MODULE

Tujuan:

CRUD Employee.

---

## STEP 17

Buat EmployeeEntity.

Status:

```text
⬜ TODO
```

---

## STEP 18

Buat EmployeeView.

Status:

```text
⬜ TODO
```

---

## STEP 19

Buat bean employee.

```text
EmployeeCreateRequest
EmployeeUpdateRequest
EmployeeResponse
EmployeeSearchRequest
```

Status:

```text
⬜ TODO
```

---

## STEP 20

Buat validation employee.

Contoh:

```text
ValidPhone
ValidEmployeeCode
```

Status:

```text
⬜ TODO
```

---

## STEP 21

Buat EmployeeService.

Fitur:

```text
Create
Update
Delete
Detail
List
```

Status:

```text
⬜ TODO
```

---

## STEP 22

Buat EmployeeController.

Endpoint:

```text
POST   /api/employees
GET    /api/employees
GET    /api/employees/{id}
PUT    /api/employees/{id}
DELETE /api/employees/{id}
```

Status:

```text
⬜ TODO
```

---

# PHASE 4 — DEPARTMENT MODULE

## STEP 23

DepartmentEntity

## STEP 24

DepartmentView

## STEP 25

DepartmentService

## STEP 26

DepartmentController

Status:

```text
⬜ TODO
```

---

# PHASE 5 — POSITION MODULE

## STEP 27

PositionEntity

## STEP 28

PositionView

## STEP 29

PositionService

## STEP 30

PositionController

Status:

```text
⬜ TODO
```

---

# PHASE 6 — POLISHING

## STEP 31

Swagger Documentation

## STEP 32

Pagination Standardization

## STEP 33

Sorting Standardization

## STEP 34

Audit Fields

```text
createdAt
updatedAt
createdBy
updatedBy
```

## STEP 35

Testing

```text
Service Test
Controller Test
Security Test
```

---

# Current Progress

```text
[✓] Phase 1 Completed
[ ] Phase 2 Authentication
[ ] Phase 3 Employee
[ ] Phase 4 Department
[ ] Phase 5 Position
[ ] Phase 6 Polishing
```
