package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import java.util.concurrent.TimeUnit;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DuplicateTransactionRuleTest {

    @Test
    void testTransactionIsDuplicated() {
        // ARRANGE: Set up the mock data
        // 1. We create "fake" versions of the Redis classes using Mockito.mock()
        StringRedisTemplate mockRedis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> mockOps = mock(ValueOperations.class);

        // 2. We explicitly teach the fake database how to respond when asked questions!
        when(mockRedis.opsForValue()).thenReturn(mockOps); 
        when(mockRedis.hasKey(anyString())).thenReturn(true);

        // 3. We pass the fake database into our rule!
        DuplicateTransactionRule rule = new DuplicateTransactionRule(mockRedis);
        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of());
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("15000.00"), "M111", "127.0.0.1", "1"
        );
        // ACT: Run the rule. Because we taught the mock to return "true" for hasKey, it will think it's a duplicate!
        RuleResult result = rule.evaluate(request, profile);
        // ASSERT
        assertEquals(100, result.score());
    }
    

    @Test
    void testTransactionIsNotDuplicated() {
        StringRedisTemplate mockRedis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> mockOps = mock(ValueOperations.class);

        when(mockRedis.opsForValue()).thenReturn(mockOps);
        when(mockRedis.hasKey(anyString())).thenReturn(false);

        DuplicateTransactionRule rule = new DuplicateTransactionRule(mockRedis);
        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of());
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("6000.00"), "M111", "127.0.0.1", "1"
        );
        RuleResult result = rule.evaluate(request, profile);
        assertEquals(0, result.score());

        verify(mockOps).set(anyString(), eq("1"), eq(30L), eq(TimeUnit.SECONDS));
    }
}
