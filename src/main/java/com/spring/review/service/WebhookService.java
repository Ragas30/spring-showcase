package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.bean.webhook.CreateWebhookRequest;
import com.spring.review.bean.webhook.UpdateWebhookRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.WebhookLogEntity;
import com.spring.review.entity.WebhookSubscriptionEntity;
import com.spring.review.entityView.WebhookLogView;
import com.spring.review.entityView.WebhookSubscriptionView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.review.entity.QWebhookSubscriptionEntity.webhookSubscriptionEntity;
import static com.spring.review.entity.QWebhookLogEntity.webhookLogEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class WebhookService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final JPAQueryFactory jpaQueryFactory;

    public WebhookSubscriptionView create(CreateWebhookRequest request) {
        WebhookSubscriptionEntity entity = WebhookSubscriptionEntity.builder()
                .name(request.getName())
                .url(request.getUrl())
                .secretKey(request.getSecretKey())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        em.persist(entity);
        em.flush();
        return toView(entity.getId());
    }

    public WebhookSubscriptionView getById(Long id) {
        return toView(id);
    }

    public PageResponse<WebhookSubscriptionView> getWebhooks(
            String name, Boolean isActive, int page, int size
    ) {
        BooleanExpression predicate = buildFilters(name, isActive);

        Long totalElements = jpaQueryFactory
                .select(webhookSubscriptionEntity.count())
                .from(webhookSubscriptionEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(webhookSubscriptionEntity.id)
                .from(webhookSubscriptionEntity)
                .where(predicate)
                .orderBy(webhookSubscriptionEntity.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        List<WebhookSubscriptionView> content = ids.stream()
                .map(this::toView)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<WebhookSubscriptionView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    public WebhookSubscriptionView update(Long id, UpdateWebhookRequest request) {
        WebhookSubscriptionEntity entity = findById(id);

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getUrl() != null) entity.setUrl(request.getUrl());
        if (request.getSecretKey() != null) entity.setSecretKey(request.getSecretKey());
        if (request.getIsActive() != null) entity.setIsActive(request.getIsActive());

        em.flush();
        return toView(id);
    }

    public void delete(Long id) {
        WebhookSubscriptionEntity entity = findById(id);
        em.remove(entity);
    }

    public void recordLog(
            Long subscriptionId, String eventName,
            String payload, String response, String status
    ) {
        WebhookLogEntity log = WebhookLogEntity.builder()
                .subscriptionId(subscriptionId)
                .eventName(eventName)
                .payload(payload)
                .response(response)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        em.persist(log);
    }

    public PageResponse<WebhookLogView> getWebhookLogs(
            Long subscriptionId, String eventName,
            String status, int page, int size
    ) {
        BooleanExpression predicate = buildLogFilters(subscriptionId, eventName, status);

        Long totalElements = jpaQueryFactory
                .select(webhookLogEntity.count())
                .from(webhookLogEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(webhookLogEntity.id)
                .from(webhookLogEntity)
                .where(predicate)
                .orderBy(webhookLogEntity.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        List<WebhookLogView> content = ids.stream()
                .map(id -> evm.applySetting(
                                EntityViewSetting.create(WebhookLogView.class),
                                cbf.create(em, WebhookLogEntity.class).where("id").eq(id)
                        )
                        .getSingleResult()
                )
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<WebhookLogView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    private WebhookSubscriptionEntity findById(Long id) {
        WebhookSubscriptionEntity entity = em.find(WebhookSubscriptionEntity.class, id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Webhook subscription tidak ditemukan");
        }
        return entity;
    }

    private WebhookSubscriptionView toView(Long id) {
        return evm.applySetting(
                        EntityViewSetting.create(WebhookSubscriptionView.class),
                        cbf.create(em, WebhookSubscriptionEntity.class).where("id").eq(id)
                )
                .getSingleResult();
    }

    private BooleanExpression buildFilters(String name, Boolean isActive) {
        BooleanExpression predicate = Expressions.TRUE;

        if (name != null && !name.isBlank()) {
            predicate = predicate.and(webhookSubscriptionEntity.name.contains(name));
        }
        if (isActive != null) {
            predicate = predicate.and(webhookSubscriptionEntity.isActive.eq(isActive));
        }

        return predicate;
    }

    private BooleanExpression buildLogFilters(
            Long subscriptionId, String eventName, String status
    ) {
        BooleanExpression predicate = Expressions.TRUE;

        if (subscriptionId != null) {
            predicate = predicate.and(webhookLogEntity.subscriptionId.eq(subscriptionId));
        }
        if (eventName != null && !eventName.isBlank()) {
            predicate = predicate.and(webhookLogEntity.eventName.eq(eventName));
        }
        if (status != null && !status.isBlank()) {
            predicate = predicate.and(webhookLogEntity.status.eq(status));
        }

        return predicate;
    }
}
