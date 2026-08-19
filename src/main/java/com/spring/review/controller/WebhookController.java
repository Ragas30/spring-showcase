package com.spring.review.controller;

import com.spring.review.bean.webhook.CreateWebhookRequest;
import com.spring.review.bean.webhook.UpdateWebhookRequest;
import com.spring.review.bean.webhook.WebhookSearchRequest;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entityView.WebhookLogView;
import com.spring.review.entityView.WebhookSubscriptionView;
import com.spring.review.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Webhook")
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @Operation(summary = "Buat webhook subscription baru")
    @PostMapping
    public ApiResponse<WebhookSubscriptionView> create(
            @Valid @RequestBody CreateWebhookRequest request
    ) {
        WebhookSubscriptionView view =
                webhookService.create(request);

        return ApiResponse.<WebhookSubscriptionView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Webhook subscription berhasil dibuat")
                .data(view)
                .build();
    }

    @Operation(summary = "Dapatkan webhook subscription by ID")
    @GetMapping("/{id}")
    public ApiResponse<WebhookSubscriptionView> getById(
            @PathVariable Long id
    ) {
        WebhookSubscriptionView view =
                webhookService.getById(id);

        return ApiResponse.<WebhookSubscriptionView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Webhook subscription berhasil ditemukan")
                .data(view)
                .build();
    }

    @Operation(summary = "Dapatkan semua webhook subscriptions")
    @GetMapping
    public ApiResponse<PageResponse<WebhookSubscriptionView>> getWebhooks(
            WebhookSearchRequest request
    ) {
        PageResponse<WebhookSubscriptionView> response =
                webhookService.getWebhooks(
                        request.getName(),
                        request.getIsActive(),
                        request.getPage(),
                        request.getSize()
                );

        return ApiResponse
                .<PageResponse<WebhookSubscriptionView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Webhook subscriptions berhasil didapatkan")
                .data(response)
                .build();
    }

    @Operation(summary = "Update webhook subscription")
    @PutMapping("/{id}")
    public ApiResponse<WebhookSubscriptionView> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWebhookRequest request
    ) {
        WebhookSubscriptionView view =
                webhookService.update(id, request);

        return ApiResponse.<WebhookSubscriptionView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Webhook subscription berhasil diupdate")
                .data(view)
                .build();
    }

    @Operation(summary = "Hapus webhook subscription")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        webhookService.delete(id);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Webhook subscription berhasil dihapus")
                .build();
    }

    @Operation(summary = "Dapatkan webhook logs")
    @GetMapping("/logs")
    public ApiResponse<PageResponse<WebhookLogView>> getWebhookLogs(
            @RequestParam(required = false) Long subscriptionId,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<WebhookLogView> response =
                webhookService.getWebhookLogs(
                        subscriptionId,
                        eventName,
                        status,
                        page,
                        size
                );

        return ApiResponse.<PageResponse<WebhookLogView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Webhook logs berhasil didapatkan")
                .data(response)
                .build();
    }
}
