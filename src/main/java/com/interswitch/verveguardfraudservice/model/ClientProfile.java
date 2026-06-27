package com.interswitch.verveguardfraudservice.model;

public record ClientProfile(
        String clientId,
        ClientTier tier
) {}
