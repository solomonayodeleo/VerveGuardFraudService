package com.interswitch.verveguardfraudservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_logs")
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String traceId;
    private String clientId;
    private String clientTier;
    private int aggregateScore;
    private String decision;
    private Long executionTimeMs;
    private Instant createdAt;
    private String cardNumberHash;
    private String ipAddress;
}
