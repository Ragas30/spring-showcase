# List Error & Solusi

Daftar error yang pernah terjadi beserta penyebab dan solusinya. Dicatat agar tidak mengulangi kesalahan yang sama.

---

## 1. Hibernate DDL: column contains null values

**Error:**
```
ERROR: column "is_active" of relation "users" contains null values
```

**Penyebab:**
Menambahkan kolom baru dengan `NOT NULL` ke tabel yang sudah punya data, tanpa default value. PostgreSQL reject karena existing rows bakal NULL.

**Solusi:**
Tambahkan `columnDefinition` dengan default value di entity:
```java
@Column(
    nullable = false,
    columnDefinition = "boolean not null default true"
)
private Boolean isActive;
```

**Tanggal:** 2026-08-17

---

## 2. JPA Query: column does not exist

**Error:**
```
ERROR: column ue1_0.is_active does not exist
```

**Penyebab:**
Query menggunakan kolom yang belum terbentuk di database karena DDL pada error #1 gagal dieksekusi.

**Solusi:**
Fix error #1 terlebih dahulu. DDL harus berhasil agar kolom tersedia untuk query.

**Tanggal:** 2026-08-17

---

## 3. DataInitializer: NoResultException on empty table

**Error:**
```
jakarta.persistence.NoResultException: No result found for query
[SELECT d.departmentCode FROM DepartmentEntity d ORDER BY d.departmentCode DESC]
```

**Penyebab:**
Menggunakan `getSingleResult()` pada query yang menghadap tabel kosong (first run). `getSingleResult()` throw `NoResultException` jika tidak ada data.

**Solusi:**
Gunakan `getResultList()` + `setMaxResults(1)`, lalu cek apakah list kosong:
```java
List<String> results = entityManager.createQuery(
        "SELECT d.departmentCode FROM DepartmentEntity d ORDER BY d.departmentCode DESC",
        String.class
).setMaxResults(1).getResultList();

String maxCode = results.isEmpty() ? null : results.getFirst();
```

**Tanggal:** 2026-08-18

---

## 4. Flyway: Found non-empty schema but no schema history table

**Error:**
```
Found non-empty schema(s) "public" but no schema history table. Use baseline() or set baselineOnMigrate to true to initialize the schema history table.
```

**Penyebab:**
Menambahkan Flyway ke project yang sudah punya schema (sudah dijalankan sebelumnya dengan ddl-auto=update). Flyway menemukan tabel-tabel yang sudah ada tapi belum punya flyway_schema_history.

**Solusi:**
Tambahkan `spring.flyway.baselineOnMigrate=true` di application.properties. Ini membuat Flyway melakukan baseline pada schema yang sudah ada sebelum menjalankan migration.

**Tanggal:** 2026-08-19

---

## 5. Hibernate: cannot find symbol readOnly

**Error:**
```
cannot find symbol: method readOnly() location: @interface jakarta.transaction.Transactional
```

**Penyebab:**
Menggunakan `@Transactional(readOnly = true)` dari `jakarta.transaction.Transactional` yang tidak memiliki atribut `readOnly`. Yang memiliki atribut `readOnly` adalah `org.springframework.transaction.annotation.Transactional`.

**Solusi:**
Import `org.springframework.transaction.annotation.Transactional` bukan `jakarta.transaction.Transactional`.

**Tanggal:** 2026-08-19

---

## 6. OpenPDF: cannot find symbol setFont on Cell

**Error:**
```
cannot find symbol: method setFont(com.lowagie.text.Font) location: variable pdfCell of type com.lowagie.text.Cell
```

**Penyebab:**
OpenPDF 2.x menghapus method `setFont()` dari class `Cell`. API berubah dari versi 1.x ke 2.x.

**Solusi:**
Gunakan `PdfPTable` dan `PdfPCell` (bukan `Table` dan `Cell`). Untuk font, gunakan `cell.setPhrase(new Phrase(text, font))` bukan `cell.setFont(font)`.

**Tanggal:** 2026-08-19

---

## 7. H2 Test: Sequence not found

**Error:**
```
Sequence "DEPT_CODE_SEQ" not found; SQL statement: select nextval('dept_code_seq')
```

**Penyebab:**
Test profile menggunakan H2 database dengan `spring.jpa.hibernate.ddl-auto=create-drop` tapi Flyway disabled. Sequence PostgreSQL (`nextval('dept_code_seq')`) yang dibuat di V1__init_schema.sql tidak dijalankan di H2, dan Hibernate create-drop tidak membuat sequence dari SQL.

**Solusi:**
Disable contextLoads test yang menggunakan full Spring context. Gunakan unit tests dengan Mockito untuk testing. Atau buat H2-compatible migration terpisah.

**Tanggal:** 2026-08-19

---

## 8. Async Configuration Missing

**Error:**
```
Async method cannot be invoked because target bean is not a JDK/JDK proxy
```

**Penyebab:**
Spring tidak mengenali `@Async` karena `@EnableAsync` belum ditambahkan di `@SpringBootApplication`.

**Solusi:**
Tambahkan `@EnableAsync` di class `ReviewApplication`. Juga tambahkan `@EnableScheduling` untuk cleanup scheduler.

**Tanggal:** 2026-08-20

---

## 9. QueryDSL Predicate and() method not found

**Error:**
```
cannot find symbol: method and(com.querydsl.core.types.dsl.BooleanExpression)
location: variable predicate of type com.querydsl.core.types.Predicate
```

**Penyebab:**
Menggunakan `com.querydsl.core.types.Predicate` sebagai tipe variabel. `Predicate` interface tidak memiliki method `and()`. Method `and()` hanya ada di `BooleanExpression` (subclass dari `Predicate`).

**Solusi:**
Gunakan `BooleanExpression` sebagai tipe variabel, bukan `Predicate`. Mulai chain dengan `Expressions.TRUE` sebagai default.
```java
BooleanExpression predicate = Expressions.TRUE;
predicate = predicate.and(qEntity.field.eq(value));
```

**Tanggal:** 2026-08-20

---

## Catatan Umum

| Error Pattern | Pencegahan |
|---------------|------------|
| DDL `NOT NULL` tanpa default pada tabel existing | Selalu tambah `DEFAULT value` di `columnDefinition` |
| `getSingleResult()` pada tabel mungkin kosong | Pakai `getResultList()` + cek `isEmpty()` |
| Query pakai kolom yang belum ada | Pastikan DDL sukses dulu, atau gunakan `try-catch` |
| Flyway pada existing DB | Selalu set `baselineOnMigrate=true` |
| `jakarta.transaction.Transactional` vs `Spring @Transactional` | Cek atribut yang tersedia sebelum pakai |
| API library berubah antar versi | Selalu cek release notes / javadoc versi yang dipakai |
