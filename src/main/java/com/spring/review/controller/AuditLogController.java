package com.spring.review.controller;

import com.spring.review.bean.audit.AuditLogSearchRequest;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entityView.AuditLogView;
import com.spring.review.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Audit Log")
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "Dapatkan audit logs dengan filter")
    @GetMapping
    public ApiResponse<PageResponse<AuditLogView>> getAuditLogs(
            AuditLogSearchRequest request
    ) {

        PageResponse<AuditLogView> response =
                auditLogService.getAuditLogs(
                        request.getEntityType(),
                        request.getAction(),
                        request.getPage(),
                        request.getSize()
                );

        return ApiResponse
                .<PageResponse<AuditLogView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Audit logs retrieved successfully")
                .data(response)
                .build();
    }
}
