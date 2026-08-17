package com.spring.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex
    ) {

        HttpStatus status = switch (ex.getCode()) {

            case BAD_REQUEST ->
                    HttpStatus.BAD_REQUEST;

            case NOT_FOUND,
                 EMPLOYEE_NOT_FOUND,
                 USER_NOT_FOUND,
                 DEPARTMENT_NOT_FOUND,
                 POSITION_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;

            case UNAUTHORIZED,
                 INVALID_TOKEN,
                 TOKEN_EXPIRED ->
                    HttpStatus.UNAUTHORIZED;

            case FORBIDDEN,
                 ACCESS_DENIED ->
                    HttpStatus.FORBIDDEN;

            case CONFLICT,
                 EMAIL_ALREADY_EXISTS ->
                    HttpStatus.CONFLICT;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        List<String> errors = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                        fieldError.getField() + ": "
                                + fieldError.getDefaultMessage()
                )
                .collect(Collectors.toList());

        String message = String.join("; ", errors);

        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

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

