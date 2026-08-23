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
import com.spring.review.bean.position.CreatePositionRequest;
import com.spring.review.bean.position.UpdatePositionRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entity.PositionEntity;
import com.spring.review.entityView.PositionView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilderFactory cbf;

    @Mock
    private EntityViewManager evm;

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @InjectMocks
    private PositionService positionService;

    private CreatePositionRequest createRequest;
    private UpdatePositionRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreatePositionRequest();
        createRequest.setName("Software Developer");
        createRequest.setDescription("Develop software");
        createRequest.setDepartmentId(1L);

        updateRequest = new UpdatePositionRequest();
        updateRequest.setName("Senior Developer");
        updateRequest.setDescription("Senior developer role");
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPosition_validRequest_returnsView() {
        jakarta.persistence.TypedQuery<Long> posSeqQuery = mock(jakarta.persistence.TypedQuery.class);
        when(em.createQuery("SELECT nextval('pos_code_seq')", Long.class))
                .thenReturn(posSeqQuery);
        when(posSeqQuery.getSingleResult())
                .thenReturn(1L);

        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(0L);

        DepartmentEntity dept = new DepartmentEntity();
        when(em.find(DepartmentEntity.class, 1L)).thenReturn(dept);

        PositionView mockView = mock(PositionView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        PositionView result = positionService.createPosition(createRequest);

        assertNotNull(result);
        verify(em).persist(any(PositionEntity.class));
        verify(em).flush();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPosition_duplicateName_throwsException() {
        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(1L);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> positionService.createPosition(createRequest)
        );
        assertEquals(ErrorCode.CONFLICT, ex.getCode());
    }

    @Test
    void getPositionById_existingId_returnsView() {
        PositionEntity pos = new PositionEntity();
        when(em.find(PositionEntity.class, 1L)).thenReturn(pos);

        PositionView mockView = mock(PositionView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        PositionView result = positionService.getPositionById(1L);

        assertNotNull(result);
    }

    @Test
    void getPositionById_nonExistingId_throwsException() {
        when(em.find(PositionEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> positionService.getPositionById(999L)
        );
        assertEquals(ErrorCode.POSITION_NOT_FOUND, ex.getCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void updatePosition_validRequest_returnsView() {
        PositionEntity pos = PositionEntity.builder()
                .id(1L)
                .name("Old Name")
                .build();
        when(em.find(PositionEntity.class, 1L)).thenReturn(pos);

        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        when(jpaQueryFactory.<Long>select(any(Expression.class))).thenReturn(countQuery);
        when(countQuery.from(any(EntityPath.class))).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(countQuery.fetchOne()).thenReturn(0L);

        PositionView mockView = mock(PositionView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        PositionView result = positionService.updatePosition(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Senior Developer", pos.getName());
    }

    @Test
    void deletePosition_existingId_removesEntity() {
        PositionEntity pos = new PositionEntity();
        when(em.find(PositionEntity.class, 1L)).thenReturn(pos);

        positionService.deletePosition(1L);

        verify(em).remove(pos);
    }

    @Test
    void deletePosition_nonExistingId_throwsException() {
        when(em.find(PositionEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> positionService.deletePosition(999L)
        );
        assertEquals(ErrorCode.POSITION_NOT_FOUND, ex.getCode());
    }
}
