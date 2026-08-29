package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import com.spring.review.bean.department.CreateDepartmentRequest;
import com.spring.review.bean.department.DepartmentSearchRequest;
import com.spring.review.bean.department.UpdateDepartmentRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.config.Auditable;
import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entityView.DepartmentView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.review.entity.QDepartmentEntity.departmentEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final JPAQueryFactory jpaQueryFactory;

    private final SQLQueryFactory sqlQueryFactory;

    private boolean existsByName(String name) {
        Long count = jpaQueryFactory
                .select(departmentEntity.count())
                .from(departmentEntity)
                .where(departmentEntity.name.eq(name))
                .fetchOne();
        return count > 0;
    }

    private boolean existsByNameExceptId(String name, Long id) {
        Long count = jpaQueryFactory
                .select(departmentEntity.count())
                .from(departmentEntity)
                .where(departmentEntity.name.eq(name)
                        .and(departmentEntity.id.ne(id)))
                .fetchOne();
        return count > 0;
    }

    private String generateDepartmentCode() {
        Long seq = sqlQueryFactory
                .select(Expressions.numberTemplate(
                        Long.class,
                        "nextval('dept_code_seq')"
                ))
                .fetchOne();
        return String.format("DEPT%03d", seq);
    }

    private DepartmentEntity findDepartmentById(Long id) {
        DepartmentEntity department = em.find(DepartmentEntity.class, id);
        if (department == null) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND, "Department not found");
        }
        return department;
    }

    private DepartmentView toView(Long id) {
        return evm.applySetting(
                        EntityViewSetting.create(DepartmentView.class),
                        cbf.create(em, DepartmentEntity.class).where("id").eq(id)
                )
                .getSingleResult();
    }

    private BooleanExpression buildFilters(DepartmentSearchRequest request) {
        BooleanExpression predicate = Expressions.TRUE;

        if (request.getName() != null && !request.getName().isBlank()) {
            predicate = predicate.and(departmentEntity.name.contains(request.getName()));
        }
        if (request.getDepartmentCode() != null && !request.getDepartmentCode().isBlank()) {
            predicate = predicate.and(departmentEntity.departmentCode.contains(request.getDepartmentCode()));
        }
        if (request.getIsActive() != null) {
            predicate = predicate.and(departmentEntity.isActive.eq(request.getIsActive()));
        }

        return predicate;
    }

    @Auditable(action = "CREATE", entityType = "Department")
    public DepartmentView createDepartment(CreateDepartmentRequest request) {
        if (existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Department name already exists");
        }

        DepartmentEntity department = DepartmentEntity.builder()
                .departmentCode(generateDepartmentCode())
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        em.persist(department);
        em.flush();
        return toView(department.getId());
    }

    public DepartmentView getDepartmentById(Long id) {
        findDepartmentById(id);
        return toView(id);
    }

    public PageResponse<DepartmentView> getDepartments(DepartmentSearchRequest request) {
        BooleanExpression predicate = buildFilters(request);

        Long totalElements = jpaQueryFactory
                .select(departmentEntity.count())
                .from(departmentEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(departmentEntity.id)
                .from(departmentEntity)
                .where(predicate)
                .orderBy(departmentEntity.id.asc())
                .offset((long) request.getPage() * request.getSize())
                .limit(request.getSize())
                .fetch();

        List<DepartmentView> content = ids.stream()
                .map(this::toView)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        return PageResponse.<DepartmentView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(totalPages)
                .build();
    }

    @Auditable(action = "UPDATE", entityType = "Department")
    public DepartmentView updateDepartment(Long id, UpdateDepartmentRequest request) {
        DepartmentEntity department = findDepartmentById(id);

        if (existsByNameExceptId(request.getName(), id)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Department name already exists");
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            department.setIsActive(request.getIsActive());
        }
        department.setUpdatedAt(LocalDateTime.now());

        em.flush();
        return toView(department.getId());
    }

    @Auditable(action = "DELETE", entityType = "Department")
    public void deleteDepartment(Long id) {
        DepartmentEntity department = findDepartmentById(id);
        em.remove(department);
    }
}
