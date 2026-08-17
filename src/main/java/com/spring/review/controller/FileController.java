package com.spring.review.controller;

import com.spring.review.common.ApiResponse;
import com.spring.review.common.ErrorCode;
import com.spring.review.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> uploadFile(
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
