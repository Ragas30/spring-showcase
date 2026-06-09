package com.spring.review.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code;

    private String message;

    private LocalDateTime timestamp;
}
