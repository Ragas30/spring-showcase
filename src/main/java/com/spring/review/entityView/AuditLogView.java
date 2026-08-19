package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.AuditLogEntity;

import java.time.LocalDateTime;

@EntityView(AuditLogEntity.class)
public interface AuditLogView {

    Long getId();

    String getEntityType();

    Long getEntityId();

    String getAction();

    String getOldValues();

    String getNewValues();

    String getPerformedBy();

    LocalDateTime getPerformedAt();
}
