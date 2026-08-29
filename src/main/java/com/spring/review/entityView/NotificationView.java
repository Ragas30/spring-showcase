package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.NotificationEntity;

import java.time.LocalDateTime;

@EntityView(NotificationEntity.class)
public interface NotificationView {

    Long getId();

    String getRecipientUsername();

    String getRecipientRole();

    String getTitle();

    String getMessage();

    String getType();

    String getEntityType();

    Long getEntityId();

    Boolean getIsRead();

    LocalDateTime getReadAt();

    LocalDateTime getCreatedAt();
}
