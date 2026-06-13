package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.spring.review.bean.employee.CreateEmployeeRequest;
import com.spring.review.bean.employee.UpdateEmployeeRequest;
import com.spring.review.common.ErrorCode;
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
     */
    private String generateEmployeeCode() {

        Long maxId = cbf.create(em, Long.class)
                .from(EmployeeEntity.class)
                .select("MAX(id)")
                .getSingleResult();

        if (maxId == null) {
            maxId = 0L;
        }

        return String.format(
                "EMP%04d",
                maxId + 1
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
                    ErrorCode.NOT_FOUND,
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
                    ErrorCode.BAD_REQUEST,
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
    public List<EmployeeView> getEmployees() {

        return evm.applySetting(
                        EntityViewSetting.create(
                                EmployeeView.class
                        ),
                        cbf.create(
                                em,
                                EmployeeEntity.class
                        )
                )
                .getResultList();
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
                    ErrorCode.BAD_REQUEST,
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