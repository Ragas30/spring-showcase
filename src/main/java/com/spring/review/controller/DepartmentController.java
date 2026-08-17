package com.spring.review.controller;

import com.spring.review.bean.department.CreateDepartmentRequest;
import com.spring.review.bean.department.DepartmentSearchRequest;
import com.spring.review.bean.department.UpdateDepartmentRequest;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entityView.DepartmentView;
import com.spring.review.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ApiResponse<DepartmentView> createDepartment(
            @Valid @RequestBody
            CreateDepartmentRequest request
    ) {

        return ApiResponse.<DepartmentView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Department created successfully")
                .data(
                        departmentService.createDepartment(
                                request
                        )
                )
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<DepartmentView>> getDepartments(
            @Valid
            @ParameterObject
            @ModelAttribute
            DepartmentSearchRequest request
    ) {

        return ApiResponse
                .<PageResponse<DepartmentView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Departments retrieved successfully")
                .data(
                        departmentService.getDepartments(
                                request
                        )
                )
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DepartmentView> getDepartmentById(
            @PathVariable Long id
    ) {

        return ApiResponse.<DepartmentView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Department retrieved successfully")
                .data(
                        departmentService.getDepartmentById(
                                id
                        )
                )
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentView> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateDepartmentRequest request
    ) {

        return ApiResponse.<DepartmentView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Department updated successfully")
                .data(
                        departmentService.updateDepartment(
                                id,
                                request
                        )
                )
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDepartment(
            @PathVariable Long id
    ) {

        departmentService.deleteDepartment(id);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Department deleted successfully")
                .build();
    }
}
