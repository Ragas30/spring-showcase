# AI_HANDOVER.md

## Project Overview

Project ini adalah Employee Management System (EMS) yang dibangun menggunakan:

* Java 21
* Spring Boot 3.5.3
* Spring Security
* JWT Authentication
* Blaze Persistence
* Blaze Entity View
* QueryDSL (OpenFeign fork v7.3.0)
* PostgreSQL
* Flyway
* Swagger / OpenAPI
* Maven

---

# Arsitektur Project

## Penting

Project ini **TIDAK MENGGUNAKAN REPOSITORY**.

Jangan membuat:

```java
UserRepository
EmployeeRepository
JpaRepository
CrudRepository
```

Karena project ini menggunakan:

```java
EntityManager
CriteriaBuilderFactory
EntityViewManager
```

Contoh dependency service:

```java
private final EntityManager em;
private final CriteriaBuilderFactory cbf;
private final EntityViewManager evm;
private final JPAQueryFactory queryFactory;
```

---

# Package Structure

```text
com.spring.review

├── bean
│   ├── auth
│   ├── audit
│   ├── dashboard
│   └── employee
│
├── common
│   ├── ApiResponse
│   ├── ErrorCode
│   ├── PageResponse
│   └── PageSpec
│
├── config
│   ├── SecurityConfig
│   ├── OpenApiConfig
│   ├── BlazeConfig
│   ├── Auditable
│   ├── AuditAspect
│   └── DataInitializer
│
├── controller
│
├── entity
│
├── entityView
│
├── exception
│
├── service
│
└── validation
```

---

# Database Rules

## Flyway Migration

Project menggunakan Flyway untuk schema migration.

Gunakan:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
spring.flyway.baselineOnMigrate=true
```

Schema dikelola oleh migration file:

```text
V1__init_schema.sql
```

## DB Sequences

Gunakan database sequences untuk code generation:

```sql
emp_code_seq
dept_code_seq
pos_code_seq
```

Tidak menggunakan `spring.jpa.hibernate.ddl-auto=update`.

---

# Authentication Rules

## Login Endpoint

```http
POST /api/auth/login
```

## JWT

Menggunakan:

```text
JWT Stateless Authentication
```

Security menggunakan:

```java
SessionCreationPolicy.STATELESS
```

dan:

```java
JwtAuthenticationFilter
```

---

# Swagger Rules

Swagger wajib mendukung:

```text
Authorize Button
Bearer Token
JWT Authentication
```

OpenApiConfig harus memiliki:

```java
SecuritySchemeType.HTTP
scheme("bearer")
bearerFormat("JWT")
```

Jika Swagger dapat dibuka tetapi endpoint selalu:

```http
401 Unauthorized
```

cek OpenApiConfig terlebih dahulu.

---

# ApiResponse Standard

Semua controller wajib menggunakan:

```java
ApiResponse<T>
```

Struktur:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String code;

    private String message;

    private T data;
}
```

Jangan return entity atau DTO secara langsung.

---

# Error Handling Standard

## BusinessException

Jangan hardcode:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
```

untuk semua BusinessException.

Karena akan menyebabkan:

```text
NOT_FOUND -> 400
UNAUTHORIZED -> 400
BAD_REQUEST -> 400
```

yang salah secara REST.

---

## Mapping ErrorCode

Gunakan mapping:

```text
BAD_REQUEST -> 400
NOT_FOUND -> 404
UNAUTHORIZED -> 401
INTERNAL_SERVER_ERROR -> 500
```

---

# Validation Rules

Gunakan Bean Validation:

```java
@NotBlank
@NotNull
@email
```

dan tangani dengan:

```java
MethodArgumentNotValidException
```

Response:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "..."
}
```

---

# Custom Validators Rules

## @UniqueEmail

Validasi email unik via EntityManager query.

```java
@UniqueEmail
private String email;
```

Validasi dilakukan dengan query ke database menggunakan `EntityManager` untuk memastikan email belum terdaftar.

## @ExistingDepartment

Validasi departmentId exists.

```java
@ExistingDepartment
private Long departmentId;
```

Validasi dilakukan dengan query ke database menggunakan `EntityManager` untuk memastikan department dengan ID tersebut ada.

## @ExistingPosition

Validasi positionId exists.

```java
@ExistingPosition
private Long positionId;
```

Validasi dilakukan dengan query ke database menggunakan `EntityManager` untuk memastikan position dengan ID tersebut ada.

## Implementation Pattern

Semua custom validator menggunakan:

```java
@PersistenceContext
private EntityManager em;
```

untuk melakukan validasi database.

---

# Audit Logging Rules

