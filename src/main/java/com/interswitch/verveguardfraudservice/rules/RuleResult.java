package com.interswitch.verveguardfraudservice.rules;

public record RuleResult(
        String ruleName,
        int score,
        RuleCategory category
) {
}
