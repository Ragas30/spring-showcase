package com.spring.review.controller;

import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.service.ExportImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "Export/Import")
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportImportService exportImportService;

    @Operation(summary = "Export employees ke Excel")
    @GetMapping("/excel")
    public void exportExcel(
            HttpServletResponse response
    ) throws IOException {

        exportImportService.exportEmployeesToExcel(response);
    }

    @Operation(summary = "Export employees ke PDF")
    @GetMapping("/pdf")
    public void exportPdf(
            HttpServletResponse response
    ) throws IOException {

        exportImportService.exportEmployeesToPdf(response);
    }

    @Operation(summary = "Import employees dari Excel")
    @PostMapping("/import")
    public ApiResponse<List<String>> importExcel(
            @Parameter(
                    description = "File Excel (.xlsx) yang berisi data employees",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            format = "binary"
                    )
            )
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        List<String> results =
                exportImportService.importEmployeesFromExcel(
                        file.getInputStream()
                );

        return ApiResponse.<List<String>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("Import completed")
                .data(results)
                .build();
    }
}
