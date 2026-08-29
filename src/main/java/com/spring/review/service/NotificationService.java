package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.NotificationEntity;
import com.spring.review.entityView.NotificationView;
import com.spring.review.exception.BusinessException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.review.entity.QNotificationEntity.notificationEntity;
import static com.spring.review.entity.QUserEntity.userEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final JPAQueryFactory jpaQueryFactory;

    private final SimpMessagingTemplate messagingTemplate;

    private final UserAuthService userAuthService;

    public NotificationView sendToUser(
            String recipientUsername,
            String title,
            String message,
            String type,
            String entityType,
            Long entityId,
            boolean broadcastToRole
    ) {
        String role = userAuthService.getUserRole(recipientUsername);

        return notify(
                recipientUsername,
                role,
                title,
                message,
                type,
                entityType,
                entityId,
                broadcastToRole
        );
    }

    public void broadcastToRole(
            String role,
            String title,
            String message,
            String type,
            String entityType,
            Long entityId
    ) {
        List<String> usernames = jpaQueryFactory
                .select(userEntity.username)
                .from(userEntity)
                .where(userEntity.role.eq(role))
                .fetch();

        NotificationView last = null;

        for (String username : usernames) {
            last = notify(
                    username,
                    role,
                    title,
                    message,
                    type,
                    entityType,
                    entityId,
                    false
            );
        }

        if (last != null && role != null) {
            messagingTemplate.convertAndSend(
                    "/topic/notifications." + role,
                    last
            );
        }
    }

    public NotificationView notify(
            String recipientUsername,
            String recipientRole,
            String title,
            String message,
            String type,
            String entityType,
            Long entityId,
            boolean broadcastToRole
    ) {
        NotificationEntity entity = NotificationEntity.builder()
                .recipientUsername(recipientUsername)
                .recipientRole(recipientRole)
                .title(title)
                .message(message)
                .type(type)
                .entityType(entityType)
                .entityId(entityId)
                .isRead(false)
                .readAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        em.persist(entity);
        em.flush();

        NotificationView view = toView(entity.getId());

        push(view, broadcastToRole);

        return view;
    }

    public PageResponse<NotificationView> getNotifications(
            String username,
            Boolean unreadOnly,
            int page,
            int size
    ) {
        if (size <= 0) size = 10;
        if (page < 0) page = 0;

        BooleanExpression predicate = notificationEntity
                .recipientUsername.eq(username);

        if (Boolean.TRUE.equals(unreadOnly)) {
            predicate = predicate.and(
                    notificationEntity.isRead.isFalse()
            );
        }

        Long totalElements = jpaQueryFactory
                .select(notificationEntity.count())
                .from(notificationEntity)
                .where(predicate)
                .fetchOne();

        List<Long> ids = jpaQueryFactory
                .select(notificationEntity.id)
                .from(notificationEntity)
                .where(predicate)
                .orderBy(notificationEntity.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        List<NotificationView> content = ids.stream()
                .map(this::toView)
                .toList();

        int totalPages = (int) Math.ceil(
                (double) totalElements / size
        );

        return PageResponse.<NotificationView>builder()
                .content(content)
                .totalElements(totalElements)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    public long getUnreadCount(String username) {
        Long count = jpaQueryFactory
                .select(notificationEntity.count())
                .from(notificationEntity)
                .where(
                        notificationEntity.recipientUsername
                                .eq(username)
                                .and(notificationEntity.isRead.isFalse())
                )
                .fetchOne();

        return count == null ? 0 : count;
    }

    public NotificationView markAsRead(
            String username,
            Long id
    ) {
        NotificationEntity entity =
                em.find(NotificationEntity.class, id);

        if (entity == null) {
            throw new BusinessException(
                    ErrorCode.NOT_FOUND,
                    "Notification not found"
            );
        }

        if (!entity.getRecipientUsername().equals(username)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Cannot access another user's notification"
            );
        }

        if (Boolean.FALSE.equals(entity.getIsRead())) {
            entity.setIsRead(true);
            entity.setReadAt(LocalDateTime.now());
            em.flush();
        }

        return toView(entity.getId());
    }

    public void markAllAsRead(String username) {
        jpaQueryFactory
                .update(notificationEntity)
                .set(notificationEntity.isRead, true)
                .set(notificationEntity.readAt, LocalDateTime.now())
                .where(
                        notificationEntity.recipientUsername
                                .eq(username)
                                .and(notificationEntity.isRead.isFalse())
                )
                .execute();
    }

    private NotificationView toView(Long id) {
        return evm.applySetting(
                        EntityViewSetting.create(NotificationView.class),
                        cbf.create(em, NotificationEntity.class)
                                .where("id").eq(id)
                )
                .getSingleResult();
    }

    private void push(
            NotificationView view,
            boolean broadcastToRole
    ) {
        messagingTemplate.convertAndSendToUser(
                view.getRecipientUsername(),
                "/queue/notifications",
                view
        );

        if (broadcastToRole && view.getRecipientRole() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/notifications." + view.getRecipientRole(),
                    view
            );
        }
    }
}
