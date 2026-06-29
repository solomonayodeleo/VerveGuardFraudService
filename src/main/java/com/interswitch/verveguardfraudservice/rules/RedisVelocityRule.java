package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.utils.Utils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisVelocityRule implements FraudRule{

    private final StringRedisTemplate redisTemplate;

    public RedisVelocityRule(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public RuleResult evaluate(TransactionRequest transactionRequest, ClientProfile clientProfile) {
        String cardHashNumber = Utils.hashCardNumber(transactionRequest.cardNumber());
        String redisKey = "velocity:" + cardHashNumber;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        }

        assert currentCount != null;
        if (currentCount.intValue() > 5) {
            return new RuleResult(
                    "Redis Velocity Rule",
                    100,
                    RuleCategory.VELOCITY
            );
        }


        return new RuleResult(
                "Redis Velocity Rule",
                0,
                RuleCategory.VELOCITY
        );
    }

    @Override
    public RuleCategory getRuleCategory() {
        return RuleCategory.VELOCITY;
    }
}