## Auditable Annotation

Gunakan annotation pada service method:

```java
@Auditable(action = "CREATE", entityType = "Employee")
public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
    // ...
}
```

## AOP Implementation

`AuditAspect` merekam audit log menggunakan AOP (Aspect-Oriented Programming).

Audit log tercatat secara otomatis untuk setiap method yang ditandai `@Auditable`.

## Access Control

Hanya role `ADMIN` yang dapat mengakses audit logs.

---

# Export/Import Rules

## Export Excel

Menggunakan Apache POI:

```java
XSSFWorkbook workbook = new XSSFWorkbook();
XSSFSheet sheet = workbook.createSheet("Employees");
```

## Export PDF

Menggunakan OpenPDF:

```java
PdfPTable table = new PdfPTable(headers.length);
PdfPCell cell = new PdfPCell(new Phrase("Value"));
```

Jangan menggunakan:

```java
com.lowagie.text.Table
com.lowagie.text.Cell
```

Karena deprecated di OpenPDF 2.x.

## Import

Menggunakan MultipartFile dengan Swagger annotation:

```java
@Parameter(schema = @Schema(type = "string", format = "binary"))
MultipartFile file
```

---

# Code Generation Rules

## DB Sequences

Gunakan database sequences untuk code generation:

```sql
SELECT nextval('emp_code_seq');
```

Ini menjamin concurrency safety karena sequence di-generate oleh database.

## Jangan Gunakan MAX()

```java
// JANGAN
Long maxCode = em.createQuery("SELECT MAX(e.id) FROM EmployeeEntity e", Long.class)
        .getSingleResult();
```

Karena rawan race condition.

---

# Employee Module Rules

## Employee Code

Jangan diinput oleh user.

Employee code harus dibuat otomatis oleh backend.

Contoh:

```text
EMP0001
EMP0002
EMP0003
```

Generate melalui service.

---

## CreateEmployeeRequest

Tidak boleh memiliki:

```java
private String employeeCode;
```

Karena generated otomatis.

---

## UpdateEmployeeRequest

Tidak boleh mengubah:

```java
employeeCode
```

Employee code bersifat immutable.

---

# JPA Rules

## Managed Entity

Jika entity diperoleh melalui:

```java
em.find(...)
```

maka entity sudah managed.

Jangan lakukan:

```java
em.merge(entity);
```

Cukup:

```java
entity.setField(...);

em.flush();
```

---

# Blaze Persistence Rules

## Versi Project

Project menggunakan Blaze Persistence 1.6.x.

---

## Jangan Gunakan

```java
evm.find(
        em,
        EmployeeView.class,
        id
)
```

Karena pernah menyebabkan error:

```text
The class EmployeeView is not a view type!
```

meskipun EntityView sudah benar.

---

## Gunakan

```java
evm.applySetting(
        EntityViewSetting.create(
                EmployeeView.class
        ),
        cbf.create(
                em,
                EmployeeEntity.class
        )
        .where("id")
        .eq(id)
)
.getSingleResult();
```

untuk mapping Entity -> View.

---

# QueryDSL Rules

## OpenFeign Fork

Project menggunakan QueryDSL OpenFeign fork v7.3.0.

Dependency:

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>7.3.0</version>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>7.3.0</version>
    <scope>provided</scope>
</dependency>
```

## Q-Classes

Q-classes di-generate otomatis oleh annotation processor dari entity:

```java
QEmployeeEntity qEmployee = QEmployeeEntity.employeeEntity;
QDepartmentEntity qDepartment = QDepartmentEntity.departmentEntity;
QPositionEntity qPosition = QPositionEntity.positionEntity;
```

## BooleanExpression

Gunakan `BooleanExpression` bukan `Predicate` sebagai tipe variabel:

```java
BooleanExpression predicate = Expressions.TRUE;
predicate = predicate.and(qEmployee.fullName.containsIgnoreCase(keyword));
```

`Expressions.TRUE` digunakan sebagai default agar predicates bisa di-chain dengan method `and()`.

## JPAQueryFactory

Gunakan `JPAQueryFactory` untuk count queries dan ID fetching:

```java
Long count = queryFactory.select(qEmployee.count())
    .from(qEmployee)
    .where(predicate)
    .fetchOne();
```

## Hybrid Approach

1. QueryDSL untuk query building (predicates, count, ID fetching)
2. Blaze-Persistence Entity Views untuk DTO projection

```java
// 1. QueryDSL: fetch IDs
List<Long> ids = queryFactory.select(qEmployee.id)
    .from(qEmployee)
    .where(predicate)
    .fetch();

