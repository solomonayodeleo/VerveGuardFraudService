package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;

public interface FraudRule {
    RuleResult evaluate(TransactionRequest transactionRequest, ClientProfile clientProfile);
}
