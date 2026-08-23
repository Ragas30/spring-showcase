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
import com.spring.review.bean.department.CreateDepartmentRequest;
import com.spring.review.bean.department.DepartmentSearchRequest;
import com.spring.review.bean.department.UpdateDepartmentRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entityView.DepartmentView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilderFactory cbf;

    @Mock
    private EntityViewManager evm;

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @InjectMocks
    private DepartmentService departmentService;

    private CreateDepartmentRequest createRequest;
    private UpdateDepartmentRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateDepartmentRequest();
        createRequest.setName("Engineering");
        createRequest.setDescription("Software development");

        updateRequest = new UpdateDepartmentRequest();
        updateRequest.setName("Engineering Updated");
        updateRequest.setDescription("Updated description");
    }

    @Test
    @SuppressWarnings("unchecked")
    void createDepartment_validRequest_returnsView() {
        jakarta.persistence.TypedQuery<Long> deptSeqQuery = mock(jakarta.persistence.TypedQuery.class);
        when(em.createQuery("SELECT nextval('dept_code_seq')", Long.class))
                .thenReturn(deptSeqQuery);
        when(deptSeqQuery.getSingleResult())
                .thenReturn(1L);

        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(0L);

        DepartmentView mockView = mock(DepartmentView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        DepartmentView result = departmentService.createDepartment(createRequest);

        assertNotNull(result);
        verify(em).persist(any(DepartmentEntity.class));
        verify(em).flush();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createDepartment_duplicateName_throwsException() {
        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(1L);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> departmentService.createDepartment(createRequest)
        );
        assertEquals(ErrorCode.CONFLICT, ex.getCode());
    }

    @Test
    void getDepartmentById_existingId_returnsView() {
        DepartmentEntity dept = new DepartmentEntity();
        when(em.find(DepartmentEntity.class, 1L)).thenReturn(dept);

        DepartmentView mockView = mock(DepartmentView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        DepartmentView result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
    }

    @Test
    void getDepartmentById_nonExistingId_throwsException() {
        when(em.find(DepartmentEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> departmentService.getDepartmentById(999L)
        );
        assertEquals(ErrorCode.DEPARTMENT_NOT_FOUND, ex.getCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateDepartment_validRequest_returnsView() {
        DepartmentEntity dept = DepartmentEntity.builder()
                .id(1L)
                .name("Old Name")
                .build();
        when(em.find(DepartmentEntity.class, 1L)).thenReturn(dept);

        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(0L);

        DepartmentView mockView = mock(DepartmentView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        DepartmentView result = departmentService.updateDepartment(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Engineering Updated", dept.getName());
    }

    @Test
    void deleteDepartment_existingId_removesEntity() {
        DepartmentEntity dept = new DepartmentEntity();
        when(em.find(DepartmentEntity.class, 1L)).thenReturn(dept);

        departmentService.deleteDepartment(1L);

        verify(em).remove(dept);
    }

    @Test
    void deleteDepartment_nonExistingId_throwsException() {
        when(em.find(DepartmentEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> departmentService.deleteDepartment(999L)
        );
        assertEquals(ErrorCode.DEPARTMENT_NOT_FOUND, ex.getCode());
    }
}
