package com.spring.review.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistingPositionValidator implements ConstraintValidator<ExistingPosition, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean isValid(Long positionId, ConstraintValidatorContext context) {
        if (positionId == null) {
            return true;
        }

        Long count = em.createQuery(
                "SELECT COUNT(p) FROM PositionEntity p WHERE p.id = :id",
                Long.class
        ).setParameter("id", positionId).getSingleResult();

        return count > 0;
    }
}
