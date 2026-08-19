package com.spring.review.controller;

import com.spring.review.bean.dashboard.DashboardStatsResponse;
import com.spring.review.bean.dashboard.HiringTrendResponse;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Dapatkan statistik dashboard")
    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> getStats() {

        DashboardStatsResponse response =
                dashboardService.getStats();

        return ApiResponse.<DashboardStatsResponse>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Dashboard stats retrieved successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Dapatkan trend hiring 12 bulan terakhir")
    @GetMapping("/hiring-trend")
    public ApiResponse<HiringTrendResponse> getHiringTrend() {

        HiringTrendResponse response =
                dashboardService.getHiringTrend();

        return ApiResponse.<HiringTrendResponse>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Hiring trend retrieved successfully")
                .data(response)
                .build();
    }
}
