package com.interswitch.verveguardfraudservice.controller;


import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.dto.TransactionResponse;
import com.interswitch.verveguardfraudservice.engine.FraudRuleEngine;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.model.ClientTier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    private final FraudRuleEngine  fraudRuleEngine;

    public TransactionController(FraudRuleEngine fraudRuleEngine) {
        this.fraudRuleEngine = fraudRuleEngine;
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

        ClientProfile clientProfile = new ClientProfile(transactionRequest.clientId(), tier);

        // 2. Process the transaction through our Rules Engine
        TransactionResponse transactionResponse = fraudRuleEngine.processTransaction(transactionRequest, clientProfile);

        return ResponseEntity.ok(transactionResponse);
    }
}
