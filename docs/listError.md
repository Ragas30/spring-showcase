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

## Catatan Umum

| Error Pattern | Pencegahan |
|---------------|------------|
| DDL `NOT NULL` tanpa default pada tabel existing | Selalu tambah `DEFAULT value` di `columnDefinition` |
| `getSingleResult()` pada tabel mungkin kosong | Pakai `getResultList()` + cek `isEmpty()` |
| Query pakai kolom yang belum ada | Pastikan DDL sukses dulu, atau gunakan `try-catch` |
