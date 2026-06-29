package com.interswitch.verveguardfraudservice.engine;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.dto.TransactionResponse;
import com.interswitch.verveguardfraudservice.entity.TransactionLog;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.rules.FraudRule;
import com.interswitch.verveguardfraudservice.model.RuleDecision;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.utils.Utils;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FraudRuleEngine {
    private final List<FraudRule> rules;
    private final TransactionLogRepository transactionLogRepository;


    public FraudRuleEngine(List<FraudRule> rules, TransactionLogRepository transactionLogRepository) {
        this.rules = rules;
        this.transactionLogRepository = transactionLogRepository;
    }

    public TransactionResponse processTransaction(TransactionRequest transactionRequest, ClientProfile clientProfile) {
        int totalScore = 0;

        List<RuleResult> results = new ArrayList<>();

        long start = System.currentTimeMillis();

        // 1. Loop through all rules and evaluate them (based on client's choice)
        for (FraudRule rule : rules) {
            // Check for Client's Rule Choices and then run the fraud rule if selected
            if (clientProfile.activeCategories().contains(rule.getRuleCategory())) {
                RuleResult ruleResult = rule.evaluate(transactionRequest, clientProfile);
                results.add(ruleResult);
                totalScore += ruleResult.score();
            }

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

        // 5. Create a Transaction Log
        TransactionLog transactionLog = TransactionLog.builder()
                .traceId(traceId)
                .clientId(transactionRequest.clientId())
                .clientTier(String.valueOf(clientProfile.tier()))
                .decision(String.valueOf(decision))
                .aggregateScore(totalScore)
                .ipAddress(transactionRequest.ipAddress())
                .cardNumberHash(Utils.hashCardNumber(transactionRequest.cardNumber()))
                .createdAt(Instant.now())
                .executionTimeMs(System.currentTimeMillis() - start)
                .build();

        // 6. Save the Transaction Log to the DB
        transactionLogRepository.save(transactionLog);


        return new TransactionResponse(traceId, String.valueOf(decision), totalScore, reason);
    }
}
