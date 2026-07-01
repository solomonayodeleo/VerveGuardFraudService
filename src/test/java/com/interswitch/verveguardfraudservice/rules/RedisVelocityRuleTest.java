package com.interswitch.verveguardfraudservice.rules;


import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedisVelocityRuleTest {

    @Test
    void testVelocityExceedsLimit() {
        // ARRANGE: Set up the mock data
        // 1. We create "fake" versions of the Redis classes using Mockito.mock()
        StringRedisTemplate mockRedis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> mockOps = mock(ValueOperations.class);

        // 2. We explicitly teach the fake database how to respond when asked questions.
        when(mockRedis.opsForValue()).thenReturn(mockOps);
        when(mockRedis.opsForValue().increment(anyString())).thenReturn(6L);

        RedisVelocityRule rule = new RedisVelocityRule(mockRedis);

        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("2000"), "M111", "172.123.0.0", "11");

        // ACT: Run the actual rule logic
        RuleResult result = rule.evaluate(request, profile);

        // ASSERT
        assertEquals(100, result.score());
    }

    @Test
    void testVelocityIsWithinLimit() {
        StringRedisTemplate mockRedis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> mockOps = mock(ValueOperations.class);

        when(mockRedis.opsForValue()).thenReturn(mockOps);
        when(mockRedis.opsForValue().increment(anyString())).thenReturn(2L);

        RedisVelocityRule rule = new RedisVelocityRule(mockRedis);

        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("2000"), "M111", "172.123.0.0", "11");

        // ACT: Run the actual rule logic
        RuleResult result = rule.evaluate(request, profile);

        // ASSERT
        assertEquals(0, result.score());
    }
}
