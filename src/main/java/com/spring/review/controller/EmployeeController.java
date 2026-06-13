package com.spring.review.controller;

import com.spring.review.bean.employee.CreateEmployeeRequest;
import com.spring.review.bean.employee.UpdateEmployeeRequest;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.entityView.EmployeeView;
import com.spring.review.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Create Employee
     */
    @PostMapping
    public ApiResponse<EmployeeView> createEmployee(
            @Valid @RequestBody
            CreateEmployeeRequest request
    ) {

        return ApiResponse.<EmployeeView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Employee created successfully")
                .data(
                        employeeService.createEmployee(
                                request
                        )
                )
                .build();
    }

    /**
     * Get All Employees
     */
    @GetMapping
    public ApiResponse<List<EmployeeView>> getEmployees() {

        return ApiResponse
                .<List<EmployeeView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Employees retrieved successfully")
                .data(
                        employeeService.getEmployees()
                )
                .build();
    }

    /**
     * Get Employee By Id
     */
    @GetMapping("/{id}")
    public ApiResponse<EmployeeView> getEmployeeById(
            @PathVariable Long id
    ) {

        return ApiResponse.<EmployeeView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Employee retrieved successfully")
                .data(
                        employeeService.getEmployeeById(
                                id
                        )
                )
                .build();
    }

    /**
     * Update Employee
     */
    @PutMapping("/{id}")
    public ApiResponse<EmployeeView> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateEmployeeRequest request
    ) {

        return ApiResponse.<EmployeeView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Employee updated successfully")
                .data(
                        employeeService.updateEmployee(
                                id,
                                request
                        )
                )
                .build();
    }

    /**
     * Delete Employee
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(
            @PathVariable Long id
    ) {

        employeeService.deleteEmployee(id);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Employee deleted successfully")
                .build();
    }
}