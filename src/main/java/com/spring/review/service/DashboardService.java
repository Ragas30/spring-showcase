package com.spring.review.service;

import com.spring.review.bean.dashboard.DashboardStatsResponse;
import com.spring.review.bean.dashboard.HiringTrendResponse;
import jakarta.persistence.EntityManager;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final EntityManager em;

    public DashboardStatsResponse getStats() {

        Long totalEmployees = em.createQuery(
                "SELECT COUNT(e) FROM EmployeeEntity e",
                Long.class
        ).getSingleResult();

        Long totalDepartments = em.createQuery(
                "SELECT COUNT(d) FROM DepartmentEntity d",
                Long.class
        ).getSingleResult();

        Long totalPositions = em.createQuery(
                "SELECT COUNT(p) FROM PositionEntity p",
                Long.class
        ).getSingleResult();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        List<Object[]> statusResults = em.createQuery(
                "SELECT e.status, COUNT(e) FROM EmployeeEntity e GROUP BY e.status",
                Object[].class
        ).getResultList();
        for (Object[] row : statusResults) {
            String status = row[0] != null
                    ? row[0].toString() : "UNKNOWN";
            Long count = (Long) row[1];
            byStatus.put(status, count);
        }

        Map<String, Long> byGender = new LinkedHashMap<>();
        List<Object[]> genderResults = em.createQuery(
                "SELECT e.gender, COUNT(e) FROM EmployeeEntity e GROUP BY e.gender",
                Object[].class
        ).getResultList();
        for (Object[] row : genderResults) {
            String gender = row[0] != null
                    ? row[0].toString() : "UNKNOWN";
            Long count = (Long) row[1];
            byGender.put(gender, count);
        }

        Map<String, Long> byDepartment = new LinkedHashMap<>();
        List<Object[]> deptResults = em.createQuery(
                "SELECT d.name, COUNT(e) FROM EmployeeEntity e " +
                        "LEFT JOIN e.department d " +
                        "GROUP BY d.name",
                Object[].class
        ).getResultList();
        for (Object[] row : deptResults) {
            String dept = row[0] != null
                    ? row[0].toString() : "No Department";
            Long count = (Long) row[1];
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

        List<Object[]> results = em.createQuery(
                "SELECT FUNCTION('TO_CHAR', e.hireDate, 'YYYY-MM'), COUNT(e) " +
                        "FROM EmployeeEntity e " +
                        "WHERE e.hireDate >= :start " +
                        "GROUP BY FUNCTION('TO_CHAR', e.hireDate, 'YYYY-MM') " +
                        "ORDER BY FUNCTION('TO_CHAR', e.hireDate, 'YYYY-MM')",
                Object[].class
        ).setParameter("start", start)
         .getResultList();

        Map<String, Long> dataMap = new LinkedHashMap<>();
        for (Object[] row : results) {
            String month = (String) row[0];
            Long count = (Long) row[1];
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
