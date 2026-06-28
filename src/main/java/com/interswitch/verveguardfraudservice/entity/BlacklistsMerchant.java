package com.interswitch.verveguardfraudservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Audited;

import java.time.Instant;

@Entity
@Table(name = "blacklist_merchants")
public class BlacklistsMerchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false, unique = true)
    private String merchantId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at",  nullable = false, updatable = false)
    private Instant createdAt;
}