// 2. Blaze: project to view
evm.applySetting(
    EntityViewSetting.create(EmployeeView.class),
    cbf.create(em, EmployeeEntity.class)
        .where("id").in(ids)
).getResultList();
```

## Dashboard Exception

DashboardService tetap menggunakan raw JPQL untuk aggregate queries (statistik, hiring trend).

---

# Test Rules

## Unit Testing Framework

Menggunakan Mockito:

```java
@ExtendWith(MockitoExtension.class)
```

## Unit Test Classes

```text
JwtServiceTest
GlobalExceptionHandlerTest
TokenBlacklistServiceTest
```

## Jangan Gunakan

```java
@SpringBootTest
```

untuk unit tests.

Unit tests tidak memerlukan full Spring context. Gunakan `@ExtendWith(MockitoExtension.class)` dengan `@Mock` dan `@InjectMocks`.

---

# Lesson Learned

## Kasus Blaze

Terjadi kasus:

```text
GET ALL berhasil
CREATE gagal
```

Awalnya dicurigai:

```text
EmployeeView salah
BlazeConfig salah
Entity salah
```

Tetapi akar masalah sebenarnya:

```java
evm.find(...)
```

Tidak kompatibel dengan konfigurasi Blaze yang digunakan.

Solusi:

```java
EntityViewSetting.create(...)
```

---

## Kasus GlobalExceptionHandler

Awalnya:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
```

untuk semua BusinessException.

Akibat:

```text
NOT_FOUND -> 400
UNAUTHORIZED -> 400
```

Solusi:

Mapping HttpStatus berdasarkan ErrorCode.

---

## Kasus Debugging

Jika terjadi error aneh seperti:

```text
Class not found
View not registered
Bean not found
```

Lakukan:

```bash
mvn clean
```

kemudian:

```text
Stop Application
Run Ulang
```

sebelum melakukan refactor besar.

---

# Coding Rules

Saat membuat contoh kode:

1. Jangan gunakan Repository.
2. Gunakan EntityManager.
3. Gunakan Blaze Persistence.
4. Gunakan EntityView.
5. Gunakan DTO pada package bean.
6. Gunakan ApiResponse.
7. Gunakan ErrorCode.
8. Gunakan GlobalExceptionHandler.
9. Gunakan Bean Validation.
10. Gunakan QueryDSL (BooleanExpression) untuk predicates.
11. Jangan mengasumsikan API library sama antar versi.
12. Verifikasi contoh terhadap dependency yang digunakan project.

---

# Current Progress

## Authentication Module

Status:

```text
DONE
```

Fitur:

* JWT Authentication
* Login Endpoint
* Refresh Token
* Logout Endpoint
* Change Password
* Role-based Authorities (RBAC)
* CORS Configuration
* Security Configuration
* Swagger Authorization

---

## Employee Module

Status:

```text
DONE
```

Fitur:

* Create Employee
* Get Employee By Id
* Get All Employees
* Update Employee
* Delete Employee
* Validation
* Search
* Entity View
* Global Exception Handling
* RESIGNED status

---

## Department Module

Status:

```text
DONE
```

Fitur:

* CRUD Department
* Validation
* Entity View

---

## Position Module

Status:

```text
DONE
```

Fitur:

* CRUD Position
* Validation
* Entity View

---

## File Upload Module

Status:

```text
DONE
```

Fitur:

* File upload endpoint
* MultipartFile handling

---

## Audit Logging Module

Status:

```text
DONE
```

Fitur:

* AOP-based audit logging
* @Auditable annotation
* AuditAspect
* ADMIN-only access

---

## Export/Import Module

Status:

```text
DONE
```

Fitur:

* Export to Excel (Apache POI)
* Export to PDF (OpenPDF)
* Import from file

---

## Dashboard Module

Status:

```text
DONE
```

Fitur:

* Statistics endpoint
* Hiring Trend

---

## Custom Validators

Status:

```text
DONE
```

Fitur:

* @UniqueEmail
* @ExistingDepartment
* @ExistingPosition

---

## Unit Tests

Status:

```text
DONE
```

Fitur:

* 16 tests passing
* JwtServiceTest
* GlobalExceptionHandlerTest
* TokenBlacklistServiceTest

---

## QueryDSL Integration

Status:

```text
DONE
```

Fitur:

* QueryDSL OpenFeign fork v7.3.0
* Q-class auto-generation
* BooleanExpression with Expressions.TRUE
* JPAQueryFactory for count/ID queries
* Hybrid: QueryDSL + Blaze Entity Views
* Refactored: EmployeeService, DepartmentService, PositionService, WebhookService, AuditLogService

---
