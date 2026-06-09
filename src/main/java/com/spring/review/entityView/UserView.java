package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.spring.review.entity.UserEntity;

@EntityView(UserEntity.class)
public interface UserView {

    Long getId();

    String getUsername();

    String getRole();

}
