package com.interswitch.verveguardfraudservice.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        String cardNumber,
        BigDecimal amount,
        String merchantId,
        String ipAddress,
        String clientId
) {}