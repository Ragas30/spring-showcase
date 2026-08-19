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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
```

---

## STEP 18

Buat EmployeeView.

Status:

```text
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
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
✓ DONE
```

---

# PHASE 4 — DEPARTMENT MODULE

## STEP 23

DepartmentEntity

Status:

```text
✓ DONE
```

## STEP 24

DepartmentView

Status:

```text
✓ DONE
```

## STEP 25

DepartmentService

Status:

```text
✓ DONE
```

## STEP 26

DepartmentController

Status:

```text
✓ DONE
```

---

# PHASE 5 — POSITION MODULE

## STEP 27

PositionEntity

Status:

```text
✓ DONE
```

## STEP 28

PositionView

Status:

```text
✓ DONE
```

## STEP 29

PositionService

Status:

```text
✓ DONE
```

## STEP 30

PositionController

Status:

```text
✓ DONE
```

---

# PHASE 6 — POLISHING

## STEP 31

Swagger Documentation

Status:

```text
✓ DONE
```

## STEP 32

Pagination Standardization

Status:

```text
✓ DONE
```

## STEP 33

Sorting Standardization

Status:

```text
✓ DONE
```

## STEP 34

Audit Fields

```text
createdAt
updatedAt
createdBy
updatedBy
```

Status:

```text
✓ DONE
```

## STEP 35

Testing

```text
Service Test
Controller Test
Security Test
```

Status:

```text
✓ DONE
```

---

# PHASE 7 — INFRASTRUCTURE

Tujuan:

Setup Flyway migrations, H2 test database, dan DB sequences.

---

## STEP 36

Setup Flyway migration.

Tujuan:

```text
Database versioning dengan Flyway
```

Status:

```text
✓ DONE
```

## STEP 37

Setup H2 test database.

Tujuan:

```text
In-memory database untuk testing
```

Status:

```text
✓ DONE
```

## STEP 38

Setup DB Sequences.

Tujuan:

```text
Sequence-based ID generation
```

Status:

```text
✓ DONE
```

---

# PHASE 8 — CUSTOM VALIDATORS

Tujuan:

Membuat custom Bean Validation annotations.

---

## STEP 39

@UniqueEmail

Tujuan:

```text
Validasi email unik di database
```

Status:

```text
✓ DONE
```

## STEP 40

@ExistingDepartment

Tujuan:

```text
Validasi department ID exists di database
```

Status:

```text
✓ DONE
```

## STEP 41

@ExistingPosition

Tujuan:

```text
Validasi position ID exists di database
```

Status:

```text
✓ DONE
```

---

# PHASE 9 — AUTH ENHANCEMENTS

Tujuan:

Menambahkan fitur Logout dan Change Password.

---

## STEP 42

Logout

Tujuan:

```text
Blacklist JWT token saat logout
Endpoint: POST /api/auth/logout
```

Status:

```text
✓ DONE
```

## STEP 43

Change Password

Tujuan:

```text
Ubah password user yang sedang login
Endpoint: POST /api/auth/change-password
```

Status:

```text
✓ DONE
```

---

# PHASE 10 — AUDIT LOGGING

Tujuan:

Mencatat semua aktivitas menggunakan AOP.

---

## STEP 44

AuditLog Entity

Tujuan:

```text
Entity untuk menyimpan log aktivitas
```

Status:

```text
✓ DONE
```

## STEP 45

AOP Audit Aspect

Tujuan:

```text
Automatic logging setiap ada perubahan data
```

Status:

```text
✓ DONE
```

## STEP 46

AuditLog Controller

Tujuan:

```text
Endpoint untuk melihat audit logs
Endpoint: GET /api/audit-logs
```

Status:

```text
✓ DONE
```

---

# PHASE 11 — EXPORT/IMPORT

Tujuan:

Export dan Import data dalam format Excel dan PDF.

---

## STEP 47

Excel Export

Tujuan:

```text
Export data ke format Excel
```

Status:

```text
✓ DONE
```

## STEP 48

Excel Import

Tujuan:

```text
Import data dari file Excel
```

Status:

```text
✓ DONE
```

## STEP 49

PDF Export

Tujuan:

```text
Export data ke format PDF
```

Status:

```text
✓ DONE
```

---

# PHASE 12 — DASHBOARD

Tujuan:

Menampilkan statistik dan hiring trend.

---

## STEP 50

Dashboard Stats

Tujuan:

```text
Endpoint untuk statistik umum
```

Status:

```text
✓ DONE
```

## STEP 51

Hiring Trend

Tujuan:

```text
Endpoint untuk trend data karyawan
```

Status:

```text
✓ DONE
```

---

# PHASE 13 — UNIT TESTS

Tujuan:

Menulis unit test untuk komponen penting.

---

## STEP 52

JwtServiceTest

Tujuan:

```text
Test JWT generation, validation, dan extraction
```

Status:

```text
✓ DONE
```

## STEP 53

GlobalExceptionHandlerTest

Tujuan:

```text
Test exception handling untuk semua tipe exception
```

Status:

```text
✓ DONE
```

## STEP 54

TokenBlacklistServiceTest

Tujuan:

```text
Test blacklist token saat logout
```

Status:

```text
✓ DONE
```

---

# PHASE 14 — WEBHOOK MODULE

Tujuan:

Membuat sistem webhook untuk notifikasi otomatis.

---

## STEP 55

WebhookSubscription + WebhookLog Entities

Tujuan:

```text
WebhookSubscription + WebhookLog entities + tables
```

Status:

```text
✓ DONE
```

## STEP 56

WebhookService

Tujuan:

```text
WebhookService (CRUD)
```

Status:

```text
✓ DONE
```

## STEP 57

WebhookDeliveryService

Tujuan:

```text
WebhookDeliveryService (async HTTP dispatch with HMAC-SHA256 signature)
```

Status:

```text
✓ DONE
```

## STEP 58

WebhookController

Tujuan:

```text
WebhookController (REST endpoints)
```

Status:

```text
✓ DONE
```

## STEP 59

Integration with AuditAspect

Tujuan:

```text
Auto-trigger webhook on CRUD via AuditAspect
```

Status:

```text
✓ DONE
```

---

# PHASE 15 — REDIS TOKEN BLACKLIST

Tujuan:

Mengganti ConcurrentHashMap dengan Redis untuk token blacklist.

---

## STEP 60

Redis Token Blacklist

Tujuan:

```text
Replaced ConcurrentHashMap with Redis
TTL: 168 hours (7 days)
Tokens persist across restarts
Prerequisite: Redis running on localhost:6379
```

Status:

```text
✓ DONE
```

---

# PHASE 16 — SPRING ACTUATOR

Tujuan:

Menambahkan monitoring dan health check dengan Spring Actuator.

---

## STEP 61

Spring Actuator Setup

Tujuan:

```text
/actuator/health (public)
/actuator/info, /actuator/metrics (ADMIN only)
```

Status:

```text
✓ DONE
```

---

# PHASE 17 — QUERYDSL INTEGRATION

Tujuan:

Menambahkan QueryDSL untuk type-safe queries dengan pendekatan hybrid bersama Blaze-Persistence.

---

## STEP 62

QueryDSL Setup

Tujuan:

```text
Added querydsl-jpa and querydsl-apt (OpenFeign fork v7.3.0) to pom.xml
Configured annotation processor for Q-class generation
```

Status:

```text
✓ DONE
```

## STEP 63

Service Refactoring

Tujuan:

```text
Refactored EmployeeService, DepartmentService, PositionService, WebhookService, AuditLogService
Hybrid approach: QueryDSL predicates for count/search + Blaze Entity Views for DTO projection
DashboardService kept with raw JPQL (aggregate queries)
```

Status:

```text
✓ DONE
```

---

# Current Progress

```text
[✓] Phase 1 Completed - Project Foundation
[✓] Phase 2 Completed - Authentication
[✓] Phase 3 Completed - Employee Module
[✓] Phase 4 Completed - Department Module
[✓] Phase 5 Completed - Position Module
[✓] Phase 6 Completed - Polishing
[✓] Phase 7 Completed - Infrastructure (Flyway, H2, DB Sequences)
[✓] Phase 8 Completed - Custom Validators
[✓] Phase 9 Completed - Auth Enhancements (Logout, Change Password)
[✓] Phase 10 Completed - Audit Logging
[✓] Phase 11 Completed - Export/Import (Excel, PDF)
[✓] Phase 12 Completed - Dashboard
[✓] Phase 13 Completed - Unit Tests
[✓] Phase 14 Completed - Webhook Module
[✓] Phase 15 Completed - Redis Token Blacklist
[✓] Phase 16 Completed - Spring Actuator
[✓] Phase 17 Completed - QueryDSL Integration
```