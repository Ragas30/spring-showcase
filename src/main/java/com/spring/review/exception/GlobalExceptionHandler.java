package com.spring.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Business Exception
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex
    ) {

        HttpStatus status = switch (ex.getCode()) {

            case BAD_REQUEST ->
                    HttpStatus.BAD_REQUEST;

            case NOT_FOUND ->
                    HttpStatus.NOT_FOUND;

            case UNAUTHORIZED ->
                    HttpStatus.UNAUTHORIZED;

            default ->
                    HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ErrorResponse response =
                ErrorResponse.builder()
                        .code(ex.getCode().name())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
    // Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String message = ex
                .getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Unknown Exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(
            Exception ex
    ) {

        return ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

