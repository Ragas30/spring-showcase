package com.spring.review.bean.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentUserResponse {

    private String username;

    private Boolean authenticated;
}
