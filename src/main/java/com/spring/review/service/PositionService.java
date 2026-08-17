package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.spring.review.bean.position.CreatePositionRequest;
import com.spring.review.bean.position.PositionSearchRequest;
import com.spring.review.bean.position.UpdatePositionRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
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

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private boolean existsByName(String name) {

        Long count = cbf.create(em, Long.class)
                .from(PositionEntity.class)
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
                .from(PositionEntity.class)
                .select("COUNT(id)")
                .where("name")
                .eq(name)
                .where("id")
                .notEq(id)
                .getSingleResult();

        return count > 0;
    }

    private String generatePositionCode() {

        String maxCode = cbf.create(
                        em,
                        String.class
                )
                .from(PositionEntity.class)
                .select("MAX(positionCode)")
                .getSingleResult();

        int next = 1;

        if (maxCode != null
                && maxCode.startsWith("POS")) {

            try {

                next = Integer.parseInt(
                        maxCode.substring(3)
                ) + 1;

            } catch (NumberFormatException e) {

                next = 1;
            }
        }

        return String.format(
                "POS%03d",
                next
        );
    }

    private PositionEntity findPositionById(
            Long id
    ) {

        PositionEntity position =
                em.find(
                        PositionEntity.class,
                        id
                );

        if (position == null) {

            throw new BusinessException(
                    ErrorCode.POSITION_NOT_FOUND,
                    "Position not found"
            );
        }

        return position;
    }

    private DepartmentEntity findDepartmentById(
            Long id
    ) {

        if (id == null) {
            return null;
        }

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

    private PositionView toView(Long id) {

        return evm.applySetting(
                        EntityViewSetting.create(
                                PositionView.class
                        ),
                        cbf.create(
                                        em,
                                        PositionEntity.class
                                )
                                .where("id")
                                .eq(id)
                )
                .getSingleResult();
    }

    private void applyFilters(
            CriteriaBuilder<?> cb,
            PositionSearchRequest request
    ) {

        if (request.getName() != null
                && !request.getName().isBlank()) {
            cb.where("name")
                    .like()
                    .value("%" + request.getName() + "%")
                    .noEscape();
        }
        if (request.getPositionCode() != null
                && !request.getPositionCode().isBlank()) {
            cb.where("positionCode")
                    .like()
                    .value("%" + request.getPositionCode() + "%")
                    .noEscape();
        }
        if (request.getDepartmentId() != null) {
            cb.where("department.id")
                    .eq(request.getDepartmentId());
        }
        if (request.getIsActive() != null) {
            cb.where("isActive")
                    .eq(request.getIsActive());
        }
    }

    public PositionView createPosition(
            CreatePositionRequest request
    ) {

        if (existsByName(request.getName())) {

            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Position name already exists"
            );
        }

        PositionEntity position =
                PositionEntity.builder()
                        .positionCode(
                                generatePositionCode()
                        )
                        .name(
                                request.getName()
                        )
                        .description(
                                request.getDescription()
                        )
                        .department(
                                findDepartmentById(
                                        request.getDepartmentId()
                                )
                        )
                        .isActive(true)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        em.persist(position);
        em.flush();

        return toView(position.getId());
    }

    public PositionView getPositionById(
            Long id
    ) {

        PositionEntity position =
                findPositionById(id);

        return toView(position.getId());
    }

    public PageResponse<PositionView> getPositions(
            PositionSearchRequest request
    ) {

        var countCb = cbf.create(
                        em,
                        Long.class
                )
                .from(PositionEntity.class)
                .select("COUNT(id)");
        applyFilters(countCb, request);
        Long totalElements = countCb.getSingleResult();

        var dataCb = cbf.create(
                em,
                PositionEntity.class
        );
        applyFilters(dataCb, request);
        dataCb.orderByAsc("id");

        List<PositionView> content =
                evm.applySetting(
                                EntityViewSetting.create(
                                        PositionView.class
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

        return PageResponse.<PositionView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(totalPages)
                .build();
    }

    public PositionView updatePosition(
            Long id,
            UpdatePositionRequest request
    ) {

        PositionEntity position =
                findPositionById(id);

        if (existsByNameExceptId(
                request.getName(),
                id
        )) {

            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Position name already exists"
            );
        }

        position.setName(
                request.getName()
        );

        position.setDescription(
                request.getDescription()
        );

        if (request.getDepartmentId() != null) {
            position.setDepartment(
                    findDepartmentById(
                            request.getDepartmentId()
                    )
            );
        }

        if (request.getIsActive() != null) {
            position.setIsActive(
                    request.getIsActive()
            );
        }

        position.setUpdatedAt(
                LocalDateTime.now()
        );

        em.flush();

        return toView(position.getId());
    }

    public void deletePosition(Long id) {

        PositionEntity position =
                findPositionById(id);

        em.remove(position);
    }
}
