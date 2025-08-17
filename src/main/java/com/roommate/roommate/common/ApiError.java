package com.roommate.roommate.common;

import java.time.LocalDateTime;

public record ApiError(int status, String code, String message, LocalDateTime timestamp) {
    public static ApiError of(int status, String code, String message) {
        return new ApiError(status, code, message, LocalDateTime.now());
    }
}
