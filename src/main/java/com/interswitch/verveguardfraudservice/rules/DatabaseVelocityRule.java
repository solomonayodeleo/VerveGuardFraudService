package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.utils.Utils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DatabaseVelocityRule implements FraudRule {

    private final TransactionLogRepository transactionLogRepository;

    // This runs automatically if Redis crashes or times out!
    public RuleResult failOpen(TransactionRequest request, ClientProfile profile, Throwable throwable) {
        System.out.println("⚠️ DB IS DOWN! Failing Open gracefully. Error: " + throwable.getMessage());

        // Return a score of 0 so the transaction is NOT blocked by a broken cache!
        return new RuleResult("Database Velocity Rule", 0, RuleCategory.VELOCITY);
    }

    @CircuitBreaker(name = "database", fallbackMethod = "failOpen")
    @Override
    public RuleResult evaluate(TransactionRequest transactionRequest, ClientProfile clientProfile) {
        String hashedCardNum = Utils.hashCardNumber(transactionRequest.cardNumber());
        Instant velocityTimeStamp = Instant.now().minusSeconds(300);
        long transactionCount = transactionLogRepository.countByCardNumberHashAndCreatedAtAfter(hashedCardNum, velocityTimeStamp);
        int score = 0;
        if (transactionCount > 5) {
            score = 100;
        }
        return new RuleResult(
                "Database Velocity Rule",
                score,
                RuleCategory.VELOCITY
        );
    }

    @Override
    public RuleCategory getRuleCategory() {
        return RuleCategory.VELOCITY;
    }
}
