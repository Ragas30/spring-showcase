package com.spring.review.bean.webhook;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWebhookRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    @NotBlank(message = "URL wajib diisi")
    private String url;

    @NotBlank(message = "Secret key wajib diisi")
    @Size(min = 32, message = "Secret key minimal 32 karakter")
    private String secretKey;
}
