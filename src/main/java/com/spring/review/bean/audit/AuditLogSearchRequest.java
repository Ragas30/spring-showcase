package com.spring.review.bean.audit;

import lombok.Data;

@Data
public class AuditLogSearchRequest {

    private String entityType;

    private String action;

    private int page = 0;

    private int size = 10;

}
