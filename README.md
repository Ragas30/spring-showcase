# Employee Management System (EMS)

REST API backend untuk manajemen karyawan, departemen, dan posisi. Menggunakan JWT authentication, role-based access control, PostgreSQL, dan Blaze-Persistence.

## Tech Stack

| Komponen | Teknologi |
|---|---|
| Language | Java 21+ |
| Framework | Spring Boot 3.5.3 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + Hibernate |
| Query Builder | Blaze-Persistence 1.6.17 |
| QueryDSL | OpenFeign QueryDSL 7.3.0 (type-safe queries) |
| Database | PostgreSQL 17 |
| Migration | Flyway 11.7.2 |
| Validation | Hibernate Validator (Jakarta) |
| API Docs | Springdoc OpenAPI 2.7.0 (Swagger UI) |
| Export Excel | Apache POI 5.2.5 |
| Export PDF | OpenPDF 2.0.3 |
| Build Tool | Maven |
| Code Gen | Lombok 1.18.38 |
| Redis | Spring Data Redis |
| Monitoring | Spring Actuator |

## Features

- JWT Authentication (login, refresh token, logout, change password)
- Role-based Access Control (ADMIN, HR, MANAGER)
- Employee CRUD with search, filter & pagination
- Department CRUD with search, filter & pagination
- Position CRUD with search, filter & pagination
- File upload (foto employee)
- Custom Validators (@UniqueEmail, @ExistingDepartment, @ExistingPosition)
- Audit Logging (track CRUD operations via AOP)
- Export to Excel (.xlsx)
- Import from Excel (.xlsx)
- Export to PDF
- Dashboard statistics & hiring trend
- Concurrency-safe code generation (DB sequences)
- Swagger/OpenAPI documentation
- Webhook Integration (event-driven, HMAC-SHA256 signed)
- Redis Token Blacklist (persistent across restarts)
- Spring Actuator (health check, metrics)
- QueryDSL Integration (type-safe queries, hybrid Blaze approach)

## Prerequisites

1. Java 21+
2. Maven 3.8+
3. PostgreSQL 15+
4. Redis 7+ (untuk token blacklist)

```sql
CREATE DATABASE karyawan;
```

## Quick Start

```bash
# Clone
git clone <repo-url>

# Start Redis (required for token blacklist)
docker run -d -p 6379:6379 redis:7-alpine

# Start PostgreSQL
docker run -d -p 5432:5432 -e POSTGRES_DB=karyawan -e POSTGRES_PASSWORD=12345678 postgres:17

# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run
```

Aplikasi jalan di: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Default Users

| Username | Password | Role | Akses |
|---|---|---|---|
| admin | admin123 | ADMIN | Full akses |
| hr_user | hr123 | HR | Create, Read, Update |
| manager | manager123 | MANAGER | Read, Update |

## API Endpoints

### Authentication

- POST /api/auth/login
- POST /api/auth/refresh
- POST /api/auth/logout
- POST /api/auth/change-password
- GET /api/auth/me

### Employee

- POST /api/employees
- GET /api/employees
- GET /api/employees/{id}
- PUT /api/employees/{id}
- DELETE /api/employees/{id}

### Department

- POST /api/departments
- GET /api/departments
- GET /api/departments/{id}
- PUT /api/departments/{id}
- DELETE /api/departments/{id}

### Position

- POST /api/positions
- GET /api/positions
- GET /api/positions/{id}
- PUT /api/positions/{id}
- DELETE /api/positions/{id}

### File

- POST /api/files/upload
- DELETE /api/files/{filename}

### Export/Import

- GET /api/export/excel
- GET /api/export/pdf
- POST /api/export/import

### Dashboard

- GET /api/dashboard/stats
- GET /api/dashboard/hiring-trend

### Audit Log (Admin only)

- GET /api/audit-logs

### Webhook (Admin only)

- POST /api/webhooks
- GET /api/webhooks
- GET /api/webhooks/{id}
- PUT /api/webhooks/{id}
- DELETE /api/webhooks/{id}
- GET /api/webhooks/logs

### Monitoring

- GET /actuator/health
- GET /actuator/info (Admin)
- GET /actuator/metrics (Admin)

## Project Structure

```
src/main/java/com/spring/review/
├── bean/           # Request/Response DTOs
├── bean/webhook/         # Webhook DTOs
├── common/         # ApiResponse, ErrorCode, PageResponse, PageSpec
├── config/         # Security, Blaze, Flyway, Audit AOP
├── controller/     # REST Controllers
├── entity/         # JPA Entities
├── entity/Webhook*.java  # Webhook entities
├── entityView/     # Blaze Entity Views
├── entityView/Webhook*.java  # Webhook views
├── exception/      # Exception Handling
├── service/        # Business Logic
└── validation/     # Custom Validators
```

## License

MIT
