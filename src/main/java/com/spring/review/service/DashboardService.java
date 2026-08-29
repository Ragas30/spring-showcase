package com.spring.review.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.review.bean.dashboard.DashboardStatsResponse;
import com.spring.review.bean.dashboard.HiringTrendResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.spring.review.entity.QEmployeeEntity.employeeEntity;
import static com.spring.review.entity.QDepartmentEntity.departmentEntity;
import static com.spring.review.entity.QPositionEntity.positionEntity;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final JPAQueryFactory jpaQueryFactory;

    public DashboardStatsResponse getStats() {

        Long totalEmployees = jpaQueryFactory
                .select(employeeEntity.count())
                .from(employeeEntity)
                .fetchOne();

        Long totalDepartments = jpaQueryFactory
                .select(departmentEntity.count())
                .from(departmentEntity)
                .fetchOne();

        Long totalPositions = jpaQueryFactory
                .select(positionEntity.count())
                .from(positionEntity)
                .fetchOne();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        List<Tuple> statusResults = jpaQueryFactory
                .select(employeeEntity.status, employeeEntity.count())
                .from(employeeEntity)
                .groupBy(employeeEntity.status)
                .fetch();
        for (Tuple row : statusResults) {
            String status = row.get(employeeEntity.status) != null
                    ? row.get(employeeEntity.status).name() : "UNKNOWN";
            Long count = row.get(employeeEntity.count());
            byStatus.put(status, count);
        }

        Map<String, Long> byGender = new LinkedHashMap<>();
        List<Tuple> genderResults = jpaQueryFactory
                .select(employeeEntity.gender, employeeEntity.count())
                .from(employeeEntity)
                .groupBy(employeeEntity.gender)
                .fetch();
        for (Tuple row : genderResults) {
            String gender = row.get(employeeEntity.gender) != null
                    ? row.get(employeeEntity.gender).name() : "UNKNOWN";
            Long count = row.get(employeeEntity.count());
            byGender.put(gender, count);
        }

        Map<String, Long> byDepartment = new LinkedHashMap<>();
        List<Tuple> deptResults = jpaQueryFactory
                .select(departmentEntity.name, employeeEntity.count())
                .from(employeeEntity)
                .leftJoin(employeeEntity.department, departmentEntity)
                .groupBy(departmentEntity.name)
                .fetch();
        for (Tuple row : deptResults) {
            String dept = row.get(departmentEntity.name) != null
                    ? row.get(departmentEntity.name) : "No Department";
            Long count = row.get(employeeEntity.count());
            byDepartment.put(dept, count);
        }

        return DashboardStatsResponse.builder()
                .totalEmployees(totalEmployees)
                .totalDepartments(totalDepartments)
                .totalPositions(totalPositions)
                .employeesByStatus(byStatus)
                .employeesByGender(byGender)
                .employeesByDepartment(byDepartment)
                .build();
    }

    public HiringTrendResponse getHiringTrend() {

        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(11).withDayOfMonth(1);

        StringTemplate monthExpr = Expressions.stringTemplate(
                "TO_CHAR({0}, 'YYYY-MM')",
                employeeEntity.hireDate
        );

        List<Tuple> results = jpaQueryFactory
                .select(monthExpr, employeeEntity.count())
                .from(employeeEntity)
                .where(employeeEntity.hireDate.goe(start))
                .groupBy(monthExpr)
                .orderBy(monthExpr.asc())
                .fetch();

        Map<String, Long> dataMap = new LinkedHashMap<>();
        for (Tuple row : results) {
            String month = row.get(monthExpr);
            Long count = row.get(employeeEntity.count());
            dataMap.put(month, count);
        }

        List<HiringTrendResponse.MonthlyHire> trend =
                new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            String key = ym.format(
                    DateTimeFormatter.ofPattern("yyyy-MM")
            );
            String label = ym.format(
                    DateTimeFormatter.ofPattern("MMM yyyy")
            );
            trend.add(
                    HiringTrendResponse.MonthlyHire.builder()
                            .month(label)
                            .count(dataMap.getOrDefault(key, 0L))
                            .build()
            );
        }

        return HiringTrendResponse.builder()
                .trend(trend)
                .build();
    }
}
