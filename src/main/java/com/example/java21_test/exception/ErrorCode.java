package com.example.java21_test.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common Exception
    // 400
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    // 401
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "User is not authorized"),
    // 403
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "No permition to access resource"),
    // 404
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    // 405
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "method not allowed"),
    // 409
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "Resource conflict occur"),
    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
