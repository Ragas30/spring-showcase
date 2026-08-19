package com.spring.review.bean.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HiringTrendResponse {

    private List<MonthlyHire> trend;

    @Data
    @Builder
    public static class MonthlyHire {

        private String month;

        private Long count;

    }

}
