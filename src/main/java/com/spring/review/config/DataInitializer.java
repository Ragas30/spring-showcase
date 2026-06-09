package com.spring.review.config;

import com.spring.review.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        TypedQuery<UserEntity> query =
                entityManager.createQuery(
                        """
                        SELECT u
                        FROM UserEntity u
                        WHERE u.username = :username
                        """,
                        UserEntity.class
                );

        query.setParameter(
                "username",
                "admin"
        );

        List<UserEntity> users =
                query.getResultList();

        if (!users.isEmpty()) {
            return;
        }

        UserEntity admin =
                UserEntity.builder()
                        .username("admin")
                        .password(
                                passwordEncoder.encode(
                                        "admin123"
                                )
                        )
                        .role("ADMIN")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        entityManager.persist(admin);

        System.out.println(
                "Default admin user created"
        );
    }
}