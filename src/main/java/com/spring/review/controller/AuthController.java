package com.spring.review.controller;

import com.spring.review.bean.auth.CurrentUserResponse;
import com.spring.review.bean.auth.LoginRequest;
import com.spring.review.bean.auth.LoginResponse;
import com.spring.review.bean.auth.RefreshTokenRequest;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Login dan dapatkan JWT token")
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

    @Operation(summary = "Refresh token untuk mendapatkan access token baru")
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        LoginResponse response =
                userAuthService.refreshToken(request);

        return ApiResponse.<LoginResponse>builder()
                .code("SUCCESS")
                .message("Token refreshed successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Dapatkan informasi user yang sedang login")
    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(
            Authentication authentication
    ) {

        String role = "";

        if (authentication.getAuthorities()
                != null
                && !authentication.getAuthorities()
                .isEmpty()) {

            role = authentication.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority()
                    .replace("ROLE_", "");
        }

        CurrentUserResponse response =
                CurrentUserResponse.builder()
                        .username(
                                authentication.getName()
                        )
                        .role(role)
                        .authenticated(
                                authentication.isAuthenticated()
                        )
                        .build();

        return ApiResponse.<CurrentUserResponse>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Current User")
                .data(response)
                .build();
    }
}