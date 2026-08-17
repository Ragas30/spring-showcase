package com.spring.review.config;

import com.spring.review.entity.EmployeeEntity;
import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;
import com.spring.review.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        fixExistingUsers();

        if (!userExists("admin")) {
            createUser("admin", "admin123", "ADMIN");
            createUser("hr_user", "hr123", "HR");
            createUser("manager", "manager123", "MANAGER");
        }

        if (!employeeExists()) {
            createEmployees();
        }
    }

    private void createUser(
            String username,
            String password,
            String role
    ) {

        UserEntity user =
                UserEntity.builder()
                        .username(username)
                        .password(
                                passwordEncoder.encode(
                                        password
                                )
                        )
                        .role(role)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        entityManager.persist(user);

        System.out.println(
                "User created: " + username
                        + " (role: " + role + ")"
        );
    }

    private void createEmployees() {

        createEmployee(
                "EMP0001",
                "Budi Santoso",
                "budi@company.com",
                "081234567890",
                Gender.MALE,
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2022, 1, 10),
                EmployeeStatus.ACTIVE
        );

        createEmployee(
                "EMP0002",
                "Siti Rahayu",
                "siti@company.com",
                "081234567891",
                Gender.FEMALE,
                LocalDate.of(1992, 8, 20),
                LocalDate.of(2022, 3, 15),
                EmployeeStatus.ACTIVE
        );

        createEmployee(
                "EMP0003",
                "Andi Pratama",
                "andi@company.com",
                "081234567892",
                Gender.MALE,
                LocalDate.of(1988, 12, 1),
                LocalDate.of(2021, 6, 1),
                EmployeeStatus.ACTIVE
        );

        createEmployee(
                "EMP0004",
                "Maya Putri",
                "maya@company.com",
                "081234567893",
                Gender.FEMALE,
                LocalDate.of(1995, 3, 25),
                LocalDate.of(2023, 2, 1),
                EmployeeStatus.INACTIVE
        );

        createEmployee(
                "EMP0005",
                "Rudi Hartono",
                "rudi@company.com",
                "081234567894",
                Gender.MALE,
                LocalDate.of(1985, 7, 10),
                LocalDate.of(2020, 1, 15),
                EmployeeStatus.RESIGNED
        );

        System.out.println("5 sample employees created");
    }

    private void createEmployee(
            String employeeCode,
            String fullName,
            String email,
            String phoneNumber,
            Gender gender,
            LocalDate birthDate,
            LocalDate hireDate,
            EmployeeStatus status
    ) {

        LocalDateTime now = LocalDateTime.now();

        EmployeeEntity employee =
                EmployeeEntity.builder()
                        .employeeCode(employeeCode)
                        .fullName(fullName)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .gender(gender)
                        .birthDate(birthDate)
                        .hireDate(hireDate)
                        .status(status)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        entityManager.persist(employee);
    }

    private void fixExistingUsers() {

        List<UserEntity> users = entityManager
                .createQuery(
                        """
                        SELECT u FROM UserEntity u
                        WHERE u.isActive IS NULL
                        """,
                        UserEntity.class
                )
                .getResultList();

        for (UserEntity user : users) {
            user.setIsActive(true);
            user.setUpdatedAt(LocalDateTime.now());
        }

        if (!users.isEmpty()) {
            System.out.println(
                    "Fixed " + users.size()
                            + " users with missing isActive"
            );
        }
    }

    private boolean userExists(
            String username
    ) {

        TypedQuery<Long> query =
                entityManager.createQuery(
                        """
                        SELECT COUNT(u)
                        FROM UserEntity u
                        WHERE u.username = :username
                        """,
                        Long.class
                );

        query.setParameter(
                "username",
                username
        );

        return query.getSingleResult() > 0;
    }

    private boolean employeeExists() {

        Long count = entityManager.createQuery(
                """
                SELECT COUNT(e)
                FROM EmployeeEntity e
                """,
                Long.class
        ).getSingleResult();

        return count > 0;
    }
}
