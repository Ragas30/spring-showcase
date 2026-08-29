package com.spring.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notification_recipient_username", columnList = "recipientUsername"),
        @Index(name = "idx_notification_recipient_role", columnList = "recipientRole"),
        @Index(name = "idx_notification_created_at", columnList = "createdAt")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String recipientUsername;

    @Column(length = 50)
    private String recipientRole;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(length = 50)
    private String entityType;

    @Column
    private Long entityId;

    @Column(nullable = false, columnDefinition = "boolean not null default false")
    private Boolean isRead;

    @Column
    private LocalDateTime readAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
