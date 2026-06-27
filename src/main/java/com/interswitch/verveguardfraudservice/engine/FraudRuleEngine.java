package com.interswitch.verveguardfraudservice.engine;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.dto.TransactionResponse;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.rules.FraudRule;
import com.interswitch.verveguardfraudservice.rules.RuleDecision;
import com.interswitch.verveguardfraudservice.rules.RuleResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FraudRuleEngine {
    private final List<FraudRule> rules;


    public FraudRuleEngine(List<FraudRule> rules) {
        this.rules = rules;
    }

    public TransactionResponse processTransaction(TransactionRequest transactionRequest, ClientProfile clientProfile) {
        int totalScore = 0;

        List<RuleResult> results = new ArrayList<>();

        // 1. Loop through all rules and evaluate them
        for (FraudRule rule : rules) {
            RuleResult ruleResult = rule.evaluate(transactionRequest, clientProfile);
            results.add(ruleResult);
            totalScore += ruleResult.score();
        }

        // 2. Determine the decision based on the totalScore
        RuleDecision decision;
        if (totalScore >= 80) {
            decision = RuleDecision.BLOCKED;
        } else if (totalScore >= 40) {
            decision = RuleDecision.REVIEW;
        } else {
            decision = RuleDecision.APPROVED;
        }

        // 3. Generate a unique trace ID for tracking this check
        String traceId = UUID.randomUUID().toString();

        // 4. Create a readable reason summarizing the rules that triggered a score > 0
        String reason = results.stream()
                .filter(result -> result.score() > 0)
                .map(result -> result.ruleName() + " - (Score: " + result.score() + ") ")
                .collect(Collectors.collectingAndThen(
                        Collectors.joining(", "),
                        s -> s.isEmpty() ? "No rules triggered" : s
                ));

        return new TransactionResponse(traceId, String.valueOf(decision), totalScore, reason);
    }
}
