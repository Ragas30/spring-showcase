package com.spring.review.bean.employee;

import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;
import com.spring.review.validation.ExistingDepartment;
import com.spring.review.validation.ExistingManager;
import com.spring.review.validation.ExistingPosition;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeRequest {

    @NotBlank(message = "Full name wajib diisi")
    private String fullName;

    @Email(message = "Format email tidak valid")
    private String email;

    private String phoneNumber;

    @NotNull(message = "Gender wajib diisi")
    private Gender gender;

    @NotNull(message = "Birth date wajib diisi")
    private LocalDate birthDate;

    @NotNull(message = "Hire date wajib diisi")
    private LocalDate hireDate;

    @NotNull(message = "Status wajib diisi")
    private EmployeeStatus status;

    @ExistingDepartment
    private Long departmentId;

    @ExistingPosition
    private Long positionId;

    @ExistingManager
    private Long managerId;

    private String photoUrl;

}