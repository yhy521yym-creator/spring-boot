package com.example.servicearea.common;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ApiResponse.fail(400, msg.isBlank() ? "参数错误" : msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<String> handleJsonParse(HttpMessageNotReadableException e) {
        return ApiResponse.fail(400, "请求体 JSON 格式不正确");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneric(Exception e) {
        return ApiResponse.fail(500, e.getMessage() == null ? "服务器内部错误" : e.getMessage());
    }
}

