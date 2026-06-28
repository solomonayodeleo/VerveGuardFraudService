package com.interswitch.verveguardfraudservice.rules;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.repository.BlacklistsMerchantRepository;
import org.springframework.stereotype.Component;

@Component
public class BlacklistMerchantRule implements FraudRule {

    public final BlacklistsMerchantRepository  repository;

    public BlacklistMerchantRule(BlacklistsMerchantRepository repository) {
        this.repository = repository;
    }

    @Override
    public RuleResult evaluate(TransactionRequest request, ClientProfile profile) {
        return repository.findByMerchantId(request.merchantId())
                .map(merchant -> new RuleResult(
                        "Merchant Blacklist Check",
                        100,
                        RuleCategory.BLACKLIST
                ))
                .orElse(new RuleResult(
                        "Merchant Blacklist Check",
                        0,
                        RuleCategory.BLACKLIST
                ));
    }
}
