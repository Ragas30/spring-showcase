package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.DepartmentEntity;

import java.time.LocalDateTime;

@EntityView(DepartmentEntity.class)
public interface DepartmentView {

    Long getId();

    String getDepartmentCode();

    String getName();

    String getDescription();

    Boolean getIsActive();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

}
