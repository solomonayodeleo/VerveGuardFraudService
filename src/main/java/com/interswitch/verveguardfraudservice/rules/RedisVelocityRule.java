package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.utils.Utils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisVelocityRule implements FraudRule{

    private final StringRedisTemplate redisTemplate;

    public RedisVelocityRule(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // This runs automatically if Redis crashes or times out!
    public RuleResult failOpen(TransactionRequest request, ClientProfile profile, Throwable throwable) {
        System.out.println("⚠️ REDIS IS DOWN! Failing Open gracefully. Error: " + throwable.getMessage());

        // Return a score of 0 so the transaction is NOT blocked by a broken cache!
        return new RuleResult("Redis Velocity Rule", 0, RuleCategory.VELOCITY);
    }


    @CircuitBreaker(name = "redis", fallbackMethod = "failOpen")
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
