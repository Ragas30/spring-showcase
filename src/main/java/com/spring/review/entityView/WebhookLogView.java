package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.WebhookLogEntity;

import java.time.LocalDateTime;

@EntityView(WebhookLogEntity.class)
public interface WebhookLogView {

    Long getId();

    Long getSubscriptionId();

    String getEventName();

    String getPayload();

    String getResponse();

    String getStatus();

    LocalDateTime getCreatedAt();
}
