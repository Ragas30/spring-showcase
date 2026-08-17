# AI_HANDOVER.md

## Project Overview

Project ini adalah Employee Management System (EMS) yang dibangun menggunakan:

* Java 24
* Spring Boot 4.x
* Spring Security
* JWT Authentication
* Blaze Persistence
* Blaze Entity View
* MySQL
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
```

---

# Package Structure

```text
com.spring.review

├── bean
│   ├── auth
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

## Hibernate

Gunakan:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Tabel dibuat otomatis oleh Hibernate.

Tidak menggunakan migration tool saat ini.

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
@Email
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

untuk mapping Entity → View.

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
10. Jangan mengasumsikan API library sama antar versi.
11. Verifikasi contoh terhadap dependency yang digunakan project.

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
* Entity View
* Global Exception Handling
* RESIGNED status

---

## Auth Enhancements

Status:

```text
DONE
```

Fitur:

* Refresh Token
* Role-based Authorities (RBAC)
* CORS Configuration
* User isActive check
* User email field
* Role in JWT claims
* Role in LoginResponse

---

# Next Target

Implementasi Department Module:

```text
DepartmentEntity
DepartmentView
DepartmentService
DepartmentController
```

sebelum melanjutkan ke Position Module.
