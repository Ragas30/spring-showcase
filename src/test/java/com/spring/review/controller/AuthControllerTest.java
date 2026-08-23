package com.spring.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.review.bean.auth.ChangePasswordRequest;
import com.spring.review.bean.auth.LoginRequest;
import com.spring.review.bean.auth.LoginResponse;
import com.spring.review.bean.auth.LogoutRequest;
import com.spring.review.bean.auth.RefreshTokenRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.exception.BusinessException;
import com.spring.review.exception.GlobalExceptionHandler;
import com.spring.review.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserAuthService userAuthService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_validCredentials_returnsTokens() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        LoginResponse response = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .role("ADMIN")
                .build();

        when(userAuthService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("wrong");
        request.setPassword("wrong");

        when(userAuthService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException(
                        ErrorCode.INVALID_USERNAME_OR_PASSWORD,
                        "Username atau password salah"
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_USERNAME_OR_PASSWORD"));
    }

    @Test
    void refreshToken_validToken_returnsNewTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        LoginResponse response = LoginResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .role("ADMIN")
                .build();

        when(userAuthService.refreshToken(any(RefreshTokenRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
    }

    @Test
    void refreshToken_invalidToken_returns401() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");

        when(userAuthService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new BusinessException(
                        ErrorCode.INVALID_TOKEN,
                        "Refresh token tidak valid"
                ));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
    }

    @Test
    void me_authenticatedUser_returnsUserInfo() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        mockMvc.perform(get("/api/auth/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void logout_validToken_returnsSuccess() throws Exception {
        LogoutRequest request = new LogoutRequest();
        request.setToken("some-token");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Logout berhasil"));

        verify(userAuthService).logout("some-token");
    }

    @Test
    void changePassword_validRequest_returnsSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old123");
        request.setNewPassword("new123");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        mockMvc.perform(post("/api/auth/change-password")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Password berhasil diubah"));

        verify(userAuthService).changePassword("admin", "old123", "new123");
    }
}
