package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SingleTransactionLimitRule implements FraudRule {

    @Override
    public RuleResult evaluate(TransactionRequest transactionRequest, ClientProfile clientProfile) {
        if (clientProfile.tier().equals(ClientTier.GOLD) && transactionRequest.amount().compareTo(new BigDecimal("10000")) > 0) {
            return new RuleResult(
                    "Single Transaction Limit Rule",
                    100,
                    RuleCategory.AMOUNT
            );
        } else if (clientProfile.tier().equals(ClientTier.SILVER) && transactionRequest.amount().compareTo(new BigDecimal("5000")) > 0) {
            return new RuleResult(
                    "Single Transaction Limit Rule",
                    100,
                    RuleCategory.AMOUNT
            );
        } else if (clientProfile.tier().equals(ClientTier.BRONZE) && transactionRequest.amount().compareTo(new BigDecimal("1000")) > 0) {
            return new RuleResult(
                    "Single Transaction Limit Rule",
                    100,
                    RuleCategory.AMOUNT
            );
        }
        return new RuleResult(
                "Single Transaction Limit Rule",
                0,
                RuleCategory.AMOUNT
        );
    }

    @Override
    public RuleCategory getRuleCategory() {
        return RuleCategory.AMOUNT;
    }
}
