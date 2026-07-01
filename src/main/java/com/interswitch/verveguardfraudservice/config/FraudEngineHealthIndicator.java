package com.interswitch.verveguardfraudservice.config;


import com.interswitch.verveguardfraudservice.rules.FraudRule;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class FraudEngineHealthIndicator implements HealthIndicator {

    private final List<FraudRule> activeRules;

    public FraudEngineHealthIndicator(List<FraudRule> activeRules) {
        this.activeRules = activeRules;
    }

    @Override
    public @Nullable Health health() {
        return Health.up()
                .withDetail("Fraud Engine", "Online")
                .withDetail("Active Rules", activeRules.size())
                .build();
    }
}
