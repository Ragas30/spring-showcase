package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.WebhookSubscriptionEntity;

import java.time.LocalDateTime;

@EntityView(WebhookSubscriptionEntity.class)
public interface WebhookSubscriptionView {

    Long getId();

    String getName();

    String getUrl();

    String getSecretKey();

    Boolean getIsActive();

    LocalDateTime getCreatedAt();
}
