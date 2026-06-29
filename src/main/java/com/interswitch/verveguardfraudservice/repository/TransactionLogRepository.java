package com.interswitch.verveguardfraudservice.repository;

import com.interswitch.verveguardfraudservice.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;


public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    long countByCardNumberHashAndCreatedAtAfter(String cardNumberHash, Instant timestamp);
}