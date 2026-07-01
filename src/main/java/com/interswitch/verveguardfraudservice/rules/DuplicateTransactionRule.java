package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.utils.Utils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DuplicateTransactionRule implements FraudRule {

    private final StringRedisTemplate redisTemplate;

    public DuplicateTransactionRule(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public RuleResult evaluate(TransactionRequest transactionRequest, ClientProfile clientProfile) {
        String redisTransactionKey = "dup:" + Utils.hashCardNumber(transactionRequest.cardNumber()) + ":" + transactionRequest.amount();
        if  (redisTemplate.hasKey(redisTransactionKey)) {
            return new RuleResult(
                    "Duplicate Transaction Rule",
                    100,
                    RuleCategory.VELOCITY
            );
        }

        redisTemplate.opsForValue().set(redisTransactionKey, "1", 30, TimeUnit.SECONDS);
        return new RuleResult(
                "Duplicate Transaction Rule",
                0,
                RuleCategory.VELOCITY
        );
    }

    @Override
    public RuleCategory getRuleCategory() {
        return RuleCategory.VELOCITY;
    }
}
