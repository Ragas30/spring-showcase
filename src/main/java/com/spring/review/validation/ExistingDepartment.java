package com.spring.review.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistingDepartmentValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingDepartment {

    String message() default "Department tidak ditemukan";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
