package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.AuditLogEntity;
import com.spring.review.entityView.AuditLogView;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.review.entity.QAuditLogEntity.auditLogEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final JPAQueryFactory jpaQueryFactory;

    public void record(
            String entityType, Long entityId, String action,
            String oldValues, String newValues, String performedBy
    ) {
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValues(oldValues)
                .newValues(newValues)
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .build();

        em.persist(auditLog);
    }

    public PageResponse<AuditLogView> getAuditLogs(
            String entityType, String action, int page, int size
    ) {
        BooleanExpression predicate = buildFilters(entityType, action);

        Long totalElements = jpaQueryFactory
                .select(auditLogEntity.count())
                .from(auditLogEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(auditLogEntity.id)
                .from(auditLogEntity)
                .where(predicate)
                .orderBy(auditLogEntity.performedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        List<AuditLogView> content = ids.stream()
                .map(id -> evm.applySetting(
                                EntityViewSetting.create(AuditLogView.class),
                                cbf.create(em, AuditLogEntity.class).where("id").eq(id)
                        )
                        .getSingleResult()
                )
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<AuditLogView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    private BooleanExpression buildFilters(String entityType, String action) {
        BooleanExpression predicate = Expressions.TRUE;

        if (entityType != null && !entityType.isBlank()) {
            predicate = predicate.and(auditLogEntity.entityType.eq(entityType));
        }
        if (action != null && !action.isBlank()) {
            predicate = predicate.and(auditLogEntity.action.eq(action));
        }

        return predicate;
    }
}
