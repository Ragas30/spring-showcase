package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.spring.review.bean.auth.LoginRequest;
import com.spring.review.bean.auth.LoginResponse;
import com.spring.review.bean.auth.RefreshTokenRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.entity.UserEntity;
import com.spring.review.entityView.AuthUserView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final EntityManager entityManager;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final TokenBlacklistService tokenBlacklistService;

    public LoginResponse login(
            LoginRequest request
    ) {

        CriteriaBuilder<UserEntity> cb =
                cbf.create(
                        entityManager,
                        UserEntity.class
                );

        cb.where("username")
                .eq(request.getUsername());

        AuthUserView user =
                evm.applySetting(
                                com.blazebit.persistence.view
                                        .EntityViewSetting
                                        .create(AuthUserView.class),
                                cb
                        )
                        .getSingleResultOrNull();

        if (user == null) {
            throw new BusinessException(
                    ErrorCode.USER_NOT_FOUND,
                    "User tidak ditemukan"
            );
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Akun sudah tidak aktif"
            );
        }

        boolean passwordValid =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordValid) {
            throw new BusinessException(
                    ErrorCode.INVALID_USERNAME_OR_PASSWORD,
                    "Username atau password salah"
            );
        }

        String accessToken =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole()
                );

        String refreshToken =
                jwtService.generateRefreshToken(
                        user.getUsername(),
                        user.getRole()
                );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(user.getRole())
                .build();
    }

    public LoginResponse refreshToken(
            RefreshTokenRequest request
    ) {

        String refreshToken =
                request.getRefreshToken();

        String username;

        try {
            username =
                    jwtService.extractUsername(
                            refreshToken
                    );
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.INVALID_TOKEN,
                    "Refresh token tidak valid"
            );
        }

        if (username == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_TOKEN,
                    "Refresh token tidak valid"
            );
        }

        if (!jwtService.isTokenValid(
                refreshToken,
                username
        )) {
            throw new BusinessException(
                    ErrorCode.TOKEN_EXPIRED,
                    "Refresh token sudah expired"
            );
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BusinessException(
                    ErrorCode.INVALID_TOKEN,
                    "Token bukan refresh token"
            );
        }

        CriteriaBuilder<UserEntity> cb =
                cbf.create(
                        entityManager,
                        UserEntity.class
                );

        cb.where("username")
                .eq(username);

        AuthUserView user =
                evm.applySetting(
                                com.blazebit.persistence.view
                                        .EntityViewSetting
                                        .create(AuthUserView.class),
                                cb
                        )
                        .getSingleResultOrNull();

        if (user == null) {
            throw new BusinessException(
                    ErrorCode.USER_NOT_FOUND,
                    "User tidak ditemukan"
            );
        }

        String newAccessToken =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole()
                );

        String newRefreshToken =
                jwtService.generateRefreshToken(
                        user.getUsername(),
                        user.getRole()
                );

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .role(user.getRole())
                .build();
    }

    @Transactional
    public boolean userExists(
            String username
    ) {

        Long count = cbf.create(
                        entityManager,
                        Long.class
                )
                .from(UserEntity.class)
                .select("COUNT(id)")
                .where("username")
                .eq(username)
                .getSingleResult();

        return count > 0;
    }

    public String getUserRole(
            String username
    ) {

        CriteriaBuilder<UserEntity> cb =
                cbf.create(
                        entityManager,
                        UserEntity.class
                );

        cb.where("username")
                .eq(username);

        AuthUserView user =
                evm.applySetting(
                                com.blazebit.persistence.view
                                        .EntityViewSetting
                                        .create(AuthUserView.class),
                                cb
                        )
                        .getSingleResultOrNull();

        if (user != null) {
            return user.getRole();
        }

        return null;
    }

    @Transactional
    public void logout(String token) {
        tokenBlacklistService.blacklist(token);
    }

    @Transactional
    public void changePassword(
            String username,
            String oldPassword,
            String newPassword
    ) {

        CriteriaBuilder<UserEntity> cb =
                cbf.create(
                        entityManager,
                        UserEntity.class
                );

        cb.where("username")
                .eq(username);

        AuthUserView user =
                evm.applySetting(
                                com.blazebit.persistence.view
                                        .EntityViewSetting
                                        .create(AuthUserView.class),
                                cb
                        )
                        .getSingleResultOrNull();

        if (user == null) {
            throw new BusinessException(
                    ErrorCode.USER_NOT_FOUND,
                    "User tidak ditemukan"
            );
        }

        boolean passwordValid =
                passwordEncoder.matches(
                        oldPassword,
                        user.getPassword()
                );

        if (!passwordValid) {
            throw new BusinessException(
                    ErrorCode.INVALID_USERNAME_OR_PASSWORD,
                    "Password lama salah"
            );
        }

        UserEntity userEntity =
                entityManager.find(
                        UserEntity.class,
                        user.getId()
                );

        userEntity.setPassword(
                passwordEncoder.encode(newPassword)
        );
        userEntity.setUpdatedAt(
                java.time.LocalDateTime.now()
        );
    }
}