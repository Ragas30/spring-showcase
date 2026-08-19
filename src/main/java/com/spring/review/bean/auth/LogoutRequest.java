package com.spring.review.bean.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "Token wajib diisi")
    private String token;

}
