package com.example.java21_test.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;


@Builder
public record ErrorResponse(String code, String message,
                            @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ValidationError> errors) {

    @Builder
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
