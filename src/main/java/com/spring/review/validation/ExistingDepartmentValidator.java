package com.spring.review.validation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.spring.review.entity.QDepartmentEntity.departmentEntity;

@Component
public class ExistingDepartmentValidator implements ConstraintValidator<ExistingDepartment, Long> {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean isValid(Long departmentId, ConstraintValidatorContext context) {
        if (departmentId == null) {
            return true;
        }

        Long count = jpaQueryFactory
                .select(departmentEntity.count())
                .from(departmentEntity)
                .where(departmentEntity.id.eq(departmentId))
                .fetchOne();

        return count > 0;
    }
}
