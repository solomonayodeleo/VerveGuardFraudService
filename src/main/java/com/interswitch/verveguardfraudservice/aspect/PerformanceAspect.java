package com.interswitch.verveguardfraudservice.aspect;

import com.interswitch.verveguardfraudservice.dto.TransactionRequest;
import com.interswitch.verveguardfraudservice.dto.TransactionResponse;
import com.interswitch.verveguardfraudservice.entity.TransactionLog;
import com.interswitch.verveguardfraudservice.model.ClientProfile;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import com.interswitch.verveguardfraudservice.utils.Utils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class PerformanceAspect {

    private final TransactionLogRepository  transactionLogRepository;

    public PerformanceAspect(TransactionLogRepository transactionLogRepository) {
        this.transactionLogRepository = transactionLogRepository;
    }

    // This intercepts ANY method that has the custom annotation
    @Around("@annotation(PerformanceAudit)")
    public Object auditAndLogTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        long  start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis()  - start;

        // Extract the Inputs (Request and Profile) from the Engine's method arguments
        Object[] args = joinPoint.getArgs();
        TransactionRequest transactionRequest = (TransactionRequest) args[0];
        ClientProfile clientProfile = (ClientProfile) args[1];

        // Extract the Output (Response)
        TransactionResponse transactionResponse = (TransactionResponse) result;

        // Build and Save the Log to the Database!
        TransactionLog transactionLog = TransactionLog.builder()
                .traceId(transactionResponse.traceId())
                .clientId(transactionRequest.clientId())
                .clientTier(String.valueOf(clientProfile.tier()))
                .decision(String.valueOf(transactionResponse.decision()))
                .aggregateScore(transactionResponse.score())
                .ipAddress(transactionRequest.ipAddress())
                .cardNumberHash(Utils.hashCardNumber(transactionRequest.cardNumber()))
                .executionTimeMs(executionTime)
                .createdAt(Instant.now())
                .build();

        transactionLogRepository.save(transactionLog);

        // Return the original response back to the Controller
        return result;
    }
}
