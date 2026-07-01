package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTransactionLimitRuleTest {
    private final SingleTransactionLimitRule rule = new SingleTransactionLimitRule();

    @Test
    void testGoldTierExceedsLimit() {
        // ARRANGE: Set up the mock data
        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));

        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("15000.00"), "M111", "127.0.0.1", "1"
        );

        // ACT: Run the actual rule logic
        RuleResult result = rule.evaluate(request, profile);

        // ASSERT: Check that the score is 100 because 15,000 > 10,000
        assertEquals(100, result.score());
    }

    @Test
    void testGoldTierNotExceedsLimit() {
        // ARRANGE: Set up the mock data
        ClientProfile profile = new ClientProfile("1", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));

        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("8000"), "M111", "127.0.0.1", "2"
        );

        // ACT: Run the actual rule logic
        RuleResult result = rule.evaluate(request, profile);

        // ASSERT: Check that the score is 0 because 8000 > 10,000
        assertEquals(0, result.score());
    }
}
