package com.spring.review.controller;

import com.spring.review.bean.position.CreatePositionRequest;
import com.spring.review.bean.position.PositionSearchRequest;
import com.spring.review.bean.position.UpdatePositionRequest;
import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entityView.PositionView;
import com.spring.review.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public ApiResponse<PositionView> createPosition(
            @Valid @RequestBody
            CreatePositionRequest request
    ) {

        return ApiResponse.<PositionView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Position created successfully")
                .data(
                        positionService.createPosition(
                                request
                        )
                )
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<PositionView>> getPositions(
            @Valid
            @ParameterObject
            @ModelAttribute
            PositionSearchRequest request
    ) {

        return ApiResponse
                .<PageResponse<PositionView>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Positions retrieved successfully")
                .data(
                        positionService.getPositions(
                                request
                        )
                )
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PositionView> getPositionById(
            @PathVariable Long id
    ) {

        return ApiResponse.<PositionView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Position retrieved successfully")
                .data(
                        positionService.getPositionById(
                                id
                        )
                )
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PositionView> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdatePositionRequest request
    ) {

        return ApiResponse.<PositionView>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Position updated successfully")
                .data(
                        positionService.updatePosition(
                                id,
                                request
                        )
                )
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePosition(
            @PathVariable Long id
    ) {

        positionService.deletePosition(id);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Position deleted successfully")
                .build();
    }
}
