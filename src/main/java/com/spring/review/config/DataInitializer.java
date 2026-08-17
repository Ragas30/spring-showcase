package com.spring.review.config;

import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entity.EmployeeEntity;
import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;
import com.spring.review.entity.PositionEntity;
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

        if (!departmentExists()) {
            createDepartments();
        }

        if (!positionExists()) {
            createPositions();
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

        DepartmentEntity engineering = findDepartmentByName("Engineering");
        DepartmentEntity hr = findDepartmentByName("Human Resources");
        DepartmentEntity finance = findDepartmentByName("Finance");

        PositionEntity softwareDev = findPositionByName("Software Developer");
        PositionEntity hrManager = findPositionByName("HR Manager");
        PositionEntity accountant = findPositionByName("Accountant");

        createEmployee(
                "EMP0001",
                "Budi Santoso",
                "budi@company.com",
                "081234567890",
                Gender.MALE,
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2022, 1, 10),
                EmployeeStatus.ACTIVE,
                engineering,
                softwareDev
        );

        createEmployee(
                "EMP0002",
                "Siti Rahayu",
                "siti@company.com",
                "081234567891",
                Gender.FEMALE,
                LocalDate.of(1992, 8, 20),
                LocalDate.of(2022, 3, 15),
                EmployeeStatus.ACTIVE,
                hr,
                hrManager
        );

        createEmployee(
                "EMP0003",
                "Andi Pratama",
                "andi@company.com",
                "081234567892",
                Gender.MALE,
                LocalDate.of(1988, 12, 1),
                LocalDate.of(2021, 6, 1),
                EmployeeStatus.ACTIVE,
                finance,
                accountant
        );

        createEmployee(
                "EMP0004",
                "Maya Putri",
                "maya@company.com",
                "081234567893",
                Gender.FEMALE,
                LocalDate.of(1995, 3, 25),
                LocalDate.of(2023, 2, 1),
                EmployeeStatus.INACTIVE,
                engineering,
                softwareDev
        );

        createEmployee(
                "EMP0005",
                "Rudi Hartono",
                "rudi@company.com",
                "081234567894",
                Gender.MALE,
                LocalDate.of(1985, 7, 10),
                LocalDate.of(2020, 1, 15),
                EmployeeStatus.RESIGNED,
                finance,
                accountant
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
            EmployeeStatus status,
            DepartmentEntity department,
            PositionEntity position
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
                        .department(department)
                        .position(position)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        entityManager.persist(employee);
    }

    private void createDepartments() {

        createDepartment(
                "Engineering",
                "Department for software development and IT operations"
        );

        createDepartment(
                "Human Resources",
                "Department for employee management and recruitment"
        );

        createDepartment(
                "Finance",
                "Department for financial planning and accounting"
        );

        System.out.println("3 sample departments created");
    }

    private void createDepartment(
            String name,
            String description
    ) {

        LocalDateTime now = LocalDateTime.now();

        DepartmentEntity department =
                DepartmentEntity.builder()
                        .departmentCode(
                                generateDepartmentCode()
                        )
                        .name(name)
                        .description(description)
                        .isActive(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        entityManager.persist(department);
    }

    private String generateDepartmentCode() {

        List<String> results = entityManager.createQuery(
                """
                SELECT d.departmentCode
                FROM DepartmentEntity d
                ORDER BY d.departmentCode DESC
                """,
                String.class
        ).setMaxResults(1).getResultList();

        String maxCode = results.isEmpty()
                ? null
                : results.getFirst();

        int next = 1;

        if (maxCode != null
                && maxCode.startsWith("DEPT")) {

            try {

                next = Integer.parseInt(
                        maxCode.substring(4)
                ) + 1;

            } catch (NumberFormatException e) {

                next = 1;
            }
        }

        return String.format("DEPT%03d", next);
    }

    private DepartmentEntity findDepartmentByName(
            String name
    ) {

        return entityManager.createQuery(
                """
                SELECT d FROM DepartmentEntity d
                WHERE d.name = :name
                """,
                DepartmentEntity.class
        ).setParameter("name", name)
         .getSingleResult();
    }

    private void createPositions() {

        DepartmentEntity engineering = findDepartmentByName("Engineering");
        DepartmentEntity hr = findDepartmentByName("Human Resources");
        DepartmentEntity finance = findDepartmentByName("Finance");

        createPosition(
                "Software Developer",
                "Develop and maintain software applications",
                engineering
        );

        createPosition(
                "HR Manager",
                "Manage human resources and recruitment",
                hr
        );

        createPosition(
                "Accountant",
                "Manage financial records and accounting",
                finance
        );

        System.out.println("3 sample positions created");
    }

    private void createPosition(
            String name,
            String description,
            DepartmentEntity department
    ) {

        LocalDateTime now = LocalDateTime.now();

        PositionEntity position =
                PositionEntity.builder()
                        .positionCode(
                                generatePositionCode()
                        )
                        .name(name)
                        .description(description)
                        .department(department)
                        .isActive(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        entityManager.persist(position);
    }

    private String generatePositionCode() {

        List<String> results = entityManager.createQuery(
                """
                SELECT p.positionCode
                FROM PositionEntity p
                ORDER BY p.positionCode DESC
                """,
                String.class
        ).setMaxResults(1).getResultList();

        String maxCode = results.isEmpty()
                ? null
                : results.getFirst();

        int next = 1;

        if (maxCode != null
                && maxCode.startsWith("POS")) {

            try {

                next = Integer.parseInt(
                        maxCode.substring(3)
                ) + 1;

            } catch (NumberFormatException e) {

                next = 1;
            }
        }

        return String.format("POS%03d", next);
    }

    private PositionEntity findPositionByName(
            String name
    ) {

        return entityManager.createQuery(
                """
                SELECT p FROM PositionEntity p
                WHERE p.name = :name
                """,
                PositionEntity.class
        ).setParameter("name", name)
         .getSingleResult();
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

    private boolean departmentExists() {

        Long count = entityManager.createQuery(
                """
                SELECT COUNT(d)
                FROM DepartmentEntity d
                """,
                Long.class
        ).getSingleResult();

        return count > 0;
    }

    private boolean positionExists() {

        Long count = entityManager.createQuery(
                """
                SELECT COUNT(p)
                FROM PositionEntity p
                """,
                Long.class
        ).getSingleResult();

        return count > 0;
    }
}
