package com.spring.review.bean.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token wajib diisi")
    private String refreshToken;
}
