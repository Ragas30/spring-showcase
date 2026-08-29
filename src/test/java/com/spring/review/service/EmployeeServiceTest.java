package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.spring.review.bean.employee.CreateEmployeeRequest;
import com.spring.review.bean.employee.EmployeeSearchRequest;
import com.spring.review.bean.employee.UpdateEmployeeRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.*;
import com.spring.review.entityView.EmployeeView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilderFactory cbf;

    @Mock
    private EntityViewManager evm;

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @Mock
    private SQLQueryFactory sqlQueryFactory;

    @InjectMocks
    private EmployeeService employeeService;

    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateEmployeeRequest();
        createRequest.setFullName("Budi Santoso");
        createRequest.setEmail("budi@test.com");
        createRequest.setPhoneNumber("081234567890");
        createRequest.setGender(Gender.MALE);
        createRequest.setBirthDate(LocalDate.of(1990, 5, 15));
        createRequest.setHireDate(LocalDate.of(2022, 1, 10));
        createRequest.setStatus(EmployeeStatus.ACTIVE);
        createRequest.setDepartmentId(1L);
        createRequest.setPositionId(1L);

        updateRequest = new UpdateEmployeeRequest();
        updateRequest.setFullName("Budi Santoso Updated");
        updateRequest.setEmail("budi.updated@test.com");
        updateRequest.setPhoneNumber("081234567891");
        updateRequest.setGender(Gender.MALE);
        updateRequest.setBirthDate(LocalDate.of(1990, 5, 15));
        updateRequest.setHireDate(LocalDate.of(2022, 1, 10));
        updateRequest.setStatus(EmployeeStatus.ACTIVE);
        updateRequest.setDepartmentId(1L);
        updateRequest.setPositionId(1L);
    }

    @Test
    void createEmployee_validRequest_returnsView() {
        SQLQuery<Long> seqQuery = mock(SQLQuery.class);
        when(sqlQueryFactory.select(any(Expression.class))).thenReturn(seqQuery);
        when(seqQuery.fetchOne()).thenReturn(1L);

        DepartmentEntity dept = new DepartmentEntity();
        when(em.find(DepartmentEntity.class, 1L)).thenReturn(dept);

        PositionEntity pos = new PositionEntity();
        when(em.find(PositionEntity.class, 1L)).thenReturn(pos);

        EmployeeView mockView = mock(EmployeeView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        EmployeeView result = employeeService.createEmployee(createRequest);

        assertNotNull(result);
        verify(em).persist(any(EmployeeEntity.class));
        verify(em).flush();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createEmployee_duplicateEmail_throwsException() {
        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(1L);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> employeeService.createEmployee(createRequest)
        );
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getCode());
    }

    @Test
    void getEmployeeById_existingId_returnsView() {
        EmployeeEntity employee = new EmployeeEntity();
        when(em.find(EmployeeEntity.class, 1L)).thenReturn(employee);

        EmployeeView mockView = mock(EmployeeView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        EmployeeView result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
    }

    @Test
    void getEmployeeById_nonExistingId_throwsException() {
        when(em.find(EmployeeEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> employeeService.getEmployeeById(999L)
        );
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateEmployee_validRequest_returnsView() {
        EmployeeEntity employee = EmployeeEntity.builder()
                .id(1L)
                .employeeCode("EMP0001")
                .fullName("Old Name")
                .build();
        when(em.find(EmployeeEntity.class, 1L)).thenReturn(employee);

        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(0L);

        DepartmentEntity dept = new DepartmentEntity();
        when(em.find(DepartmentEntity.class, 1L)).thenReturn(dept);

        PositionEntity pos = new PositionEntity();
        when(em.find(PositionEntity.class, 1L)).thenReturn(pos);

        EmployeeView mockView = mock(EmployeeView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        EmployeeView result = employeeService.updateEmployee(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Budi Santoso Updated", employee.getFullName());
    }

    @Test
    void updateEmployee_nonExistingId_throwsException() {
        when(em.find(EmployeeEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> employeeService.updateEmployee(999L, updateRequest)
        );
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getCode());
    }

    @Test
    void deleteEmployee_existingId_removesEntity() {
        EmployeeEntity employee = new EmployeeEntity();
        when(em.find(EmployeeEntity.class, 1L)).thenReturn(employee);

        employeeService.deleteEmployee(1L);

        verify(em).remove(employee);
    }

    @Test
    void deleteEmployee_nonExistingId_throwsException() {
        when(em.find(EmployeeEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> employeeService.deleteEmployee(999L)
        );
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getCode());
    }
}
