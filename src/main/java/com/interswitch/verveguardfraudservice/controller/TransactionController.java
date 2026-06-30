package com.interswitch.verveguardfraudservice.controller;


import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.dto.TransactionResponse;
import com.interswitch.verveguardfraudservice.engine.FraudRuleEngine;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import com.interswitch.verveguardfraudservice.model.RuleCategory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final FraudRuleEngine  dbFraudRuleEngine;
    private final FraudRuleEngine  redisFraudRuleEngine;

    public TransactionController(@Qualifier("dbFraudRuleEngine") FraudRuleEngine dbFraudRuleEngine, @Qualifier("redisFraudRuleEngine") FraudRuleEngine redisFraudRuleEngine) {
        this.dbFraudRuleEngine = dbFraudRuleEngine;
        this.redisFraudRuleEngine = redisFraudRuleEngine;
    }

    @PostMapping("/verify")
    public ResponseEntity<TransactionResponse> verifyTransaction(@RequestBody TransactionRequest transactionRequest) {

        // 1. Resolve client tier dynamically based on the clientId prefix for testing
        ClientTier tier = ClientTier.BRONZE;
        if (transactionRequest.clientId() != null) {
            String lowerId = transactionRequest.clientId().toLowerCase();
            if (lowerId.startsWith("bronze")) {
                tier = ClientTier.BRONZE;
            } else if (lowerId.startsWith("gold")) {
                tier = ClientTier.GOLD;
            }  else if (lowerId.startsWith("silver")) {
                tier = ClientTier.SILVER;
            }
        }

        Set<RuleCategory> categories = Set.of(RuleCategory.BLACKLIST, RuleCategory.VELOCITY);
        ClientProfile clientProfile = new ClientProfile(transactionRequest.clientId(), tier, categories);

        // 2. Process the transaction through our Rules Engine
        TransactionResponse transactionResponse = dbFraudRuleEngine.processTransaction(transactionRequest, clientProfile);

        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping("/verify/redis")
    public ResponseEntity<TransactionResponse> verifyRedisTransaction(@RequestBody TransactionRequest transactionRequest) {
        ClientTier tier = ClientTier.BRONZE;
        if (transactionRequest.clientId() != null) {
            String lowerId = transactionRequest.clientId().toLowerCase();
            if (lowerId.startsWith("bronze")) {
                tier = ClientTier.BRONZE;
            } else if (lowerId.startsWith("silver")) {
                tier = ClientTier.SILVER;
            } else if (lowerId.startsWith("gold")) {
                tier = ClientTier.GOLD;
            }
        }

        Set<RuleCategory> categories = Set.of(RuleCategory.BLACKLIST, RuleCategory.VELOCITY);
        ClientProfile clientProfile = new ClientProfile(transactionRequest.clientId(), tier, categories);

        TransactionResponse transactionResponse = redisFraudRuleEngine.processTransaction(transactionRequest, clientProfile);

        return ResponseEntity.ok(transactionResponse);
    }
}
