package com.spring.review.validation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.spring.review.entity.QPositionEntity.positionEntity;

@Component
public class ExistingPositionValidator implements ConstraintValidator<ExistingPosition, Long> {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean isValid(Long positionId, ConstraintValidatorContext context) {
        if (positionId == null) {
            return true;
        }

        Long count = jpaQueryFactory
                .select(positionEntity.count())
                .from(positionEntity)
                .where(positionEntity.id.eq(positionId))
                .fetchOne();

        return count > 0;
    }
}
