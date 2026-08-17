package com.spring.review.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {

    private String uploadDir = "uploads";

    private long maxSize = 5 * 1024 * 1024;

    private List<String> allowedTypes = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

}
