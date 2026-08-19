package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.bean.employee.CreateEmployeeRequest;
import com.spring.review.bean.employee.EmployeeSearchRequest;
import com.spring.review.bean.employee.UpdateEmployeeRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.config.Auditable;
import com.spring.review.entity.*;
import com.spring.review.entityView.EmployeeView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.review.entity.QEmployeeEntity.employeeEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final JPAQueryFactory jpaQueryFactory;

    private boolean existsByEmail(String email) {
        Long count = jpaQueryFactory
                .select(employeeEntity.count())
                .from(employeeEntity)
                .where(employeeEntity.email.eq(email))
                .fetchOne();
        return count > 0;
    }

    private boolean existsByEmailExceptId(String email, Long id) {
        Long count = jpaQueryFactory
                .select(employeeEntity.count())
                .from(employeeEntity)
                .where(employeeEntity.email.eq(email)
                        .and(employeeEntity.id.ne(id)))
                .fetchOne();
        return count > 0;
    }

    private String generateEmployeeCode() {
        Long seq = em.createQuery(
                "SELECT nextval('emp_code_seq')", Long.class
        ).getSingleResult();
        return String.format("EMP%04d", seq);
    }

    private EmployeeEntity findEmployeeById(Long id) {
        EmployeeEntity employee = em.find(EmployeeEntity.class, id);
        if (employee == null) {
            throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND, "Employee not found");
        }
        return employee;
    }

    private DepartmentEntity findDepartmentById(Long id) {
        if (id == null) return null;
        DepartmentEntity department = em.find(DepartmentEntity.class, id);
        if (department == null) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND, "Department not found");
        }
        return department;
    }

    private PositionEntity findPositionById(Long id) {
        if (id == null) return null;
        PositionEntity position = em.find(PositionEntity.class, id);
        if (position == null) {
            throw new BusinessException(ErrorCode.POSITION_NOT_FOUND, "Position not found");
        }
        return position;
    }

    private EmployeeView toView(Long id) {
        return evm.applySetting(
                        EntityViewSetting.create(EmployeeView.class),
                        cbf.create(em, EmployeeEntity.class).where("id").eq(id)
                )
                .getSingleResult();
    }

    private BooleanExpression buildFilters(EmployeeSearchRequest request) {
        BooleanExpression predicate = Expressions.TRUE;

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            predicate = predicate.and(employeeEntity.fullName.contains(request.getFullName()));
        }
        if (request.getEmployeeCode() != null && !request.getEmployeeCode().isBlank()) {
            predicate = predicate.and(employeeEntity.employeeCode.contains(request.getEmployeeCode()));
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            predicate = predicate.and(employeeEntity.email.contains(request.getEmail()));
        }
        if (request.getGender() != null) {
            predicate = predicate.and(employeeEntity.gender.eq(request.getGender()));
        }
        if (request.getStatus() != null) {
            predicate = predicate.and(employeeEntity.status.eq(request.getStatus()));
        }
        if (request.getDepartmentId() != null) {
            predicate = predicate.and(employeeEntity.department.id.eq(request.getDepartmentId()));
        }
        if (request.getPositionId() != null) {
            predicate = predicate.and(employeeEntity.position.id.eq(request.getPositionId()));
        }

        return predicate;
    }

    @Auditable(action = "CREATE", entityType = "Employee")
    public EmployeeView createEmployee(CreateEmployeeRequest request) {
        if (request.getEmail() != null && existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
        }

        EmployeeEntity employee = EmployeeEntity.builder()
                .employeeCode(generateEmployeeCode())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .hireDate(request.getHireDate())
                .status(request.getStatus())
                .department(findDepartmentById(request.getDepartmentId()))
                .position(findPositionById(request.getPositionId()))
                .photoUrl(request.getPhotoUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        em.persist(employee);
        em.flush();
        return toView(employee.getId());
    }

    public EmployeeView getEmployeeById(Long id) {
        findEmployeeById(id);
        return toView(id);
    }

    public PageResponse<EmployeeView> getEmployees(EmployeeSearchRequest request) {
        BooleanExpression predicate = buildFilters(request);

        Long totalElements = jpaQueryFactory
                .select(employeeEntity.count())
                .from(employeeEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(employeeEntity.id)
                .from(employeeEntity)
                .where(predicate)
                .orderBy(employeeEntity.id.asc())
                .offset((long) request.getPage() * request.getSize())
                .limit(request.getSize())
                .fetch();

        List<EmployeeView> content = ids.stream()
                .map(this::toView)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        return PageResponse.<EmployeeView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(totalPages)
                .build();
    }

    @Auditable(action = "UPDATE", entityType = "Employee")
    public EmployeeView updateEmployee(Long id, UpdateEmployeeRequest request) {
        EmployeeEntity employee = findEmployeeById(id);

        if (request.getEmail() != null && existsByEmailExceptId(request.getEmail(), id)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
        }

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setGender(request.getGender());
        employee.setBirthDate(request.getBirthDate());
        employee.setHireDate(request.getHireDate());
        employee.setStatus(request.getStatus());
        employee.setDepartment(findDepartmentById(request.getDepartmentId()));
        employee.setPosition(findPositionById(request.getPositionId()));
        employee.setPhotoUrl(request.getPhotoUrl());
        employee.setUpdatedAt(LocalDateTime.now());

        em.flush();
        return toView(employee.getId());
    }

    @Auditable(action = "DELETE", entityType = "Employee")
    public void deleteEmployee(Long id) {
        EmployeeEntity employee = findEmployeeById(id);
        em.remove(employee);
    }
}
