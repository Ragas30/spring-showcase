package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.spring.review.bean.employee.CreateEmployeeRequest;
import com.spring.review.bean.employee.EmployeeSearchRequest;
import com.spring.review.bean.employee.UpdateEmployeeRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.common.PageSpec;
import com.spring.review.entity.EmployeeEntity;
import com.spring.review.entityView.EmployeeView;
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
public class EmployeeService {

    /**
     * JPA Entity Manager
     */
    private final EntityManager em;

    /**
     * Blaze Persistence Criteria Builder Factory
     */
    private final CriteriaBuilderFactory cbf;

    /**
     * Blaze Persistence Entity View Manager
     */
    private final EntityViewManager evm;

    // HELPER METHODS

    /**
     * Validasi apakah email sudah digunakan employee lain
     */
    private boolean existsByEmail(
            String email
    ) {

        Long count = cbf.create(em, Long.class)
                .from(EmployeeEntity.class)
                .select("COUNT(id)")
                .where("email")
                .eq(email)
                .getSingleResult();

        return count > 0;
    }

    /**
     * Validasi email saat update
     * Mengabaikan employee yang sedang diupdate
     */
    private boolean existsByEmailExceptId(
            String email,
            Long id
    ) {

        Long count = cbf.create(em, Long.class)
                .from(EmployeeEntity.class)
                .select("COUNT(id)")
                .where("email")
                .eq(email)
                .where("id")
                .notEq(id)
                .getSingleResult();

        return count > 0;
    }

    /**
     * Generate employee code otomatis
     *
     * Contoh:
     * EMP0001
     * EMP0002
     * EMP0003
     *
     * Menggunakan nilai maksimal dari kolom employeeCode
     * (bukan MAX(id)) supaya tidak bentrok ketika
     * record terakhir dihapus.
     */
    private String generateEmployeeCode() {

        String maxCode = cbf.create(
                        em,
                        String.class
                )
                .from(EmployeeEntity.class)
                .select("MAX(employeeCode)")
                .getSingleResult();

        int next = 1;

        if (maxCode != null
                && maxCode.startsWith("EMP")) {

            try {

                next = Integer.parseInt(
                        maxCode.substring(3)
                ) + 1;

            } catch (NumberFormatException e) {

                next = 1;
            }
        }

        return String.format(
                "EMP%04d",
                next
        );
    }

    /**
     * Cari employee berdasarkan id
     * Throw exception jika tidak ditemukan
     */
    private EmployeeEntity findEmployeeById(
            Long id
    ) {

        EmployeeEntity employee =
                em.find(
                        EmployeeEntity.class,
                        id
                );

        if (employee == null) {

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_NOT_FOUND,
                    "Employee not found"
            );
        }

        return employee;
    }

    /**
     * Mapping Entity → EmployeeView
     */
//    private EmployeeView toView(
//            Long id
//    ) {
//
//        return evm.find(
//                em,
//                EmployeeView.class,
//                id
//        );
//    }

    private EmployeeView toView(
            Long id
    ) {

        return evm.applySetting(
                        EntityViewSetting.create(
                                EmployeeView.class
                        ),
                        cbf.create(
                                        em,
                                        EmployeeEntity.class
                                )
                                .where("id")
                                .eq(id)
                )
                .getSingleResult();
    }

    private void applyFilters(
            CriteriaBuilder<?> cb,
            EmployeeSearchRequest request
    ) {
        if (request.getFullName() != null
                && !request.getFullName().isBlank()) {
            cb.where("fullName")
                    .like()
                    .value("%" + request.getFullName() + "%")
                    .noEscape();
        }
        if (request.getEmployeeCode() != null
                && !request.getEmployeeCode().isBlank()) {
            cb.where("employeeCode")
                    .like()
                    .value("%" + request.getEmployeeCode() + "%")
                    .noEscape();
        }
        if (request.getEmail() != null
                && !request.getEmail().isBlank()) {
            cb.where("email")
                    .like()
                    .value("%" + request.getEmail() + "%")
                    .noEscape();
        }
        if (request.getGender() != null) {
            cb.where("gender")
                    .eq(request.getGender());
        }
        if (request.getStatus() != null) {
            cb.where("status")
                    .eq(request.getStatus());
        }
    }

    /**
     * Create Employee
     */
    public EmployeeView createEmployee(
            CreateEmployeeRequest request
    ) {

        if (request.getEmail() != null
                && existsByEmail(
                request.getEmail()
        )) {

            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Email already exists"
            );
        }

        EmployeeEntity employee =
                EmployeeEntity.builder()
                        .employeeCode(
                                generateEmployeeCode()
                        )
                        .fullName(
                                request.getFullName()
                        )
                        .email(
                                request.getEmail()
                        )
                        .phoneNumber(
                                request.getPhoneNumber()
                        )
                        .gender(
                                request.getGender()
                        )
                        .birthDate(
                                request.getBirthDate()
                        )
                        .hireDate(
                                request.getHireDate()
                        )
                        .status(
                                request.getStatus()
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        em.persist(employee);

        em.flush();

        return toView(
                employee.getId()
        );

//        return null;
    }

    /**
     * Get Employee By Id
     */
    public EmployeeView getEmployeeById(
            Long id
    ) {

        EmployeeEntity employee =
                findEmployeeById(id);

        return toView(
                employee.getId()
        );
    }

    /**
     * Get All Employees
     */
    public PageResponse<EmployeeView> getEmployees(
            EmployeeSearchRequest request
    ) {

        var countCb = cbf.create(
                        em,
                        Long.class
                )
                .from(EmployeeEntity.class)
                .select("COUNT(id)");
        applyFilters(countCb, request);
        Long totalElements = countCb.getSingleResult();

        var dataCb = cbf.create(
                em,
                EmployeeEntity.class
        );
        applyFilters(dataCb, request);
        dataCb.orderByAsc("id");

        List<EmployeeView> content =
                evm.applySetting(
                                EntityViewSetting.create(
                                        EmployeeView.class
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

        return PageResponse.<EmployeeView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(totalPages)
                .build();
    }

    /**
     * Update Employee
     */
    public EmployeeView updateEmployee(
            Long id,
            UpdateEmployeeRequest request
    ) {

        EmployeeEntity employee =
                findEmployeeById(id);

        if (request.getEmail() != null
                && existsByEmailExceptId(
                request.getEmail(),
                id
        )) {

            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Email already exists"
            );
        }

        employee.setFullName(
                request.getFullName()
        );

        employee.setEmail(
                request.getEmail()
        );

        employee.setPhoneNumber(
                request.getPhoneNumber()
        );

        employee.setGender(
                request.getGender()
        );

        employee.setBirthDate(
                request.getBirthDate()
        );

        employee.setHireDate(
                request.getHireDate()
        );

        employee.setStatus(
                request.getStatus()
        );

        employee.setUpdatedAt(
                LocalDateTime.now()
        );

        /**
         * Tidak perlu em.merge()
         * karena entity hasil em.find()
         * sudah managed oleh persistence context
         */

        em.flush();

        return toView(
                employee.getId()
        );
    }

    /**
     * Delete Employee
     */
    public void deleteEmployee(
            Long id
    ) {

        EmployeeEntity employee =
                findEmployeeById(id);

        em.remove(employee);
    }
}