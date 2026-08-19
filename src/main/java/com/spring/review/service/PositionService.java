package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.bean.position.CreatePositionRequest;
import com.spring.review.bean.position.PositionSearchRequest;
import com.spring.review.bean.position.UpdatePositionRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.config.Auditable;
import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entity.PositionEntity;
import com.spring.review.entityView.PositionView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.review.entity.QPositionEntity.positionEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final JPAQueryFactory jpaQueryFactory;

    private boolean existsByName(String name) {
        Long count = jpaQueryFactory
                .select(positionEntity.count())
                .from(positionEntity)
                .where(positionEntity.name.eq(name))
                .fetchOne();
        return count > 0;
    }

    private boolean existsByNameExceptId(String name, Long id) {
        Long count = jpaQueryFactory
                .select(positionEntity.count())
                .from(positionEntity)
                .where(positionEntity.name.eq(name)
                        .and(positionEntity.id.ne(id)))
                .fetchOne();
        return count > 0;
    }

    private String generatePositionCode() {
        Long seq = em.createQuery(
                "SELECT nextval('pos_code_seq')", Long.class
        ).getSingleResult();
        return String.format("POS%03d", seq);
    }

    private PositionEntity findPositionById(Long id) {
        PositionEntity position = em.find(PositionEntity.class, id);
        if (position == null) {
            throw new BusinessException(ErrorCode.POSITION_NOT_FOUND, "Position not found");
        }
        return position;
    }

    private DepartmentEntity findDepartmentById(Long id) {
        if (id == null) return null;
        DepartmentEntity department = em.find(DepartmentEntity.class, id);
        if (department == null) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND, "Department not found");
        }
        return department;
    }

    private PositionView toView(Long id) {
        return evm.applySetting(
                        EntityViewSetting.create(PositionView.class),
                        cbf.create(em, PositionEntity.class).where("id").eq(id)
                )
                .getSingleResult();
    }

    private BooleanExpression buildFilters(PositionSearchRequest request) {
        BooleanExpression predicate = Expressions.TRUE;

        if (request.getName() != null && !request.getName().isBlank()) {
            predicate = predicate.and(positionEntity.name.contains(request.getName()));
        }
        if (request.getPositionCode() != null && !request.getPositionCode().isBlank()) {
            predicate = predicate.and(positionEntity.positionCode.contains(request.getPositionCode()));
        }
        if (request.getDepartmentId() != null) {
            predicate = predicate.and(positionEntity.department.id.eq(request.getDepartmentId()));
        }
        if (request.getIsActive() != null) {
            predicate = predicate.and(positionEntity.isActive.eq(request.getIsActive()));
        }

        return predicate;
    }

    @Auditable(action = "CREATE", entityType = "Position")
    public PositionView createPosition(CreatePositionRequest request) {
        if (existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Position name already exists");
        }

        PositionEntity position = PositionEntity.builder()
                .positionCode(generatePositionCode())
                .name(request.getName())
                .description(request.getDescription())
                .department(findDepartmentById(request.getDepartmentId()))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        em.persist(position);
        em.flush();
        return toView(position.getId());
    }

    public PositionView getPositionById(Long id) {
        findPositionById(id);
        return toView(id);
    }

    public PageResponse<PositionView> getPositions(PositionSearchRequest request) {
        BooleanExpression predicate = buildFilters(request);

        Long totalElements = jpaQueryFactory
                .select(positionEntity.count())
                .from(positionEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(positionEntity.id)
                .from(positionEntity)
                .where(predicate)
                .orderBy(positionEntity.id.asc())
                .offset((long) request.getPage() * request.getSize())
                .limit(request.getSize())
                .fetch();

        List<PositionView> content = ids.stream()
                .map(this::toView)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        return PageResponse.<PositionView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(totalPages)
                .build();
    }

    @Auditable(action = "UPDATE", entityType = "Position")
    public PositionView updatePosition(Long id, UpdatePositionRequest request) {
        PositionEntity position = findPositionById(id);

        if (existsByNameExceptId(request.getName(), id)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Position name already exists");
        }

        position.setName(request.getName());
        position.setDescription(request.getDescription());
        if (request.getDepartmentId() != null) {
            position.setDepartment(findDepartmentById(request.getDepartmentId()));
        }
        if (request.getIsActive() != null) {
            position.setIsActive(request.getIsActive());
        }
        position.setUpdatedAt(LocalDateTime.now());

        em.flush();
        return toView(position.getId());
    }

    @Auditable(action = "DELETE", entityType = "Position")
    public void deletePosition(Long id) {
        PositionEntity position = findPositionById(id);
        em.remove(position);
    }
}
