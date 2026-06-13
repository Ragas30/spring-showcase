package com.spring.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}