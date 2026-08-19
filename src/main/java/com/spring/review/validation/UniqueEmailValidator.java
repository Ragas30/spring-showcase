package com.spring.review.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @PersistenceContext
    private final EntityManager em;

    private String entityType;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        this.entityType = constraintAnnotation.entityClass().getSimpleName();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }

        Long count = em.createQuery(
                "SELECT COUNT(e) FROM " + entityType + " e WHERE e.email = :email",
                Long.class
        ).setParameter("email", email).getSingleResult();

        return count == 0;
    }
}
