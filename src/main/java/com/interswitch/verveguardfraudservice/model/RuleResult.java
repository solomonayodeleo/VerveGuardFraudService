package com.interswitch.verveguardfraudservice.model;

public record RuleResult(
        String ruleName,
        int score,
        RuleCategory category
) {
}
