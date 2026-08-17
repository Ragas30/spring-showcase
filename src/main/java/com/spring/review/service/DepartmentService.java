package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.spring.review.bean.department.CreateDepartmentRequest;
import com.spring.review.bean.department.DepartmentSearchRequest;
import com.spring.review.bean.department.UpdateDepartmentRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entityView.DepartmentView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private boolean existsByName(String name) {

        Long count = cbf.create(em, Long.class)
                .from(DepartmentEntity.class)
                .select("COUNT(id)")
                .where("name")
                .eq(name)
                .getSingleResult();

        return count > 0;
    }

    private boolean existsByNameExceptId(
            String name,
            Long id
    ) {

        Long count = cbf.create(em, Long.class)
                .from(DepartmentEntity.class)
                .select("COUNT(id)")
                .where("name")
                .eq(name)
                .where("id")
                .notEq(id)
                .getSingleResult();

        return count > 0;
    }

    private String generateDepartmentCode() {

        String maxCode = cbf.create(
                        em,
                        String.class
                )
                .from(DepartmentEntity.class)
                .select("MAX(departmentCode)")
                .getSingleResult();

        int next = 1;

        if (maxCode != null
                && maxCode.startsWith("DEPT")) {

            try {

                next = Integer.parseInt(
                        maxCode.substring(4)
                ) + 1;

            } catch (NumberFormatException e) {

                next = 1;
            }
        }

        return String.format(
                "DEPT%03d",
                next
        );
    }

    private DepartmentEntity findDepartmentById(
            Long id
    ) {

        DepartmentEntity department =
                em.find(
                        DepartmentEntity.class,
                        id
                );

        if (department == null) {

            throw new BusinessException(
                    ErrorCode.DEPARTMENT_NOT_FOUND,
                    "Department not found"
            );
        }

        return department;
    }

    private DepartmentView toView(Long id) {

        return evm.applySetting(
                        EntityViewSetting.create(
                                DepartmentView.class
                        ),
                        cbf.create(
                                        em,
                                        DepartmentEntity.class
                                )
                                .where("id")
                                .eq(id)
                )
                .getSingleResult();
    }

    private void applyFilters(
            CriteriaBuilder<?> cb,
            DepartmentSearchRequest request
    ) {

        if (request.getName() != null
                && !request.getName().isBlank()) {
            cb.where("name")
                    .like()
                    .value("%" + request.getName() + "%")
                    .noEscape();
        }
        if (request.getDepartmentCode() != null
                && !request.getDepartmentCode().isBlank()) {
            cb.where("departmentCode")
                    .like()
                    .value("%" + request.getDepartmentCode() + "%")
                    .noEscape();
        }
        if (request.getIsActive() != null) {
            cb.where("isActive")
                    .eq(request.getIsActive());
        }
    }

    public DepartmentView createDepartment(
            CreateDepartmentRequest request
    ) {

        if (existsByName(request.getName())) {

            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Department name already exists"
            );
        }

        DepartmentEntity department =
                DepartmentEntity.builder()
                        .departmentCode(
                                generateDepartmentCode()
                        )
                        .name(
                                request.getName()
                        )
                        .description(
                                request.getDescription()
                        )
                        .isActive(true)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        em.persist(department);
        em.flush();

        return toView(department.getId());
    }

    public DepartmentView getDepartmentById(
            Long id
    ) {

        DepartmentEntity department =
                findDepartmentById(id);

        return toView(department.getId());
    }

    public PageResponse<DepartmentView> getDepartments(
            DepartmentSearchRequest request
    ) {

        var countCb = cbf.create(
                        em,
                        Long.class
                )
                .from(DepartmentEntity.class)
                .select("COUNT(id)");
        applyFilters(countCb, request);
        Long totalElements = countCb.getSingleResult();

        var dataCb = cbf.create(
                em,
                DepartmentEntity.class
        );
        applyFilters(dataCb, request);
        dataCb.orderByAsc("id");

        List<DepartmentView> content =
                evm.applySetting(
                                EntityViewSetting.create(
                                        DepartmentView.class
                                ),
                                dataCb
                        )
                        .setFirstResult(
                                request.getPage()
                                        * request.getSize()
                        )
                        .setMaxResults(
                                request.getSize()
                        )
                        .getResultList();

        int totalPages =
                (int) Math.ceil(
                        (double) totalElements
                                / request.getSize()
                );

        return PageResponse.<DepartmentView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(totalPages)
                .build();
    }

    public DepartmentView updateDepartment(
            Long id,
            UpdateDepartmentRequest request
    ) {

        DepartmentEntity department =
                findDepartmentById(id);

        if (existsByNameExceptId(
                request.getName(),
                id
        )) {

            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Department name already exists"
            );
        }

        department.setName(
                request.getName()
        );

        department.setDescription(
                request.getDescription()
        );

        if (request.getIsActive() != null) {
            department.setIsActive(
                    request.getIsActive()
            );
        }

        department.setUpdatedAt(
                LocalDateTime.now()
        );

        em.flush();

        return toView(department.getId());
    }

    public void deleteDepartment(Long id) {

        DepartmentEntity department =
                findDepartmentById(id);

        em.remove(department);
    }
}
