package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DatabaseVelocityRule implements FraudRule {

    private final TransactionLogRepository transactionLogRepository;

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
