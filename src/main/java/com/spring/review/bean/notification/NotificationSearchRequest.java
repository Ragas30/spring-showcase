package com.spring.review.bean.notification;

import lombok.Data;

@Data
public class NotificationSearchRequest {

    private Boolean unreadOnly;

    private int page = 0;

    private int size = 10;
}
