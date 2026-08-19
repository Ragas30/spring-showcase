package com.spring.review.bean.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {

    private Long totalEmployees;

    private Long totalDepartments;

    private Long totalPositions;

    private Map<String, Long> employeesByStatus;

    private Map<String, Long> employeesByGender;

    private Map<String, Long> employeesByDepartment;

}
