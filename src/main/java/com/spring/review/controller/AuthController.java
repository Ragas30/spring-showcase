package com.spring.review.controller;

import com.spring.review.bean.auth.CurrentUserResponse;
import com.spring.review.bean.auth.LoginRequest;
import com.spring.review.bean.auth.LoginResponse;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.service.UserAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                userAuthService.login(request);

        return ApiResponse.<LoginResponse>builder()
                .code("SUCCESS")
                .message("Login berhasil")
                .data(response)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(Authentication authentication) {
        CurrentUserResponse response =
                CurrentUserResponse.builder()
                        .username(authentication.getName())
                        .authenticated(authentication.isAuthenticated())
                        .build();

        return ApiResponse.<CurrentUserResponse>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Current User")
                .data(response).build();
    }
}