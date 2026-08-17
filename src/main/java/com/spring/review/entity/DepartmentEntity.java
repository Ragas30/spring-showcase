package com.spring.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            length = 50
    )
    private String departmentCode;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
