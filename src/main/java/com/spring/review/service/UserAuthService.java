package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.spring.review.bean.auth.LoginRequest;
import com.spring.review.bean.auth.LoginResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.entity.UserEntity;
import com.spring.review.entityView.AuthUserView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
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

        String token =
                jwtService.generateToken(
                        user.getUsername()
                );

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}