package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.UserEntity;

@EntityView(UserEntity.class)
public interface AuthUserView {

    Long getId();

    String getUsername();

    String getPassword();

    String getRole();

    Boolean getIsActive();

}