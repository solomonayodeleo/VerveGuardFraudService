package com.interswitch.verveguardfraudservice.dto;

import java.util.List;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> subErrors
) {}
