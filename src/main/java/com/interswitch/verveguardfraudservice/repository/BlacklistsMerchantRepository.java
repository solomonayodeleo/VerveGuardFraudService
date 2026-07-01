package com.interswitch.verveguardfraudservice.repository;

import com.interswitch.verveguardfraudservice.entity.BlacklistsMerchant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistsMerchantRepository extends JpaRepository<BlacklistsMerchant, Long> {

    @Cacheable("blacklistedMerchants")
    Optional<BlacklistsMerchant> findByMerchantId(String merchantId);

}
