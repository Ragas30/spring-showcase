package com.spring.review.validation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.spring.review.entity.QEmployeeEntity.employeeEntity;

@Component
public class ExistingManagerValidator implements ConstraintValidator<ExistingManager, Long> {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean isValid(Long managerId, ConstraintValidatorContext context) {
        if (managerId == null) {
            return true;
        }

        Long count = jpaQueryFactory
                .select(employeeEntity.count())
                .from(employeeEntity)
                .where(employeeEntity.id.eq(managerId))
                .fetchOne();

        return count > 0;
    }
}
