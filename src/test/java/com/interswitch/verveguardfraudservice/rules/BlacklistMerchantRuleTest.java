package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.entity.BlacklistsMerchant;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import com.interswitch.verveguardfraudservice.repository.BlacklistsMerchantRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlacklistMerchantRuleTest {

    @Test
    void testBlacklistedMerchantFails() {
        BlacklistsMerchantRepository mockRepo = mock(BlacklistsMerchantRepository.class);

        when(mockRepo.findByMerchantId(anyString())).thenReturn(Optional.of(new BlacklistsMerchant()));

        BlacklistMerchantRule rule = new BlacklistMerchantRule(mockRepo);
        ClientProfile profile = new ClientProfile("gold_client_02", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("10000.00"), "M111", "127.0.0.1", "1"
        );

        RuleResult result = rule.evaluate(request, profile);

        assertEquals(100, result.score());

    }

    @Test
    void testBlacklistedMerchantSuccess() {
        BlacklistsMerchantRepository mockRepo = mock(BlacklistsMerchantRepository.class);

        when(mockRepo.findByMerchantId(anyString())).thenReturn(Optional.empty());

        BlacklistMerchantRule rule = new BlacklistMerchantRule(mockRepo);
        ClientProfile profile = new ClientProfile("gold_client_02", ClientTier.GOLD, Set.of(RuleCategory.AMOUNT, RuleCategory.VELOCITY, RuleCategory.BLACKLIST));
        TransactionRequest request = new TransactionRequest(
                "1234567890123456", new BigDecimal("10000.00"), "M111", "127.0.0.1", "1"
        );

        RuleResult result = rule.evaluate(request, profile);

        assertEquals(0, result.score());

    }
}
