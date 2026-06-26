package com.repohealth.common;

import com.repohealth.github.GitHubApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("缺少必要参数：" + ex.getParameterName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Message not readable: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("请求参数格式错误"));
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleGitHubApiException(GitHubApiException ex) {
        int statusCode = ex.getStatusCode();
        HttpStatus httpStatus;

        if (statusCode == 404) {
            httpStatus = HttpStatus.NOT_FOUND;
            log.warn("GitHub API 404: {}", ex.getMessage());
        } else if (statusCode == 403) {
            httpStatus = HttpStatus.TOO_MANY_REQUESTS;
            log.warn("GitHub API rate limited: {}", ex.getMessage());
        } else if (statusCode == 401) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            log.warn("GitHub API unauthorized: {}", ex.getMessage());
        } else {
            httpStatus = HttpStatus.BAD_GATEWAY;
            log.error("GitHub API error ({}): {}", statusCode, ex.getMessage());
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpClientError(HttpClientErrorException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        log.warn("HTTP client error: {} - {}", status.value(), ex.getMessage());

        String message = switch (status.value()) {
            case 404 -> "GitHub 用户不存在，请检查用户名是否正确";
            case 403 -> "GitHub API 请求过于频繁，请稍后重试";
            case 401 -> "GitHub API 认证失败";
            default -> "GitHub API 请求失败：" + ex.getStatusText();
        };

        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(message));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAccess(ResourceAccessException ex) {
        log.error("Network error: ", ex);

        Throwable cause = ex.getCause();
        if (cause instanceof SocketTimeoutException) {
            return ResponseEntity
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(ApiResponse.fail("请求 GitHub API 超时，请检查网络连接后重试"));
        }
        if (cause instanceof ConnectException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(ApiResponse.fail("无法连接到 GitHub API，请检查网络连接"));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail("GitHub API 网络请求失败：" + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: ", ex);

        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = "服务器内部错误，请稍后重试";
        } else if (message.length() > 200) {
            message = message.substring(0, 200) + "...";
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("服务器内部错误：" + message));
    }
}
