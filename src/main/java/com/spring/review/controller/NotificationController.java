package com.spring.review.controller;

import com.spring.review.bean.notification.CreateNotificationRequest;
import com.spring.review.bean.notification.NotificationSearchRequest;
import com.spring.review.bean.notification.UnreadCountResponse;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entityView.NotificationView;
import com.spring.review.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Dapatkan daftar notifikasi user yang sedang login")
    @GetMapping
    public ApiResponse<PageResponse<NotificationView>> getNotifications(
            NotificationSearchRequest request,
            Authentication authentication
    ) {

        PageResponse<NotificationView> response =
                notificationService.getNotifications(
                        authentication.getName(),
                        request.getUnreadOnly(),
                        request.getPage(),
                        request.getSize()
                );

        return ApiResponse
                .<PageResponse<NotificationView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Notifications retrieved successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Dapatkan jumlah notifikasi yang belum dibaca")
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount(
            Authentication authentication
    ) {

        long count = notificationService.getUnreadCount(
                authentication.getName()
        );

        return ApiResponse.<UnreadCountResponse>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Unread count retrieved successfully")
                .data(
                        UnreadCountResponse.builder()
                                .unreadCount(count)
                                .build()
                )
                .build();
    }

    @Operation(summary = "Tandai satu notifikasi sebagai sudah dibaca")
    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationView> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {

        NotificationView response = notificationService
                .markAsRead(authentication.getName(), id);

        return ApiResponse.<NotificationView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Notification marked as read")
                .data(response)
                .build();
    }

    @Operation(summary = "Tandai semua notifikasi user sebagai sudah dibaca")
    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(
            Authentication authentication
    ) {

        notificationService.markAllAsRead(
                authentication.getName()
        );

        return ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("All notifications marked as read")
                .build();
    }

    @Operation(summary = "Kirim notifikasi manual ke user (khusus admin/hr)")
    @PostMapping
    public ApiResponse<NotificationView> sendNotification(
            @Valid @RequestBody CreateNotificationRequest request
    ) {

        String type = request.getType() == null
                ? "MANUAL"
                : request.getType();

        NotificationView response = notificationService
                .sendToUser(
                        request.getRecipientUsername(),
                        request.getTitle(),
                        request.getMessage(),
                        type,
                        request.getEntityType(),
                        request.getEntityId(),
                        request.isBroadcastToRole()
                );

        return ApiResponse.<NotificationView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Notification sent successfully")
                .data(response)
                .build();
    }
}
