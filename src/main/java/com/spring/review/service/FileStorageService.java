package com.spring.review.service;

import com.spring.review.config.FileStorageConfig;
import com.spring.review.common.ErrorCode;
import com.spring.review.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageConfig config;

    public String storeFile(MultipartFile file) {

        validateFile(file);

        String filename = generateFilename(
                file.getOriginalFilename()
        );

        Path uploadPath = Paths
                .get(config.getUploadDir())
                .toAbsolutePath()
                .normalize();

        try {

            Files.createDirectories(uploadPath);

            Path targetPath = uploadPath.resolve(filename);

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return filename;

        } catch (IOException e) {

            log.error(
                    "Failed to store file: {}",
                    filename,
                    e
            );

            throw new BusinessException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Failed to store file: "
                            + e.getMessage()
            );
        }
    }

    public void deleteFile(String filename) {

        if (filename == null || filename.isBlank()) {
            return;
        }

        Path uploadPath = Paths
                .get(config.getUploadDir())
                .toAbsolutePath()
                .normalize();

        Path filePath = uploadPath.resolve(filename).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "Invalid filename"
            );
        }

        try {

            Files.deleteIfExists(filePath);

        } catch (IOException e) {

            log.warn(
                    "Failed to delete file: {}",
                    filename,
                    e
            );
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "File is empty"
            );
        }

        if (file.getSize() > config.getMaxSize()) {

            long maxMb = config.getMaxSize()
                    / (1024 * 1024);

            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "File size exceeds maximum limit of "
                            + maxMb + " MB"
            );
        }

        String contentType =
                file.getContentType();

        Set<String> allowed = config
                .getAllowedTypes()
                .stream()
                .collect(Collectors.toSet());

        if (contentType == null
                || !allowed.contains(contentType)) {

            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "File type not allowed. Allowed: "
                            + String.join(", ", allowed)
            );
        }
    }

    private String generateFilename(String original) {

        String ext = "";

        if (original != null
                && original.contains(".")) {

            ext = original.substring(
                    original.lastIndexOf(".")
            );
        }

        return UUID.randomUUID() + ext;
    }
}
