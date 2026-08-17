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

        fixExistingUsers();

        if (userExists("admin")) {
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
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        entityManager.persist(admin);

        System.out.println(
                "Default admin user created"
        );
    }

    private void fixExistingUsers() {

        List<UserEntity> users = entityManager
                .createQuery(
                        """
                        SELECT u FROM UserEntity u
                        WHERE u.isActive IS NULL
                        """,
                        UserEntity.class
                )
                .getResultList();

        for (UserEntity user : users) {
            user.setIsActive(true);
            user.setUpdatedAt(LocalDateTime.now());
        }

        if (!users.isEmpty()) {
            System.out.println(
                    "Fixed " + users.size()
                            + " users with missing isActive"
            );
        }
    }

    private boolean userExists(
            String username
    ) {

        TypedQuery<Long> query =
                entityManager.createQuery(
                        """
                        SELECT COUNT(u)
                        FROM UserEntity u
                        WHERE u.username = :username
                        """,
                        Long.class
                );

        query.setParameter(
                "username",
                username
        );

        return query.getSingleResult() > 0;
    }
}
