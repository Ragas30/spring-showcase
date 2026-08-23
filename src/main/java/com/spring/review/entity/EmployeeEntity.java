package com.spring.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_full_name", columnList = "fullName"),
        @Index(name = "idx_employee_employee_code", columnList = "employeeCode"),
        @Index(name = "idx_employee_email", columnList = "email"),
        @Index(name = "idx_employee_gender", columnList = "gender"),
        @Index(name = "idx_employee_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            length = 50
    )
    private String employeeCode;

    @Column(
            nullable = false,
            length = 150
    )
    private String fullName;

    @Column(
            unique = true,
            length = 150
    )
    private String email;

    @Column(length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private PositionEntity position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private EmployeeEntity manager;

    @Column(length = 255)
    private String photoUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}