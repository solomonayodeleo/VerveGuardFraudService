package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import com.interswitch.verveguardfraudservice.model.RuleResult;

public interface FraudRule {
    RuleResult evaluate(TransactionRequest transactionRequest, ClientProfile clientProfile);
    RuleCategory getRuleCategory();
}