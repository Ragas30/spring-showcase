package com.spring.review.bean.webhook;

import lombok.Data;

@Data
public class WebhookSearchRequest {

    private String name;

    private Boolean isActive;

    private int page = 0;

    private int size = 10;
}
