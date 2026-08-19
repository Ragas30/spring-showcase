package com.spring.review.validation;

import com.spring.review.entity.DepartmentEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistingDepartmentValidator implements ConstraintValidator<ExistingDepartment, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean isValid(Long departmentId, ConstraintValidatorContext context) {
        if (departmentId == null) {
            return true;
        }

        Long count = em.createQuery(
                "SELECT COUNT(d) FROM DepartmentEntity d WHERE d.id = :id",
                Long.class
        ).setParameter("id", departmentId).getSingleResult();

        return count > 0;
    }
}
