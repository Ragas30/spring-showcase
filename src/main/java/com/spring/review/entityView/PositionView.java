package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.Mapping;
import com.spring.review.entity.PositionEntity;

import java.time.LocalDateTime;

@EntityView(PositionEntity.class)
public interface PositionView {

    Long getId();

    String getPositionCode();

    String getName();

    String getDescription();

    @Mapping("department.name")
    String getDepartmentName();

    Boolean getIsActive();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

}
