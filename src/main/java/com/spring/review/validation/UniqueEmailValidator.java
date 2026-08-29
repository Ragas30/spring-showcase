package com.spring.review.validation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.spring.review.entity.QEmployeeEntity.employeeEntity;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }

        Long count = jpaQueryFactory
                .select(employeeEntity.count())
                .from(employeeEntity)
                .where(employeeEntity.email.eq(email))
                .fetchOne();

        return count == 0;
    }
}
