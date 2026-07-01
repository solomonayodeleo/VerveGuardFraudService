package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseVelocityRuleTest {

    @Test
    void testVelocityExceedsLimit() {
        TransactionLogRepository repository = mock(TransactionLogRepository.class);

        // 2. We explicitly teach the fake database how to respond when asked questions.
        when(repository.countByCardNumberHashAndCreatedAtAfter(anyString(), any())).thenReturn(6L);

        DatabaseVelocityRule rule = new DatabaseVelocityRule(repository);
        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("15000.00"), "M111", "127.0.0.1", "1"
        );

        RuleResult result = rule.evaluate(request, profile);

        assertEquals(100, result.score());


    }

    @Test
    void testVelocityIsWithinLimit() {
        TransactionLogRepository repository = mock(TransactionLogRepository.class);

        when(repository.countByCardNumberHashAndCreatedAtAfter(anyString(), any())).thenReturn(4L);

        DatabaseVelocityRule rule = new DatabaseVelocityRule(repository);
        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("15000.00"), "M111", "127.0.0.1", "1"
        );
        RuleResult result = rule.evaluate(request, profile);
        assertEquals(0, result.score());
    }
}
