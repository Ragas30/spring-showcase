package com.spring.review.bean.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotBlank(message = "Username receiver tidak boleh kosong")
    @Size(max = 100, message = "Username receiver maksimal 100 karakter")
    private String recipientUsername;

    @NotBlank(message = "Title tidak boleh kosong")
    @Size(max = 200, message = "Title maksimal 200 karakter")
    private String title;

    @Size(max = 2000, message = "Message maksimal 2000 karakter")
    private String message;

    @Size(max = 50, message = "Type maksimal 50 karakter")
    private String type;

    @Size(max = 50, message = "Entity type maksimal 50 karakter")
    private String entityType;

    private Long entityId;

    private boolean broadcastToRole;
}
