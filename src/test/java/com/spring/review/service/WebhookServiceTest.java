package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.bean.webhook.CreateWebhookRequest;
import com.spring.review.bean.webhook.UpdateWebhookRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.entity.WebhookSubscriptionEntity;
import com.spring.review.entityView.WebhookSubscriptionView;
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
class WebhookServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilderFactory cbf;

    @Mock
    private EntityViewManager evm;

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @InjectMocks
    private WebhookService webhookService;

    private CreateWebhookRequest createRequest;
    private UpdateWebhookRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateWebhookRequest();
        createRequest.setName("Test Webhook");
        createRequest.setUrl("https://example.com/webhook");
        createRequest.setSecretKey("my-super-secret-key-1234567890123456");

        updateRequest = new UpdateWebhookRequest();
        updateRequest.setName("Updated Webhook");
    }

    @Test
    void create_validRequest_returnsView() {
        WebhookSubscriptionView mockView = mock(WebhookSubscriptionView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        WebhookSubscriptionView result = webhookService.create(createRequest);

        assertNotNull(result);
        verify(em).persist(any(WebhookSubscriptionEntity.class));
        verify(em).flush();
    }

    @Test
    void getById_existingId_returnsView() {
        WebhookSubscriptionView mockView = mock(WebhookSubscriptionView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        WebhookSubscriptionView result = webhookService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void update_existingId_returnsView() {
        WebhookSubscriptionEntity entity = WebhookSubscriptionEntity.builder()
                .id(1L)
                .name("Old Name")
                .build();
        when(em.find(WebhookSubscriptionEntity.class, 1L)).thenReturn(entity);

        WebhookSubscriptionView mockView = mock(WebhookSubscriptionView.class);
        FullQueryBuilder fqb = mock(FullQueryBuilder.class);
        when(evm.applySetting(any(EntityViewSetting.class), any())).thenReturn(fqb);
        when(fqb.getSingleResult()).thenReturn(mockView);

        WebhookSubscriptionView result = webhookService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Webhook", entity.getName());
    }

    @Test
    void update_nonExistingId_throwsException() {
        when(em.find(WebhookSubscriptionEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> webhookService.update(999L, updateRequest)
        );
        assertEquals(ErrorCode.WEBHOOK_NOT_FOUND, ex.getCode());
    }

    @Test
    void delete_existingId_removesEntity() {
        WebhookSubscriptionEntity entity = new WebhookSubscriptionEntity();
        when(em.find(WebhookSubscriptionEntity.class, 1L)).thenReturn(entity);

        webhookService.delete(1L);

        verify(em).remove(entity);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(em.find(WebhookSubscriptionEntity.class, 999L)).thenReturn(null);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> webhookService.delete(999L)
        );
        assertEquals(ErrorCode.WEBHOOK_NOT_FOUND, ex.getCode());
    }
}
