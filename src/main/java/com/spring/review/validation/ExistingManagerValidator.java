package com.spring.review.validation;

import com.spring.review.entity.EmployeeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistingManagerValidator implements ConstraintValidator<ExistingManager, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean isValid(Long managerId, ConstraintValidatorContext context) {
        if (managerId == null) {
            return true;
        }

        Long count = em.createQuery(
                "SELECT COUNT(e) FROM EmployeeEntity e WHERE e.id = :id",
                Long.class
        ).setParameter("id", managerId).getSingleResult();

        return count > 0;
    }
}
