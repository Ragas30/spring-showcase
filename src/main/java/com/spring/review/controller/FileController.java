package com.spring.review.controller;

import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "File Management")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Upload foto employee")
    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> uploadFile(
            @Parameter(
                    description = "File gambar (JPEG, PNG, WebP)",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            format = "binary"
                    )
            )
            @RequestParam("file") MultipartFile file
    ) {

        String filename =
                fileStorageService.storeFile(file);

        return ApiResponse.<Map<String, String>>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("File uploaded successfully")
                .data(Map.of(
                        "filename", filename,
                        "url", "/uploads/" + filename
                ))
                .build();
    }

    @Operation(summary = "Hapus file berdasarkan filename")
    @DeleteMapping("/{filename}")
    public ApiResponse<Void> deleteFile(
            @PathVariable String filename
    ) {

        fileStorageService.deleteFile(filename);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.name())
                .message("File deleted successfully")
                .build();
    }
}
