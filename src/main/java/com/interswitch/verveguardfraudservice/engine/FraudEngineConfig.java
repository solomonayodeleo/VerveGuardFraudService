package com.interswitch.verveguardfraudservice.engine;

import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.rules.BlacklistMerchantRule;
import com.interswitch.verveguardfraudservice.rules.DatabaseVelocityRule;
import com.interswitch.verveguardfraudservice.rules.FraudRule;
import com.interswitch.verveguardfraudservice.rules.RedisVelocityRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FraudEngineConfig {

    @Bean("dbFraudRuleEngine")
    public FraudRuleEngine dbFraudRuleEngine(
            BlacklistMerchantRule blacklistMerchantRule,
            DatabaseVelocityRule databaseVelocityRule,
            TransactionLogRepository  transactionLogRepository){

        List<FraudRule> rules = List.of(
                blacklistMerchantRule,
                databaseVelocityRule
        );

        return new FraudRuleEngine(rules, transactionLogRepository);
    }

    @Bean("redisFraudRuleEngine")
    public FraudRuleEngine redisFraudRuleEngine(
            BlacklistMerchantRule blacklistMerchantRule,
            RedisVelocityRule redisVelocityRule,
            TransactionLogRepository  transactionLogRepository){

        List<FraudRule> rules = List.of(
                blacklistMerchantRule,
                redisVelocityRule
        );

        return new FraudRuleEngine(rules, transactionLogRepository);
    }
}
