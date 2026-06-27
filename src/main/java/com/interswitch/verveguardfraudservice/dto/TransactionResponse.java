package com.interswitch.verveguardfraudservice.dto;

public record TransactionResponse(
        String traceId,
        String decision,
        int score,
        String reason
) {}