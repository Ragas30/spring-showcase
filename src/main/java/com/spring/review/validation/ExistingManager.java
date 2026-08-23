package com.spring.review.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistingManagerValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingManager {

    String message() default "Manager tidak ditemukan";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
