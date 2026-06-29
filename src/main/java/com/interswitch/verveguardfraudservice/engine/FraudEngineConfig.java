package com.interswitch.verveguardfraudservice.engine;

import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.rules.BlacklistMerchantRule;
import com.interswitch.verveguardfraudservice.rules.DatabaseVelocityRule;
import com.interswitch.verveguardfraudservice.rules.FraudRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FraudEngineConfig {

    @Bean("fraudRuleEngine")
    public FraudRuleEngine fraudRuleEngine(
            BlacklistMerchantRule blacklistMerchantRule,
            DatabaseVelocityRule databaseVelocityRule,
            TransactionLogRepository  transactionLogRepository){

        List<FraudRule> rules = List.of(
                blacklistMerchantRule,
                databaseVelocityRule
        );

        return new FraudRuleEngine(rules, transactionLogRepository);
    }
}
