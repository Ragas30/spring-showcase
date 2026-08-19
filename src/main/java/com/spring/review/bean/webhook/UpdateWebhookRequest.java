package com.spring.review.bean.webhook;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateWebhookRequest {

    private String name;

    private String url;

    @Size(min = 32, message = "Secret key minimal 32 karakter")
    private String secretKey;

    private Boolean isActive;
}
